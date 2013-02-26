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


public class JoglPBOIndexedTriangleMesh
    extends JoglPBOIndexedMesh {

    public JoglPBOIndexedTriangleMesh(final JoglFrameBufferObject theFrameBufferObject,
                                      final boolean theUseColors,
                                      final boolean theUseNormals,
                                      final boolean theUseTexCoords) {
        super(theFrameBufferObject, theUseColors, theUseNormals, theUseTexCoords);
    }


    protected int getPrimitiveType() {
        return GL.GL_TRIANGLES;
    }


    protected int getNumberOfVertices(int w, int h) {
        return (w - 1) * (h - 1) * 6;
    }


    protected int[] setupIndexList(int theNumberOfIndices, int w, int h) {
        int[] myIndices = new int[theNumberOfIndices];
        int i = 0;
        for (int x = 0; x < w - 1; x++) {
            for (int y = 0; y < h - 1; y++) {
                myIndices[i + 0] = to1D(x + 0, y + 0, w);
                myIndices[i + 1] = to1D(x + 1, y + 0, w);
                myIndices[i + 2] = to1D(x + 1, y + 1, w);
                myIndices[i + 3] = to1D(x + 0, y + 0, w);
                myIndices[i + 4] = to1D(x + 1, y + 1, w);
                myIndices[i + 5] = to1D(x + 0, y + 1, w);
                i += 6;
            }
        }
        return myIndices;
    }


    private final int to1D(final int x, final int y, final int width) {
        return x + y * width;
    }
}
