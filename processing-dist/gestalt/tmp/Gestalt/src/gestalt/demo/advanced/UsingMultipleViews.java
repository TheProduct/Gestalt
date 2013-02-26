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


import java.util.Vector;

import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;

import mathematik.Random;


public class UsingMultipleViews
    extends AnimatorRenderer {

    private RenderBin _myCubeBin;

    private final Vector<Camera> _myCameras = new Vector<Camera> ();

    private int _myCameraID;

    public void setup() {
        cameramover(true);
        fpscounter(true);
        framerate(UNDEFINED);
        bin(BIN_3D_SETUP).remove(camera());
        replaceCamera(null);

        _myCubeBin = new RenderBin(1000);

        {
            final Camera myCamera = drawablefactory().camera();
            myCamera.add(_myCubeBin);
            myCamera.viewport().width = 200;
            myCamera.viewport().x = 0;
            myCamera.setMode(CAMERA_MODE_LOOK_AT);
            myCamera.position().y = 200;
            myCamera.position().x = -100;
            bin(BIN_3D_SETUP).add(myCamera);
            _myCameras.add(myCamera);
        }
        {
            final Camera myCamera = drawablefactory().camera();
            myCamera.plugins().add(drawablefactory().fog());
            myCamera.add(_myCubeBin);
            myCamera.viewport().width = 200;
            myCamera.viewport().x = 220;
            myCamera.setMode(CAMERA_MODE_LOOK_AT);
            myCamera.position().y = 200;
            bin(BIN_3D_SETUP).add(myCamera);
            _myCameras.add(myCamera);
        }
        {
            final Camera myCamera = drawablefactory().camera();
            myCamera.add(_myCubeBin);
            myCamera.viewport().width = 200;
            myCamera.viewport().x = 440;
            myCamera.setMode(CAMERA_MODE_LOOK_AT);
            myCamera.position().y = 100;
            myCamera.position().x = 150;
            bin(BIN_3D_SETUP).add(myCamera);
            _myCameras.add(myCamera);
        }

        /* create world and add it to the sharedbin(BIN_3D) bin */
        for (int i = 0; i < 1000; i++) {
            Cuboid myCube = drawablefactory().cuboid();
            myCube.origin(SHAPE_ORIGIN_BOTTOM_CENTERED);
            myCube.position().x = new Random().getFloat( -1000, 1000);
            myCube.position().z = new Random().getFloat( -1000, 1000);
            float myRatio = (1 - Math.abs(myCube.position().x / 1000f)) + (1 - Math.abs(myCube.position().z / 1000f));
            myRatio *= 0.5f;
            myCube.scale().x = myRatio * 50;
            myCube.scale().y = myRatio * myRatio * 200;
            myCube.scale().z = myRatio * 50;
            _myCubeBin.add(myCube);
        }

        Plane myPlane = drawablefactory().plane();
        myPlane.scale().set(2000, 2000);
        myPlane.rotation().x = PI_HALF;
        myPlane.material().color4f().set(1, 0, 0, 1);
        _myCubeBin.add(myPlane);

        switchCamera();
    }


    public void keyPressed(char theKey, int theKeyCode) {
        if (theKey == ' ') {
            switchCamera();
        }
    }


    private void switchCamera() {
        _myCameraID++;
        _myCameraID %= _myCameras.size();
        setCameraRef(_myCameras.get(_myCameraID));
    }


    public static void main(String[] args) {
        new UsingMultipleViews().init();
    }
}
