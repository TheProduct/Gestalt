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

import gestalt.Gestalt;


public class BufferInfo {

    public static final int SECONDARY = 0;

    public static final int TERTIARY = 1;

    public static final int QUATERNARY = 2;

    public int framebuffer_object = 0;

    public int renderbuffer_depth = 0;

    public int renderbuffer_stencil = Gestalt.UNDEFINED;

    public int renderbuffer_color = Gestalt.UNDEFINED;

    public int framebuffer_object_MULTISAMPLE = Gestalt.UNDEFINED;

    public int texture = 0;

    public int attachment_point = GL.GL_COLOR_ATTACHMENT0_EXT;

    public int[] additional_textures = null;

    public int[] additional_attachment_points = null;

    public static BufferInfo getBufferInfoMultipleTexture(int theNumberOfAdditionalTexture) {
        final BufferInfo myBufferInfo = new BufferInfo();
        myBufferInfo.additional_textures = new int[theNumberOfAdditionalTexture];
        myBufferInfo.additional_attachment_points = new int[theNumberOfAdditionalTexture];
        for (int i = 0; i < myBufferInfo.additional_textures.length; i++) {
            myBufferInfo.additional_textures[i] = Gestalt.UNDEFINED;
            myBufferInfo.additional_attachment_points[i] = GL.GL_COLOR_ATTACHMENT1_EXT + i;
        }
        return myBufferInfo;
    }
}
