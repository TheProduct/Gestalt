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


package gestalt.demo.processing.unsorted;


import javax.media.opengl.GL;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.AbstractDrawable;

import mathematik.Vector3f;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;


/** @todo
 * this demo is NOT done yet.
 * the character bitmaps are still missing.
 * plus it is just copied source from core processing.
 */

/**
 * this demo shows how to use a PFont in 'raw' OpenGL via gestalt.
 */

public class UsingPFonts
    extends PApplet {

    private static final long serialVersionUID = 8886435863994154385L;

    private GestaltPlugIn gestalt;

    private PFontRenderer _myFont;

    public void setup() {

        System.err.println("### this sketch is not up to date. PFonts changed ...");
        System.exit(-1);

        /* setup p5 */
        size(640, 480, OPENGL);
        rectMode(CENTER);
        noStroke();

        gestalt = new GestaltPlugIn(this);

        /* create a font renderer and add it to gestalt */
        _myFont = new PFontRenderer(loadFont("data/demo/processing/DINMittelschrift-12.vlw"), gestalt);
        _myFont.text = "ichi";
        gestalt.bin(Gestalt.BIN_3D).add(_myFont);
    }


    public void draw() {
        /* clear screen */
        background(127, 255, 0);
        _myFont.position.set(mouseX, mouseY);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingPFonts.class.getName()});
    }


    public class PFontRenderer
        extends AbstractDrawable {

        public String text;

        public Vector3f position = new Vector3f();

        public float size = 12;

        public float leading = 14;

        public int align = 0; //LEFT;

        private final PFont textFont;

        private GL gl;

        public PFontRenderer(PFont thePFont, GestaltPlugIn thePlugin) {
            textFont = thePFont;
            gestalt = thePlugin;
        }


        public void draw(final GLContext theContext) {
            gl = (  theContext).gl;
            text(text, position.x, position.y);
        }


        /**
         * Internal buffer used by the text() functions
         * because the String object is slow
         */
        private char textBuffer[] = new char[8 * 1024];

        /**
         * Draw a chunk of text.
         * Newlines that are \n (Unix newline or linefeed char, ascii 10)
         * are honored, but \r (carriage return, Windows and Mac OS) are
         * ignored.
         */
        public void text(String str, float x, float y) {
            if (textFont == null) {
                throw new RuntimeException("use textFont() before text()");
            }

            int length = str.length();
            if (length > textBuffer.length) {
                textBuffer = new char[length + 10];
            }
            str.getChars(0, length, textBuffer, 0);

            int start = 0;
            int index = 0;
            while (index < length) {
                if (textBuffer[index] == '\n') {
                    textLineImpl(textBuffer, start, index, x, y);
                    start = index + 1;
                    y += leading;
                }
                index++;
            }
            if (start < length) {
                textLineImpl(textBuffer, start, index, x, y);
            }
        }


        /**
         * Handles placement of a text line, then calls textLinePlaced
         * to actually render at the specific point.
         */
        protected void textLineImpl(char buffer[], int start, int stop,
                                    float x, float y) {
            /** @todo add alignment here. */
//        if (textAlign == CENTER) {
//            x -= textWidthImpl(buffer, start, stop) / 2f;
//
//        } else if (textAlign == RIGHT) {
//            x -= textWidthImpl(buffer, start, stop);
//        }
            textLinePlacedImpl(buffer, start, stop, x, y);
        }


        /**
         * Implementation of returning the text width of
         * the chars [start, stop) in the buffer.
         * Unlike the previous version that was inside PFont, this will
         * return the size not of a 1 pixel font, but the actual current size.
         */
        protected float textWidthImpl(char buffer[], int start, int stop) {
            float wide = 0;
            for (int i = start; i < stop; i++) {
                // could add kerning here, but it just ain't implemented
                wide += textFont.width(buffer[i]) * size;
            }
            return wide;
        }


        protected void textLinePlacedImpl(char buffer[], int start, int stop,
                                          float x, float y) {
            for (int index = start; index < stop; index++) {
                textCharImpl(buffer[index], x, y); //, 0); //z);

                // this doesn't account for kerning
                x += textWidth(buffer[index]);
            }
        }


        public float textWidth(char c) {
            textBuffer[0] = c;
            return textWidthImpl(textBuffer, 0, 1);
        }


        protected void textCharImpl(char ch, float x, float y) { //, float z) {
//            int index = textFont.index(ch);
//            if (index == -1) {
//                return;
//            }
//
//            PImage glyph = textFont.images[index];
//
//            float high = (float) textFont.height[index] / textFont.size;
//            float bwidth = (float) textFont.width[index] / textFont.size;
//            float lextent = (float) textFont.leftExtent[index] / textFont.size;
//            float textent = (float) textFont.topExtent[index] / textFont.size;
//
//            float x1 = x + lextent * size;
//            float y1 = y - textent * size;
//            float x2 = x1 + bwidth * size;
//            float y2 = y1 + high * size;
//
//            textCharModelImpl(glyph,
//                              x1, y1, x2, y2,
//                              textFont.width[index], textFont.height[index]);
        }


        protected void textCharModelImpl(PImage glyph,
                                         float x1, float y1, //float z1,
                                         float x2, float y2, //float z2,
                                         int u2, int v2) {

            /** @todo draw position to the screen */
            // glyph

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(x1, y1, 0);
            gl.glTexCoord2f(0, v2);
            gl.glVertex3f(x1, y2, 0);
            gl.glTexCoord2f(u2, v2);
            gl.glVertex3f(x2, y2, 0);
            gl.glTexCoord2f(u2, 0);
            gl.glVertex3f(x2, y1, 0);
            gl.glEnd();
        }
    }
}
