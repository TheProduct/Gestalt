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
import gestalt.render.controller.FrameBufferCopy;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.IntegerBitmap;

import processing.core.PApplet;


/**
 * this demo shows how to use a processing sketch as a texture in gestalt.
 */


public class UsingSketchesAsTextures
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Plane _myScreen;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);
        rectMode(CENTER);

        gestalt = new GestaltPlugIn(this);

        /*
         * keep gestalt from clearing any buffers.
         * we do this after we copied the framebuffer into a texture
         */
        gestalt.framesetup().depthbufferclearing = false;
        gestalt.framesetup().colorbufferclearing = false;

        /* create an empty dummy bitmap */
        TexturePlugin myTexture = gestalt.drawablefactory().texture();
        myTexture.load(IntegerBitmap.getDefaultImageBitmap(width, height));

        /* create a plane to display our texture */
        _myScreen = gestalt.drawablefactory().plane();
        _myScreen.position().set(width / 2, height / 2);
        _myScreen.material().addPlugin(myTexture);
        _myScreen.scale().set(width, height);
        gestalt.bin(Gestalt.BIN_3D).add(_myScreen);

        /* create a screengrabber */
        FrameBufferCopy myFrameBufferCopy = new FrameBufferCopy(myTexture);
        myFrameBufferCopy.backgroundcolor.set(0.2f, 1);
        myFrameBufferCopy.width = width;
        myFrameBufferCopy.height = height;
        gestalt.bin(Gestalt.BIN_3D_SETUP).add(myFrameBufferCopy);
    }


    public void draw() {
        /* clear screen */
        background(0, 128, 255);

        /* draw processing stuff */
        fill(255, 255);
        stroke(0, 255);
        strokeWeight(20);
        rect(mouseX, mouseY, 200, 200);

        /* rotate gestalt canvas */
        _myScreen.rotation().x += -0.0024f;
        _myScreen.rotation().y += 0.0063f;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingSketchesAsTextures.class.getName()});
    }
}
