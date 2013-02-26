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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.controller.Camera;
import gestalt.shape.DrawableFactory;
import gestalt.shape.Line;
import gestalt.shape.Quad;

import mathematik.Intersection;
import mathematik.Plane3f;
import mathematik.Ray3f;
import mathematik.Vector2i;
import mathematik.Vector3f;


public class CameraFrustumView
    implements Drawable {

    private final Vector2i _myStart;

    private final Vector2i _myEnd;

    private final Quad _myViewPlane;

    private final Line _myFrustumLines;

    public CameraFrustumView(DrawableFactory drawablefactory) {
        /* frustum size */
        _myStart = new Vector2i();
        _myEnd = new Vector2i();

        /* frustum pyramid */
        _myFrustumLines = drawablefactory.line();
        _myFrustumLines.material().color4f().set(1);
        _myFrustumLines.setPrimitive(Gestalt.LINE_PRIMITIVE_TYPE_LINES);
        _myFrustumLines.points = new Vector3f[] {
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f(),
                                 new Vector3f()
        };

        /* viewplane */
        _myViewPlane = drawablefactory.quad();
        _myViewPlane.material().transparent = false;
        _myViewPlane.material().depthtest = true;
        _myViewPlane.material().depthmask = false;
        _myViewPlane.material().wireframe = true;
        _myViewPlane.material().color4f().set(1, 0.5f, 0);
    }


    public void update(Camera theCamera) {
        _myStart.set(theCamera.viewport().width / -2, theCamera.viewport().height / -2);
        _myEnd.set(theCamera.viewport().width / 2, theCamera.viewport().height / 2);

        /* create frustum rays */
        CameraUtil.stickToCameraSurface(theCamera,
                                        _myViewPlane.a().position,
                                        _myStart.x,
                                        _myStart.y);
        CameraUtil.stickToCameraSurface(theCamera,
                                        _myViewPlane.b().position,
                                        _myEnd.x,
                                        _myStart.y);
        CameraUtil.stickToCameraSurface(theCamera,
                                        _myViewPlane.c().position,
                                        _myEnd.x,
                                        _myEnd.y);
        CameraUtil.stickToCameraSurface(theCamera,
                                        _myViewPlane.d().position,
                                        _myStart.x,
                                        _myEnd.y);

        /* create view plane */
        Vector3f myDirectionA = new Vector3f();
        myDirectionA.sub(_myViewPlane.c().position, _myViewPlane.a().position);
        Vector3f myDirectionB = new Vector3f();
        myDirectionB.sub(_myViewPlane.b().position, _myViewPlane.a().position);

        /* create near plane */
        {
            Vector3f myNearOrigin = new Vector3f();
            myNearOrigin.set(theCamera.getForward());
            myNearOrigin.scale(theCamera.nearclipping);
            myNearOrigin.add(theCamera.position());

            Plane3f myNearPlane = new Plane3f(myNearOrigin, myDirectionA, myDirectionB);

            /* create frustum ray */
            for (int i = 0; i < 4; i++) {
                Vector3f myRayDirection = new Vector3f();
                myRayDirection.sub(_myViewPlane.vertices()[i].position, theCamera.position());
                Ray3f myFrustumRay = new Ray3f(theCamera.position(), myRayDirection);
                Intersection.intersectLinePlane(myFrustumRay, myNearPlane, _myFrustumLines.points[i]);
            }
        }
        /* create far plane */
        {
            Vector3f myFarOrigin = new Vector3f();
            myFarOrigin.set(theCamera.getForward());
            myFarOrigin.scale(theCamera.farclipping);
            myFarOrigin.add(theCamera.position());

            Plane3f myNearPlane = new Plane3f(myFarOrigin, myDirectionA, myDirectionB);

            /* create frustum ray */
            for (int i = 0; i < 4; i++) {
                Vector3f myRayDirection = new Vector3f();
                myRayDirection.sub(_myViewPlane.vertices()[i].position, theCamera.position());
                Ray3f myFrustumRay = new Ray3f(theCamera.position(), myRayDirection);
                Intersection.intersectLinePlane(myFrustumRay, myNearPlane, _myFrustumLines.points[i + 4]);
            }
        }

        /* connect near with far plane */
        _myFrustumLines.points[8].set(_myFrustumLines.points[0]);
        _myFrustumLines.points[9].set(_myFrustumLines.points[0 + 4]);
        _myFrustumLines.points[10].set(_myFrustumLines.points[1]);
        _myFrustumLines.points[11].set(_myFrustumLines.points[1 + 4]);
        _myFrustumLines.points[12].set(_myFrustumLines.points[2]);
        _myFrustumLines.points[13].set(_myFrustumLines.points[2 + 4]);
        _myFrustumLines.points[14].set(_myFrustumLines.points[3]);
        _myFrustumLines.points[15].set(_myFrustumLines.points[3 + 4]);

        /* connect plane rows with columns */
        _myFrustumLines.points[16].set(_myFrustumLines.points[0]);
        _myFrustumLines.points[17].set(_myFrustumLines.points[3]);
        _myFrustumLines.points[18].set(_myFrustumLines.points[1]);
        _myFrustumLines.points[19].set(_myFrustumLines.points[2]);
        _myFrustumLines.points[20].set(_myFrustumLines.points[0 + 4]);
        _myFrustumLines.points[21].set(_myFrustumLines.points[3 + 4]);
        _myFrustumLines.points[22].set(_myFrustumLines.points[1 + 4]);
        _myFrustumLines.points[23].set(_myFrustumLines.points[2 + 4]);
    }


    /* -> drawable */

    public void draw(GLContext theRenderContext) {
        _myFrustumLines.draw(theRenderContext);
        _myViewPlane.draw(theRenderContext);
    }


    public void add(Drawable theDrawable) {
    }


    public boolean isActive() {
        return true;
    }


    public float getSortValue() {
        return 0.0F;
    }


    public void setSortValue(float theSortValue) {
    }


    public float[] getSortData() {
        return null;
    }


    public boolean isSortable() {
        return false;
    }
}
