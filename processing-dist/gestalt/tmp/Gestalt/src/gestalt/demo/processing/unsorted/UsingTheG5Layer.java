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


package gestalt.demo.processing.unsorted;

import gestalt.Gestalt;
import gestalt.processing.G5;

import data.Resource;
import processing.core.PApplet;


/**
 * this demo shows how to use the G convinience layer. this demo is simplified
 * version of the already simple 'UsingANiceModel' demo.
 */

public class UsingTheG5Layer
    extends PApplet {

    public void setup() {
        /* first create the window, than start gestalt */
        size(640, 480, OPENGL);
        G5.setup(this);
//        G5.fullscreen(true);

        /* load model */
        G5.model(Resource.getStream("demo/common/person.obj"),
                 Resource.getStream("demo/common/person.png"));

        /* camera */
        G5.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        G5.camera().lookat().set(0, 150, 0);
    }


    public void draw() {
        background(0, 127, 255);

        /* update the cameramover */
        G5.cameramover(1.0f / frameRate);
    }


    public static void main(String[] args) {
        /* start gestalt */
        G5.init_processing(UsingTheG5Layer.class);
    }
}
