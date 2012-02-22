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


package gestalt.extension.shadow;


import javax.media.opengl.GL;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.shape.AbstractShape;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.util.JoglUtil;


public class JoglGLSLShadowMaterial
    implements MaterialPlugin {

    protected final AbstractShape _myParent;

    protected final TexturePlugin _myProjectionTexture;

    protected final JoglGLSLShadowMap _myShadowMapExtension;

    protected final ShaderManager _myShaderManager;

    protected final ShaderProgram _myShaderProgram;

    private final int _myShadowTextureUnit = GL.GL_TEXTURE1;

    public JoglGLSLShadowMaterial(ShaderManager theShaderManager,
                                  ShaderProgram theShaderProgram,
                                  JoglGLSLShadowMap theJoglGLSLShadowMap,
                                  AbstractShape theParent) {
        _myParent = theParent;
        _myProjectionTexture = theJoglGLSLShadowMap.fbo();
        _myShadowMapExtension = theJoglGLSLShadowMap;
        _myShaderManager = theShaderManager;
        _myShaderProgram = theShaderProgram;
    }


    public void begin(GLContext theRenderContext, Material theParent) {
        /* enable shader */
        _myShaderManager.enable(_myShaderProgram);

        setUniforms();

        /* tex gen matrix */
        final GL gl = (  theRenderContext).gl;

        gl.glBindTexture(_myShadowMapExtension.getTextureTarget(), _myShadowMapExtension.getTextureID());

        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        gl.glActiveTexture(_myShadowTextureUnit);
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();

        /* T = bias * Pl * Vl * M */

        /* bias */
        gl.glTranslatef(0.5f, 0.5f, 0.5f);
        gl.glScalef(0.5f, 0.5f, 0.5f);

        /* Pl ( light projection matrix ) */
        gl.glMultMatrixf(_myShadowMapExtension.lightcamera.projectionmatrix(), 0);

        /* Vl ( light view matrix ) */
        gl.glMultMatrixf(_myShadowMapExtension.lightcamera.modelviewmatrix(), 0);

        /* M ( model matrix ) */
        applyModelMatrix(gl);

        gl.glPopAttrib();
    }


    protected void applyModelMatrix(GL gl) {
        JoglUtil.applyTransform(gl,
                                _myParent.getTransformMode(),
                                _myParent.transform(),
                                _myParent.rotation(),
                                _myParent.scale());
    }


    protected void setUniforms() {
        /* set uniform variables in shader */
        _myShaderManager.setUniform(_myShaderProgram, "shadowMap",
                                    JoglUtil.getTextureUnitID(_myProjectionTexture.getTextureUnit()));
        /** @todo set shadow texture coord unit here as well */
    }


    public void end(GLContext theRenderContext, Material theParent) {
        _myShaderManager.disable();
    }
}
