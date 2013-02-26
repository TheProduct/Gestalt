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


package gestalt.render.controller;

import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;

import mathematik.Vector2f;
import mathematik.Vector3f;

import javax.media.opengl.GL;


public class Origin
        extends AbstractDrawable {

    public Vector3f position = new Vector3f();

    public Vector2f scale = new Vector2f(1, 1);

    public Vector3f rotation = new Vector3f();

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        gl.glTranslatef(position.x, position.y, 0);

        /** @todo replace with faster 'to degree' calculation */
        if (rotation.x != 0.0f) {
            gl.glRotatef((float)Math.toDegrees(rotation.x), 1, 0, 0);
        }
        if (rotation.y != 0.0f) {
            gl.glRotatef((float)Math.toDegrees(rotation.y), 0, 1, 0);
        }
        if (rotation.z != 0.0f) {
            gl.glRotatef((float)Math.toDegrees(rotation.z), 0, 0, 1);
        }

        gl.glScalef(scale.x, scale.y, 1);
    }
}
