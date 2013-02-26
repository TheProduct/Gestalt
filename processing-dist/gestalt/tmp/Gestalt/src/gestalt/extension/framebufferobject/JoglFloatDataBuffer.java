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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.material.texture.TextureInfo;
import gestalt.util.JoglUtil;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;


public class JoglFloatDataBuffer
        implements MaterialPlugin,
                   TextureInfo {

    private static final int mTextureTarget = GL.GL_TEXTURE_RECTANGLE_ARB;

    private static final int mTextureFormat = GL.GL_RGBA;

    private int mTextureUnit = GL.GL_TEXTURE0;

    public static final int components_per_fragment = 4;

    private static final int mInternalFormat = GL.GL_RGBA32F_ARB;

    private static final int mPixelType = GL.GL_FLOAT;

    private int mTextureID = Gestalt.UNDEFINED;

    private boolean mUpdate = false;

    private final int mWidth;

    private final int mHeight;

    private float[] mDataBuffer;

    public JoglFloatDataBuffer(int theWidth, int theHeight) {
        this(theWidth, theHeight, new float[theWidth * theHeight * components_per_fragment]);
    }

    public JoglFloatDataBuffer(int theWidth, int theHeight, float[] pDataBuffer) {
        mWidth = theWidth;
        mHeight = theHeight;
        mDataBuffer = pDataBuffer;
        if (mWidth * mHeight * components_per_fragment != mDataBuffer.length) {
            System.err.println("+++ WARNING @" + getClass().getSimpleName() + " / width and height don t match buffer size.");
        }
    }

    public void begin(GLContext theRenderContext, Material theParentMaterial) {
        final GL gl = theRenderContext.gl;
        update(gl);
        gl.glActiveTexture(mTextureUnit);
        gl.glEnable(mTextureTarget);
        gl.glBindTexture(mTextureTarget, mTextureID);
    }

    public void end(GLContext theRenderContext, Material theParentMaterial) {
        final GL gl = theRenderContext.gl;
        gl.glActiveTexture(mTextureUnit);
        gl.glBindTexture(mTextureTarget, 0);
        gl.glDisable(mTextureTarget);
    }

    public void update(final GL gl) {
        /* create float texture (maybe disable blending) */
        if (mTextureID == Gestalt.UNDEFINED) {
            /* enable */
            gl.glActiveTexture(mTextureUnit);
            gl.glEnable(mTextureTarget);
            create(gl);
            /* disable */
            gl.glDisable(mTextureTarget);
        }
        /* upload data if scheduled */
        if (mUpdate) {
            /* enable */
            gl.glActiveTexture(mTextureUnit);
            gl.glEnable(mTextureTarget);
            update_data(gl);
            /* disable */
            gl.glDisable(mTextureTarget);
        }
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

    public float[] buffer() {
        return mDataBuffer;
    }

    public void set_buffer_ref(final float[] pDataBuffer) {
        if (mDataBuffer.length != pDataBuffer.length) {
            System.err.println("### WARNING @" + getClass().getSimpleName() + " / buffer sizes don t match.");
        }
        mDataBuffer = pDataBuffer;
    }

    public void scheduleUpdate() {
        mUpdate = true;
    }

    private void update_data(final GL gl) {
        mUpdate = false;
        gl.glBindTexture(mTextureTarget, mTextureID);
        gl.glTexSubImage2D(mTextureTarget,
                           0,
                           0, 0,
                           mWidth,
                           mHeight,
                           mTextureFormat,
                           mPixelType,
                           FloatBuffer.wrap(mDataBuffer));
        gl.glBindTexture(mTextureTarget, 0);
    }

    private void create(GL gl) {
        /* create buffers */
        if (!JoglUtil.testExtensionAvailability(gl, "GL_ARB_texture_float")) {
            System.out.println("+++ GL_ARB_texture_float not available.");
        }

        /* create textures */
        int[] myTextureConainter = new int[1];
        gl.glGenTextures(1, myTextureConainter, 0);
        mTextureID = myTextureConainter[0];

        gl.glBindTexture(mTextureTarget, mTextureID);
        gl.glTexParameteri(mTextureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(mTextureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(mTextureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameteri(mTextureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);

        gl.glTexImage2D(mTextureTarget,
                        0,
                        mInternalFormat,
                        mWidth,
                        mHeight,
                        0,
                        mTextureFormat,
                        mPixelType,
                        FloatBuffer.wrap(mDataBuffer));
        gl.glBindTexture(mTextureTarget, 0);
    }

    public int getTextureTarget() {
        return mTextureTarget;
    }

    public int getTextureID() {
        return mTextureID;
    }

    public void setTextureUnit(final int pTextureUnit) {
        mTextureUnit = pTextureUnit;
    }

    public int getTextureUnit() {
        return mTextureUnit;
    }
}
