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


package gestalt.material.texture;


import java.io.InputStream;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gestalt.Gestalt;

import mathematik.Vector2i;


public class AWTFontImageProducer
    implements Gestalt {

    public static boolean VERBOSE = false;

    public static int minimum_image_width = 4;

    public static int minimum_image_height = 4;

    private static final int _myImageType = BufferedImage.TYPE_4BYTE_ABGR;

    private Color _myBackgroundColor;

    private Color _myFontColor;

    private Font _myBasefont;

    private Font _myFont;

    private float _myFontsize;

    private float _myLinewidth;

    private int _myAlignment;

    private int _myStyle;

    private float _myImageHeightRatio;

    private Vector2i _myImageBorder;

    private boolean _myAntialias;

    public AWTFontImageProducer(String theInstalledFontName) {
        init(loadBaseFontFromString(theInstalledFontName));
    }


    public AWTFontImageProducer(InputStream theInputStream) {
        init(loadBaseFontFromStream(theInputStream));
    }


    private void init(Font theBaseFont) {
        _myBasefont = theBaseFont;
        _myImageHeightRatio = 0.2f;
        _myFontsize = 48f;
        _myLinewidth = 48f;
        _myImageBorder = new Vector2i(0, 0);
        _myAlignment = FONT_ALIGN_LEFT;
        _myStyle = FONT_STYLE_REGULAR;
        _myAntialias = true;
        _myBackgroundColor = new Color(255, 255, 255, 0);
        _myFontColor = new Color(255, 255, 255, 255);
        setSize(_myFontsize);
    }


    public void setBackgroundColor(Color theColor) {
        _myBackgroundColor = theColor;
    }


    public void setFontColor(Color theColor) {
        _myFontColor = theColor;
    }


    private Font deriveFont(float theSize, int theStyle) {
        int myAWTStyle = 0;

        switch (theStyle) {
            case FONT_STYLE_REGULAR:
                myAWTStyle = Font.PLAIN;
                break;
            case FONT_STYLE_BOLD:
                myAWTStyle = Font.BOLD;
                break;
            case FONT_STYLE_ITALIC:
                myAWTStyle = Font.ITALIC;
                break;
        }
        return _myBasefont.deriveFont(myAWTStyle, theSize);
    }


    public void setSize(float theSize) {
        _myFontsize = theSize;
        _myFont = deriveFont(theSize, _myStyle);
    }


    public void setStyle(int theStyle) {
        _myStyle = theStyle;
        _myFont = deriveFont(_myFontsize, _myStyle);
    }


    public void setLineWidth(float theLinewidth) {
        _myLinewidth = theLinewidth;
    }


    public void setAlignment(int theAlignment) {
        _myAlignment = theAlignment;
    }


    public void setImageBorder(Vector2i theBorder) {
        _myImageBorder.set(theBorder);
    }


    private Font loadBaseFontFromString(String theInstalledFontName) {
        Font myFont = Font.decode(theInstalledFontName);
        if (myFont == null) {
            System.err.println("### ERROR @ " + this.getClass() +
                               " / couldn t find font / " + theInstalledFontName);
        }
        if (!myFont.getFontName().equals(theInstalledFontName)) {
            System.err.println("### WARNING @ " + this.getClass() +
                               " / couldn t find font / " + theInstalledFontName +
                               " / using default font: " + myFont.getFontName());
        }
        return myFont;
    }


    public static String[] getAvailableFonts() {
        Font[] myAvailableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        String[] myAvailableFontNames = new String[myAvailableFonts.length];
        for (int i = 0; i < myAvailableFonts.length; i++) {
            myAvailableFontNames[i] = myAvailableFonts[i].getFontName();
        }
        return myAvailableFontNames;
    }


    private Font loadBaseFontFromStream(InputStream theInputStream) {
        Font myFont = null;
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, theInputStream);
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass() + " / couldn t load font / " + ex);
        }
        return myFont;
    }


    private FontRenderContext getContext() {
        Graphics2D myGraphics = new BufferedImage(1, 1, _myImageType).createGraphics();
        if (_myAntialias) {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        return myGraphics.getFontRenderContext();
    }


    public char checkString(final String theText) {
        if (theText != null &&
            theText.length() != 0) {
            final int myIllegalCharacter = _myFont.canDisplayUpTo(theText);
            if (myIllegalCharacter != -1) {
                System.err.println("### WARNING @ " +
                                   this.getClass() +
                                   " / can t display character " + (char) myIllegalCharacter + " (" +
                                   myIllegalCharacter +
                                   ") in String '" +
                                   theText + "'");
                return (char) myIllegalCharacter;
            }
        }
        return (char) 0;
    }


    public void antialias(final boolean theAntialias) {
        _myAntialias = theAntialias;
    }


    public BufferedImage getImage(String theText) {

        if (VERBOSE) {
            System.out.println("### checking string: " + theText);
            checkString(theText);
        }

        String[] myParagraph = formatText(theText);

        /* get font rendercontext */
        FontRenderContext myContext = getContext();

        int[] myParagraphDimension = getParagraphBoundingBox(myParagraph, myContext);
        int myWidth = myParagraphDimension[0] + _myImageBorder.x * 2;
        int myHeight = myParagraphDimension[1] +
                       (int) (myParagraphDimension[1] * _myImageHeightRatio) +
                       +_myImageBorder.y * 2;
        int myY = myParagraphDimension[3];

        /* check if the image is to small */
        if (myWidth < minimum_image_width) {
            myWidth = minimum_image_width;
        }
        if (myHeight < minimum_image_height) {
            myHeight = minimum_image_height;
        }

        /* create image */
        BufferedImage myBufferImage = new BufferedImage(myWidth, myHeight, _myImageType);
        Graphics2D myGraphics = myBufferImage.createGraphics();

        if (_myAntialias) {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        /* set colors */
        myGraphics.setBackground(_myBackgroundColor);
        myGraphics.clearRect(0, 0, myWidth, myHeight);
        myGraphics.setColor(_myFontColor);

        /* draw string */
        for (int i = 0; i < myParagraph.length; ++i) {
            int[] myBoundingBox = getStringBoundingBox(myParagraph[i], myContext);
            int yOffset = i * (int) _myLinewidth + _myImageBorder.y;
            int xOffset = 0;
            switch (_myAlignment) {
                case FONT_ALIGN_LEFT:
                    xOffset = 0 + _myImageBorder.x;
                    break;
                case FONT_ALIGN_CENTER:
                    xOffset = (myParagraphDimension[0] - myBoundingBox[0]) / 2 + _myImageBorder.x;
                    break;
                case FONT_ALIGN_RIGHT:
                    xOffset = (myParagraphDimension[0] - myBoundingBox[0]) + _myImageBorder.x;
                    break;
            }
            myGraphics.setFont(_myFont);
            myGraphics.drawString(myParagraph[i], 0 + xOffset, -myY + yOffset);
        }
        return myBufferImage;
    }


    private String[] formatText(String theText) {
        if (theText == null) {
            return new String[] {""};
        } else {
            /* windows */
            theText = theText.replaceAll("\r\n", "\n");
            /* old mac */
            theText = theText.replaceAll("\r", "\n");
            return theText.toString().split("\n");
        }
    }


    private int[] getParagraphBoundingBox(String[] theParagraph, FontRenderContext theContext) {
        int[] myDimensions;
        int[] myParagraphDimension = getStringBoundingBox(theParagraph[0], theContext);
        /* height */
        myParagraphDimension[1] = myParagraphDimension[1] + (int) (_myLinewidth * (float) (theParagraph.length - 1));
        /* width */
        for (int i = 0; i < theParagraph.length; ++i) {
            myDimensions = getStringBoundingBox(theParagraph[i], theContext);
            if (myDimensions[0] > myParagraphDimension[0]) {
                myParagraphDimension[0] = myDimensions[0];
            }
        }
        return myParagraphDimension;
    }


    private int[] getStringBoundingBox(String theText, FontRenderContext theContext) {
        int[] myDimensions = new int[4];
        Rectangle2D r = _myFont.getStringBounds(theText, theContext);
        myDimensions[0] = (int) r.getWidth();
        myDimensions[1] = (int) _myFont.getMaxCharBounds(theContext).getHeight();
        myDimensions[2] = (int) r.getX();
        myDimensions[3] = (int) r.getY();
        /* w, h, x, y */
        return myDimensions;
    }
}
