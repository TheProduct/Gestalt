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

import gestalt.material.texture.Bitmap;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.util.FontTextureCreator;
import gestalt.util.FontTextureCreator.TextFragment;

import data.Resource;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;


public class UsingFontTextureCreator
        extends AnimatorRenderer {

    private Plane _myFontPlane;

    public void setup() {

        displaycapabilities().backgroundcolor.set(0.2f);

        /* create a plane */
        _myFontPlane = drawablefactory().plane();

        /* create a texture */
        _myFontPlane.material().addTexture();
        bin(BIN_3D).add(_myFontPlane);
        getRenderedText(1.2f);
    }

    public void loop(float theDeltaTime) {
        _myFontPlane.rotation().z = event().mouseX / (float)displaycapabilities().width * PI;
        if (event().mouseClicked) {
            final float linewidth = 1 + 2 * event().mouseX / (float)displaycapabilities().width;
            getRenderedText(linewidth);
        }
    }

    private void getRenderedText(float theLinewidth) {
        Font myFont = FontTextureCreator.getFont(Resource.getStream("demo/font/exmouth/exmouth_.ttf"), 36);
        TextFragment myText = new TextFragment();
        myText.text = "Gestalt\n";
        myText.property(TextAttribute.FONT, myFont);
        myText.property(TextAttribute.FOREGROUND, new Color(1f, 0f, 0f));
        myText.property(TextAttribute.BACKGROUND, new Color(1f, 0f, 0f, 0f));
        FontTextureCreator.textfragments.add(myText); /* this static container can be used to store textfragments */

        Font myOtherFont = FontTextureCreator.getFont(Resource.getStream("demo/font/silkscreen/slkscr.ttf"), 8);
        TextFragment myOtherText = new TextFragment();
        myOtherText.text = "a very small render engine toolboxtoolboxtoolboxtoolbox.";
        myOtherText.property(TextAttribute.FONT, myOtherFont);
        myOtherText.property(TextAttribute.STRIKETHROUGH, Boolean.TRUE);
        myOtherText.property(TextAttribute.FOREGROUND, Color.WHITE);
        myOtherText.property(TextAttribute.BACKGROUND, Color.BLACK);
        FontTextureCreator.textfragments.add(myOtherText);

        FontTextureCreator.background = new Color(1f, 0f, 0f, 0.75f);
        FontTextureCreator.alignment = FontTextureCreator.CENTERED;
        FontTextureCreator.linewidth = theLinewidth;
        FontTextureCreator.padding = 20;

        Bitmap myBitmap = FontTextureCreator.getBitmap(256, true);
        FontTextureCreator.textfragments.clear(); /* clean up fragment container */
        _myFontPlane.material().texture().load(myBitmap);
        _myFontPlane.setPlaneSizeToTextureSize();
    }

    public static void main(String[] arg) {
        new UsingFontTextureCreator().init();
    }
}
