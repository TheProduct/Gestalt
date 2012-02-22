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


import javax.media.opengl.GL;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.materialplugin.JoglMaterialPluginCubeMap;
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

import data.Resource;


/**
 * this demo show how to integrate glsl ( opengl shader language ) into gestalt.
 * note that you need a graphic card that supports glsl.
 * on OS X glsl is only available in 10.4+ and with hardware like
 * for example the Nvidia FX5900 in 12" powerbooks, or the Nvidia FX6800 in G5s.
 */


public class UsingGLSLReflections
    extends AnimatorRenderer {

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    public void setup() {
        /* setup camera */
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* create shadermanager and a shaderprogram */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/texreflection.vsh"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/texreflection.fsh"));

        /* create a base texture */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getPath("demo/common/vignet.png")));

        /*
         * create the cuboid map material plugin
         * you need to load six bitmaps. one for each side of the cuboid
         */
        JoglMaterialPluginCubeMap myCubeMap = new JoglMaterialPluginCubeMap(true);
        myCubeMap.load(Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negx.png")),
                       Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posx.png")),
                       Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negy.png")),
                       Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posy.png")),
                       Bitmaps.getBitmap(Resource.getStream("demo/common/cube_negz.png")),
                       Bitmaps.getBitmap(Resource.getStream("demo/common/cube_posz.png")));
        myCubeMap.setTextureUnit(GL.GL_TEXTURE1);

        /* load model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));
        myModelData.averageNormals();
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);

        Model myModel = drawablefactory().model(myModelData, myModelMesh);
        myModel.mesh().material().lit = true;
        myModel.mesh().material().addPlugin(myCubeMap);
//        myModel.mesh().material().addPlugin(myTexture);
        myModel.mesh().material().addPlugin(new ShaderMaterial());

        bin(BIN_3D).add(myModel);
    }


    public void loop(final float theDeltaTime) {
        light().position().set(camera().position());
        light().position().z = event().mouseY;
    }


    private class ShaderMaterial
        implements MaterialPlugin {

        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myShaderManager.enable(_myShaderProgram);

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myShaderProgram, "ReflectionTexture", 1);
            _myShaderManager.setUniform(_myShaderProgram, "FirstTexture", 0);
            _myShaderManager.setUniform(_myShaderProgram, "SkyColor", new float[] {1f, 1f, 1f});
            _myShaderManager.setUniform(_myShaderProgram, "GroundColor", new float[] {1f, 1f, 1f});
            _myShaderManager.setUniform(_myShaderProgram, "LightPosition", light().position());
            _myShaderManager.setUniform(_myShaderProgram, "LightRatio", 1f);
            _myShaderManager.setUniform(_myShaderProgram, "ReflectionMixIntensity", event().mouseX / 640f);
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] args) {
        new UsingGLSLReflections().init();
    }
}
