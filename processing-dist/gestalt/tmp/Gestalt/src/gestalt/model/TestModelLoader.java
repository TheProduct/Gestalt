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


package gestalt.model;


import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.util.CameraMover;

import data.Resource;


public class TestModelLoader
    extends AnimatorRenderer {

    private Model[] _myModels;

    public void setup() {
        light().enable = true;
        light().position().set(100, 200, 100);
        createDiscreteModel();
    }


    public void loop(final float theDeltaTime) {
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        Model myModel = getModelByName(_myModels, "Sphere");
        myModel.mesh().position().set(0, event().mouseY, 0);
        myModel.mesh().rotation().add(0.01f, 0.01f, 0.01f);
    }


    private void createDiscreteModel() {
        ModelLoaderOBJ.VERBOSE = false;
        ModelLoaderOBJ.PRIMITIVE = MESH_TRIANGLES;
        ModelData[] myModelData = ModelLoaderOBJ.getModelDataAsDiscreteModels(Resource.getStream(
            "demo/common/two_models.obj"));
        _myModels = new Model[myModelData.length];
        for (int i = 0; i < myModelData.length; i++) {
            /* get mesh */
            Mesh myModelMesh = drawablefactory().mesh(true,
                                                      myModelData[i].vertices, 3,
                                                      myModelData[i].vertexColors, 4,
                                                      myModelData[i].texCoordinates, 2,
                                                      myModelData[i].normals,
                                                      myModelData[i].primitive);
            Model myModel = drawablefactory().model(myModelData[i], myModelMesh);
            myModelMesh.material().transparent = false;
            myModelMesh.material().lit = true;
            bin(BIN_3D).add(myModel);

            _myModels[i] = myModel;
        }
    }


    public void createOneModel() {
        ModelLoaderOBJ.VERBOSE = false;
        ModelLoaderOBJ.PRIMITIVE = MESH_TRIANGLES;
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/two_models.obj"));
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);
        Model myModel = drawablefactory().model(myModelData, myModelMesh);
        myModelMesh.material().transparent = false;
        bin(BIN_3D).add(myModel);
    }


    private Model getModelByName(Model[] theModels, String theName) {
        for (int i = 0; i < theModels.length; i++) {
            if (theModels[i].name().equals(theName)) {
                return theModels[i];
            }
        }
        return null;
    }


    public static void main(String[] args) {
        DisplayCapabilities dc = new DisplayCapabilities();
        dc.width = 640;
        dc.height = 480;
        dc.backgroundcolor.set(0.1f, 0.1f, 0.1f);
        new TestModelLoader().init(dc);
    }
}
