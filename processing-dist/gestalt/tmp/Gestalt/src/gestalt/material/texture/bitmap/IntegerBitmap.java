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

package gestalt.material.texture.bitmap;


import java.io.IOException;
import java.io.InputStream;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import gestalt.Gestalt;
import gestalt.material.Color;
import gestalt.material.texture.Bitmap;
import gestalt.util.ImageUtil;


public class IntegerBitmap
    implements Bitmap {

    public static final int NUMBER_OF_PIXEL_COMPONENTS = 1;

    /** @todo colorspace is 0...1 not 0...255?
     * if there is a need to change that TRANSFORM_VALUE
     * can be set to for example 1.
     */

    public static final float TRANSFORM_VALUE = 1f / 255f;

    private final int _myWidth;

    private final int _myHeight;

    private final int _myComponentOrder;

    private final int _myProportionType;

    private int[] _myPixelDataRef;

    public IntegerBitmap(int[] thePixels, int theWidth, int theHeight,
                         int theComponentOrder) {
        _myPixelDataRef = thePixels;
        _myWidth = theWidth;
        _myHeight = theHeight;
        _myComponentOrder = theComponentOrder;
        if (ImageUtil.isPowerOf2(theWidth) && ImageUtil.isPowerOf2(theHeight)) {
            _myProportionType = Gestalt.TEXTURE_PROPORTION_POWEROF2;
        } else {
            _myProportionType = Gestalt.TEXTURE_PROPORTION_ARBITRARY;
        }
    }


    public static IntegerBitmap getDefaultImageBitmap(int theWidth,
                                                      int theHeight) {
        return new IntegerBitmap(new int[theWidth * theHeight],
                                 theWidth,
                                 theHeight,
                                 Gestalt.BITMAP_COMPONENT_ORDER_BGRA);
    }


    public int getWidth() {
        return _myWidth;
    }


    public int getHeight() {
        return _myHeight;
    }


    public int getComponentOrder() {
        return _myComponentOrder;
    }


    public int getProportionType() {
        return _myProportionType;
    }


    public Object getDataRef() {
        return _myPixelDataRef;
    }


    public void setDataRef(Object theDataRef) {
        try {
            int[] myDataRef = (int[]) theDataRef;
            setIntDataRef(myDataRef);
        } catch (Exception ex) {
            System.err.println("### ERROR @ IntegerBitmap / " + ex);
            ex.printStackTrace(System.err);
        }
    }


    public void setIntDataRef(int[] theDataRef) {
        if (_myPixelDataRef.length != theDataRef.length) {
            System.err.println("### ERROR @ " + this.getClass()
                               + " / data reference sizes do not match.");
        }
        _myPixelDataRef = theDataRef;
    }


    public Color getPixel(int x, int y) {
        final Color myColor = new Color();
        getPixel(x, y, myColor);
        return myColor;
    }


    public void getPixel(int x, int y, Color thePixel) {
        int myColorIndex = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        int myColor = _myPixelDataRef[myColorIndex];

        int a = (myColor >> 24) & 0xFF;
        int r = (myColor >> 16) & 0xFF;
        int g = (myColor >> 8) & 0xFF;
        int b = myColor & 0xFF;

        thePixel.set(r * TRANSFORM_VALUE,
                     g * TRANSFORM_VALUE,
                     b * TRANSFORM_VALUE,
                     a * TRANSFORM_VALUE);
    }


    public void getColor(int theColorIndex, Color thePixel) {
        int myColor = _myPixelDataRef[theColorIndex];

        int a = (myColor >> 24) & 0xFF;
        int r = (myColor >> 16) & 0xFF;
        int g = (myColor >> 8) & 0xFF;
        int b = myColor & 0xFF;

        thePixel.set(r * TRANSFORM_VALUE,
                     g * TRANSFORM_VALUE,
                     b * TRANSFORM_VALUE,
                     a * TRANSFORM_VALUE);
    }


    public void setColor(int theColorIndex, Color thePixel) {
        int r = (int) (thePixel.r / TRANSFORM_VALUE);
        int g = (int) (thePixel.g / TRANSFORM_VALUE);
        int b = (int) (thePixel.b / TRANSFORM_VALUE);
        int a = (int) (thePixel.a / TRANSFORM_VALUE);
        int myIntColor = (a << 24) | (r << 16) | (g << 8) | b;
        _myPixelDataRef[theColorIndex] = myIntColor;
    }


    public void setPixel(int x, int y, Color thePixel) {
        int myColorIndex = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        int r = (int) (thePixel.r / TRANSFORM_VALUE);
        int g = (int) (thePixel.g / TRANSFORM_VALUE);
        int b = (int) (thePixel.b / TRANSFORM_VALUE);
        int a = (int) (thePixel.a / TRANSFORM_VALUE);
        int myIntColor = (a << 24) | (r << 16) | (g << 8) | b;
        _myPixelDataRef[myColorIndex] = myIntColor;
    }


    public int[] getIntDataRef() {
        return _myPixelDataRef;
    }


    public static IntegerBitmap load(final InputStream theStream) {

        BufferedImage myImage = null;
        int myWidth;
        int myHeight;

        try {
            myImage = javax.imageio.ImageIO.read(theStream);
        } catch (IOException theException) {
            System.err.println(theException);
        }

        myWidth = myImage.getWidth();
        myHeight = myImage.getHeight();

        int[] myImageData = new int[myWidth * myHeight];
        PixelGrabber myPixelGrabber = new PixelGrabber(myImage, 0, 0, myWidth,
                                                       myHeight, myImageData, 0, myWidth);
        try {
            myPixelGrabber.grabPixels();
        } catch (InterruptedException theInterruptedException) {
        }

        return new IntegerBitmap(myImageData, myWidth, myHeight,
                                 Gestalt.BITMAP_COMPONENT_ORDER_BGRA);
    }


    public IntegerBitmap duplicate() {
        IntegerBitmap myClone = new IntegerBitmap(new int[_myWidth * _myHeight],
                                                  _myWidth,
                                                  _myHeight,
                                                  _myComponentOrder);
        System.arraycopy(_myPixelDataRef, 0, myClone._myPixelDataRef, 0, _myPixelDataRef.length);
        return myClone;
    }
}
