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


package gestalt.demo.basic;

import gestalt.render.SketchRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.FontProducer;

import mathematik.Vector3f;

import data.Resource;


/**
 * this demo shows how to obtain mouse and key events
 *
 * there are two different ways to obtain events.
 *
 * 1 event model
 * implement the 'GestaltKeyListener' and/or 'GestaltMouseListner' interfaces
 * when event methods are invoked, the mouse coordinates and pressed keys
 * are passed as argument
 *
 * 2 polling
 * poll the EventHandler 'event'
 *
 * event.mouseX
 * event.mouseY
 * event.keyDown
 * event.keyCode
 * ...
 *
 * to use this demo, move the mouse over the window and press numbers 1 - 9
 */
public class UsingEventHandling
        extends SketchRenderer {

    public void setup() {
        bin(BIN_2D_FOREGROUND).add(stats_view());

        /*
         * register the renderer at the EventHandler using the
         * 'GestaltKeyListener' and 'GestaltMouseListener' interfaces.
         */
        // this already happens in AnimatorRenderer
//        event().addMouseListener(this);
//        event().addKeyListener(this);

        /* just for illustration purposes */
        createPlanes();

        /* print help */
        printHelp();
    }

    public void loop(float theDeltaTime) {

        /* polling mouse states */
        if (event().mouseClicked) {
            System.out.println("event.mouseClicked " + event().mouseButton);
        }
        if (event().mouseDoubleClicked) {
            System.out.println("event.mouseDoubleClicked " + event().mouseButton);
        }
        if (event().mouseReleased) {
            System.out.println("event.mouseReleased");
        }

        /* polling keys + shift */
        if (event().keyDown && event().shift) {
            System.out.println(event().key + ": " + event().keyCode);
        }

        /* print help */
        if (event().keyPressed) {
            if (event().key == 'h') {
                printHelp();
            }
        }

        /* visualize things */
        updatePlanes(event().mouseX, event().mouseY);

        addStatistic("shift", event().shift);
        addStatistic("meta", event().meta);
        addStatistic("control", event().control);
        addStatistic("alt", event().alt);
        addStatistic("key", (int)event().key + "(" + event().key + ")");

        addStatistic("keyDown", event().keyDown);
        addStatistic("keyReleased", event().keyReleased);
    }

    public void mousePressed(int x, int y, int thePressedMouseButton) {
        if (thePressedMouseButton == MOUSEBUTTON_RIGHT) {
            System.out.println("pressed right: " + x + " " + y + " " + thePressedMouseButton);
        } else if (thePressedMouseButton == MOUSEBUTTON_LEFT) {
            System.out.println("pressed left: " + x + " " + y + " " + thePressedMouseButton);
        }
    }

    public void mouseReleased(int x, int y, int thePressedMouseButton) {
        System.out.println("released: " + x + " " + y + "  " + thePressedMouseButton);
    }

    public void mouseDragged(int x, int y, int thePressedMouseButton) {
        System.out.println("dragged: " + x + " " + y + "  " + thePressedMouseButton);
    }

    public void keyPressed(char theKey, int theKeyCode) {
        highlightPlane(theKey);
        switch (theKeyCode) {
            case KEYCODE_UP:
                System.out.println("cursor up");
                break;
            case KEYCODE_DOWN:
                System.out.println("cursor down");
                break;
            case KEYCODE_LEFT:
                System.out.println("cursor left");
                break;
            case KEYCODE_RIGHT:
                System.out.println("cursor right");
                break;
        }
    }

    public void keyReleased(char theKey, int theKeyCode) {
        dehighlightPlane(theKey);
    }

    private void printHelp() {
        System.out.println("### INFO / How to use the demo:");
        System.out.println("### press 'h' for help");
        System.out.println("### press '1-9' to highlight the different numbers");
        System.out.println("### move mouse");
        System.out.println("### click mouse to release mouse events");
    }


    /*
     *
     * illustration purpose; not needed for basic functionality beauty section :)
     *
     */
    private Plane[] _myFontPlane;

    private void createPlanes() {
        /*
         * for further information check out demo 'UsingFontTextures.java'
         */

        /* create a fontproducer */
        String theFontFile = Resource.getPath("demo/font/silkscreen/slkscr.ttf");
        FontProducer _myFontProducer = Bitmaps.getFontProducer(theFontFile);
        _myFontProducer.setSize(32);
        _myFontProducer.setQuality(FONT_QUALITY_LOW);
        _myFontProducer.antialias(false);

        /* create text planes */
        _myFontPlane = new Plane[9];
        TexturePlugin[] _myFontTexture = new TexturePlugin[_myFontPlane.length];
        for (int i = 0; i < _myFontPlane.length; ++i) {
            /* create plane */
            _myFontPlane[i] = drawablefactory().plane();
            _myFontPlane[i].material().color4f().set(1f, 1f);

            /* create texture */
            _myFontTexture[i] = drawablefactory().texture();
            _myFontTexture[i].setFilterType(TEXTURE_FILTERTYPE_NEAREST);
            _myFontTexture[i].load(_myFontProducer.getBitmap(String.valueOf(i + 1)));

            _myFontPlane[i].material().addPlugin(_myFontTexture[i]);
            _myFontPlane[i].setPlaneSizeToTextureSize();
            bin(BIN_3D).add(_myFontPlane[i]);
        }
    }

    private void updatePlanes(int x, int y) {
        Vector3f myCenter = new Vector3f(0, 0, 0);
        Vector3f myMouse = new Vector3f(x, y, 0);
        myMouse.scale(1.f / (float)_myFontPlane.length);
        for (int i = 0; i < _myFontPlane.length; ++i) {
            myCenter.add(myMouse);
            myCenter.z = i * 2;
            _myFontPlane[i].transform().translation.set(myCenter);
        }
    }

    private void highlightPlane(char key) {
        switch (key) {
            case '1':
                _myFontPlane[0].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '2':
                _myFontPlane[1].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '3':
                _myFontPlane[2].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '4':
                _myFontPlane[3].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '5':
                _myFontPlane[4].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '6':
                _myFontPlane[5].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '7':
                _myFontPlane[6].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '8':
                _myFontPlane[7].material().color4f().set(1f, 0, 0, 1f);
                break;
            case '9':
                _myFontPlane[8].material().color4f().set(1f, 0, 0, 1f);
                break;
        }
    }

    private void dehighlightPlane(char key) {
        switch (key) {
            case '1':
                _myFontPlane[0].material().color4f().set(1f, 1f);
                break;
            case '2':
                _myFontPlane[1].material().color4f().set(1f, 1f);
                break;
            case '3':
                _myFontPlane[2].material().color4f().set(1f, 1f);
                break;
            case '4':
                _myFontPlane[3].material().color4f().set(1f, 1f);
                break;
            case '5':
                _myFontPlane[4].material().color4f().set(1f, 1f);
                break;
            case '6':
                _myFontPlane[5].material().color4f().set(1f, 1f);
                break;
            case '7':
                _myFontPlane[6].material().color4f().set(1f, 1f);
                break;
            case '8':
                _myFontPlane[7].material().color4f().set(1f, 1f);
                break;
            case '9':
                _myFontPlane[8].material().color4f().set(1f, 1f);
                break;
        }
    }

    public static void main(String[] arg) {
        new UsingEventHandling().init();
    }
}
