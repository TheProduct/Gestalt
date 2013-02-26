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


import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;


/**
 * this demo shows how to configure a display and open a window.
 */

public class UsingDisplay
    extends AnimatorRenderer {

    public static void main(String[] args) {
        /*
         * create and start renderer without passing 'displaycapabilities'.
         * the 'displaycapabilites' structure holds the settings needed by the
         * renderer to set up a display.
         *
         * note that the renderer tries to use the 'createDisplayCapabilities'
         * method to create a user specified display.
         * in this demo 'createDisplayCapabilities' is used to specify a
         * display.
         */
        new UsingDisplay().init();

        /*
         * instead of using the 'createDisplayCapabilities' method to define
         * 'displaycapabilities' an object can be passed to the renderer with
         * the 'init(DisplayCapabilities)' method.
         *
         *    DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
         *    new UsingDisplay().init(myDisplayCapabilities);
         *
         * instead of newing displaycapability manually, an object can be
         * created by calling
         *
         * DisplayCapabilities.getFromFile(InputStream)
         *
         * the method takes a valid XML file to parse the settings from.
         * this can be very helpful in more complex application since there
         * is no recompilation needed for setting something as simple as the
         * window appearence.
         */
    }


    public UsingDisplay() {
        /*
         * do not use the constructor of a renderer to set renderer specific
         * things as they may be overridden during the instantiation of the
         * renderer.
         * always use the 'setup' method to do things like replacing bins
         * creating cameras etc.
         */
    }


    public DisplayCapabilities createDisplayCapabilities() {
        /*
         * create a 'displaycapabilities' object
         */
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();

        /*
         * this property appears in the bar above the window and is of course
         * not visible if the window is undecorated.
         */
        myDisplayCapabilities.name = "UsingDisplay";

        /* width and height define the window size in pixel. */
        myDisplayCapabilities.width = 800;
        myDisplayCapabilities.height = 600;

        /* undecorated removes the title bar if set to true */
        myDisplayCapabilities.undecorated = false;

        /* switch to fullscreenmode */
        myDisplayCapabilities.fullscreen = false;

        /* 'centered' positions the window in the middle of the screen */
        myDisplayCapabilities.centered = true;

        /*
         * backgroundcolor specifies the background color of the window
         * ( including the alphavalue which only seems to work under osx
         * but nevertheless produces very interesting results ) and the opengl
         * clear color.
         * values range from 0 to 1, where 0 is dark or transparent and 1 is
         * light or opaque.
         */
        myDisplayCapabilities.backgroundcolor.set(1);

        /*
         * antialiasing is experimental. if 'antialiasing' is set higher than 0
         * a fullscene antialiasing is attempted, but not garantueed.
         * again, be warned.
         */
        myDisplayCapabilities.antialiasinglevel = 0;

        /* the cursor flag remove the cursor. */
        myDisplayCapabilities.cursor = false;

        /*
         * there is also a 'headless' option which prevents the renderer from
         * creating an actual window. this is used in cases where a window
         * is created by another part of an application.
         * this option is rather advanced and usually left at 'false'
         */
        myDisplayCapabilities.headless = false;

        /*
         * the 'switchresolution' flag whether the resolution of the display is
         * switched when entering fullscreen mode.
         */
        myDisplayCapabilities.switchresolution = true;

        /**
         * here can select a screen in a multi-screen enviroment.
         * the good news is it that it can help you ( on some systems ) to create
         * a window that is bigger than the default screen.
         * the bad news is it doesn t work for fullscreen mode ( at least not
         * on my mac / ati system ).
         */
        DisplayCapabilities.listDisplayDevices();
        myDisplayCapabilities.device = 0;

        return myDisplayCapabilities;
    }


    public void loop(float theDeltaTime) {
        if (event().keyPressed) {
            /* switch to fullscreen at runtime */
            if (event().key == 'f') {
                displaycapabilities().switchresolution = false;
                displaycapabilities().fullscreen = true;
                displaycapabilities().undecorated = true;
                /**
                 * 'updateDisplayCapabilities' makes sure that the properties
                 * are passed through to the renderer. not all properties
                 * need to call this method. if unsure just call this method,
                 * it wouldn t hurt.
                 */
                updateDisplayCapabilities();
            }
            if (event().key == 'w') {
                displaycapabilities().fullscreen = false;
                displaycapabilities().undecorated = false;
                updateDisplayCapabilities();
            }
            /* change screen height at runtime */
            if (event().key == ',') {
                displaycapabilities().height -= 10;
                updateDisplayCapabilities();
            }
            if (event().key == '.') {
                displaycapabilities().height += 10;
                updateDisplayCapabilities();
            }
            /* change opengl canvas location in frame */
            if (event().key == ';') {
                displaycapabilities().canvaslocation.y -= 10;
                updateDisplayCapabilities();
            }
            if (event().key == ':') {
                displaycapabilities().canvaslocation.y += 10;
                updateDisplayCapabilities();
            }
        }

        /* change background color depending on mouse position */
        displaycapabilities().backgroundcolor.set(0.5f +
                                                  (float) event().mouseX /
                                                  (float) displaycapabilities().width);
    }
}
