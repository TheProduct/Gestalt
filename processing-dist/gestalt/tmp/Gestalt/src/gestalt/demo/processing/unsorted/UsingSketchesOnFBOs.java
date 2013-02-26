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
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_DepthRGBA;
import gestalt.processing.GestaltPlugIn;
import gestalt.processing.JoglProcessingFrameBufferObject;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;

import processing.core.PApplet;


/*
 * this sketch shows how to hack a processing sketch so that it
 * is rendered into a FBO. the FBO can than be used as a texture
 * without any extra pixel copying costs.
 * that s the beauty of it.
 *
 * there is an issue with the framebuffer objects being larger
 * than the actual frame.
 */

/**
 * @deprecated this sketch seems to contain errors...
 */
public class UsingSketchesOnFBOs
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Plane _myPlane;

    private static final boolean USE_FBO = true;

    public void setup() {
        System.err.println("### " + getClass().getSimpleName() + " seems to be broken since processing 148++.");

        /* setup p5 */
        size(512, 512, OPENGL);
        rectMode(CENTER);

        /* by not create the p5 framebuffer object the sketch will run as a regular processing sketch */
        if (USE_FBO) {

            /* create gestalt plugin. don t make it processing friendly */
            boolean MAKE_PROCESSING_FRIENDLY = false;
            gestalt = new GestaltPlugIn(this, MAKE_PROCESSING_FRIENDLY);
            gestalt.displaycapabilities().backgroundcolor.set(0.2f);

            /* this flag must be true if sketch is run in presentation mode */
            final boolean PRESENTATION_MODE = false;

            /* create p5 framebuffer object. the first two values define the size of the actual frame the sketch will run in. */
            TexturePlugin myFBO = sketchTexture(this, gestalt, 1024, 768, PRESENTATION_MODE);

            /* create plane to show processing sketch */
            _myPlane = gestalt.drawablefactory().plane();
            _myPlane.material().addPlugin(myFBO);
            _myPlane.setPlaneSizeToTextureSize();
            gestalt.bin(Gestalt.BIN_3D).add(_myPlane);
        }
    }


    private JoglProcessingFrameBufferObject sketchTexture(final PApplet theParent,
                                                          final GestaltPlugIn theGestalt,
                                                          final int theWindowWidth,
                                                          final int theWindowHeight,
                                                          final boolean thePresentationModeFlag) {

        /* create p5 framebuffer object. the first two values define the size of the actual frame the sketch will run in. */
        JoglProcessingFrameBufferObject myFBO = new JoglProcessingFrameBufferObject(theWindowWidth, theWindowHeight,
                                                                                    new JoglTexCreatorFBO_DepthRGBA(),
                                                                                    theGestalt,
                                                                                    theParent,
                                                                                    thePresentationModeFlag);

        return myFBO;
    }


    public void draw() {
        /* clear screen */
        background(127, 255, 0);

        /* draw processing stuff */
        fill(255);
        stroke(0);
        strokeWeight(20);
        rect(mouseX, mouseY, 200, 200);

        /* rotate plane with sketch */
        if (USE_FBO) {
            _myPlane.rotation().x += 0.005f;
            _myPlane.rotation().y += 0.0033f;
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingSketchesOnFBOs.class.getName()});
    }
}
