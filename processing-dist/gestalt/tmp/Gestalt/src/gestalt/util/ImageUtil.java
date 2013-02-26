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
package gestalt.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import static gestalt.Gestalt.*;
import gestalt.material.Color;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.material.texture.bitmap.IntegerBitmap;

import werkzeug.interpolation.InterpolateClamp;
import werkzeug.interpolation.Interpolator;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class ImageUtil {

    public static boolean VERBOSE = false;

    public static final int HORIZONTAL = 0;

    public static final int VERTICAL = 1;

    /* save */

    public static void save(final ByteBitmap myByteBitmap,
                            final String theFileName,
                            int theImageFileFormat) {
        BufferedImage myBufferedImage = convertByteBitmap2BufferedImage(myByteBitmap);
        save(myBufferedImage,
             theFileName,
             theImageFileFormat);
    }


    public static void save(final BufferedImage theImage,
                            final String theFileName,
                            int theImageFileFormat) {

        try {
            /* create directory if needed */
            final File myFile = new File(theFileName);
            final File myParent = myFile.getParentFile();
            if (myParent != null) {
                if (!myParent.exists()) {
                    myParent.mkdirs();
                }
            }
        } catch (Exception ex) {
            System.err.println("### ERROR @ImageUtil.save / " + ex);
        }

        switch (theImageFileFormat) {
            case IMAGE_FILEFORMAT_JPEG:
                 {
                    try {
                        File myFile = new File(theFileName);
                        FileOutputStream myOutputFile = new FileOutputStream(myFile);
                        JPEGEncodeParam myEncodeParam = JPEGCodec.getDefaultJPEGEncodeParam(theImage);
                        myEncodeParam.setQuality(1.0f, true);
                        JPEGImageEncoder myEncoder = JPEGCodec.createJPEGEncoder(myOutputFile);
                        myEncoder.encode(theImage, myEncodeParam);
                        myOutputFile.close();
                    } catch (Exception ex) {
                        System.err.println("### ERROR @ ImageUtil / failed to write JPEG: " + ex);
                    }
                }
                break;
            case IMAGE_FILEFORMAT_PNG:
                 {
                    File file = new File(theFileName);
                    try {
                        javax.imageio.ImageIO.write(theImage, "png", file);
                    } catch (IOException ex) {
                        System.err.println("### ERROR @ ImageUtil / failed to write PNG: " + ex);
                    }
                }
                break;
            default:
                System.err.println("### ERROR @ ImageUtil / couldn t find image format. " + theImageFileFormat);
        }
    }


    /* conversion and image arithmetic */
    public static boolean isPowerOf2(int theValue) {
        int myValue = 1;
        while (myValue < theValue) {
            myValue *= 2;
        }
        if (myValue == theValue) {
            return true;
        } else {
            return false;
        }
    }


    public static int getNextPowerOf2(int theValue) {
        int myValue = 1;
        while (myValue < theValue) {
            myValue *= 2;
        }
        return myValue;
    }


    public static ByteBitmap flipBitmap(ByteBitmap theBitmap) {
        BufferedImage myBufferedImage = ImageUtil.convertByteBitmap2BufferedImage(theBitmap);
        myBufferedImage = ImageUtil.flip(myBufferedImage, ImageUtil.VERTICAL);
        return ImageUtil.convertBufferedImage2ByteBitmap(myBufferedImage);
    }


    public static ByteBitmap convertBufferedImage2ByteBitmap(BufferedImage myImage) {
        int myWidth = myImage.getWidth();
        int myHeight = myImage.getHeight();
        int[] myPackedPixels = new int[myWidth * myHeight];

        /**
         * @todo
         * textures are stored in 32bit, RGBA always.
         */
        byte[] myUnpackedPixels = new byte[myPackedPixels.length * ByteBitmap.NUMBER_OF_PIXEL_COMPONENTS];

        PixelGrabber myPixelGrabber = new PixelGrabber(myImage,
                                                       0,
                                                       0,
                                                       myWidth,
                                                       myHeight,
                                                       myPackedPixels,
                                                       0,
                                                       myWidth);
        try {
            myPixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("### ERROR @ BitmapProducer.bufferedImage2Bitmap / " + e);
        }

        for (int row = myHeight - 1; row >= 0; row--) {
            for (int col = 0; col < myWidth; col++) {
                int myPackedPixelPosition = row * myWidth + col;
                int myUnpackedPixelPosition = myPackedPixelPosition * ByteBitmap.NUMBER_OF_PIXEL_COMPONENTS;
                int myPackedPixel = myPackedPixels[myPackedPixelPosition];
                myUnpackedPixels[myUnpackedPixelPosition + BLUE] = (byte) ((myPackedPixel >> 0) & 0xFF);
                myUnpackedPixels[myUnpackedPixelPosition + GREEN] = (byte) ((myPackedPixel >> 8) & 0xFF);
                myUnpackedPixels[myUnpackedPixelPosition + RED] = (byte) ((myPackedPixel >> 16) & 0xFF);
                myUnpackedPixels[myUnpackedPixelPosition + ALPHA] = (byte) ((myPackedPixel >> 24) & 0xFF);
            }
        }
        return new ByteBitmap(myUnpackedPixels,
                              myWidth,
                              myHeight,
                              BITMAP_COMPONENT_ORDER_RGBA);
    }


    public static BufferedImage convertByteBitmap2BufferedImage(ByteBitmap theBitmap) {
        BufferedImage myImage = new BufferedImage(theBitmap.getWidth(),
                                                  theBitmap.getHeight(),
                                                  BufferedImage.TYPE_4BYTE_ABGR);
        byte[] src = theBitmap.getByteDataRef();
        byte[] dest = ((DataBufferByte) myImage.getRaster().getDataBuffer()).getData();

        if (src.length != dest.length) {
            System.err.println("### ERROR @ Util.bitmap2BufferedImage / malformed bitmap");
        }

        for (int j = 0; j < src.length; j += 4) {
            dest[j + ALPHA] = src[j + RED];
            dest[j + BLUE] = src[j + GREEN];
            dest[j + GREEN] = src[j + BLUE];
            dest[j + RED] = src[j + ALPHA];
        }

        return myImage;
    }


    public static BufferedImage convertByteBitmap2BufferedImageBGR(ByteBitmap theBitmap) {
        BufferedImage myImage = new BufferedImage(theBitmap.getWidth(),
                                                  theBitmap.getHeight(),
                                                  BufferedImage.TYPE_3BYTE_BGR);
        byte[] src = theBitmap.getByteDataRef();
        byte[] dest = ((DataBufferByte) myImage.getRaster().getDataBuffer()).getData();

        if (src.length / 4 != dest.length / 3) {
            System.err.println("### ERROR @ Util.bitmap2BufferedImage / malformed bitmap");
        }

        for (int j = 0; j < src.length / 4; j++) {
            dest[j * 3 + BLUE] = src[j * 4 + RED];
            dest[j * 3 + GREEN] = src[j * 4 + GREEN];
            dest[j * 3 + RED] = src[j * 4 + BLUE];
        }

        return myImage;
    }


    public static BufferedImage convertIntegerBitmap2BufferedImage(IntegerBitmap theBitmap) {
        final BufferedImage myImage = new BufferedImage(theBitmap.getWidth(),
                                                        theBitmap.getHeight(),
                                                        BufferedImage.TYPE_INT_ARGB);
        final int[] src = theBitmap.getIntDataRef();
        final int[] dest = ((DataBufferInt) myImage.getRaster().getDataBuffer()).getData();

        if (src.length != dest.length) {
            System.err.println("### ERROR @ Util.convertIntegerBitmap2BufferedImage / malformed bitmap");
        }
        System.arraycopy(src, 0, dest, 0, src.length);

        return myImage;
    }


    public static IntegerBitmap convertBufferedImage2IntegerBitmap(BufferedImage theBitmap) {
        final int[] src = ((DataBufferInt) theBitmap.getRaster().getDataBuffer()).getData();
        final IntegerBitmap myImage = IntegerBitmap.getDefaultImageBitmap(theBitmap.getWidth(), theBitmap.getHeight());
        final int[] dest = myImage.getIntDataRef();

        if (theBitmap.getType() != BufferedImage.TYPE_INT_ARGB) {
            System.err.println("### WARNING @ Util.convertBufferedImage2IntegerBitmap / buffered image type is not ARGB.");
        }

        if (src.length != dest.length) {
            System.err.println("### ERROR @ Util.convertBufferedImage2IntegerBitmap / malformed bitmap");
        }
        System.arraycopy(src, 0, dest, 0, src.length);

        return myImage;
    }


    /* bitmap manipualtion */
    public static void invert(final ByteBitmap theBitmap, boolean theInvertAlpha) {
        final Color myPixel = new Color();
        for (int x = 0; x < theBitmap.getWidth(); ++x) {
            for (int y = 0; y < theBitmap.getHeight(); ++y) {
                /* read */
                theBitmap.getPixel(x, y, myPixel);
                /* manipulate pixel */
                myPixel.r = 1 - myPixel.r;
                myPixel.g = 1 - myPixel.g;
                myPixel.b = 1 - myPixel.b;
                if (theInvertAlpha) {
                    myPixel.a = 1 - myPixel.a;
                }
                /* write back */
                theBitmap.setPixel(x, y, myPixel);
            }
        }
    }


    public static void gradientCurve(final ByteBitmap theBitmap, Interpolator theInterpolator) {
        Interpolator myInterpolator = new Interpolator(0.0f, 1.0f, new InterpolateClamp(0.0f, 1.0f));
        Color myPixel = new Color();
        for (int x = 0; x < theBitmap.getWidth(); ++x) {
            for (int y = 0; y < theBitmap.getHeight(); ++y) {
                /* read */
                theBitmap.getPixel(x, y, myPixel);
                /* manipulate pixel */
                myPixel.r = myInterpolator.get(theInterpolator.get(myPixel.r));
                myPixel.g = myInterpolator.get(theInterpolator.get(myPixel.g));
                myPixel.b = myInterpolator.get(theInterpolator.get(myPixel.b));
                myPixel.a = myInterpolator.get(theInterpolator.get(myPixel.a));
                /* write back */
                theBitmap.setPixel(x, y, myPixel);
            }
        }
    }


    public static void edgeDetection(final ByteBitmap theBitmap, float theValue) {
        final float myValue = -1;
        float[] theKernel = {myValue, theValue, myValue};
        convolve1DH(theBitmap, theKernel);
        convolve1DV(theBitmap, theKernel);
    }


    /**
     * there seems to be a problem with transparency.
     * @param theBitmap ByteBitmap
     * @param theRadius float
     */
    public static void gaussianBlur(final ByteBitmap theBitmap, float theRadius) {
        int diameter = (int) (2 * theRadius + 1);
        float invrsq = 1.0F / (theRadius * theRadius);

        float[] theKernel = new float[diameter];

        float sum = 0.0F;
        for (int i = 0; i < diameter; ++i) {
            float d = i - theRadius;
            float val = (float) Math.exp(-d * d * invrsq);
            theKernel[i] = val;
            sum += val;
        }
        float invsum = 1.0F / sum;
        for (int i = 0; i < diameter; ++i) {
            theKernel[i] *= invsum;
        }

        /* seperated bluring */
        convolve1DH(theBitmap, theKernel);
        convolve1DV(theBitmap, theKernel);
    }


    public static void convolve1DH(final ByteBitmap theBitmap, float[] theKernel) {
        final int myWidth = theBitmap.getWidth();
        final int myHeight = theBitmap.getHeight();
        final int myKernelRange = theKernel.length / 2;

        /* copy bitmap */
        ByteBitmap mySource = ByteBitmap.getDefaultImageBitmap(theBitmap.getWidth(), theBitmap.getHeight());
        mySource.setByteDataRef(theBitmap.getByteDataRef());
        theBitmap.setByteDataRef(new byte[mySource.getByteDataRef().length]);

        /* convolve the image */
        Color mySum = new Color();
        Color myNeighbor = new Color();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                mySum.set(0, 0, 0, 0);
                for (int j = -myKernelRange; j < myKernelRange; j++) {
                    // Reflect x-j to not exceed array boundary
                    int myXOffset = x - j;
                    if (myXOffset < 0) {
//                        myXOffset = myXOffset + myWidth;
                        myXOffset = 0;
                    } else if (x - j >= myWidth) {
//                        myXOffset = myXOffset - myWidth;
                        myXOffset = myWidth - 1;
                    }
                    mySource.getPixel(myXOffset, y, myNeighbor);
                    mySum.r = mySum.r + theKernel[j + myKernelRange] * myNeighbor.r;
                    mySum.g = mySum.g + theKernel[j + myKernelRange] * myNeighbor.g;
                    mySum.b = mySum.b + theKernel[j + myKernelRange] * myNeighbor.b;
                    mySum.a = mySum.a + theKernel[j + myKernelRange] * myNeighbor.a;
                }
                mySum.r = Math.min(1, Math.max(0, mySum.r));
                mySum.g = Math.min(1, Math.max(0, mySum.g));
                mySum.b = Math.min(1, Math.max(0, mySum.b));
                mySum.a = Math.min(1, Math.max(0, mySum.a));
                theBitmap.setPixel(x, y, mySum);
            }
        }
    }


    public static void convolve1DV(final ByteBitmap theBitmap, float[] theKernel) {
        final int myWidth = theBitmap.getWidth();
        final int myHeight = theBitmap.getHeight();
        final int myKernelRange = theKernel.length / 2;

        /* copy bitmap */
        ByteBitmap mySource = ByteBitmap.getDefaultImageBitmap(theBitmap.getWidth(), theBitmap.getHeight());
        mySource.setByteDataRef(theBitmap.getByteDataRef());
        theBitmap.setByteDataRef(new byte[mySource.getByteDataRef().length]);

        /* convolve the image */
        Color mySum = new Color();
        Color myNeighbor = new Color();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                mySum.set(0, 0, 0, 0);
                for (int j = -myKernelRange; j < myKernelRange; j++) {
                    // Reflect y-j to not exceed array boundary
                    int myYOffset = y - j;
                    if (myYOffset < 0) {
                        myYOffset = myYOffset + myHeight;
                    } else if (myYOffset >= myHeight) {
                        myYOffset = myYOffset - myHeight;
                    }
                    mySource.getPixel(x, myYOffset, myNeighbor);
                    mySum.r = mySum.r + theKernel[j + myKernelRange] * myNeighbor.r;
                    mySum.g = mySum.g + theKernel[j + myKernelRange] * myNeighbor.g;
                    mySum.b = mySum.b + theKernel[j + myKernelRange] * myNeighbor.b;
                    mySum.a = mySum.a + theKernel[j + myKernelRange] * myNeighbor.a;
                }
                mySum.r = Math.min(1, Math.max(0, mySum.r));
                mySum.g = Math.min(1, Math.max(0, mySum.g));
                mySum.b = Math.min(1, Math.max(0, mySum.b));
                mySum.a = Math.min(1, Math.max(0, mySum.a));
                theBitmap.setPixel(x, y, mySum);
            }
        }
    }


    /* bufferedimage manipulation */
    public static BufferedImage flip(BufferedImage myImage, int type) {
        AffineTransform tx;
        AffineTransformOp op;
        switch (type) {
            case HORIZONTAL:
                tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-myImage.getWidth(), 0);
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                myImage = op.filter(myImage, null);
                break;
            case VERTICAL:
                tx = AffineTransform.getScaleInstance(1, -1);
                tx.translate(0, -myImage.getHeight());
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                myImage = op.filter(myImage, null);
                break;
            default:
                break;
        }
        return myImage;
    }


    public static ByteBitmap scale(final ByteBitmap theImage, float theScale) {
        BufferedImage myBufferedImage = convertByteBitmap2BufferedImage(theImage);
        return convertBufferedImage2ByteBitmap(scale(myBufferedImage, theScale));
    }


    public static BufferedImage scale(final BufferedImage theImage, float theScale) {
        int myWidth = (int) (theImage.getWidth() * theScale);
        int myHeight = (int) (theImage.getHeight() * theScale);

        final java.awt.Image myScaledImage = theImage.getScaledInstance(myWidth, myHeight, BufferedImage.SCALE_SMOOTH);
        final BufferedImage myScaledBufferedImage = new BufferedImage(myWidth, myHeight, theImage.getType());
        myScaledBufferedImage.createGraphics().drawImage(myScaledImage, 0, 0, null);
        return myScaledBufferedImage;
    }


    public static ByteBitmap scaleTo(final ByteBitmap theImage, int myWidth, int myHeight) {
        BufferedImage myBufferedImage = convertByteBitmap2BufferedImage(theImage);
        return convertBufferedImage2ByteBitmap(scaleTo(myBufferedImage, myWidth, myHeight));
    }


    public static BufferedImage scaleTo(final BufferedImage theImage, int myWidth, int myHeight) {
        myWidth = Math.max(1, myWidth);
        myHeight = Math.max(1, myHeight);
        final java.awt.Image myScaledImage = theImage.getScaledInstance(myWidth, myHeight, BufferedImage.SCALE_SMOOTH);
        final BufferedImage myScaledBufferedImage = new BufferedImage(myWidth, myHeight, theImage.getType());
        myScaledBufferedImage.createGraphics().drawImage(myScaledImage, 0, 0, null);
        return myScaledBufferedImage;
    }


    public static BufferedImage filter(final BufferedImage image, BufferedImageOp op) {
        BufferedImage filteredImage = new BufferedImage(image.getWidth(),
                                                        image.getHeight(),
                                                        image.getType());
        op.filter(image, filteredImage);
        return filteredImage;
    }


    public static BufferedImage convolve(final BufferedImage image, float[] elements, int theWidth, int theHeight) {
        Kernel kernel = new Kernel(theWidth, theHeight, elements);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return filter(image, op);
    }


    public static ByteBitmap blur(final ByteBitmap theImage, int theRadius) {
        return ImageUtil.convertBufferedImage2ByteBitmap(
                ImageUtil.blur(ImageUtil.convertByteBitmap2BufferedImage(theImage), theRadius));
    }


    public static BufferedImage blur(final BufferedImage image) {
        float weight = 1.0f / 9.0f;
        float[] elements = new float[9];
        for (int i = 0; i < 9; i++) {
            elements[i] = weight;
        }
        return convolve(image, elements, 3, 3);
    }


    public static BufferedImage blur(final BufferedImage image, int theRadius) {
        /* create a blur kernel */
        int myDiameter = 2 * theRadius + 1;
        float invrsq = 1.0f / (theRadius * theRadius);
        float[] elements = new float[myDiameter];
        float sum = 0.0f;
        for (int i = 0; i < myDiameter; ++i) {
            float d = i - theRadius;
            float val = (float) Math.exp(-d * d * invrsq);
            elements[i] = val;
            sum += val;
        }
        float invsum = 1.0f / sum;
        for (int i = 0; i < myDiameter; ++i) {
            elements[i] *= invsum;
        }
        /* perform two 1D blur passes */
        BufferedImage myImage = convolve(image, elements, 1, elements.length);
        return convolve(myImage, elements, elements.length, 1);
    }


    public static BufferedImage gaussianBlur(final BufferedImage image, float sigma) {

        final int radius = (int) Math.round(3.0 * sigma) - 1;
        final int size = radius + radius + 1;
        float[][] kernel = new float[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                float x = i - radius;
                float y = j - radius;
                kernel[i][j] = (float) Math.exp(-(x * x + y * y) / (2.0f * sigma * sigma)) /
                        (2.0f * PI * sigma * sigma);
            }
        }

        /* copy kernel to 1d array */
        float[] elements = new float[kernel.length * kernel.length];
        int myCounter = 0;
        for (int y = 0; y < kernel.length; y++) {
            for (int x = 0; x < kernel[y].length; x++) {
                elements[myCounter++] = kernel[x][y];
            }
        }

        return convolve(image, elements, size, size);
    }


    public static BufferedImage sharpen(final BufferedImage image) {
        float[] elements = {0.0f, -1.0f, 0.0f,
            -1.0f, 5.f, -1.0f,
            0.0f, -1.0f, 0.0f
        };
        return convolve(image, elements, 3, 3);
    }


    public static BufferedImage edgeDetect(BufferedImage image) {
        float[] elements = {0.0f, -1.0f, 0.0f,
            -1.0f, 4.f, -1.0f,
            0.0f, -1.0f, 0.0f
        };
        return convolve(image, elements, 3, 3);
    }


    public static BufferedImage brighten(BufferedImage image) {
        float a = 1.5f;
        float b = -20.0f;
        RescaleOp op = new RescaleOp(a, b, null);
        return filter(image, op);
    }


    public static void add(ByteBitmap a, ByteBitmap b) {
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                Color ca = new Color(a.getPixel(x, y));
                Color cb = new Color(b.getPixel(x, y));
                ca.add(cb);
                a.setPixel(x, y, ca);
            }
        }
    }


    public static BufferedImage rotate(BufferedImage image, float theRadiansAngle) {
        AffineTransform transform = AffineTransform.getRotateInstance(theRadiansAngle,
                                                                      image.getWidth() / 2,
                                                                      image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform,
                                                     AffineTransformOp.TYPE_BILINEAR);
        return filter(image, op);
    }


    /* display */
    public static JFrame displayBitmap(final Bitmap theBitamp,
                                       final String theWindowName,
                                       boolean theUndecorated) {
        final JFrame myFrame;
        if (theBitamp instanceof ByteBitmap) {
            myFrame = displayBytesAsImage(((ByteBitmap) theBitamp).getByteDataRef(),
                                          theBitamp.getWidth(),
                                          theBitamp.getHeight(),
                                          4,
                                          theWindowName,
                                          theUndecorated);
        } else if (theBitamp instanceof IntegerBitmap) {
            myFrame = displayIntsAsImage(((IntegerBitmap) theBitamp).getIntDataRef(),
                                         theBitamp.getWidth(),
                                         theBitamp.getHeight(),
                                         theWindowName,
                                         theUndecorated);
        } else if (theBitamp instanceof ByteBufferBitmap) {
            myFrame = displayByteBufferAsImage(((ByteBufferBitmap) theBitamp).getByteBufferDataRef(),
                                               theBitamp.getWidth(),
                                               theBitamp.getHeight(),
                                               theWindowName,
                                               theUndecorated);
        } else {
            myFrame = null;
        }
        return myFrame;
    }


    public static JFrame displayBitmap(final Bitmap theBitamp) {
        return displayBitmap(theBitamp, "", false);
    }


    public static JFrame displayBytesAsImage(byte[] data,
                                             int width,
                                             int height,
                                             int channels,
                                             String theWindowName,
                                             boolean theUndecorated) {
        BufferedImage img = null;
        switch (channels) {
            case 4:
                img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                break;
            case 3:
                img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                break;
            case 1:
                img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                break;
        }
        WritableRaster raster = img.getRaster();
        DataBufferByte db = (DataBufferByte) raster.getDataBuffer();
        byte[] src = data;
        byte[] dest = db.getData();

        if (src.length != dest.length) {
            System.err.println("### ERROR @Util / image format");
        }

        try {
            switch (channels) {
                case 4:
                    for (int j = 0; j < src.length; j += 4) {
                        dest[j + ALPHA] = src[j + RED];
                        dest[j + BLUE] = src[j + GREEN];
                        dest[j + GREEN] = src[j + BLUE];
                        dest[j + RED] = src[j + ALPHA];
                    }
                    break;
                case 3:
                    for (int j = 0; j < src.length; j += 3) {
                        dest[j + 0] = src[j + 2];
                        dest[j + 1] = src[j + 1];
                        dest[j + 2] = src[j + 0];
                    }
                    break;
                case 1:
                    System.arraycopy(src, 0, dest, 0, src.length);
                    break;
            }
        } catch (Exception ex) {
            System.err.println("### ERROR @Util.displayBytesAsImage / malformed bitmap: " + ex);
        }

        return displayBufferedImage(img, theWindowName, theUndecorated);
    }


    public static JFrame displayByteBufferAsImage(ByteBuffer data,
                                                  int width,
                                                  int height,
                                                  String theWindowName,
                                                  boolean theUndecorated) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        WritableRaster raster = img.getRaster();
        DataBufferByte db = (DataBufferByte) raster.getDataBuffer();
        byte[] dest = db.getData();

        for (int j = 0; j < dest.length; j += 4) {
            dest[j + ALPHA] = data.get(j + RED);
            dest[j + BLUE] = data.get(j + GREEN);
            dest[j + GREEN] = data.get(j + BLUE);
            dest[j + RED] = data.get(j + ALPHA);
        }
        return displayBufferedImage(img, theWindowName, theUndecorated);
    }


    public static JFrame displayIntsAsImage(int[] data,
                                            int width,
                                            int height,
                                            String theWindowName,
                                            boolean theUndecorated) {

        BufferedImage myImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster myRaster = myImage.getRaster();
        DataBufferInt myDataBuffer = (DataBufferInt) myRaster.getDataBuffer();
        int[] mySource = data;
        int[] myDestination = myDataBuffer.getData();

        if (mySource.length != myDestination.length) {
            System.err.println("### ERROR @Util / image format");
        }

        System.arraycopy(mySource, 0, myDestination, 0, mySource.length);

        return displayBufferedImage(myImage, theWindowName, theUndecorated);
    }


    public static JFrame displayBufferedImage(BufferedImage img, String theWindowName, boolean theUndecorated) {
        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel();
        label.setIcon(icon);
        JFrame frame = new JFrame(theWindowName);
        frame.getContentPane().add(label);
        if (!theUndecorated) {
            frame.addWindowListener(new WindowAdapter() {

                                public void windowClosing(WindowEvent e) {
                                    System.exit(0);
                                }
                            });
        } else {
            frame.setUndecorated(true);
        }
        frame.pack();
        frame.setVisible(true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) (dimension.width - frame.getSize().getWidth()) / 2,
                          (int) (dimension.height - frame.getSize().getHeight()) / 2);
        return frame;
    }
}
