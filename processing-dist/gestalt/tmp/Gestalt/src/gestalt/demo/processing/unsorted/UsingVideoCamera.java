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
import gestalt.material.TexturePlugin;

import processing.core.PApplet;
import processing.video.Capture;


/**
 * this demo shows how to use a gestalt shape and the processing video capture.
 */

public class UsingVideoCamera
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Plane _myPlane;

    private TexturePlugin _myTexture;

    private Capture _myCapture;

    public void setup() {
        System.err.println("### " + getClass().getSimpleName() + " seems to be broken since processing 148++.");

        /* setup p5 */
        size(640, 480, OPENGL);
        rectMode(CENTER);
        noStroke();

        gestalt = new GestaltPlugIn(this);

        /* create a texture with reference to the camera */
        _myCapture = new Capture(this, 320, 240, 60);
        _myCapture.read();
        _myTexture = gestalt.drawablefactory().texture();
        _myTexture.load(GestaltPlugIn.createGestaltBitmap(_myCapture));

        /* create planes */
        _myPlane = gestalt.drawablefactory().plane();
        _myPlane.material().addPlugin(_myTexture);
        _myPlane.setPlaneSizeToTextureSize();
        gestalt.bin(Gestalt.BIN_3D).add(_myPlane);
    }


    public void draw() {
        /* clear screen */
        background(0, 255, 255);

        if (_myCapture.available()) {
            _myCapture.read();
            _myTexture.reload();
        }

        /* glue shapes to mouse */
        _myPlane.position().x = mouseX;
        _myPlane.position().y = mouseY;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingVideoCamera.class.getName()});
    }
}
