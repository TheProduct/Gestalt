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

import javax.media.opengl.GL;

import static gestalt.Gestalt.*;


/** @todo create display lists for each origin. */
public class AtomCubeWrappedTexture {

    public static boolean USE_DISPLAY_LISTS = false;

    private static boolean _myIsCompiled = false;

    private static int _myDisplayList;

    public static final void draw(GL gl, int theOrigin) {

        /** @todo
         * instead of drawing the shape from scratch all the time
         * we should compile the shape into a display list or VBO.
         * this also means that we need to transform the texture
         * coordinates using the texturematrix stack.
         */

        /* translate origin */
        gl.glPushMatrix();
        switch (theOrigin) {
            case SHAPE_ORIGIN_BOTTOM_RIGHT:
                gl.glTranslatef(-0.5f, 0.5f, 0.5f);
                break;
            case SHAPE_ORIGIN_TOP_LEFT:
                gl.glTranslatef(0.5f, -0.5f, 0.5f);
                break;
            case SHAPE_ORIGIN_TOP_RIGHT:
                gl.glTranslatef(-0.5f, -0.5f, 0.5f);
                break;
            case SHAPE_ORIGIN_BOTTOM_LEFT:
                gl.glTranslatef(0.5f, 0.5f, 0.5f);
                break;
            case SHAPE_ORIGIN_CENTERED:
                break;
        }

        if (USE_DISPLAY_LISTS) {
            if (!_myIsCompiled) {
                _myIsCompiled = true;
                _myDisplayList = gl.glGenLists(1);
                gl.glNewList(_myDisplayList, GL.GL_COMPILE);
                drawCube(gl);
                gl.glEndList();
            }

            if (_myIsCompiled) {
                gl.glCallList(_myDisplayList);
            }
        } else {
            drawCube(gl);
        }
    }

    private static void drawCube(GL gl) {
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glNormal3f(0, 0, 1);
        gl.glTexCoord2f(0.33f, 0);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glTexCoord2f(0.66f, 0);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glTexCoord2f(0.66f, 0.33f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glTexCoord2f(0.33f, 0.33f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        // Back Face
        gl.glNormal3f(0, 0, -1);
        gl.glTexCoord2f(0.66f, 0.66f);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glTexCoord2f(0.66f, 1);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glTexCoord2f(0.33f, 1);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glTexCoord2f(0.33f, 0.66f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        // Top Face
        gl.glNormal3f(0, 1, 0);
        gl.glTexCoord2f(0.33f, 0.66f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glTexCoord2f(0.33f, 0.33f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glTexCoord2f(0.66f, 0.33f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glTexCoord2f(0.66f, 0.66f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        // Bottom Face
        gl.glNormal3f(0, -1, 0);
        gl.glTexCoord2f(0.33f, 1);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glTexCoord2f(0, 1);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glTexCoord2f(0, 0.66f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glTexCoord2f(0.33f, 0.66f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        // Right face
        gl.glNormal3f(1, 0, 0);
        gl.glTexCoord2f(1, 0.33f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glTexCoord2f(1, 0.66f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glTexCoord2f(0.66f, 0.66f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glTexCoord2f(0.66f, 0.33f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        // Left Face
        gl.glNormal3f(-1, 0, 0);
        gl.glTexCoord2f(0, 0.33f);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glTexCoord2f(0.33f, 0.33f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glTexCoord2f(0.33f, 0.66f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glTexCoord2f(0, 0.66f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();
    }

    public static final void cleanup(GL gl) {
        if (USE_DISPLAY_LISTS) {
            gl.glDeleteLists(_myDisplayList, 1);
        }
    }
}
