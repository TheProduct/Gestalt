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


import gestalt.extension.quadline.QuadLine;
import gestalt.model.Model;
import gestalt.processing.G5;
import gestalt.shape.Cuboid;
import gestalt.shape.Disk;
import gestalt.shape.Mesh;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;

import mathematik.Vector3f;

import processing.core.PApplet;


public class UsingGestaltConvenienceLayer
    extends PApplet {

    private Cuboid myCube;

    private Model myModel;

    private Plane myPlane;

    private Mesh myMesh;

    private Disk myDisk;

    private QuadLine myQuadline;

    private TexturePlugin myTexture;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);

        /* intentionally causing an error. no plane object is returned. */
        println("### intentionally causing an error. no plane object is returned.");
        myPlane = G5.plane();

        /* gestalt */
        G5.setup(this);
        /** @todo 'fullscreen' seems to be broken. working on it */
//        G5.fullscreen(false);

        /* plane -- create a plane and put a texture on it from a file. */
        myPlane = G5.plane("data/demo/common/police.png");

        /* disk -- create a disk. */
        myDisk = G5.disk();

        /* cuboid -- create a cuboid and set its size to 150  */
        myCube = G5.cuboid();
        myCube.scale().set(150, 150, 150);

        /* texture -- create a texture and load its data from a file. */
        myTexture = G5.texture("data/demo/common/police.png");
        myCube.material().addTexture(myTexture);

        /* mesh -- create a mesh that  */
        float[] myVertices = new float[] {
                             -100, -100, 25,
                             100, -100, 50,
                             100, 100, 75,
                             -100, 100, 100};
        float[] myVertexColors = new float[] {
                                 1, 1, 1, 1,
                                 0.5f, 1, 0.5f, 1,
                                 1, 1, 1, 1,
                                 0.5f, 1, 0.5f, 1};
        myMesh = G5.mesh(false, myVertices, myVertexColors, G5.MESH_QUADS);

        /* model -- load a model from a file and store it in the internal mesh.
         * rescale the model to half the size.
         */
        myModel = G5.model("data/demo/common/weirdobject.obj", false);
        myModel.mesh().scale().set(0.5f, 0.5f, 0.5f);

        /* quadline */
        myQuadline = G5.quadline();
        myQuadline.points = new Vector3f[2];
        myQuadline.points[0] = new Vector3f(0, 0, 0);
        myQuadline.points[1] = new Vector3f(width, height, 0);
        myQuadline.update();
    }


    public void draw() {
        background(127, 255, 127);
        myPlane.position().set(mouseX, mouseY);
        myDisk.position().set(mouseX, mouseY);
        myCube.position().set(mouseX, mouseY);
        myMesh.position().set(mouseX, mouseY);
        myModel.mesh().position().set(mouseX, mouseY);
        myQuadline.points[1].set(mouseX, mouseY);

        /* deactivate drawing of a shape when mouse is pressed */
        myPlane.setActive(!mousePressed);
    }


    public void keyPressed() {
        /* remove the cuboid permanently from the renderer */
        G5.remove(myCube);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingGestaltConvenienceLayer.class.getName()});
    }
}
