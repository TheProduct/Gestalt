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


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.AbstractDrawable;
import gestalt.shape.Plane;

import processing.core.PApplet;


/**
 * this demo shows how to use a gestalt renderer from inside a processing applet.
 */

public class UsingProcessingInGestaltDrawables
    extends PApplet {

    private GestaltPlugIn gestalt;

    private MyDrawable _myDrawable;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);

        gestalt = new GestaltPlugIn(this);

        /*
         * discard all gestalt bins.
         * this also remove features like 'camera', 'light', 'fog' etc.
         * this all is now handled by processing.
         * unpluggin can be useful when you want to mix gestalt and processing drawing.
         */
        gestalt.unplug();

        /* create drawable */
        _myDrawable = new MyDrawable();
        gestalt.bin().add(_myDrawable);

        /* create plane */
        Plane myPlane = gestalt.drawablefactory().plane();
        myPlane.scale(100, 100, 1);
        myPlane.position(0, 0, -420);
        myPlane.rotation(0.03f, 0.4f, 0.0f);
        gestalt.bin().add(myPlane);
    }


    public void draw() {
        /* clear screen */
        background(127, 0, 127);

        gestalt.getGL().glBegin(GL.GL_TRIANGLES);
        gestalt.getGL().glVertex3f(mouseX - width / 2, mouseY - height / 2, -420);
        gestalt.getGL().glVertex3f( -100, -100, -420);
        gestalt.getGL().glVertex3f(100, -100, -420);
        gestalt.getGL().glEnd();

        line(0, 0, mouseX, mouseY);

        gestalt.draw();

        translate(10, 0);
        gestalt.draw();

        translate(10, 0);
        gestalt.draw();
    }


    private class MyDrawable
        extends AbstractDrawable {
        public void draw(GLContext theRenderContext) {
            stroke(255);
            line(width, height, mouseX, mouseY);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingProcessingInGestaltDrawables.class.getName()});
    }
}
