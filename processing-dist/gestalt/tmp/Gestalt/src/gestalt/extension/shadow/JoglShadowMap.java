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

import static gestalt.Gestalt.*;
import gestalt.extension.framebufferobject.BufferInfo;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_ShadowMap;
import gestalt.extension.framebufferobject.JoglTexCreatorShadowMap;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.cameraplugins.Light;
import gestalt.shape.AbstractDrawable;
import gestalt.material.Color;
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;


public class JoglShadowMap
        extends AbstractDrawable {

    public Color shadowcolor;

    public boolean enabled;

    private boolean _myIsShadowEnabled;

    private boolean _myIsInitialized;

    private Light _myLight;

    private Bin _myBin;

    /* shadow texture object */
    private int _myShadowMapTexture;

    private final int _myTextureUnitID;

    private final int _myTextureTargetID;

    private final int _myTextureTargetQueryID;

    public final Camera lightcamera;

    private final int _myShadowTextureWidth;

    private final int _myShadowTextureHeight;

    private final JoglTexCreator _myTexCreator;

    private final JoglFrameBufferObject _myFBO;

    /* texture generation planes */
    private float[] PS = {1, 0, 0, 0};

    private float[] PT = {0, 1, 0, 0};

    private float[] PR = {0, 0, 1, 0};

    private float[] PQ = {0, 0, 0, 1};

    public JoglShadowMap(final Light theLight,
                         final int theTextureWidth,
                         final int theTextureHeight,
                         final boolean theUseNonPowerOfTwoTexture,
                         final boolean theUseFBO) {
        this(theLight,
             theTextureWidth,
             theTextureHeight,
             GL.GL_TEXTURE1,
             theUseNonPowerOfTwoTexture,
             theUseFBO);
    }

    public JoglShadowMap(final Light theLight,
                         final int theTextureWidth,
                         final int theTextureHeight,
                         final int theTextureUnitID,
                         final boolean theUseNonPowerOfTwoTexture,
                         final boolean theUseFBO) {
        _myLight = theLight;
        boolean _myUseFBO = theUseFBO;
        enabled = true;
        _myIsInitialized = false;
        _myIsShadowEnabled = true;

        _myTextureUnitID = theTextureUnitID;
        if (theUseNonPowerOfTwoTexture) { // this is really interessting. on OS X it seems to be fine to use any texture sizes???
            _myTextureTargetID = GL.GL_TEXTURE_RECTANGLE_ARB;
            _myTextureTargetQueryID = GL.GL_TEXTURE_BINDING_RECTANGLE_ARB;
        } else {
            _myTextureTargetID = GL.GL_TEXTURE_2D;
            _myTextureTargetQueryID = GL.GL_TEXTURE_BINDING_2D;
        }
        _myShadowTextureWidth = theTextureWidth;
        _myShadowTextureHeight = theTextureHeight;
        _myBin = new RenderBin(200);

        shadowcolor = new Color(0, 0, 0, 1);

        lightcamera = new Camera();
        lightcamera.setMode(CAMERA_MODE_LOOK_AT);
        lightcamera.setPositionRef(light().position());
        lightcamera.culling = CAMERA_CULLING_BACKFACE;
        lightcamera.viewport().width = _myShadowTextureWidth;
        lightcamera.viewport().height = _myShadowTextureHeight;

        if (_myUseFBO) {
            _myTexCreator = new JoglTexCreatorFBO_ShadowMap();
            _myFBO = createDefault(_myShadowTextureWidth, _myShadowTextureHeight, lightcamera, _myTexCreator);
        } else {
            _myTexCreator = new JoglTexCreatorShadowMap(theUseNonPowerOfTwoTexture);
            _myFBO = null;
        }
    }

    private static JoglFrameBufferObject createDefault(int theFBOWidth,
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

    public int getTextureTargetID() {
        return _myTextureTargetID;
    }

    public int getTextureID() {
        return _myShadowMapTexture;
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

    public void draw(GLContext theRenderContext) {
        if (enabled) {
            if (!_myIsInitialized) {
                _myIsInitialized = true;
                init(theRenderContext);
            }
            display(theRenderContext);
        } else {
            disableShadow(theRenderContext.gl);
        }
    }

    private void init(GLContext theRenderContext) {
        GLContext myJoglContext = theRenderContext;
        GL gl = myJoglContext.gl;

        /* check extensions */
        if (_myTextureTargetID == GL.GL_TEXTURE_RECTANGLE_ARB) {
            JoglUtil.testExtensionAvailability(gl, "GL_ARB_texture_rectangle");
        }
        if (_myFBO != null) {
            JoglUtil.testExtensionAvailability(gl, "GL_EXT_framebuffer_object");
        }

        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        if (_myFBO != null) {
            _myFBO.init(gl);
            _myShadowMapTexture = _myFBO.getTextureID();
        } else {
            BufferInfo _myBufferID = new BufferInfo();
            _myTexCreator.create(gl, _myShadowTextureWidth, _myShadowTextureHeight, _myBufferID);
            _myShadowMapTexture = _myBufferID.texture;
        }

        /* --- */
        gl.glEnable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, _myShadowMapTexture);

        /* restore texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);
    }

    private void lightCamera(GLContext theRenderContext) {
        GLContext myContext = new GLContext();
        myContext.gl = theRenderContext.gl;
        lightcamera.draw(myContext);
    }

    private void textureCamera(GLContext theRenderContext) {

        GL gl = theRenderContext.gl;

        /* use a clean glcontext */
        GLContext myContext = new GLContext();
        myContext.gl = theRenderContext.gl;

        /* store camera viewport */
        int myWidth = theRenderContext.camera.viewport().width;
        int myHeight = theRenderContext.camera.viewport().height;

        theRenderContext.camera.viewport().width = _myShadowTextureWidth;
        theRenderContext.camera.viewport().height = _myShadowTextureHeight;

        theRenderContext.camera.draw(myContext);

        /* reset viewport */
        theRenderContext.camera.viewport().width = myWidth;
        theRenderContext.camera.viewport().height = myHeight;

        /* set the TexGen planes */
        /** @todo JSR-231 -- added 0 */
        gl.glTexGenfv(GL.GL_S, GL.GL_EYE_PLANE, PS, 0);
        gl.glTexGenfv(GL.GL_T, GL.GL_EYE_PLANE, PT, 0);
        gl.glTexGenfv(GL.GL_R, GL.GL_EYE_PLANE, PR, 0);
        gl.glTexGenfv(GL.GL_Q, GL.GL_EYE_PLANE, PQ, 0);

        /* prepare the texture matrix for the projected textures */
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();

        /* because depth value range form -1 to 1, we need to rescale */
        if (_myTextureTargetID == GL.GL_TEXTURE_RECTANGLE_ARB) {
            gl.glScalef(_myShadowTextureWidth, _myShadowTextureHeight, 1);
        }
        gl.glTranslatef(0.5f, 0.5f, 0.5f);
        gl.glScalef(0.5f, 0.5f, 0.5f);

        /* setup texture transform matrix */
        Camera myCamera = lightcamera;

        /* projection matrix */
        JoglUtil.gluPerspective(gl,
                                myCamera.fovy,
                                (float)_myShadowTextureWidth / (float)_myShadowTextureHeight,
                                myCamera.nearclipping,
                                myCamera.farclipping,
                                myCamera.frustumoffset);

        /* model-view matrix */
        /** @todo JSR-231 -- added 0 */
        gl.glMultMatrixf(myCamera.getRotationMatrix().toArray4f(), 0);
        gl.glTranslatef(-myCamera.position().x, -myCamera.position().y, -myCamera.position().z);

        /* restore modelview matrix mode */
        gl.glMatrixMode(GL.GL_MODELVIEW);
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
        gl.glBindTexture(_myTextureTargetID, _myShadowMapTexture);

        /* bind FBO */
        int myPreviousFBOBufferID = 0;
        if (_myFBO != null) {
            myPreviousFBOBufferID = _myFBO.bindBuffer(gl);
            gl.glBindTexture(_myFBO.getTextureTarget(), _myFBO.getTextureID());
        }

        /* setup shadow viewport and masking */
        lightCamera(theRenderContext);

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

        if (_myFBO == null) {
            /* copy the Z buffer to the shadow map */
            gl.glEnable(_myTextureTargetID);
            gl.glBindTexture(_myTextureTargetID, _myShadowMapTexture);
            gl.glCopyTexSubImage2D(_myTextureTargetID, 0, 0, 0, 0, 0, _myShadowTextureWidth, _myShadowTextureHeight);
        }

        /* enable color buffer writing again */
        gl.glColorMask(true, true, true, true);

        /* switch polygon offset off */
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

        if (_myFBO != null) {
            JoglFrameBufferObject.unbindBuffer(gl, myPreviousFBOBufferID);
        }

        /* setup shadow texture matrix */

        /* camera */
        if (_myFBO != null) {
            gl.glEnable(_myTextureTargetID);
            gl.glBindTexture(_myTextureTargetID, _myShadowMapTexture);
        }

        textureCamera(theRenderContext);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);

        /* clean up */
        if (_myFBO == null) {
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        }
    }

    public boolean isShadowEnabled() {
        return _myIsShadowEnabled;
    }

    public boolean querryShadowExtensionState(GL gl) {
        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        /* texture enabled */
        byte[] myEnabled = new byte[1];
        gl.glGetBooleanv(_myTextureTargetID, myEnabled, 0);

        /* texture bound */
        int[] myBoundTexture = new int[1];
        gl.glGetIntegerv(_myTextureTargetQueryID, myBoundTexture, 0);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);

        /* result */
        if (myBoundTexture[0] == _myShadowMapTexture &&
                myEnabled[0] == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void disableShadow(GL gl) {
        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        /* disable shadowing */
        gl.glDisable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, 0);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);

        _myIsShadowEnabled = false;
    }

    public void enableShadow(GL gl) {
        /* store active texture unit */
        int[] myActiveTextureUnit = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, myActiveTextureUnit, 0);
        gl.glActiveTexture(_myTextureUnitID);

        /* enable shadowing */
        gl.glEnable(_myTextureTargetID);
        gl.glBindTexture(_myTextureTargetID, _myShadowMapTexture);

        /* restore former texture unit */
        gl.glActiveTexture(myActiveTextureUnit[0]);

        _myIsShadowEnabled = true;
    }

    protected void drawShadowCastingShapes(GLContext theRenderContext) {
        /*
         * INFO override this method if you want to do special stuff like disabling the material for example boolean
         * myMaterialState = myShape.material.enabled;
         * myShape.material.enabled = false;
         * myShape.draw(theRenderContext);
         * myShape.material.enabled = myMaterialState;
         */
        Drawable[] myDrawables = _myBin.getDataRef();
        for (int i = 0; i < _myBin.size(); i++) {
            Drawable myShape = myDrawables[i];
            if (myShape != null) {
                if (myShape.isActive()) {
                    myShape.draw(theRenderContext);
                // AbstractShape myAbstractShape = (AbstractShape) myShape;
                // boolean myMaterialState =
                // myAbstractShape.material.enabled;
                // myAbstractShape.material.enabled = false;
                // myAbstractShape.draw(theRenderContext);
                // myAbstractShape.material.enabled = myMaterialState;
                }
            } else {
                System.err.println("### ERROR @ Shadowmap.drawDrawables / drawable #" + i + " is not initialized!");
            }
        }
    }
}
