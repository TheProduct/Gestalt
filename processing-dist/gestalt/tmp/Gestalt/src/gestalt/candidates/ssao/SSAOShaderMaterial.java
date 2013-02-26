package gestalt.candidates.ssao;


import java.io.InputStream;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;


public class SSAOShaderMaterial
    implements MaterialPlugin {

    private final ShaderManager _myMaterialShaderManager;

    private final ShaderProgram _myMaterialShaderProgram;

    private float[] myRandomVectors;

    public float sampleRadius;

    public float distanceScale;

    public float maxSampleDelta;

    public SSAOShaderMaterial(final ShaderManager theMaterialShaderManager,
                              final ShaderProgram theMaterialShaderProgram,
                              final InputStream theVertexShaderCode,
                              final InputStream theFragmentShaderCode) {
        _myMaterialShaderManager = theMaterialShaderManager;
        _myMaterialShaderProgram = theMaterialShaderProgram;

        _myMaterialShaderManager.attachVertexShader(_myMaterialShaderProgram, theVertexShaderCode);
        _myMaterialShaderManager.attachFragmentShader(_myMaterialShaderProgram, theFragmentShaderCode);

        myRandomVectors = new float[64 * 3];
        for (int i = 0; i < myRandomVectors.length; i++) {
            myRandomVectors[i] = ( (float) Math.random() - 0.5f) * 2f;
        }
    }


    public void begin(GLContext theRenderContext, Material theParent) {
        /* enable shader */
        _myMaterialShaderManager.enable(_myMaterialShaderProgram);

        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "depthUnit", 0);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "randomMapUnit", 1);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "sampleRadius", sampleRadius);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "distanceScale", distanceScale);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "maxSampleDelta", maxSampleDelta);
        //_myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "texture1", 1);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "random", myRandomVectors);
        //_myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "camerarange", new float[]{200, 600});
        //_myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "screensize", new float[]{512, 512});
    }


    public void end(final GLContext theRenderContext,
                    final Material theParent) {
        _myMaterialShaderManager.disable();
    }
}