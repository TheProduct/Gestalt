package gestalt.candidates.ssao;


import java.io.InputStream;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;

import mathematik.Util;


public class DepthBufferMaterial
    implements MaterialPlugin {

    private final ShaderManager _myMaterialShaderManager;

    private final ShaderProgram _myMaterialShaderProgram;

    private float[] myRandomVectors;

    public float far;

    public DepthBufferMaterial(final ShaderManager theMaterialShaderManager,
                               final ShaderProgram theMaterialShaderProgram,
                               final InputStream theVertexShaderCode,
                               final InputStream theFragmentShaderCode) {
        _myMaterialShaderManager = theMaterialShaderManager;
        _myMaterialShaderProgram = theMaterialShaderProgram;

        _myMaterialShaderManager.attachVertexShader(_myMaterialShaderProgram, theVertexShaderCode);
        _myMaterialShaderManager.attachFragmentShader(_myMaterialShaderProgram, theFragmentShaderCode);

        myRandomVectors = new float[12 * 3];
        for (int i = 0; i < myRandomVectors.length; i++) {
            myRandomVectors[i] = Util.random( -0.01f, 0.01f);
        }
    }


    public void begin(GLContext theRenderContext, Material theParent) {
        /* enable shader */
        _myMaterialShaderManager.enable(_myMaterialShaderProgram);

        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "far", far);
    }


    public void end(final GLContext theRenderContext,
                    final Material theParent) {
        _myMaterialShaderManager.disable();
    }
}