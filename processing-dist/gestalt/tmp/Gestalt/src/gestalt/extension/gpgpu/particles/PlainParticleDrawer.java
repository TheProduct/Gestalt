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
import gestalt.Gestalt;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.util.JoglUtil;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class PlainParticleDrawer
        extends AbstractParticleDrawer {

    public PlainParticleDrawer() {
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

        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".createVBO()", true);
    }

    private void display(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
        final int w = theFBO.getPixelWidth();
        final int h = theFBO.getPixelHeight();

        // render vertex array as points
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBO);

        gl.glVertexPointer(4, GL.GL_FLOAT, 0, 0);
        gl.glDrawArrays(GL.GL_POINTS, 0, w * h);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);

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
