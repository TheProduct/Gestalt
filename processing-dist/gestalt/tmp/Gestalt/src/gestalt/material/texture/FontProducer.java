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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import static gestalt.Gestalt.*;
import gestalt.material.Color;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;

import mathematik.Vector2i;


public class FontProducer {

    private AWTFontImageProducer _myFont;

    private int _myFontRenderingQuality;

    private float _myFontSize;

    private int _myFontStyle;

    private float _myLineWidth;

    private int _mySmoothFactor;

    private float _myScaleFactor;

    private static InputStream getStream(String thePath) {
        try {
            return new FileInputStream(thePath);
        } catch (FileNotFoundException ex) {
            System.err.println("### ERROR @ FontProducer.getStream / couldn t create inputstream " + ex);
            return null;
        }
    }


    public static FontProducer fromInstalledFont(String theInstalledFont, int theQuality) {
        FontProducer myFontProducer = new FontProducer(theInstalledFont,
                                                       theQuality,
                                                       24f,
                                                       24f,
                                                       FONT_STYLE_REGULAR);
        return myFontProducer;
    }


    public static FontProducer fromInstalledFont(String theInstalledFont,
                                                 int theQuality,
                                                 float theSize,
                                                 float theLineWidth,
                                                 int theStyle) {
        FontProducer myFontProducer = new FontProducer(theInstalledFont,
                                                       theQuality,
                                                       theSize,
                                                       theLineWidth,
                                                       theStyle);
        return myFontProducer;
    }


    public static String[] list() {
        GraphicsEnvironment myGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] myFonts = myGraphicsEnvironment.getAllFonts();
        String myList[] = new String[myFonts.length];
        for (int i = 0; i < myList.length; i++) {
            myList[i] = myFonts[i].getName();
        }
        return myList;
    }


    public static boolean isInstalled(String theString) {
        String myList[] = list();
        for (int i = 0; i < myList.length; i++) {
            if (myList[i].equals(theString)) {
                return true;
            }
        }
        return false;
    }


    public FontProducer(String theFilename) {
        this(theFilename, FONT_QUALITY_LOW);
    }


    public FontProducer(String theFilename, int theQuality) {
        this(getStream(theFilename), theQuality);
    }


    public FontProducer(String theFont,
                        int theQuality,
                        float theSize,
                        float theLineWidth,
                        int theStyle) {
        _myFont = new AWTFontImageProducer(theFont);
        init(theSize, theLineWidth, theStyle);
        setStyle(_myFontStyle);
        setQuality(theQuality);
    }


    public FontProducer(InputStream theFilename) {
        this(theFilename, FONT_QUALITY_LOW);
    }


    public FontProducer(InputStream theFilename, int theQuality) {
        this(theFilename,
             theQuality,
             24f,
             24f,
             FONT_STYLE_REGULAR);
    }


    public FontProducer(InputStream theFilename,
                        int theQuality,
                        float theSize,
                        float theLineWidth,
                        int theStyle) {
        _myFont = new AWTFontImageProducer(theFilename);
        init(theSize, theLineWidth, theStyle);
        setStyle(_myFontStyle);
        setQuality(theQuality);
    }


    private void init(float theSize, float theLineWidth, int theStyle) {
        _myFontRenderingQuality = FONT_QUALITY_LOW;
        _myFontSize = theSize;
        _myLineWidth = theLineWidth;
        _myFontStyle = theStyle;
        _mySmoothFactor = 1;
        _myScaleFactor = 3;
    }


    public void setBackgroundColor(Color theColor) {
        _myFont.setBackgroundColor(new java.awt.Color(theColor.r, theColor.g, theColor.b, theColor.a));
    }


    public void setFontColor(Color theColor) {
        _myFont.setFontColor(new java.awt.Color(theColor.r, theColor.g, theColor.b, theColor.a));
    }


    public void setSmoothFactor(int theSmooth) {
        _mySmoothFactor = theSmooth;
    }


    public void setScaleFactor(float theScale) {
        /**
         * @todo
         * the scale factor can t be set
         * after the size has been specified
         */
        _myScaleFactor = theScale;
    }


    public void setQuality(int theQuality) {
        if (theQuality == FONT_QUALITY_LOW &&
            _myFontRenderingQuality == FONT_QUALITY_HIGH) {
            _myFontRenderingQuality = theQuality;
            setSize(_myFontSize / _myScaleFactor);
            setLineWidth(_myLineWidth / _myScaleFactor);
        } else if (theQuality == FONT_QUALITY_HIGH &&
                   _myFontRenderingQuality == FONT_QUALITY_LOW) {
            _myFontRenderingQuality = theQuality;
            setSize(_myFontSize);
            setLineWidth(_myLineWidth);
        }
    }


    public void setStyle(int theStyle) {
        _myFontStyle = theStyle;
        _myFont.setStyle(theStyle);
    }


    public void setSize(float theSize) {
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                _myFontSize = theSize;
                _myFont.setSize(_myFontSize);
                break;
            case FONT_QUALITY_HIGH:
                _myFontSize = theSize * _myScaleFactor;
                _myFont.setSize(_myFontSize);
                break;
        }
    }


    public void setLineWidth(float theLinewidth) {
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                _myLineWidth = theLinewidth;
                _myFont.setLineWidth(_myLineWidth);
                break;
            case FONT_QUALITY_HIGH:
                _myLineWidth = theLinewidth * _myScaleFactor;
                _myFont.setLineWidth(_myLineWidth);
                break;
        }
    }


    public void setAlignment(int theAlignment) {
        _myFont.setAlignment(theAlignment);
    }


    public void setImageBorder(Vector2i theBorder) {
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                break;
            case FONT_QUALITY_HIGH:
                theBorder.x *= _myScaleFactor;
                theBorder.y *= _myScaleFactor;
                break;
        }
        _myFont.setImageBorder(theBorder);
    }


    public BufferedImage getImage(String theText) {
        BufferedImage myImage = _myFont.getImage(theText);
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                break;
            case FONT_QUALITY_HIGH:
                myImage = ImageUtil.blur(myImage, _mySmoothFactor);
                myImage = ImageUtil.scale(myImage, 1.0f / _myScaleFactor);
                break;
        }
        return myImage;
    }


    public void antialias(boolean theAntialias) {
        _myFont.antialias(theAntialias);
    }


    public ByteBitmap getBitmap(String theText) {
        return ImageUtil.convertBufferedImage2ByteBitmap(getImage(theText));
    }
}
