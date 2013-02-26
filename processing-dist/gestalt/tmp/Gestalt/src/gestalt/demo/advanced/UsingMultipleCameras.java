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


import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.Camera;
import gestalt.shape.Cuboid;
import gestalt.util.CameraMover;


/**
 * this demo shows how to use multiple cameras.
 */


public class UsingMultipleCameras
    extends AnimatorRenderer {

    private Camera[] _myCameras;

    private Cuboid _mySmallCube;

    private Cuboid _myBigCube;

    private int _myCurrentCamera;

    public void setup() {
        /* gestalt */
        displaycapabilities().backgroundcolor.set(0.2f);

        /* cubes */
        _mySmallCube = drawablefactory().cuboid();
        _mySmallCube.position().set(50, 100, 0);
        _mySmallCube.scale().set(50, 50, 50);
        bin(BIN_3D).add(_mySmallCube);

        _myBigCube = drawablefactory().cuboid();
        _myBigCube.position().set( -50, 0, 0);
        _myBigCube.scale().set(100, 100, 100);
        bin(BIN_3D).add(_myBigCube);

        /* cameras */
        _myCameras = new Camera[3];
        _myCameras[0] = camera();
        _myCameras[0].setMode(CAMERA_MODE_LOOK_AT);

        _myCameras[1] = drawablefactory().camera();
        _myCameras[1].setMode(CAMERA_MODE_LOOK_AT);
        _myCameras[1].position().set(100, 200, 200);
        _myCameras[1].viewport().width = displaycapabilities().width;
        _myCameras[1].viewport().height = displaycapabilities().height;

        _myCameras[2] = drawablefactory().camera();
        _myCameras[2].setMode(CAMERA_MODE_LOOK_AT);
        _myCameras[2].position().set( -100, 200, 500);
        _myCameras[2].viewport().x = displaycapabilities().width / 4;
        _myCameras[2].viewport().y = displaycapabilities().height / 4;
        _myCameras[2].viewport().width = displaycapabilities().width / 2;
        _myCameras[2].viewport().height = displaycapabilities().height / 2;
    }


    public void loop(float theDeltaTime) {
        CameraMover.handleKeyEvent(_myCameras[_myCurrentCamera], event(), theDeltaTime);

        if (event().mouseClicked) {
            _myCurrentCamera++;
            _myCurrentCamera %= _myCameras.length;
            replaceCamera(_myCameras[_myCurrentCamera]);
        }
    }


    public static void main(String[] args) {
        new UsingMultipleCameras().init();
    }
}
