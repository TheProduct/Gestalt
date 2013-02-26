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

package gestalt.model;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;

import static gestalt.Gestalt.*;

import mathematik.Vector3f;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;


public class ModelUtil {

    public static int getGestaltPrimitiveType(String theType) {
        if (theType.equalsIgnoreCase("quads")) {
            return MESH_QUADS;
        } else if (theType.equalsIgnoreCase("triangles")) {
            return MESH_TRIANGLES;
        }
        return UNDEFINED;
    }


    public static float[] rearrangeVertices(float[] theVertices,
                                            int[] theIndices,
                                            int theNumberOfVertexComponents,
                                            Vector3f theScale,
                                            Vector3f thePosition) {
        float[] myRearrangedVertices = new float[theIndices.length * theNumberOfVertexComponents];
        int myVertexIndex = -1;
        for (int i = 0; i < theIndices.length; i++) {
            int myIndex = theIndices[i] * theNumberOfVertexComponents;
            if (theNumberOfVertexComponents == 3) {
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex] * theScale.x + thePosition.x;
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex + 1] * theScale.y + thePosition.y;
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex + 2] * theScale.z + thePosition.z;
            } else if (theNumberOfVertexComponents == 2) {
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex] * theScale.x + thePosition.x;
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex + 1] * theScale.y + thePosition.y;
            } else if (theNumberOfVertexComponents == 4) {
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex] * theScale.x + thePosition.x;
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex + 1] * theScale.y + thePosition.y;
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex + 2] * theScale.z + thePosition.z;
                myRearrangedVertices[++myVertexIndex] = theVertices[myIndex + 3] * 1 + 0;
            }
        }
        return myRearrangedVertices;
    }


    public static Vector<ModelAnimation> getAnimations(InputStream theAnimationsFile) {
        /* get xmlelement for the model config file */
        XMLElement myModelConfigXML = new XMLElement();
        InputStreamReader configreader = new InputStreamReader(theAnimationsFile);
        try {
            myModelConfigXML.parseFromReader(configreader);
        } catch (XMLParseException ex) {
            System.err.println("### ERROR @ ModelUtil / couldn t parse ANIMATIONSXML." + ex);
        } catch (IOException ex) {
            System.err.println("### ERROR @ ModelUtil / couldn t read ANIMATIONSXML." + ex);
        }

        /* get animations and store them in a vector */
        Vector<ModelAnimation> myAnimations = new Vector<ModelAnimation> ();
        Enumeration<? > myConfigEnum = myModelConfigXML.enumerateChildren();
        while (myConfigEnum.hasMoreElements()) {
            XMLElement myChild = (XMLElement) myConfigEnum.nextElement();
            String myName = myChild.getStringAttribute("name");
            int myStartFrame = myChild.getIntAttribute("start");
            int myStopFrame = myChild.getIntAttribute("stop");
            ModelAnimation myAnimation = new ModelAnimation(myName, myStartFrame, myStopFrame);
            myAnimations.add(myAnimation);
        }

        return myAnimations;
    }
}
