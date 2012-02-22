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


package gestalt.extension.materialplugin;


import java.io.InputStream;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;

import mathematik.Vector2f;
import mathematik.Vector2i;


public class JoglMaterialPluginGLSLGaussianBlur
    implements MaterialPlugin {

    private final ShaderManager _myMaterialShaderManager;

    private final ShaderProgram _myMaterialShaderProgram;

//    public float blursize = 20;

    public float blurspread = 1f;

    public float strength = 1.5f;

    private final Vector2f _myTextureSize;

    private float direction = 0;

    public JoglMaterialPluginGLSLGaussianBlur(final ShaderManager theMaterialShaderManager,
                                              final ShaderProgram theMaterialShaderProgram,
                                              final InputStream theVertexShaderCode,
                                              final InputStream theFragmentShaderCode,
                                              final Vector2i theTextureSize) {
        _myMaterialShaderManager = theMaterialShaderManager;
        _myMaterialShaderProgram = theMaterialShaderProgram;

        _myMaterialShaderManager.attachVertexShader(_myMaterialShaderProgram, theVertexShaderCode);
        _myMaterialShaderManager.attachFragmentShader(_myMaterialShaderProgram, theFragmentShaderCode);

        _myTextureSize = new Vector2f(1.0f / theTextureSize.x, 1.0f / theTextureSize.y);
    }


    public void begin(GLContext theRenderContext, Material theParent) {
        /* enable shader */
        _myMaterialShaderManager.enable(_myMaterialShaderProgram);

        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "textureunit", 0);
//        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "width", 1f / _myTextureSize.x);
//        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "height", 1f / _myTextureSize.y);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "texOffset", _myTextureSize);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "direction", direction);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "strength", strength);
        _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "spread", blurspread);

        /** @todo hack to switch buffers */
        if (direction == 1) {
            direction = 0;
        } else {
            direction = 1;
        }
    }


    public void end(final GLContext theRenderContext, final Material theParent) {
        _myMaterialShaderManager.disable();
    }
}
