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


package gestalt.demo.processing.unsorted;

import gestalt.context.GLContext;
import gestalt.processing.GestaltPlugIn;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;

import processing.core.PApplet;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


/**
 * this demo shows how to use a gestalt renderer, completly strip it of all
 * functionality and than render 'raw' opengl with just a single 'drawable'.
 * this is a proof of concept and not the ideal way to do it.<br/>
 * another reason for writing this demo was that processing 'messes' up some
 * opengl states, which are restored by 'gestalt'. this way one could say
 * that it is a bit saver to use opengl from within 'gestalt'.
 */

public class UsingOpenGL
    extends PApplet {

    private GestaltPlugIn gestalt;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);

        /* create gestalt plugin */
        gestalt = new GestaltPlugIn(this);

        /* remove all gestalt presets */
        RenderBin myRenderBin = new RenderBin();
        gestalt.setBinRef(myRenderBin);
        myRenderBin.add(new RawOpenGL());
    }


    public void draw() {
        background(0);
    }


    private class RawOpenGL
        extends AbstractDrawable {

        public void draw(final GLContext theContext) {
            GL gl = gestalt.getGL();
            GLU glu = gestalt.getGLU();

            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f,
                               (float) width / (float) height,
                               1.0,
                               20.0);

            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glTranslatef( -1.5f, 0.0f, -6.0f);
            gl.glBegin(GL.GL_TRIANGLES);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, 1.0f, 0.0f);
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 0.0f);
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glVertex3f(1.0f, -1.0f, 0.0f);
            gl.glEnd();

            gl.glTranslatef(3.0f, 0.0f, 0.0f);
            gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.5f, 0.5f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 0.0f);
            gl.glVertex3f(1.0f, 1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 0.0f);
            gl.glEnd();
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingOpenGL.class.getName()});
    }
}
