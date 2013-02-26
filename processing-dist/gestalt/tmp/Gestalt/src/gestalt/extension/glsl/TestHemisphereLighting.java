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


package gestalt.extension.glsl;


import gestalt.context.GLContext;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.material.MaterialPlugin;
import gestalt.util.CameraMover;

import mathematik.Vector3f;

import data.Resource;


public class TestHemisphereLighting
    extends AnimatorRenderer {

    private Model _myObject;

    private Model _myObject2;

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    private Vector3f _myLightPosition = new Vector3f();

    public void setup() {
        light().ambient.set(0.6f);

        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/hemispherelight.vsh"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/hemispherelight.fsh"));

        _myObject = createModel();
        _myObject.mesh().material().addPlugin( (new ShaderMaterial()));
        _myObject.mesh().position().set(130, 0, -100);
        bin(BIN_3D).add(_myObject);

        _myObject2 = createModel();
        _myObject2.setActive(false);
        _myObject2.mesh().material().lit = true;
        _myObject2.mesh().position().set(130, 0, -100);
        bin(BIN_3D).add(_myObject2);
    }


    public void loop(final float theDeltaTime) {
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
        _myLightPosition.set(event().mouseX * 5,
                             event().mouseY * 5,
                             10);
        light().position().set(_myLightPosition);

        if (event().keyPressed) {
            if (event().key == 'L') {
                light().enable = true;
                _myObject2.setActive(true);
                _myObject.setActive(false);
            }
            if (event().key == 'l') {
                light().enable = false;
                _myObject2.setActive(false);
                _myObject.setActive(true);
            }
        }
    }


    private Model createModel() {
        Model myModel;
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));
        myModelData.averageNormals();
        Mesh myModelMesh = drawablefactory().mesh(false,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);
        myModel = drawablefactory().model(myModelData, myModelMesh);
        myModelMesh.material().transparent = false;

        return myModel;
    }


    private class ShaderMaterial
        implements MaterialPlugin {

        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myShaderManager.enable(_myShaderProgram);

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myShaderProgram, "LightPosition", _myLightPosition);
            _myShaderManager.setUniform(_myShaderProgram, "SkyColor", new Vector3f(0f, 1f, 0f));
            _myShaderManager.setUniform(_myShaderProgram, "GroundColor", new Vector3f(0f, 0f, 1f));
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] args) {
        new TestHemisphereLighting().init();
    }
}
