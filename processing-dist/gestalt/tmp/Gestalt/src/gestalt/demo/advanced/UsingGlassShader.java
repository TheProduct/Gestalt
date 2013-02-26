package gestalt.demo.advanced;


import javax.media.opengl.GL;

import gestalt.G;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Material;
import gestalt.shape.Plane;
import gestalt.material.MaterialPlugin;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


public class UsingGlassShader
    extends AnimatorRenderer {

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    public void setup() {
        /* setup camera */
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* create shadermanager and a shaderprogram */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/RefractionReflectionShader.vs"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/RefractionReflectionShader.fs"));
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        /* create textures */
        TexturePlugin myRefractionTexture = drawablefactory().texture();
        myRefractionTexture.load(Bitmaps.getBitmap(Resource.getPath("demo/common/cube.png")));
        myRefractionTexture.setTextureUnit(GL.GL_TEXTURE1);

        TexturePlugin myReflectionTexture = drawablefactory().texture();
        myReflectionTexture.load(Bitmaps.getBitmap(Resource.getPath("demo/common/sky-reflection.png")));
        myReflectionTexture.setTextureUnit(GL.GL_TEXTURE0);

        /* create model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));
        myModelData.averageNormals();
        Model myModel = G.model(myModelData);
        myModel.mesh().material().lit = true;
        myModel.mesh().material().addPlugin(myRefractionTexture);
        myModel.mesh().material().addPlugin(myReflectionTexture);
        myModel.mesh().material().addPlugin(new ShaderMaterial());

        /* create background */
        Plane myPlane = G.plane(bin(BIN_2D_BACKGROUND), Resource.getPath("demo/common/cube.png"));
        myPlane.setPlaneSizeToTextureSize();
        myPlane.rotation().set(PI, 0, 0);
        myPlane.material().depthmask = false;

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
            _myShaderManager.setUniform(_myShaderProgram, "LightPos", 0.0f, 0.0f, 4.0f);
            _myShaderManager.setUniform(_myShaderProgram, "BaseColor", 1.0f, 1.0f, 1.0f);
            _myShaderManager.setUniform(_myShaderProgram, "EnvMap", 0);
            _myShaderManager.setUniform(_myShaderProgram, "RefractionMap", 1);
            _myShaderManager.setUniform(_myShaderProgram, "textureWidth", 512.0f);
            _myShaderManager.setUniform(_myShaderProgram, "textureHeight", 512.0f);

            _myShaderManager.setUniform(_myShaderProgram, "Depth", event().normalized_mouseX);
            _myShaderManager.setUniform(_myShaderProgram, "MixRatio", event().normalized_mouseY);
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 512;
        myDisplayCapabilities.height = 512;
        myDisplayCapabilities.backgroundcolor.set(1);
        G.init(UsingGlassShader.class, myDisplayCapabilities);
    }
}
