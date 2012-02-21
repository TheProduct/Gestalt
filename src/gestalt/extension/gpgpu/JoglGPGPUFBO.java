

package gestalt.extension.gpgpu;


import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.Gestalt;
import gestalt.extension.framebufferobject.BufferInfo;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_2xRGBA32Float;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.shape.AbstractDrawable;
import gestalt.util.JoglUtil;
import java.awt.image.DataBufferByte;


public class JoglGPGPUFBO {

    private final JoglFrameBufferObject _myFBO;

    private final FBOAdmin _myFBOAdmin;

    private final FBOViewportTransform _myFBOViewportTransform;

    private float[] _myWriteData = null;

    private float[][] _myAdditionalWriteData = null;

    public JoglGPGPUFBO(final int theWidth, final int theHeight,
                        final int theTextureUnit,
                        final JoglTexCreator theTexCreator) {
        this(theWidth, theHeight, theTextureUnit, theTexCreator, 0);
    }

    public JoglGPGPUFBO(final int theWidth, final int theHeight,
                        final int theTextureUnit,
                        final JoglTexCreator theTexCreator,
                        final int theNumberOfAdditionalTextures) {
        if (theNumberOfAdditionalTextures > 0) {
            final BufferInfo myBufferInfo = BufferInfo.getBufferInfoMultipleTexture(theNumberOfAdditionalTextures);
            _myFBO = new JoglFrameBufferObject(theWidth, theHeight, null, theTexCreator, myBufferInfo);
//            System.out.println("### creating GPGPUFBO with multiple textures.");
        } else {
            if (theTexCreator instanceof JoglTexCreatorFBO_2xRGBA32Float) {
                final BufferInfo myBufferInfo = BufferInfo.getBufferInfoMultipleTexture(1);
                _myFBO = new JoglFrameBufferObject(theWidth, theHeight, null, theTexCreator, myBufferInfo);
//                System.out.println("### creating GPGPUFBO with two textures.");
            } else {
                _myFBO = new JoglFrameBufferObject(theWidth, theHeight, null, theTexCreator, null);
//                System.out.println("### creating GPGPUFBO with a single texture.");
            }
        }
        _myFBOAdmin = new FBOAdmin(_myFBO);
        _myFBOViewportTransform = new FBOViewportTransform(_myFBO);

        _myFBO.add(_myFBOAdmin);
        _myFBO.add(_myFBOViewportTransform);

        _myFBO.setWrapMode(Gestalt.TEXTURE_WRAPMODE_CLAMP);
        _myFBO.setFilterType(Gestalt.TEXTURE_FILTERTYPE_NEAREST);
        _myFBO.setTextureTarget(GL.GL_TEXTURE_RECTANGLE_ARB);
        _myFBO.setTextureUnit(theTextureUnit);

        if (_myFBO.additional_textures() != null) {
            for (int i = 0; i < _myFBO.additional_textures().length; i++) {
                _myFBO.additional_texture(i).setWrapMode(Gestalt.TEXTURE_WRAPMODE_CLAMP);
                _myFBO.additional_texture(i).setFilterType(Gestalt.TEXTURE_FILTERTYPE_NEAREST);
                _myFBO.additional_texture(i).setTextureTarget(GL.GL_TEXTURE_RECTANGLE_ARB);
                _myFBO.additional_texture(i).setTextureUnit(theTextureUnit + 1 + i);
            }
        }
    }

    public JoglFrameBufferObject fbo() {
        return _myFBO;
    }

    /* utilities */
    public static void quadFullscreenQuad(GL gl, float theWidth, float theHeight) {
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(0, 0);
        gl.glTexCoord2f(theWidth, 0);
        gl.glVertex2f(theWidth, 0);
        gl.glTexCoord2f(theWidth, theHeight);
        gl.glVertex2f(theWidth, theHeight);
        gl.glTexCoord2f(0, theHeight);
        gl.glVertex2f(0, theHeight);
        gl.glEnd();
    }

    public void scheduleWriteData(final float[] theSource) {
        _myWriteData = theSource;
    }

    public void scheduleAdditionalWriteData(final float[][] theSource) {
        _myAdditionalWriteData = theSource;
    }

    public boolean isWriteDataScheduled() {
        return _myAdditionalWriteData != null || _myWriteData != null;
    }

    public float[] getReadBackData() {
        return _myFBOAdmin._myResultArray;
    }

    public void writeData(final GL gl, final GLU glu) {
        _myFBOAdmin.writeData(gl, glu);
    }


    /* --- */
    private class FBOAdmin
            extends AbstractDrawable {

        private static final int COMPONENTS = 4;

        private final float[] _myResultArray;

        private final FloatBuffer _myResultBuffer;

        private final JoglFrameBufferObject _myFBO;

        private final int _myWidth;

        private final int _myHeight;

//        private boolean _myFirstFrame = true;
        private boolean _myClearBuffer = false;

        private final boolean _myReadBackData = false;

        public FBOAdmin(final JoglFrameBufferObject theFBO) {
            _myFBO = theFBO;
            _myWidth = theFBO.getPixelWidth();
            _myHeight = theFBO.getPixelHeight();
            _myResultArray = new float[_myWidth * _myHeight * COMPONENTS];
            _myResultBuffer = FloatBuffer.wrap(_myResultArray);
        }

        public void draw(final GLContext theContext) {
            final GL gl = (theContext).gl;
            final GLU glu = (theContext).glu;

            /* disable blending. this seems to be an ATI bug. */
            gl.glDisable(GL.GL_BLEND);

            /* clear color buffer. do we have a depth buffer? */
            if (_myClearBuffer) {
//            if (_myFirstFrame || _myClearBuffer) {
//                _myFirstFrame = false;
                gl.glClearColor(_myFBO.backgroundcolor().r,
                                _myFBO.backgroundcolor().g,
                                _myFBO.backgroundcolor().b,
                                _myFBO.backgroundcolor().a);
                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            }

            /* write data */
            writeData(gl, glu);

            /* read data */
            if (_myReadBackData) {
                readbackData(gl, glu);
            }

            JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".draw", true);
        }

        private void readbackData(final GL gl, final GLU glu) {
            gl.glReadPixels(0, 0, _myWidth, _myHeight, GL.GL_RGBA, GL.GL_FLOAT, _myResultBuffer);
            JoglUtil.printGLError(gl, glu, "readbackData", true);
        }

        private static final boolean USE_DRAW_PIXELS = false;

        private void writeDataToTexture(final GL gl, final GLU glu,
                                        final TexturePlugin myTexture,
                                        final float[] theSource) {

            /* prepare data */
            final FloatBuffer myWriteData = FloatBuffer.wrap(theSource);

            /* transfer data to texture */
            if (USE_DRAW_PIXELS) {
                gl.glDrawPixels(myTexture.getPixelWidth(), myTexture.getPixelHeight(),
                                GL.GL_RGBA,
                                _myFBO.creator().pixeltype(),
                                myWriteData);
            } else {
                /* transfer data to texture */
                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glEnable(myTexture.getTextureTarget());
                gl.glBindTexture(myTexture.getTextureTarget(),
                                 myTexture.getTextureID());
                gl.glTexSubImage2D(myTexture.getTextureTarget(),
                                   0,
                                   0, 0,
                                   myTexture.getPixelWidth(), myTexture.getPixelHeight(),
                                   GL.GL_RGBA,
                                   _myFBO.creator().pixeltype(),
                                   myWriteData);
                gl.glBindTexture(myTexture.getTextureTarget(), 0);
                gl.glDisable(myTexture.getTextureTarget());
            }
            JoglUtil.printGLError(gl, glu, "writeDataToTexture", true);
        }

        public void writeData(final GL gl, final GLU glu) {
            if (_myWriteData != null || _myAdditionalWriteData != null) {
                /* ATI workaround -- i really hate someone for this! */
                gl.glDisable(GL.GL_BLEND);

                int myPreviousFBOBufferID = 0;

                if (USE_DRAW_PIXELS) {
                    myPreviousFBOBufferID = _myFBO.bindBuffer(gl);
                    gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
                    gl.glPushClientAttrib(GL.GL_CLIENT_PIXEL_STORE_BIT);

                    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);
                    gl.glColorMask(true, true, true, true);
                    gl.glDepthMask(false);
                    gl.glWindowPos2i(0, 0);
                }

                JoglUtil.printGLError(gl, glu, "writeData0", true);

                if (_myWriteData != null) {
                    if (USE_DRAW_PIXELS) {
                        gl.glDrawBuffer(_myFBO.getBufferInfo().attachment_point);
                    }
                    JoglUtil.printGLError(gl, glu, "writeData1", true);
                    writeDataToTexture(gl, glu, _myFBO, _myWriteData);
                    _myWriteData = null;
                }

                if (_myAdditionalWriteData != null && _myFBO.getBufferInfo().additional_attachment_points != null) {
                    if (_myAdditionalWriteData.length != _myFBO.getBufferInfo().additional_attachment_points.length) {
                        System.err.println("### WARNING @ " + getClass().getSimpleName() + ".writeData / write data and additional attachments should be of same length.");
                    }
                    if (_myFBO.additional_textures() != null) {
                        for (int i = 0; i < _myAdditionalWriteData.length; i++) {
                            if (USE_DRAW_PIXELS) {
                                gl.glDrawBuffer(_myFBO.getBufferInfo().additional_attachment_points[i]);
                            }
                            JoglUtil.printGLError(gl, glu, "writeData" + (i + 2), true);
                            writeDataToTexture(gl, glu, _myFBO.additional_texture(i), _myAdditionalWriteData[i]);
                        }
                    } else {
                        System.err.println("### WARNING @ " + getClass().getSimpleName() + ".writeData / there are no additional texture to write to.");
                    }
                    _myAdditionalWriteData = null;
                }

                if (USE_DRAW_PIXELS) {
                    gl.glPopClientAttrib();
                    gl.glPopAttrib();
                    JoglFrameBufferObject.unbindBuffer(gl, myPreviousFBOBufferID);
                }
            }
        }
    }


    /* --- */
    private class FBOViewportTransform
            extends AbstractDrawable {

        private final JoglFrameBufferObject _myFBO;

        private final int _myWidth;

        private final int _myHeight;

        public FBOViewportTransform(final JoglFrameBufferObject theFBO) {
            _myFBO = theFBO;
            _myWidth = _myFBO.getPixelWidth();
            _myHeight = _myFBO.getPixelHeight();
        }

        public void draw(final GLContext theContext) {
            final GL gl = (theContext).gl;
            final GLU glu = (theContext).glu;

            /* viewport transform for 1:1 pixel=texel=data mapping */
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluOrtho2D(0, _myWidth, 0, _myHeight);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glViewport(0, 0, _myWidth, _myHeight);

            JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".draw", true);
        }
    }
}
