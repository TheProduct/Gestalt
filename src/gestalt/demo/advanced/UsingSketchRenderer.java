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
import gestalt.render.SketchRenderer;

import mathematik.Vector3f;


public class UsingSketchRenderer
        extends SketchRenderer {

    private float _myCounter;

    public void setup() {
        bin(BIN_3D).add(g());
    }

    public void loop(float theDeltaTime) {

        _myCounter += theDeltaTime;

        g().line(10, 10, 20, 20);

        g().color().set(1, 0.1f);
        final float NUMBER_OF_LINES = 200;
        for (int i = 0; i < NUMBER_OF_LINES; i++) {
            final float myValue = i / NUMBER_OF_LINES * TWO_PI;
            final Vector3f myCircle = new Vector3f((float)Math.sin(_myCounter * 0.33f + myValue) * 100f,
                                                   (float)Math.cos(_myCounter + myValue) * 100f,
                                                   0);
            g().line(myCircle,
                     new Vector3f(event().mouseX,
                                  event().mouseY,
                                  0));
            g().line(myCircle,
                     new Vector3f(-event().mouseX,
                                  -event().mouseY,
                                  0));
            g().line(myCircle,
                     new Vector3f(event().mouseX,
                                  -event().mouseY,
                                  0));
            g().line(myCircle,
                     new Vector3f(-event().mouseX,
                                  event().mouseY,
                                  0));
        }
    }

    public DisplayCapabilities createDisplayCapabilities() {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.antialiasinglevel = 4;
        myDisplayCapabilities.backgroundcolor.set(0.2f);
        return myDisplayCapabilities;
    }

    public static void main(String[] args) {
        new UsingSketchRenderer().init();
    }
}
