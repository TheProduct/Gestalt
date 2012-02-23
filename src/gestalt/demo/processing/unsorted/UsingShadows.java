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
import gestalt.extension.shadow.JoglShadowMap;
import gestalt.extension.shadow.JoglShadowMapDisplay;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.Plane;
import gestalt.shape.Sphere;
import gestalt.util.CameraMover;

import processing.core.PApplet;


/**
 * this demo shows how to use the shadow extension.
 */

public class UsingShadows
    extends PApplet {

    private GestaltPlugIn gestalt;

    private JoglShadowMap _myShadowMapExtension;

    private Sphere mySphereA;

    private Sphere mySphereB;

    private Sphere mySphereC;

    private float _myCounter = 0;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);

        /* create gestalt */
        gestalt = new GestaltPlugIn(this);

        /* setup shadow map */
        final int myShadowMapWidth = width;
        final int myShadowMapHeight = height;
        _myShadowMapExtension = new JoglShadowMap(gestalt.light(),
                                                  myShadowMapWidth,
                                                  myShadowMapHeight,
                                                  true,
                                                  false);
        gestalt.bin(Gestalt.BIN_FRAME_SETUP).add(_myShadowMapExtension);

        /* this is a workaround for a state issue between openl, processing and gestalt */
        GestaltPlugIn.SKIP_FIRST_FRAME = true;

        /* create shapes and a floor */
        mySphereA = new Sphere();
        mySphereA.position().set(100, 50, -100);
        mySphereA.scale().set(100, 100, 100);
        mySphereA.material().lit = true;
        mySphereA.material().color4f().set(0.5f, 0.5f, 0, 1);

        mySphereB = new Sphere();
        mySphereB.position().set(0, 100, 0);
        mySphereB.scale().set(100, 100, 100);
        mySphereB.material().lit = true;
        mySphereB.material().color4f().set(1, 0, 0, 1);

        mySphereC = new Sphere();
        mySphereC.position().set(100, 350, 0);
        mySphereC.scale().set(100, 100, 100);
        mySphereC.material().lit = true;
        mySphereC.material().color4f().set(1, 0.5f, 0, 1);

        Plane myPlane = gestalt.drawablefactory().plane();
        myPlane.scale().set(1000, 1000, 1);
        myPlane.rotation().x = -Gestalt.PI_HALF;
        myPlane.material().lit = true;
        myPlane.material().color4f().set(1, 1);

        /* add shapes to bins */
        gestalt.bin(Gestalt.BIN_3D).add(mySphereA);
        gestalt.bin(Gestalt.BIN_3D).add(mySphereB);
        gestalt.bin(Gestalt.BIN_3D).add(mySphereC);
        gestalt.bin(Gestalt.BIN_3D).add(myPlane);

        /* add shapes to shadow extension */
        _myShadowMapExtension.addShape(mySphereA);
        _myShadowMapExtension.addShape(mySphereB);
        _myShadowMapExtension.addShape(mySphereC);
        _myShadowMapExtension.lightcamera.nearclipping = 100;
        _myShadowMapExtension.lightcamera.farclipping = 5000;

        /* light */
        gestalt.light().enable = true;
        gestalt.light().position().set(450, 720, 23);
        gestalt.light().diffuse.set(1, 1, 1, 1);
        gestalt.light().ambient.set(0, 0, 0, 1);

        /* camera() */
        gestalt.camera().position().set( -400, 1000, 1000);
        gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);

        /* create a display for the shadowmap */
        createShadowmapDisplay();
    }


    private void createShadowmapDisplay() {
        JoglShadowMapDisplay myDisplay = new JoglShadowMapDisplay(_myShadowMapExtension, width, height);
        myDisplay.scale().scale(0.25f);
        myDisplay.position().x = myDisplay.scale().x / 2;
        myDisplay.position().y = myDisplay.scale().y / 2;
        myDisplay.material().color4f().a = 0.75f;
        myDisplay.material().depthtest = false;
        gestalt.bin(Gestalt.BIN_2D_FOREGROUND).add(myDisplay);
    }


    public void draw() {
        /* clear screen */
        background(255, 255, 0);

        /* move camera */
        CameraMover.handleKeyEvent(gestalt.camera(), gestalt.event(), 1 / 60f);

        /* bounce spheres */
        _myCounter += 1 / 60f;
        mySphereA.position().y += sin(_myCounter * 1.5f) * 2;
        mySphereB.position().y += sin(_myCounter * 2.3f) * 4;
        mySphereC.position().y += sin(_myCounter * 2.5f) * 3;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingShadows.class.getName()});
    }
}
