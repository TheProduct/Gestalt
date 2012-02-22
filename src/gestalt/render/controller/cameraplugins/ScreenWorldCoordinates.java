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

/*
 * transforms screen position to world position and back
 */

package gestalt.render.controller.cameraplugins;

import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.controller.Origin;

import mathematik.Intersection;
import mathematik.Plane3f;
import mathematik.Ray3f;
import mathematik.Vector3f;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class ScreenWorldCoordinates
        implements Drawable,
                   CameraPlugin {

    private final double[] _myProjectionMatrix;

    private final double[] _myModelViewMatrix;

    private final int[] _myViewPort;

    public boolean initalized = false;

    /**
     * @todo it is not cool to cache GLU but we ll need to so as long as our own
     * implementation of un/project does not work
     */
    private GLU glu;

    public boolean enable;

    private Origin _myOrigin;

    public ScreenWorldCoordinates() {
        this(new Origin());
    }

    public ScreenWorldCoordinates(Origin theOrigin) {
        _myOrigin = theOrigin;
        _myProjectionMatrix = new double[16];
        _myModelViewMatrix = new double[16];
        _myViewPort = new int[4];
        enable = true;
    }

    public void draw(GLContext theRenderContext) {

        if (!initalized) {
            initalized = true;
        }

        final GL gl = theRenderContext.gl;
        glu = theRenderContext.glu;
        /** @todo JSR-231 -- added 0 */
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, _myProjectionMatrix, 0);
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, _myModelViewMatrix, 0);
        gl.glGetIntegerv(GL.GL_VIEWPORT, _myViewPort, 0);

        /**
         * @todo we shift the center to the middle. check if this is correct.
         * also rotating the origin does not work very well.
         */
        _myViewPort[0] = _myViewPort[2] / -2;
        _myViewPort[1] = _myViewPort[3] / -2;
        _myViewPort[0] = (int)(_myViewPort[0] - _myOrigin.position.x);
        _myViewPort[1] = (int)(_myViewPort[1] - _myOrigin.position.y);
    }

    public void begin(GLContext theRenderContext) {
        draw(theRenderContext);
    }

    public void end(GLContext theRenderContext) {
    }

    public boolean isActive() {
        return enable;
    }

    public boolean screenToWorldPosition(final int theScreenX,
                                         final int theScreenY,
                                         final Plane3f thePlane,
                                         final Vector3f theResult) {

        /** @todo optimize here */
        Ray3f myRay = new Ray3f();
        boolean myResult = screenPositionToWorldRay(theScreenX, theScreenY, myRay);

        if (!myResult) {
            return false;
        }

        myResult = Intersection.intersectRayPlane(myRay, thePlane, theResult);

        return myResult;
    }

    public boolean worldToScreenPosition(final Vector3f theWorldPosition,
                                         final Vector3f theResult) {
        return project(theWorldPosition.x, theWorldPosition.y,
                       theWorldPosition.z, _myModelViewMatrix, _myProjectionMatrix,
                       _myViewPort, theResult);
    }

    public boolean screenPositionToWorldRay(int theScreenPositionX,
                                            int theScreenPositionY, Ray3f theEyeRay) {
        /** @todo optimize here */
        Vector3f myResultA = new Vector3f();
        boolean myResult = unproject(theScreenPositionX, theScreenPositionY, 2,
                                     _myModelViewMatrix, _myProjectionMatrix, _myViewPort, myResultA);
        if (!myResult) {
            return false;
        }

        myResult = unproject(theScreenPositionX, theScreenPositionY, 1.5,
                             _myModelViewMatrix, _myProjectionMatrix, _myViewPort,
                             theEyeRay.origin);
        if (!myResult) {
            return false;
        }

        theEyeRay.direction.sub(theEyeRay.origin, myResultA);
        theEyeRay.direction.normalize();

        return true;
    }

    private boolean unproject(int screenX, int screenY, double screenZ,
                              double[] model, double[] projection, int[] viewPort,
                              Vector3f theResult) {

        /** @todo optimize here */
        double[] result = new double[3];

        boolean myResult = false;
        if (glu != null) {
            /** @todo JSR-231 -- added 0 */
            myResult = glu.gluUnProject(screenX, screenY, screenZ, model, 0,
                                        projection, 0, viewPort, 0, result, 0);
        }
        theResult.set(result[0], result[1], result[2]);
        return myResult;

        /** @todo there is a bug in this code OR even worse in the matrix class */
        // Vector4f myResult = new Vector4f();
        // myResult.x = (screenX - viewPort[0]) * 2 / (float) viewPort[2] -
        // 1.0f;
        // myResult.y = (screenY - viewPort[1]) * 2 / (float) viewPort[3] -
        // 1.0f;
        // myResult.z = 2 * screenZ - 1.0f;
        // myResult.w = 1.0f;
        //
        // Matrix4f myProjectionMatrix = new Matrix4f(projection);
        // Matrix4f myModelViewMatrix = new Matrix4f(model);
        // Matrix4f A = new Matrix4f();
        //
        // A.multiply(myProjectionMatrix, myModelViewMatrix);
        // A.invert();
        // A.transform(myResult);
        //
        // if (myResult.w == 0.0f) {
        // return false;
        // }
        //
        // theResult.set(myResult.x / myResult.w, myResult.y / myResult.w,
        // myResult.z / myResult.w);
        // return true;
    }

    private boolean project(final float worldX, final float worldY,
                            final float worldZ, final double[] model,
                            final double[] projection, final int[] viewPort,
                            final Vector3f theResult) {

        /** @todo optimize here */
        double[] result = new double[3];

        boolean myResult = false;
        if (glu != null) {
            /** @todo JSR-231 -- added 0 */
            myResult = glu.gluProject(worldX, worldY, worldZ, model, 0,
                                      projection, 0, viewPort, 0, result, 0);
        }
        theResult.set((float)result[0], (float)result[1], 0);
        // theResult.set( (float) resultX[0], (float) resultY[0], (float)
        // resultZ[0]);
        return myResult;

        // /** @todo optimize here */
        // Vector4f myResult = new Vector4f(worldX, worldY, worldZ, 1);
        // Matrix4f myProjectionMatrix = new Matrix4f(projection);
        // Matrix4f myModelViewMatrix = new Matrix4f(model);
        //
        // myModelViewMatrix.transform(myResult);
        // myProjectionMatrix.transform(myResult);
        //
        // if (myResult.w == 0.0) {
        // return false;
        // }
        //
        // myResult.x /= myResult.w;
        // myResult.y /= myResult.w;
        // myResult.z /= myResult.w;
        //
        // theResult.set(viewPort[0] + (1 + myResult.x) * viewPort[2] / 2,
        // viewPort[1] + (1 + myResult.y) * viewPort[3] / 2,
        // (1 + myResult.z) / 2);
        // return true;
    }


    /* unused methods from implemented interface */
    public void add(Drawable theDrawable) {
    }

    public float getSortValue() {
        return 0.0f;
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
