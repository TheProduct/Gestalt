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


package gestalt.context;


import java.io.InputStream;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import gestalt.Gestalt;
import gestalt.material.Color;

import mathematik.Vector2i;


/**
 * is a structure of properties defining the window. some of those properties
 * need to be set before the window is instantiated, others can be set at
 * runtime.
 */
public class DisplayCapabilities {

    /**
     *
     */
    public String name = "*";

    /**
     *
     */
    public int width = 640;

    /**
     *
     */
    public int height = 480;

    /**
     *
     */
    public Vector2i location;

    /**
     *
     */
    public Vector2i canvaslocation = new Vector2i();

    /**
     *
     */
    public boolean undecorated = false;

    /**
     *
     */
    public boolean fullscreen = false;

    /**
     *
     */
    public boolean centered = true;

    /**
     *
     */
    public Color backgroundcolor = new Color(0, 0, 0, 1);

    /**
     *
     */
    public int antialiasinglevel = 0;

    /**
     *
     */
    public boolean cursor = true;

    /**
     *
     */
    public boolean headless = false;

    /**
     *
     */
    public boolean switchresolution = true;

    /**
     *
     */
    public boolean synctovblank = false;

    /**
     *
     */
    public int device = Gestalt.UNDEFINED;

    public DisplayCapabilities() {
    }

    public DisplayCapabilities(int theWidth, int theHeight) {
        this();
        width = theWidth;
        height = theHeight;
    }

    public DisplayCapabilities(String theName,
                               int theWidth,
                               int theHeight,
                               Vector2i theLocation,
                               Vector2i theCanvasLocation,
                               boolean theUndecorated,
                               boolean theFullscreen,
                               boolean theCentered,
                               Color theBackgroundcolor,
                               int theAntialiasinglevel,
                               boolean theCursor,
                               boolean theHeadless,
                               boolean theSwitchResolution,
                               boolean theSyncToVBlank,
                               int theDevice) {
        name = theName;
        width = theWidth;
        height = theHeight;
        location = theLocation;
        canvaslocation = theCanvasLocation;
        undecorated = theUndecorated;
        fullscreen = theFullscreen;
        centered = theCentered;
        backgroundcolor.r = theBackgroundcolor.r;
        backgroundcolor.g = theBackgroundcolor.g;
        backgroundcolor.b = theBackgroundcolor.b;
        backgroundcolor.a = theBackgroundcolor.a;
        antialiasinglevel = theAntialiasinglevel;
        cursor = theCursor;
        headless = theHeadless;
        switchresolution = theSwitchResolution;
        synctovblank = theSyncToVBlank;
        device = theDevice;
    }

    public DisplayCapabilities(DisplayCapabilities theDisplayCapabilities) {
        set(theDisplayCapabilities);
    }

    public static DisplayCapabilities getFromFile(InputStream theFilename) {
        return new DisplayCapabilitiesXMLParser().get(theFilename);
    }

    public void set(DisplayCapabilities theDisplayCapabilities) {
        name = theDisplayCapabilities.name;
        width = theDisplayCapabilities.width;
        height = theDisplayCapabilities.height;
        location = theDisplayCapabilities.location;
        canvaslocation = theDisplayCapabilities.canvaslocation;
        undecorated = theDisplayCapabilities.undecorated;
        fullscreen = theDisplayCapabilities.fullscreen;
        centered = theDisplayCapabilities.centered;
        backgroundcolor.r = theDisplayCapabilities.backgroundcolor.r;
        backgroundcolor.g = theDisplayCapabilities.backgroundcolor.g;
        backgroundcolor.b = theDisplayCapabilities.backgroundcolor.b;
        backgroundcolor.a = theDisplayCapabilities.backgroundcolor.a;
        antialiasinglevel = theDisplayCapabilities.antialiasinglevel;
        cursor = theDisplayCapabilities.cursor;
        headless = theDisplayCapabilities.headless;
        switchresolution = theDisplayCapabilities.switchresolution;
        synctovblank = theDisplayCapabilities.synctovblank;
        device = theDisplayCapabilities.device;
    }

    public static int getRendererFromString(String renderer) {
        if (renderer.equalsIgnoreCase("jogl")) {
            return Gestalt.ENGINE_JOGL;
        } else {
            System.err.println("### ERROR @ Display / could find renderer");
            System.exit(-1);
        }
        return -1;
    }

    public static Vector2i getScreenSize() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        DisplayMode dm = gs.getDisplayMode();
        return new Vector2i(dm.getWidth(), dm.getHeight());
    }

    public static void listDisplayDevices() {
        System.out.println("### DISPLAY DEVICES");
        GraphicsDevice[] _myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (int j = 0; j < _myGraphicsDevice.length; j++) {
            GraphicsDevice myGraphicsDevice = _myGraphicsDevice[j];
            System.out.println(j + ": "
                    + myGraphicsDevice.getDisplayMode().getWidth() + " x "
                    + myGraphicsDevice.getDisplayMode().getHeight() + "px / "
                    + myGraphicsDevice.getDisplayMode().getBitDepth() + " Bit");
        }
    }

    public static void main(String[] args) {
        listDisplayDevices();
    }

    public String toString() {
        String myString = "name: " + name + "\n" + "width: " + width + "\n" + "height: " + height + "\n" + "location: " + location + "\n" + "canvaslocation: " + canvaslocation + "\n" + "undecorated: " + undecorated + "\n" + "fullscreen: " + fullscreen + "\n" + "centered: " + centered + "\n" + "backgroundcolor: " + backgroundcolor + "\n" + "antialiasinglevel: " + antialiasinglevel + "\n" + "cursor: " + cursor + "\n" + "headless: " + headless + "\n" + "switchresolution: " + switchresolution + "\n" + "synctovblank: " + synctovblank + "\n";
        return myString;
    }
}
