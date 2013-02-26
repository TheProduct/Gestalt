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

import gestalt.material.Color;
import gestalt.material.texture.Bitmap;
import gestalt.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import static gestalt.Gestalt.*;


/** @todo the pixel operations only work for BITMAP_COMPONENT_ORDER_RGBA */
public class ByteBitmap
    implements Bitmap {

    public static final int NUMBER_OF_PIXEL_COMPONENTS = 4;

    private static final float TRANSFORM_VALUE = 1f / 255f;

    private final int _myWidth;

    private final int _myHeight;

    private final int _myComponentOrder;

    private final int _myProportionType;

    private byte[] _myPixelDataRef;

    public ByteBitmap(final byte[] thePixels,
                      final int theWidth,
                      final int theHeight,
                      final int theComponentOrder) {
        _myPixelDataRef = thePixels;
        _myWidth = theWidth;
        _myHeight = theHeight;
        _myComponentOrder = theComponentOrder;
        if (ImageUtil.isPowerOf2(theWidth) &&
            ImageUtil.isPowerOf2(theHeight)) {
            _myProportionType = TEXTURE_PROPORTION_POWEROF2;
        } else {
            _myProportionType = TEXTURE_PROPORTION_ARBITRARY;
        }
    }


    public static ByteBitmap getDefaultImageBitmap(int theWidth, int theHeight) {
        return new ByteBitmap(new byte[NUMBER_OF_PIXEL_COMPONENTS * theWidth * theHeight],
                              theWidth,
                              theHeight,
                              BITMAP_COMPONENT_ORDER_RGBA);
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
        if (theDataRef instanceof byte[]) {
            byte[] myDataRef = (byte[]) theDataRef;
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
            System.err.println("### ERROR @ " + this.getClass() + " / data reference sizes do not match.");
        }
        _myPixelDataRef = theDataRef;
    }


    public static ByteBitmap load(InputStream theStream) {
        ByteBitmap myBitmap;
        try {
            BufferedImage myImage = javax.imageio.ImageIO.read(theStream);
            myBitmap = ImageUtil.convertBufferedImage2ByteBitmap(myImage);
            return myBitmap;
        } catch (Exception theException) {
            System.err.println("### ERROR @ " + ByteBitmap.class.getName() + " / texture " +
                               " is missing: " + theException);
            return null;
        }
    }


    /* pixel operations */
    public void combine(Bitmap theBitmap, int theX, int theY, int theBlendmode) {
        combine(theBitmap, theX, theY, theBlendmode, 1, 1);
    }


    public void combine(Bitmap theBitmap,
                        final int theX,
                        final int theY,
                        final int theBlendmode,
                        final float theSource,
                        final float theDestination) {
        /** @todo we might include a adding mode here */
        for (int y = 0; y < theBitmap.getHeight(); y++) {
            for (int x = 0; x < theBitmap.getWidth(); x++) {
                final int myLocalX = x + theX;
                final int myLocalY = y + theY;
                final Color myPixel = theBitmap.getPixel(x, y);
                if (myLocalX >= 0 && myLocalX < getWidth() &&
                    myLocalY >= 0 && myLocalY < getHeight()) {
                    switch (theBlendmode) {
                        case BITMAP_BLENDMODE_OVERWRITE:
                            setPixel(myLocalX, myLocalY, myPixel);
                            break;
                        case BITMAP_BLENDMODE_ADD: {
                            final Color myLocalPixel = getPixel(myLocalX, myLocalY);
                            myLocalPixel.r *= theSource * (1 - myPixel.a);
                            myLocalPixel.g *= theSource * (1 - myPixel.a);
                            myLocalPixel.b *= theSource * (1 - myPixel.a);
                            myLocalPixel.a *= theSource * (1 - myPixel.a);

                            myLocalPixel.r += myPixel.r * myPixel.a * theDestination;
                            myLocalPixel.g += myPixel.g * myPixel.a * theDestination;
                            myLocalPixel.b += myPixel.b * myPixel.a * theDestination;
                            myLocalPixel.a += myPixel.a * theDestination;

//                            myLocalPixel.r += myPixel.r * theDestination;
//                            myLocalPixel.g += myPixel.g * theDestination;
//                            myLocalPixel.b += myPixel.b * theDestination;
//                            myLocalPixel.a += myPixel.a * theDestination;

                            myLocalPixel.r = Math.min(1, myLocalPixel.r);
                            myLocalPixel.g = Math.min(1, myLocalPixel.g);
                            myLocalPixel.b = Math.min(1, myLocalPixel.b);
                            myLocalPixel.a = Math.min(1, myLocalPixel.a);
                            setPixel(myLocalX, myLocalY, myLocalPixel);
                            break;
                        }
                        case BITMAP_BLENDMODE_ADD_2: {
                            final Color myLocalPixel = getPixel(myLocalX, myLocalY);

                            myLocalPixel.r += myPixel.r;
                            myLocalPixel.g += myPixel.g;
                            myLocalPixel.b += myPixel.b;

                            myLocalPixel.r = Math.min(1, myLocalPixel.r);
                            myLocalPixel.g = Math.min(1, myLocalPixel.g);
                            myLocalPixel.b = Math.min(1, myLocalPixel.b);

                            myLocalPixel.r = Math.max(0, myLocalPixel.r);
                            myLocalPixel.g = Math.max(0, myLocalPixel.g);
                            myLocalPixel.b = Math.max(0, myLocalPixel.b);

                            myLocalPixel.a = 1.0f;

                            setPixel(myLocalX, myLocalY, myLocalPixel);
                            break;
                        }
                        case BITMAP_BLENDMODE_MULTIPLY: {
                            final Color myLocalPixel = getPixel(myLocalX, myLocalY);
                            myLocalPixel.r *= theSource;
                            myLocalPixel.g *= theSource;
                            myLocalPixel.b *= theSource;
                            myLocalPixel.a *= theSource;
                            myLocalPixel.r *= myPixel.r * theDestination;
                            myLocalPixel.g *= myPixel.g * theDestination;
                            myLocalPixel.b *= myPixel.b * theDestination;
                            myLocalPixel.a *= myPixel.a * theDestination;
                            myLocalPixel.r = Math.min(1, myLocalPixel.r);
                            myLocalPixel.g = Math.min(1, myLocalPixel.g);
                            myLocalPixel.b = Math.min(1, myLocalPixel.b);
                            myLocalPixel.a = Math.min(1, myLocalPixel.a);
                            setPixel(myLocalX, myLocalY, myLocalPixel);
                            break;
                        }
                    }
                }
            }
        }
    }


    public void copyPixels(Bitmap theBitmap,
                           final int theX,
                           final int theY) {
        combine(theBitmap,
                theX,
                theY,
                BITMAP_BLENDMODE_OVERWRITE,
                1,
                1);
    }


    public void fill(final Color theColor) {
        /** @todo we might include a adding mode here */
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                setPixel(x, y, theColor);
            }
        }
    }


    public void invert() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                final Color myColor = getPixel(x, y);
                myColor.r = 1 - myColor.r;
                myColor.g = 1 - myColor.g;
                myColor.b = 1 - myColor.b;
                setPixel(x, y, myColor);
            }
        }
    }


    public void flipY() {
        final Color myColorA = new Color();
        final Color myColorB = new Color();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight() / 2; y++) {
                getPixel(x, y, myColorA);
                getPixel(x, getHeight() - y - 1, myColorB);
                setPixel(x, getHeight() - y - 1, myColorA);
                setPixel(x, y, myColorB);
            }
        }
    }


    public void flipX() {
        final Color myColorA = new Color();
        final Color myColorB = new Color();
        for (int x = 0; x < getWidth() / 2f; x++) {
            for (int y = 0; y < getHeight(); y++) {
                getPixel(x, y, myColorA);
                getPixel(getWidth() - x - 1, y, myColorB);
                setPixel(getWidth() - x - 1, y, myColorA);
                setPixel(x, y, myColorB);
            }
        }
    }


    final Color mySourceFactor = new Color();

    final Color myDestinationFactor = new Color();

    public void blend(Bitmap theBitmap,
                      final int theX,
                      final int theY,
                      final int theSourceFactorType,
                      final int theDestinationFactorType) {
        /** @todo we might include a adding mode here */
        for (int y = 0; y < theBitmap.getHeight(); y++) {
            for (int x = 0; x < theBitmap.getWidth(); x++) {
                final int myLocalX = x + theX;
                final int myLocalY = y + theY;
                if (myLocalX >= 0 && myLocalX < getWidth() &&
                    myLocalY >= 0 && myLocalY < getHeight()) {
                    final Color mySourceColor = theBitmap.getPixel(x, y);
                    final Color myDestinationColor = getPixel(myLocalX, myLocalY);
                    getBlendFactor(theSourceFactorType,
                                   mySourceColor,
                                   myDestinationColor,
                                   mySourceFactor);
                    getBlendFactor(theDestinationFactorType,
                                   mySourceColor,
                                   myDestinationColor,
                                   myDestinationFactor);
                    myDestinationColor.r = mySourceFactor.r * mySourceColor.r + myDestinationFactor.r * myDestinationColor.r;
                    myDestinationColor.g = mySourceFactor.g * mySourceColor.g + myDestinationFactor.g * myDestinationColor.g;
                    myDestinationColor.b = mySourceFactor.b * mySourceColor.b + myDestinationFactor.b * myDestinationColor.b;
                    myDestinationColor.a = mySourceFactor.a * mySourceColor.a + myDestinationFactor.a * myDestinationColor.a;
                    myDestinationColor.r = Math.max(0, Math.min(1, myDestinationColor.r));
                    myDestinationColor.g = Math.max(0, Math.min(1, myDestinationColor.g));
                    myDestinationColor.b = Math.max(0, Math.min(1, myDestinationColor.b));
                    myDestinationColor.a = Math.max(0, Math.min(1, myDestinationColor.a));
                    setPixel(myLocalX, myLocalY, myDestinationColor);
                }
            }
        }
    }


    private void getBlendFactor(final int theBlendFunctionEnum,
                                final Color theSource,
                                final Color theDestination,
                                final Color theBlendFactorResult) {

        switch (theBlendFunctionEnum) {

            case BITMAP_BLENDFACTOR_ZERO:
                theBlendFactorResult.set(0, 0);

                /* GL_ZERO  source or destination  (0, 0, 0, 0) */
                break;

            case BITMAP_BLENDFACTOR_ONE:
                theBlendFactorResult.set(1, 1);

                /* GL_ONE  source or destination  (1, 1, 1, 1) */
                break;

            case BITMAP_BLENDFACTOR_DST_COLOR:
                theBlendFactorResult.set(theDestination);

                /* GL_DST_COLOR  source (Rd, Gd, Bd, Ad) */
                break;

            case BITMAP_BLENDFACTOR_SRC_COLOR:
                theBlendFactorResult.set(theSource);

                /* GL_SRC_COLOR destination  (Rs, Gs, Bs, As) */
                break;

            case BITMAP_BLENDFACTOR_ONE_MINUS_DST_COLOR:
                theBlendFactorResult.set(1 - theDestination.r,
                                         1 - theDestination.g,
                                         1 - theDestination.b,
                                         1 - theDestination.a);

                /* GL_ONE_MINUS_DST_COLOR source (1, 1, 1, 1)-(Rd, Gd, Bd, Ad) */
                break;

            case BITMAP_BLENDFACTOR_ONE_MINUS_SRC_COLOR:
                theBlendFactorResult.set(1 - theSource.r,
                                         1 - theSource.g,
                                         1 - theSource.b,
                                         1 - theSource.a);

                /* GL_ONE_MINUS_SRC_COLOR   destination (1, 1, 1, 1)-(Rs, Gs, Bs, As) */
                break;

            case BITMAP_BLENDFACTOR_SRC_ALPHA:
                theBlendFactorResult.set(theSource.a, theSource.a);

                /* GL_SRC_ALPHA  source or destination (As, As, As, As) */
                break;

            case BITMAP_BLENDFACTOR_ONE_MINUS_SRC_ALPHA:
                theBlendFactorResult.set(1 - theSource.a, 1 - theSource.a);

                /* GL_ONE_MINUS_SRC_ALPHA  source or destination  (1, 1, 1, 1)-(As, As, As, As) */
                break;

            case BITMAP_BLENDFACTOR_DST_ALPHA:
                theBlendFactorResult.set(theDestination.a, theDestination.a);

                /* GL_DST_ALPHA  source or destination  (Ad, Ad, Ad, Ad) */
                break;

            case BITMAP_BLENDFACTOR_ONE_MINUS_DST_ALPHA:
                theBlendFactorResult.set(1 - theDestination.a, 1 - theDestination.a);

                /* GL_ONE_MINUS_DST_ALPHA  source or destination  (1, 1, 1, 1)-(Ad, Ad, Ad, Ad) */
                break;

            case BITMAP_BLENDFACTOR_SRC_ALPHA_SATURATE:
                theBlendFactorResult.set(Math.min(theSource.a, 1 - theDestination.a), 1);

                /* GL_SRC_ALPHA_SATURATE source (f, f, f, 1); f=min(As, 1-Ad) */
                break;
        }

        /*
             glBlendFunc(source factor, destination factor)

             EXAMPLE

             source            destination
             colors          : 1, 0.5, 0, 0.25 / 0.35, 1, 0, 1
             blend functions : GL_SRC_ALPHA    / GL_ONE_MINUS_SRC_ALPHA

             RED (R)

             sourceFactorR (sfR)      = scA
             destinationFactorR (dfR) = 1 - scA
             colorR                   = sfR * scR + dfR * dcR

             sourceFactorR (sfR)      = 0.25
             destinationFactorR (dfR) = 1 - 0.25 = 0.75
             colorR                   = 0.25 * 1 + 0.75 * 0.35

         */
    }


    public Color getPixel(int x, int y) {
        final Color myColor = new Color();
        getPixel(x, y, myColor);
        return myColor;
    }


    public void getPixel(int x, int y, Color thePixel) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        thePixel.r = (_myPixelDataRef[myPixel + RED] & 0xff) * TRANSFORM_VALUE;
        thePixel.g = (_myPixelDataRef[myPixel + GREEN] & 0xff) * TRANSFORM_VALUE;
        thePixel.b = (_myPixelDataRef[myPixel + BLUE] & 0xff) * TRANSFORM_VALUE;
        thePixel.a = (_myPixelDataRef[myPixel + ALPHA] & 0xff) * TRANSFORM_VALUE;
    }


    public void setPixel(int x, int y, Color thePixel) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + RED] = (byte) (thePixel.r * 255);
        _myPixelDataRef[myPixel + GREEN] = (byte) (thePixel.g * 255);
        _myPixelDataRef[myPixel + BLUE] = (byte) (thePixel.b * 255);
        _myPixelDataRef[myPixel + ALPHA] = (byte) (thePixel.a * 255);
    }


    public float getRf(int x, int y) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        return (_myPixelDataRef[myPixel + RED] & 0xff) * TRANSFORM_VALUE;
    }


    public float getGf(int x, int y) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        return (_myPixelDataRef[myPixel + GREEN] & 0xff) * TRANSFORM_VALUE;
    }


    public float getBf(int x, int y) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        return (_myPixelDataRef[myPixel + BLUE] & 0xff) * TRANSFORM_VALUE;
    }


    public float getAf(int x, int y) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        return (_myPixelDataRef[myPixel + ALPHA] & 0xff) * TRANSFORM_VALUE;
    }


    public void setRf(int x, int y, float theValue) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + RED] = (byte) (theValue * 255);
    }


    public void setGf(int x, int y, float theValue) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + GREEN] = (byte) (theValue * 255);
    }


    public void setBf(int x, int y, float theValue) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + BLUE] = (byte) (theValue * 255);
    }


    public void setAf(int x, int y, float theValue) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + ALPHA] = (byte) (theValue * 255);
    }


    public void setRfs(int x, int y, float theValue) {
        if (theValue < 0) {
            theValue = 0;
        } else if (theValue > 1) {
            theValue = 1;
        }
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + RED] = (byte) (theValue * 255);
    }


    public void setGfs(int x, int y, float theValue) {
        if (theValue < 0) {
            theValue = 0;
        } else if (theValue > 1) {
            theValue = 1;
        }
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + GREEN] = (byte) (theValue * 255);
    }


    public void setBfs(int x, int y, float theValue) {
        if (theValue < 0) {
            theValue = 0;
        } else if (theValue > 1) {
            theValue = 1;
        }
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + BLUE] = (byte) (theValue * 255);
    }


    public void setAfs(int x, int y, float theValue) {
        if (theValue < 0) {
            theValue = 0;
        } else if (theValue > 1) {
            theValue = 1;
        }
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + ALPHA] = (byte) (theValue * 255);
    }


    /* byte specific operations */
    /** @todo
     * in order to remove the dependency on byte[] data the methods below
     * need to be moved to a more specific type of 'Bitmap'.
     * 'ByteBitmap' for example.
     */
    public void getPixel(int x, int y, byte[] thePixel) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        for (int i = 0; i < thePixel.length; ++i) {
            thePixel[i] = _myPixelDataRef[myPixel + i];
        }
    }


    public void setPixel(int x, int y, byte[] thePixel) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        for (int i = 0; i < thePixel.length; ++i) {
            _myPixelDataRef[myPixel + i] = thePixel[i];
        }
    }


    public void setPixelComponent(int x,
                                  int y,
                                  int thePixelComponent,
                                  byte thePixelComponentValue) {
        final int myPixel = (x + y * _myWidth) * NUMBER_OF_PIXEL_COMPONENTS;
        _myPixelDataRef[myPixel + thePixelComponent] = thePixelComponentValue;
    }


    public ByteBitmap duplicate() {
        ByteBitmap myClone = new ByteBitmap(new byte[NUMBER_OF_PIXEL_COMPONENTS * _myWidth * _myHeight],
                                            _myWidth,
                                            _myHeight,
                                            _myComponentOrder);
        System.arraycopy(_myPixelDataRef, 0, myClone._myPixelDataRef, 0, _myPixelDataRef.length);
        return myClone;
    }


    public Object clone() {
        return duplicate();
    }
}
