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

import gestalt.Gestalt;
import gestalt.material.Color;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;


/** @todo the pixel operations only work for BITMAP_COMPONENT_ORDER_RGBA */
public class ByteBitmap3D
        implements Bitmap {

    private static final int NUMBER_OF_PIXEL_COMPONENTS = 4;

    private static final float TRANSFORM_VALUE = 1f / 255f;

    private final int _myWidth;

    private final int _myHeight;

    private final int _myDepth;

    private final int _myComponentOrder;

    private byte[] _myPixelDataRef;

    public ByteBitmap3D(final byte[] thePixels,
                        final int theWidth,
                        final int theHeight,
                        final int theDepth,
                        final int theComponentOrder) {
        _myWidth = theWidth;
        _myHeight = theHeight;
        _myDepth = theDepth;
        _myComponentOrder = theComponentOrder;
        _myPixelDataRef = thePixels;

        if (!ImageUtil.isPowerOf2(theWidth)
                || !ImageUtil.isPowerOf2(theHeight)
                || !ImageUtil.isPowerOf2(theDepth)) {
            System.err.println("### ERROR @ " + getClass().getName()
                    + " / only power of two textures are supported.");
        }
    }

    public static ByteBitmap3D getDefaultImageBitmap(int theWidth,
                                                     int theHeight,
                                                     int theDepth) {
        return getDefaultImageBitmap(theWidth,
                                     theHeight,
                                     theDepth,
                                     Gestalt.BITMAP_COMPONENT_ORDER_RGBA);
    }

    public static ByteBitmap3D getDefaultImageBitmap(int theWidth,
                                                     int theHeight,
                                                     int theDepth,
                                                     int theComponentOrder) {
        return new ByteBitmap3D(new byte[NUMBER_OF_PIXEL_COMPONENTS
                * theWidth
                * theHeight
                * theDepth],
                                theWidth,
                                theHeight,
                                theDepth,
                                theComponentOrder);
    }

    public int getWidth() {
        return _myWidth;
    }

    public int getHeight() {
        return _myHeight;
    }

    public int getDepth() {
        return _myDepth;
    }

    public int getComponentOrder() {
        return _myComponentOrder;
    }

    public int getProportionType() {
        return Gestalt.TEXTURE_PROPORTION_POWEROF2;
    }

    public Object getDataRef() {
        return _myPixelDataRef;
    }

    public void setDataRef(Object theDataRef) {
        if (theDataRef instanceof byte[]) {
            byte[] myDataRef = (byte[])theDataRef;
            setByteDataRef(myDataRef);
        } else {
            System.err.println("### ERROR @ " + this.getClass() + " / data reference is not of type byte[].");
        }
    }

    public byte[] getByteDataRef() {
        return _myPixelDataRef;
    }

    public void setByteDataRef(byte[] theDataRef) {
        if (_myPixelDataRef.length != theDataRef.length) {
            System.err.println("### ERROR @ " + this.getClass()
                    + " / data reference sizes do not match.");
        }
        _myPixelDataRef = theDataRef;
    }

    public void copyByteBitmap(final ByteBitmap theByteBitmap, final int theLevel) {
        if (theByteBitmap.getWidth() != getWidth()
                || theByteBitmap.getHeight() != getHeight()) {
            System.err.println("### WARNING @ " + this.getClass()
                    + " / dimensions don t match. / " + theByteBitmap);
        }
        if (theLevel < 0 || theLevel >= getDepth()) {
            System.err.println("### WARNING @ " + this.getClass()
                    + " / depth exceeds bounds. / " + theLevel);
        }

        final int myBitmapSize = NUMBER_OF_PIXEL_COMPONENTS * _myWidth * _myHeight;
        final int myDataRefOffset = myBitmapSize * theLevel;
        /* copy bitmap data into array */
        try {
            final byte[] mySource = theByteBitmap.getByteDataRef();

            System.arraycopy(mySource,
                             0,
                             _myPixelDataRef,
                             myDataRefOffset,
                             mySource.length);
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass()
                    + " / copying data. / " + ex);
        }
    }


    /* pixel operations */
    public void getPixel(int x, int y, Color thePixel) {
        getPixel(x, y, 0, thePixel);
    }

    public Color getPixel(int x, int y) {
        Color myColor = new Color();
        getPixel(x, y, myColor);
        return myColor;
    }

    public void setPixel(int x, int y, Color thePixel) {
        setPixel(x, y, 0, thePixel);
    }

    public void getPixel(final int x,
                         final int y,
                         final int z,
                         final Color thePixel) {
        final int myPixel = map3DPixelsTo1DArray(x, y, z);
        thePixel.r = (_myPixelDataRef[myPixel + Gestalt.RED] & 0xff) * TRANSFORM_VALUE;
        thePixel.g = (_myPixelDataRef[myPixel + Gestalt.GREEN] & 0xff) * TRANSFORM_VALUE;
        thePixel.b = (_myPixelDataRef[myPixel + Gestalt.BLUE] & 0xff) * TRANSFORM_VALUE;
        thePixel.a = (_myPixelDataRef[myPixel + Gestalt.ALPHA] & 0xff) * TRANSFORM_VALUE;
    }

    public void setPixel(final int x,
                         final int y,
                         final int z,
                         final Color thePixel) {
        final int myPixel = map3DPixelsTo1DArray(x, y, z);
        _myPixelDataRef[myPixel + Gestalt.RED] = (byte)(thePixel.r * 255);
        _myPixelDataRef[myPixel + Gestalt.GREEN] = (byte)(thePixel.g * 255);
        _myPixelDataRef[myPixel + Gestalt.BLUE] = (byte)(thePixel.b * 255);
        _myPixelDataRef[myPixel + Gestalt.ALPHA] = (byte)(thePixel.a * 255);
    }

    private int map3DPixelsTo1DArray(int x, int y, int z) {
        /** @todo check this conversion. */
        return (NUMBER_OF_PIXEL_COMPONENTS * (x * _myWidth + y)
                + NUMBER_OF_PIXEL_COMPONENTS * _myWidth * _myHeight * z);
    }
}
