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

import gestalt.util.JoglUtil;


public class JoglTexCreatorFBO_ShadowMap
        implements JoglTexCreator {

    private final int _myPixelType;

    private final int _myTexInternalFormat;

    private final int _myTextureTarget;

    public JoglTexCreatorFBO_ShadowMap() {
        this(GL.GL_UNSIGNED_BYTE,
             GL.GL_DEPTH_COMPONENT,
             GL.GL_TEXTURE_2D);
    }

    public JoglTexCreatorFBO_ShadowMap(int thePixelType,
                                       int theTexInternalFormat,
                                       int theTextureTarget) {
        _myPixelType = thePixelType;
        _myTexInternalFormat = theTexInternalFormat;
        _myTextureTarget = theTextureTarget;
    }

    public void create(GL gl, int theWidth, int theHeight, BufferInfo theBufferID) {

        /* create buffers */
        createBuffers(gl, theBufferID);

        /* allocate texture */
        gl.glBindTexture(_myTextureTarget, theBufferID.texture);

        /* setup texture for shadowmap generation */
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        gl.glTexParameteri(_myTextureTarget, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_LUMINANCE);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_COMPARE_MODE, GL.GL_COMPARE_R_TO_TEXTURE);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);

        /* setup texture generation */
        gl.glEnable(GL.GL_TEXTURE_GEN_S);
        gl.glEnable(GL.GL_TEXTURE_GEN_T);
        gl.glEnable(GL.GL_TEXTURE_GEN_R);
        gl.glEnable(GL.GL_TEXTURE_GEN_Q);

        gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
        gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
        gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
        gl.glTexGeni(GL.GL_Q, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);

        gl.glTexImage2D(_myTextureTarget,
                        0,
                        _myTexInternalFormat,
                        theWidth,
                        theHeight,
                        0,
                        GL.GL_DEPTH_COMPONENT, // fixed type
                        _myPixelType,
                        (Buffer)null);

        JoglUtil.printGLError(gl, "texture creation");

        /* bind FBO */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theBufferID.framebuffer_object);

        /* bind texture to FBO */
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                                     GL.GL_DEPTH_ATTACHMENT_EXT,
                                     _myTextureTarget,
                                     theBufferID.texture,
                                     0);

        /* disable color buffer */
        gl.glReadBuffer(GL.GL_NONE);
        gl.glDrawBuffer(GL.GL_NONE);

        // Check framebuffer completeness at the end of initialization.
        JoglUtil.checkFrameBufferStatus(gl);
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
        int[] myFrameBufferConainter = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGenFramebuffersEXT(1, myFrameBufferConainter, 0);
        theBufferIDs.framebuffer_object = myFrameBufferConainter[0];

        return true;
    }

    public int texturetarget() {
        return _myTextureTarget;
    }

    public int pixeltype() {
        return _myPixelType;
    }
}
