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


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.Gestalt;
import gestalt.candidates.JoglMultiTexPlane;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_RGBA32Float;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;
import gestalt.shape.AbstractDrawable;
import gestalt.material.texture.Bitmaps;
import gestalt.util.JoglUtil;

import com.sun.opengl.util.BufferUtil;
import data.Resource;
import gestalt.extension.gpgpu.JoglGPGPUFBO;


public class GPGPU2DParticlesSimulation
        extends AbstractDrawable {

    private final int WIDTH;

    private final int HEIGHT;

    private final JoglGPGPUFBO[] _myFBOs;

    private final ShaderProgram _myShaderProgram;

    private ShaderManager _myShaderManager;

    private int POSITION_READ = 0;

    private int POSITION_WRITE = 1;

    private float _myDeltaTime = 1 / 60f;

    private final float width;

    private final float height;

    private TexturePlugin _myHeightfield;

    public GPGPU2DParticlesSimulation(ShaderManager theShaderManager,
                                      int theWidth, int theHeight,
                                      int theScreenWidth, int theScreenHeight) {
        WIDTH = theWidth;
        HEIGHT = theHeight;
        width = theScreenWidth;
        height = theScreenHeight;
        _myFBOs = new JoglGPGPUFBO[2];
        _myFBOs[0] = new JoglGPGPUFBO(WIDTH, HEIGHT, GL.GL_TEXTURE0, new JoglTexCreatorFBO_RGBA32Float());
        _myFBOs[1] = new JoglGPGPUFBO(WIDTH, HEIGHT, GL.GL_TEXTURE1, new JoglTexCreatorFBO_RGBA32Float());

        _myShaderManager = theShaderManager;

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram,
                                            Resource.getStream("demo/shader/gpgpu/GPGPU2DParticle.vs"));
        _myShaderManager.attachFragmentShader(_myShaderProgram,
                                              Resource.getStream("demo/shader/gpgpu/GPGPU2DParticle.fs"));

        _myHeightfield = new TexturePlugin(true);
        _myHeightfield.setWrapMode(Gestalt.TEXTURE_WRAPMODE_CLAMP);
        _myHeightfield.setTextureTarget(GL.GL_TEXTURE_RECTANGLE_ARB);
        _myHeightfield.setTextureUnit(GL.GL_TEXTURE3);

        _myHeightfield.load(Bitmaps.getBitmap(Resource.getStream("demo/common/heightfield.png")));
    }

    private JoglMultiTexPlane createView(JoglFrameBufferObject theFBO, int theID) {
        /* create view */
        JoglMultiTexPlane myFBOView = JoglFrameBufferObject.createView(theFBO);
        myFBOView.material().texture().scale().set(WIDTH, HEIGHT);
        myFBOView.position().set(WIDTH / 2 + 2, -HEIGHT / 2 - 2);
        myFBOView.position().add(-width / 2, height / 2);
        myFBOView.position().x += (WIDTH + 2) * theID;
        return myFBOView;
    }

    public void setDeltaTime(final float theDeltaTime) {
        _myDeltaTime = theDeltaTime;
    }

    public void draw(GLContext theRenderContext) {

        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        final JoglFrameBufferObject READ_FBO = _myFBOs[POSITION_READ].fbo();
        final JoglFrameBufferObject WRITE_FBO = _myFBOs[POSITION_WRITE].fbo();

        JoglUtil.printGLError(gl, glu, "draw().begin", true);

        /* 'touch' heightfield / should be an FBO */
        _myHeightfield.begin(theRenderContext, null);
        _myHeightfield.end(theRenderContext, null);

        JoglUtil.printGLError(gl, glu, "heightfield", true);

        /* enable shader */
        _myShaderManager.enable(_myShaderProgram);

        _myShaderManager.setUniform(_myShaderProgram, "deltatime", _myDeltaTime);
        _myShaderManager.setUniform(_myShaderProgram, "width", (float)width);
        _myShaderManager.setUniform(_myShaderProgram, "height", (float)height);

        /* set uniform variables in shader */
        _myShaderManager.setUniform(_myShaderProgram, "texturePosition",
                                    getTextureUnitID(READ_FBO.getTextureUnit()));
        _myShaderManager.setUniform(_myShaderProgram, "textureHeightfield",
                                    getTextureUnitID(_myHeightfield.getTextureUnit()));

        /* bind textures */
        gl.glActiveTexture(READ_FBO.getTextureUnit());
        gl.glBindTexture(READ_FBO.getTextureTarget(), READ_FBO.getTextureID());

        /* prepare fbo viewport */
        WRITE_FBO.draw(theRenderContext);

        /* draw fullscreen quad */
        final int myPreviousFBOBufferID = WRITE_FBO.bindBuffer(gl);
        quadFullscreenQuad(gl, WRITE_FBO.getPixelWidth(), WRITE_FBO.getPixelHeight());
        JoglFrameBufferObject.unbindBuffer(gl, myPreviousFBOBufferID);

        /* -- */
        _myShaderManager.disable();

        POSITION_READ++;
        POSITION_READ %= 2;
        POSITION_WRITE++;
        POSITION_WRITE %= 2;
    }

    private int getTextureUnitID(int theOpenGLTextureUnit) {
        return theOpenGLTextureUnit - GL.GL_TEXTURE0;
    }

    public class ParticleDrawer
            extends AbstractDrawable {

        private int _myVBO = Gestalt.UNDEFINED;

        private final Material _myMaterial;

        public ParticleDrawer() {
            _myMaterial = new Material();
        }

        private void createVBO(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
            final int w = theFBO.getPixelWidth();
            final int h = theFBO.getPixelHeight();

            // setup buffer object for 4 floats per item
            int[] tmp = new int[1];
            gl.glGenBuffers(1, tmp, 0);
            _myVBO = tmp[0];
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBO);
            gl.glBufferData(GL.GL_ARRAY_BUFFER,
                            w * h * 4 * BufferUtil.SIZEOF_FLOAT,
                            null,
                            GL.GL_STREAM_COPY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

            JoglUtil.printGLError(gl, glu, "createVBO()", true);
        }

        private void readBackData(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
            final int w = theFBO.getPixelWidth();
            final int h = theFBO.getPixelHeight();

            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, theFBO.getBufferInfo().framebuffer_object);

            // #pragma mark PBO read back
            // now that rendering is done, read the pixel colors into the VBO mesh
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myVBO);
            gl.glReadPixels(0, 0,
                            w, h,
                            GL.GL_RGBA,
                            GL.GL_FLOAT,
                            0);
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, 0);

            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

            JoglUtil.printGLError(gl, glu, "readBackData()", true);
        }

        private void display(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
            final int w = theFBO.getPixelWidth();
            final int h = theFBO.getPixelHeight();

            // render vertex array as triangle mesh
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBO);

            gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
            gl.glDrawArrays(GL.GL_POINTS, 0, w * h * 2);

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);

            JoglUtil.printGLError(gl, glu, "display()", true);
        }

        public void draw(GLContext theRenderContext) {
            final GL gl = theRenderContext.gl;
            final GLU glu = theRenderContext.glu;

            if (_myVBO == Gestalt.UNDEFINED) {
                createVBO(gl, glu, _myFBOs[POSITION_WRITE].fbo());
            }
            readBackData(gl, glu, _myFBOs[POSITION_WRITE].fbo());

            _myMaterial.begin(theRenderContext);
            display(gl, glu, _myFBOs[POSITION_WRITE].fbo());
            _myMaterial.end(theRenderContext);
        }

        public Material material() {
            return _myMaterial;
        }
    }

    public ParticleDrawer view() {
        return new ParticleDrawer();
    }


//    public float[] data() {
//        return _myFBOs[POSITION_WRITE].data();
//    }
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
