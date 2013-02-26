

package gestalt.extension.framebufferobject;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.gpgpu.JoglGPGPUBase;
import gestalt.util.JoglUtil;

import data.Resource;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class TestJoglGPGPUBase
        extends JoglGPGPUBase {

    private final ShaderProgram _myShaderProgram;

    private ShaderManager _myShaderManager;

    public TestJoglGPGPUBase(final ShaderManager theShaderManager,
                             final int theWidth,
                             final int theHeight) {
        super(new JoglTexCreatorFBO_MultipleRGBA32Float(), theWidth, theHeight, 1);

        _myShaderManager = theShaderManager;
        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/gpgpu/GPGPU3DParticle.vs"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/gpgpu/GPGPU3DParticle.fs"));
    }

    protected void beginShader(GL gl, GLU glu) {
        final JoglFrameBufferObject READ_CURRENT_FBO = getFBObyOffset(READ_FBO);

        /* enable shader */
        _myShaderManager.enable(_myShaderProgram);

        /* set uniforms */
        _myShaderManager.setUniform(_myShaderProgram,
                                    "texture_unit_1",
                                    JoglUtil.getTextureUnitID(READ_CURRENT_FBO.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram,
                                    "texture_unit_2",
                                    JoglUtil.getTextureUnitID(READ_CURRENT_FBO.additional_texture(0).getTextureUnit()));
    }

    protected void endShader(GL gl, GLU glu) {
        _myShaderManager.disable();
    }
}
