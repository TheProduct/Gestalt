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


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;


/**
 * this demo shows how to handle transparent shapes.
 */

public class UsingTransparentShapes
    extends AnimatorRenderer {

    private Plane[] _myTransparentPlanes;

    public void setup() {
        /* setup renderer */
        displaycapabilities().backgroundcolor.set(1);

        /*
         * to make a plane transparent asign an alpha value to the plane color4f.
         * alternativly a texture with an alpha channel can make a plane transparent.
         */
        _myTransparentPlanes = new Plane[3];
        for (int i = 0; i < _myTransparentPlanes.length; i++) {
            _myTransparentPlanes[i] = drawablefactory().plane();
            _myTransparentPlanes[i].scale().set(400, 400);
            _myTransparentPlanes[i].position().z -= i * 400;
            /*
             * it makes a great difference whether depth testing is enabled or not.
             * this property maps directly to the opengl depthtesting. either just
             * experiment with this setting or read the opengl redbook.
             */
            _myTransparentPlanes[i].material().depthtest = true;
            _myTransparentPlanes[i].material().color4f().set(0, i * 0.1f + 0.1f);
            bin(BIN_3D).add(_myTransparentPlanes[i]);
        }
    }


    public void loop(float theDeltaTime) {
        for (int i = 0; i < _myTransparentPlanes.length; i++) {
            _myTransparentPlanes[i].rotation().x += (i + 1) * theDeltaTime * 0.2f;
            _myTransparentPlanes[i].rotation().y += (i + 1) * theDeltaTime * 0.198f;
        }
    }


    public static void main(String[] args) {
        new UsingTransparentShapes().init();
    }
}
