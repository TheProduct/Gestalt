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


import gestalt.G;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.material.Material;
import gestalt.shape.Mesh;

import mathematik.Random;

import data.Resource;
import gestalt.render.SketchRenderer;


public class TestMeshTranslator
        extends SketchRenderer {

    private Material _myMaterial;

    private boolean _myCreatedVBO = false;

    private Mesh _myVBO;

    private ModelData _myModelData;

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

        _myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/person.obj"));

        /* create line */
        createBezierLines();
    }

    public void loop(float theDeltaTime) {
    }

    public void mousePressed(int x, int y, int thePressedMouseButton) {
        if (!_myCreatedVBO) {
            System.out.println("### INFO / creating VBO.");
            _myCreatedVBO = true;

            final MeshCreator myMeshCreator = new MeshCreator();
            myMeshCreator.createVBO(true);
            myMeshCreator.setPrimitveType(MESH_TRIANGLES);
            myMeshCreator.translators().add(new MeshTranslator());

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
        final Random r = new Random();
        for (int i = 0; i < 10; i++) {
            final Model myModel = G.model(_myModelData);
            final Mesh myMesh = myModel.mesh();
            myMesh.position().set(r.getFloat(-100, 100),
                                  r.getFloat(-100, 100),
                                  r.getFloat(-100, 100));
            final float myScale = r.getFloat(0.5f, 1.0f);
            myMesh.scale().set(myScale, myScale, myScale);
            myMesh.rotation().x = r.getFloat(-PI, PI);

            G.remove(myModel);
            bin(BIN_3D).add(myMesh);
        }
    }

    public static void main(String[] args) {
        G.init(TestMeshTranslator.class);
    }
}
