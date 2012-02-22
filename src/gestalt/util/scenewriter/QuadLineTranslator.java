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


package gestalt.util.scenewriter;


import gestalt.Gestalt;
import gestalt.extension.quadline.QuadFragment;
import gestalt.extension.quadline.QuadLine;
import gestalt.render.Drawable;
import gestalt.material.Material;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;


public class QuadLineTranslator
        implements DrawableOBJTranslator {

    static String ourObjectName = "quadline";

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof QuadLine;
    }

    public void parse(SceneWriter theParent, Drawable theDrawable) {
        final QuadLine myLine = (QuadLine)theDrawable;
        parse(theParent, myLine.getLineFragments(), myLine.material());
    }

    protected static void parse(SceneWriter theParent, QuadFragment[] myQuadsFragments, Material theMaterial) {
        /* write unique header */
        int myUniqueObjectID = Gestalt.UNDEFINED;
        if (!SceneWriter.IGNORE_OBJECTS) {
            myUniqueObjectID = theParent.bumpUniqueObjectID();
            theParent.writeGroupTag(ourObjectName + myUniqueObjectID);
        }

        /* write material */
        theParent.parseMaterial(theMaterial);

        /* we don t apply transform to points. */

        /* -- vertex data -- */

        /* write vertices -- 'v' */
        /* write texture coordiantes -- 'vt' */
        /* write vertex normals -- 'vn' */
        for (int i = 0; i < myQuadsFragments.length; ++i) {
            final QuadFragment myQuadFragment = myQuadsFragments[i];
            theParent.writeVertex(myQuadFragment.pointA);
            theParent.writeVertex(myQuadFragment.pointB);
            theParent.writeTextureCoordinates(myQuadFragment.texcoordA);
            theParent.writeTextureCoordinates(myQuadFragment.texcoordB);
            theParent.writeNormal(myQuadFragment.normal);
        }
        theParent.println();

        /* -- elements -- */

        /* write faces -- 'f' ( this is the only element cinema4d supports. ) */
        /* 'f v/vt/vn v/vt/vn v/vt/vn v/vt/vn' */
        for (int i = 0; i < myQuadsFragments.length - 1; ++i) {
            final int myVertexNormalID = theParent.bumpNormalVertexCounter();

            theParent.print(SceneWriter.FACE);
            theParent.print(SceneWriter.DELIMITER);

            theParent.print(theParent.bumpVertexCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(theParent.bumpTextureCoordinateCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(myVertexNormalID);
            theParent.print(SceneWriter.DELIMITER);

            theParent.print(theParent.bumpVertexCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(theParent.bumpTextureCoordinateCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(myVertexNormalID);
            theParent.print(SceneWriter.DELIMITER);

            theParent.print(theParent.getVertexCounter() + 1);
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(theParent.getTextureCoordinateCounter() + 1);
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(myVertexNormalID + 1);
            theParent.print(SceneWriter.DELIMITER);

            theParent.print(theParent.getVertexCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(theParent.getTextureCoordinateCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(myVertexNormalID + 1);
            theParent.print(SceneWriter.DELIMITER);

            theParent.println();
        }
        theParent.println();

        theParent.bumpVertexCounter();
        theParent.bumpVertexCounter();
        theParent.bumpTextureCoordinateCounter();
        theParent.bumpTextureCoordinateCounter();
        theParent.bumpNormalVertexCounter();

        /* save vertexcolors as texture */
        if (theParent.saveVertexColorsAsTexture() && myUniqueObjectID != Gestalt.UNDEFINED) {
            writeVertexColorTexture(myQuadsFragments, theParent.getTextureDirectory(), myUniqueObjectID);
        }
    }

    private static void writeVertexColorTexture(final QuadFragment[] theQuadsFragments,
                                                final String theDirectoryName,
                                                final int theID) {
        /* create bitmap */
        ByteBitmap myBitmap = ByteBitmap.getDefaultImageBitmap(theQuadsFragments.length, 2);

        for (int i = 0; i < theQuadsFragments.length; i++) {
            final QuadFragment myQuadFragment = theQuadsFragments[i];
            myBitmap.setPixel(i, 0, myQuadFragment.colorA);
            myBitmap.setPixel(i, 1, myQuadFragment.colorB);
        }

        String myFileName = theDirectoryName + "/" + ourObjectName + "vertexcolors_" + theID + ".png";
        ImageUtil.save(myBitmap, myFileName, Gestalt.IMAGE_FILEFORMAT_PNG);
    }
}
