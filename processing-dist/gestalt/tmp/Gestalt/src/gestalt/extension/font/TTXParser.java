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

/**
 * http://www.letterror.com/code/ttx/ -- the truetype to xml tool
 */


package gestalt.extension.font;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;


public class TTXParser {

    /* the unicode maximum is 65536, only needed if full character set is required */
    private static final int MAX_NUMBER_OF_CHARACTERS = 65536;

    private int fUnitsPerEm;

    private String[] characterNameMap;

    private int[] characterWidthMap;

    private int[][] kerningTable;

    private String[][] glyphsNameAndWidth;

    public void read(InputStream theFilename) {
        XMLElement xml = new XMLElement();
        InputStreamReader reader = new InputStreamReader(theFilename);
        try {
            xml.parseFromReader(reader);
        } catch (XMLParseException ex) {
            System.err.println("### ERROR @ TTXParser / couldn t parse XML." + ex);
        } catch (IOException ex) {
            System.err.println("### ERROR @ TTXParser / couldn t read XML file." + ex);
        }
        parseHead(xml);
        parseWidths(xml);
        parseCharacterMap(xml);
        parseKerning(xml);
    }


    /**
     * Parse some common formation stored in the head tag.
     * Currently only what is needed for calculations.
     * @param theRootNode XMLElement
     */
    @SuppressWarnings("unchecked")
        private void parseHead(XMLElement theRootNode) {
        /* get 'head' */
        XMLElement myHeadNode = null;
        Enumeration myEnum = theRootNode.enumerateChildren();
        while (myEnum.hasMoreElements()) {
            XMLElement myChild = (XMLElement) myEnum.nextElement();
            if (myChild.getName().equals("head")) {
                myHeadNode = myChild;
                break;
            }
        }
        /* get 'unitsPerEm' */
        if (myHeadNode != null) {
            Enumeration myHeadEnum = myHeadNode.enumerateChildren();
            while (myHeadEnum.hasMoreElements()) {
                XMLElement myChild = (XMLElement) myHeadEnum.nextElement();
                if (myChild.getName().equals("unitsPerEm")) {
                    fUnitsPerEm = myChild.getIntAttribute("value");
                    return;
                }
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() + " / couldn t find 'head'. TTX possibly corrupt.");
        }
        System.err.println("### ERROR @ " + this.getClass() + " / couldn t find 'unitsPerEm'. TTX possibly corrupt.");
    }


    /**
     * Parse the horizontal advances of each glyph, simply called width.
     * Don't mix up with the width of the glyphs outline!
     * @param theRootNode XMLElement
     */
    @SuppressWarnings("unchecked")
        private void parseWidths(XMLElement theRootNode) {
        /* get 'hmtx' */
        XMLElement myHTMXNode = null;
        {
            Enumeration myEnum = theRootNode.enumerateChildren();
            while (myEnum.hasMoreElements()) {
                XMLElement myChild = (XMLElement) myEnum.nextElement();
                if (myChild.getName().equals("hmtx")) {
                    myHTMXNode = myChild;
                    break;
                }
            }
        }
        /* get glyph name and width */
        if (myHTMXNode != null) {
            glyphsNameAndWidth = new String[myHTMXNode.countChildren()][];
            {
                int i = 0;
                Enumeration myEnum = myHTMXNode.enumerateChildren();
                while (myEnum.hasMoreElements()) {
                    XMLElement myChild = (XMLElement) myEnum.nextElement();
                    glyphsNameAndWidth[i] = new String[2];
                    glyphsNameAndWidth[i][0] = myChild.getStringAttribute("name");
                    glyphsNameAndWidth[i][1] = myChild.getStringAttribute("width");
                    i++;
                }
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() + " / couldn t find 'hmtx'. TTX possibly corrupt.");
        }
    }


    /**
     * Parse the Map of Characters by charsNumMax.
     * @param theRootNode XMLElement
     */
    @SuppressWarnings("unchecked")
        private void parseCharacterMap(XMLElement theRootNode) {
        /* get 'cmap' */
        XMLElement myCMAPNode = null;
        {
            Enumeration myEnum = theRootNode.enumerateChildren();
            while (myEnum.hasMoreElements()) {
                XMLElement myChild = (XMLElement) myEnum.nextElement();
                if (myChild.getName().equals("cmap")) {
                    myCMAPNode = myChild;
                    break;
                }
            }
        }
        /* get 'cmap_format_4' */
        XMLElement myCMAPFORMAT4Node = null;
        if (myCMAPNode != null) {
            Enumeration myEnum = myCMAPNode.enumerateChildren();
            while (myEnum.hasMoreElements()) {
                XMLElement myChild = (XMLElement) myEnum.nextElement();
                if (myChild.getName().equals("cmap_format_4")) {
                    myCMAPFORMAT4Node = myChild;
                    break;
                }
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() + " / couldn t find 'cmap'. TTX possibly corrupt.");
        }
        /* map characters */
        if (myCMAPFORMAT4Node != null) {
            characterNameMap = new String[MAX_NUMBER_OF_CHARACTERS];
            characterWidthMap = new int[MAX_NUMBER_OF_CHARACTERS];
            Enumeration myEnum = myCMAPFORMAT4Node.enumerateChildren();
            while (myEnum.hasMoreElements()) {
                XMLElement myChild = (XMLElement) myEnum.nextElement();
                String myCode = myChild.getStringAttribute("code");
                if (myCode != null) {
                    int myCurrentInt = getIntFromHexString(myCode);
                    if (myCurrentInt < MAX_NUMBER_OF_CHARACTERS) {
                        characterNameMap[myCurrentInt] = myChild.getStringAttribute("name");
                        /* assign width */
                        for (int j = 0; j < glyphsNameAndWidth.length; j++) {
                            if (characterNameMap[myCurrentInt].equals(glyphsNameAndWidth[j][0])) {
                                String width = glyphsNameAndWidth[j][1];
                                int widthInt = Integer.parseInt(width);
                                characterWidthMap[myCurrentInt] = widthInt;
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() +
                               " / couldn t find 'cmap_format_4'. TTX possibly corrupt.");
        }
    }


    /**
     * Parse the kerning values of the kerning pairs.
     * @param theRootNode XMLElement
     */
    @SuppressWarnings("unchecked")
        private void parseKerning(XMLElement theRootNode) {
        /* get 'kern' */
        XMLElement myKERNNode = null;
        {
            Enumeration myEnum = theRootNode.enumerateChildren();
            while (myEnum.hasMoreElements()) {
                XMLElement myChild = (XMLElement) myEnum.nextElement();
                if (myChild.getName().equals("kern")) {
                    myKERNNode = myChild;
                    break;
                }
            }
        }
        /* get 'kernsubtable' */
        XMLElement myKERNSubtableNode = null;
        if (myKERNNode != null) {
            {
                Enumeration myEnum = myKERNNode.enumerateChildren();
                while (myEnum.hasMoreElements()) {
                    XMLElement myChild = (XMLElement) myEnum.nextElement();
                    if (myChild.getName().equals("kernsubtable")) {
                        myKERNSubtableNode = myChild;
                        break;
                    }
                }
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() +
                               " / couldn t find 'kern'. TTX possibly corrupt.");
        }

        if (myKERNSubtableNode != null) {
            {
                int i = 0;
                int leftIntLast = 0;
                kerningTable = new int[MAX_NUMBER_OF_CHARACTERS][];
                Enumeration myEnum = myKERNSubtableNode.enumerateChildren();
                while (myEnum.hasMoreElements()) {
                    XMLElement myNode = (XMLElement) myEnum.nextElement();

                    int leftInt = getIntFromName(myNode.getStringAttribute("l"));
                    int rightInt = getIntFromName(myNode.getStringAttribute("r"));
                    int valueInt = parseInt(myNode.getStringAttribute("v"));

                    if (leftInt < MAX_NUMBER_OF_CHARACTERS) {
                        if (leftInt != leftIntLast) {
                            kerningTable[leftInt] = new int[MAX_NUMBER_OF_CHARACTERS];
                        }
                        if (rightInt < MAX_NUMBER_OF_CHARACTERS) {
                            /* kerning value */
                            kerningTable[leftInt][rightInt] = valueInt;
                        }
                    }
                    leftIntLast = leftInt;
                    i++;
                }
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() +
                               " / couldn t find 'kernsubtable'. TTX possibly corrupt.");
        }
    }


    private int parseInt(String theString) {
        int myInt = 0;
        if (theString != null) {
            myInt = Integer.parseInt(theString);
        }
        return myInt;
    }


    /**
     * Returns an integer value from an UTF-16 hex string.
     * @param theHexString String
     * @return int
     */
    private int getIntFromHexString(String theHexString) {
        if (theHexString != null) {
            theHexString = theHexString.replaceFirst("0x", "");
            return Integer.parseInt(theHexString, 16);
        } else {
            return 0;
        }
    }


    /**
     * Returns an integer value from a UTF-16 character name.
     * @param theName String
     * @return int
     */
    private int getIntFromName(String theName) {
        int myInt = 0;
        for (int i = 0; i < characterNameMap.length; ++i) {
            if (theName.equals(characterNameMap[i])) {
                myInt = i;
                break;
            }
        }
        return myInt;
    }


    /**
     * Returns the number of fUnits per em,
     * usually 2048 in truetype and 1000 in postscript fonts.
     * @return int
     */
    public int getFUnitsPerEm() {
        return fUnitsPerEm;
    }


    /**
     * Returns a map storing the character advances (simply widths).
     * @return int[]
     */
    public int[] getCharacterWidthMap() {
        return characterWidthMap;
    }


    /**
     * Returns a table containing the kerning values of character pairs.
     * @return int[][]
     */
    public int[][] getKerningTable() {
        return kerningTable;
    }
}
