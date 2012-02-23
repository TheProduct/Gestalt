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
import gestalt.extension.edgeblending.EdgeBlender;


public class UsingEdgeBlending
    extends AnimatorRenderer {

    private Cuboid _myCube;

    public void setup() {
        /* g1 */
        framerate(60);
        displaycapabilities().backgroundcolor.set(1f);

        /*
         * this is the convinient way to setup edgeblending.
         * for deeper inside dig into 'EdgeBlender.setup()'.
         */
        final int BLENDAREA = 50;
        EdgeBlender.setup(this, BLENDAREA);

        /* create cuboid */
        _myCube = drawablefactory().cuboid();
        _myCube.material().color4f().set(1, 0, 0);
        _myCube.scale().set(100, 100, 100);
        bin(BIN_3D).add(_myCube);
    }


    public void loop(final float theDeltaTime) {
        _myCube.rotation().x += theDeltaTime * 0.7f;
        _myCube.rotation().y += theDeltaTime * 0.3f;
    }


    public static void main(String[] args) {
        new UsingEdgeBlending().init();
    }
}
