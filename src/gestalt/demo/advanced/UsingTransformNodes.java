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
import gestalt.shape.Cuboid;
import gestalt.shape.Sphere;
import gestalt.shape.TransformNode;


public class UsingTransformNodes
    extends AnimatorRenderer {

    private TransformNode _myRoot;

    private Cuboid _myLocalCube;

    private Sphere _myWorldPosition;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        cameramover(true);

        light().enable = true;
        light().setPositionRef(camera().position());

        _myRoot = drawablefactory().transformnode();
        bin(BIN_3D).add(_myRoot);

        _myLocalCube = drawablefactory().cuboid();
        _myLocalCube.scale().set(100, 100, 100);
        _myLocalCube.material().lit = true;
        _myLocalCube.material().color4f().set(1, 0, 0, 0.5f);
        _myLocalCube.material().depthtest = false;
        _myLocalCube.position().set(_myLocalCube.scale());
        _myLocalCube.position().scale(0.5f);
        _myRoot.add(_myLocalCube);

        _myWorldPosition = drawablefactory().sphere();
        _myWorldPosition.scale().set(5, 5, 5);
        _myWorldPosition.material().lit = true;
        _myWorldPosition.material().color4f().set(1);
        _myWorldPosition.material().depthtest = false;
        bin(BIN_3D).add(_myWorldPosition);
    }


    public void loop(final float theDeltaTime) {
        if (event().mouseDown) {
            if (event().mouseButton == MOUSEBUTTON_LEFT) {
                _myRoot.rotation().x = event().normalized_mouseX * PI - PI_HALF;
                _myRoot.rotation().y = event().normalized_mouseY * PI - PI_HALF;
            }
            if (event().mouseButton == MOUSEBUTTON_RIGHT) {
                _myRoot.position().x = event().normalized_mouseX * width - width / 2;
                _myRoot.position().y = event().normalized_mouseY * height - height / 2;
            }
        }

        /* transform local position to world position */
        _myWorldPosition.position().set(_myLocalCube.position());
        _myRoot.transformLocalPositionToWorldPosition(_myWorldPosition.position());
    }


    public static void main(String[] args) {
        new UsingTransformNodes().init();
    }
}
