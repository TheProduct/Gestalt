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
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.util.scenewriter.SceneWriter;

import mathematik.Random;


public class UsingSceneWriterOBJ
    extends AnimatorRenderer {

    private int _myID;

    private Material _myMaterial;

    public void setup() {
        _myMaterial = drawablefactory().material();
    }


    public Mesh createMesh() {
        float[] myVertices = new float[4 * 3];
        float[] myColors = new float[4 * 3];
        float[] myTexCoords = new float[4 * 2];
        float[] myNormals = null;

        myVertices[0] = -100;
        myVertices[1] = -100;
        myVertices[2] = 0;

        myVertices[3] = 100;
        myVertices[4] = -100;
        myVertices[5] = 0;

        myVertices[6] = 100;
        myVertices[7] = 100;
        myVertices[8] = 0;

        myVertices[9] = -100;
        myVertices[10] = 100;
        myVertices[11] = 0;

        myColors[0] = 0;
        myColors[1] = 0;
        myColors[2] = 0;

        myColors[3] = 0;
        myColors[4] = 0;
        myColors[5] = 0;

        myColors[6] = 1;
        myColors[7] = 1;
        myColors[8] = 1;

        myColors[9] = 1;
        myColors[10] = 1;
        myColors[11] = 1;

        myTexCoords[0] = 0;
        myTexCoords[1] = 0;

        myTexCoords[2] = 0.5f;
        myTexCoords[3] = 0;

        myTexCoords[4] = 0.5f;
        myTexCoords[5] = 0.5f;

        myTexCoords[6] = 0;
        myTexCoords[7] = 0.5f;

        Mesh myMesh = drawablefactory().mesh(false,
                                             myVertices, 3,
                                             myColors, 3,
                                             myTexCoords, 2,
                                             myNormals,
                                             MESH_QUADS);
        /* add to renderer */
        bin(BIN_3D).add(myMesh);

        return myMesh;
    }


    public void loop(float theDeltaTime) {
        if (event().keyPressed) {
            if (event().key == 's') {
                _myID++;
                new SceneWriter("../thisisatest" + _myID + ".obj", bin(BIN_3D));
            }
            if (event().key == 'm') {
                _myMaterial = drawablefactory().material();
            }
        }

        if (event().mouseClicked) {
            Mesh myMesh = createMesh();
            myMesh.setMaterialRef(_myMaterial);

            Random myRandom = new Random();
            myMesh.scale().set(myRandom.getFloat(0.5f, 1),
                               myRandom.getFloat(0.5f, 1),
                               myRandom.getFloat(0.5f, 1));

            /* rotate mesh */
            myMesh.rotation().x = PI * event().mouseX / (float) displaycapabilities().width;
            myMesh.rotation().z = PI * event().mouseY / (float) displaycapabilities().height;
        }
    }


    public static void main(String[] args) {
        new UsingSceneWriterOBJ().init();
    }
}
