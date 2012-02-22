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


package gestalt.util;

import gestalt.material.texture.bitmap.ByteBitmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Iterator;
import java.util.Vector;


/**
 * FontTextureCreator draws attributed strings as bitmaps. NOTE the
 * implementation of 'newline' is still flaky. a 'newline' at the beginning of a
 * string should be avoided. multiple consecutive 'newlines' will be interpreted
 * as one single newline. a 'newline' at the end of a paragraph is ignored.
 */
public abstract class FontTextureCreator {

    public static final int LEFT = 0;

    public static final int CENTERED = 1;

    public static final int RIGHT = 2;

    public static Color foreground;

    public static Color background;

    public static int padding = 5;

    public static int alignment = LEFT;

    public static float linewidth = 1;

    public static final Vector<TextFragment> textfragments = new Vector<TextFragment>();

    private final static int _myImageType = BufferedImage.TYPE_4BYTE_ABGR;

    public static Font getFont(InputStream theStream, float theSize) {
        return getFont(theStream, theSize, Font.PLAIN, Font.TRUETYPE_FONT);
    }

    public static Font getFont(InputStream theStream, float theSize,
                               int theStyle) {
        return getFont(theStream, theSize, theStyle, Font.TRUETYPE_FONT);
    }

    public static Font getFont(InputStream theStream, float theSize,
                               int theStyle, int theType) {
        try {
            return Font.createFont(theType, theStream).deriveFont(theStyle,
                                                                  theSize);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Font getInstalledFont(String theFontName, float theSize,
                                        int theStyle) {
        return Font.decode(theFontName).deriveFont(theStyle, theSize);
    }

    public static Font getInstalledFont(String theFontName, float theSize) {
        return getInstalledFont(theFontName, theSize, Font.PLAIN);
    }


    /* ByteBitmap */
    public static ByteBitmap getBitmap(AttributedString theString,
                                       float theWidth) {
        return getBitmap(theString, theWidth, true);
    }

    public static ByteBitmap getBitmap(AttributedString theString,
                                       float theWidth,
                                       boolean theAntialias) {
        return getBitmap(theString, theWidth, theAntialias, null);

    }

    public static ByteBitmap getBitmap(AttributedString theString,
                                       float theWidth,
                                       boolean theAntialias,
                                       StringBuffer myLayoutedString) {
        return ImageUtil.convertBufferedImage2ByteBitmap(getBufferedImage(
                theString, theWidth, theAntialias, myLayoutedString));
    }

    public static ByteBitmap getBitmap(Vector<TextFragment> theTextFragments,
                                       float theWidth) {
        return getBitmap(theTextFragments, theWidth, true);
    }

    public static ByteBitmap getBitmap(Vector<TextFragment> theTextFragments,
                                       float theWidth, boolean theAntialias) {
        return getBitmap(getAttributedString(theTextFragments), theWidth, theAntialias, null);
    }

    public static ByteBitmap getBitmap(float theWidth, boolean theAntialias) {
        return getBitmap(getAttributedString(textfragments), theWidth, theAntialias, null);
    }

    public static ByteBitmap getBitmap(float theWidth) {
        return getBitmap(theWidth, true);
    }


    /* BufferedImage */
    public static BufferedImage getBufferedImage(AttributedString theString,
                                                 float theWidth) {
        return getBufferedImage(theString, theWidth, true);
    }

    public static BufferedImage getBufferedImage(AttributedString theString,
                                                 float theWidth, boolean theAntialias) {
        return getBufferedImage(theString, theWidth, theAntialias, null);
    }

    public static double getStringWidth(final Font theFont, final String theText, boolean theAntialias) {
        final Graphics2D myGraphics = new BufferedImage(1, 1, _myImageType).createGraphics();
        if (theAntialias) {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        final FontRenderContext theContext = myGraphics.getFontRenderContext();

        return theFont.getStringBounds(theText, theContext).getWidth();
    }

    public static int suggestTextureWidth(final Font theFont, final String theText, boolean theAntialias) {
        final double myWidth = getStringWidth(theFont, theText, theAntialias);
        return ImageUtil.getNextPowerOf2((int)Math.ceil(myWidth));
    }

    public static BufferedImage getBufferedImage(AttributedString theString,
                                                 float theWidth,
                                                 boolean theAntialias,
                                                 StringBuffer myLayoutedString) {
        /* create layout */
        final Graphics2D myTempGraphics = createGraphics(createImage(1, 1), theAntialias);
        final FontRenderContext myFontRenderContext = myTempGraphics.getFontRenderContext();
        final LineBreakMeasurer myMeasurer = new LineBreakMeasurer(theString.getIterator(), myFontRenderContext);
        final Vector<TextLayout> myLayouts = new Vector<TextLayout>();

        /* reconstruct string ( stoopid ) */
        AttributedCharacterIterator myCharIterator = theString.getIterator();
        StringBuilder myBuffer = new StringBuilder();
        for (char c = myCharIterator.first(); c != AttributedCharacterIterator.DONE; c = myCharIterator.next()) {
            myBuffer.append(c);
        }
        final String myText = myBuffer.toString();

        /* get lines */
        int myNumberOfLines = 0;
        Vector<Integer> myLineBreaks = new Vector<Integer>();

        while (myMeasurer.getPosition() < myText.length()) {
            TextLayout myLayout; // = myMeasurer.nextLayout(theWidth - 2 *
            // padding);
            myLineBreaks.add(myMeasurer.getPosition());
            if (myText.indexOf("\n", myMeasurer.getPosition()) > myMeasurer.getPosition()) {
                myLayout = myMeasurer.nextLayout(theWidth - 2 * padding,
                                                 myText.indexOf("\n", myMeasurer.getPosition()) + 1, false);
            } else {
                myLayout = myMeasurer.nextLayout(theWidth - 2 * padding);
            }

            myLayouts.add(myLayout);
            myNumberOfLines++;
        }

        /* parse string to represent linebreaks */
        if (myLayoutedString != null) {
            for (int i = 1; i < myLineBreaks.size(); i++) {
                String mySubString = myText.substring(myLineBreaks.get(i - 1),
                                                      myLineBreaks.get(i));
                myLayoutedString.append(mySubString);
                if (mySubString.charAt(mySubString.length() - 1) != '\n') {
                    myLayoutedString.append('\n');
                }
            }
            /* get last segment */
            myLayoutedString.append(myText.substring(myLineBreaks.get(myLineBreaks.size() - 1), myText.length()));
        }

        /* get resulting height */
        int myHeight;
        {
            float myPosition = padding;
            Iterator<TextLayout> myIterator = myLayouts.iterator();
            boolean myFirstLine = true;
            while (myIterator.hasNext()) {
                TextLayout myLayout = myIterator.next();
                if (myFirstLine) {
                    myFirstLine = false;
                    myPosition += myLayout.getAscent();
                } else {
                    myPosition += myLayout.getAscent() * linewidth;
                }
                if (myIterator.hasNext()) {
                    myPosition += myLayout.getDescent() * linewidth;
                } else {
                    myPosition += myLayout.getDescent();
                }
            }
            myHeight = (int)myPosition + padding;
        }

        /* create image */
        final BufferedImage myBufferImage = createImage((int)theWidth,
                                                        myHeight);
        final Graphics2D myGraphics = createGraphics(myBufferImage,
                                                     theAntialias);
        if (foreground != null) {
            myGraphics.setColor(foreground);
        }
        if (background != null) {
            myGraphics.setBackground(background);
            myGraphics.clearRect(0, 0, (int)theWidth, myHeight);
        }

        {
            float y = padding;
            boolean myFirstLine = true;
            final Iterator<TextLayout> myIterator = myLayouts.iterator();
            while (myIterator.hasNext()) {
                TextLayout myLayout = myIterator.next();
                float x = 0;
                if (alignment == LEFT) {
                    x = padding;
                } else if (alignment == CENTERED) {
                    x = (theWidth - (float)myLayout.getBounds().getWidth()) / 2.0f;
                } else if (alignment == RIGHT) {
                    x = theWidth - (float)myLayout.getBounds().getWidth() - padding;
                }

                if (myFirstLine) {
                    myFirstLine = false;
                    y += myLayout.getAscent();
                } else {
                    y += myLayout.getAscent() * linewidth;
                }

                myLayout.draw(myGraphics, x, y);

                y += myLayout.getDescent() * linewidth;
            }
        }
        return myBufferImage;
    }

    public static int getLayoutedString(AttributedString theString,
                                        float theWidth, boolean theAntialias, StringBuffer myLayoutedString) {
        /* create layout */
        final Graphics2D myTempGraphics = createGraphics(createImage(1, 1),
                                                         theAntialias);
        final FontRenderContext myFontRenderContext = myTempGraphics.getFontRenderContext();
        final LineBreakMeasurer myMeasurer = new LineBreakMeasurer(theString.getIterator(), myFontRenderContext);
        final Vector<TextLayout> myLayouts = new Vector<TextLayout>();

        /* reconstruct string ( stoopid ) */
        AttributedCharacterIterator myCharIterator = theString.getIterator();
        StringBuilder myBuffer = new StringBuilder();
        for (char c = myCharIterator.first(); c != AttributedCharacterIterator.DONE; c = myCharIterator.next()) {
            myBuffer.append(c);
        }
        final String myText = myBuffer.toString();

        /* get lines */
        int myNumberOfLines = 0;
        Vector<Integer> myLineBreaks = new Vector<Integer>();

        while (myMeasurer.getPosition() < myText.length()) {
            TextLayout myLayout; // = myMeasurer.nextLayout(theWidth - 2 *
            // padding);
            myLineBreaks.add(myMeasurer.getPosition());
            if (myText.indexOf("\n", myMeasurer.getPosition()) > myMeasurer.getPosition()) {
                myLayout = myMeasurer.nextLayout(theWidth - 2 * padding, myText.indexOf("\n", myMeasurer.getPosition()) + 1, false);
            } else {
                myLayout = myMeasurer.nextLayout(theWidth - 2 * padding);
            }

            myLayouts.add(myLayout);
            myNumberOfLines++;
        }

        /* parse string to represent linebreaks */
        if (myLayoutedString != null) {
            for (int i = 1; i < myLineBreaks.size(); i++) {
                String mySubString = myText.substring(myLineBreaks.get(i - 1),
                                                      myLineBreaks.get(i));
                myLayoutedString.append(mySubString);
                if (mySubString.charAt(mySubString.length() - 1) != '\n') {
                    myLayoutedString.append('\n');
                }
            }
            /* get last segment */
            myLayoutedString.append(myText.substring(myLineBreaks.get(myLineBreaks.size() - 1), myText.length()));
        }

        /* get resulting height */
        int myHeight;
        {
            float myPosition = padding;
            final Iterator<TextLayout> myIterator = myLayouts.iterator();
            boolean myFirstLine = true;
            while (myIterator.hasNext()) {
                TextLayout myLayout = myIterator.next();
                if (myFirstLine) {
                    myFirstLine = false;
                    myPosition += myLayout.getAscent();
                } else {
                    myPosition += myLayout.getAscent() * linewidth;
                }
                if (myIterator.hasNext()) {
                    myPosition += myLayout.getDescent() * linewidth;
                } else {
                    myPosition += myLayout.getDescent();
                }
            }
            myHeight = (int)myPosition + padding;
        }

        return myHeight;
    }

    private static Graphics2D createGraphics(BufferedImage myBufferImage,
                                             boolean theAntialias) {
        final Graphics2D myGraphics = myBufferImage.createGraphics();
        if (theAntialias) {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        return myGraphics;
    }

    private static BufferedImage createImage(int theWidth, int theHeight) {
        final BufferedImage myBufferImage = new BufferedImage(theWidth,
                                                              theHeight, _myImageType);
        return myBufferImage;
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

    public static AttributedString getAttributedString(Vector<TextFragment> theTextFragments) {
        /* combine string */
        StringBuilder myRenderedString = new StringBuilder();
        for (TextFragment myTextFragment : theTextFragments) {
            myRenderedString.append(myTextFragment.text);
        }

        /* set attributes */
        AttributedString myAttributedString = new AttributedString(myRenderedString.toString());
        int myIndex = 0;
        for (TextFragment myTextFragment : theTextFragments) {
            if (myTextFragment.text.length() > 0) {
                final int myNextIndex = myIndex + myTextFragment.text.length();
                try {
                    for (TextProperty myTextProperty : myTextFragment.properties) {
                        myAttributedString.addAttribute(myTextProperty.name,
                                                        myTextProperty.value,
                                                        myIndex,
                                                        myNextIndex);
                    }
                } catch (Exception ex) {
                    System.err.println("### ERROR adding text / " + ex);
                }
                myIndex = myNextIndex;
            }
        }

        return myAttributedString;
    }

    public static class TextProperty {

        public final TextAttribute name;

        public final Object value;

        public TextProperty(TextAttribute theName, Object theValue) {
            name = theName;
            value = theValue;
        }
    }

    public static class TextFragment {

        public String text;

        public Vector<TextProperty> properties = new Vector<TextProperty>();

        public void property(TextAttribute theName, Object theValue) {
            properties.add(new TextProperty(theName, theValue));
        }
    }
}
