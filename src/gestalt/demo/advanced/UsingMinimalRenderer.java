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


package gestalt.demo.advanced;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.MinimalRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;


/**
 * this demo shows a minimum gestalt setup.
 * it uses 'MinimalRenderer', the most primitive renderer.
 * instead of using gestalt shapes it uses plain opengl.
 * gestalt is just handling the window creation, the mouse-
 * and keyevents and of course the drawing of the drawables.
 *
 * note that this is simplest way of using gestalt,
 * not the most efficient one.
 */

public class UsingMinimalRenderer
    extends AbstractDrawable {

    private boolean isInitialized = false;

    private float _myRotationAngle = 0.0f;

    public void draw(final GLContext theContext) {
        final GL gl = (  theContext).gl;
        final GLU glu = (  theContext).glu;

        if (isInitialized) {
            display(gl, glu);
        } else {
            isInitialized = true;
            init(gl,
                 glu,
                 theContext.displaycapabilities.width,
                 theContext.displaycapabilities.height);
        }
    }


    public void init(GL gl, GLU glu, int theWidth, int theHeight) {
        gl.glViewport(0,
                      0,
                      theWidth,
                      theHeight);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f,
                           (float) theWidth / (float) theHeight,
                           1.0,
                           20.0);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
    }


    public void display(GL gl, GLU glu) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -6.0f);
        gl.glRotatef(_myRotationAngle, 1.0f, 0.5f, 0.33f);

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f( -1.0f, 1.0f, -1.0f);
        gl.glVertex3f( -1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);

        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glVertex3f( -1.0f, -1.0f, 1.0f);
        gl.glVertex3f( -1.0f, -1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);

        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f( -1.0f, 1.0f, 1.0f);
        gl.glVertex3f( -1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);

        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glVertex3f( -1.0f, -1.0f, -1.0f);
        gl.glVertex3f( -1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);

        gl.glVertex3f( -1.0f, 1.0f, 1.0f);
        gl.glVertex3f( -1.0f, 1.0f, -1.0f);
        gl.glVertex3f( -1.0f, -1.0f, -1.0f);
        gl.glVertex3f( -1.0f, -1.0f, 1.0f);

        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glEnd();

        _myRotationAngle += 0.1f;
    }


    public static void main(String[] args) {
        MinimalRenderer myRenderer = new MinimalRenderer();

        /* create context with default settings */
        myRenderer.create(myRenderer.createDisplayCapabilities());

        /* add the eventhandler to the renderer so it gets updated */
        myRenderer.bin().add(myRenderer.event());

        /* create and add a bin */
        RenderBin myBin = new RenderBin();
        myRenderer.bin().add(myBin);

        /* create and add a drawable to that bin */
        myBin.add(new UsingMinimalRenderer());

        /* loop renderer until 'escape' is pressed */
        while (myRenderer.event().keyCode != Gestalt.KEYCODE_ESCAPE) {
            myRenderer.display().display();
        }

        /* clean up */
        myRenderer.quit();
    }
}
