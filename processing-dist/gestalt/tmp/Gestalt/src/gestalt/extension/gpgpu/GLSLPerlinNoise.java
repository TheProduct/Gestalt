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


package gestalt.extension.gpgpu;


import gestalt.Gestalt;
import gestalt.material.texture.TextureInfo;
import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class GLSLPerlinNoise
        implements TextureInfo {

    /*
     * Testbed for GLSL fragment noise() replacement.
     *
     * Shaders are loaded from two external files:
     * "GLSLnoisetest.vert" and "GLSLnoisetest.frag".
     * The program itself draws a spinning sphere
     * with a noise-generated fragment color.
     *
     * This program uses GLFW for convenience, to handle the OS-specific
     * window management stuff. Some Windows-specific stuff is still there,
     * though, so this code is not portable to other platforms.
     * The non-portable parts of the code is the MessageBox() function
     * call, which is easily replaced by a fprintf(stderr) call,
     * and probably most of the stupid loading of function pointers that
     * Windows requires of you to access anything above OpenGL 1.1.
     *
     * Author: Stefan Gustavson (stegu@itn.liu.se) 2004
     */
    private int _myTextureID = Gestalt.UNDEFINED;

    private final int _myTextureUnit;

    public GLSLPerlinNoise(final int theTextureUnit) {
        _myTextureUnit = theTextureUnit;
    }

    /*
    int getTextureTarget();
    int getTextureID();
    int getTextureUnit();
     */
    public int getTextureUnit() {
        return _myTextureUnit;
    }

    public int getTextureID() {
        return _myTextureID;
    }

    public int getTextureTarget() {
        return GL.GL_TEXTURE_2D;
    }

    private final int[] perm = {151, 160, 137, 91, 90, 15,
                                131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
                                190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
                                88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
                                77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
                                102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
                                135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
                                5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
                                223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
                                129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
                                251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
                                49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
                                138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

    /* These are Ken Perlin's proposed gradients for 3D noise. I kept them for
    better consistency with the reference implementation, but there is really
    no need to pad this to 16 gradients for this particular implementation.
    If only the "proper" first 12 gradients are used, they can be extracted
    from the grad4[][] array: grad3[i][j] == grad4[i*2][j], 0<=i<=11, j=0,1,2
     */
    private final int[][] grad3 = {{0, 1, 1}, {0, 1, -1}, {0, -1, 1}, {0, -1, -1},
                                   {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1},
                                   {1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0}, // 12 cube edges
                                   {1, 0, -1}, {-1, 0, -1}, {0, -1, 1}, {0, 1, 1}}; // 4 more to make 16

    /*
     * initPermTexture(GLuint *_myTextureID) - create and load a 2D texture for
     * a combined index permutation and gradient lookup table.
     * This texture is used for 2D and 3D noise, both classic and simplex.
     */
    public void init(final GL gl) {
        byte[] pixels;
        int i, j;

        int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        _myTextureID = tmp[0];
        gl.glBindTexture(GL.GL_TEXTURE_2D, _myTextureID);

        pixels = new byte[256 * 256 * 4];
        for (i = 0; i < 256; i++) {
            for (j = 0; j < 256; j++) {
                int offset = (i * 256 + j) * 4;
                byte value = (byte)(perm[(j + perm[i]) & 0xFF]);
                pixels[offset] = (byte)(grad3[value & 0x0F][0] * 64 + 64);   // Gradient x
                pixels[offset + 1] = (byte)(grad3[value & 0x0F][1] * 64 + 64); // Gradient y
                pixels[offset + 2] = (byte)(grad3[value & 0x0F][2] * 64 + 64); // Gradient z
                pixels[offset + 3] = value;                     // Permuted index
            }
        }

        // GLFW texture loading functions won't work here - we need GL_NEAREST lookup.
        final ByteBuffer myBuffer = ByteBuffer.wrap(pixels);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 256, 256, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, myBuffer);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }

    public void update(final GL gl, final GLU glu) {
        if (_myTextureID == Gestalt.UNDEFINED) {
            init(gl);
        }
    }

    public void bind(final GL gl) {
        gl.glActiveTexture(_myTextureUnit);
        gl.glBindTexture(GL.GL_TEXTURE_2D, _myTextureID);
    }

    public void unbind(final GL gl) {
        gl.glActiveTexture(_myTextureUnit);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }
}