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


import javax.media.opengl.GL;

import gestalt.util.JoglUtil;


public class JoglTexCreatorFBO_MultipleRGBA32Float
        implements JoglTexCreator {

    private final int _myTextureTarget = GL.GL_TEXTURE_RECTANGLE_ARB;

    private static final int texture_format = GL.GL_RGBA;

    private static final int internal_format = GL.GL_RGBA32F_ARB;

    private static final int _myPixelType = GL.GL_FLOAT;

    public void create(final GL gl,
                       final int theWidth,
                       final int theHeight,
                       final BufferInfo theBufferID) {
        /* create buffers */
        createBuffers(gl, theBufferID);

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theBufferID.framebuffer_object);
        createTexture(gl,
                      theWidth, theHeight,
                      theBufferID.texture,
                      theBufferID.attachment_point);
        if (theBufferID.additional_textures != null) {
            for (int i = 0; i < theBufferID.additional_textures.length; i++) {
                createTexture(gl,
                              theWidth, theHeight,
                              theBufferID.additional_textures[i],
                              theBufferID.additional_attachment_points[i]);
            }
        }
    }

    private void createTexture(final GL gl,
                               final int theWidth, int theHeight,
                               final int theTextureID,
                               final int theAttachementPoint) {
        gl.glBindTexture(_myTextureTarget, theTextureID);

        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameteri(_myTextureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);

        gl.glTexImage2D(_myTextureTarget,
                        0,
                        internal_format,
                        theWidth,
                        theHeight,
                        0,
                        texture_format,
                        _myPixelType,
                        null);

        /* bind texture to FBO */
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                                     theAttachementPoint,
                                     _myTextureTarget,
                                     theTextureID,
                                     0);
    }

    private boolean createBuffers(GL gl, BufferInfo theBufferIDs) {
        /* check for framebuffer object extension */
        if (!JoglUtil.testExtensionAvailability(gl, "GL_EXT_framebuffer_object")) {
            return false;
        }

        if (!JoglUtil.testExtensionAvailability(gl, "GL_ARB_texture_float")) {
            return false;
        }

        /* create textures */
        final int myAdditionalTexture = theBufferIDs.additional_textures == null ? 0 : theBufferIDs.additional_textures.length;
        int[] myTextureConainter = new int[1 + myAdditionalTexture];
        gl.glGenTextures(myTextureConainter.length, myTextureConainter, 0);
        theBufferIDs.texture = myTextureConainter[0];
        if (theBufferIDs.additional_textures != null) {
            for (int i = 0; i < theBufferIDs.additional_textures.length; i++) {
                theBufferIDs.additional_textures[i] = myTextureConainter[i + 1];
            }
        }

        /* create framebuffer */
        int[] myFrameBufferConainter = new int[1];
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
