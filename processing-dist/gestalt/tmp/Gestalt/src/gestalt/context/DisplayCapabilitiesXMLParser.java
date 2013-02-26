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


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import mathematik.Vector2i;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;


/**
 * reads displaycapabilites from an XML file.
 */


public class DisplayCapabilitiesXMLParser {

    public DisplayCapabilities get(InputStream theFilename) {
        DisplayCapabilities myDisplayCapabilties = new DisplayCapabilities();
        XMLElement xml = new XMLElement();
        InputStreamReader reader = new InputStreamReader(theFilename);
        try {
            xml.parseFromReader(reader);
        } catch (XMLParseException ex) {
            System.err.println("### ERROR @ DisplayCapabilities / couldn t parse XML." + ex);
        } catch (IOException ex) {
            System.err.println("### ERROR @ DisplayCapabilities / couldn t read XML file." + ex);
        }

        myDisplayCapabilties.name =
            xml.getStringAttribute("name") == null ?
            myDisplayCapabilties.name : xml.getStringAttribute("name");

        myDisplayCapabilties.width =
            xml.getAttribute("screenwidth") == null ?
            myDisplayCapabilties.width : xml.getIntAttribute("screenwidth");

        myDisplayCapabilties.height =
            xml.getAttribute("screenheight") == null ?
            myDisplayCapabilties.height : xml.getIntAttribute("screenheight");

        myDisplayCapabilties.location = new Vector2i();
        myDisplayCapabilties.location.x =
            xml.getAttribute("location_x") == null ?
            myDisplayCapabilties.width : xml.getIntAttribute("location_x");

        myDisplayCapabilties.location.y =
            xml.getAttribute("location_y") == null ?
            myDisplayCapabilties.height : xml.getIntAttribute("location_y");

        myDisplayCapabilties.device =
            xml.getAttribute("device") == null ?
            0 : xml.getIntAttribute("device");

        myDisplayCapabilties.undecorated =
            xml.getAttribute("undecorated") == null ?
            myDisplayCapabilties.undecorated :
            xml.getBooleanAttribute("undecorated", "true", "false", false);

        myDisplayCapabilties.fullscreen =
            xml.getAttribute("fullscreen") == null ?
            myDisplayCapabilties.fullscreen :
            xml.getBooleanAttribute("fullscreen", "true", "false", false);

        myDisplayCapabilties.centered =
            xml.getAttribute("centered") == null ?
            myDisplayCapabilties.centered :
            xml.getBooleanAttribute("centered", "true", "false", true);

        myDisplayCapabilties.backgroundcolor.r =
            xml.getAttribute("background_r") == null ?
            myDisplayCapabilties.backgroundcolor.r :
            (float) xml.getDoubleAttribute("background_r");

        myDisplayCapabilties.backgroundcolor.g =
            xml.getAttribute("background_g") == null ?
            myDisplayCapabilties.backgroundcolor.g :
            (float) xml.getDoubleAttribute("background_g");

        myDisplayCapabilties.backgroundcolor.b =
            xml.getAttribute("background_b") == null ?
            myDisplayCapabilties.backgroundcolor.b :
            (float) xml.getDoubleAttribute("background_b");

        myDisplayCapabilties.backgroundcolor.a =
            xml.getAttribute("background_a") == null ?
            myDisplayCapabilties.backgroundcolor.a :
            (float) xml.getDoubleAttribute("background_a");

        myDisplayCapabilties.antialiasinglevel =
            xml.getAttribute("anitaliasinglevel") == null ?
            myDisplayCapabilties.antialiasinglevel :
            xml.getIntAttribute("anitaliasinglevel");

        myDisplayCapabilties.cursor =
            xml.getAttribute("cursor") == null ?
            myDisplayCapabilties.cursor : xml.getBooleanAttribute("cursor", "true", "false", true);

        myDisplayCapabilties.headless =
            xml.getAttribute("headless") == null ?
            myDisplayCapabilties.headless : xml.getBooleanAttribute("headless", "true", "false", true);

        myDisplayCapabilties.switchresolution =
            xml.getAttribute("switchresolution") == null ?
            myDisplayCapabilties.switchresolution : xml.getBooleanAttribute("switchresolution", "true", "false", true);

        myDisplayCapabilties.synctovblank =
            xml.getAttribute("synctovblank") == null ?
            myDisplayCapabilties.synctovblank : xml.getBooleanAttribute("synctovblank", "true", "false", false);

        /** @todo insert canvaslocation */

        return myDisplayCapabilties;
    }
}
