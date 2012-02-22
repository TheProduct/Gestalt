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


package gestalt.extension.gpgpu.cawater;


import gestalt.material.Material;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;
import gestalt.util.JoglUtil;

import data.Resource;
import gestalt.extension.glsl.ShaderMaterial;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_RGBA32Float;
import gestalt.extension.gpgpu.JoglGPGPUFBO;
import gestalt.material.TexturePlugin;
import gestalt.material.TexturePlugin;
import mathematik.Vector2f;


public class GPGPUCAWater
        extends AbstractDrawable {

    public Vector2f flow_direction = new Vector2f(0, 0);

    public float damping = 0.998f;

    private final JoglGPGPUFBO[] _myFBOs;

    private final ShaderProgram _myShaderProgram;

    private ShaderManager _myShaderManager;

    private int _myTextureReadID = 0;

    private final TexturePlugin _myInputEnergyMap;

    private final ShaderProgram _myWaterDrawerShader;

    public GPGPUCAWater(ShaderManager theShaderManager,
                        TexturePlugin theInputEnergyMap,
                        String theParticleFragShader,
                        String theWaterDrawerShader) {
        final int myWidth = theInputEnergyMap.getPixelWidth();
        final int myHeight = theInputEnergyMap.getPixelHeight();
        _myInputEnergyMap = theInputEnergyMap;

        _myFBOs = new JoglGPGPUFBO[3];
        for (int i = 0; i < _myFBOs.length; i++) {
            _myFBOs[i] = new JoglGPGPUFBO(myWidth, myHeight, GL.GL_TEXTURE1 + i, new JoglTexCreatorFBO_RGBA32Float());
            _myFBOs[i].fbo().name = "JoglGPGPUFBO[" + i + "]";
        }

        _myShaderManager = theShaderManager;

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream(theParticleFragShader));

        /* create water shader drawer */
        _myWaterDrawerShader = _myShaderManager.createShaderProgram();
        _myShaderManager.attachFragmentShader(_myWaterDrawerShader, Resource.getStream(theWaterDrawerShader));
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        final JoglFrameBufferObject READ_PREV_FBO = getFBObyOffset(0);
        final JoglFrameBufferObject READ_CURRENT_FBO = getFBObyOffset(1);
        final JoglFrameBufferObject WRITE_FBO = getFBObyOffset(2);

        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".draw().begin", true);

        /* init FBOs */
        if (!READ_CURRENT_FBO.isInitialized()) {
            for (int i = 0; i < _myFBOs.length; i++) {
                _myFBOs[i].fbo().init(gl);
                _myFBOs[i].fbo().draw(theRenderContext);
            }
        }

        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".draw().initFBOs", true);

        /* enable shader */
        _myShaderManager.enable(_myShaderProgram);

        /* set uniform variables in shader */
        _myShaderManager.setUniform(_myShaderProgram, "read_cells", JoglUtil.getTextureUnitID(READ_CURRENT_FBO.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "read_prev_cells", JoglUtil.getTextureUnitID(READ_PREV_FBO.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "energy_map", JoglUtil.getTextureUnitID(_myInputEnergyMap.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "flow_direction", flow_direction);
        _myShaderManager.setUniform(_myShaderProgram, "damping", damping);

        /* bind textures */
        gl.glActiveTexture(_myInputEnergyMap.getTextureUnit());
        gl.glBindTexture(_myInputEnergyMap.getTextureTarget(),
                         _myInputEnergyMap.getTextureID());

        gl.glActiveTexture(READ_CURRENT_FBO.getTextureUnit());
        gl.glBindTexture(READ_CURRENT_FBO.getTextureTarget(),
                         READ_CURRENT_FBO.getTextureID());

        gl.glActiveTexture(READ_PREV_FBO.getTextureUnit());
        gl.glBindTexture(READ_PREV_FBO.getTextureTarget(),
                         READ_PREV_FBO.getTextureID());

        /* prepare fbo viewport */
        WRITE_FBO.draw(theRenderContext);

        /* the buffer needs to be bound a second time :( no good. */
        final int myPreviousFBOBufferID = WRITE_FBO.bindBuffer(gl);

        /* draw fullscreen quad */
        JoglGPGPUFBO.quadFullscreenQuad(gl, WRITE_FBO.getPixelWidth(), WRITE_FBO.getPixelHeight());

        /* -- */
        gl.glActiveTexture(READ_CURRENT_FBO.getTextureUnit());
        gl.glBindTexture(READ_CURRENT_FBO.getTextureTarget(), 0);

        gl.glActiveTexture(READ_PREV_FBO.getTextureUnit());
        gl.glBindTexture(READ_PREV_FBO.getTextureTarget(), 0);

        gl.glActiveTexture(_myInputEnergyMap.getTextureUnit());
        gl.glBindTexture(_myInputEnergyMap.getTextureTarget(), 0);

        JoglFrameBufferObject.unbindBuffer(gl, myPreviousFBOBufferID);
        _myShaderManager.disable();

        nextTextureID();
    }

    private JoglFrameBufferObject getFBObyOffset(int theOffset) {
        return _myFBOs[(_myTextureReadID + theOffset) % _myFBOs.length].fbo();
    }

    private void nextTextureID() {
        _myTextureReadID++;
        _myTextureReadID %= _myFBOs.length;
    }

    private JoglFrameBufferObject getReadBuffer() {
        return _myFBOs[_myTextureReadID].fbo();
    }

    /**
     * attaches a texture and a shader to the referenced material.
     */
    public void attachWater(final Material theMaterial) {

        /* TODO do we have to create a new instance of the texture? */
        theMaterial.addTexture(new WaterTexture());

        final ShaderMaterial myWaterShaderMaterial = new ShaderMaterial(_myShaderManager, _myWaterDrawerShader) {

            public void setUniforms() {
                setUniform("heightfield", JoglUtil.getTextureUnitID(theMaterial.texture().getTextureUnit()));
            }
        };

        /* attach material */
        theMaterial.addPlugin(myWaterShaderMaterial);
    }

    private class WaterTexture
            extends TexturePlugin {

        public WaterTexture() {
            setTextureUnit(GL.GL_TEXTURE0);
            setTextureTarget(GL.GL_TEXTURE_RECTANGLE_EXT);
            scale().set(getPixelWidth(), getPixelHeight());
        }

        public int getMaxTextureSize() {
            return 4096;
        }

        public int getPixelWidth() {
            final JoglFrameBufferObject myFBO = _myFBOs[_myTextureReadID].fbo();
            return myFBO.getPixelWidth();
        }

        public int getPixelHeight() {
            final JoglFrameBufferObject myFBO = _myFBOs[_myTextureReadID].fbo();
            return myFBO.getPixelHeight();
        }

        public void begin(GLContext theRenderContext, Material theParentMaterial) {
            final GL gl = theRenderContext.gl;
            final GLU glu = theRenderContext.glu;
            final JoglFrameBufferObject myFBO = _myFBOs[_myTextureReadID].fbo();

            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());

            gl.glBindTexture(getTextureTarget(), myFBO.getTextureID());

            /* adjust texture matrix */
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPushMatrix();

            if (rotation().x != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().x), 1, 0, 0);
            }
            if (rotation().y != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().y), 0, 1, 0);
            }
            if (rotation().z != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().z), 0, 0, 1);
            }

            gl.glTranslatef(position().x,
                            position().y,
                            0);

            gl.glScalef(scale().x,
                        scale().y,
                        scale().z);

            gl.glMatrixMode(GL.GL_MODELVIEW);
        }

        public void end(GLContext theRenderContext, Material theParent) {
            final GL gl = theRenderContext.gl;
            final GLU glu = theRenderContext.glu;


            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());

            /* recover texture matrix */
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW);

            /* enable texture target */
            gl.glDisable(getTextureTarget());

        }

        public void dispose(GLContext theRenderContext) {
        }
    }
}
