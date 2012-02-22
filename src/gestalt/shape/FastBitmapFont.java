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

import gestalt.context.GLContext;
import gestalt.material.Color;

import mathematik.Vector3f;

import com.sun.opengl.util.GLUT;

import javax.media.opengl.GL;


public class FastBitmapFont
        extends AbstractDrawable {

    public Vector3f position = new Vector3f();

    public String text;

    public int font = GLUT.BITMAP_HELVETICA_10;

    public Color color;

    private final GLUT glut = new GLUT();

    public int align;

    public boolean active = true;

    public static final int LEFT = 0;

    public static final int CENTERED = 1;

    public static final int RIGHT = 2;

    public FastBitmapFont() {
        text = new String();
        color = new Color(1, 1);
        align = LEFT;
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glColor4f(color.r, color.g, color.b, color.a);
        final int myLength;
        if (align == CENTERED) {
            myLength = glut.glutBitmapLength(font, text) / -2;
        } else if (align == RIGHT) {
            myLength = -glut.glutBitmapLength(font, text);
        } else {
            myLength = 0;
        }
        gl.glRasterPos3f(position.x + myLength, position.y, position.z);
        glut.glutBitmapString(font, text);
    }

    public boolean isActive() {
        return active;
    }
}
