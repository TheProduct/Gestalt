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


package gestalt.model;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractShape;
import gestalt.material.Material;

import javax.media.opengl.GL;


public class BoundingBoxView
        extends AbstractShape {

    protected BoundingBoxData _myData;

    public BoundingBoxView(BoundingBoxData theData) {
        _myData = theData;
        material = new Material();
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        material.begin(theRenderContext);

        /* draw box */
        gl.glBegin(GL.GL_QUADS);

        /* front */
        gl.glVertex3f(_myData.p0.x, _myData.p0.y, _myData.p0.z);
        gl.glVertex3f(_myData.p1.x, _myData.p1.y, _myData.p1.z);
        gl.glVertex3f(_myData.p2.x, _myData.p2.y, _myData.p2.z);
        gl.glVertex3f(_myData.p3.x, _myData.p3.y, _myData.p3.z);

        /* right */
        gl.glVertex3f(_myData.p3.x, _myData.p3.y, _myData.p3.z);
        gl.glVertex3f(_myData.p2.x, _myData.p2.y, _myData.p2.z);
        gl.glVertex3f(_myData.p6.x, _myData.p6.y, _myData.p6.z);
        gl.glVertex3f(_myData.p7.x, _myData.p7.y, _myData.p7.z);

        /* to_myData.p */
        gl.glVertex3f(_myData.p4.x, _myData.p4.y, _myData.p4.z);
        gl.glVertex3f(_myData.p0.x, _myData.p0.y, _myData.p0.z);
        gl.glVertex3f(_myData.p3.x, _myData.p3.y, _myData.p3.z);
        gl.glVertex3f(_myData.p7.x, _myData.p7.y, _myData.p7.z);

        /* bottom */
        gl.glVertex3f(_myData.p5.x, _myData.p5.y, _myData.p5.z);
        gl.glVertex3f(_myData.p1.x, _myData.p1.y, _myData.p1.z);
        gl.glVertex3f(_myData.p2.x, _myData.p2.y, _myData.p2.z);
        gl.glVertex3f(_myData.p6.x, _myData.p6.y, _myData.p6.z);

        /* left */
        gl.glVertex3f(_myData.p4.x, _myData.p4.y, _myData.p4.z);
        gl.glVertex3f(_myData.p5.x, _myData.p5.y, _myData.p5.z);
        gl.glVertex3f(_myData.p1.x, _myData.p1.y, _myData.p1.z);
        gl.glVertex3f(_myData.p0.x, _myData.p0.y, _myData.p0.z);

        /* back */
        gl.glVertex3f(_myData.p4.x, _myData.p4.y, _myData.p4.z);
        gl.glVertex3f(_myData.p5.x, _myData.p5.y, _myData.p5.z);
        gl.glVertex3f(_myData.p6.x, _myData.p6.y, _myData.p6.z);
        gl.glVertex3f(_myData.p7.x, _myData.p7.y, _myData.p7.z);

        gl.glEnd();

        /* draw center */
        gl.glBegin(GL.GL_POINTS);
        gl.glVertex3f(_myData.center.x, _myData.center.y, _myData.center.z);
        gl.glEnd();

        material.end(theRenderContext);
    }
}
