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


package gestalt.extension.font;


import java.io.InputStream;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;


public class CharacterGrid {

    public static boolean VERBOSE = false;

    private float _myFontSize;

    private Font _myFont;

    private int _myWidth;

    private int _myHeight;

    private int _myImageType;

    private Color _myBackgroundColor;

    private Color _myFontColor;

    private BufferedImage _myCanvas;

    private String[] _myCharacters;

    public CharacterGrid(int theWidth,
                         int theHeight,
                         int theImageType,
                         float theFontSize,
                         InputStream theFontFile) {
        _myWidth = theWidth;
        _myHeight = theHeight;
        _myImageType = theImageType;
        _myFontColor = new Color(255, 255, 255, 255);
        _myBackgroundColor = new Color(0, 0, 0, 255);
        _myFontSize = theFontSize;
        setFont(getFont(theFontFile));
        setImage(createImage());
        _myCharacters = new String[0];
    }


    public CharacterGrid(int theWidth,
                         int theHeight,
                         int theImageType,
                         float theFontSize,
                         Font theFont) {
        _myWidth = theWidth;
        _myHeight = theHeight;
        _myImageType = theImageType;
        _myFontColor = new Color(255, 255, 255, 255);
        _myBackgroundColor = new Color(0, 0, 0, 255);
        _myFontSize = theFontSize;
        setFont(theFont);
        setImage(createImage());
        _myCharacters = new String[0];
    }


    private Font getFont(InputStream theInputStream) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, theInputStream);
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass() + " / couldn t read font file." + ex);
            return null;
        }
    }


    private void setFont(Font theFont) {
        _myFont = theFont.deriveFont(_myFontSize);
    }


    public void setImage(BufferedImage theImage) {
        _myCanvas = theImage;
    }


    public BufferedImage createImage() {
        /* create buffered image */
        BufferedImage myBitmap = new BufferedImage(_myWidth, _myHeight, _myImageType);
        return myBitmap;
    }


    public BufferedImage getImage() {
        return _myCanvas;
    }


    public void drawCharacters(int theWidth, int theHeight) {
        if (theWidth * theHeight != _myCharacters.length) {
            System.err.println("### ERROR @ " + this.getClass() + " / dimensions do not match number of characters.");
        }
        Graphics2D myGraphics = _myCanvas.createGraphics();
        /* set font */
        myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        myGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        myGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        myGraphics.setFont(_myFont);
        /* set colors and clear canvas */
        myGraphics.setBackground(_myBackgroundColor);
        myGraphics.clearRect(0, 0, _myWidth, _myHeight);
        myGraphics.setColor(_myFontColor);
        /* get widest character */
        FontRenderContext myContext = myGraphics.getFontRenderContext();
        float myWidestCharacter = getWidestCharacter(myContext);
        /* paint characters */
        float myTileWidth = _myWidth * 1.0f / theWidth;
        float myTileHeight = _myHeight * 1.0f / theHeight;
        if (VERBOSE) {
            System.out.println("### INFO tile width  (px): " + myTileWidth);
            System.out.println("### INFO tile height (px): " + myTileHeight);
        }
        for (int i = 0; i < _myCharacters.length; ++i) {
            int myX = i % theWidth;
            int myY = i / theWidth;
            float myXPosition = (myX + 0.5f) / (float) theWidth * _myWidth - myWidestCharacter * 0.5f;
            float myYPosition = (myY + 0.5f) / (float) theHeight * _myHeight + myWidestCharacter * 0.4f;
            myGraphics.drawString(_myCharacters[i], myXPosition, myYPosition);
        }
    }


    private float getWidestCharacter(FontRenderContext theContext) {
        /*
         * although this seems to be a precise way
         * the possible change of character positioning
         * when changing characters seems to be unstable.
         * that s why we only measure the capital 'M'.
         */
        /*
                 float myWidestCharacter = 0;
                 for (int i = 0; i < _myCharacters.length; ++i) {
            float myCharWidth = getCharWidth(_myCharacters[i], theContext);
            if (myCharWidth > myWidestCharacter) {
                myWidestCharacter = myCharWidth;
            }
                 }
         */
        return getCharWidth("M", theContext);
    }


    private float getCharWidth(String theString, FontRenderContext theContext) {
        return (float) _myFont.getStringBounds(theString, theContext).getWidth();
    }


    public String[] getCharacters(int theNumberOfCharacters, int theStartOffset) {
        String[] myCharacters = new String[theNumberOfCharacters];
        for (int i = 0; i < myCharacters.length; ++i) {
            myCharacters[i] = String.valueOf( (char) (i + theStartOffset));
        }
        return myCharacters;
    }


    public void setCharacters(String[] theCharacters) {
        _myCharacters = theCharacters;
    }


    public void setFontColor(Color theColor) {
        _myFontColor = theColor;
    }


    public void setBackgroundColor(Color theColor) {
        _myBackgroundColor = theColor;
    }


    public void drawGlurCharacters(int theWidth, int theHeight, int theGlur) {
        BufferedImage myFinalImage = _myCanvas;
        /* draw normal image */
        BufferedImage myUnblured = createImage();
        setImage(myUnblured);
        drawCharacters(theWidth, theHeight);
        /* draw blur image */
        BufferedImage myBlured = createImage();
        setImage(myBlured);
        drawCharacters(theWidth, theHeight);

        /** @todo check if this works correctly. */
        /** @todo remove the conversions one day. */
        ByteBitmap myBitmap = ImageUtil.convertBufferedImage2ByteBitmap(myBlured);
        ImageUtil.gaussianBlur(myBitmap, theGlur);
        myBlured = ImageUtil.convertByteBitmap2BufferedImage(myBitmap);

//        myBlured = ImageUtil.planarImage2BufferedImage(
//            ImageUtil.gaussianBlur(
//                ImageUtil.bufferedImage2PlanarImage(myBlured), theGlur));

        /* compose final image */
        myFinalImage.getGraphics().drawImage(myBlured, 0, 0, null);
        myFinalImage.getGraphics().drawImage(myUnblured, 0, 0, null);
        _myCanvas = myFinalImage;
    }

}
