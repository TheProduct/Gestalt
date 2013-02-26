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


import java.awt.Font;

import mathematik.Vector2f;


public class TextLine {

    private float[] _myTabs;

    private float pointSize;

    private Font font;

    private int fUnitsPerEm;

    private float fUnitPixelSize;

    private FontMetrics metrics;

    private float tracking = 0;

    private char[] chars = new char[] {' '};

    private Vector2f[] positionsFUnits;

    private Vector2f[] positionsPixels;

    private float[] bounds = new float[2];

    private boolean textChanged = true;

    private boolean pointSizeChanged = true;

    private boolean trackingChanged = true;

    /**
     *  Constructor. Sets default font. Requires font metrics.
     * @param metrics TypoMetrics
     */
    public TextLine(FontMetrics metrics, Font theFont) {
        setMetrics(metrics);
        font = theFont;
        setText(" ");
        pointSize = 10f;
    }


//    /**
//     * Load Font from external true type file
//     * @param is InputStream
//     * @return Font
//     */
//    private Font loadBaseFont(InputStream is) {
//        Font font = null;
//        try {
//            font = Font.createFont(Font.TRUETYPE_FONT, is);
//        } catch (IOException ex) {
//            System.err.println("### ERROR @ " + this.getClass() + " IOException");
//        } catch (FontFormatException ex) {
//            System.err.println("### ERROR @ " + this.getClass() + " FontFormatException");
//        }
//        return font;
//    }
//
//
//    /**
//     * Set font from external file
//     * @param is InputStream
//     */
//    public void setFont(InputStream is) {
//        font = loadBaseFont(is);
//        font = font.deriveFont(pointSize);
//    }
//
//
//    /**
//     * Set system font from font name
//     * @param fontName String
//     */
//    public void setFont(String fontName) {
//        font = new Font(fontName, Font.PLAIN, (int) pointSize);
//    }
//
//
//    /**
//     * Set font
//     * @param fontName String
//     */
//    public void setFont(Font font) {
//        this.font = font;
//    }


    public Font getFont() {
        return font;
    }


    public void setTabList(float[] theTabList) {
        _myTabs = theTabList;
    }


    public void setFontSize(float pointSize) {
        this.pointSize = pointSize;
        font = font.deriveFont(pointSize);
        setFUnitPixelSize(pointSize, fUnitsPerEm);
        pointSizeChanged = true;
    }


    public float getFontSize() {
        return pointSize;
    }


    public void setMetrics(FontMetrics metrics) {
        this.metrics = metrics;
        fUnitsPerEm = metrics.getFUnitsPerEm();
        setFUnitPixelSize(pointSize, fUnitsPerEm);
    }


    public FontMetrics getMetrics() {
        return metrics;
    }


    /**
     * Calculate the pixel size of one fUnit, assuming 72dpi screen resolution
     * @param pointSize float
     * @param fUnitsPerEm int
     */
    private void setFUnitPixelSize(float pointSize, int fUnitsPerEm) {
        fUnitPixelSize = pointSize / fUnitsPerEm;
    }


    public float getFUnitPixelSize() {
        return fUnitPixelSize;
    }


    /**
     * Set tracking in 1/1000 em, a unit of measure that is relative to the current type size.
     * In a 6-point font, 1 em equals 6 points; tracking are strictly proportional to the current type size.
     * Works exactly like in Adobe InDesign.
     * @param tracking int
     */
    public void setTracking(float tracking) {
        this.tracking = tracking;
        trackingChanged = true;
    }


    /**
     * Returns the current tracking value in 1/1000 em.
     * @return int
     */
    public float getTracking() {
        return tracking;
    }


    /**
     * Set the text to be calculated.
     * @param chars char[]
     */
    public void setText(char[] chars) {
        if (chars.length > 0) {
            this.chars = chars;
        }
        positionsFUnits = metrics.getCharacterPositions(chars);
        positionsPixels = new Vector2f[positionsFUnits.length];
        for (int i = 0; i < positionsFUnits.length; ++i) {
            positionsPixels[i] = new Vector2f();
        }
        textChanged = true;
    }


    /**
     * Set the text to be calculated.
     * @param string String
     */
    public void setText(String string) {
        chars = string.toCharArray();
        setText(chars);
    }


    /**
     * Returns the stored text.
     * @return char[]
     */
    public char[] getTextCharacters() {
        return chars;
    }


    /**
     * Returns the stored text.
     * @return char[]
     */
    public String getTextString() {
        return new String(chars);
    }


    /**
     * Returns the character positions of the stored text.
     * @return Vector2f[]
     */
    public Vector2f[] getFUnitCharacterPositions() {
        return positionsFUnits;
    }


    /**
     * Calculates the character positions of the stored text
     * @return Vector2f[]
     */
    private void updatePositionsPixels() {
        float trackingSizeFUnits = tracking / 1000f * fUnitsPerEm;
        for (int i = 0; i < positionsFUnits.length; ++i) {
            positionsPixels[i].set(positionsFUnits[i]);
            positionsPixels[i].x += trackingSizeFUnits * i;
            positionsPixels[i].scale(fUnitPixelSize);
        }

        /* add tabs */
        if (_myTabs != null) {
            float myTabOffset = 0;
            for (int i = 0; i < positionsPixels.length; ++i) {
                positionsPixels[i].x += myTabOffset;
                if (chars[i] == '\t') {
                    myTabOffset += getNextTabPosition(positionsPixels[i].x);
                }
            }
        }

        /* set bounds */
        bounds[0] = positionsPixels[positionsPixels.length - 1].x +
                    metrics.getCharacterWidth(chars[chars.length - 1]) * fUnitPixelSize;
        bounds[1] = pointSize;
    }


    private float getNextTabPosition(float thePosition) {
        if (_myTabs != null) {
            int myIndex = -1;
            for (int i = 0; i < _myTabs.length; ++i) {
                if (thePosition < _myTabs[i]) {
                    myIndex = i;
                    break;
                }
            }
            if (myIndex != -1) {
                return _myTabs[myIndex] - thePosition;
            }
        }
        return 0;
    }


    /**
     * Checks if a recalculation is required and if so, recalculates the character positions
     */
    private void checkUpdate() {
        if (pointSizeChanged || trackingChanged ||
            textChanged) {
            if (positionsPixels.length > 1) {
                updatePositionsPixels();
            }
        }
        pointSizeChanged = false;
        trackingChanged = false;
        textChanged = false;
    }


    /**
     * Returns the character positions of the stored text in pixels
     * @return Vector2f[]
     */
    public Vector2f[] getCharacterPositions() {
        checkUpdate();
        return positionsPixels;
    }


    /**
     * Returns the width and height of the whole line in pixels
     * @return float[]
     */
    public float[] getBounds() {
        checkUpdate();
        return bounds;
    }
}
