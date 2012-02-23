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


package gestalt.demo.advanced;

import gestalt.render.AnimatorRenderer;
import gestalt.shape.MeshVBO;
import gestalt.shape.MeshVBO.VBOModifier;
import gestalt.shape.Mesh;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import mathematik.Vector2f;

import data.Resource;

import java.nio.FloatBuffer;


/**
 *
 * this demo shows how to use a mesh. a mesh is bascially a big chunk of vertex data.
 * <br/>
 * this demo also shows how incredibly fast the new vertex buffer objects (VBO) are.
 * of course there is a catch to VBOs. it becomes rather difficult to modify the
 * VBO vertex data once it is loaded into the graphics cards memory. it is possible though,
 * to change it afterwards. two ways are illiustrated below.
 *
 */

public class UsingMesh
    extends AnimatorRenderer {

    private static final int NUMBER_OF_VERTICES = 200 * 200 * 4;

    private static final boolean USE_VBO = false;

    private static final boolean USE_VBO_MODIFIER = false;

    private static final float SPIKE_OFFSET = 50;

    private static final int[] VERTEX_X_ORDER = new int[] {0, 1, 1, 0};

    private static final int[] VERTEX_Y_ORDER = new int[] {0, 0, 1, 1};

    private Mesh _myMesh;

    private int myVertexCounter = 3 * NUMBER_OF_VERTICES / 2 + 2;

    public void setup() {
        fpscounter(true);
        /* data to be uploaded */
        float[] myVertices = new float[NUMBER_OF_VERTICES * 3];
        float[] myColors = new float[NUMBER_OF_VERTICES * 3];
        float[] myTexCoords = new float[NUMBER_OF_VERTICES * 2];
        float[] myNormals = new float[NUMBER_OF_VERTICES * 3];

        /* assign data to every single vertex */
        final int myEdgeSize = (int) Math.sqrt(NUMBER_OF_VERTICES / 4);
        final Vector2f myScale = new Vector2f(3.2f, 2.4f);
        for (int i = 0; i < NUMBER_OF_VERTICES / 4; i++) {
            final int x = i % myEdgeSize;
            final int y = i / myEdgeSize;
            for (int j = 0; j < 4; j++) {
                final int myIndex = (i * 4 + j);
                /* vertex position */
                myVertices[myIndex * 3 + 0] =
                    x * myScale.x + VERTEX_X_ORDER[j] * myScale.x - displaycapabilities().width / 2;
                myVertices[myIndex * 3 + 1] =
                    y * myScale.y + VERTEX_Y_ORDER[j] * myScale.y - displaycapabilities().height / 2;
                myVertices[myIndex * 3 + 2] = 0;
                /* vertex color4f */
                myColors[myIndex * 3 + 0] = 1;
                myColors[myIndex * 3 + 1] = 1;
                myColors[myIndex * 3 + 2] = 1;
                /* vertex texture coordinates */
                myTexCoords[myIndex * 2 + 0] =
                    (x * myScale.x + VERTEX_X_ORDER[j] * myScale.x) / (myEdgeSize * myScale.x);
                myTexCoords[myIndex * 2 + 1] =
                    (y * myScale.y + VERTEX_Y_ORDER[j] * myScale.y) / (myEdgeSize * myScale.y);
                /* vertex normal */
                myNormals[myIndex * 3 + 0] = 0;
                myNormals[myIndex * 3 + 1] = 0;
                myNormals[myIndex * 3 + 2] = 1;
            }
        }

        /* create mesh */
        _myMesh = drawablefactory().mesh(USE_VBO,
                                         myVertices, 3,
                                         myColors, 3,
                                         myTexCoords, 2,
                                         myNormals,
                                         MESH_QUADS);

        /* add modifier */
        if (_myMesh instanceof MeshVBO && USE_VBO_MODIFIER) {
            ( (MeshVBO) _myMesh).addModifier(new MyVBOModifier());
        }

        /* create texture */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png"), "auto"));
        _myMesh.material().addPlugin(myTexture);

        /* add to renderer */
        bin(BIN_3D).add(_myMesh);

        /* set framerate */
        framerate(UNDEFINED);
    }


    public void loop(float theDeltaTime) {

        /* rotate mesh */
        _myMesh.rotation().x = PI * event().mouseX / (float) displaycapabilities().width;
        _myMesh.rotation().z = PI * event().mouseY / (float) displaycapabilities().height;

        if (!event().mouseDown) {
            /*
             * change mesh data at runtime. there is a regular way of modifying data
             * and for VBOs there is also a crazy more advanced way. the regular way
             * is very straight forward for regular mesh for VBO it has the
             * disadvantage of having to update the data which means upload the
             * complete meshdata into graphics card memory. the more crazy way
             * registers an object at the VBO mesh which is called when the VBO is
             * ready to modifiy specific data.
             */
            if (_myMesh instanceof MeshVBO) {
                if (!USE_VBO_MODIFIER) {
                    /* modify vertex data */
                    for (int i = 0; i < 200; i++) {
                        myVertexCounter += 3;
                        myVertexCounter %= _myMesh.vertices().length;
                        float myValue = _myMesh.vertices()[myVertexCounter];
                        myValue += new mathematik.Random().getFloat( -SPIKE_OFFSET, SPIKE_OFFSET);
                        /* here we modify the actually data */
                        _myMesh.vertices()[myVertexCounter] = myValue;
                    }
                    /*
                     * note that updateData() only needs to be called on VBOs because
                     * their data resides on the graphics card.
                     */
                    _myMesh.updateData();
                }
            } else {
                /* modify vertex data */
                for (int i = 0; i < 200; i++) {
                    myVertexCounter += 3;
                    myVertexCounter %= _myMesh.vertices().length;
                    float myValue = _myMesh.vertices()[myVertexCounter];
                    myValue += new mathematik.Random().getFloat( -SPIKE_OFFSET, SPIKE_OFFSET);
                    /* here we modify the actually data */
                    _myMesh.vertices()[myVertexCounter] = myValue;
                }
            }
        }

        /* change mesh properties */
        if (event().keyPressed) {
            /* switch primitves */
            if (event().key == '1') {
                _myMesh.setPrimitive(MESH_QUADS);
            }
            if (event().key == '2') {
                _myMesh.setPrimitive(MESH_LINES);
            }
            if (event().key == '3') {
                _myMesh.setPrimitive(MESH_POINTS);
            }
            if (event().key == '5') {
                /* show first half of all vertices */
                _myMesh.drawstart(0);
                _myMesh.drawlength(NUMBER_OF_VERTICES / 2);
            }
            if (event().key == '6') {
                /* show second half of all vertices */
                _myMesh.drawstart(NUMBER_OF_VERTICES / 2);
                _myMesh.drawlength(NUMBER_OF_VERTICES / 2);
            }
            if (event().key == '7') {
                /* show all vertices */
                _myMesh.drawstart(0);
                _myMesh.drawlength(NUMBER_OF_VERTICES);
            }
        }
    }


    private class MyVBOModifier
        implements VBOModifier {

        public void modifyVertexData(FloatBuffer theVertexData) {
            for (int i = 0; i < 20; i++) {
                /* modify vertex data */
                myVertexCounter += 3;
                myVertexCounter %= _myMesh.vertices().length;
                float myValue = theVertexData.get(myVertexCounter);
                myValue += new mathematik.Random().getFloat( -SPIKE_OFFSET, SPIKE_OFFSET);
                /* here we modify the actually data */
                theVertexData.put(myVertexCounter, myValue);
            }
        }
    }


    public static void main(String[] args) {
        new UsingMesh().init();
    }
}
