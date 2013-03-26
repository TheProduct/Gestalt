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
import gestalt.shape.Plane;

import mathematik.TransformMatrix4f;
import mathematik.Vector2f;
import mathematik.Vector3f;


public class PlaneTranslator
        implements DrawableOBJTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Plane;
    }

    public void parse(SceneWriter theParent,
                      Drawable theDrawable) {
        final Plane myPlane = (Plane) theDrawable;

        /* write unique header */
        if (!SceneWriter.IGNORE_OBJECTS) {
            theParent.writeGroupTag("plane" + theParent.bumpUniqueObjectID());
        }

        /* write material */
        parseMaterial(theParent, myPlane);

        /* get transform */
        final TransformMatrix4f myTransform = gestalt.util.Util.getTranslateRotationTransform(myPlane.getTransformMode(),
                                                                                              myPlane.transform(),
                                                                                              myPlane.rotation(),
                                                                                              myPlane.scale());
        final TransformMatrix4f myNormalTransform = gestalt.util.Util.getRotationTransform(myPlane.getTransformMode(),
                                                                                           myPlane.transform(),
                                                                                           myPlane.rotation(),
                                                                                           myPlane.scale());

        /* get four points */
        float myX = 0;
        float myY = 0;
        switch (myPlane.origin()) {
            case Gestalt.SHAPE_ORIGIN_BOTTOM_RIGHT:
                myX = -1;
                break;
            case Gestalt.SHAPE_ORIGIN_TOP_LEFT:
                myY = -1;
                break;
            case Gestalt.SHAPE_ORIGIN_TOP_RIGHT:
                myX = -1;
                myY = -1;
                break;
            case Gestalt.SHAPE_ORIGIN_CENTERED:
                myX = -0.5f;
                myY = -0.5f;
                break;
            case Gestalt.SHAPE_ORIGIN_BOTTOM_LEFT:
                break;
        }

        final Vector3f myNormal = new Vector3f(0, 0, 1);

        final Vector3f myA = new Vector3f(myX, myY);
        final Vector3f myB = new Vector3f(1 + myX, myY);
        final Vector3f myC = new Vector3f(1 + myX, 1 + myY);
        final Vector3f myD = new Vector3f(myX, 1 + myY);

        final Vector2f myATex = new Vector2f(0, 0);
        final Vector2f myBTex = new Vector2f(1, 0);
        final Vector2f myCTex = new Vector2f(1, 1);
        final Vector2f myDTex = new Vector2f(0, 1);

        /* transform texture coords */

        /*   rotation */

        /*   scale */
        if (myPlane.material().texture() != null) {
            Vector2f myTexScale = new Vector2f(myPlane.material().texture().scale().x,
                                               myPlane.material().texture().scale().y);
            myTexScale.scale(myPlane.material().texture().nonpoweroftwotexturerescale());
            myATex.scale(myTexScale);
            myBTex.scale(myTexScale);
            myCTex.scale(myTexScale);
            myDTex.scale(myTexScale);

            /*   translation */
            Vector2f myTexPosition = myPlane.material().texture().position();
            myATex.add(myTexPosition);
            myBTex.add(myTexPosition);
            myCTex.add(myTexPosition);
            myDTex.add(myTexPosition);
        }

        /* transform points */
        myTransform.transform(myA);
        myTransform.transform(myB);
        myTransform.transform(myC);
        myTransform.transform(myD);
        myNormalTransform.transform(myNormal);

        /* write points */
        theParent.writeVertex(myA);
        theParent.writeVertex(myB);
        theParent.writeVertex(myC);
        theParent.writeVertex(myD);
        theParent.writeTextureCoordinates(myATex);
        theParent.writeTextureCoordinates(myBTex);
        theParent.writeTextureCoordinates(myCTex);
        theParent.writeTextureCoordinates(myDTex);
        theParent.writeNormal(myNormal);

        /* write face */
        theParent.print(SceneWriter.FACE);
        theParent.print(SceneWriter.DELIMITER);

        int myVertexNormalID = theParent.bumpNormalVertexCounter();

        for (int i = 0; i < 4; i++) {
            theParent.print(theParent.bumpVertexCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(theParent.bumpTextureCoordinateCounter());
            theParent.print(SceneWriter.FACE_DELIMITER);
            theParent.print(myVertexNormalID);
            theParent.print(SceneWriter.DELIMITER);
        }
        theParent.println();
    }

    protected void parseMaterial(SceneWriter theParent, Plane myPlane) {
        theParent.parseMaterial(myPlane.material());
    }
}
