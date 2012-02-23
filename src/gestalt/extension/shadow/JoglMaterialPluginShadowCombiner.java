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

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.util.JoglUtil;


/**
 * this plugin was written to be used with the shadow extension. it
 * blendes the shadow color4f in texture unit 1 with the color4f from
 * texture unit 0.
 */

public class JoglMaterialPluginShadowCombiner
    implements MaterialPlugin {

    private boolean isInitialized;

    private boolean hasExtension;

    private boolean _myIsActive;

    private final JoglShadowMap _myShadowMapExtension;

    public JoglMaterialPluginShadowCombiner(JoglShadowMap theShadowMapExtension) {
        _myShadowMapExtension = theShadowMapExtension;
        isInitialized = false;
        hasExtension = false;
        _myIsActive = true;
    }


    public boolean isActive() {
        return _myIsActive;
    }


    public void setActive(boolean theState) {
        _myIsActive = theState;
    }


    public void begin(final GLContext theRenderContext,
                      final Material theParent) {
        if (!isInitialized) {
            final GL gl = (  theRenderContext).gl;
            if (JoglUtil.testExtensionAvailability(gl, "GL_NV_register_combiners")) {
                hasExtension = true;
            }
            isInitialized = true;
        }

        if (hasExtension &&
            _myIsActive &&
            _myShadowMapExtension.isShadowEnabled()) {
            final GL gl = (  theRenderContext).gl;
            final boolean withTexture = (theParent.texture() != null);
            gl.glCombinerParameteriNV(GL.GL_NUM_GENERAL_COMBINERS_NV, withTexture ? 2 : 1);
            gl.glEnable(GL.GL_REGISTER_COMBINERS_NV);

            /*
             CombinerInputNV(GLenum stage,
                GLenum portion,
                GLenum variable,
                GLenum input, GLenum mapping,
                GLenum componentUsage);

             CombinerOutputNV(GLenum stage,
                 GLenum portion,
                 GLenum abOutput, GLenum cdOutput, GLenum sumOutput,
                 GLenum scale, GLenum bias,
                 GLboolean abDotProduct, GLboolean cdDotProduct, GLboolean muxSum);

             FinalCombinerInputNV(GLenum variable,
                                  GLenum input,
                                  GLenum mapping,
                                  GLenum componentUsage);
             */

            /*
             * combiner 0 (A*B + C*D)
             * Computes scale/biased shadow intensity in SPARE0 from CONSTANT_COLOR0.
             * A: inv(shadowcolor)
             * B: depthmap
             * C: shadowcolor
             * D: 1
             * SPARE0 = A*B + C*D = (1-shadowcolor) * depthmap + shadowcolor
             */
            gl.glCombinerInputNV(GL.GL_COMBINER0_NV,
                                 GL.GL_RGB,
                                 GL.GL_VARIABLE_A_NV,
                                 GL.GL_CONSTANT_COLOR0_NV, GL.GL_UNSIGNED_INVERT_NV,
                                 GL.GL_RGB);

            gl.glCombinerInputNV(GL.GL_COMBINER0_NV,
                                 GL.GL_RGB,
                                 GL.GL_VARIABLE_B_NV,
                                 GL.GL_TEXTURE1,
                                 GL.GL_UNSIGNED_IDENTITY_NV,
                                 GL.GL_RGB);

            gl.glCombinerInputNV(GL.GL_COMBINER0_NV,
                                 GL.GL_RGB,
                                 GL.GL_VARIABLE_C_NV,
                                 GL.GL_CONSTANT_COLOR0_NV, GL.GL_UNSIGNED_IDENTITY_NV,
                                 GL.GL_RGB);

            gl.glCombinerInputNV(GL.GL_COMBINER0_NV,
                                 GL.GL_RGB,
                                 GL.GL_VARIABLE_D_NV,
                                 GL.GL_ZERO, GL.GL_UNSIGNED_INVERT_NV,
                                 GL.GL_RGB);

            gl.glCombinerOutputNV(GL.GL_COMBINER0_NV,
                                  GL.GL_RGB,
                                  GL.GL_DISCARD_NV, GL.GL_DISCARD_NV, GL.GL_SPARE0_NV,
                                  GL.GL_NONE, GL.GL_NONE,
                                  false, false, false);

            if (withTexture) {
                /*
                 * combiner 1
                 * Computes primary_color * texcolor0 in SPARE1.
                 * A: primary_color
                 * B: texcolor0
                 * C: 0
                 * D: 0
                 * SPARE1 = A*B + C*D = primary_color * texcolor0
                 */
                gl.glCombinerInputNV(GL.GL_COMBINER1_NV,
                                     GL.GL_RGB,
                                     GL.GL_VARIABLE_A_NV,
                                     GL.GL_PRIMARY_COLOR_NV, GL.GL_UNSIGNED_IDENTITY_NV,
                                     GL.GL_RGB);
                gl.glCombinerInputNV(GL.GL_COMBINER1_NV,
                                     GL.GL_ALPHA,
                                     GL.GL_VARIABLE_A_NV,
                                     GL.GL_PRIMARY_COLOR_NV, GL.GL_UNSIGNED_IDENTITY_NV,
                                     GL.GL_ALPHA);

                gl.glCombinerInputNV(GL.GL_COMBINER1_NV,
                                     GL.GL_RGB,
                                     GL.GL_VARIABLE_B_NV,
                                     GL.GL_TEXTURE0,
                                     GL.GL_UNSIGNED_IDENTITY_NV,
                                     GL.GL_RGB);
                gl.glCombinerInputNV(GL.GL_COMBINER1_NV,
                                     GL.GL_ALPHA,
                                     GL.GL_VARIABLE_B_NV,
                                     GL.GL_TEXTURE0,
                                     GL.GL_UNSIGNED_IDENTITY_NV,
                                     GL.GL_ALPHA);

                gl.glCombinerInputNV(GL.GL_COMBINER1_NV,
                                     GL.GL_RGB,
                                     GL.GL_VARIABLE_C_NV,
                                     GL.GL_ZERO, GL.GL_UNSIGNED_IDENTITY_NV,
                                     GL.GL_RGB);

                gl.glCombinerInputNV(GL.GL_COMBINER1_NV,
                                     GL.GL_RGB,
                                     GL.GL_VARIABLE_D_NV,
                                     GL.GL_ZERO, GL.GL_UNSIGNED_IDENTITY_NV,
                                     GL.GL_RGB);

                gl.glCombinerOutputNV(GL.GL_COMBINER1_NV,
                                      GL.GL_RGB,
                                      GL.GL_DISCARD_NV, GL.GL_DISCARD_NV, GL.GL_SPARE1_NV,
                                      GL.GL_NONE, GL.GL_NONE,
                                      false, false, false);
            }

            // final combiner (fragment = A*B + (1-A)*C + D)
            // Computer fragment color4f
            // A: SPARE0
            // B: withTexture ? SPARE1 : primary_color
            // C: 0
            // D: 0
            gl.glFinalCombinerInputNV(GL.GL_VARIABLE_A_NV,
                                      GL.GL_SPARE0_NV,
                                      GL.GL_UNSIGNED_IDENTITY_NV,
                                      GL.GL_RGB);
            if (withTexture) {
                gl.glFinalCombinerInputNV(GL.GL_VARIABLE_B_NV,
                                          GL.GL_SPARE1_NV,
                                          GL.GL_UNSIGNED_IDENTITY_NV,
                                          GL.GL_RGB);
            } else {
                gl.glFinalCombinerInputNV(GL.GL_VARIABLE_B_NV,
                                          GL.GL_PRIMARY_COLOR_NV,
                                          GL.GL_UNSIGNED_IDENTITY_NV,
                                          GL.GL_RGB);
            }
            gl.glFinalCombinerInputNV(GL.GL_VARIABLE_C_NV,
                                      GL.GL_ZERO,
                                      GL.GL_UNSIGNED_IDENTITY_NV,
                                      GL.GL_RGB);
            gl.glFinalCombinerInputNV(GL.GL_VARIABLE_D_NV,
                                      GL.GL_ZERO,
                                      GL.GL_UNSIGNED_IDENTITY_NV,
                                      GL.GL_RGB);
        }
    }


    public void end(final GLContext theRenderContext,
                    final Material theParent) {
        if (hasExtension &&
            _myIsActive &&
            _myShadowMapExtension.isShadowEnabled()) {
            (  theRenderContext).gl.glDisable(GL.GL_REGISTER_COMBINERS_NV);
        }
    }
}
