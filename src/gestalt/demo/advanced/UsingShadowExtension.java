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

import gestalt.context.DisplayCapabilities;
import gestalt.extension.shadow.JoglMaterialPluginShadowCombiner;
import gestalt.extension.shadow.JoglMaterialPluginShadowController;
import gestalt.extension.shadow.JoglShadowMap;
import gestalt.extension.shadow.JoglShadowMapDisplay;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.Camera;
import gestalt.shape.Plane;
import gestalt.shape.Sphere;
import gestalt.util.CameraMover;

import data.Resource;


/**
 * this demo shows how to use the shadow extension.
 */
public class UsingShadowExtension
        extends AnimatorRenderer {

    private JoglShadowMap _myShadowMapExtension;

    private JoglMaterialPluginShadowController _myController;

    public DisplayCapabilities createDisplayCapabilities() {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.backgroundcolor.set(0.5f, 0.5f, 0.5f);
        myDisplayCapabilities.width = 800;
        myDisplayCapabilities.height = 600;
        return myDisplayCapabilities;
    }

    public void setup() {
        /* gestalt */
        framerate(60);

        /* setup shadow map */
        final int myShadowMapWidth = 512;
        final int myShadowMapHeight = 512;
        _myShadowMapExtension = new JoglShadowMap(light(), myShadowMapWidth, myShadowMapHeight, false, true);
        _myShadowMapExtension.shadowcolor.a = 0.5f;
        _myShadowMapExtension.lightcamera.nearclipping = 100;
        _myShadowMapExtension.lightcamera.farclipping = 5000;
        bin(BIN_FRAME_SETUP).add(_myShadowMapExtension);

        /* create plugin */
        _myController = new JoglMaterialPluginShadowController(_myShadowMapExtension);
        _myController.enableShadow();
        JoglMaterialPluginShadowCombiner myCombiner = new JoglMaterialPluginShadowCombiner(_myShadowMapExtension);

        /* create shapes */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/mask256.png")));

        Sphere mySphereA = new Sphere();
        mySphereA.position().set(100, 50, -100);
        mySphereA.scale().set(100, 100, 100);
        mySphereA.material().lit = true;
        mySphereA.material().color4f().set(1, 0, 0, 1);
        mySphereA.material().addPlugin(myCombiner);

        Sphere mySphereB = new Sphere();
        mySphereB.position().set(0, 100, 0);
        mySphereB.scale().set(100, 100, 100);
        mySphereB.material().lit = true;
        mySphereB.material().color4f().set(1, 0, 0, 1);
        mySphereB.material().addPlugin(myCombiner);
        mySphereB.material().addPlugin(myTexture);

        Sphere mySphereC = new Sphere();
        mySphereC.position().set(100, 350, 0);
        mySphereC.scale().set(100, 100, 100);
        mySphereC.material().lit = true;
        mySphereC.material().color4f().set(1, 0.5f, 0, 1);
        mySphereC.material().addPlugin(myCombiner);

        Plane myPlane = drawablefactory().plane();
        myPlane.scale().set(1000, 1000, 1);
        myPlane.rotation().x = -PI_HALF;
        myPlane.material().lit = true;
        myPlane.material().color4f().set(1, 1, 1, 1);
        myPlane.material().normalizenormals = true;
        myPlane.material().addPlugin(myTexture);
        myPlane.material().transparent = true;

        /**
         * @todo
         * there is an order issue with the combiner and controller used in combination.
         * if the combiner is plugged before the controller, the effect of switch off
         * the controller yields undesirable combinder results.
         */
        myPlane.material().addPlugin(_myController);
        myPlane.material().addPlugin(myCombiner);

        /* add shapes to bins */
        bin(BIN_3D).add(mySphereA);
        bin(BIN_3D).add(mySphereB);
        bin(BIN_3D).add(mySphereC);
        bin(BIN_3D).add(myPlane);

        _myShadowMapExtension.addShape(mySphereA);
        _myShadowMapExtension.addShape(mySphereB);
        _myShadowMapExtension.addShape(mySphereC);

        /* light */
        light().enable = true;
        light().position().set(450, 720, 230);
        light().diffuse.set(1, 1, 1, 1);
        light().ambient.set(0, 0, 0, 1);

        /* camera() */
        camera().position().set(-400, 1000, 1000);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* display shadowmap */
        JoglShadowMapDisplay myDisplay = new JoglShadowMapDisplay(_myShadowMapExtension,
                                                                  myShadowMapWidth,
                                                                  myShadowMapHeight);
        myDisplay.scale().scale(0.5f);
        myDisplay.material().color4f().a = 0.5f;
        myDisplay.position().set(displaycapabilities().width / -2,
                                 displaycapabilities().height / 2);
        myDisplay.position().x += myDisplay.scale().x / 2;
        myDisplay.position().y += myDisplay.scale().y / -2;
        bin(BIN_2D_FOREGROUND).add(myDisplay);
    }

    private boolean toggleCamera;

    public void loop(float theDeltaTime) {

        /* toggle camera()s */
        if (event().keyPressed && event().key == 'c') {
            System.out.println("toggled camera().");
            toggleCamera = !toggleCamera;
        }

        Camera myCamera;
        if (toggleCamera) {
            myCamera = _myShadowMapExtension.lightcamera;
        } else {
            myCamera = camera();
        }

        /* set shadow color4f */
        _myShadowMapExtension.shadowcolor.set((float)event().mouseX / (float)displaycapabilities().width);
        _myShadowMapExtension.shadowcolor.a = (float)event().mouseY / (float)displaycapabilities().height;

        /* use controller plugin */
        if (event().mouseClicked) {
            if (event().mouseButton == MOUSEBUTTON_LEFT) {
                _myController.enableShadow();
            }
            if (event().mouseButton == MOUSEBUTTON_RIGHT) {
                _myController.disableShadow();
            }
        }

        /* move camera */
        CameraMover.handleKeyEvent(myCamera, event(), theDeltaTime);

        /* toggle camera modes */
        if (event().keyPressed) {
            if (event().key == '1') {
                myCamera.setMode(CAMERA_MODE_LOOK_AT);
            }
            if (event().key == '2') {
                myCamera.setMode(CAMERA_MODE_ROTATE_XYZ);
            }
            if (event().key == 'i') {
                myCamera.frustumoffset.y += 0.1f;
            }
            if (event().key == 'k') {
                myCamera.frustumoffset.y -= 0.1f;
            }
            if (event().key == 'u') {
                myCamera.farclipping += 100f;
            }
            if (event().key == 'j') {
                myCamera.farclipping -= 100f;
            }
            if (event().key == 'z') {
                myCamera.nearclipping += 10f;
            }
            if (event().key == 'h') {
                myCamera.nearclipping -= 10f;
            }
        }
    }

    public static void main(String[] arg) {
        new UsingShadowExtension().init();
    }
}
