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

import java.nio.ByteBuffer;

import javax.media.opengl.GL;


public class JoglTexCreatorShadowMap
        implements JoglTexCreator {

    private final int _myTextureTargetID;

    private static final int _myPixelType = GL.GL_UNSIGNED_BYTE;

    public JoglTexCreatorShadowMap(boolean useNonPowerOfTwo) {
        if (useNonPowerOfTwo) {
            _myTextureTargetID = GL.GL_TEXTURE_RECTANGLE_ARB;
        } else {
            _myTextureTargetID = GL.GL_TEXTURE_2D;
        }
    }

    public void create(GL gl,
                       int theWidth,
                       int theHeight,
                       BufferInfo theBufferID) {
        int[] myShadowTexture = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGenTextures(1, myShadowTexture, 0);
        theBufferID.texture = myShadowTexture[0];

        /* allocate and create texture */
        gl.glBindTexture(_myTextureTargetID, theBufferID.texture);
        /** @todo JSR-231 -- what? */
        gl.glTexImage2D(_myTextureTargetID,
                        0,
                        GL.GL_DEPTH_COMPONENT,
                        theWidth,
                        theHeight,
                        0,
                        GL.GL_DEPTH_COMPONENT,
                        _myPixelType,
                        (ByteBuffer)null);
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
//         gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
//         gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);

        gl.glTexParameteri(_myTextureTargetID, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_LUMINANCE);
//         gl.glTexParameteri(_myTextureTargetID, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_INTENSITY);
//         gl.glTexParameteri(_myTextureTargetID, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_ALPHA);
        // gl.glTexParameteri(_myTextureTargetID, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_LUMINANCE_ALPHA);

        /*
         * Set the depth texture up for the Z comparison. The Z value from the texture will be compared to the R texture
         * coordinate, which will be the Z value of the fragment in the spotlight's coordinate system. If the specified
         * comparison (GL.GL_LEQUAL in this case) is false, it outputs 0 (black). It outputs 1 (white) otherwise.
         */

        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_COMPARE_MODE, GL.GL_COMPARE_R_TO_TEXTURE);
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);
//        gl.glTexParameterf(_myTextureTargetID, GL.GL_TEXTURE_COMPARE_FAIL_VALUE_ARB, 0.1f);

        /* setup texture generation */
        gl.glEnable(GL.GL_TEXTURE_GEN_S);
        gl.glEnable(GL.GL_TEXTURE_GEN_T);
        gl.glEnable(GL.GL_TEXTURE_GEN_R);
        gl.glEnable(GL.GL_TEXTURE_GEN_Q);
        gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
        gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
        gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
        gl.glTexGeni(GL.GL_Q, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR);
    }

    public int texturetarget() {
        return _myTextureTargetID;
    }

    public int pixeltype() {
        return _myPixelType;
    }
}
