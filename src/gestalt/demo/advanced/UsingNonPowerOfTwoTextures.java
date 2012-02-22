

package gestalt.demo.advanced;


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
import gestalt.extension.materialplugin.JoglMaterialPluginNonPowerOfTwoTexture;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.ByteBitmap;

import data.Resource;


/**
 */
public class UsingNonPowerOfTwoTextures
        extends AnimatorRenderer {

    private Plane _myImagePlane;

    private TexturePlugin _myImageTexture;

    private static final boolean NORMALIZE_TEXTURE_COORDINATES = false;

    private static final boolean HINT_TEXTURE_FLIP_Y_AXIS = false;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create a plane that carries the texture. */
        _myImagePlane = drawablefactory().plane();

        /* create texture and store its ID. */
        _myImageTexture = new JoglMaterialPluginNonPowerOfTwoTexture(HINT_TEXTURE_FLIP_Y_AXIS, NORMALIZE_TEXTURE_COORDINATES);

        /* load texture. */
        final ByteBitmap myBitmap = Bitmaps.getBitmap(Resource.getStream("demo/common/police.png"));
        myBitmap.flipY();
        _myImageTexture.load(myBitmap);

        /* set the texture in the material of your shape */
        _myImagePlane.material().addPlugin(_myImageTexture);

        /* set plane to texture size. */
        _myImagePlane.setPlaneSizeToTextureSize();
        _myImagePlane.rotation().z = 0.1f;

        /* add the plane to the renderer */
        bin(BIN_3D).add(_myImagePlane);
    }

    public void loop(float theDeltaTime) {
        /* change wrap mode */
        if (event().keyPressed) {
            if (event().keyCode == KEYCODE_R) {
                _myImageTexture.setWrapMode(TEXTURE_WRAPMODE_REPEAT);
            } else if (event().keyCode == KEYCODE_C) {
                _myImageTexture.setWrapMode(TEXTURE_WRAPMODE_CLAMP);
            }
        }

        /* change texturescale */
        if (NORMALIZE_TEXTURE_COORDINATES) {
            _myImageTexture.scale().set(event().normalized_mouseX * 2,
                                        event().normalized_mouseY * 2);
        } else {
            _myImageTexture.scale().set(event().normalized_mouseX * width,
                                        event().normalized_mouseY * height);
        }
    }

    public static void main(String[] arg) {
        new UsingNonPowerOfTwoTextures().init();
    }
}
