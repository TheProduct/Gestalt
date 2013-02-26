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
import gestalt.util.FPSCounter;


/**
 * this demo shows how to use the FPS ( frames per second ) counter.
 */

public class UsingFPSCounter
    extends AnimatorRenderer {

    private FPSCounter _myFPSCounter;

    public void setup() {

        /* g1 */
        camera().culling = CAMERA_CULLING_BACKFACE;
        framerate(UNDEFINED);

        /* fps counter */
        _myFPSCounter = new FPSCounter();

        /* set the interval of sampling */
        _myFPSCounter.setInterval(60);

        /* create and a view of the FPS sampler */
        bin(BIN_2D_FOREGROUND).add(_myFPSCounter.display());
        _myFPSCounter.display().position.set(displaycapabilities().width / -2 + 20, displaycapabilities().height / 2 - 20);
        _myFPSCounter.display().color.set(1);
    }


    public void loop(float theDeltaTime) {
        /* update fps counter */
        _myFPSCounter.loop();
    }


    public static void main(String[] args) {
        new UsingFPSCounter().init();
    }
}
