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

import mathematik.Vector2f;


public class FontMetrics {
    private int fUnitsPerEm;

    private int[] characterWidthMap;

    private int[][] kerningTable;

    private Vector2f drawPosition = new Vector2f();

    public FontMetrics() {}


    /**
     * Constructor. Requires the TTX file containing the font metrics.
     * @param path String
     */
    public FontMetrics(InputStream path) {
        // parse xml -----------------------------------------------------------
        TTXParser parser = new TTXParser();
        parser.read(path);

        fUnitsPerEm = parser.getFUnitsPerEm();
        characterWidthMap = parser.getCharacterWidthMap();
        kerningTable = parser.getKerningTable();
    }


    /**
     * Returns the number of fUnits per em, usually 2048 in truetype and 1000 in postscript fonts.
     * @return int
     */
    public int getFUnitsPerEm() {
        return fUnitsPerEm;
    }


    /**
     * Returns the advance of an single character, simply called width
     * @param c char
     * @return int
     */
    public int getCharacterWidth(char c) {
        return characterWidthMap[c];
    }


    /**
     * Returns the character positions calcualted form the char[].
     * @param chars char[]
     * @return Vector2f[]
     */
    public Vector2f[] getCharacterPositions(char[] chars) {
        if (chars.length < 1) {
            chars = new char[] {' '};
        }
        Vector2f[] positions = new Vector2f[chars.length];
        positions[0] = new Vector2f();

        drawPosition.set(0, 0);

        for (int i = 1; i < chars.length; ++i) {
            float fUnitsX = characterWidthMap[chars[i - 1]];

            // kerning:
            if (kerningTable[chars[i - 1]] != null) {
                fUnitsX += kerningTable[chars[i - 1]][chars[i]];
            }

            drawPosition.x += fUnitsX;
            positions[i] = new Vector2f(drawPosition);
        }
        return positions;
    }
}
