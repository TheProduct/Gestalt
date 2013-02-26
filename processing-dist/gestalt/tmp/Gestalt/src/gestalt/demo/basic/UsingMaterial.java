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
import gestalt.material.Color;
import gestalt.shape.Line;
import gestalt.shape.Plane;

import mathematik.Vector3f;


/**
 * this demo shows how to use a material and its plugins.
 */
public class UsingMaterial
        extends AnimatorRenderer {

    /* reference to our plane */
    private Plane _myPlane;

    /* reference to our line */
    private Line _myLine;

    public void setup() {
        displaycapabilities().backgroundcolor.set(1);

        createPlane();
        _myPlane.material().color4f().set(0.8f, 0.8f, 0.8f, 1f);
        _myPlane.material().transparent = true;

        createLine();
        _myLine.material().color4f().set(0f, 0f, 0f, 1f);
        _myLine.linewidth = 5;
        _myLine.smooth = true;
        _myLine.stipple = true;
        _myLine.setStipplePattern("1100110011001100");
    }

    private void createPlane() {
        _myPlane = drawablefactory().plane();
        _myPlane.transform().translation.z = -20;
        _myPlane.scale().set(200, 200);

        bin(BIN_3D).add(_myPlane);
    }

    private void createLine() {
        _myLine = drawablefactory().line();
        _myLine.linewidth = 1.0f;
        _myLine.points = new Vector3f[500];
        _myLine.colors = new Color[500];
        for (int i = 0; i < _myLine.points.length; i++) {
            _myLine.points[i] = new Vector3f(Math.random() * 300 - 150,
                                             Math.random() * 300 - 150,
                                             0);
            _myLine.colors[i] = new Color(1f,
                                          1f,
                                          1f,
                                          new mathematik.Random().getFloat(0, 0.5f));
        }

        bin(BIN_3D).add(_myLine);
    }

    public static void main(String[] args) {
        new UsingMaterial().init();
    }
}
