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


import gestalt.extension.quadline.QuadLine;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.shape.Mesh;

import mathematik.Random;
import mathematik.Vector3f;


public class TestQuadLineTranslator
    extends AnimatorRenderer {

    private Material _myMaterial;

    private boolean _myCreatedVBO = false;

    private Mesh _myVBO;

    public void setup() {

        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        fpscounter(true);

        /* material */
        _myMaterial = drawablefactory().material();
        _myMaterial.transparent = true;
        _myMaterial.depthtest = false;
        _myMaterial.depthmask = false;
        _myMaterial.blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myMaterial.disableTextureCoordinates = true;

        /* create line */
        createBezierLines();
    }


    public void loop(float theDeltaTime) {}


    public void mousePressed(int x, int y, int thePressedMouseButton) {
        if (!_myCreatedVBO) {
            System.out.println("### INFO / creating VBO.");
            _myCreatedVBO = true;

            final MeshCreator myMeshCreator = new MeshCreator();
            myMeshCreator.createVBO(true);
            myMeshCreator.setPrimitveType(MESH_QUADS);

            final QuadLineTranslator myQuadLineTranslator = new QuadLineTranslator();
            myQuadLineTranslator.setPrimitveType(MESH_QUADS);
            myMeshCreator.translators().add(myQuadLineTranslator);

            _myVBO = myMeshCreator.parse(bin(BIN_3D), drawablefactory());
            _myVBO.setMaterialRef(_myMaterial);

            bin(BIN_3D).clear();
            bin(BIN_3D).add(_myVBO);
        } else {
            System.out.println("### INFO / removing VBO.");
            _myCreatedVBO = false;
            bin(BIN_3D).clear();
            dispose(_myVBO);
            _myVBO = null;
            createBezierLines();
        }
    }


    private void createBezierLines() {
        for (int i = 0; i < 3; i++) {
            final QuadLine _myQuadLine = drawablefactory().extensions().quadline();
            _myQuadLine.points = new Vector3f[] {
                                 getRandomVector(0),
                                 getRandomVector(50),
                                 getRandomVector(100),
                                 getRandomVector(150)};
            _myQuadLine.colors = new Color[] {
                                 new Color(1, 0),
                                 new Color(1, 0.33f),
                                 new Color(1, 0.6f),
                                 new Color(1, 1)};
            _myQuadLine.linewidth = 20;
            _myQuadLine.update();
            _myQuadLine.setMaterialRef(_myMaterial);
            bin(BIN_3D).add(_myQuadLine);
        }
    }


    private Vector3f getRandomVector(float theValue) {
        Random myRandom = new Random();
        return new Vector3f(myRandom.getFloat(0, theValue),
                            myRandom.getFloat(0, theValue),
                            myRandom.getFloat(0, theValue));
    }


    public static void main(String[] args) {
        new TestQuadLineTranslator().init();
    }
}
