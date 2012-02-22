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


package gestalt.material.texture;

import gestalt.material.Color;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;

import werkzeug.interpolation.InterpolateClamp;
import werkzeug.interpolation.Interpolator;

import java.awt.image.BufferedImage;


public class BitmapFilter {

    public void curve(final ByteBitmap theBitmap, Interpolator theInterpolator) {
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


    public void edgedetection(final ByteBitmap theBitmap, float theValue) {
        final float myValue = -1;
        float[] theKernel = {myValue, theValue, myValue};
        convolve1DH(theBitmap, theKernel);
        convolve1DV(theBitmap, theKernel);
    }


    public void gaussianblur(final ByteBitmap theBitmap, int theRadius) {
        int diameter = 2 * theRadius + 1;
        float invrsq = 1.0F / (theRadius * theRadius);

        float[] theKernel = new float[diameter];

        float sum = 0.0f;
        for (int i = 0; i < diameter; ++i) {
            float d = i - theRadius;
            float val = (float) Math.exp( -d * d * invrsq);
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


    public void convolve1DH(final ByteBitmap theBitmap, float[] theKernel) {
        int width = theBitmap.getWidth();
        int height = theBitmap.getHeight();
        int m2 = theKernel.length / 2;

        /* copy bitmap */
        ByteBitmap mySource = ByteBitmap.getDefaultImageBitmap(theBitmap.getWidth(), theBitmap.getHeight());
        mySource.setDataRef(theBitmap.getDataRef());
        theBitmap.setDataRef(new byte[mySource.getByteDataRef().length]);

        /* convolve the image */
        Color mySum = new Color();
        Color myNeighbor = new Color();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mySum.set(0, 0, 0, 0);
                for (int j = -m2; j < m2; j++) {
                    int xp = x - j;
                    if (xp < 0) {
                        xp = xp + width;
                    } else if (x - j >= width) {
                        xp = xp - width;
                    }
                    mySource.getPixel(xp, y, myNeighbor);
                    mySum.r = mySum.r + theKernel[j + m2] * myNeighbor.r;
                    mySum.g = mySum.g + theKernel[j + m2] * myNeighbor.g;
                    mySum.b = mySum.b + theKernel[j + m2] * myNeighbor.b;
                    mySum.a = mySum.a + theKernel[j + m2] * myNeighbor.a;
                    mySum.r = Math.min(1, Math.max(0, mySum.r));
                    mySum.g = Math.min(1, Math.max(0, mySum.g));
                    mySum.b = Math.min(1, Math.max(0, mySum.b));
                    mySum.a = Math.min(1, Math.max(0, mySum.a));
                }
                theBitmap.setPixel(x, y, mySum);
            }
        }
    }


    public void convolve1DV(final ByteBitmap theBitmap, float[] kernel) {
        int width = theBitmap.getWidth();
        int height = theBitmap.getHeight();
        int m2 = kernel.length / 2;

        /* copy bitmap */
        ByteBitmap mySource = ByteBitmap.getDefaultImageBitmap(theBitmap.getWidth(), theBitmap.getHeight());
        mySource.setDataRef(theBitmap.getDataRef());
        theBitmap.setDataRef(new byte[mySource.getByteDataRef().length]);

        /* convolve the image */
        Color mySum = new Color();
        Color myNeighbor = new Color();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mySum.set(0, 0, 0, 0);
                for (int j = -m2; j < m2; j++) {
                    int yp = y - j;
                    if (yp < 0) {
                        yp = yp + height;
                    } else if (yp >= height) {
                        yp = yp - height;
                    }
                    mySource.getPixel(x, yp, myNeighbor);
                    mySum.r = mySum.r + kernel[j + m2] * myNeighbor.r;
                    mySum.g = mySum.g + kernel[j + m2] * myNeighbor.g;
                    mySum.b = mySum.b + kernel[j + m2] * myNeighbor.b;
                    mySum.a = mySum.a + kernel[j + m2] * myNeighbor.a;
                    mySum.r = Math.min(1, Math.max(0, mySum.r));
                    mySum.g = Math.min(1, Math.max(0, mySum.g));
                    mySum.b = Math.min(1, Math.max(0, mySum.b));
                    mySum.a = Math.min(1, Math.max(0, mySum.a));
                }
                theBitmap.setPixel(x, y, mySum);
            }
        }
    }


    public ByteBitmap scale(final ByteBitmap theBitmap, float theScale) {
        /** @todo SLOW! replace this by homegrown method. */
        BufferedImage myBufferedImage = ImageUtil.convertByteBitmap2BufferedImage(theBitmap);
        myBufferedImage = ImageUtil.scale(myBufferedImage, theScale);
        ByteBitmap myResult = ImageUtil.convertBufferedImage2ByteBitmap(myBufferedImage);
        return myResult;
    }
}
