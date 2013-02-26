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
import gestalt.render.bin.Bin;
import gestalt.shape.Cuboid;

import processing.core.PApplet;


public class LessonXX_UsingBins
        extends PApplet {

    private Bin bin;

    public void setup() {
        size(640, 480, OPENGL);
        G5.setup(this);

        /* this bin is by default BIN_3D. there are many different bins in gestalt for example BIN_2D_BACKGROUND, BIN_3D, or BIN_2D_FOREGROUND */
        bin = G5.default_bin(); /* in this case the same as 'G5.gestalt().bin(Gestalt.BIN_3D)' */

        for (int i = 0; i < 10; i++) {
            Cuboid mCubeRed = new Cuboid();
            mCubeRed.scale(100, 100, 100);
            mCubeRed.position(width / 2, height / 2);
            mCubeRed.material().color4f().set((float)i / 5.0f, 1, 0);
            bin.add(mCubeRed);
        }

        /* dump bin */
        println(bin);
    }

    public void draw() {
        background(255);
        for (int i = 0; i < bin.size(); i++) {
            Cuboid mCube = (Cuboid)bin.getDataRef()[i];
            mCube.rotation().x += 0.001 * (i + 1);
            mCube.rotation().y += 0.0003 * (i + 1);
        }
    }

    public static void main(String[] args) {
        G5.init_processing(LessonXX_UsingBins.class);
    }
}
