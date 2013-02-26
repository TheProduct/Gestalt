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


package gestalt.shape;


import gestalt.material.Material;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.util.JoglUtil;
import java.util.Vector;

import javax.media.opengl.GL;


public class Triangles
        extends AbstractShape {

    private Vector<Triangle> _myTriangles = new Vector<Triangle>();

    public Triangles() {
        material = new Material();
    }

    public Vector<Triangle> triangles() {
        return _myTriangles;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* begin material */
        material.begin(theRenderContext);

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* draw shape */
        gl.glBegin(GL.GL_TRIANGLES);
        for (final Triangle myTriangle : triangles()) {
            JoglUtil.draw(gl, myTriangle.a());
            JoglUtil.draw(gl, myTriangle.b());
            JoglUtil.draw(gl, myTriangle.c());
        }
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);

        /* draw children */
        drawChildren(theRenderContext);
    }
}
