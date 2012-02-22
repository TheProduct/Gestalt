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


package gestalt.candidates.ssao;

import gestalt.candidates.JoglMultiTexPlane;
import gestalt.extension.framebufferobject.BufferInfo;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.FrameSetup;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.util.JoglUtil;

import mathematik.Vector3f;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;


public class FrameBufferDepth
        extends TexturePlugin
        implements Drawable {

    private final BufferInfo _myBufferID;

    private final RenderBin _myBin;

    private final int _myTextureWidth;

    private final int _myTextureHeight;

    private boolean _myIsActive = true;

    private final JoglTexCreator _myTexCreator;

    private final GLContext _myContext;

    private static int _myMaxTextureSize;

    private static boolean ourStaticInitalized = false;

    public MaterialPlugin shader;

    public FrameBufferDepth(final int theWidth,
                            final int theHeight,
                            final JoglTexCreator theTexGenerator) {
        this(theWidth, theHeight, null, theTexGenerator);
    }

    public FrameBufferDepth(final int theWidth,
                            final int theHeight,
                            final Camera theCamera,
                            final JoglTexCreator theTexGenerator) {
        this(theWidth, theHeight, theCamera, theTexGenerator, null);
    }

    public FrameBufferDepth(final int theWidth,
                            final int theHeight,
                            final Camera theCamera,
                            final JoglTexCreator theTexGenerator,
                            final BufferInfo theBufferID) {
        super(false);

        _myTextureWidth = theWidth;
        _myTextureHeight = theHeight;

        scale().set(1, 1, 1);

        _myBin = new RenderBin(100);

        _myContext = new GLContext();
        _myContext.displaycapabilities = new DisplayCapabilities();
        _myContext.displaycapabilities.width = _myTextureWidth;
        _myContext.displaycapabilities.height = _myTextureHeight;
        _myContext.camera = theCamera;

        _myTexCreator = theTexGenerator;

        if (theBufferID == null) {
            _myBufferID = new BufferInfo();
            _myBufferID.attachment_point = GL.GL_COLOR_ATTACHMENT0_EXT;
        } else {
            _myBufferID = theBufferID;
        }

        setTextureTarget(_myTexCreator.texturetarget());
        setTextureUnit(GL.GL_TEXTURE0);
    }

    public void setAttachmentPoint(int theAttachmentPoint) {
        if (_myIsInitalized) {
            System.err.println(
                    "### WARNING / possible a bad idea to change the attachment point after initialization. have to check this ...");
        }
        _myBufferID.attachment_point = theAttachmentPoint;
    }

    public static JoglMultiTexPlane createView(FrameBufferDepth theFBO) {
        return JoglUtil.createTextureView(theFBO,
                                          new Vector3f(),
                                          new Vector3f(theFBO.getPixelWidth(), theFBO.getPixelHeight(), 1));
    }

    public static FrameBufferDepth createDefault(int theFBOWidth, int theFBOHeight, Camera theCamera,
                                                 boolean theColorBufferClearing, boolean theDepthBufferClearing) {
        /* set camera for framebuffer object */
        theCamera.position().z = theFBOHeight;
        theCamera.viewport().width = theFBOWidth;
        theCamera.viewport().height = theFBOHeight;

        /* create framebuffer object */
        final FrameBufferDepth myFBO = new FrameBufferDepth(theFBOWidth,
                                                            theFBOHeight,
                                                            theCamera,
                                                            new TexCreatorDepth());

        /* create a framesetup for the FBO to clear the screen */
        final FrameSetup myFrameSetup = new FrameSetup();
        myFrameSetup.colorbufferclearing = theColorBufferClearing;
        myFrameSetup.depthbufferclearing = theDepthBufferClearing;
        myFBO.add(myFrameSetup);

        /* add camera to framebuffer object renderbin */
        if (theCamera != null) {
            myFBO.add(theCamera);
        }

        return myFBO;
    }

    public static FrameBufferDepth createDefault(int theFBOWidth, int theFBOHeight, Camera theCamera) {
        return createDefault(theFBOWidth, theFBOHeight, theCamera, true, true);
    }

    public void initStatic(final GL gl) {
        /* query maximum texture size */
        int[] myValue = new int[1];
        gl.glGetIntegerv(GL.GL_MAX_RENDERBUFFER_SIZE_EXT, myValue, 0);
        _myMaxTextureSize = myValue[0];
        ourStaticInitalized = true;
    }

    public void setCameraRef(final Camera theCamera) {
        _myContext.camera = theCamera;
    }

    public Camera camera() {
        return _myContext.camera;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

//        /* choose texture unit */
//        gl.glActiveTexture(getTextureUnit());
//
//        /* enable texture target */
//        gl.glEnable(getTextureTarget());

        /* copy context properties */
        _myContext.event = theRenderContext.event;
        _myContext.gl = theRenderContext.gl;
        _myContext.glu = theRenderContext.glu;
        _myContext.drawable = (theRenderContext).drawable;

        /* initialize */
        if (!_myIsInitalized) {
            init(_myContext.gl);
            JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".init", true);
        }
        /* draw bin */
        display(_myContext);
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".display", true);

        checkFrameBufferStatus(gl);
//        /* turn off texture again */
//        gl.glDisable(getTextureTarget());
    }

    public Color backgroundcolor() {
        return _myContext.displaycapabilities.backgroundcolor;
    }

    public int getPixelWidth() {
        return _myTextureWidth;
    }

    public int getPixelHeight() {
        return _myTextureHeight;
    }

    public int getTextureID() {
        return _myBufferID.texture;
    }

    public void add(final Drawable theDrawable) {
        _myBin.add(theDrawable);
    }

    public RenderBin bin() {
        return _myBin;
    }

    public Drawable remove(final Drawable theDrawable) {
        return _myBin.remove(theDrawable);
    }

    private TexturePlugin[] _mySecondaryTexture;

    public TexturePlugin secondary_texture() {
//        if (_mySecondaryTexture == null) {
//            _mySecondaryTexture = new TexturePlugin(false);
//        }
        System.out.println("use JoglFrameBufferObject instead...");
        return _mySecondaryTexture[0];
    }

    public final void init(final GL gl) {
        /*
         * handle texture creation
         */
        _myTexCreator.create(gl, _myTextureWidth, _myTextureHeight, _myBufferID);
        if (_myBufferID.additional_textures != null) {
            for (int i = 0; i < _myBufferID.additional_textures.length; i++) {
                _mySecondaryTexture[i].setTextureID(_myBufferID.additional_textures[i]);
            }
        }

        checkFrameBufferStatus(gl);

        /* unbind objects */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
        gl.glBindTexture(getTextureTarget(), 0);

        _myIsInitalized = true;
    }

    private final void display(final GLContext theRenderContext) {
        if (_myBin.size() == 0) {
            return;
        }
        final GL gl = theRenderContext.gl;
        /* draw drawables to framebuffer object */
        bindBuffer(gl);

        shader.begin(theRenderContext, null);
        _myBin.draw(theRenderContext);
        shader.end(theRenderContext, null);

        unbindBuffer(gl);
    }

    public BufferInfo getBufferInfo() {
        return _myBufferID;
    }

    public final void bindBuffer(GL gl) {
        /* bind framebuffer object */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT,
                                _myBufferID.framebuffer_object);
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                                     _myBufferID.attachment_point,
                                     getTextureTarget(),
                                     _myBufferID.texture,
                                     0);
//        if (_myBufferID.additional_textures != UNDEFINED) {
//            gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
//                                         _myBufferID.additional_attachment_points,
//                                         getTextureTarget(),
//                                         _myBufferID.additional_textures,
//                                         0);
//        }
    }

    public static final void unbindBuffer(GL gl) {
        /* release framebuffer object */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
    }

    public static boolean checkFrameBufferStatus(GL gl) {
        return JoglUtil.checkFrameBufferStatus(gl);
    }

    public void update(final GL gl, final GLU glu) {
        /* initialize texture */
        if (!ourStaticInitalized) {
            initStatic(gl);
        }
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".initStatic", true);

        if (!_myIsInitalized) {
            _myIsInitalized = true;
            init(gl);
        }
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".init", true);

        /* enable and bind texture */
        bind(gl);
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".bind", true);

        /* update properties */
        if (_myWrapModeChanged) {
            updateWrapMode(gl);
            _myWrapModeChanged = false;
        }

        if (_myFilterTypeChanged) {
            updateFilterType(gl);
            _myFilterTypeChanged = false;
        }
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".update", true);
    }

    protected void updateWrapMode(final GL gl) {
        switch (getWrapMode()) {
            case TEXTURE_WRAPMODE_CLAMP:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP);
                break;
            case TEXTURE_WRAPMODE_REPEAT:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_REPEAT);
                break;
        }
    }

    protected void updateFilterType(final GL gl) {
        switch (getFilterType()) {
            case TEXTURE_FILTERTYPE_LINEAR:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                break;
            case TEXTURE_FILTERTYPE_MIPMAP:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
                break;
            case TEXTURE_FILTERTYPE_NEAREST:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
                break;
        }
    }

    public void load(Bitmap theBitmap) {
        System.err.println("### WARNING @" + getClass().getName() + " / unsupported method.");
    }

    public void reload() {
        System.err.println("### WARNING @" + getClass().getName() + " / unsupported method.");
    }

    public Bitmap bitmap() {
        if (_myBitmap == null) {
            _myBitmap = ByteBufferBitmap.getDefaultImageBitmap(_myTextureWidth, _myTextureHeight);
        }
        return _myBitmap;
    }

    public void setBitmapRef(final Bitmap theBitmap) {
        _myBitmap = theBitmap;
    }

    public void begin(GLContext theRenderContext, Material theParent) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        final boolean myWireframe;
        if (theParent == null) {
            myWireframe = false;
        } else {
            myWireframe = theParent.wireframe;
        }

        if (!myWireframe) {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());
        }

        /* enable and bind texture */
        update(gl, glu);

        /* handle wireframe OR texturematrix */
        if (myWireframe) {
            gl.glDisable(getTextureTarget());
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        } else {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPushMatrix();

            if (rotation().x != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().x), 1, 0, 0);
            }
            if (rotation().y != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().y), 0, 1, 0);
            }
            if (rotation().z != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().z), 0, 0, 1);
            }

            gl.glTranslatef(position().x,
                            position().y,
                            0);

            gl.glScalef(scale().x * nonpoweroftwotexturerescale().x,
                        scale().y * nonpoweroftwotexturerescale().y,
                        scale().z);

            if (_myHintFlipYAxis) {
                gl.glTranslatef(0, -1, 0);
            }

            gl.glMatrixMode(GL.GL_MODELVIEW);
        }
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".begin", true);
    }

    public void end(GLContext theRenderContext, Material theParent) {
        final GL gl = theRenderContext.gl;

        final boolean myWireframe;
        if (theParent == null) {
            myWireframe = false;
        } else {
            myWireframe = theParent.wireframe;
        }

        /* turn off texture again */
        if (!myWireframe) {
            gl.glDisable(getTextureTarget());
        }

        /* handle wireframe OR texturematrix */
        if (myWireframe) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        } else {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());

            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW);

            /* enable texture target */
            gl.glDisable(getTextureTarget());
        }

        final GLU glu = theRenderContext.glu;
        JoglUtil.printGLError(gl, glu, FrameBufferDepth.class.getSimpleName() + ".end", true);
    }

    public boolean isActive() {
        return _myIsActive;
    }

    public void setActive(boolean theIsActive) {
        _myIsActive = theIsActive;
    }

    public float getSortValue() {
        return 0.0f;
    }

    public void setSortValue(float theSortValue) {
    }

    public float[] getSortData() {
        return null;
    }

    public boolean isSortable() {
        return false;
    }

    public int getMaxTextureSize() {
        if (!ourStaticInitalized) {
            System.err.println("### WARNING @ " + getClass().getName()
                    + " / can t validate bitmap size. opengl has not been initalized.");
        }
        return _myMaxTextureSize;
    }
}
