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

import gestalt.render.controller.cameraplugins.Culling;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.cameraplugins.ArcBall;
import gestalt.shape.Cuboid;
import gestalt.shape.FastBitmapFont;

import mathematik.Random;


public class UsingCulling
        extends AnimatorRenderer {

    private Cuboid[] _myTransparentPlanes;

    private Culling _myCulling;

    private static final float CUBE_SCALE = 50;

    private boolean USE_SPHERE_TEST = false;

    private FastBitmapFont _myFont;

    public void setup() {
        framerate(UNDEFINED);
        camera().plugins().add(new ArcBall());
        fpscounter(true);
        displaycapabilities().backgroundcolor.set(0.2f);

        _myTransparentPlanes = new Cuboid[5000];
        for (int i = 0; i < _myTransparentPlanes.length; i++) {
            _myTransparentPlanes[i] = drawablefactory().cuboid();
            _myTransparentPlanes[i].scale().set(CUBE_SCALE * 0.025f, CUBE_SCALE * 0.025f, CUBE_SCALE);
            _myTransparentPlanes[i].position().set(
                    new Random().getFloat(-displaycapabilities().width / 2, displaycapabilities().width / 2),
                    new Random().getFloat(-displaycapabilities().height / 2, displaycapabilities().height / 2),
                    new Random().getFloat(0, 500));
            _myTransparentPlanes[i].material().color4f().set(1, new Random().getFloat(0.25f, 0.75f));
            bin(BIN_3D).add(_myTransparentPlanes[i]);
        }

        /* add culling object */
        _myCulling = new Culling();
        camera().plugins().add(_myCulling);

        /* stats */
        _myFont = new FastBitmapFont();
        _myFont.align = FastBitmapFont.CENTERED;
        bin(BIN_2D_FOREGROUND).add(_myFont);
    }

    public void loop(float theDeltaTime) {
        int myCounter = 0;
        for (int i = 0; i < _myTransparentPlanes.length; i++) {
            final boolean myVisible;
            if (USE_SPHERE_TEST) {
                myVisible = _myCulling.sphereInFrustum(_myTransparentPlanes[i].position(), CUBE_SCALE);
            } else {
                myVisible = _myCulling.pointInFrustum(_myTransparentPlanes[i].position());
            }
            _myTransparentPlanes[i].setActive(myVisible);
            myCounter += myVisible ? 1 : 0;
        }

        /* stats */
        _myFont.text = "DISPLAYING " + myCounter + " / " + _myTransparentPlanes.length + "";
    }

    public void keyPressed(final char theKey, final int theKeyCode) {
        if (theKeyCode == KEYCODE_SPACE) {
            USE_SPHERE_TEST = !USE_SPHERE_TEST;
        }
    }

    public static void main(String[] args) {
        new UsingCulling().init();
    }
}
