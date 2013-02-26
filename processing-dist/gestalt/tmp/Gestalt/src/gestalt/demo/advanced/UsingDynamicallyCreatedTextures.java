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


package gestalt.demo.advanced;


import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.ByteBitmap;


/**
 * this demo shows how to create textures dynamically
 *
 * 1. create a bitmap
 * 2. create texture and load bitmap into texture
 * 3. assign texture to plane
 * 4. set color4f values of bitmap
 * 5. reload bitmap into texture
 *
 */

public class UsingDynamicallyCreatedTextures
    extends AnimatorRenderer {

    private int _myCounter;

    private TexturePlugin _myImageTexture;

    private Plane _myImagePlane;

    private ByteBitmap _myBitmap;

    private int _myColorComponent;

    public void setup() {

        framerate(120);

        /*
         * create a new bitmap
         * bitmaps hold the following information:
         * 1. pixelinformation; number of pixels, number of channels per pixel
         * 2. width and height of the bitmap
         * 3. number of channels per pixel
         * 4. type of image
         *
         * in this case we create a bitmap that is 64x64px,
         * with 4 channels per pixel and of type RGBA
         */
        _myBitmap = ByteBitmap.getDefaultImageBitmap(32, 32); /* RGBA image */

        /* create a plane */
        _myImagePlane = drawablefactory().plane();

        /* create an empty texture */
        _myImageTexture = drawablefactory().texture();

        /* define filtering of texture */
        _myImageTexture.setFilterType(TEXTURE_FILTERTYPE_NEAREST);

        /* load your created bitmap into the texture */
        _myImageTexture.load(_myBitmap);

        /* assign the texture to the plane */
        _myImagePlane.material().addPlugin(_myImageTexture);

        /* set size of plane */
        _myImagePlane.setPlaneSizeToTextureSize();
        _myImagePlane.scale().x = displaycapabilities().width;
        _myImagePlane.scale().y = displaycapabilities().height;

        /* set origin */
        _myImagePlane.origin(SHAPE_ORIGIN_CENTERED);

        /* add plane to renderbin */
        bin(BIN_3D).add(_myImagePlane);

        /* init variables */
        _myColorComponent = RED;

        /* prints help for this demo */
        printHelp();
    }


    public void loop(float theDeltaTime) {
        /*
         * the following is used to calculate the colors for each pixel
         * in the texture.
         */
        _myCounter++;
        int myX = _myCounter % _myBitmap.getWidth();
        int myY = (_myCounter / _myBitmap.getHeight()) % _myBitmap.getHeight();
        byte myValueA = (byte) (Math.cos(Math.toRadians(_myCounter * 3.33)) * 64 + 128);
        byte myValueB = (byte) (Math.sin(Math.toRadians(_myCounter)) * 64 + 128);

        /*
         * if key is pressed you switch the colorcomponent, that should
         * be altered later
         */
        if (event().keyPressed) {
            switch (event().key) {
                case 'r':
                    _myColorComponent = RED;
                    break;
                case 'g':
                    _myColorComponent = GREEN;
                    break;
                case 'b':
                    _myColorComponent = BLUE;
                    break;
                case 'a':
                    _myColorComponent = ALPHA;
                    break;
                case 'h':
                    printHelp();
                    break;
            }
        }
        /*
         * construct a pixel
         * each pixel is defined through 4 bytes, 1 byte per color4f channel.
         * so colors are assigned through byte values from -128..128 for
         * each channel.
         * in this case the resulting pixel is completely opaque and has
         * the calculated value assigned to the blue channel
         */
        byte[] myPixel = new byte[4];
        myPixel[RED] = (byte) 0;
        myPixel[GREEN] = (byte) 0;
        myPixel[BLUE] = myValueB;
        myPixel[ALPHA] = (byte) 255;

        /*
         * there are two ways to assign color4f values to a pixel
         * 1. overwrite a pixel through its coordinates in the bitmap
         * 2. change one specific colorcomponent of one pixel defined
         * through its coordinates in the bitmap
         */
        _myBitmap.setPixel(myX,
                           myY,
                           myPixel);

        _myBitmap.setPixelComponent(myX,
                                    myY,
                                    _myColorComponent,
                                    myValueA);

        /* after changing the bitmap, the bitmap has to be reloaded */
        _myImageTexture.reload();

        /* rotate plane */
        _myImagePlane.rotation().x = (float) event().mouseX / (float) displaycapabilities().width;
        _myImagePlane.rotation().z = (float) event().mouseY / (float) displaycapabilities().height;
    }


    private void printHelp() {
        System.out.println("### INFO / How to use the demo:");
        System.out.println("### press 'h' for help");
        System.out.println("### press 'r' / 'g' / 'b' / 'a' to switch between color components");
    }


    public static void main(String[] arg) {
        DisplayCapabilities myDisplay = new DisplayCapabilities();
        myDisplay.backgroundcolor.set(1);
        myDisplay.centered = true;
        myDisplay.undecorated = false;
        myDisplay.fullscreen = false;
        myDisplay.width = 512;
        myDisplay.height = 512;
        new UsingDynamicallyCreatedTextures().init(myDisplay);
    }
}
