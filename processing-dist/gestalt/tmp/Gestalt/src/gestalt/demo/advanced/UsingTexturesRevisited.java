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


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


/**
 * this demo shows some other ways to work with textures not shown in 'UsingImageTextures'.
 */

public class UsingTexturesRevisited
    extends AnimatorRenderer {

    private Plane _myImagePlane;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create a plane that carries the texture. */
        _myImagePlane = drawablefactory().plane();

        /* create texture and store its ID. */
        TexturePlugin myImageTexture = drawablefactory().texture();

        /* load texture. */
        myImageTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));

        /* set the texture in the material of your shape */
        _myImagePlane.material().addPlugin(myImageTexture);

        /* set plane to texture size. */
        _myImagePlane.scale().set(displaycapabilities().width, displaycapabilities().height);

        /* add the plane to the renderer */
        bin(BIN_3D).add(_myImagePlane);
    }


    public void loop(float theDeltaTime) {
        /* get plane s texture */
        final TexturePlugin myTexture = _myImagePlane.material().texture();

        /* change wrap mode */
        if (event().mouseDown) {
            if (event().mouseButton == MOUSEBUTTON_LEFT) {
                myTexture.setWrapMode(TEXTURE_WRAPMODE_REPEAT);
            } else {
                myTexture.setWrapMode(TEXTURE_WRAPMODE_CLAMP);
            }
        }

        /* change texturescale */
        myTexture.scale().set(1 + (float) event().mouseX / (float) displaycapabilities().width * 10,
                              -1 + (float) event().mouseY / (float) displaycapabilities().height * 10);
    }


    public static void main(String[] arg) {
        new UsingTexturesRevisited().init();
    }
}
