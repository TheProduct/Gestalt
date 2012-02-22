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


package gestalt.candidates;

import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.shape.Plane;
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;


public class JoglMultiTexPlane
        extends Plane {

    final int[] _myTextureUnit;

    public JoglMultiTexPlane(int... theTextureUnit) {
        _myTextureUnit = theTextureUnit;
        material = new Material();
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = (theRenderContext).gl;

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
        draw(gl, _myOrigin);

        /* finish drawing */
        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);

        /* draw children */
        drawChildren(theRenderContext);
    }

    private void glMultiTexCoord(final GL gl, final float theX, final float theY) {
        for (int i = 0; i < _myTextureUnit.length; i++) {
            gl.glMultiTexCoord2f(_myTextureUnit[i], theX, theY);
        }
    }

    private void draw(final GL gl, final int theOrigin) {
        /* translate origin */
        gl.glPushMatrix();
        JoglUtil.applyOrigin(gl, theOrigin);

        /* draw plane */
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0, 0, 1);

        glMultiTexCoord(gl, 0, 0);
        gl.glVertex2f(0, 0);

        glMultiTexCoord(gl, 1, 0);
        gl.glVertex2f(1, 0);

        glMultiTexCoord(gl, 1, 1);
        gl.glVertex2f(1, 1);

        glMultiTexCoord(gl, 0, 1);
        gl.glVertex2f(0, 1);

        gl.glEnd();

        gl.glPopMatrix();
    }
}
