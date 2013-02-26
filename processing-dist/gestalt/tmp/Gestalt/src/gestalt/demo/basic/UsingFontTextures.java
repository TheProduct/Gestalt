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

import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.FontProducer;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;

import mathematik.Vector2i;

import data.Resource;


/**
 * this demo shows how to use font textures in gestalt.<br/>
 * see also 'UsingImageTextures'<br/>
 * <br/>
 * a font texture is generated from a ttf font.<br/>
 * the qualtiy of the font rendering can be adjusted as well as
 * fontsize and linewidth.<br/>
 * font bitmaps are created by the fontproducer.<br/>
 * <br/>
 * kerning and mutliple fonts in one bitmap are not yet supported.<br/>
 */
public class UsingFontTextures
        extends AnimatorRenderer {

    private TexturePlugin _myFontTexture;

    private Plane _myFontPlane;

    private FontProducer _myFontProducer;


    public void setup() {
displaycapabilities().backgroundcolor.set(1f,0,0,1f);

        /* create a plane */
        _myFontPlane = drawablefactory().plane();

        /* create a texture */
        _myFontTexture = drawablefactory().texture();

        /*
         * get a fontproducer from the Bitmaps.
         * the fontproducer provides certain methods to control
         * the quality and size of the font texture. further it provides
         * the actual bitmap that can be used as data for your texture
         *
         * you can use true type fonts to create the fontproducer
         */
        _myFontProducer = Bitmaps.getFontProducer(Resource.getPath("demo/font/exmouth/exmouth_.ttf"));

        /* set size and quality of the font texture */
        _myFontProducer.setSize(200);
        _myFontProducer.setSmoothFactor(16);
        _myFontProducer.setLineWidth(24);
        _myFontProducer.setQuality(FONT_QUALITY_HIGH);
        _myFontProducer.setAlignment(FONT_ALIGN_LEFT);

        /*
         * you also can use fonts that are installed on your system
         *    _myFontProducer = FontProducer.fromInstalledFont("Georgia", FONT_QUALITY_HIGH, 16, 12, FONT_STYLE_ITALIC);
         */

        /* the font is a little extreme so we create an invisible border around. */
        _myFontProducer.setImageBorder(new Vector2i(20, 20));

        /*
         * now you create a bitmap and load it to the texture.
         *
         * to create a bitmap, you call 'getBitmap(String theText)' with a String,
         * that is the text, that should be displayed
         */
        _myFontTexture.load(_myFontProducer.getBitmap("Hier steht was"));

        /* set the texture in the material of your shape */
        _myFontPlane.material().addPlugin(_myFontTexture);

        /* set scale of the plane */
        _myFontPlane.setPlaneSizeToTextureSize();
        
        /* set blend mode */
        _myFontPlane.material().color4f().a = 1f;
//        _myFontPlane.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myFontPlane.origin(SHAPE_ORIGIN_CENTERED);
        
        /* add the plane to the renderer */
        bin(BIN_3D).add(_myFontPlane);

        /* create second plane */
        Plane myFontPlane = drawablefactory().plane();
        myFontPlane.material().addPlugin(_myFontTexture);
        myFontPlane.material().color4f().a = 0.5f;
//        myFontPlane.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        myFontPlane.setPlaneSizeToTextureSize();
        bin(BIN_3D).add(myFontPlane);
    }


    public void loop(final float theDeltaTime) {
        _myFontPlane.position().set(event().mouseX, event().mouseY);
    }


    public static void main(String[] arg) {
        new UsingFontTextures().init();
    }
}
