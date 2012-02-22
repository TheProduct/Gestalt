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


import java.nio.Buffer;
import javax.media.opengl.GL;

import gestalt.Gestalt;
import gestalt.util.JoglUtil;


/**
 * for refernce see:
 * http://www.nvidia.com/dev_content/nvopenglspecs/GL_EXT_framebuffer_object.txt
 * and NVIDIAs 'Simple framebuffer object (FBO) example'
 *
 * NOTE no CSAA support.
 *
 */
public class JoglTexCreatorFBO_DepthRGBAMultisample
        implements JoglTexCreator {

    public static boolean VERBOSE = false;

    private final int _myPixelType;

    private final int _myDepthInternalFormat;

    private final int _myTexInternalFormat;

    private final int _myTextureTarget;

    private final int _mySamples;

    public JoglTexCreatorFBO_DepthRGBAMultisample(final int theMultiSampling) {
        this(GL.GL_UNSIGNED_BYTE,
             GL.GL_RGBA,
             GL.GL_DEPTH_COMPONENT24,
             GL.GL_TEXTURE_2D,
             theMultiSampling);
    }

    public JoglTexCreatorFBO_DepthRGBAMultisample(int thePixelType,
                                                  int theTexInternalFormat,
                                                  int theDepthInternalFormat,
                                                  int theTextureTarget,
                                                  final int theMultiSampling) {
        _myPixelType = thePixelType;
        _myDepthInternalFormat = theDepthInternalFormat;
        _myTexInternalFormat = theTexInternalFormat;
        _myTextureTarget = theTextureTarget;
        _mySamples = theMultiSampling;
    }

    public void create(final GL gl, final int theWidth,
                       final int theHeight,
                       final BufferInfo theBufferID) {
        /* create buffers */
        createBuffers(gl, theBufferID);

        gl.glBindTexture(_myTextureTarget, theBufferID.texture);

        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        gl.glTexImage2D(_myTextureTarget,
                        0,
                        _myTexInternalFormat,
                        theWidth,
                        theHeight,
                        0,
                        GL.GL_RGBA,
                        _myPixelType,
                        (Buffer)null);

        /* bind texture to multisample resolve FBO */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theBufferID.framebuffer_object_MULTISAMPLE);
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                                     theBufferID.attachment_point,
                                     _myTextureTarget,
                                     theBufferID.texture,
                                     0);

        JoglUtil.checkFrameBufferStatus(gl);

        /* handle the rendering FBO */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theBufferID.framebuffer_object);

        /* initialize color renderbuffer */
        gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, theBufferID.renderbuffer_color);

        /* create a regular MSAA color buffer */
        gl.glRenderbufferStorageMultisampleEXT(GL.GL_RENDERBUFFER_EXT,
                                               _mySamples,
                                               _myTexInternalFormat,
                                               theWidth, theHeight);

        // check the number of samples
        int[] myQuery = new int[1];
        gl.glGetRenderbufferParameterivEXT(GL.GL_RENDERBUFFER_EXT,
                                           GL.GL_RENDERBUFFER_SAMPLES_EXT,
                                           myQuery, 0);
//        if (myQuery[0] < _mySamples) {
//            System.out.println("myQuery[0] < _mySamples (" + myQuery[0] + ")");
//        } else if (myQuery[0] > _mySamples) {
//            System.out.println("myQuery[0] > _mySamples (" + myQuery[0] + ")");
//        }

        /* attach the multisampled color buffer */
        gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT,
                                        theBufferID.attachment_point,
                                        GL.GL_RENDERBUFFER_EXT,
                                        theBufferID.renderbuffer_color);

        JoglUtil.checkFrameBufferStatus(gl);

        final boolean USE_DEPTHBUFFER = true; // depthbuffer fails on my ATI X1600...
        if (USE_DEPTHBUFFER) {
            /* bind the multisampled depth buffer */
            gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT,
                                     theBufferID.renderbuffer_depth);

            /* create a MSAA depth buffer */
            gl.glRenderbufferStorageMultisampleEXT(GL.GL_RENDERBUFFER_EXT,
                                                   _mySamples,
                                                   _myDepthInternalFormat,
                                                   theWidth, theHeight);

            /* attach the depth buffer */
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT,
                                            GL.GL_DEPTH_ATTACHMENT_EXT,
                                            GL.GL_RENDERBUFFER_EXT,
                                            theBufferID.renderbuffer_depth);

            JoglUtil.checkFrameBufferStatus(gl);
        }
    }

    private boolean createBuffers(GL gl, BufferInfo theBufferIDs) {
        /* check for framebuffer object extension */
        boolean myState = true;
        myState &= JoglUtil.checkExtension(gl, "GL_EXT_framebuffer_object", getClass().getSimpleName() + ".createBuffers");
        myState &= JoglUtil.checkExtension(gl, "GL_EXT_framebuffer_blit", getClass().getSimpleName() + ".createBuffers");
        myState &= JoglUtil.checkExtension(gl, "GL_EXT_framebuffer_multisample", getClass().getSimpleName() + ".createBuffers");
        if (!myState) {
            System.err.println("### ERROR @ FrameBufferObject / failed at 'createBuffers' / extensions missing.");
            return false;
        }

        /* create textures */
        final int[] myTextureConainter = new int[1];
        gl.glGenTextures(1, myTextureConainter, 0);
        theBufferIDs.texture = myTextureConainter[0];

        /* create framebuffer */
        final int[] myFrameBufferContainer = new int[1];
        gl.glGenFramebuffersEXT(1, myFrameBufferContainer, 0);
        theBufferIDs.framebuffer_object = myFrameBufferContainer[0];

        /* create framebuffer mulitsample */
        final int[] myFrameBufferMultisampleContainer = new int[1];
        gl.glGenFramebuffersEXT(1, myFrameBufferMultisampleContainer, 0);
        theBufferIDs.framebuffer_object_MULTISAMPLE = myFrameBufferMultisampleContainer[0];

        /* create color renderbuffer */
        final int[] myRenderBufferColor = new int[1];
        gl.glGenRenderbuffersEXT(1, myRenderBufferColor, 0);
        theBufferIDs.renderbuffer_color = myRenderBufferColor[0];

        /* create depth renderbuffer */
        int[] myRenderBufferDepth = new int[1];
        gl.glGenRenderbuffersEXT(1, myRenderBufferDepth, 0);
        theBufferIDs.renderbuffer_depth = myRenderBufferDepth[0];

        return true;
    }

    public int texturetarget() {
        return _myTextureTarget;
    }

    public int pixeltype() {
        return _myPixelType;
    }
}