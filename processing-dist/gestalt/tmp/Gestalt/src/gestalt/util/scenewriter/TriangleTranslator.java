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
import gestalt.render.Drawable;
import gestalt.shape.Triangle;
import gestalt.shape.Triangles;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class TriangleTranslator
        implements DrawableOBJTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Triangles;
    }

    public void parse(SceneWriter theParent, Drawable theDrawable) {
        final Triangles theMesh = (Triangles)theDrawable;

        /* write unique header */
        int myUniqueID;// = Gestalt.UNDEFINED;
        if (!SceneWriter.IGNORE_OBJECTS) {
            myUniqueID = theParent.bumpUniqueObjectID();
            theParent.writeGroupTag("triangles" + myUniqueID);
        }

        /* apply transform */
        final TransformMatrix4f myTransform = mathematik.Util.getTranslateRotationTransform(theMesh.getTransformMode(),
                                                                                            theMesh.transform(),
                                                                                            theMesh.rotation(),
                                                                                            theMesh.scale());
        /* vertices */
        for (int i = 0; i < theMesh.triangles().size(); i++) {
            Triangle myTriangle = theMesh.triangles().get(i);
            {
                Vector3f myPosition = new Vector3f(myTriangle.a().position);
                myTransform.transform(myPosition);
                theParent.writeVertex(myPosition);
            }
            {
                Vector3f myPosition = new Vector3f(myTriangle.b().position);
                myTransform.transform(myPosition);
                theParent.writeVertex(myPosition);
            }
            {
                Vector3f myPosition = new Vector3f(myTriangle.c().position);
                myTransform.transform(myPosition);
                theParent.writeVertex(myPosition);
            }
        }
        theParent.println();

        /* normals */
        for (int i = 0; i < theMesh.triangles().size(); i++) {
            Triangle myTriangle = theMesh.triangles().get(i);
            {
                Vector3f myNormal = new Vector3f(myTriangle.a().normal);
                myTransform.transform(myNormal);
                theParent.writeNormal(myNormal);
            }
            {
                Vector3f myNormal = new Vector3f(myTriangle.b().normal);
                myTransform.transform(myNormal);
                theParent.writeNormal(myNormal);
            }
            {
                Vector3f myNormal = new Vector3f(myTriangle.c().normal);
                myTransform.transform(myNormal);
                theParent.writeNormal(myNormal);
            }
        }
        theParent.println();

        /* elements */
        for (int i = 0; i < theMesh.triangles().size(); i++) {
            theParent.print(SceneWriter.FACE);
            theParent.print(SceneWriter.DELIMITER);
            for (int j = 0; j < 3; j++) {
                theParent.print(theParent.bumpVertexCounter());
                theParent.print(SceneWriter.FACE_DELIMITER);
//                theParent.writer().print(theParent.bumpTextureCoordinateCounter());
//                theParent.writer().print(SceneWriter.FACE_DELIMITER);
                theParent.print(theParent.bumpNormalVertexCounter());
                theParent.print(SceneWriter.DELIMITER);
            }
            theParent.println();
        }
        theParent.println();
    }
}
