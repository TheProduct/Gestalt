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


import gestalt.processing.G5;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;

import processing.core.PApplet;


/*
 * this sketch shows how to hack a processing sketch so that it
 * is rendered into a FBO. the FBO can than be used as a texture
 * without any extra pixel copying costs.
 * that s the beauty of it.
 *
 * all FBO related processing sketches seem to have problems on
 * windows hardware. problems on windows? that s new.
 */


/**
 * @deprecated this sketch seems to contain errors...
 */
public class UsingSnitchSketchToTexture
    extends PApplet {

    private Plane _myPlane;

    public void setup() {
        System.err.println("### " + getClass().getSimpleName() + " seems to be broken since processing 148++.");

        /* setup p5 */
        size(4096, 4096, OPENGL);
        rectMode(CENTER);

        /* gestalt */
        boolean MAKE_PROCESSING_FRIENDLY = false;
        G5.setup(this, MAKE_PROCESSING_FRIENDLY);
        G5.gestalt().displaycapabilities().backgroundcolor.set(0.2f);

        /* this flag must be true if sketch is run in presentation mode */
        final boolean PRESENTATION_MODE = false;

        /* create p5 framebuffer object. the first two values define the size of the actual frame the sketch will run in. */
        TexturePlugin myFBO = G5.sketchTexture(this, 1024, 768, PRESENTATION_MODE);

        /* create plane to show processing sketch */
        _myPlane = G5.plane();
        _myPlane.material().addPlugin(myFBO);
        _myPlane.scale().set(512, 512);
    }


    public void draw() {
        /* clear screen */
        background(127, 255, 127);

        /* draw processing stuff */
        fill(255);
        stroke(0);
        strokeWeight(20);
        rect(mouseX / 1024f * width, mouseY / 768f * height, 200, 200);

        /* rotate plane with sketch */
        _myPlane.rotation().x += 0.005f;
        _myPlane.rotation().y += 0.0033f;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingSnitchSketchToTexture.class.getName()});
    }
}
