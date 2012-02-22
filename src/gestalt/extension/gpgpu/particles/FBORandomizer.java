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


package gestalt.extension.gpgpu.particles;


import gestalt.extension.framebufferobject.BufferInfo;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;
import gestalt.util.JoglUtil;


public class FBORandomizer
        extends AbstractDrawable {

    private static final int COMPONENTS = 4;

    private final JoglFrameBufferObject _myFBO;

    private final int _myWidth;

    private final int _myHeight;

    private boolean FIRST_FRAME = true;

    public FBORandomizer(final JoglFrameBufferObject theFBO) {
        _myFBO = theFBO;
        _myWidth = theFBO.getPixelWidth();
        _myHeight = theFBO.getPixelHeight();
    }

    public void draw(GLContext theContext) {
        if (!FIRST_FRAME) {
            return;
        }
        FIRST_FRAME = false;

        final GL gl = (theContext).gl;
        final GLU glu = (theContext).glu;

        /* write data */
        float[] mySource = new float[_myWidth * _myHeight * COMPONENTS];
        for (int i = 0; i < mySource.length; i += 4) {
            mySource[i + 0] = (float)Math.random() * 1024 - 512;
            mySource[i + 1] = (float)Math.random() * 768 - 384;

//            mySource[i + 0] = (float)Math.random() * 1400 + 700;//(float) Math.random() * 1024 - 512;
//            mySource[i + 1] = (float)Math.random() * 350 - (350 / 2f);//(float) Math.random() * 768 - 384;
            mySource[i + 2] = (float)Math.random() * 768 - 384;
            mySource[i + 3] = 1.0f;
        }

        float[] myOtherSource = new float[_myWidth * _myHeight * COMPONENTS];
        for (int i = 0; i < mySource.length; i += 4) {
            myOtherSource[i + 0] = 10;
            myOtherSource[i + 1] = 0;
            myOtherSource[i + 2] = 0;
            myOtherSource[i + 3] = (float)Math.random() * 1.25f + 0.25f; // mass
        }

        writeData(gl, glu, mySource, myOtherSource);

        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".draw", true);
    }

    private void writeData(GL gl, GLU glu, float[] theSource, float[] theOtherSource) {
        /* prepare data */
        final FloatBuffer myResult = FloatBuffer.wrap(theSource);

        /* transfer data to texture */
//        gl.glActiveTexture(_myFBO.getTextureUnit());
//        gl.glEnable(_myFBO.getTextureTarget());
        gl.glBindTexture(_myFBO.getTextureTarget(),
                         _myFBO.getTextureID());

        gl.glTexSubImage2D(_myFBO.getTextureTarget(),
                           0,
                           0, 0, // offset(x, y)
                           _myWidth, _myHeight,
                           GL.GL_RGBA,
                           GL.GL_FLOAT,
                           myResult);

        /* prepare data */
        final FloatBuffer myOtherResult = FloatBuffer.wrap(theOtherSource);

        /* transfer data to texture */
        gl.glBindTexture(_myFBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(),
                         _myFBO.additional_texture(BufferInfo.SECONDARY).getTextureID());

        gl.glTexSubImage2D(_myFBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(),
                           0,
                           0, 0, // offset(x, y)
                           _myWidth, _myHeight,
                           GL.GL_RGBA,
                           GL.GL_FLOAT,
                           myOtherResult);

        gl.glBindTexture(_myFBO.additional_texture(BufferInfo.SECONDARY).getTextureTarget(), 0);

        JoglUtil.printGLError(gl, glu, "writeData", true);
    }

    public boolean isActive() {
        return FIRST_FRAME;
    }
}
