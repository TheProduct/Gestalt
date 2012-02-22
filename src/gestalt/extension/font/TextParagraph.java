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


import java.io.IOException;
import java.io.InputStream;

import java.awt.Font;
import java.awt.FontFormatException;

import mathematik.Vector2f;


public class TextParagraph {

    private float pointSize = 10f;

    private Font _myFont;

    private FontMetrics _myMetrics;

    private float tracking = 0;

    private float leading = 12;

    private TextLine[] lines;

    private Vector2f[] positionsPixels = new Vector2f[0];

    private String[] _myTextLines;

    private float[] bounds = new float[2];

    private boolean textChanged = true;

    private boolean pointSizeChanged = true;

    private boolean trackingChanged = true;

    private boolean leadingChanged = true;

    public TextParagraph(FontMetrics theMetrics, InputStream theInputStream) {
        _myTextLines = new String[0];
        _myMetrics = theMetrics;
        _myFont = loadBaseFont(theInputStream);
    }


    public TextParagraph(FontMetrics theMetrics, Font theFont) {
        _myTextLines = new String[0];
        _myMetrics = theMetrics;
        _myFont = theFont;
    }


    private Font loadBaseFont(InputStream is) {
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (IOException ex) {
            System.err.println("### ERROR @ " + this.getClass() + " IOException / " + ex);
        } catch (FontFormatException ex) {
            System.err.println("### ERROR @ " + this.getClass() + " FontFormatException / " + ex);
        }
        return font;
    }


    public Font getFont() {
        return _myFont;
    }


    public void setFontSize(float theSize) {
        pointSize = theSize;
        setLeading(pointSize);
        _myFont = _myFont.deriveFont(pointSize);
        for (int i = 0; i < _myTextLines.length; ++i) {
            lines[i].setFontSize(pointSize);
        }
        pointSizeChanged = true;
    }


    public float getFontSize() {
        return pointSize;
    }


    public void setTabList(float[] theTabList) {
        for (int i = 0; i < _myTextLines.length; ++i) {
            lines[i].setTabList(theTabList);
        }
    }


    public void setMetrics(FontMetrics theMetrics) {
        _myMetrics = theMetrics;
        for (int i = 0; i < _myTextLines.length; ++i) {
            lines[i].setMetrics(_myMetrics);
        }
    }


    public FontMetrics getMetrics() {
        return _myMetrics;
    }


    /**
     * Set tracking in 1/1000 em, a unit of measure that is relative to the current type size.
     * In a 6-point font, 1 em equals 6 points; tracking are strictly proportional to the current type size.
     * Works exactly like in Adobe InDesign.
     * @param tracking int
     */
    public void setTracking(float tracking) {
        this.tracking = tracking;
        for (int i = 0; i < _myTextLines.length; ++i) {
            lines[i].setTracking(tracking);
        }
        trackingChanged = true;
    }


    /**
     * Returns the current tracking value in 1/1000 em.
     * @return int
     */
    public float getTracking() {
        return tracking;
    }


    public void setText(String theText) {

        _myTextLines = getTextFormatted(cleanText(theText));
        lines = new TextLine[_myTextLines.length];

        positionsPixels = new Vector2f[getTextStringFormatted().length()];
        int currentCharacter = 0;
        for (int i = 0; i < _myTextLines.length; ++i) {
            lines[i] = new TextLine(_myMetrics, _myFont);
            lines[i].setText(_myTextLines[i]);
            lines[i].setFontSize(pointSize);
            lines[i].setTracking(tracking);

            for (int j = 0; j < _myTextLines[i].length(); j++) {
                positionsPixels[currentCharacter] = new Vector2f();
                currentCharacter++;
            }
        }
        textChanged = true;
    }


    public String getTextStringFormatted() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < _myTextLines.length; ++i) {
            stringBuffer.append(_myTextLines[i]);
        }
        return stringBuffer.toString();
    }


    public char[] getTextCharacters() {
        return getTextStringFormatted().toCharArray();
    }


    public void setLeading(float leading) {
        this.leading = leading;
        leadingChanged = true;
    }


    public TextLine[] getLines() {
        return lines;
    }


    private void updatePositionsPixels() {
        int currentCharacter = 0;
        bounds[0] = 0;
        for (int i = 0; i < _myTextLines.length; ++i) {
            Vector2f[] characterPositions = lines[i].getCharacterPositions();

            for (int j = 0; j < characterPositions.length; j++) {
                positionsPixels[currentCharacter].set(characterPositions[j]);
                positionsPixels[currentCharacter].y += leading * i;
                currentCharacter++;
            }
            if (lines[i].getBounds()[0] > bounds[0]) {
                bounds[0] = lines[i].getBounds()[0];
            }
        }
        bounds[1] = leading * (lines.length - 1) + pointSize;
    }


    private void checkUpdate() {
        if (pointSizeChanged ||
            leadingChanged ||
            trackingChanged ||
            textChanged) {
            if (positionsPixels.length > 1) {
                updatePositionsPixels();
            }
        }
        pointSizeChanged = false;
        leadingChanged = false;
        trackingChanged = false;
        textChanged = false;
    }


    public Vector2f[] getCharacterPositions() {
        checkUpdate();
        return positionsPixels;
    }


    public float[] getBounds() {
        checkUpdate();
        return bounds;
    }


    private String cleanText(String theText) {
        if (theText == null) {
            return "";
        } else {
            theText = theText.replaceAll("\r\n", "\n"); /* windows */
            theText = theText.replaceAll("\r", "\n"); /* old mac */
            return theText;
        }
    }


    private String[] getTextFormatted(String theText) {
        String[] myText = theText.toString().split("\n");
        for (int i = 0; i < myText.length; ++i) {
            if (myText[i].length() == 0) {
                myText[i] = " ";
            }
        }
        return myText;
    }
}
