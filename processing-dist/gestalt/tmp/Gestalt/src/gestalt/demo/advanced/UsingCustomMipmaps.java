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


import gestalt.extension.materialplugin.JoglCustomMipMap;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.ByteBitmap;

import data.Resource;


public class UsingCustomMipmaps
    extends AnimatorRenderer {

    private Plane _myImagePlane;

    public void setup() {
        cameramover(true);

        /* create plane */
        _myImagePlane = drawablefactory().plane();
        bin(BIN_3D).add(_myImagePlane);

        /* create custom mipmap texture */
        ByteBitmap myBitmap = Bitmaps.getBitmap(Resource.getStream("demo/common/d3.png"));
        ByteBitmap[] myBitmaps = JoglCustomMipMap.createBluredBitmapSequence(myBitmap, 10);
        TexturePlugin myImageTexture = new JoglCustomMipMap(myBitmaps, true);
        _myImagePlane.material().addTexture(myImageTexture);
        _myImagePlane.setPlaneSizeToTextureSize();
    }


    public static void main(String[] arg) {
        new UsingCustomMipmaps().init();
    }
}
