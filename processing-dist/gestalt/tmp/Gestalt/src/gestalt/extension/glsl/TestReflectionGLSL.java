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


import gestalt.extension.materialplugin.JoglMaterialPluginCubeMap;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.material.MaterialPlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.util.CameraMover;

import mathematik.Vector3f;

import data.Resource;


/**
 * this demo shows how to use cuboid maps. cuboid mapping is a technique that takes
 * a three dimensional texture coordinate and returns a texel from a given cuboid
 * map.
 *
 * here we used it, to create reflections on a model
 *
 * thanks to humus.ca for the textures
 */

public class TestReflectionGLSL
    extends AnimatorRenderer {

    public static final boolean SERIALIZE_MODEL_DATA = false;

    public static final boolean LOAD_SERIALIZED_MODEL_DATA = false;

    private Model _myMesh;

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    private JoglMaterialPluginCubeMap _myCubeMap;

    private Vector3f _myLightPosition = new Vector3f();

    private float _myMixIntensity = 0.8f;

    private Vector3f _myBaseColor = new Vector3f(108f / 255f, 152f / 255f,
                                                 204f / 255f);

    public void setup() {
        /* shader setup */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource
                                            .getStream("demo/shader/env.vsh"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource
                                              .getStream("demo/shader/env.fsh"));

        /*
         * create the cuboid map material plugin you need to load six bitmaps. one
         * for each side of the cuboid
         */
        _myCubeMap = new JoglMaterialPluginCubeMap(true);
        _myCubeMap.load(Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negx.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posx.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negy.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posy.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negz.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posz.png")));

        /* crate the model */
        _myMesh = createModel();

        /* add material plugin to the material of the mesh */
        _myMesh.mesh().material().addPlugin(_myCubeMap);
        _myMesh.mesh().material().addPlugin(new ShaderMaterial());
        _myMesh.mesh().position().set(0, 0, -200);
        bin(BIN_3D).add(_myMesh);

        System.out
            .println("### INFO: move your mouse around to control the rotation of the shape");
    }


    public void loop(final float theDeltaTime) {
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        /* rotation of the models depends on the mouse position */
        _myMesh.mesh().rotation()
            .add(
                (float) event().mouseX / displaycapabilities().width
                / 10f,
                (float) event().mouseY / displaycapabilities().height
                / 10f, 0f);

        _myLightPosition.set(event().mouseX / 100f, event().mouseY / 100f, 10);

        if (event().keyPressed) {
            if (event().key == '+') {
                _myMixIntensity += 0.05f;
                if (_myMixIntensity > 1f) {
                    _myMixIntensity = 1f;
                }
            }
            if (event().key == '-') {
                _myMixIntensity -= 0.05f;
                if (_myMixIntensity < 0f) {
                    _myMixIntensity = 0f;
                }
            }
        }
    }


    private Model createModel() {
        ModelData myModelData = null;
        if (!LOAD_SERIALIZED_MODEL_DATA) {
            /* load model data from .obj file */
            myModelData = ModelLoaderOBJ.getModelData(Resource
                                                      .getStream("demo/common/weirdobject.obj"));

            /*
             * this method calculates the normals in a way, that each same
             * vertices have the same normal NOTE: the algorithm is not very
             * efficient. big models will take a while. seriously.
             */
            ModelData.VERBOSE = false;
            myModelData.averageNormals();

            /*
             * as parsing and averaging normals may take a while, you can once
             * do it, and then serialize the modeldata. reloading the serialized
             * modeldata the next startups is much faster
             */
            if (SERIALIZE_MODEL_DATA) {
                myModelData.serialize(Resource.getPath("demo/common/")
                                      + "/blob.ser");
            }
        } else {
            myModelData = ModelData.getSerializedModelData(Resource.getPath("demo/common/weirdobject.ser"));
        }

        /* get mesh */
        Mesh myModelMesh = drawablefactory().mesh(true, myModelData.vertices,
                                                  3, myModelData.vertexColors, 4, myModelData.texCoordinates, 2,
                                                  myModelData.normals, myModelData.primitive);
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

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myShaderProgram, "EnvMap", 0);
            _myShaderManager.setUniform(_myShaderProgram, "LightPos",
                                        _myLightPosition);
            _myShaderManager.setUniform(_myShaderProgram, "BaseColor",
                                        _myBaseColor);
            _myShaderManager.setUniform(_myShaderProgram, "MixRatio",
                                        _myMixIntensity);
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
        new TestReflectionGLSL().init(dc);
    }
}
