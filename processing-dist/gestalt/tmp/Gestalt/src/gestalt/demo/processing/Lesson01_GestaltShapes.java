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


package gestalt.demo.processing;

import gestalt.processing.G5;
import gestalt.shape.Cuboid;

import processing.core.PApplet;


public class Lesson01_GestaltShapes
        extends PApplet {

    private Cuboid mCube;

    public void setup() {
        size(640, 480, OPENGL);
        G5.setup(this);

        mCube = G5.cuboid();
        mCube.scale(100, 100, 100);
        mCube.position(width / 2, height / 2);
    }

    public void draw() {
        background(0, 127, 255);
        mCube.rotation().x += 0.01;
        mCube.rotation().y += 0.003;
    }

    public static void main(String[] args) {
        G5.init_processing(Lesson01_GestaltShapes.class);
    }
}
