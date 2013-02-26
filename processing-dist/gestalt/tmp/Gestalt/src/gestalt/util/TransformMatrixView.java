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


package gestalt.util;


import static gestalt.Gestalt.*;
import gestalt.context.GLContext;
import gestalt.shape.AbstractShape;
import gestalt.material.Color;
import gestalt.shape.DrawableFactory;
import gestalt.shape.Line;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class TransformMatrixView
    extends AbstractShape {

    private Line myLines;

    public float axisscale;

    private TransformMatrix4f _myMatrix;

    public TransformMatrixView(DrawableFactory theFactory, TransformMatrix4f theMatrix) {

        axisscale = 20;

        myLines = theFactory.line();
        myLines.points = new Vector3f[] {
                         new Vector3f(),
                         new Vector3f(),
                         new Vector3f(),
                         new Vector3f(),
                         new Vector3f(),
                         new Vector3f()};
        myLines.colors = new Color[] {
                         new Color(1, 0, 0),
                         new Color(1, 0, 0),
                         new Color(0, 1, 0),
                         new Color(0, 1, 0),
                         new Color(0, 0, 1),
                         new Color(0, 0, 1)};
        myLines.setPrimitive(LINE_PRIMITIVE_TYPE_LINES);

        _myMatrix = theMatrix;
    }


    private void update() {
        for (int i = 0; i < myLines.points.length; i++) {
            myLines.points[i].set(_myMatrix.translation);
        }

        myLines.points[1].set(_myMatrix.rotation.getXAxis());
        myLines.points[3].set(_myMatrix.rotation.getYAxis());
        myLines.points[5].set(_myMatrix.rotation.getZAxis());
        myLines.points[1].scale(axisscale);
        myLines.points[3].scale(axisscale);
        myLines.points[5].scale(axisscale);
        myLines.points[1].add(_myMatrix.translation);
        myLines.points[3].add(_myMatrix.translation);
        myLines.points[5].add(_myMatrix.translation);
    }


    public void draw(GLContext theRenderContext) {
        update();
        myLines.draw(theRenderContext);
    }


    public boolean isActive() {
        return myLines.isActive();
    }
}
