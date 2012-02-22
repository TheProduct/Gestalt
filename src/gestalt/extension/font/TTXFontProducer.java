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

import java.awt.image.BufferedImage;

import static gestalt.Gestalt.*;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;


public class TTXFontProducer {

    private int _myFontRenderingQuality;

    private float _myFontSize;

    private float _myLeading;

    private float _myTracking;

    private int _mySmoothFactor;

    private float _myScaleFactor;

    private TextParagraph _myParagraph;

    private FontImageProducer _myFontRenderer;

    private float[] _myTabList;

    public TTXFontProducer(InputStream theTTXFilename,
                           InputStream theTTFFilename,
                           int theQuality) {
        FontMetrics myMetrics = new FontMetrics(theTTXFilename);
        _myParagraph = new TextParagraph(myMetrics, theTTFFilename);
        _myFontRenderer = new FontImageProducer(_myParagraph);
        _myScaleFactor = 2;
        _mySmoothFactor = 1;
        _myFontSize = 48f;
        _myLeading = 48f;
        _myTracking = 0;
        setQuality(theQuality);
    }


    public void setTracking(float theTracking) {
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                _myTracking = theTracking;
                break;
            case FONT_QUALITY_HIGH:
                _myTracking = theTracking * _myScaleFactor;
                break;
        }
        _myParagraph.setTracking(_myTracking);
    }


    public void setSmoothFactor(int theSmooth) {
        _mySmoothFactor = theSmooth;
    }


    public void setScaleFactor(float theScale) {
        /**
         * @todo
         * the scale factor can t be set
         * after the size has been specified :(
         */
        _myScaleFactor = theScale;
    }


    public void setQuality(int theQuality) {
        if (theQuality == FONT_QUALITY_LOW && _myFontRenderingQuality == FONT_QUALITY_HIGH) {
            _myFontRenderingQuality = theQuality;
            setSize(_myFontSize / _myScaleFactor);
            setLeading(_myLeading / _myScaleFactor);
            setTracking(_myTracking / _myScaleFactor);
        } else if (theQuality == FONT_QUALITY_HIGH && _myFontRenderingQuality == FONT_QUALITY_LOW) {
            _myFontRenderingQuality = theQuality;
            setSize(_myFontSize);
            setLeading(_myLeading);
            setTracking(_myTracking);
        }
    }


    public void setSize(float theSize) {
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                _myFontSize = theSize;
                break;
            case FONT_QUALITY_HIGH:
                _myFontSize = theSize * _myScaleFactor;
                break;
        }
        _myParagraph.setFontSize(_myFontSize);
    }


    public void setTabList(float[] theTabList) {
        _myTabList = new float[theTabList.length];
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                for (int i = 0; i < theTabList.length; ++i) {
                    _myTabList[i] = theTabList[i];
                }
                break;
            case FONT_QUALITY_HIGH:
                for (int i = 0; i < theTabList.length; ++i) {
                    _myTabList[i] = theTabList[i] * _myScaleFactor;
                }
                break;
        }
        _myParagraph.setTabList(_myTabList);
    }


    public void setLeading(float theLeading) {
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                _myLeading = theLeading;
                break;
            case FONT_QUALITY_HIGH:
                _myLeading = theLeading * _myScaleFactor;
                break;
        }
        _myParagraph.setLeading(_myLeading);
    }


    private BufferedImage getImage(String theText) {
        _myParagraph.setText(theText);
        _myParagraph.setTabList(_myTabList);
        BufferedImage myImage = _myFontRenderer.getImage();
        switch (_myFontRenderingQuality) {
            case FONT_QUALITY_LOW:
                break;
            case FONT_QUALITY_HIGH:
                myImage = ImageUtil.blur(myImage, _mySmoothFactor);

//                myImage = ImageUtil.gaussianBlur(myImage, _mySmoothFactor);
                myImage = ImageUtil.scale(myImage, 1.0f / _myScaleFactor);
                break;
        }
        return myImage;
    }


    public ByteBitmap getBitmap(String theText) {
        return ImageUtil.convertBufferedImage2ByteBitmap(getImage(theText));
    }


    public void antialias(boolean theAntialias) {
        _myFontRenderer.antialias(theAntialias);
    }

}
