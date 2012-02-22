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


package gestalt.util.scenewriter;


import gestalt.render.AnimatorRenderer;
import gestalt.material.Material;
import gestalt.shape.Plane;
import gestalt.util.CameraMover;

import mathematik.Vector2f;
import mathematik.Vector3f;


public class TestPlaneWriter
    extends AnimatorRenderer {

    private int _myID;

    private Material _myMaterial;

    public void setup() {

        _myMaterial = drawablefactory().material();

        final int myScale = 10;
        for (int x = -30; x < 30; x++) {
            for (int y = -30; y < 30; y++) {
                makePlane(new Vector3f(x * myScale, 0, y * myScale),
                          new Vector2f(myScale, myScale),
                          y * y + x * x);
            }
        }
    }


    public void loop(float theDeltaTime) {
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
        if (event().keyPressed('s')) {
            new SceneWriter("../testplane" + _myID++ +".obj", bin(BIN_3D));
        }
    }


    private void makePlane(Vector3f thePosition, Vector2f theScale, float theRotation) {
        Plane myPlane = drawablefactory().plane();
        myPlane.setMaterialRef(_myMaterial);
        myPlane.position().set(thePosition);
        myPlane.scale().set(theScale);
        myPlane.rotation().x = theRotation / 1000 * (float) Math.PI;
        myPlane.rotation().y = theRotation / 1111 * (float) Math.PI;
        bin(BIN_3D).add(myPlane);
    }


    public static void main(String[] args) {
        new TestPlaneWriter().init();
    }
}
