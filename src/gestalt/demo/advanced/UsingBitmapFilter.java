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


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.BitmapFilter;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.ByteBitmap;

import werkzeug.interpolation.InterpolateBezier;
import werkzeug.interpolation.Interpolator;

import data.Resource;


/**
 * this demo shows how to perform filters on bitmaps<br/>
 * <br/>
 * 1. create texture and load bitmap into texture<br/>
 * 2. assign texture to plane<br/>
 * 3. get instance from BitmapFilter<br/>
 * 4. perform filtering and/or color4f curving on bitmap
 *
 */

public class UsingBitmapFilter
    extends AnimatorRenderer {

    private TexturePlugin _myImageTexture;

    private Plane _myImagePlane;

    public void setup() {

        /*
         * setup displaycapabilities
         * see also "UsingDisplay"
         */
        displaycapabilities().backgroundcolor.set(0.2f);
        updateDisplayCapabilities();

        /* create plane */
        _myImagePlane = drawablefactory().plane();

        /* create empty texture */
        _myImageTexture = drawablefactory().texture();

        /* define filtering mode */
        _myImageTexture.setFilterType(TEXTURE_FILTERTYPE_LINEAR);

        /*
         * create a bitmap from a file and load it into the texture
         * you can also create a new Bitmap from scratch, without an external
         * file. see "UsingDynamicallyCreatedTextures"
         */
        _myImageTexture.load(Bitmaps.getBitmap(Resource.getPath("demo/common/auto.png")));

        /* asign texture to plane */
        _myImagePlane.material().addPlugin(_myImageTexture);

        /* set size of plane */
        _myImagePlane.setPlaneSizeToTextureSize();

        /* set origin */
        _myImagePlane.origin(SHAPE_ORIGIN_CENTERED);

        /* add plane to render bin */
        bin(BIN_3D).add(_myImagePlane);

        /*
         * to use the filters for bitmapmanipulation you need to get
         * an object of class BitmapFilter
         */
        BitmapFilter myBitmapFilter = new BitmapFilter();

        /*
         * using that object, you can perform image manipulation
         * operations on a Bitmap.
         * here we use the bitmap from the previously created texture to
         * perform a gaussian blur
         */
        myBitmapFilter.gaussianblur( (ByteBitmap) _myImageTexture.bitmap(), 5);

        /*
         * after blurring the image, we adjust the color4f values of the bitmap
         * by calling the curve() method with the bitmap and an interpolator.
         * interpolators are used to transform values between 0..1 in a specific
         * way. here these values are transformed through a bezier curve, that
         * is defined by two handles.
         * this bezier-interpolator is then used in the curve() method to
         * alter the color4f values
         */
        Interpolator myInterpolator;
        myInterpolator = new Interpolator(0.0f, 1.0f, new InterpolateBezier(3.0f, -1.0f));
        myBitmapFilter.curve( (ByteBitmap) _myImageTexture.bitmap(), myInterpolator);
    }


    public static void main(String[] arg) {
        new UsingBitmapFilter().init();
    }
}
