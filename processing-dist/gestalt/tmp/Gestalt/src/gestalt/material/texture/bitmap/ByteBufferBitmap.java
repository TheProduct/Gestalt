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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import gestalt.Gestalt;
import gestalt.material.Color;
import gestalt.material.texture.Bitmap;
import gestalt.util.ImageUtil;


public class ByteBufferBitmap
    implements Bitmap {

    private static final int NUMBER_OF_PIXEL_COMPONENTS = 4;

    private final int _myWidth;

    private final int _myHeight;

    private final int _myMediaType;

    private final int _myProportionType;

    private ByteBuffer _myPixelDataRef;

    public ByteBufferBitmap(ByteBuffer thePixels,
                            int theWidth,
                            int theHeight,
                            int theMediaType) {
        _myPixelDataRef = thePixels;
        _myWidth = theWidth;
        _myHeight = theHeight;
        _myMediaType = theMediaType;
        if (ImageUtil.isPowerOf2(theWidth) &&
            ImageUtil.isPowerOf2(theHeight)) {
            _myProportionType = Gestalt.TEXTURE_PROPORTION_POWEROF2;
        } else {
            _myProportionType = Gestalt.TEXTURE_PROPORTION_ARBITRARY;
        }
    }


    public static ByteBufferBitmap getDefaultImageBitmap(int theWidth, int theHeight) {
        ByteBuffer myImageData = ByteBuffer.allocateDirect(theWidth * theHeight * NUMBER_OF_PIXEL_COMPONENTS);
        myImageData.order(ByteOrder.nativeOrder());
        return new ByteBufferBitmap(myImageData,
                                    theWidth,
                                    theHeight,
                                    Gestalt.BITMAP_COMPONENT_ORDER_RGBA);
    }


    public int getWidth() {
        return _myWidth;
    }


    public int getHeight() {
        return _myHeight;
    }


    public int getComponentOrder() {
        return _myMediaType;
    }


    public int getProportionType() {
        return _myProportionType;
    }


    public Object getDataRef() {
        return _myPixelDataRef;
    }


    public void setDataRef(Object theDataRef) {
        try {
            ByteBuffer myDataRef = (ByteBuffer) theDataRef;
            setByteBufferDataRef(myDataRef);
        } catch (Exception theException) {
            System.err.println("### ERROR @ ByteBufferBitmap / " + theException);
            theException.printStackTrace(System.err);
        }
    }


    public void setByteBufferDataRef(ByteBuffer theDataRef) {
        if (_myPixelDataRef.capacity() != theDataRef.capacity()) {
            System.err.println("### ERROR @ " + this.getClass() + " / data reference sizes do not match.");
        }
        _myPixelDataRef = theDataRef;
    }


    public Color getPixel(int x, int y) {
        Color myColor = new Color();
        getPixel(x, y, myColor);
        return myColor;
    }


    public void getPixel(int x, int y, Color thePixel) {
        System.err.println("### WARNING @ " + getClass().getName() + " / getPixel() not implemented.");
    }


    public void setPixel(int x, int y, Color thePixel) {
        System.err.println("### WARNING @ " + getClass().getName() + " / setPixel() not implemented.");
    }


    public ByteBuffer getByteBufferDataRef() {
        return _myPixelDataRef;
    }


    public static ByteBufferBitmap load(final InputStream theStream) {

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

        DataBufferByte myDataBuffer = (DataBufferByte) myImage.getData().getDataBuffer();
        ByteBuffer myImageData = ByteBuffer.allocateDirect(myWidth * myHeight * NUMBER_OF_PIXEL_COMPONENTS);
        myImageData.order(ByteOrder.nativeOrder());

        if (myDataBuffer.getData().length == myWidth * myHeight * 4) {
            /* 32 bit */
            myImageData.put(myDataBuffer.getData());
        } else if (myDataBuffer.getData().length == myWidth * myHeight * 3) {
            /* 24 bit */
            System.err.println("### WARNING @ " + ByteBufferBitmap.class.getName() +
                               " / loading 24bit images is not optimized, use 32bit instead.");

            for (int i = 0; i < myDataBuffer.getData().length; i += 3) {
                myImageData.put(myDataBuffer.getData()[i]);
                myImageData.put(myDataBuffer.getData()[i + 1]);
                myImageData.put(myDataBuffer.getData()[i + 2]);
                myImageData.put( (byte) 0xFF);
            }
        } else if (myDataBuffer.getData().length == myWidth * myHeight) {
            /* 8 bit */
            System.err.println("### WARNING @ " + ByteBufferBitmap.class.getName() +
                               " / loading 8bit images is not optimized, use 32bit instead.");

            for (int i = 0; i < myDataBuffer.getData().length; i++) {
                myImageData.put(myDataBuffer.getData()[i]);
                myImageData.put(myDataBuffer.getData()[i]);
                myImageData.put(myDataBuffer.getData()[i]);
                myImageData.put( (byte) 0xFF);
            }
        } else {
            System.err.println("### ERROR @ " + ByteBufferBitmap.class.getName() +
                               " / image format unidentified, use 32bit images instead.");
        }

        myImageData.rewind();

        return new ByteBufferBitmap(myImageData,
                                    myWidth,
                                    myHeight,
                                    Gestalt.BITMAP_COMPONENT_ORDER_RGBA);
    }
}
