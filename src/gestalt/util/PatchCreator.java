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


package gestalt.util;

import gestalt.material.Color;
import gestalt.shape.Mesh;
import gestalt.shape.MeshVBO;

import mathematik.Vector2f;
import mathematik.Vector2i;
import mathematik.Vector3f;

import static gestalt.Gestalt.MESH_QUADS;


public abstract class PatchCreator {

    // ///////
    // 3--2 //
    // |..| //
    // 0--1 //
    // ///////
    public static Mesh getRectangularPatch(Vector2i theResolution,
                                           Vector2f theSize,
                                           Vector3f theBottomLeftPosition,
                                           Color theColor,
                                           float[] theStartTexCoords,
                                           float[] theStopTexCoords,
                                           boolean useVBO) {

        /* vertices */
        float myWidth = theSize.x / (float)theResolution.x;
        float myHeight = theSize.y / (float)theResolution.y;
        int myNumberOfVertices = 4 * (theResolution.x * theResolution.y);
        float[] myVertices = new float[myNumberOfVertices * 3];
        int myIndex = -1;
        for (int x = 0; x < theResolution.x; x++) {
            for (int y = 0; y < theResolution.y; y++) {
                /* 0 */
                myVertices[++myIndex] = myWidth * x + theBottomLeftPosition.x;
                myVertices[++myIndex] = myHeight * y + theBottomLeftPosition.y;
                myVertices[++myIndex] = 0;
                /* 1 */
                myVertices[++myIndex] = myWidth * (x + 1) + theBottomLeftPosition.x;
                myVertices[++myIndex] = myHeight * y + theBottomLeftPosition.y;
                myVertices[++myIndex] = 0;
                /* 2 */
                myVertices[++myIndex] = myWidth * (x + 1) + theBottomLeftPosition.x;
                myVertices[++myIndex] = myHeight * (y + 1) + theBottomLeftPosition.y;
                myVertices[++myIndex] = 0;
                /* 3 */
                myVertices[++myIndex] = myWidth * x + theBottomLeftPosition.x;
                myVertices[++myIndex] = myHeight * (y + 1) + theBottomLeftPosition.y;
                myVertices[++myIndex] = 0;
            }
        }

        /* color */
        float[] myVertexColors = new float[myNumberOfVertices * 4];
        for (int i = 0; i < myVertexColors.length; i += 4) {
            myVertexColors[i] = theColor.r;
            myVertexColors[i + 1] = theColor.g;
            myVertexColors[i + 2] = theColor.b;
            myVertexColors[i + 3] = theColor.a;
        }

        /* texturecoords */
        float[] myTexCoordinates = null;
        if (theStartTexCoords != null && theStopTexCoords != null) {
            float[] myStart = theStartTexCoords;
            float[] myStop = theStopTexCoords;
            float myCoordWidth = (myStop[0] - myStart[0]) / (float)theResolution.x;
            float myCoordHeight = (myStop[1] - myStart[1]) / (float)theResolution.y;
            myTexCoordinates = new float[myNumberOfVertices * 2];
            myIndex = -1;
            for (int x = 0; x < theResolution.x; x++) {
                for (int y = 0; y < theResolution.y; y++) {
                    /* 0 */
                    myTexCoordinates[++myIndex] = myCoordWidth * x + myStart[0];
                    myTexCoordinates[++myIndex] = myCoordHeight * y + myStart[1];
                    /* 1 */
                    myTexCoordinates[++myIndex] = myCoordWidth * (x + 1) + myStart[0];
                    myTexCoordinates[++myIndex] = myCoordHeight * y + myStart[1];
                    /* 2 */
                    myTexCoordinates[++myIndex] = myCoordWidth * (x + 1) + myStart[0];
                    myTexCoordinates[++myIndex] = myCoordHeight * (y + 1) + myStart[1];
                    /* 3 */
                    myTexCoordinates[++myIndex] = myCoordWidth * x + myStart[0];
                    myTexCoordinates[++myIndex] = myCoordHeight * (y + 1) + myStart[1];
                }
            }
        }

        /* normals */
        float[] myNormals = new float[myNumberOfVertices * 3];
        myIndex = -1;
        for (int i = 0; i < myNormals.length - 2; i += 3) {
            myNormals[++myIndex] = 0f;
            myNormals[++myIndex] = 0f;
            myNormals[++myIndex] = 1f;
        }

        /* get mesh */
        // TODO if we d use a factory here this could become a 'Patch' instead
        // of 'PatchCreator'
        if (useVBO) {
            return new MeshVBO(myVertices, 3, myVertexColors, 4, myTexCoordinates, 2, myNormals, MESH_QUADS);
        } else {
            return new Mesh(myVertices, 3, myVertexColors, 4, myTexCoordinates, 2, myNormals, MESH_QUADS);
        }
    }
}
