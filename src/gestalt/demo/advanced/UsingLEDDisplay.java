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

import gestalt.G;
import gestalt.candidates.JoglLEDDisplay;
import gestalt.material.texture.Bitmaps;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.cameraplugins.ArcBall;
import gestalt.shape.Sphere;
import gestalt.util.CameraMover;

import mathematik.Random;
import mathematik.Vector3f;

import data.Resource;


public class UsingLEDDisplay
        extends AnimatorRenderer {

    private JoglLEDDisplay _myLEDDisplay;

    private float _myCounter;

    public void setup() {
        fpscounter(true);
        framerate(UNDEFINED);
        displaycapabilities().backgroundcolor.set(0.2f);

        camera().plugins().add(new ArcBall());

        final int myWidth = 256;
        final int myHeight = 128;

        _myLEDDisplay = JoglLEDDisplay.create(myWidth, myHeight,
                                              bin(BIN_FRAME_SETUP),
                                              Bitmaps.getBitmap(Resource.getStream("demo/common/particle-round.png")),
                                              true);
        _myLEDDisplay.addMask(Bitmaps.getBitmap(Resource.getStream("demo/common/mask256x128.png")));
        bin(BIN_3D).add(_myLEDDisplay);

        _myLEDDisplay.backgroundcolor().set(1.0f);
        _myLEDDisplay.scale().set(8, 8);
        _myLEDDisplay.getPointSpriteMaterialPlugin().minpointsize = 1;
        _myLEDDisplay.getPointSpriteMaterialPlugin().maxpointsize = 64;
        _myLEDDisplay.getPointSpriteMaterialPlugin().pointsize = 20;

        for (int i = 0; i < _myLEDDisplay.points().size(); i++) {
            final Vector3f v = _myLEDDisplay.points().get(i);
            int x = i % myWidth;
            int y = i / myWidth;
            v.z += (float)Math.sin((float)x / (float)myWidth * TWO_PI * 2) * 75;
            v.z += (float)Math.sin((float)y / (float)myHeight * TWO_PI) * 50;
        }

        /* add shape to FBO */
        for (int i = 0; i < 13; i++) {
            final Sphere mySphere = G.sphere(_myLEDDisplay.bin());
            mySphere.scale(20, 20, 20);
            mySphere.material().color4f().set(0, 0.5f, 1.0f);
            mySphere.position().set(Random.FLOAT(-100, 100),
                                    Random.FLOAT(-100, 100));
        }
    }

    public void loop(final float theDeltaTime) {
        CameraMover.handleKeyEvent(_myLEDDisplay.camera(),
                                   event(),
                                   theDeltaTime);

        /* pulse background color4f */
        _myCounter += theDeltaTime;
        final float myColor = (float)Math.abs(Math.sin(_myCounter * 0.5f)) * 0.9f + 0.1f;
        _myLEDDisplay.backgroundcolor().set(myColor);
    }

    public static void main(String[] args) {
        G.init(UsingLEDDisplay.class);
    }
}
