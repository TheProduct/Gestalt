/*
 * Gestalt
 *
 * Copyright (C) 2006 Patrick Kochlik + Dennis Paul
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


package gestalt.extension.materialplugin;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.material.Material;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBitmap;


public class JoglMaterialPluginCubeMap
        extends TexturePlugin {

    private Bitmap _myScheduledBitmap_NEGX;

    private Bitmap _myScheduledBitmap_POSX;

    private Bitmap _myScheduledBitmap_NEGY;

    private Bitmap _myScheduledBitmap_POSY;

    private Bitmap _myScheduledBitmap_NEGZ;

    private Bitmap _myScheduledBitmap_POSZ;

    private int myImageSize;

    private boolean firstFrame = true;

    private static int _myMaxTextureSize;

    private static boolean ourStaticInitalized = false;

    public JoglMaterialPluginCubeMap(final boolean theHintFlipYAxis) {
        super(theHintFlipYAxis);
        setTextureTarget(GL.GL_TEXTURE_CUBE_MAP);
        setTextureUnit(GL.GL_TEXTURE0);
        setWrapMode(TEXTURE_WRAPMODE_CLAMP);
        setFilterType(TEXTURE_FILTERTYPE_LINEAR);
    }

    protected void init(final GL gl) {
        /* create opengl texture ID */
        int[] myIDs = new int[1];
        /** @todo JSR-231 performance hit! */
        gl.glGenTextures(myIDs.length, myIDs, 0);
        setTextureID(myIDs[0]);
    }

    public void initStatic(final GL gl) {
        /* query maximum texture size */
        int[] myValue = new int[1];
        /** @todo JSR-231 performance hit! */
        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, myValue, 0);
        _myMaxTextureSize = myValue[0];
        ourStaticInitalized = true;
    }

    public void load(Bitmap theBitmap_NEGX,
                     Bitmap theBitmap_POSX,
                     Bitmap theBitmap_NEGY,
                     Bitmap theBitmap_POSY,
                     Bitmap theBitmap_NEGZ,
                     Bitmap theBitmap_POSZ) {
        /* schedule for upload */
        _myScheduledBitmap_NEGX = theBitmap_NEGX;
        _myScheduledBitmap_POSX = theBitmap_POSX;
        _myScheduledBitmap_NEGY = theBitmap_NEGY;
        _myScheduledBitmap_POSY = theBitmap_POSY;
        _myScheduledBitmap_NEGZ = theBitmap_NEGZ;
        _myScheduledBitmap_POSZ = theBitmap_POSZ;
        myImageSize = _myScheduledBitmap_NEGX.getWidth();
    }

    public void update(final GL gl, final GLU glu) {
        /* initialize texture */
        if (!ourStaticInitalized) {
            initStatic(gl);
        }

        if (!_myIsInitalized) {
            init(gl);
            _myIsInitalized = true;
        }

        /* enable and bind texture */
        gl.glBindTexture(getTextureTarget(), getTextureID());

        /* update properties */
        if (_myWrapModeChanged) {
            updateWrapMode(gl);
            _myWrapModeChanged = false;
        }

        if (_myFilterTypeChanged) {
            updateFilterType(gl);
            _myFilterTypeChanged = false;
        }

        if (_myScheduledBitmap_NEGX != null) {
            handleScheduledBitmap(gl, glu, _myScheduledBitmap_NEGX);
        }

        if (_myBorderColorChanged) {
            updateBorderColor(gl);
            _myBorderColorChanged = false;
        }
    }

    protected void handleScheduledBitmap(GL gl, GLU glu, Bitmap theScheduledBitmap) {
        if (firstFrame) {
            firstFrame = false;
            gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_REFLECTION_MAP);
            gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_REFLECTION_MAP);
            gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, GL.GL_REFLECTION_MAP);
            gl.glEnable(GL.GL_TEXTURE_GEN_S);
            gl.glEnable(GL.GL_TEXTURE_GEN_T);
            gl.glEnable(GL.GL_TEXTURE_GEN_R);
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
            changeData(gl, glu);
        }
    }

    protected void updateData(final GL gl) {
    }

    protected void changeData(final GL gl, final GLU glu) {
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL.GL_RGBA,
                        myImageSize, myImageSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap(((ByteBitmap)_myScheduledBitmap_POSX).getByteDataRef()));
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL.GL_RGBA,
                        myImageSize, myImageSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap(((ByteBitmap)_myScheduledBitmap_NEGX).getByteDataRef()));
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL.GL_RGBA,
                        myImageSize, myImageSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap(((ByteBitmap)_myScheduledBitmap_POSY).getByteDataRef()));
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL.GL_RGBA,
                        myImageSize, myImageSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap(((ByteBitmap)_myScheduledBitmap_NEGY).getByteDataRef()));
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL.GL_RGBA,
                        myImageSize, myImageSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap(((ByteBitmap)_myScheduledBitmap_POSZ).getByteDataRef()));
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL.GL_RGBA,
                        myImageSize, myImageSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap(((ByteBitmap)_myScheduledBitmap_NEGZ).getByteDataRef()));
    }

    public void begin(GLContext theRenderContext, Material theParent) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        if (!theParent.wireframe) {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());
        }

        /* update texture properties */
        update(gl, glu);

        /* handle wireframe */
        if (theParent.wireframe) {
            gl.glDisable(getTextureTarget());
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        }
    }

    public void end(GLContext theRenderContext, Material theParent) {
        GL gl = theRenderContext.gl;
        gl.glDisable(GL.GL_TEXTURE_GEN_S);
        gl.glDisable(GL.GL_TEXTURE_GEN_T);
        gl.glDisable(GL.GL_TEXTURE_GEN_R);
        gl.glDisable(GL.GL_TEXTURE_CUBE_MAP);
        gl.glBindTexture(getTextureTarget(), 0);
    }

    protected void updateBorderColor(final GL gl) {
        gl.glTexParameterfv(getTextureTarget(), GL.GL_TEXTURE_BORDER_COLOR,
                            FloatBuffer.wrap(new float[] {_myBorderColor.r,
                                                          _myBorderColor.g,
                                                          _myBorderColor.b,
                                                          _myBorderColor.a}));
    }

    protected void updateWrapMode(final GL gl) {
        switch (getWrapMode()) {
            case TEXTURE_WRAPMODE_CLAMP:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP);
                break;
            case TEXTURE_WRAPMODE_REPEAT:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_REPEAT);
                break;
            case TEXTURE_WRAPMODE_CLAMP_TO_BORDER:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_BORDER);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_BORDER);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_BORDER);
                break;
        }
    }

    protected void updateFilterType(final GL gl) {
        switch (getFilterType()) {
            case TEXTURE_FILTERTYPE_LINEAR:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                break;
            case TEXTURE_FILTERTYPE_NEAREST:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
                break;
        }
    }

    public void setBitmapRef(final Bitmap theBitmap) {
        System.out.println("### WARNING @ JoglMaterialPluginCubeMap / setBitmapRef doesn t work here");
    }

    public int getMaxTextureSize() {
        if (!ourStaticInitalized) {
            System.err.println("### WARNING @ " + getClass().getName() + " / can t validate bitmap size. opengl has not been initalized.");
        }
        return _myMaxTextureSize;
    }
}
