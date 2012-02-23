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


package gestalt.demo.basic;

import gestalt.Gestalt;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.FontProducer;

import data.Resource;


/**
 * this demo shows how to use the 'camera' object and the camera mover.
 */

public class UsingTheCamera
    extends AnimatorRenderer {

    private Plane[] _myFontPlane;

    private TexturePlugin[] _myFontTexture;

    private float _myCounter;

    public void setup() {
        /* configure renderer */
        framerate(60);
        displaycapabilities().backgroundcolor.set(0.5f);

        /*
         * there are different modes that define the camera behavior.
         *
         * CAMERA_MODE_ROTATE_XYZ
         * CAMERA_MODE_LOOK_AT
         * CAMERA_MODE_ROTATION_AXIS
         *
         * 'CAMERA_MODE_ROTATE_XYZ' lets the camera use three angles to define
         * it s rotation. note that rotation about axis is always order
         * dependent. in this case we first rotate around the X axis, then the
         * Y axis and finally the Z axis.
         *
         * 'CAMERA_MODE_LOOK_AT' lets the camera look at a point specified by
         * a 'lookat' position.
         *
         * 'CAMERA_MODE_ROTATION_AXIS' is rather raw. it lets the client
         * set the rotation values in the camera rotation matrix directly.
         */
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* create text planes for illustration purposes. */
        createTextPlanes();
    }


    private void createTextPlanes() {
        FontProducer _myFontProducer = Bitmaps.getFontProducer(
            Resource.getStream("demo/font/silkscreen/slkscr.ttf"));
        _myFontProducer.setSize(32);
        _myFontProducer.setQuality(Gestalt.FONT_QUALITY_HIGH);

        _myFontPlane = new Plane[10];
        _myFontTexture = new TexturePlugin[_myFontPlane.length];
        for (int i = 0; i < _myFontPlane.length; ++i) {
            System.out.println("### INFO / creating texture " + (i + 1) + "/" + _myFontPlane.length);

            _myFontTexture[i] = drawablefactory().texture();
            _myFontTexture[i].load(_myFontProducer.getBitmap("***** " + i + " *****"));

            _myFontPlane[i] = drawablefactory().plane();
            _myFontPlane[i].material().color4f().set( (float) i / (float) _myFontPlane.length);
            _myFontPlane[i].material().depthtest = false;
            _myFontPlane[i].material().addPlugin(_myFontTexture[i]);
            _myFontPlane[i].setPlaneSizeToTextureSize();
            _myFontPlane[i].transform().translation.z = i * -10;
            bin(BIN_3D).add(_myFontPlane[i]);
        }
    }


    public void loop(float theDeltaTime) {
        /**
         * move the camera according to the pressed key.
         *
         * note the existence of the class 'gestalt.util.CameraMover' which can
         * handle a set of camera properties conveniently with one line of code:
         *
         *    CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
         *
         * replace 'moveCamera' with 'CameraMover.handleKeyEvent' to use the
         * camera mover. also use it as code reference.
         */
        moveCamera(theDeltaTime);

        /* rotate the text planes for illustration purposes. */
        rotateTextPlanes();
    }


    private void moveCamera(float theDeltaTime) {
        float mySpeed = 300f * theDeltaTime;
        /* move camera */
        if (event().keyCode == KEYCODE_A) {
            camera().forward(mySpeed);
        }
        if (event().keyCode == KEYCODE_Q) {
            camera().forward( -mySpeed);
        }
        if (event().keyCode == KEYCODE_LEFT) {
            camera().side( -mySpeed);
        }
        if (event().keyCode == KEYCODE_RIGHT) {
            camera().side(mySpeed);
        }
        if (event().keyCode == KEYCODE_DOWN) {
            camera().up( -mySpeed);
        }
        if (event().keyCode == KEYCODE_UP) {
            camera().up(mySpeed);
        }
        if (event().keyCode == KEYCODE_W) {
            camera().fovy += mySpeed;
        }
        if (event().keyCode == KEYCODE_S) {
            camera().fovy -= mySpeed;
        }
    }


    private void rotateTextPlanes() {
        _myCounter += 0.01f;
        for (int i = 0; i < _myFontPlane.length; ++i) {
            _myFontPlane[i].rotation().x = (_myCounter * (float) i / _myFontPlane.length * 1.45f);
            _myFontPlane[i].rotation().y = (_myCounter * (float) i / _myFontPlane.length * 1.25f);
        }
    }


    public static void main(String[] arg) {
        new UsingTheCamera().init();
    }
}
