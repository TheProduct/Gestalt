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

import gestalt.util.JoglUtil;

import javax.media.opengl.GL;


/** @todo create display lists for each origin. */
public abstract class AtomPlane {

    public static boolean USE_DISPLAY_LISTS = false;

    private static boolean _myIsCompiled = false;

    private static int _myDisplayList;

    public static boolean MULTITEX_3 = false;

    public static final void draw(final GL gl, final int theOrigin) {
        /* translate origin */
        gl.glPushMatrix();
        JoglUtil.applyOrigin(gl, theOrigin);

        if (USE_DISPLAY_LISTS) {
            if (!_myIsCompiled) {
                _myIsCompiled = true;
                _myDisplayList = gl.glGenLists(1);
                gl.glNewList(_myDisplayList, GL.GL_COMPILE);
                if (MULTITEX_3) {
                    drawMultiTexPlane(gl);
                } else {
                    drawPlane(gl);
                }
                gl.glEndList();
            }

            if (_myIsCompiled) {
                /* call display list */
                gl.glCallList(_myDisplayList);
            }
        } else {
            if (MULTITEX_3) {
                drawMultiTexPlane(gl);
            } else {
                drawPlane(gl);
            }
        }
    }

    private static void drawMultiTexPlane(GL gl) {
        /* draw plane */
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0, 0, 1);

        gl.glMultiTexCoord2f(0, 0, 0);
        gl.glMultiTexCoord2f(1, 0, 0);
        gl.glMultiTexCoord2f(2, 0, 0);
        gl.glVertex2f(0, 0);

        gl.glMultiTexCoord2f(0, 0, 0);
        gl.glMultiTexCoord2f(1, 0, 0);
        gl.glMultiTexCoord2f(2, 0, 0);
        gl.glVertex2f(1, 0);

        gl.glMultiTexCoord2f(0, 1, 1);
        gl.glMultiTexCoord2f(1, 1, 1);
        gl.glMultiTexCoord2f(2, 1, 1);
        gl.glVertex2f(1, 1);

        gl.glMultiTexCoord2f(0, 0, 1);
        gl.glMultiTexCoord2f(1, 0, 1);
        gl.glMultiTexCoord2f(2, 0, 1);
        gl.glVertex2f(0, 1);

        gl.glEnd();

        gl.glPopMatrix();
    }

    private static void drawPlane(GL gl) {
        /* draw plane */
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0, 0, 1);

        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(0, 0);

        gl.glTexCoord2f(1, 0);
        gl.glVertex2f(1, 0);

        gl.glTexCoord2f(1, 1);
        gl.glVertex2f(1, 1);

        gl.glTexCoord2f(0, 1);
        gl.glVertex2f(0, 1);

        gl.glEnd();

        gl.glPopMatrix();
    }

    public static final void cleanup(GL gl) {
        if (USE_DISPLAY_LISTS) {
            gl.glDeleteLists(_myDisplayList, 1);
        }
    }
}
