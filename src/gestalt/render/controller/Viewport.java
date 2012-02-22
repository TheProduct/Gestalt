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


package gestalt.render.controller;


import java.io.Serializable;


/**
 * structure to store viewport properties.
 */

public class Viewport
    implements Serializable {

    public int x = 0;

    public int y = 0;

    public int width = 640;

    public int height = 480;

    public Viewport() {}


    public Viewport(final Viewport theViewport) {
        set(theViewport);
    }


    public Viewport(final String theViewport) {
        set(theViewport);
    }


    public Viewport(final int theX, final int theY,
                    final int theWidth, final int theHeight) {
        x = theX;
        y = theY;
        width = theWidth;
        height = theHeight;
    }


    public final void set(final Viewport theViewport) {
        x = theViewport.x;
        y = theViewport.y;
        width = theViewport.width;
        height = theViewport.height;
    }


    public final void set(String theViewport) {
        theViewport = theViewport.replaceAll("x:", "");
        theViewport = theViewport.replaceAll("y:", "");
        theViewport = theViewport.replaceAll("width:", "");
        theViewport = theViewport.replaceAll("height:", "");
        theViewport = theViewport.replace("(", "");
        theViewport = theViewport.replace(")", "");
        theViewport = theViewport.replaceAll(" ", "");
        String[] myComponents = theViewport.split(",");

        try {
            x = Integer.parseInt(myComponents[0]);
        } catch (Exception ex) {
        }
        try {
            y = Integer.parseInt(myComponents[1]);
        } catch (Exception ex1) {
        }
        try {
            width = Integer.parseInt(myComponents[2]);
        } catch (Exception ex2) {
        }
        try {
            height = Integer.parseInt(myComponents[3]);
        } catch (Exception ex3) {
        }
    }


    public String toString() {
        return "(x:" + x + " y:" + y + " width:" + width + " height:" + height + ")";
    }
}
