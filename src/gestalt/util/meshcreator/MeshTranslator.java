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
package gestalt.util.meshcreator;


import gestalt.render.Drawable;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.shape.Mesh;

import mathematik.TransformMatrix4f;
import mathematik.Vector2f;
import mathematik.Vector3f;


public class MeshTranslator
        implements DrawableMeshTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Mesh;
    }

    public void parse(MeshCreator theParent, Drawable theDrawable) {
        /**
         * @todo there are a million things missing here. translation from
         * triangle to quad for example...
         */
        final Mesh myMesh = (Mesh) theDrawable;
        final Material material = myMesh.material();

        final TransformMatrix4f myTransform = gestalt.util.Util.getTranslateRotationTransform(myMesh.getTransformMode(),
                                                                                              myMesh.transform(),
                                                                                              myMesh.rotation(),
                                                                                              myMesh.scale());

        final int _myDrawLength = myMesh.drawlength();
        final float[] _myNormals = myMesh.normals();
        final float[] _myTexCoords = myMesh.texcoords();
        final float[] _myColors = myMesh.colors();
        final float[] _myVertices = myMesh.vertices();
        final int NUMBER_OF_NORMAL_COMPONENTS = 3;
        final int _myDrawStart = myMesh.drawstart();
        final int _myNumberOfTexCoordComponents = myMesh.getNumberOfTexCoordComponents();
        final int _myNumberOfColorComponents = myMesh.getNumberOfColorComponents();
        final int _myNumberOfVertexComponents = myMesh.getNumberOfVertexComponents();

        int myNormalIndex = _myDrawStart * NUMBER_OF_NORMAL_COMPONENTS;
        int myTexCoordIndex = _myDrawStart * _myNumberOfTexCoordComponents;
        int myColorIndex = _myDrawStart * _myNumberOfColorComponents;
        int myVertexIndex = _myDrawStart * _myNumberOfVertexComponents;

        for (int i = 0; i < _myDrawLength; i++) {
            /* normals */
            if (_myNormals != null
                    && _myNormals.length != 0) {
                final Vector3f myNormal = new Vector3f(_myNormals[myNormalIndex],
                                                       _myNormals[myNormalIndex + 1],
                                                       _myNormals[myNormalIndex + 2]);
                myTransform.transform(myNormal);
                theParent.addNormal(myNormal);
                myNormalIndex += NUMBER_OF_NORMAL_COMPONENTS;
            }
            /* texcoords */
            if (_myTexCoords != null
                    && _myTexCoords.length != 0
                    && !material.disableTextureCoordinates) {
                if (_myNumberOfTexCoordComponents == 2) {
                    theParent.addTexCoord(new Vector2f(_myTexCoords[myTexCoordIndex],
                                                       _myTexCoords[myTexCoordIndex + 1]));
                } else if (_myNumberOfTexCoordComponents == 1) {
                    // (_myTexCoords[myTexCoordIndex]);
                } else if (_myNumberOfTexCoordComponents == 3) {
                    // (_myTexCoords[myTexCoordIndex],
                    //  _myTexCoords[myTexCoordIndex + 1],
                    //  _myTexCoords[myTexCoordIndex + 2]);
                }
                myTexCoordIndex += _myNumberOfTexCoordComponents;
            }
            /* color */
            if (_myColors != null
                    && _myColors.length != 0) {
                if (_myNumberOfColorComponents == 3) {
                    theParent.addColor(new Color(_myColors[myColorIndex],
                                                 _myColors[myColorIndex + 1],
                                                 _myColors[myColorIndex + 2]));
                } else if (_myNumberOfColorComponents == 4) {
                    theParent.addColor(new Color(_myColors[myColorIndex],
                                                 _myColors[myColorIndex + 1],
                                                 _myColors[myColorIndex + 2],
                                                 _myColors[myColorIndex + 3]));
                }
                myColorIndex += _myNumberOfColorComponents;
            }
            /* vertex */
            if (_myNumberOfVertexComponents == 3) {
                final Vector3f myVertex = new Vector3f(_myVertices[myVertexIndex],
                                                       _myVertices[myVertexIndex + 1],
                                                       _myVertices[myVertexIndex + 2]);
                myTransform.transform(myVertex);
                theParent.addVertex(myVertex);
            } else if (_myNumberOfVertexComponents == 2) {
                // (_myVertices[myVertexIndex],
                //  _myVertices[myVertexIndex + 1]);
            } else if (_myNumberOfVertexComponents == 4) {
                // (_myVertices[myVertexIndex],
                //  _myVertices[myVertexIndex + 1],
                //  _myVertices[myVertexIndex + 2],
                //  _myVertices[myVertexIndex + 3]);
            }
            myVertexIndex += _myNumberOfVertexComponents;
        }
    }
}
