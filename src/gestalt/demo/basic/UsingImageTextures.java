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


package gestalt.demo.basic;

import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


/**
 * this demo shows how to use image textures in gestalt.<br/>
 * <br/>
 * a texture is only a container object. the actual data is not
 * stored in the texture but in a 'bitmap'.<br/>
 * <br/>
 * there are different ways of creating a 'bitmap'. three of them
 * are from images, movies and typographie.<br/>
 * <br/>
 * to apply new data to a texture, the data needs to be loaded.
 * a texture only accepts 'bitmaps' it is not aware whether this data
 * comes from an image, a movie, a font or whatever.<br/>
 * <br/>
 * a texture is associated to a shape through its material.<br/>
 * <br/>
 * in gestalt textures do not have to be power-of-2.<br/>
 */


public class UsingImageTextures
    extends AnimatorRenderer {

    private TexturePlugin _myImageTexture;

    private Plane _myImagePlane;

    public void setup() {
        /*
         * change background color4f of window to white
         * see also 'UsingDisplay'
         */
        displaycapabilities().backgroundcolor.set(1.0f);

        /* create a plane that carries the texture */
        _myImagePlane = drawablefactory().plane();

        /* create a texture */
        _myImageTexture = drawablefactory().texture();

        /*
         * set the type of the texture there are three different types:
         *    TEXTURE_TYPE_NEAREST_FILTERED
         *    TEXTURE_TYPE_LINEAR_FILTERED
         *    TEXTURE_TYPE_MIPMAP
         */
        _myImageTexture.setFilterType(TEXTURE_FILTERTYPE_LINEAR);

        /*
         * now you have to actually load your image data
         * as we are using normal images as data for our texture
         * you obtain a gestalt bitmap from the bitmapfactory
         *
         * the bitmap factory is used to convert an image format to
         * a gestalt bitmap.
         * currently the following image formats are supported:
         * jpg
         * png
         *
         * images are usually loaded from files although bitmaps
         * can be created dynamically, shown in another demo
         * see also 'UsingDynamicallyCreatingTextures'
         */
        _myImageTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));

        /* set the texture in the material of your shape */
        _myImagePlane.material().addPlugin(_myImageTexture);

        /* this is a helper function to set the size of your
         * plane to the size of your texture.
         * if your image is 256x256 pixels, the scale of your
         * plane will be also 256x256 units
         */
        _myImagePlane.setPlaneSizeToTextureSize();

        /* add the plane to the renderer */
        bin(BIN_3D).add(_myImagePlane);
    }


    public static void main(String[] arg) {
        /* create renderer */
        new UsingImageTextures().init();
    }
}
