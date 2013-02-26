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


import javax.media.opengl.GL;

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
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import mathematik.Vector3f;

import data.Resource;


public class TestTextureAndReflection
    extends AnimatorRenderer {

    private float _myMixIntensity = 0.03f;

    private float _myLightRatio = 0.30f;

    public void setup() {
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* glsl shader */
        ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(myShaderManager);

        ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
        myShaderManager.attachVertexShader(myShaderProgram, Resource.getStream("demo/shader/texreflection.vsh"));
        myShaderManager.attachFragmentShader(myShaderProgram, Resource.getStream("demo/shader/texreflection.fsh"));

        ShaderMaterial _myShaderMaterial = new ShaderMaterial(myShaderManager, myShaderProgram);

        /* model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/occlusioncube.obj"));
        myModelData.averageNormals();
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);
        Model myModel = drawablefactory().model(myModelData, myModelMesh);

        /* create a texture */
        TexturePlugin myTexture0 = drawablefactory().texture();
        myTexture0.load(Bitmaps.getBitmap(Resource.getStream("demo/common/occlusion.png")));
        myTexture0.setTextureUnit(GL.GL_TEXTURE0);

        /*
         * create the cuboid map material plugin
         * you need to load six bitmaps. one for each side of the cuboid
         */
        JoglMaterialPluginCubeMap _myCubeMap = new JoglMaterialPluginCubeMap(true);
        _myCubeMap.load(Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negx.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posx.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negy.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posy.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negz.png")),
                        Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posz.png")));
        _myCubeMap.setTextureUnit(GL.GL_TEXTURE1);

        /* set the texture in the material of the shape */
        myModel.mesh().material().addPlugin(myTexture0);
        myModel.mesh().material().addPlugin(_myCubeMap);
        myModel.mesh().material().addPlugin(_myShaderMaterial);

        /* add the model to the renderer */
        bin(BIN_3D).add(myModel);
    }


    public void loop(final float theDeltaTime) {
        camera().side(theDeltaTime * 100);
        light().position().set(event().mouseX, event().mouseY, -100);

        if (event().keyPressed) {
            if (event().key == '+') {
                _myMixIntensity += 0.01f;
            }
            if (event().key == '-') {
                _myMixIntensity -= 0.01f;
            }
            _myMixIntensity = Math.min(1f, Math.max(0, _myMixIntensity));

            if (event().key == '*') {
                _myLightRatio += 0.05f;
            }
            if (event().key == '_') {
                _myLightRatio -= 0.05f;
            }
            _myLightRatio = Math.min(1f, Math.max(0, _myLightRatio));
        }
    }


    public class ShaderMaterial
        implements MaterialPlugin {

        private ShaderManager _myShaderManager;

        private ShaderProgram _myShaderProgram;

        public ShaderMaterial(ShaderManager theShaderManager,
                              ShaderProgram theShaderProgram) {
            _myShaderManager = theShaderManager;
            _myShaderProgram = theShaderProgram;
        }


        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myShaderManager.enable(_myShaderProgram);

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myShaderProgram, "LightPosition", light().position());
            _myShaderManager.setUniform(_myShaderProgram, "FirstTexture", 0);
            _myShaderManager.setUniform(_myShaderProgram, "ReflectionTexture", 1);
            _myShaderManager.setUniform(_myShaderProgram, "ReflectionMixIntensity", _myMixIntensity);
            _myShaderManager.setUniform(_myShaderProgram, "LightRatio", _myLightRatio);
            _myShaderManager.setUniform(_myShaderProgram, "SkyColor", new Vector3f(1f, 1f, 1f));
            _myShaderManager.setUniform(_myShaderProgram, "GroundColor", new Vector3f(0f, 0f, 0f));
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] arg) {
        DisplayCapabilities dc = new DisplayCapabilities();
        dc.width = 640;
        dc.height = 480;
        dc.backgroundcolor.set(0f, 0f, 0f);
        dc.antialiasinglevel = 4;
        new TestTextureAndReflection().init(dc);
    }
}
