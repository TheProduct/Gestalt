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

import gestalt.candidates.JoglAccumulationBuffer;
import gestalt.context.DisplayCapabilities;
import gestalt.context.JoglDisplay;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;

import mathematik.Random;


public class UsingAccumulationBuffer
        extends AnimatorRenderer {

    public void setup() {
        cameramover(true);
        fpscounter(true);
        framerate(60);

        RenderBin _myCubeBin = new RenderBin(1000);

        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().y = 200;
        camera().position().x = -100;

        /* cubes */
        for (int i = 0; i < 1000; i++) {
            Cuboid myCube = drawablefactory().cuboid();
            myCube.origin(SHAPE_ORIGIN_BOTTOM_CENTERED);
            myCube.position().x = new Random().getFloat(-1000, 1000);
            myCube.position().z = new Random().getFloat(-1000, 1000);
            float myRatio = (1 - Math.abs(myCube.position().x / 1000f)) + (1 - Math.abs(myCube.position().z / 1000f));
            myRatio *= 0.5f;
            myCube.scale().x = myRatio * 50;
            myCube.scale().y = myRatio * myRatio * 200;
            myCube.scale().z = myRatio * 50;
            _myCubeBin.add(myCube);
        }

        /* floor */
        Plane myPlane = drawablefactory().plane();
        myPlane.scale().set(2000, 2000);
        myPlane.rotation().x = PI_HALF;
        myPlane.material().color4f().set(1, 0, 0, 1);
        _myCubeBin.add(myPlane);

        /* accumulation buffer */
        JoglAccumulationBuffer myBuffer = new JoglAccumulationBuffer(_myCubeBin);
        bin(BIN_3D).add(myBuffer);
    }

    public DisplayCapabilities createDisplayCapabilities() {
        final DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();

        return myDisplayCapabilities;
    }

    public static void main(String[] args) {
        JoglDisplay.ENABLE_ACCUMULATION_BUFFER = true;
        new UsingAccumulationBuffer().init();
    }
}
