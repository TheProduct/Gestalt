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


package gestalt.extension.framebufferobject;

import gestalt.candidates.JoglMultiTexPlane;
import gestalt.extension.materialplugin.JoglProxyTexturePlugin;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.FrameSetup;
import gestalt.material.Color;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.util.ImageUtil;
import gestalt.util.JoglUtil;

import mathematik.Vector3f;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;


/**
 * @todo we need to dispose the FBO properly ...
 * @todo consider implementing auto MIP generation a la
 *
 * glBindTexture( GL_TEXTURE_2D, texture);
 * glGenerateMipmapEXT( GL_TEXTURE_2D);
 * glBindTexture( GL_TEXTURE_2D, 0);
 *
 */
public class JoglFrameBufferObject
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

    private JoglAdditionalTexturePlugin[] _myAdditionalTexture;

    public JoglFrameBufferObject(final int theWidth,
                                 final int theHeight,
                                 final JoglTexCreator theTexGenerator) {
        this(theWidth, theHeight, null, theTexGenerator);
    }

    public JoglFrameBufferObject(final int theWidth,
                                 final int theHeight,
                                 final Camera theCamera,
                                 final JoglTexCreator theTexGenerator) {
        this(theWidth, theHeight, theCamera, theTexGenerator, null);
    }

    public JoglFrameBufferObject(final int theWidth,
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
            if (_myTexCreator instanceof JoglTexCreatorFBO_2xRGBA32Float) {
                _myBufferID = new BufferInfo();
                /* automaticall create second texture at color_attachmente_1 */
                _myBufferID.additional_attachment_points = new int[] {GL.GL_COLOR_ATTACHMENT1_EXT};
                _myBufferID.additional_textures = new int[] {UNDEFINED};
            } else {
                _myBufferID = new BufferInfo();
            }
        } else {
            _myBufferID = theBufferID;
        }

        if (_myBufferID.additional_textures != null) {
            _myAdditionalTexture = new JoglAdditionalTexturePlugin[_myBufferID.additional_textures.length];
            for (int i = 0; i < _myBufferID.additional_textures.length; i++) {
                _myAdditionalTexture[i] = new JoglAdditionalTexturePlugin(this);
//                _myAdditionalTexture[i] = new TexturePlugin(false);
            }
        }

        _myTexCreator.texturetarget();

        setTextureTarget(_myTexCreator.texturetarget());
        setTextureUnit(GL.GL_TEXTURE0);
    }

    public class JoglAdditionalTexturePlugin
            extends JoglProxyTexturePlugin {

        public JoglAdditionalTexturePlugin(final TexturePlugin theParentTexture) {
            super(theParentTexture);
        }

        public int getTextureID() {
            return _myOpenGLTextureID;
        }

        public void update(final GL gl, final GLU glu) {
            /* enable and bind texture */
            bind(gl);

            /* update properties */
            if (_myWrapModeChanged) {
                updateWrapMode(gl);
                _myWrapModeChanged = false;
            }

            if (_myFilterTypeChanged) {
                updateFilterType(gl);
                _myFilterTypeChanged = false;
            }

            if (_myBorderColorChanged) {
                updateBorderColor(gl);
                _myBorderColorChanged = false;
            }
        }
    }

    public JoglTexCreator creator() {
        return _myTexCreator;
    }

    public void setAttachmentPoint(int theAttachmentPoint) {
        if (_myIsInitalized) {
            System.err.println("### WARNING / possibly a bad idea to change the attachment point after initialization. have to check this ...");
        }
        _myBufferID.attachment_point = theAttachmentPoint;
    }

    public static JoglMultiTexPlane createView(JoglFrameBufferObject theFBO) {
        return JoglUtil.createTextureView(theFBO,
                                          new Vector3f(),
                                          new Vector3f(theFBO.getPixelWidth(), theFBO.getPixelHeight(), 1));
    }

    public static JoglFrameBufferObject createDefault(int theFBOWidth, int theFBOHeight,
                                                      final Camera theCamera,
                                                      boolean theColorBufferClearing,
                                                      boolean theDepthBufferClearing,
                                                      int theMultiSampling) {
        return createDefault(theFBOWidth, theFBOHeight,
                             theCamera,
                             theColorBufferClearing,
                             theDepthBufferClearing,
                             theMultiSampling > 0 ? new JoglTexCreatorFBO_DepthRGBAMultisample(theMultiSampling) : new JoglTexCreatorFBO_DepthRGBA());
    }

    public static JoglFrameBufferObject createDefault(int theFBOWidth, int theFBOHeight,
                                                      final Camera theCamera,
                                                      boolean theColorBufferClearing,
                                                      boolean theDepthBufferClearing) {
        return createDefault(theFBOWidth, theFBOHeight,
                             theCamera,
                             theColorBufferClearing,
                             theDepthBufferClearing,
                             new JoglTexCreatorFBO_DepthRGBA());
    }

    public static JoglFrameBufferObject createDefault(int theFBOWidth,
                                                      int theFBOHeight,
                                                      final Camera theCamera) {
        return createDefault(theFBOWidth, theFBOHeight, theCamera, true, true);
    }

    public static JoglFrameBufferObject createDefault(int theFBOWidth, int theFBOHeight) {
        return createDefault(theFBOWidth, theFBOHeight, new Camera(), true, true);
    }

    public static JoglFrameBufferObject createDefault(int theFBOWidth, int theFBOHeight, int theMultiSample) {
        return createDefault(theFBOWidth, theFBOHeight, new Camera(), true, true, theMultiSample);
    }

    public static JoglFrameBufferObject createDefault(int theFBOWidth, int theFBOHeight,
                                                      final Camera theCamera,
                                                      boolean theColorBufferClearing,
                                                      boolean theDepthBufferClearing,
                                                      final JoglTexCreator theJoglTexCreator) {
        /* set camera for framebuffer object */
        if (theCamera != null) {
            theCamera.position().z = theFBOHeight;
            theCamera.viewport().width = theFBOWidth;
            theCamera.viewport().height = theFBOHeight;
        }

        /* create framebuffer object */
        final JoglFrameBufferObject myFBO = new JoglFrameBufferObject(theFBOWidth,
                                                                      theFBOHeight,
                                                                      theCamera,
                                                                      theJoglTexCreator);

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

    public static JoglFrameBufferObject create(final int theFBOWidth, final int theFBOHeight) {
        if (ImageUtil.getNextPowerOf2(theFBOWidth) == theFBOWidth && ImageUtil.getNextPowerOf2(theFBOHeight) == theFBOHeight) {
//            System.out.println("### power of two FBO.");
            return createDefault(theFBOWidth, theFBOHeight, new Camera(), true, true);
        } else {
//            System.out.println("### non power of two FBO.");
            return createRectangular(theFBOWidth, theFBOHeight, new Camera(), true, true);
        }
    }

    public static JoglFrameBufferObject createRectangular(final int theFBOWidth, final int theFBOHeight) {
        return createRectangular(theFBOWidth, theFBOHeight, new Camera(), true, true);
    }

    public static JoglFrameBufferObject createRectangular(final int theFBOWidth, final int theFBOHeight,
                                                          final Camera theCamera) {
        return createRectangular(theFBOWidth, theFBOHeight, theCamera, true, true);
    }

    public static JoglFrameBufferObject createRectangular(final int theFBOWidth, final int theFBOHeight,
                                                          final int theMultiSample) {
        return createRectangular(theFBOWidth, theFBOHeight, new Camera(), true, true, theMultiSample);
    }

    public static JoglFrameBufferObject createRectangular(final int theFBOWidth, final int theFBOHeight,
                                                          final Camera theCamera,
                                                          final int theMultiSample) {
        return createRectangular(theFBOWidth, theFBOHeight, theCamera, true, true, theMultiSample);
    }

    public static JoglFrameBufferObject createRectangular(final int theFBOWidth, final int theFBOHeight,
                                                          final Camera theCamera,
                                                          final boolean theColorBufferClearing,
                                                          final boolean theDepthBufferClearing) {
        return createRectangular(theFBOWidth, theFBOHeight,
                                 theCamera,
                                 theColorBufferClearing,
                                 theDepthBufferClearing, 0);
    }

    public static JoglFrameBufferObject createRectangular(final int theFBOWidth, final int theFBOHeight,
                                                          final Camera theCamera,
                                                          final boolean theColorBufferClearing,
                                                          final boolean theDepthBufferClearing,
                                                          final int theMultiSample) {
        /* set camera for framebuffer object */
        theCamera.position().z = theFBOHeight;
        theCamera.viewport().width = theFBOWidth;
        theCamera.viewport().height = theFBOHeight;

        /* create framebuffer object */
        final JoglTexCreator myJoglTexCreator;
        if (theMultiSample == 0) {
            myJoglTexCreator = new JoglTexCreatorFBO_DepthRGBA(GL.GL_UNSIGNED_BYTE,
                                                               GL.GL_RGBA,
                                                               GL.GL_DEPTH_COMPONENT24,
                                                               GL.GL_TEXTURE_RECTANGLE_ARB);
        } else {
            myJoglTexCreator = new JoglTexCreatorFBO_DepthRGBAMultisample(GL.GL_UNSIGNED_BYTE,
                                                                          GL.GL_RGBA,
                                                                          GL.GL_DEPTH_COMPONENT24,
                                                                          GL.GL_TEXTURE_RECTANGLE_ARB,
                                                                          theMultiSample);
        }

        final JoglFrameBufferObject myFBO = new JoglFrameBufferObject(theFBOWidth,
                                                                      theFBOHeight,
                                                                      theCamera,
                                                                      myJoglTexCreator);

        /* set a few NPOT FBO properites */
        myFBO.setWrapMode(TEXTURE_WRAPMODE_CLAMP);
        myFBO.nonpoweroftwotexturerescale().set(1, 1);
        myFBO.scale().set(theFBOWidth, theFBOHeight);

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

    public static int getCurrentlyBoundFBOTexID(final GL gl) {
        /* save currently bound FBO */
        final int[] tmp = new int[1];
        gl.glGetIntegerv(GL.GL_FRAMEBUFFER_BINDING_EXT, tmp, 0);
        return tmp[0];
    }

    public static void readBackData(final GL gl, final GLU glu,
                                    final JoglFrameBufferObject theFBO,
                                    final FloatBuffer myBuffer) {
        final int w = theFBO.getPixelWidth();
        final int h = theFBO.getPixelHeight();
        gl.glFinish();

        /* save currently bound FBO */
        final int myFBOBufferID = getCurrentlyBoundFBOTexID(gl);

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theFBO.getBufferInfo().framebuffer_object);
        gl.glReadBuffer(theFBO.getBufferInfo().attachment_point);

        /** @todo the formats are fixed. might make them more dynamic... */
        gl.glReadPixels(0, 0,
                        w, h,
                        GL.GL_RGBA,
                        GL.GL_FLOAT,
                        myBuffer);
        gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, 0);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, myFBOBufferID);

        JoglUtil.printGLError(gl, glu, "readBackData()", true);
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

        /* copy context properties */
        _myContext.event = theRenderContext.event;
        _myContext.gl = theRenderContext.gl;
        _myContext.glu = theRenderContext.glu;
        _myContext.drawable = (theRenderContext).drawable;

        /* initialize */
        if (!_myIsInitalized) {
            init(_myContext.gl);
            JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".init", true);
        }
        /* draw bin */
        display(_myContext);
        JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".display", true);

        /* if this FBO is multisampled, resolve it, so it can be displayed */
        if (_myBufferID.framebuffer_object_MULTISAMPLE != UNDEFINED) {
            final int myFBOBufferID = getCurrentlyBoundFBOTexID(gl);
            gl.glBindFramebufferEXT(GL.GL_READ_FRAMEBUFFER_EXT, _myBufferID.framebuffer_object);
            gl.glBindFramebufferEXT(GL.GL_DRAW_FRAMEBUFFER_EXT, _myBufferID.framebuffer_object_MULTISAMPLE);
            gl.glBlitFramebufferEXT(0, 0,
                                    _myTextureWidth, _myTextureHeight,
                                    0, 0,
                                    _myTextureWidth, _myTextureHeight,
                                    GL.GL_COLOR_BUFFER_BIT,
                                    GL.GL_NEAREST);
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, myFBOBufferID);
        }

//        checkFrameBufferStatus(gl);
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

    public TexturePlugin additional_texture(int theID) {
        return _myAdditionalTexture[theID];
    }

    public TexturePlugin[] additional_textures() {
        return _myAdditionalTexture;
    }

    public final void init(final GL gl) {
        final int myFBOBufferID = getCurrentlyBoundFBOTexID(gl);
        /* handle texture creation */
        _myTexCreator.create(gl, _myTextureWidth, _myTextureHeight, _myBufferID);

        if (_myBufferID.additional_textures != null) {
            for (int i = 0; i < _myBufferID.additional_textures.length; i++) {
                _myAdditionalTexture[i].setTextureID(_myBufferID.additional_textures[i]);
            }
        }

        checkFrameBufferStatus(gl);

        /* unbind objects */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, myFBOBufferID);
        gl.glBindTexture(getTextureTarget(), 0);
        JoglUtil.printGLError(gl, getClass().getSimpleName() + ".init()");

        _myIsInitalized = true;
    }

    private final void display(final GLContext theRenderContext) {
        if (_myBin.size() == 0) {
            return;
        }
        final GL gl = theRenderContext.gl;
        /* draw drawables to framebuffer object */
        final int myPreviousFBOBufferID = bindBuffer(gl);
        _myBin.draw(theRenderContext);
        unbindBuffer(gl, myPreviousFBOBufferID);
    }

    public BufferInfo getBufferInfo() {
        return _myBufferID;
    }

    public final int bindBuffer(GL gl) {
        final int myFBOBufferID = getCurrentlyBoundFBOTexID(gl);
        /* bind framebuffer object */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, _myBufferID.framebuffer_object);
        return myFBOBufferID;
    }

//    public static final void unbindBuffer(GL gl) {
//        /* release framebuffer object */
//        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
//    }
    public static final void unbindBuffer(GL gl, int thePreviouslyBoundBufferTexID) {
        /* release framebuffer object */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, thePreviouslyBoundBufferTexID);
    }

    public boolean checkFrameBufferStatus(GL gl) {
        boolean myStatus = JoglUtil.checkFrameBufferStatus(gl);
        if (!myStatus) {
            System.err.println("### ERROR @ " + getClass().getSimpleName() + " / checkFrameBufferStatus");
            System.err.println(toString());
        }
        return myStatus;
    }

    public void update(final GL gl, final GLU glu) {
        /* initialize texture */
        if (!ourStaticInitalized) {
            initStatic(gl);
        }
        JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".initStatic", true);

        if (!_myIsInitalized) {
            _myIsInitalized = true;
            init(gl);
        }
        JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".init", true);

        /* enable and bind texture */
        bind(gl);
        JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".bind", true);

        /* update properties */
        if (_myWrapModeChanged) {
            updateWrapMode(gl);
            _myWrapModeChanged = false;
        }
        JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".updateWrapMode", true);

        if (_myFilterTypeChanged) {
            updateFilterType(gl);
            _myFilterTypeChanged = false;
        }
        JoglUtil.printGLError(gl, glu, JoglFrameBufferObject.class.getSimpleName() + ".updateFilterType "
                + "/ ( also check if you use framebufferobjects with texture rectangle and wrap mode to 'TEXTURE_WRAPMODE_REPEAT').", true);
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

    public String toString() {
        return "(" + name + ", " + hashCode() + ", "
                + "scale(" + getPixelWidth() + ", " + getPixelHeight() + "), "
                + "unit(" + (getTextureUnit() - GL.GL_TEXTURE0) + "), "
                + "target (" + getTextureTargetAsString() + ")";
    }
}
