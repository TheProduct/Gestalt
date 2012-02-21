

package gestalt.extension.glsl;


import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.util.JoglUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import mathematik.Vector3f;


public class ShaderMaterial
        implements MaterialPlugin {

    private final ShaderManager _myShaderManager;

    private final ShaderProgram _myShaderProgram;

    private final HashMap<String, Float> _myFloatUniforms;

    public ShaderMaterial(ShaderManager theShaderManager, ShaderProgram theShaderProgram) {
        _myShaderManager = theShaderManager;
        _myShaderProgram = theShaderProgram;
        _myFloatUniforms = new HashMap<String, Float>();
    }

    public void begin(final GLContext theRenderContext, final Material theParent) {
        /* enable shader */
        _myShaderManager.enable(_myShaderProgram);

        beginTextures(theRenderContext, theParent);

        /* set uniform variables in shader */
        setUniforms();

        /* handle float uniforms */
        final Iterator<Entry<String, Float>> it = _myFloatUniforms.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, Float> entry = it.next();
            String myName = entry.getKey();
            float myValue = entry.getValue();
            setUniform(myName, myValue);
        }
    }

    public void beginTextures(final GLContext theRenderContext, final Material theParent) {
    }

    public void endTextures(final GLContext theRenderContext, final Material theParent) {
    }

    public void registerUniform(String theName, float theValue) {
        _myFloatUniforms.put(theName, theValue);
    }

    public void removeUniform(String theName) {
        _myFloatUniforms.remove(theName);
    }

    public void setUniforms() {
        /* for example */
//        setUniform("x", 10);
//        setUniform("direction", new Vector3f(0, 1, 0));
    }

    public void setUniform(final String theName, final int theValue) {
        _myShaderManager.setUniform(_myShaderProgram, theName, theValue);
    }

    public void setUniform(final String theName, final float theValue) {
        _myShaderManager.setUniform(_myShaderProgram, theName, theValue);
    }

    public void setUniform(final String theName, final boolean theValue) {
        _myShaderManager.setUniform(_myShaderProgram, theName, theValue);
    }

    public void setUniform(final String theName, final Vector3f theValue) {
        _myShaderManager.setUniform(_myShaderProgram, theName, theValue);
    }

    public void setUniform(final String theName, final TexturePlugin theValue) {
        _myShaderManager.setUniform(_myShaderProgram, theName, JoglUtil.getTextureUnitID(theValue.getTextureUnit()));
    }

    public void end(final GLContext theRenderContext, final Material theParent) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        endTextures(theRenderContext, theParent);
        _myShaderManager.disable();
    }
}
