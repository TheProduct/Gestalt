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


import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.shape.AbstractShape;
import gestalt.util.JoglUtil;


public class JoglGLSLShadowMaterial4ud
    extends JoglGLSLShadowMaterial {

    public static float epsilon = 2f;

    public static float shadowedVal = 0.5f;

    public JoglGLSLShadowMaterial4ud(ShaderManager theShaderManager,
                                     ShaderProgram theShaderProgram,
                                     JoglGLSLShadowMap theJoglGLSLShadowMap,
                                     AbstractShape theParent) {
        super(theShaderManager, theShaderProgram, theJoglGLSLShadowMap, theParent);
    }


    protected void setUniforms() {
        /* set uniform variables in shader */
        _myShaderManager.setUniform(_myShaderProgram, "shadowMap",
                                    JoglUtil.getTextureUnitID(_myProjectionTexture.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "epsilon", epsilon);
        _myShaderManager.setUniform(_myShaderProgram, "shadowedVal", shadowedVal);
    }
}
