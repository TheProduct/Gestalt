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


/**
 * http://www.letterror.com/code/ttx/ -- the truetype to xml tool
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import mathematik.Vector2f;


public class FontImageProducer {

    private int _myImageType;

    private TextParagraph _myParagraph;

    private Color _myBackgroundColor;

    private Color _myFontColor;

    private int _myBorder;

    private boolean _myAntialias;

    public FontImageProducer(TextParagraph theParagraph) {
        _myParagraph = theParagraph;
        _myImageType = BufferedImage.TYPE_4BYTE_ABGR; //TYPE_3BYTE_BGR // TYPE_4BYTE_ABGR // TYPE_BYTE_GRAY
        _myBackgroundColor = new Color(255, 255, 255, 0);
        _myFontColor = new Color(255, 255, 255, 255);
        _myBorder = 10;
        _myAntialias = true;
    }


    public void setBackgroundColor(Color theColor) {
        _myBackgroundColor = theColor;
    }


    public void setFontColor(Color theColor) {
        _myFontColor = theColor;
    }


    public BufferedImage getImage() {
        float[] bounds = _myParagraph.getBounds();
        int width = (int) (bounds[0] + _myBorder * 2);
        int height = (int) (bounds[1] + _myBorder * 2);
        BufferedImage myBitmap = new BufferedImage(width, height, _myImageType);
        Graphics2D myGraphics = myBitmap.createGraphics();

        myGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        if (_myAntialias) {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        /* set colors */
        myGraphics.setBackground(_myBackgroundColor);
        myGraphics.clearRect(0, 0, width, height);
        myGraphics.setColor(_myFontColor);
        myGraphics.setFont(_myParagraph.getFont());

        Vector2f[] characterPositions = _myParagraph.getCharacterPositions();
        String textFormatted = _myParagraph.getTextStringFormatted();

        myGraphics.translate(_myBorder,
                             _myBorder + _myParagraph.getFontSize() * 0.75f);
        for (int i = 0; i < characterPositions.length; ++i) {
            myGraphics.drawString(textFormatted.substring(i, i + 1),
                                  characterPositions[i].x,
                                  characterPositions[i].y);
        }
        return myBitmap;
    }


    public void antialias(boolean theAntialias) {
        _myAntialias = theAntialias;
    }
}
