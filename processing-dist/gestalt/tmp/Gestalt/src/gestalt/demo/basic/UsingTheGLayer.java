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

import gestalt.G;
import gestalt.render.AnimatorRenderer;

import data.Resource;


/**
 * this demo shows how to use the G convinience layer. this demo is simplified
 * version of the already simple 'UsingANiceModel' demo.
 */

public class UsingTheGLayer
    extends AnimatorRenderer {

    public void setup() {
        /* load model */
        G.model(Resource.getStream("demo/common/person.obj"),
                Resource.getStream("demo/common/person.png"));

        /* camera */
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().lookat().set(0, 150, 0);

        /* set background color */
        displaycapabilities().backgroundcolor.set(1, 0.5f, 0);
    }


    public void loop(final float theDeltaTime) {
        /* update the cameramover */
        G.cameramover(theDeltaTime);
    }


    public static void main(String[] args) {
        /* start gestalt */
        G.init(UsingTheGLayer.class);
    }
}
