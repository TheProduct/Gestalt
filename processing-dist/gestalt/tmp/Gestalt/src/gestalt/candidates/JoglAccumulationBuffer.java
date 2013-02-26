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
import gestalt.render.bin.Bin;
import gestalt.shape.AbstractDrawable;

import javax.media.opengl.GL;


public class JoglAccumulationBuffer
        extends AbstractDrawable {

    private final Bin _myBin;

    public JoglAccumulationBuffer(final Bin theBin) {
        _myBin = theBin;
    }

    public void draw(GLContext theRenderContext) {

        final GL gl = (theRenderContext).gl;

        gl.glPushMatrix();
        gl.glClear(GL.GL_ACCUM_BUFFER_BIT);

        int myIterations = 4;
        for (int i = 0; i < myIterations; i++) {
            gl.glTranslatef(4, 0, 0);
            _myBin.draw(theRenderContext);
            gl.glAccum(GL.GL_ACCUM, 1.f / myIterations);
        }

        gl.glAccum(GL.GL_RETURN, 1.f);
        gl.glPopMatrix();
    }
}
