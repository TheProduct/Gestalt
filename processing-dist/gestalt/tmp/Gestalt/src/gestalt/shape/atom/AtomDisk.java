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

import static gestalt.Gestalt.*;


/** @todo create display lists for each origin. */
public class AtomDisk {

    public static boolean USE_DISPLAY_LISTS = true;

    private static boolean _myIsCompiled = false;

    private static int _myDisplayList;

    public static final void draw(GL gl, int theOrigin) {

        /* translate origin */
        gl.glPushMatrix();
        JoglUtil.applyOrigin(gl, theOrigin);

        if (USE_DISPLAY_LISTS) {
            if (!_myIsCompiled) {
                _myIsCompiled = true;
                _myDisplayList = gl.glGenLists(1);
                gl.glNewList(_myDisplayList, GL.GL_COMPILE);
                drawCircle(gl, 360 / 10);
                gl.glEndList();
            }

            if (_myIsCompiled) {
                /* call display list */
                gl.glCallList(_myDisplayList);
            }
        } else {
            drawCircle(gl, 360 / 10);
        }
    }

    private static void drawCircle(GL gl, float theRadius) {
        final float myResolution = TWO_PI / (theRadius / (PI / 4.0f));

        /**
         * @todo
         * we have a fixed resolution.
         * change this some time.
         */
        /** @todo display list ignore the different resolutions. hmm. */

        /* draw circle fan */
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, 0, 1);
        gl.glTexCoord2f(0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0);
        for (float i = 0; i < TWO_PI; i += myResolution) {
            final float x = (float)Math.sin(i) / 2 + 0.5f;
            final float y = (float)Math.cos(i) / 2 + 0.5f;
            gl.glTexCoord2f(x, y);
            gl.glVertex3f(x, y, 0);
        }

        /* close disk */
        gl.glTexCoord2f(0.5f, 1.0f);
        gl.glVertex3f(0.5f, 1.0f, 0);

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
