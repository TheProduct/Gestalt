/*
 * Gestalt
 *
 * Copyright (C) 2012 Patrick Kochlik + Dennis Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt.extension.shadow;

import gestalt.Gestalt;
import gestalt.extension.framebufferobject.BufferInfo;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.cameraplugins.Light;
import gestalt.shape.AbstractDrawable;
import gestalt.shape.AbstractShape;
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;


public class JoglGLSLShadowMap
        extends AbstractDrawable {

    private boolean _myIsInitialized;

    private Light _myLight;

    private Bin _myBin;

    /* shadow texture object */
    private int _myShadowOpenGLTextureID;

    private final int _myTextureUnitID;

    private final int _myTextureTargetID;

    public final Camera lightcamera;

    private final int _myShadowTextureWidth;

    private final int _myShadowTextureHeight;

    private final JoglFrameBufferObject _myFBO;

    /* texture generation planes */
    public JoglGLSLShadowMap(final Light theLight,
                             final int theTextureWidth,
                             final int theTextureHeight) {
        this(theLight,
             theTextureWidth,
             theTextureHeight,
             GL.GL_TEXTURE1);
    }

    public JoglGLSLShadowMap(final Light theLight,
                             final int theTextureWidth,
                             final int theTextureHeight,
                             final int theTextureUnitID) {
        _myLight = theLight;
        _myIsInitialized = false;

        _myTextureUnitID = theTextureUnitID;
        _myTextureTargetID = GL.GL_TEXTURE_2D;

        _myShadowTextureWidth = theTextureWidth;
        _myShadowTextureHeight = theTextureHeight;
        _myBin = new RenderBin(200);

        lightcamera = new Camera();
        lightcamera.setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        lightcamera.setPositionRef(light().position());
        lightcamera.culling = Gestalt.CAMERA_CULLING_BACKFACE;
        lightcamera.viewport().width = _myShadowTextureWidth;
        lightcamera.viewport().height = _myShadowTextureHeight;

        _myFBO = createDefault(_myShadowTextureWidth,
                               _myShadowTextureHeight,
                               lightcamera,
                               new JoglTexCreatorFBO_GLSL_ShadowMap());
    }

    private JoglFrameBufferObject createDefault(int theFBOWidth,
                                                int theFBOHeight,
                                                Camera theCamera,
                                                JoglTexCreator theJoglTexCreator) {
        final BufferInfo myBufferID = new BufferInfo();

        /* set camera for framebuffer object */
        theCamera.position().z = theFBOHeight;
        theCamera.viewport().width = theFBOWidth;
        theCamera.viewport().height = theFBOHeight;

        /* create framebuffer object */
        final JoglFrameBufferObject myFBO = new JoglFrameBufferObject(theFBOWidth,
                                                                      theFBOHeight,
                                                                      theCamera,
                                                                      theJoglTexCreator,
                                                                      myBufferID);

        /* add camera to framebuffer object renderbin */
        if (theCamera != null) {
            myFBO.add(theCamera);
        }

        return myFBO;
    }

    public void draw(GLContext theRenderContext) {
        if (!_myIsInitialized) {
            _myIsInitialized = true;
            init(theRenderContext);
        }
        display(theRenderContext);
    }

    private void init(GLContext theRenderContext) {
        GLContext myJoglContext = theRenderContext;
        GL gl = myJoglContext.gl;

        /* check extensions */
        JoglUtil.testExtensionAvailability(gl, "GL_EXT_framebuffer_object");

        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        _myFBO.init(gl);
        _myShadowOpenGLTextureID = _myFBO.getTextureID();

        /* --- */
        gl.glEnable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, _myShadowOpenGLTextureID);

        /* restore texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);
    }

    private void display(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);

        /* enable texture mapping */
        gl.glActiveTexture(_myTextureUnitID);

        /* set texture environment */
        gl.glEnable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, _myShadowOpenGLTextureID);

        /* bind FBO */
        final int myPreviousFBOBufferID = _myFBO.bindBuffer(gl);
        gl.glBindTexture(_myFBO.getTextureTarget(), _myFBO.getTextureID());

        /* setup shadow viewport and masking */
        GLContext myContext = new GLContext();
        myContext.gl = gl;
        lightcamera.draw(myContext);

        /* mask writing to color buffer */
        gl.glColorMask(false, false, false, false);

        /* needed to prevent ugly self-shadowing artefacts */
        gl.glPolygonOffset(10, 10);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);

        /* render the scene from the light's point of view */
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        /* draw shapes that cast shadows */
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        drawShadowCastingShapes(theRenderContext);
        gl.glPopAttrib();

        /* enable color buffer writing again */
        gl.glColorMask(true, true, true, true);

        /* switch polygon offset off */
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

        JoglFrameBufferObject.unbindBuffer(gl, myPreviousFBOBufferID);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);
    }

    public void disableShadow(GL gl) {
        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        /* disable shadowing */
        gl.glDisable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, 0);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);
    }

    public void enableShadow(GL gl) {
        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        /* enable shadowing */
        gl.glEnable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, _myShadowOpenGLTextureID);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);
    }

    protected void drawShadowCastingShapes(GLContext theRenderContext) {
        final Drawable[] myDrawables = _myBin.getDataRef();
        for (int j = 0; j < _myBin.size(); j++) {
            final Drawable myDrawable = myDrawables[j];
            if (myDrawable != null && myDrawable instanceof AbstractShape && myDrawable.isActive()) {
                final AbstractShape myShape = (AbstractShape)myDrawable;
                final boolean myMaterialState = myShape.material().ignoreplugins;
                myShape.material().ignoreplugins = true;
                myShape.draw(theRenderContext);
                myShape.material().ignoreplugins = myMaterialState;
            }
        }
    }

    public Light light() {
        return _myLight;
    }

    public int getTextureUnit() {
        return _myTextureUnitID;
    }

    public int getTextureWidth() {
        return _myShadowTextureWidth;
    }

    public int getTextureHeight() {
        return _myShadowTextureHeight;
    }

    public int getTextureID() {
        return _myShadowOpenGLTextureID;
    }

    public void addShape(Drawable theShape) {
        _myBin.add(theShape);
    }

    public Drawable removeDrawable(Drawable theShape) {
        return _myBin.remove(theShape);
    }

    public void setBin(Bin theBin) {
        _myBin = theBin;
    }

    public Bin getBin() {
        return _myBin;
    }

    public int getTextureTarget() {
        return _myTextureTargetID;
    }

    public JoglFrameBufferObject fbo() {
        return _myFBO;
    }
}
