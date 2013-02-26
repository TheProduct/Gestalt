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


package gestalt.util;


import gestalt.render.controller.Camera;

import mathematik.TransformMatrix4f;
import mathematik.Vector2i;
import mathematik.Vector3f;


public class CameraUtil {

    /* camera */

    public static final void stickToCameraSurface(final Camera theCamera,
                                                  final Vector3f thePosition,
                                                  final float theSurfaceX,
                                                  final float theSurfaceY) {
        /* get our destination translation */
        final float myZ = ( -theCamera.viewport().height * 0.5f) /
                          (float) Math.tan(Math.toRadians(theCamera.fovy * 0.5f));
        thePosition.set(theSurfaceX, theSurfaceY, myZ);

        /* transform destination by camera matrix */
        theCamera.updateRotationMatrix();
        theCamera.getRotationMatrix().transform(thePosition);
        thePosition.add(theCamera.position());

        /* correct frustum offset */
        final float myScreenWorldPixelRatio = 0.5f / (float) Math.tan(Math.toRadians(theCamera.fovy * 0.5f));

        Vector3f myYOffset = new Vector3f();
        myYOffset.normalize(theCamera.getUp());
        myYOffset.scale(theCamera.frustumoffset.y * theCamera.viewport().height * myScreenWorldPixelRatio);
        thePosition.add(myYOffset);

        Vector3f myXOffset = new Vector3f();
        myXOffset.normalize(theCamera.getSide());
        myXOffset.scale(theCamera.frustumoffset.x * theCamera.viewport().width * myScreenWorldPixelRatio);
        thePosition.add(myXOffset);
    }


    public static final void stickToCameraSurface(final Camera theCamera,
                                                  final Vector3f thePosition,
                                                  final Vector2i theSurface) {
        stickToCameraSurface(theCamera,
                             thePosition,
                             theSurface.x,
                             theSurface.y);
    }


    public static final void stickToCameraSurface(final Camera theCamera,
                                                  final TransformMatrix4f theMatrix,
                                                  final int theSurfaceX,
                                                  final int theSurfaceY) {
        /* get our destination translation */
        final float myZ = ( -theCamera.viewport().height * 0.5f) /
                          (float) Math.tan(Math.toRadians(theCamera.fovy * 0.5f));
        theMatrix.translation.set(theSurfaceX, theSurfaceY, myZ);

        /* transform destination by camera matrix */
        theCamera.getRotationMatrix().transform(theMatrix.translation);
        theMatrix.translation.add(theCamera.position());

        /* remove camera rotation effect from object */
        theMatrix.rotation.set(theCamera.getInversRotationMatrix());

        /* correct frustum offset */
        final float myScreenWorldPixelRatio = 0.5f / (float) Math.tan(Math.toRadians(theCamera.fovy * 0.5f));

        Vector3f myYOffset = new Vector3f();
        myYOffset.normalize(theCamera.getUp());
        myYOffset.scale(theCamera.frustumoffset.y * theCamera.viewport().height * myScreenWorldPixelRatio);
        theMatrix.translation.add(myYOffset);

        Vector3f myXOffset = new Vector3f();
        myXOffset.normalize(theCamera.getSide());
        myXOffset.scale(theCamera.frustumoffset.x * theCamera.viewport().width * myScreenWorldPixelRatio);
        theMatrix.translation.add(myXOffset);
    }


    public static final void toCameraSpace(Camera theCamera, Vector3f theLocalResult) {
        theLocalResult.sub(theCamera.position());
        theCamera.getRotationMatrix().transform(theLocalResult);
    }
}
