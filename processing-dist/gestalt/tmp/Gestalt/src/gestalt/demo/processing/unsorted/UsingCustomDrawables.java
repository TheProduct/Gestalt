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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.AbstractDrawable;

import processing.core.PApplet;

import javax.media.opengl.GL;


/**
 * this demo shows how to use a gestalt renderer from inside a processing applet.
 */
public class UsingCustomDrawables
        extends PApplet {

    private GestaltPlugIn gestalt;

    private MyDrawable _myDrawable;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);
        smooth();

        gestalt = new GestaltPlugIn(this);

        /* create drawable */
        _myDrawable = new MyDrawable();
        gestalt.bin(Gestalt.BIN_2D_FOREGROUND).add(_myDrawable);
    }

    public void draw() {
        /* clear screen */
        background(127, 0, 127);
    }

    private class MyDrawable
            extends AbstractDrawable {

        public void draw(GLContext theRenderContext) {
            final GL gl = theRenderContext.gl;
            gl.glBegin(GL.GL_LINES);
            gl.glVertex2i(0, 0);
            gl.glVertex2i(mouseX, mouseY);
            gl.glEnd();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {UsingCustomDrawables.class.getName()});
    }
}
