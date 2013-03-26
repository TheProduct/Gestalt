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


import javax.media.opengl.GL;


public class Triangle
        extends AbstractShape {

    private Vertex3f[] _myPoints = new Vertex3f[] {
        new Vertex3f(true),
        new Vertex3f(true),
        new Vertex3f(true)};

    public static int A = 0;

    public static int B = 1;

    public static int C = 2;

    public Triangle() {
        a().position.set(0, 0, 0);
        b().position.set(1, 0, 0);
        c().position.set(1, 1, 0);

        a().texcoord.set(0, 0, 0);
        b().texcoord.set(1, 0, 0);
        c().texcoord.set(1, 1, 0);

        material = new Material();
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
        JoglUtil.draw(gl, a());
        JoglUtil.draw(gl, b());
        JoglUtil.draw(gl, c());
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);

        /* draw children */
        drawChildren(theRenderContext);
    }

    public final Vertex3f a() {
        return _myPoints[A];
    }

    public final Vertex3f b() {
        return _myPoints[B];
    }

    public final Vertex3f c() {
        return _myPoints[C];
    }

    public Vertex3f[] vertices() {
        return _myPoints;
    }
}
