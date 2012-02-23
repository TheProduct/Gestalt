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

import gestalt.Gestalt;
import gestalt.model.Model;
import gestalt.processing.G5;
import gestalt.shape.Cuboid;

import processing.core.PApplet;


/**
 * this lesson is not supposed to illustrate a certain functionality but rather functions as an overview of a few concepts.
 */
public class Lesson00_GettingStarted
        extends PApplet {

    Cuboid mCube;

    Model mModel;

    public void setup() {
        size(640, 480, OPENGL); /* gestalt obviously needs an OpenGL sketch to run properly */

        G5.setup(this); /* G5 is a convenience layer around gestalt */

        mCube = G5.cuboid(); /* G5 can create and add a shape to the default bin. usually creation, adding are two seperate steps */
        mCube.scale(100, 100, 100); /* shapes usually have at least a scale (i.e. size ) and a position */
        mCube.position(width / 2, height / 2); /* by default gestalt uses the coordinate system like processing */

        mModel = G5.model(createInput("person.obj"), createInput("person.png")); /* use data.Resource to locate files. */

        /* this is a selection of other easy to use shapes. For a complete list of prefabbed shapes look into 'gestalt.shape', 'gestalt.extension', and 'gestalt.candidates' */
        G5.disk();
        G5.mesh(false, new float[] {300, 200, 0, 340, 200, 0, 320, 280, 0}, Gestalt.MESH_TRIANGLES);
        G5.plane();
        G5.quad();
        G5.sphere();
        /* ... there are many other ways to create objects. check out the source code for reference ... */

    }

    public void draw() {
        background(0, 127, 255); /* you still need to clear the screen, using background. */
        /* by default all gestalt action happens after processing s draw loop is finished */

        mCube.position().set(mouseX, mouseY);
        mModel.mesh().position().set(mouseX, mouseY);
    }

    public static void main(String[] args) {
        G5.init_processing(Lesson00_GettingStarted.class); /* this is a convenient way to start the sketch */
    }
}
