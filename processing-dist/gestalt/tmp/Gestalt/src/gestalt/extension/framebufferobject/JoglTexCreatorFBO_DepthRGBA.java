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


public class JoglTexCreatorFBO_DepthRGBA
        implements JoglTexCreator {

    public static boolean VERBOSE = false;

    private final int _myPixelType;

    private final int _myDepthInternalFormat;

    private final int _myTexInternalFormat;

    private final int _myTextureTarget;

    public JoglTexCreatorFBO_DepthRGBA() {
        this(GL.GL_UNSIGNED_BYTE,
             GL.GL_RGBA,
             GL.GL_DEPTH_COMPONENT24,
             GL.GL_TEXTURE_2D);
    }

    public JoglTexCreatorFBO_DepthRGBA(int thePixelType,
                                       int theTexInternalFormat,
                                       int theDepthInternalFormat,
                                       int theTextureTarget) {
        _myPixelType = thePixelType;
        _myDepthInternalFormat = theDepthInternalFormat;
        _myTexInternalFormat = theTexInternalFormat;
        _myTextureTarget = theTextureTarget;
    }

    public void create(GL gl, int theWidth, int theHeight, BufferInfo theBufferID) {

        /* create buffers */
        createBuffers(gl, theBufferID);

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theBufferID.framebuffer_object);

        gl.glBindTexture(_myTextureTarget, theBufferID.texture);

        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

        gl.glTexImage2D(_myTextureTarget,
                        0,
                        _myTexInternalFormat,
                        theWidth,
                        theHeight,
                        0,
                        GL.GL_RGBA,
                        _myPixelType,
                        (Buffer)null);

        /* bind texture to FBO */
//        if (theBufferID.attachment_point != GL.GL_COLOR_ATTACHMENT0_EXT) {
//            System.out.println("attachment_point != COLOR_ATTACHMENT0_EXT // is that OK?");
//            // http://www.khronos.org/opengles/sdk/docs/man/glFramebufferTexture2D.xml
//        }
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                                     theBufferID.attachment_point,
                                     _myTextureTarget,
                                     theBufferID.texture,
                                     0);

        if (_myDepthInternalFormat != Gestalt.UNDEFINED) {
            /* initialize depth renderbuffer */
            gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, theBufferID.renderbuffer_depth);
            gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT,
                                        _myDepthInternalFormat,
                                        theWidth, theHeight);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT,
                                            GL.GL_DEPTH_ATTACHMENT_EXT,
                                            GL.GL_RENDERBUFFER_EXT,
                                            theBufferID.renderbuffer_depth);
        }
    }

    private boolean createBuffers(GL gl, BufferInfo theBufferIDs) {
        /* check for framebuffer object extension */
        if (!gl.isExtensionAvailable("GL_EXT_framebuffer_object")) {
            System.err.println("### ERROR @ FrameBufferObject / GL_EXT_framebuffer_object not available.");
            return false;
        }

        /* create textures */
        int[] myTextureConainter = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGenTextures(1, myTextureConainter, 0);
        theBufferIDs.texture = myTextureConainter[0];

        /* create framebuffer */
        int[] myFrameBufferContainer = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGenFramebuffersEXT(1, myFrameBufferContainer, 0);
        theBufferIDs.framebuffer_object = myFrameBufferContainer[0];

        if (_myDepthInternalFormat != Gestalt.UNDEFINED) {
            /* create depthbuffer */
            int[] myDepthRenderBufferConainter = new int[1];
            /** @todo JSR-231 -- added 0 */
            gl.glGenRenderbuffersEXT(1, myDepthRenderBufferConainter, 0);
            theBufferIDs.renderbuffer_depth = myDepthRenderBufferConainter[0];
        }

        return true;
    }

    public int texturetarget() {
        return _myTextureTarget;
    }

    public int pixeltype() {
        return _myPixelType;
    }
}
