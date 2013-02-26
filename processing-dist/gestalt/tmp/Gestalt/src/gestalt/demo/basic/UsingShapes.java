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
import gestalt.shape.Mesh;
import gestalt.shape.Plane;

import mathematik.Vector3f;


/**
 * this demo shows how to use the drawablefactory to create shapes,
 * and of course how to use shapes.
 */

public class UsingShapes
    extends AnimatorRenderer {

    /* reference to our plane */
    private Plane _myPlane;

    /* reference to our line */
    private Line _myLine;

    /* reference to our mesh */
    private Mesh _myMesh;

    public void setup() {
        /*
         * change background color4f of window to white
         * see also 'UsingDisplay'
         */
        displaycapabilities().backgroundcolor.set(1);

        createPlane();
        createLine();
        createMesh();
    }


    private void createPlane() {
        /* gestalt uses a drawablefactory to create shapes.
         * in a more advanced demo we will see how custom shapes can be created
         * without using the drawablefactory().
         */
        _myPlane = drawablefactory().plane();

        /* each shape has a transform. a transform is a matrix that defines
         * the shapes position (translation) and rotation in space.
         * you can also use position() to define the translation in the
         * transform matrix.
         * note that 'rotation()' and 'scale()' do not affect the
         * transform matrix.
         * also see 'AbstractShape.rotation()' and 'AbstractShape.scale()'
         */
        _myPlane.transform().translation.z = -20;

        /* each shape also has a seperate scale */
        _myPlane.scale().x = 200;
        _myPlane.scale().y = 200;

        /* a shape can have different origins
         *    SHAPE_ORIGIN_BOTTOM_LEFT
         *    SHAPE_ORIGIN_BOTTOM_RIGHT
         *    SHAPE_ORIGIN_TOP_LEFT
         *    SHAPE_ORIGIN_TOP_RIGHT
         *    SHAPE_ORIGIN_CENTERED
         */
        _myPlane.origin(SHAPE_ORIGIN_CENTERED);

        /* a plane has a material. the material manages things like the shapes
         * color4f, the transparency, the blendmode and the texture.
         */
        _myPlane.material().color4f().set(0.8f, 0.8f, 0.8f, 1f);
        _myPlane.material().transparent = true;
        _myPlane.material().colormasking = true;

        /* finally add the shape to the renderer. */
        bin(BIN_3D).add(_myPlane);
    }


    private void createLine() {
        /* create a line with the drawablefactory */
        _myLine = drawablefactory().line();
        _myLine.material().color4f().set(0f, 0f, 0f, 1f);

        /* define the points of the line. a line can have an arbitrary
         * number of points and each vertex can have its own color4f
         */
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

        _myLine.linewidth = 5;
        _myLine.smooth = true;
        _myLine.stipple = true;
        _myLine.setStipplePattern("1100110011001100");

        /* add line to renderer */
        bin(BIN_3D).add(_myLine);
    }


    private void createMesh() {
        /* data to be uploaded */
        float[] myVertices = new float[] {
                             -50, -50, 0, 50, -50, 0, 50, 50, 0, -50, 50, 0};

        float[] myColors = new float[] {
                           0.5f, 0.5f, 0.5f, 0.6f, 0.6f, 0.6f, 0.7f, 0.7f, 0.7f, 0.8f, 0.8f, 0.8f};

        float[] myNormals = new float[] {
                            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};

        float[] myTexCoords = new float[] {
                              0, 0, 1, 0, 1, 1, 0, 1};

        /* create mesh */
        _myMesh = drawablefactory().mesh(true,
                                         myVertices, 3,
                                         myColors, 3,
                                         myTexCoords, 2,
                                         myNormals,
                                         MESH_QUADS);

        /* add mesh to renderer */
        bin(BIN_3D).add(_myMesh);
    }


    public static void main(String[] args) {
        new UsingShapes().init();
    }
}
