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


package gestalt.extension.framebufferobject;


import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;


public class JoglFBODataReader
    extends AbstractDrawable {

    private final JoglFrameBufferObject _myFBO;

    private final float[] _myDataArray;

    private final FloatBuffer _myDataBuffer;

    private static final int NUMBER_OF_FLOATS_PER_FRAGMENT = 4;

    public JoglFBODataReader(final JoglFrameBufferObject theFBO) {
        _myFBO = theFBO;
        _myDataArray = new float[theFBO.getPixelWidth() *
                       theFBO.getPixelHeight() *
                       NUMBER_OF_FLOATS_PER_FRAGMENT];
        _myDataBuffer = FloatBuffer.wrap(_myDataArray);
    }


    public float[] getDataRef() {
        return _myDataArray;
    }


    public void draw(GLContext theRenderContext) {
        final GL gl = (  theRenderContext).gl;
        final GLU glu = (  theRenderContext).glu;
        if (_myFBO.isInitialized()) {
            JoglFrameBufferObject.readBackData(gl, glu, _myFBO, _myDataBuffer);
        }
    }
}
