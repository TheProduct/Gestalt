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


package gestalt.render.controller;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.util.JoglUtil;

import mathematik.Vector2f;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class OrthoSetup
        extends Camera {

    private int _my2DBufferDepth;

    public OrthoSetup() {
        frustumoffset = new Vector2f();
        _my2DBufferDepth = 200;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        /* viewport */
        /** @todo should the viewport get connected to the current camera? */
        gl.glViewport(0,
                      0,
                      theRenderContext.displaycapabilities.width,
                      theRenderContext.displaycapabilities.height);


        /* projection matrix */
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        final float myNearClipping = Math.max(1.0f, theRenderContext.displaycapabilities.height - _my2DBufferDepth);
        final float myFarClipping = theRenderContext.displaycapabilities.height + _my2DBufferDepth;
        JoglUtil.gluPerspective(gl,
                                Gestalt.CAMERA_A_HANDY_ANGLE,
                                (float)(theRenderContext.displaycapabilities.width) / (float)(theRenderContext.displaycapabilities.height),
                                myNearClipping,
                                myFarClipping,
                                frustumoffset);


        /* model-view matrix */
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0, 0, -theRenderContext.displaycapabilities.height);

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDepthMask(false);

        JoglUtil.printGLError(gl, glu, getClass().getName() + ".draw");
    }
}
