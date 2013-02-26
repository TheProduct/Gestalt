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


public class JoglPBOIndexedPointMesh
    extends JoglPBOIndexedMesh {

    public JoglPBOIndexedPointMesh(final JoglFrameBufferObject theFrameBufferObject,
                                   final boolean theUseColors) {
        super(theFrameBufferObject, theUseColors, false, false);
    }


    protected int getPrimitiveType() {
        return GL.GL_POINTS;
    }


    protected int getNumberOfVertices(int w, int h) {
        return w * h;
    }


    protected int[] setupIndexList(int theNumberOfIndices, int w, int h) {
        int[] myIndices = new int[theNumberOfIndices];
        int i = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                myIndices[i++] = i; //x + y * w;
            }
        }
        return myIndices;
    }
}
