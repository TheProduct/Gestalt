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


package gestalt.extension.materialplugin;


import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.TEXTURE_FILTERTYPE_MIPMAP;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;


public class JoglCustomMipMap
        extends TexturePlugin {

    private final ByteBitmap[] _myBitmaps;

    public JoglCustomMipMap(final ByteBitmap[] theBitmaps, final boolean theHintFlipYAxis) {
        super(theHintFlipYAxis);
        setFilterType(TEXTURE_FILTERTYPE_MIPMAP);
        _myBitmaps = theBitmaps;
    }

    public static ByteBitmap[] createBluredBitmapSequence(ByteBitmap myBitmap, int theBlurRadius) {
        int myCounter = getDepth(Math.max(myBitmap.getWidth(), myBitmap.getHeight()));
        ByteBitmap[] myBitmaps = new ByteBitmap[myCounter];
        myBitmaps[0] = myBitmap;
        for (int i = 1; i < myBitmaps.length; i++) {
            myBitmaps[i] = ImageUtil.scaleTo(myBitmaps[i - 1],
                                             myBitmaps[i - 1].getWidth() / 2,
                                             myBitmaps[i - 1].getHeight() / 2);
            myBitmaps[i] = ImageUtil.blur(myBitmaps[i], theBlurRadius);
        }
        return myBitmaps;
    }

    private static int getDepth(int myWidth) {
        int myCounter = 0;
        while (myWidth > 0) {
            myWidth = myWidth >> 1;
            myCounter++;
        }
        return myCounter;
    }

    public int getPixelWidth() {
        return _myBitmaps[0].getWidth();
    }

    public int getPixelHeight() {
        return _myBitmaps[0].getHeight();
    }

    public void update(final GL gl, final GLU glu) {
        /* initialize texture */
        if (!ourStaticInitalized) {
            initStatic(gl);
        }

        if (!_myIsInitalized) {
            init(gl);
        }

        /* enable and bind texture */
        gl.glBindTexture(getTextureTarget(), _myOpenGLTextureID);

        /* update properties */
        if (_myWrapModeChanged) {
            updateWrapMode(gl);
            _myWrapModeChanged = false;
        }

        if (_myBorderColorChanged) {
            updateBorderColor(gl);
            _myBorderColorChanged = false;
        }

        if (!_myIsInitalized) {
            updateFilterType(gl);
            changeData(gl, glu);
            _myIsInitalized = true;
        }
    }

    protected void updateData(final GL gl) {
    }

    protected void changeData(final GL gl, final GLU glu) {
        final ByteBitmap myBase = _myBitmaps[0];
        if (ImageUtil.getNextPowerOf2(myBase.getWidth()) != myBase.getWidth() ||
                ImageUtil.getNextPowerOf2(myBase.getHeight()) != myBase.getHeight()) {
            System.err.println("### ERROR @" +
                    getClass().getName() +
                    " / MIPMAP texture-size should be power of two. for example (" +
                    ImageUtil.getNextPowerOf2(myBase.getWidth()) +
                    "; " +
                    ImageUtil.getNextPowerOf2(myBase.getHeight()) +
                    ").");
        }

        for (int i = 0; i < _myBitmaps.length; i++) {
            final ByteBitmap myBitmap = _myBitmaps[i];
            final ByteBuffer myBuffer = ByteBuffer.wrap((byte[])myBitmap.getDataRef());
            gl.glTexImage2D(getTextureTarget(),
                            i,
                            GL.GL_RGBA,
                            myBitmap.getWidth(),
                            myBitmap.getHeight(),
                            0,
                            getFormat(myBitmap.getComponentOrder()),
                            getOpenGLType(myBitmap.getComponentOrder()),
                            myBuffer);
        }
    }
}
