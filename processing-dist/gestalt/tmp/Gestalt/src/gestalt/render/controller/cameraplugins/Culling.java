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


package gestalt.render.controller.cameraplugins;

import gestalt.context.GLContext;
import gestalt.render.controller.cameraplugins.CameraPlugin;

import mathematik.Vector3f;

import javax.media.opengl.GL;


/**
 * spheres and points can be tested against the viewing frustum.
 */
/** @todo put copyright notice here... */
public class Culling
        implements CameraPlugin {

    public boolean FAST = true;

    private float[][] _myFrustum;

    private float[] _myClipPlane;

    private float[] _myProjectionMatrix;

    private float[] _myModelviewMatrix;

    public static final int RIGHT = 0;

    public static final int LEFT = 1;

    public static final int BOTTOM = 2;

    public static final int TOP = 3;

    public static final int FAR = 4;

    public static final int NEAR = 5;

    public Culling() {
        _myFrustum = new float[6][4];
        _myClipPlane = new float[16];
        _myProjectionMatrix = new float[16];
        _myModelviewMatrix = new float[16];
    }

    public void begin(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        if (FAST) {
            updateFrustumFaster(gl);
        } else {
            updateFrustum(gl);
        }
    }

    public void end(GLContext theRenderContext) {
    }

    public boolean sphereInFrustum(Vector3f thePosition, float theRadius) {
        for (int i = 0; i < 6; i++) {
            // If the point is outside of the plane then its not in the viewing volume.
            if (_myFrustum[i][0] * thePosition.x
                    + _myFrustum[i][1] * thePosition.y
                    + _myFrustum[i][2] * thePosition.z
                    + _myFrustum[i][3] <= -theRadius) {
                return false;
            }
        }
        return true;
    }

    public boolean pointInFrustum(Vector3f thePosition) {
        for (int i = 0; i < 6; i++) {
            if (_myFrustum[i][0] * thePosition.x
                    + _myFrustum[i][1] * thePosition.y
                    + _myFrustum[i][2] * thePosition.z
                    + _myFrustum[i][3] <= 0) {
                return false;
            }
        }
        return true;
    }

    public int pointInFrustumSide(Vector3f thePosition) {
        for (int i = 0; i < 6; i++) {
            if (frustum()[i][0] * thePosition.x
                    + frustum()[i][1] * thePosition.y
                    + frustum()[i][2] * thePosition.z
                    + frustum()[i][3] <= 0) {
                return i;
            }
        }
        return -1;
    }

    public int sphereInFrustumSide(Vector3f thePosition, float theRadius) {
        for (int i = 0; i < 6; i++) {
            if (frustum()[i][0] * thePosition.x
                    + frustum()[i][1] * thePosition.y
                    + frustum()[i][2] * thePosition.z
                    + frustum()[i][3] <= -theRadius) {
                return i;
            }
        }
        return -1;
    }


    /* culling algorithm */
    // I found this code here: http://www.markmorley.com/opengl/frustumculling.html
    // and decided to make it part of
    // the camera class just in case I might want to rotate
    // and translate the projection matrix. This code will
    // make sure that the Frustum is updated correctly but
    // this member is computational expensive with:
    // 82 muliplications, 72 additions, 24 divisions, and
    // 12 subtractions for a total of 190 operations. Ouch!
    private void updateFrustum(GL gl) {

        /* Get the current PROJECTION matrix from OpenGL */
        /** @todo JSR-231 -- added 0 */
        gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, _myProjectionMatrix, 0);

        /* Get the current MODELVIEW matrix from OpenGL */
        /** @todo JSR-231 -- added 0 */
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, _myModelviewMatrix, 0);

        /* Combine the two matrices (multiply projection by modelview) */
        _myClipPlane[0] = _myModelviewMatrix[0] * _myProjectionMatrix[0]
                + _myModelviewMatrix[1] * _myProjectionMatrix[4]
                + _myModelviewMatrix[2] * _myProjectionMatrix[8]
                + _myModelviewMatrix[3] * _myProjectionMatrix[12];
        _myClipPlane[1] = _myModelviewMatrix[0] * _myProjectionMatrix[1]
                + _myModelviewMatrix[1] * _myProjectionMatrix[5]
                + _myModelviewMatrix[2] * _myProjectionMatrix[9]
                + _myModelviewMatrix[3] * _myProjectionMatrix[13];
        _myClipPlane[2] = _myModelviewMatrix[0] * _myProjectionMatrix[2]
                + _myModelviewMatrix[1] * _myProjectionMatrix[6]
                + _myModelviewMatrix[2] * _myProjectionMatrix[10]
                + _myModelviewMatrix[3] * _myProjectionMatrix[14];
        _myClipPlane[3] = _myModelviewMatrix[0] * _myProjectionMatrix[3]
                + _myModelviewMatrix[1] * _myProjectionMatrix[7]
                + _myModelviewMatrix[2] * _myProjectionMatrix[11]
                + _myModelviewMatrix[3] * _myProjectionMatrix[15];

        _myClipPlane[4] = _myModelviewMatrix[4] * _myProjectionMatrix[0]
                + _myModelviewMatrix[5] * _myProjectionMatrix[4]
                + _myModelviewMatrix[6] * _myProjectionMatrix[8]
                + _myModelviewMatrix[7] * _myProjectionMatrix[12];
        _myClipPlane[5] = _myModelviewMatrix[4] * _myProjectionMatrix[1]
                + _myModelviewMatrix[5] * _myProjectionMatrix[5]
                + _myModelviewMatrix[6] * _myProjectionMatrix[9]
                + _myModelviewMatrix[7] * _myProjectionMatrix[13];
        _myClipPlane[6] = _myModelviewMatrix[4] * _myProjectionMatrix[2]
                + _myModelviewMatrix[5] * _myProjectionMatrix[6]
                + _myModelviewMatrix[6] * _myProjectionMatrix[10]
                + _myModelviewMatrix[7] * _myProjectionMatrix[14];
        _myClipPlane[7] = _myModelviewMatrix[4] * _myProjectionMatrix[3]
                + _myModelviewMatrix[5] * _myProjectionMatrix[7]
                + _myModelviewMatrix[6] * _myProjectionMatrix[11]
                + _myModelviewMatrix[7] * _myProjectionMatrix[15];

        _myClipPlane[8] = _myModelviewMatrix[8] * _myProjectionMatrix[0]
                + _myModelviewMatrix[9] * _myProjectionMatrix[4]
                + _myModelviewMatrix[10] * _myProjectionMatrix[8]
                + _myModelviewMatrix[11] * _myProjectionMatrix[12];
        _myClipPlane[9] = _myModelviewMatrix[8] * _myProjectionMatrix[1]
                + _myModelviewMatrix[9] * _myProjectionMatrix[5]
                + _myModelviewMatrix[10] * _myProjectionMatrix[9]
                + _myModelviewMatrix[11] * _myProjectionMatrix[13];
        _myClipPlane[10] = _myModelviewMatrix[8] * _myProjectionMatrix[2]
                + _myModelviewMatrix[9] * _myProjectionMatrix[6]
                + _myModelviewMatrix[10] * _myProjectionMatrix[10]
                + _myModelviewMatrix[11] * _myProjectionMatrix[14];
        _myClipPlane[11] = _myModelviewMatrix[8] * _myProjectionMatrix[3]
                + _myModelviewMatrix[9] * _myProjectionMatrix[7]
                + _myModelviewMatrix[10] * _myProjectionMatrix[11]
                + _myModelviewMatrix[11] * _myProjectionMatrix[15];

        _myClipPlane[12] = _myModelviewMatrix[12] * _myProjectionMatrix[0]
                + _myModelviewMatrix[13] * _myProjectionMatrix[4]
                + _myModelviewMatrix[14] * _myProjectionMatrix[8]
                + _myModelviewMatrix[15] * _myProjectionMatrix[12];
        _myClipPlane[13] = _myModelviewMatrix[12] * _myProjectionMatrix[1]
                + _myModelviewMatrix[13] * _myProjectionMatrix[5]
                + _myModelviewMatrix[14] * _myProjectionMatrix[9]
                + _myModelviewMatrix[15] * _myProjectionMatrix[13];
        _myClipPlane[14] = _myModelviewMatrix[12] * _myProjectionMatrix[2]
                + _myModelviewMatrix[13] * _myProjectionMatrix[6]
                + _myModelviewMatrix[14] * _myProjectionMatrix[10]
                + _myModelviewMatrix[15] * _myProjectionMatrix[14];
        _myClipPlane[15] = _myModelviewMatrix[12] * _myProjectionMatrix[3]
                + _myModelviewMatrix[13] * _myProjectionMatrix[7]
                + _myModelviewMatrix[14] * _myProjectionMatrix[11]
                + _myModelviewMatrix[15] * _myProjectionMatrix[15];

        /* Extract the numbers for the RIGHT plane */
        _myFrustum[0][0] = _myClipPlane[3] - _myClipPlane[0];
        _myFrustum[0][1] = _myClipPlane[7] - _myClipPlane[4];
        _myFrustum[0][2] = _myClipPlane[11] - _myClipPlane[8];
        _myFrustum[0][3] = _myClipPlane[15] - _myClipPlane[12];

        /* Normalize the result */
        float t = (float)(Math.sqrt(_myFrustum[0][0] * _myFrustum[0][0]
                + _myFrustum[0][1] * _myFrustum[0][1]
                + _myFrustum[0][2] * _myFrustum[0][2]));
        _myFrustum[0][0] /= t;
        _myFrustum[0][1] /= t;
        _myFrustum[0][2] /= t;
        _myFrustum[0][3] /= t;

        /* Extract the numbers for the LEFT plane */
        _myFrustum[1][0] = _myClipPlane[3] + _myClipPlane[0];
        _myFrustum[1][1] = _myClipPlane[7] + _myClipPlane[4];
        _myFrustum[1][2] = _myClipPlane[11] + _myClipPlane[8];
        _myFrustum[1][3] = _myClipPlane[15] + _myClipPlane[12];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[1][0] * _myFrustum[1][0]
                + _myFrustum[1][1] * _myFrustum[1][1]
                + _myFrustum[1][2] * _myFrustum[1][2]));
        _myFrustum[1][0] /= t;
        _myFrustum[1][1] /= t;
        _myFrustum[1][2] /= t;
        _myFrustum[1][3] /= t;

        /* Extract the BOTTOM plane */
        _myFrustum[2][0] = _myClipPlane[3] + _myClipPlane[1];
        _myFrustum[2][1] = _myClipPlane[7] + _myClipPlane[5];
        _myFrustum[2][2] = _myClipPlane[11] + _myClipPlane[9];
        _myFrustum[2][3] = _myClipPlane[15] + _myClipPlane[13];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[2][0] * _myFrustum[2][0]
                + _myFrustum[2][1] * _myFrustum[2][1]
                + _myFrustum[2][2] * _myFrustum[2][2]));
        _myFrustum[2][0] /= t;
        _myFrustum[2][1] /= t;
        _myFrustum[2][2] /= t;
        _myFrustum[2][3] /= t;

        /* Extract the TOP plane */
        _myFrustum[3][0] = _myClipPlane[3] - _myClipPlane[1];
        _myFrustum[3][1] = _myClipPlane[7] - _myClipPlane[5];
        _myFrustum[3][2] = _myClipPlane[11] - _myClipPlane[9];
        _myFrustum[3][3] = _myClipPlane[15] - _myClipPlane[13];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[3][0] * _myFrustum[3][0]
                + _myFrustum[3][1] * _myFrustum[3][1]
                + _myFrustum[3][2] * _myFrustum[3][2]));
        _myFrustum[3][0] /= t;
        _myFrustum[3][1] /= t;
        _myFrustum[3][2] /= t;
        _myFrustum[3][3] /= t;

        /* Extract the FAR plane */
        _myFrustum[4][0] = _myClipPlane[3] - _myClipPlane[2];
        _myFrustum[4][1] = _myClipPlane[7] - _myClipPlane[6];
        _myFrustum[4][2] = _myClipPlane[11] - _myClipPlane[10];
        _myFrustum[4][3] = _myClipPlane[15] - _myClipPlane[14];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[4][0] * _myFrustum[4][0]
                + _myFrustum[4][1] * _myFrustum[4][1]
                + _myFrustum[4][2] * _myFrustum[4][2]));
        _myFrustum[4][0] /= t;
        _myFrustum[4][1] /= t;
        _myFrustum[4][2] /= t;
        _myFrustum[4][3] /= t;

        /* Extract the NEAR plane */
        _myFrustum[5][0] = _myClipPlane[3] + _myClipPlane[2];
        _myFrustum[5][1] = _myClipPlane[7] + _myClipPlane[6];
        _myFrustum[5][2] = _myClipPlane[11] + _myClipPlane[10];
        _myFrustum[5][3] = _myClipPlane[15] + _myClipPlane[14];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[5][0] * _myFrustum[5][0]
                + _myFrustum[5][1] * _myFrustum[5][1]
                + _myFrustum[5][2] * _myFrustum[5][2]));
        _myFrustum[5][0] /= t;
        _myFrustum[5][1] /= t;
        _myFrustum[5][2] /= t;
        _myFrustum[5][3] /= t;
    }

    // This is the much faster version of the above member
    // function, however the speed increase is not gained
    // without a cost. If you rotate or translate the projection
    // matrix then this member will not work correctly. That is acceptable
    // in my book considering I very rarely do such a thing.
    // This function has far fewer operations in it and I
    // shaved off 2 square root functions by passing in the
    // near and far values. This member has:
    // 38 muliplications, 28 additions, 24 divisions, and
    // 12 subtractions for a total of 102 operations. Still hurts
    // but at least it is decent now. In practice this will
    // run about 2 times faster than the above function.
    private void updateFrustumFaster(GL gl) {
        /* Get the current PROJECTION matrix from OpenGL */
        /** @todo JSR-231 -- added 0 */
        gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, _myProjectionMatrix, 0);

        /* Get the current MODELVIEW matrix from OpenGL */
        /** @todo JSR-231 -- added 0 */
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, _myModelviewMatrix, 0);

        /* Combine the two matrices (multiply projection by modelview)
         * but keep in mind this function will only work if you do NOT
         * rotate or translate your projection matrix */
        _myClipPlane[0] = _myModelviewMatrix[0] * _myProjectionMatrix[0];
        _myClipPlane[1] = _myModelviewMatrix[1] * _myProjectionMatrix[5];
        _myClipPlane[2] = _myModelviewMatrix[2] * _myProjectionMatrix[10]
                + _myModelviewMatrix[3] * _myProjectionMatrix[14];
        _myClipPlane[3] = _myModelviewMatrix[2] * _myProjectionMatrix[11];

        _myClipPlane[4] = _myModelviewMatrix[4] * _myProjectionMatrix[0];
        _myClipPlane[5] = _myModelviewMatrix[5] * _myProjectionMatrix[5];
        _myClipPlane[6] = _myModelviewMatrix[6] * _myProjectionMatrix[10]
                + _myModelviewMatrix[7] * _myProjectionMatrix[14];
        _myClipPlane[7] = _myModelviewMatrix[6] * _myProjectionMatrix[11];

        _myClipPlane[8] = _myModelviewMatrix[8] * _myProjectionMatrix[0];
        _myClipPlane[9] = _myModelviewMatrix[9] * _myProjectionMatrix[5];
        _myClipPlane[10] = _myModelviewMatrix[10] * _myProjectionMatrix[10]
                + _myModelviewMatrix[11] * _myProjectionMatrix[14];
        _myClipPlane[11] = _myModelviewMatrix[10] * _myProjectionMatrix[11];

        _myClipPlane[12] = _myModelviewMatrix[12] * _myProjectionMatrix[0];
        _myClipPlane[13] = _myModelviewMatrix[13] * _myProjectionMatrix[5];
        _myClipPlane[14] = _myModelviewMatrix[14] * _myProjectionMatrix[10]
                + _myModelviewMatrix[15] * _myProjectionMatrix[14];
        _myClipPlane[15] = _myModelviewMatrix[14] * _myProjectionMatrix[11];

        /* Extract the numbers for the RIGHT plane */
        _myFrustum[0][0] = _myClipPlane[3] - _myClipPlane[0];
        _myFrustum[0][1] = _myClipPlane[7] - _myClipPlane[4];
        _myFrustum[0][2] = _myClipPlane[11] - _myClipPlane[8];
        _myFrustum[0][3] = _myClipPlane[15] - _myClipPlane[12];

        /* Normalize the result */
        float t = (float)(Math.sqrt(_myFrustum[0][0] * _myFrustum[0][0]
                + _myFrustum[0][1] * _myFrustum[0][1]
                + _myFrustum[0][2] * _myFrustum[0][2]));
        _myFrustum[0][0] /= t;
        _myFrustum[0][1] /= t;
        _myFrustum[0][2] /= t;
        _myFrustum[0][3] /= t;

        /* Extract the numbers for the LEFT plane */
        _myFrustum[1][0] = _myClipPlane[3] + _myClipPlane[0];
        _myFrustum[1][1] = _myClipPlane[7] + _myClipPlane[4];
        _myFrustum[1][2] = _myClipPlane[11] + _myClipPlane[8];
        _myFrustum[1][3] = _myClipPlane[15] + _myClipPlane[12];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[1][0] * _myFrustum[1][0]
                + _myFrustum[1][1] * _myFrustum[1][1]
                + _myFrustum[1][2] * _myFrustum[1][2]));
        _myFrustum[1][0] /= t;
        _myFrustum[1][1] /= t;
        _myFrustum[1][2] /= t;
        _myFrustum[1][3] /= t;

        /* Extract the BOTTOM plane */
        _myFrustum[2][0] = _myClipPlane[3] + _myClipPlane[1];
        _myFrustum[2][1] = _myClipPlane[7] + _myClipPlane[5];
        _myFrustum[2][2] = _myClipPlane[11] + _myClipPlane[9];
        _myFrustum[2][3] = _myClipPlane[15] + _myClipPlane[13];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[2][0] * _myFrustum[2][0]
                + _myFrustum[2][1] * _myFrustum[2][1]
                + _myFrustum[2][2] * _myFrustum[2][2]));
        _myFrustum[2][0] /= t;
        _myFrustum[2][1] /= t;
        _myFrustum[2][2] /= t;
        _myFrustum[2][3] /= t;

        /* Extract the TOP plane */
        _myFrustum[3][0] = _myClipPlane[3] - _myClipPlane[1];
        _myFrustum[3][1] = _myClipPlane[7] - _myClipPlane[5];
        _myFrustum[3][2] = _myClipPlane[11] - _myClipPlane[9];
        _myFrustum[3][3] = _myClipPlane[15] - _myClipPlane[13];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[3][0] * _myFrustum[3][0]
                + _myFrustum[3][1] * _myFrustum[3][1]
                + _myFrustum[3][2] * _myFrustum[3][2]));
        _myFrustum[3][0] /= t;
        _myFrustum[3][1] /= t;
        _myFrustum[3][2] /= t;
        _myFrustum[3][3] /= t;

        /* Extract the FAR plane */
        _myFrustum[4][0] = _myClipPlane[3] - _myClipPlane[2];
        _myFrustum[4][1] = _myClipPlane[7] - _myClipPlane[6];
        _myFrustum[4][2] = _myClipPlane[11] - _myClipPlane[10];
        _myFrustum[4][3] = _myClipPlane[15] - _myClipPlane[14];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[4][0] * _myFrustum[4][0]
                + _myFrustum[4][1] * _myFrustum[4][1]
                + _myFrustum[4][2] * _myFrustum[4][2]));
        _myFrustum[4][0] /= t;
        _myFrustum[4][1] /= t;
        _myFrustum[4][2] /= t;
        _myFrustum[4][3] /= t;

        /* Extract the NEAR plane */
        _myFrustum[5][0] = _myClipPlane[3] + _myClipPlane[2];
        _myFrustum[5][1] = _myClipPlane[7] + _myClipPlane[6];
        _myFrustum[5][2] = _myClipPlane[11] + _myClipPlane[10];
        _myFrustum[5][3] = _myClipPlane[15] + _myClipPlane[14];

        /* Normalize the result */
        t = (float)(Math.sqrt(_myFrustum[5][0] * _myFrustum[5][0]
                + _myFrustum[5][1] * _myFrustum[5][1]
                + _myFrustum[5][2] * _myFrustum[5][2]));
        _myFrustum[5][0] /= t;
        _myFrustum[5][1] /= t;
        _myFrustum[5][2] /= t;
        _myFrustum[5][3] /= t;
    }

    public float[][] frustum() {
        return _myFrustum;
    }
}
