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
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.Plane;

import processing.core.PApplet;


/**
 * this demo shows how to use a gestalt renderer from inside a processing applet.
 */

public class UsingShapes
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Plane _myPlaneIn3D;

    private Plane _myPlaneIn2D;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);
        rectMode(CENTER);
        noStroke();

        gestalt = new GestaltPlugIn(this);

        /* create planes */
        _myPlaneIn3D = gestalt.drawablefactory().plane();
        _myPlaneIn3D.scale().x = 50;
        _myPlaneIn3D.scale().y = 150;
        gestalt.bin(Gestalt.BIN_3D).add(_myPlaneIn3D);

        _myPlaneIn2D = gestalt.drawablefactory().plane();
        _myPlaneIn2D.scale().x = 50;
        _myPlaneIn2D.scale().y = 150;
        gestalt.bin(Gestalt.BIN_2D_FOREGROUND).add(_myPlaneIn2D);

        noCursor();
    }


    public void draw() {
        /* glue gestalt shapes to mouse */
        _myPlaneIn3D.position().x = mouseX - 60;
        _myPlaneIn3D.position().y = mouseY;

        _myPlaneIn2D.position().x = mouseX + 60;
        _myPlaneIn2D.position().y = mouseY;

        /* clear screen */
        background(255, 128, 0);

        /* draw processing shape */
        rect(gestalt.event().mouseX, gestalt.event().mouseY, 50, 150);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingShapes.class.getName()});
    }
}
