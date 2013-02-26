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


package gestalt.extension.gpgpu.particles;


import gestalt.extension.gpgpu.JoglGPGPUFBO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.Gestalt;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_2xRGBA32Float;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.shape.AbstractDrawable;
import gestalt.util.JoglUtil;

import data.Resource;
import gestalt.extension.framebufferobject.BufferInfo;
import mathematik.Vector3f;


public class GPGPU3DParticlesSimulation
        extends AbstractDrawable {

    private final int WIDTH;

    private final int HEIGHT;

    private final JoglGPGPUFBO[] _myFBOs;

    private DataReadBack _myDataReadBack;

    private final ShaderProgram _myShaderProgram;

    private ShaderManager _myShaderManager;

    public Vector3f flow_direction = new Vector3f(0, -1, 0);

    public float flow_speed = 2f;

    private int POSITION_READ = 0;

    private int POSITION_WRITE = 1;

    private float _myDeltaTime = 1 / 60f;

    private final float width;

    private final float height;

    private final TexturePlugin _myHeightfield;

    private FBORandomizer[] _myRandomizer;

    private AbstractParticleDrawer _myView;

    private AbstractParticleResetter _myParticleResetter;

    public GPGPU3DParticlesSimulation(ShaderManager theShaderManager,
                                      int theWidth, int theHeight,
                                      int theScreenWidth, int theScreenHeight,
                                      final TexturePlugin theHeightfield,
                                      String theParticleVertexShader,
                                      String theParticleFragShader) {
        WIDTH = theWidth;
        HEIGHT = theHeight;
        width = theScreenWidth;
        height = theScreenHeight;
        _myHeightfield = theHeightfield;

        _myFBOs = new JoglGPGPUFBO[2];
        _myFBOs[0] = new JoglGPGPUFBO(WIDTH, HEIGHT, GL.GL_TEXTURE0, new JoglTexCreatorFBO_2xRGBA32Float());
        _myFBOs[0].fbo().additional_texture(BufferInfo.SECONDARY).setTextureUnit(GL.GL_TEXTURE1);
        _myFBOs[0].fbo().name = "JoglGPGPUFBO[0]";
        _myFBOs[1] = new JoglGPGPUFBO(WIDTH, HEIGHT, GL.GL_TEXTURE0, new JoglTexCreatorFBO_2xRGBA32Float());
        _myFBOs[1].fbo().additional_texture(BufferInfo.SECONDARY).setTextureUnit(GL.GL_TEXTURE1);
        _myFBOs[1].fbo().name = "JoglGPGPUFBO[1]";

        _myDataReadBack = new DataReadBack();

        _myRandomizer = new FBORandomizer[2];
        _myRandomizer[0] = new FBORandomizer(_myFBOs[0].fbo());
        _myRandomizer[1] = new FBORandomizer(_myFBOs[1].fbo());

        _myShaderManager = theShaderManager;
        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream(theParticleVertexShader));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream(theParticleFragShader));
    }

    public void setView(AbstractParticleDrawer theView) {
        _myView = theView;
    }

    public void setResetter(AbstractParticleResetter theResetter) {
        _myParticleResetter = theResetter;
    }

    public void setDeltaTime(final float theDeltaTime) {
        _myDeltaTime = theDeltaTime;
    }

    public ShaderProgram getShaderProgram() {
        return _myShaderProgram;
    }

    public DataReadBack readback() {
        return new DataReadBack();
    }

    public void draw(GLContext theRenderContext) {

        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        final JoglFrameBufferObject READ_FBO = _myFBOs[POSITION_READ].fbo();
        final JoglFrameBufferObject WRITE_FBO = _myFBOs[POSITION_WRITE].fbo();

        JoglUtil.printGLError(gl, glu, "draw().begin", true);

        if (_myView != null) {
            _myView.init(READ_FBO);
        } else {
            System.out.println("### WARNING @ GPGPU3DParticlesSimulation: no view added");
        }

        /* init FBOs */
        if (!READ_FBO.isInitialized()) {
            READ_FBO.init(gl);
        }
        if (!WRITE_FBO.isInitialized()) {
            WRITE_FBO.init(gl);
        }

        JoglUtil.printGLError(gl, glu, "draw().initFBOs", true);

        /* randomize data */
        _myRandomizer[0].draw(theRenderContext);
        _myRandomizer[1].draw(theRenderContext);

        JoglUtil.printGLError(gl, glu, "randomizer", true);

        /* 'touch' heightfield / should be an FBO */
        _myHeightfield.begin(theRenderContext, null);
        _myHeightfield.end(theRenderContext, null);

        JoglUtil.printGLError(gl, glu, "heightfield", true);

        /* enable shader */
        _myShaderManager.enable(_myShaderProgram);

        _myShaderManager.setUniform(_myShaderProgram, "deltatime", _myDeltaTime);
        _myShaderManager.setUniform(_myShaderProgram, "width", (float)width);
        _myShaderManager.setUniform(_myShaderProgram, "height", (float)height);
        _myShaderManager.setUniform(_myShaderProgram, "flowdirection", flow_direction);
        _myShaderManager.setUniform(_myShaderProgram, "speed", flow_speed);

        /* set uniform variables in shader */
        _myShaderManager.setUniform(_myShaderProgram, "texturePosition", JoglUtil.getTextureUnitID(READ_FBO.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "textureVelocity", JoglUtil.getTextureUnitID(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "textureHeightfield", JoglUtil.getTextureUnitID(_myHeightfield.getTextureUnit()));

        JoglUtil.printGLError(gl, glu, "set uniforms", true);

        /* update resetter */
        if (_myParticleResetter != null) {
            _myParticleResetter.draw();
        }

        /* prepare fbo viewport */
        WRITE_FBO.draw(theRenderContext);

        /* bind textures */
        gl.glActiveTexture(READ_FBO.getTextureUnit());
        gl.glBindTexture(READ_FBO.getTextureTarget(),
                         READ_FBO.getTextureID());
        gl.glActiveTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureUnit());
        gl.glBindTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(),
                         READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureID());

        JoglUtil.printGLError(gl, glu, "bind textures", true);

        /* bind fbo viewport */
        final int myPreviousFBOBufferID = WRITE_FBO.bindBuffer(gl);

        JoglUtil.printGLError(gl, glu, "prepare viewport", true);

        /* draw fullscreen quad */
        gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
        gl.glDrawBuffers(2, new int[] {GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_COLOR_ATTACHMENT1_EXT}, 0);
        quadFullscreenQuad(gl, WRITE_FBO.getPixelWidth(), WRITE_FBO.getPixelHeight());
        gl.glPopAttrib();

        JoglUtil.printGLError(gl, glu, "draw fs quad", true);

        /* -- */
        gl.glActiveTexture(READ_FBO.getTextureUnit());
        gl.glBindTexture(READ_FBO.getTextureTarget(), 0);

        gl.glActiveTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureUnit());
        gl.glBindTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(), 0);

        JoglFrameBufferObject.unbindBuffer(gl, myPreviousFBOBufferID);
        _myShaderManager.disable();

        POSITION_READ++;
        POSITION_READ %= 2;
        POSITION_WRITE++;
        POSITION_WRITE %= 2;

        _myDataReadBack.draw(theRenderContext);
    }

    public class DataReadBack
            extends AbstractDrawable {

        public void draw(GLContext theRenderContext) {
            if (_myView != null && _myView.getVBO() != Gestalt.UNDEFINED) {
                final GL gl = theRenderContext.gl;
                final GLU glu = theRenderContext.glu;
                readBackData(gl, glu, _myFBOs[POSITION_READ].fbo());
            }
        }

        private void readBackData(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
            final int w = theFBO.getPixelWidth();
            final int h = theFBO.getPixelHeight();

            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theFBO.getBufferInfo().framebuffer_object);
            /* now that rendering is done, read the pixel colors into the VBO mesh */
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myView.getVBO());
            gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
            gl.glReadPixels(0, 0,
                            w, h,
                            GL.GL_RGBA,
                            GL.GL_FLOAT,
                            0);
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, 0);
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

            JoglUtil.printGLError(gl, glu, "readBackData()", true);
        }
    }

    private void quadFullscreenQuad(GL gl, float theWidth, float theHeight) {
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(0, 0);
        gl.glTexCoord2f(theWidth, 0);
        gl.glVertex2f(theWidth, 0);
        gl.glTexCoord2f(theWidth, theHeight);
        gl.glVertex2f(theWidth, theHeight);
        gl.glTexCoord2f(0, theHeight);
        gl.glVertex2f(0, theHeight);
        gl.glEnd();
    }
}
