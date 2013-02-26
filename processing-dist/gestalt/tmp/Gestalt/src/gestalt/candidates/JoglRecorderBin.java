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
import gestalt.render.bin.RenderBin;

import javax.media.opengl.GL;


public class JoglRecorderBin
        extends RenderBin {

    private boolean _myIsCompiled = false;

    private int _myDisplayList;

    private boolean _myDrawUncompiled = false;

    public void draw(GLContext theRenderContext) {
        if (_myDrawUncompiled) {
            super.draw(theRenderContext);
        } else {
            final GL gl = (theRenderContext).gl;

            if (!_myIsCompiled) {
                _myIsCompiled = true;

                if (_myDisplayList != 0) {
                    cleanup(gl);
                }

                _myDisplayList = gl.glGenLists(1);
                gl.glNewList(_myDisplayList, GL.GL_COMPILE);
                super.draw(theRenderContext);
                gl.glEndList();
            }
            gl.glCallList(_myDisplayList);
        }
    }

    public void setDrawUncompiled(boolean theState) {
        _myDrawUncompiled = theState;
    }

    public boolean getDrawUncompiled() {
        return _myDrawUncompiled;
    }

    public void scheduleRecording() {
        _myIsCompiled = false;
    }

    public void cleanup(GL gl) {
        gl.glDeleteLists(_myDisplayList, 1);
    }
}
