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
import gestalt.shape.Cuboid;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


/**
 * this demo shows how to use the different texture modes of a cuboid.
 * to find out how the texture is mapped onto the cuboid have a look
 * at the referenced image.
 */

public class UsingCubeTextureModes
    extends AnimatorRenderer {

    private Cuboid _myCube;

    public void setup() {
        /* gestalt */
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create cuboid */
        _myCube = drawablefactory().cuboid();
        _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_WRAP_AROUND);
        _myCube.scale().set(100, 100, 100);
        bin(BIN_3D).add(_myCube);

        /* create texture */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/cube.png")));
        _myCube.material().addPlugin(myTexture);
    }


    public void loop(float theDeltaTime) {
        /* rotate cuboid */
        _myCube.rotation().x = 2 * PI * (float) event().mouseX / displaycapabilities().width;
        _myCube.rotation().y = 2 * PI * (float) event().mouseY / displaycapabilities().height;

        /**
         * switch texture modes. there are two modes to choose from.
         *
         *    SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE
         *    SHAPE_CUBE_TEXTURE_WRAP_AROUND
         *
         * 'SAME_FOR_EACH_SIDE' uses the same image on all six sides while
         * 'WRAP_AROUND' takes parts from the texture and thus creates
         * different textures for each side.
         * to find out how the texture is mapped onto the cuboid have a look
         * at the referenced image.
         */
        if (event().keyPressed) {
            if (event().keyCode == KEYCODE_1) {
                _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE);
            }
            if (event().keyCode == KEYCODE_2) {
                _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_WRAP_AROUND);
            }
        }
    }


    public static void main(String[] args) {
        new UsingCubeTextureModes().init();
    }
}
