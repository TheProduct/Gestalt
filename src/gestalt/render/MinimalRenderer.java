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


package gestalt.render;


import gestalt.context.Display;
import gestalt.context.DisplayCapabilities;
import gestalt.input.EventHandler;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.shape.DrawableFactory;


/**
 * minimal version of a renderer with almost no features.
 * it basically opens a window, creates an eventhandler and
 * a drawablefactory and waits for things to happen.
 */

public class MinimalRenderer {

    /**
     * workaround that forces the VM to actually quit when 'quit()' is called.
     */
    public static boolean WORKAROUND_FORCE_QUIT = true;

    protected boolean _myIsInitalized;

    protected DrawableFactory _myDrawablefactory;

    protected Display _myDisplay;

    protected Bin _myBin;

    protected EventHandler _myEvent;

    public MinimalRenderer() {
        _myIsInitalized = false;
    }


    /**
     *
     * @param theDisplayCapabilities DisplayCapabilities
     */
    public void create(DisplayCapabilities theDisplayCapabilities) {
        if (!_myIsInitalized) {
            _myIsInitalized = true;

            /* core */
            createDrawablefactory(theDisplayCapabilities);
            createEvent();
            createDisplay(theDisplayCapabilities);
            _myBin = new RenderBin(20);
        }
    }


    /**
     * replace this method to create some alternative display.
     * @param theDisplayCapabilities DisplayCapabilities
     */
    protected void createDisplay(DisplayCapabilities theDisplayCapabilities) {
        _myDisplay = _myDrawablefactory.display(theDisplayCapabilities, this, _myEvent);
        _myDisplay.initialize();
    }


    protected void createDrawablefactory(DisplayCapabilities theDisplayCapabilities) {
        _myDrawablefactory = DrawableFactory.getFactory();
    }


    protected void createEvent() {
        _myEvent = _myDrawablefactory.eventhandler();
    }


    /**
     *
     */
    public void quit() {
        _myDisplay.finish();
        _myEvent = null;
        _myDrawablefactory = null;
        /**
         * @todo
         * the system exit call is here because the application does not
         * quit on windows machines. very bizarr. very uncool. anyway.
         */
        if (WORKAROUND_FORCE_QUIT) {
            System.exit(0);
        }
    }


    /**
     *
     * @return Bin
     */
    public final Bin bin() {
        return _myBin;
    }


    /**
     *
     * @param theID int
     * @return Bin
     */
    public Bin bin(int theID) {
        return (Bin) (_myBin.get(theID));
    }


    /**
     * get reference to the 'display'
     * @return Display
     */
    public final Display display() {
        return _myDisplay;
    }


    /**
     *
     * @param theDisplay Display
     */
    public final void setDisplayRef(Display theDisplay) {
        _myDisplay = theDisplay;
    }


    /**
     * refresh 'displaycapabilities'
     */
    public final void updateDisplayCapabilities() {
        _myDisplay.updateDisplayCapabilities();
    }


    /**
     *
     * @return DisplayCapabilities
     */
    public final DisplayCapabilities displaycapabilities() {
        return _myDisplay.displaycapabilities();
    }


    /**
     * override this method and define 'displaycapabilities' in here.
     * @return DisplayCapabilities
     */
    public DisplayCapabilities createDisplayCapabilities() {
        return new DisplayCapabilities();
    }


    /**
     *
     * @return DrawableFactory
     */
    public final DrawableFactory drawablefactory() {
        return _myDrawablefactory;
    }


    /**
     *
     * @param theDrawableFactory DrawableFactory
     */
    public final void setDrawablefactoryRef(DrawableFactory theDrawableFactory) {
        _myDrawablefactory = theDrawableFactory;
    }


    /**
     *
     * @param theEvent EventHandler
     */
    public final void setEventRef(EventHandler theEvent) {
        _myEvent = theEvent;
    }


    /**
     *
     * @return EventHandler
     */
    public EventHandler event() {
        return _myEvent;
    }
}
