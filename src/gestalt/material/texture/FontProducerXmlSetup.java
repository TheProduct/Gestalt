/*
 * Gestalt
 *
 * Copyright (C) 2004 Patrick Kochlik + Dennis Paul
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


package gestalt.material.texture;


import java.util.Enumeration;

import static gestalt.Gestalt.*;

import mathematik.Vector2i;

import nanoxml.XMLElement;


public class FontProducerXmlSetup {

    public static void setupFontProducer(FontProducer theFontProducer, XMLElement theXML) {
        Enumeration<? > myEnum = theXML.enumerateChildren();
        while (myEnum.hasMoreElements()) {
            XMLElement myChild = (XMLElement) myEnum.nextElement();

            if (hasAttribute(myChild, "size")) {
                theFontProducer.setSize( (float) myChild.getDoubleAttribute("size"));
            }
            if (hasAttribute(myChild, "linewidth")) {
                theFontProducer.setLineWidth( (float) myChild.getDoubleAttribute("linewidth"));
            }
            if (hasAttribute(myChild, "quality")) {
                if ( ( (String) myChild.getAttribute("quality")).equalsIgnoreCase("FONT_QUALITY_HIGH")) {
                    theFontProducer.setQuality(FONT_QUALITY_HIGH);
                } else {
                    theFontProducer.setQuality(FONT_QUALITY_LOW);
                }
            }
            if (hasAttribute(myChild, "smoothfactor")) {
                theFontProducer.setSmoothFactor(myChild.getIntAttribute("smoothfactor"));
            }
            if (hasAttribute(myChild, "scalefactor")) {
                theFontProducer.setScaleFactor( (float) myChild.getDoubleAttribute("scalefactor"));
            }
            if (hasAttribute(myChild, "alignment")) {
                if ( ( (String) myChild.getAttribute("alignment")).equalsIgnoreCase("FONT_ALIGN_LEFT")) {
                    theFontProducer.setAlignment(FONT_ALIGN_LEFT);
                } else if ( ( (String) myChild.getAttribute("alignment")).equalsIgnoreCase("FONT_ALIGN_CENTER")) {
                    theFontProducer.setAlignment(FONT_ALIGN_CENTER);
                } else if ( ( (String) myChild.getAttribute("alignment")).equalsIgnoreCase("FONT_ALIGN_RIGHT")) {
                    theFontProducer.setAlignment(FONT_ALIGN_RIGHT);
                }
            }
            if (hasAttribute(myChild, "imageborder")) {
                theFontProducer.setImageBorder(string2Vector2i( (String) myChild.getAttribute("imageborder")));
            }
        }
    }


    public static boolean hasAttribute(XMLElement myChild, String theAttribute) {
        if (myChild.getAttribute(theAttribute) != null) {
            return true;
        }
        return false;
    }


    public static Vector2i string2Vector2i(String theString) {
        String[] splitted = theString.split(",");
        return new Vector2i(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
    }
}
