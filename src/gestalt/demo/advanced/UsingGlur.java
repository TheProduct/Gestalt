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
import gestalt.extension.glur.OffscreenBlurContext;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cuboid;
import gestalt.util.CameraMover;

import mathematik.Vector2i;

import data.Resource;


public class UsingGlur
        extends AnimatorRenderer {

    private OffscreenBlurContext _myBlurContext;

    private Cuboid[] _myCube;

    private float _myCounter;

    public void setup() {
        /* g1 */
        framerate(120);

        /* create offscreen context */
        _myBlurContext = new OffscreenBlurContext(drawablefactory(),
                                                  bin(BIN_ARBITRARY),
                                                  bin(BIN_2D_FOREGROUND),
                                                  bin(BIN_FRAME_SETUP),
                                                  new Vector2i(1024, 512),
                                                  new Vector2i(512, 256),
                                                  Resource.getStream("demo/shader/simple.vsh"),
                                                  Resource.getStream("demo/shader/blur9x9.fsh"));
        _myBlurContext.blur().blurspread = 1f;
        _myBlurContext.blur().strength = 1.75f;
        _myBlurContext.camera().setMode(CAMERA_MODE_LOOK_AT);
        _myBlurContext.camera().farclipping = 420;

        /* create cuboid and add it to shape FBO */
        _myCube = new Cuboid[150];
        for (int i = 0; i < _myCube.length; i++) {
            _myCube[i] = drawablefactory().cuboid();
            _myCube[i].rotation().x = TWO_PI * -i / (float)_myCube.length;
            _myCube[i].rotation().y = TWO_PI * i / (float)_myCube.length;
            _myCube[i].rotation().z = TWO_PI * -i / (float)_myCube.length;
            _myCube[i].material().color4f().set(0, 0.5f, 1, 0.05f);
            _myCube[i].scale().set(500, 10, 10);
            _myBlurContext.bin().add(_myCube[i]);
        }

        /* create cuboid and add it to shape FBO */
        Cuboid myCube = drawablefactory().cuboid();
        myCube.material().color4f().set(1);
        myCube.scale().set(200, 200, 200);
        myCube.rotation().set(0.2, 0.5, 1.2);
        _myBlurContext.bin().add(myCube);

        /* fps counter */
        fpscounter(true);
    }

    public void mousePressed(int x, int y, int thePressedMouseButton) {
        System.out.println("### blur shader");
//        System.out.println("blursize " + _myBlurContext.blur().blursize);
        System.out.println("blurspread " + _myBlurContext.blur().blurspread);
        System.out.println("strength " + _myBlurContext.blur().strength);
    }

    public void loop(float theDeltaTime) {
        CameraMover.handleKeyEvent(_myBlurContext.camera(), event(), theDeltaTime);
        _myBlurContext.camera().side(theDeltaTime * 100);

        /* move cuboid */
        for (int i = 0; i < _myCube.length; i++) {
            _myCube[i].rotation().x -= 0.01f + theDeltaTime * 0.13f * i / (float)_myCube.length;
            _myCube[i].rotation().y -= 0.01f + theDeltaTime * 0.26f * i / (float)_myCube.length;
            _myCube[i].rotation().z -= 0.01f + theDeltaTime * 0.39f * i / (float)_myCube.length;
        }

        /* blur */
        _myCounter += theDeltaTime * 2;
//        _myBlurContext.blur().blurspread = 1f + (float) (Math.sin(_myCounter) + 1) * 0.5f;
//        _myBlurContext.blur().strength = 1f + (float) (Math.sin(_myCounter * 0.37f) + 1) * 0.5f;

        if (event().mouseDown) {
            _myBlurContext.blurdisplay().position().set(event().mouseX, event().mouseY);
        } else {
            _myBlurContext.blurdisplay().position().set(0, 0);
        }
        _myBlurContext.shapedisplay().material().color4f().set(1, 0.7f);
        _myBlurContext.blurdisplay().material().color4f().set(1, 0.9f);
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 1024;
        myDisplayCapabilities.height = 512;
        myDisplayCapabilities.backgroundcolor.set(0.2f);
        new UsingGlur().init(myDisplayCapabilities);
    }
}
