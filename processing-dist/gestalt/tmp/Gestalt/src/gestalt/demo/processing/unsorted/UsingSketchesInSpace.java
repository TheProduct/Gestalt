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
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;
import gestalt.shape.TransformNode;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.IntegerBitmap;
import gestalt.util.CameraMover;

import processing.core.PApplet;


/**
 * this demo shows how to use a processing sketch as a texture on a 3D object.
 */


public class UsingSketchesInSpace
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Plane _myScreen;

    private Cuboid _myBox;

    private TransformNode _myNode;

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
        _myNode = gestalt.drawablefactory().transformnode();
        gestalt.bin(Gestalt.BIN_3D).add(_myNode);

        _myScreen = gestalt.drawablefactory().plane();
        _myScreen.position().z = 200 + 1;
        _myScreen.rotation().x = PI;
        _myScreen.material().addPlugin(myTexture);
        _myScreen.material().transparent = false;
        _myScreen.scale().set(width / 2, height / 2);
        _myNode.add(_myScreen);

        _myBox = gestalt.drawablefactory().cuboid();
        _myBox.scale().set(_myScreen.scale());
        _myBox.scale().z = 200;
        _myBox.position().z = 100;
        _myBox.material().color4f().set(1, 1, 1);
        _myBox.material().transparent = false;
//        _myNode.add(_myBox);

        /* create a screengrabber */
        FrameBufferCopy myFrameBufferCopy = new FrameBufferCopy(myTexture);
        myFrameBufferCopy.backgroundcolor.set(0.2f, 1);
        myFrameBufferCopy.width = width;
        myFrameBufferCopy.height = height;
        gestalt.bin(Gestalt.BIN_3D_SETUP).add(myFrameBufferCopy);

        /* camera */
        gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        gestalt.camera().upvector().set(0, 0, 1);
        gestalt.camera().position().set( -400, 0, 480);
    }


    public void draw() {
        /* clear screen */
        background(127, 255, 0);

        /* draw processing stuff */
        fill(255, 255);
        stroke(0, 255);
        strokeWeight(20);
        rect(mouseX, mouseY, 200, 200);

        /* gestalt camera */
        CameraMover.handleKeyEvent(gestalt.camera(), gestalt.event(), 1 / 50f);

        /* rotate gestalt canvas */
        _myNode.rotation().z += -0.0024f;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingSketchesInSpace.class.getName()});
    }
}
