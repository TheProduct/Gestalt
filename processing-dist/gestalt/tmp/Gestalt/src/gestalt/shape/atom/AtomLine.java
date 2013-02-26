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


package gestalt.shape.atom;

import gestalt.material.Color;

import mathematik.Vector3f;

import javax.media.opengl.GL;


public abstract class AtomLine {

    public static final void draw(GL gl,
                                  final Vector3f[] thePoints,
                                  final Color[] theColors,
                                  final float theLineWidth,
                                  final int thePrimitve) {

        gl.glLineWidth(theLineWidth);
        gl.glBegin(thePrimitve);

        for (int i = 0; i < thePoints.length; ++i) {
            /** @todo how do we implement a texture here? do we want one? */
            if (theColors != null) {
                Color myColor = theColors[i];
                gl.glColor4f(myColor.r, myColor.g, myColor.b, myColor.a);
            }
            if (thePoints[i] != null) {
                Vector3f myPoint = thePoints[i];
                gl.glVertex3f(myPoint.x, myPoint.y, myPoint.z);
            }
        }
        gl.glEnd();
    }
}
