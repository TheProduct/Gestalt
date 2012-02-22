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


import gestalt.context.DisplayCapabilities;
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


/**
 * this demo shows how to attach more than one shader object to one shader program.
 * read below for further notes.
 */

public class TestModularShader
    extends AnimatorRenderer {

    private Model _myMesh;

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    private Vector3f _myLightPosition = new Vector3f();

    public void setup() {
        /* shader setup */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        ShaderProgram.VERBOSE = true;
        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/simple.vsh"));

        /*
         * you can attach multiple shader objects to one shader program. in this example, we have one shader
         * that uses a function that is located in another shader.
         * there s two things to be aware of:
         * 1. only one of the shader objects must have to main() function
         * 2. the external function you want to call must be declared in the calling shader
         *
         * i.e.
         *
         * firstshader.fsh
         * ---------------
         * float externalFunction();
         *
         * void main(void)
         * {
         *    float f = externalFunction();
         * }
         *
         * secondshader.fsh
         * ----------------
         * float externalFunction(void)
         * {
         *    return 1.0;
         * }
         */
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/f01.fsh"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/f02.fsh"));

        /* crate the model */
        _myMesh = createModel();

        /* add material plugin to the material of the mesh */
        _myMesh.mesh().material().addPlugin(new ShaderMaterial());
        _myMesh.mesh().position().set(0, 0, -200);
        bin(BIN_3D).add(_myMesh);

        System.out.println("### INFO: move your mouse around to control the rotation of the shape");
    }


    public void loop(final float theDeltaTime) {
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        /* rotation of the models depends on the mouse position */
        _myMesh.mesh().rotation().add( (float) event().mouseX / displaycapabilities().width / 10f,
                                      (float) event().mouseY / displaycapabilities().height / 10f,
                                      0f);

        _myLightPosition.set(event().mouseX / 100f,
                             event().mouseY / 100f,
                             10);
    }


    private Model createModel() {
        ModelData myModelData = null;
        myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));

        /* get mesh */
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);
        Model myModel = drawablefactory().model(myModelData, myModelMesh);

        /*
         * make the model not transparent. as the vertices came in random order,
         * you can t apply any depthsorting anyways.
         */
        myModelMesh.material().transparent = false;

        return myModel;
    }


    private class ShaderMaterial
        implements MaterialPlugin {

        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myShaderManager.enable(_myShaderProgram);
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] args) {
        DisplayCapabilities dc = new DisplayCapabilities();
        dc.width = 640;
        dc.height = 480;
        dc.backgroundcolor.set(1f, 1f, 1f);
        new TestModularShader().init(dc);
    }
}
