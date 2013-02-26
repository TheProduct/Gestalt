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


package gestalt.util.meshcreator;


import gestalt.Gestalt;
import gestalt.extension.quadline.QuadFragment;
import gestalt.extension.quadline.QuadLine;
import gestalt.render.Drawable;
import gestalt.material.Material;


public class QuadLineTranslator
    implements DrawableMeshTranslator {

    private int _myPrimitveType = Gestalt.MESH_QUADS;

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof QuadLine;
    }


    public void parse(MeshCreator theParent, Drawable theDrawable) {
        final QuadLine myLine = (QuadLine) theDrawable;
        if (myLine.getLineFragments() != null) {
            parse(theParent, myLine.getLineFragments(), myLine.material());
        }
    }


    public void setPrimitveType(int thePrimitveType) {
        _myPrimitveType = thePrimitveType;
    }


    protected void parse(MeshCreator theParent, QuadFragment[] myQuadsFragments, Material theMaterial) {
        switch (_myPrimitveType) {
            case Gestalt.MESH_QUAD_STRIP:
                parseAsQuadStrip(theParent, myQuadsFragments, theMaterial);
                break;
            case Gestalt.MESH_QUADS:
                parseAsQuads(theParent, myQuadsFragments, theMaterial);
                break;
            default:
                break;
        }
    }


    private void parseAsQuadStrip(MeshCreator theParent, QuadFragment[] myQuadsFragments, Material theMaterial) {
        for (int i = 0; i < myQuadsFragments.length; ++i) {
            final QuadFragment myQuadFragment = myQuadsFragments[i];

            /* a */
            theParent.addVertex(myQuadFragment.pointA);
            if (myQuadFragment.colorA != null) {
                theParent.addColor(myQuadFragment.colorA);
            }
            if (!theMaterial.disableTextureCoordinates) {
                theParent.addTexCoord(myQuadFragment.texcoordA);
            }
            theParent.addNormal(myQuadFragment.normal);

            /* b */
            theParent.addVertex(myQuadFragment.pointB);
            if (myQuadFragment.colorB != null) {
                theParent.addColor(myQuadFragment.colorB);
            }
            if (!theMaterial.disableTextureCoordinates) {
                theParent.addTexCoord(myQuadFragment.texcoordB);
            }
            theParent.addNormal(myQuadFragment.normal);
        }
    }


    private void parseAsQuads(MeshCreator theParent, QuadFragment[] myQuadsFragments, Material theMaterial) {
        for (int i = 0; i < myQuadsFragments.length - 1; ++i) {
            {
                final QuadFragment myQuadFragment = myQuadsFragments[i];

                /* a */
                theParent.addVertex(myQuadFragment.pointA);
                if (myQuadFragment.colorA != null) {
                    theParent.addColor(myQuadFragment.colorA);
                }
                if (!theMaterial.disableTextureCoordinates) {
                    theParent.addTexCoord(myQuadFragment.texcoordA);
                }
                theParent.addNormal(myQuadFragment.normal);

                /* b */
                theParent.addVertex(myQuadFragment.pointB);
                if (myQuadFragment.colorB != null) {
                    theParent.addColor(myQuadFragment.colorB);
                }
                if (!theMaterial.disableTextureCoordinates) {
                    theParent.addTexCoord(myQuadFragment.texcoordB);
                }
                theParent.addNormal(myQuadFragment.normal);
            }

            {
                final QuadFragment myQuadFragment = myQuadsFragments[i + 1];

                /* b + 1 */
                theParent.addVertex(myQuadFragment.pointB);
                if (myQuadFragment.colorB != null) {
                    theParent.addColor(myQuadFragment.colorB);
                }
                if (!theMaterial.disableTextureCoordinates) {
                    theParent.addTexCoord(myQuadFragment.texcoordB);
                }
                theParent.addNormal(myQuadFragment.normal);

                /* a + 1 */
                theParent.addVertex(myQuadFragment.pointA);
                if (myQuadFragment.colorA != null) {
                    theParent.addColor(myQuadFragment.colorA);
                }
                if (!theMaterial.disableTextureCoordinates) {
                    theParent.addTexCoord(myQuadFragment.texcoordA);
                }
                theParent.addNormal(myQuadFragment.normal);
            }
        }
    }
}
