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


import gestalt.G;
import gestalt.model.Model;
import gestalt.render.AnimatorRenderer;

import data.Resource;


public class UsingANiceModel
    extends AnimatorRenderer {

    public void setup() {
        cameramover(true);
        fpscounter(true);
        framerate(UNDEFINED);

        /* load model */
        Model myModel = G.model(Resource.getStream("demo/common/person.obj"),
                                Resource.getStream("demo/common/person.png"),
                                true);
        myModel.mesh().material().lit = true;

        /* camera */
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().lookat().set(0, 150, 0);

        /* setup light */
        light().enable = true;
        light().setPositionRef(camera().position());
    }


    public void loop(final float theDeltaTime) {
    }


    public static void main(String[] args) {
        G.init(UsingANiceModel.class,
               G.createDisplayCapabilities(640, 480, 0.2f, 0.2f, 0.2f, false, true));
    }
}
