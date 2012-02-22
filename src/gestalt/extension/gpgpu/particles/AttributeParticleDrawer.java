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


import com.sun.opengl.util.BufferUtil;
import data.Resource;
import gestalt.Gestalt;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.framebufferobject.BufferInfo;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.util.JoglUtil;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import mathematik.Vector3f;


public class AttributeParticleDrawer
        extends AbstractParticleDrawer {

    private int _myIBO = Gestalt.UNDEFINED;

    private ShaderManager _myShaderManager;

    private final ShaderProgram _myPointSpriteShader;

    public float velocity_threshold = 1f;

    public float size_threshold = 1f;

    public float point_size = 10.0f;

    public Vector3f flow_direction = new Vector3f(0, -1, 0);

    public float collision_ratio = 0.9f;

    public AttributeParticleDrawer(ShaderManager theShaderManager,
                                   String thePointSpriteAttributeShader) {
        _myShaderManager = theShaderManager;
        _myMaterial = new Material();

        _myPointSpriteShader = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myPointSpriteShader,
                                            Resource.getStream(thePointSpriteAttributeShader));
    }

    public void init(JoglFrameBufferObject theFBO) {
        _myFBO = theFBO;
    }

    private void createVBO(GL gl, GLU glu, JoglFrameBufferObject theFBO) {
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

        /* create float buffer for pointsizes */
        float[] myValues = new float[w * h * 3];

        for (int i = 0; i < myValues.length; i += 3) {
            myValues[i + 0] = (i / 3) % w; // x
            myValues[i + 1] = (i / 3) / w; // y
            myValues[i + 2] = (float)Math.random() * 1; // pointsize
        }

        gl.glGenBuffers(1, tmp, 0);
        _myIBO = tmp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myIBO);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                        w * h * 3 * BufferUtil.SIZEOF_FLOAT,
                        FloatBuffer.wrap(myValues),
                        GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        /* check for errors */
        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".createVBO()", true);
    }

    private void display(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
        final int w = theFBO.getPixelWidth();
        final int h = theFBO.getPixelHeight();

        /* bind position data */
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBO);
        gl.glVertexPointer(4, GL.GL_FLOAT, 0, 0);

        /* bind point size data */
        _myShaderManager.enable(_myPointSpriteShader);
        final int myPointSizeAttrib = gl.glGetAttribLocation(_myPointSpriteShader.getOpenGLID(), "vertexAttribute");
        gl.glEnableVertexAttribArray(myPointSizeAttrib);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myIBO);
        gl.glVertexAttribPointer(myPointSizeAttrib,
                                 3,
                                 GL.GL_FLOAT,
                                 false,
                                 0, 0);
        gl.glEnable(GL.GL_VERTEX_PROGRAM_POINT_SIZE_ARB);

        /* --- */
        final JoglFrameBufferObject READ_FBO = _myFBO;
        _myShaderManager.setUniform(_myPointSpriteShader, "textureVelocity",
                                    READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureUnit() - GL.GL_TEXTURE0);
        _myShaderManager.setUniform(_myPointSpriteShader, "velocityThreshold", velocity_threshold);
        _myShaderManager.setUniform(_myPointSpriteShader, "sizeThreshold", size_threshold);
        _myShaderManager.setUniform(_myPointSpriteShader, "pointSize", point_size);
        _myShaderManager.setUniform(_myPointSpriteShader, "flowdirection", flow_direction);
        _myShaderManager.setUniform(_myPointSpriteShader, "collisionratio", collision_ratio);

        gl.glActiveTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureUnit());
        gl.glBindTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(),
                         READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureID());


        JoglUtil.printGLError(gl, glu, "binding texture", true);

        gl.glDrawArrays(GL.GL_POINTS, 0, w * h);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        gl.glDisableVertexAttribArray(myPointSizeAttrib);
        _myShaderManager.disable();
        gl.glDisable(GL.GL_VERTEX_PROGRAM_POINT_SIZE_ARB);
        gl.glBindTexture(READ_FBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(), 0);

        JoglUtil.printGLError(gl, glu, "display()", true);
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        if (_myVBO == Gestalt.UNDEFINED) {
            createVBO(gl, glu, _myFBO);
        } else {
            _myMaterial.begin(theRenderContext);
            display(gl, glu, _myFBO);
            _myMaterial.end(theRenderContext);
        }
    }
}