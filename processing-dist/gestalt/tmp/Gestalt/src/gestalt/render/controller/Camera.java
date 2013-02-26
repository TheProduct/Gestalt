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


package gestalt.render.controller;

import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.cameraplugins.CameraPlugin;
import gestalt.shape.AbstractDrawable;
import gestalt.util.JoglUtil;

import mathematik.Matrix3f;
import mathematik.Vector2f;
import mathematik.Vector3f;

import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;

import static gestalt.Gestalt.*;


public class Camera
        extends AbstractDrawable
        implements Cloneable {

    /**
     * defines the percentage in which the frustum is offset from a simetric
     * position.
     */
    public Vector2f frustumoffset;

    /**
     * distance of the near clipping plane.
     */
    public float nearclipping;

    /**
     * distance of the far clipping plane.
     */
    public float farclipping;

    /**
     * field of vision (Y) in degrees(!).
     */
    public float fovy;

    /**
     * culling modes as found in 'Gestalt'. currently there are the following
     * modes.<br/>
     * Gestalt.CAMERA_CULLING_BACKFACE<br/>
     * Gestalt.CAMERA_CULLING_FRONTFACE<br/>
     * Gestalt.CAMERA_CULLING_FRONT_AND_BACKFACE<br/>
     * Gestalt.CAMERA_CULLING_NONE<br/>
     */
    public int culling;

    /**
     * the approximate up direction for the camera.
     */
    private Vector3f upvector;

    /**
     * amount of radians the camera is rotate around the X, Y and Z axis.
     * the property is only relevant if the camera is in rotation mode
     * 'Gestalt.CAMERA_MODE_ROTATE_XYZ'.
     */
    private Vector3f rotation;

    /**
     * the position the camera rotates to.
     * the property is only relevant if the camera is in rotation mode
     * 'Gestalt.CAMERA_LOOK_AT'.
     */
    private Vector3f lookat;

    /**
     * properties of the viewport of the camera.
     */
    protected final Viewport viewport;

    /**
     * a bin for camera plugins like for example a framegrabber.
     */
    protected Vector<CameraPlugin> plugins;

    private RenderBin children;

    private Vector3f position;

    private int _myCameraMode;

    private final Matrix3f _myTransformMatrix;

    private final Matrix3f _myInversRotationMatrix;

    private final Vector3f _myInternalForwardVector;

    private final Vector3f _myInternalSideVector;

    private final Vector3f _myInternalUpVector;

    private boolean _myIsActive = true;

    private boolean _myAutoUpdateUpVector = false;

    public static boolean AUTO_UPDATE_MATRIX = true;

    public static boolean STORE_MATRICES = true; // gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;

    private float[] _myProjectionMatrix = new float[16];

    private float[] _myModelViewMatrix = new float[16];

    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
        lookat = new Vector3f();
        frustumoffset = new Vector2f();
        upvector = new Vector3f();
        viewport = new Viewport();
        _myInternalForwardVector = new Vector3f();
        _myInternalSideVector = new Vector3f();
        _myInternalUpVector = new Vector3f();
        _myTransformMatrix = new Matrix3f();
        _myInversRotationMatrix = new Matrix3f();
        plugins = new Vector<CameraPlugin>();
        reset();
    }

    /**
     *
     * @param theAutoUpdateUpVector
     */
    public void auto_update_upvector(boolean theAutoUpdateUpVector) {
        _myAutoUpdateUpVector = theAutoUpdateUpVector;
    }

    /**
     * reset the camera to some more or less meaningful values.
     *
     * @param displaycapabilities DisplayCapabilities
     */
    public void reset(DisplayCapabilities displaycapabilities) {
        reset(displaycapabilities.width, displaycapabilities.height);
    }

    public Object clone() {
        Camera myCamera = null;
        try {
            myCamera = (Camera)(super.clone());
            myCamera.set(this);
        } catch (CloneNotSupportedException ex) {
            System.err.println(ex);
        }
        return myCamera;
    }

    /**
     * returns a cloned instance of this camera.<br/>
     * like in set(Camera) plugins are not copied.<br/>
     *
     * @return Camera
     */
    public Camera copy() {
        return (Camera)clone();
    }

    /**
     * reset the camera to some more or less meaningful values.
     *
     * @param theWidth int
     * @param theHeight int
     */
    public void reset(int theWidth, int theHeight) {
        nearclipping = 1;
        farclipping = 4096;
        /*
         * this is a good angle. moving the camera 400 for example z units away
         * from the origin and having a screen with 800px height. we get a
         * 1 pixel = 1 unit ratio for objects with z = 0.
         */
        fovy = CAMERA_A_HANDY_ANGLE;
        culling = UNDEFINED;
        _myTransformMatrix.setIdentity();
        _myCameraMode = CAMERA_MODE_ROTATE_XYZ;
        position.set(0, 0, theHeight);
        viewport.width = theWidth;
        viewport.height = theHeight;
        upvector.set(0, 1, 0);
        frustumoffset.set(0, 0);
        updateRotationMatrix();
    }

    /**
     * reset the camera to some more or less meaningful values.
     */
    public final void reset() {
        reset(viewport.width, viewport.height);
    }

    /**
     * copies properties from one camera. note that the plugins are only copied by reference.<br/>
     * this is no deep copy.
     *
     * @param theCamera Camera
     */
    public void set(Camera theCamera) {
        frustumoffset = theCamera.frustumoffset;
        nearclipping = theCamera.nearclipping;
        farclipping = theCamera.farclipping;
        fovy = theCamera.fovy;
        culling = theCamera.culling;
        upvector.set(theCamera.upvector);
        position.set(theCamera.position);
        rotation.set(theCamera.rotation);
        lookat.set(theCamera.lookat);
        viewport.width = theCamera.viewport.width;
        viewport.height = theCamera.viewport.height;
        viewport.x = theCamera.viewport.x;
        viewport.y = theCamera.viewport.y;
        plugins = theCamera.plugins;
        _myCameraMode = theCamera._myCameraMode;
        _myTransformMatrix.set(theCamera._myTransformMatrix);
        _myInversRotationMatrix.set(theCamera._myInversRotationMatrix);
    }

    /**
     * set the cameras rotation mode. currently there are three modes.<br/><br/>
     * Gestalt.CAMERA_MODE_LOOK_AT<br/>
     * the camera adjusts its rotation so that it looks at a point defined by
     * the vector 'lookat'.<br/>
     * Gestalt.CAMERA_MODE_ROTATE_XYZ<br/>
     * the camera rotation is defined by a vector 'rotation' in the order
     * X, Y and Z axis. note that this order profoundly matters.<br/>
     * Gestalt.CAMERA_MODE_ROTATION_AXIS<br/>
     * the camera rotation matrix is set directly by the client.
     *
     * @param theCameraMode int
     */
    public void setMode(int theCameraMode) {
        _myCameraMode = theCameraMode;
    }

    /**
     * get the camera rotation mode.
     *
     * @return int
     */
    public int getMode() {
        return _myCameraMode;
    }

    /**
     * move the camera along its side direction.
     *
     * @param theSpeed float
     */
    public void side(float theSpeed) {
        updateRotationMatrix();
        _myInternalSideVector.scale(theSpeed);
        if (!_myInternalSideVector.isNaN()) {
            position.add(_myInternalSideVector);
        } else {
            _myInternalSideVector.set(1, 0, 0);
        }
        updateRotationMatrix();
    }

    /**
     * move the camera along its up direction.
     *
     * @param theSpeed float
     */
    public void up(float theSpeed) {
        updateRotationMatrix();
        _myInternalUpVector.scale(theSpeed);
        if (!_myInternalUpVector.isNaN()) {
            position.add(_myInternalUpVector);
        } else {
            _myInternalUpVector.set(0, 1, 0);
        }
        updateRotationMatrix();
    }

    /**
     * move the camera along its forward direction.
     *
     * @param theSpeed float
     */
    public void forward(float theSpeed) {
        updateRotationMatrix();
        _myInternalForwardVector.scale(theSpeed);
        if (!_myInternalForwardVector.isNaN()) {
            position.add(_myInternalForwardVector);
        } else {
            _myInternalForwardVector.set(0, 0, -1);
        }
        updateRotationMatrix();
    }

    /**
     * the camera matrix needs to be updated after every modification of
     * position or rotation. the obligation to do so is on client side: you.
     * it makes sense to include an 'updateRotationMatrix()' method in every
     * camera implementation so that the matrix gets updated at least every
     * frame.
     */
    public void updateRotationMatrix() {
        switch (_myCameraMode) {
            case CAMERA_MODE_ROTATE_XYZ:
                _myTransformMatrix.setIdentity();
                _myTransformMatrix.setXYZRotation(rotation);

                /* store the invers */
                _myInversRotationMatrix.set(_myTransformMatrix);
                _myTransformMatrix.transpose();

                /* forward */
                _myInternalForwardVector.set(0, 0, 1);
                _myTransformMatrix.transform(_myInternalForwardVector);
                _myInternalForwardVector.normalize();

                /* side */
                _myInternalSideVector.set(1, 0, 0);
                _myTransformMatrix.transform(_myInternalSideVector);
                _myInternalSideVector.normalize();

                /* up */
                _myInternalUpVector.set(0, 1, 0);
                _myTransformMatrix.transform(_myInternalUpVector);
                _myInternalUpVector.normalize();
                break;
            case CAMERA_MODE_LOOK_AT:
                _myTransformMatrix.setIdentity();

                /* forward */
                _myInternalForwardVector.sub(position, lookat);
                _myInternalForwardVector.normalize();

                /* side */
                _myInternalSideVector.cross(upvector, _myInternalForwardVector);
                _myInternalSideVector.normalize();

                /* up */
                _myInternalUpVector.cross(_myInternalForwardVector, _myInternalSideVector);
                _myInternalUpVector.normalize();

                if (!_myInternalSideVector.isNaN()
                        && !_myInternalUpVector.isNaN()
                        && !_myInternalForwardVector.isNaN()) {
                    _myTransformMatrix.setXAxis(_myInternalSideVector);
                    _myTransformMatrix.setYAxis(_myInternalUpVector);
                    _myTransformMatrix.setZAxis(_myInternalForwardVector);
                }

                /* store the invers */
                _myInversRotationMatrix.set(_myTransformMatrix);
                _myTransformMatrix.transpose();
                break;

            case CAMERA_MODE_ROTATION_AXIS:

                /**
                 * @todo
                 * the concept is a little rough.
                 * it relies on the client updating the rotation matrix independently.
                 * use this only when you know what you are doing.
                 */
                /* forward */
                _myInternalForwardVector.set(_myTransformMatrix.getZAxis());

                /* side */
                _myInternalSideVector.set(_myTransformMatrix.getXAxis());

                /* up */
                _myInternalUpVector.set(_myTransformMatrix.getYAxis());

                /*
                 * store the invers
                 * the invers here is just the invers of the client provided rotation matrix.
                 */
                _myInversRotationMatrix.set(_myTransformMatrix);
                _myInversRotationMatrix.transpose();
                break;
        }

        if (_myAutoUpdateUpVector) {
            upvector().set(getUp());
        }
    }

    public Vector<CameraPlugin> plugins() {
        return plugins;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f getForward() {
        return _myInternalForwardVector;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f getUp() {
        return _myInternalUpVector;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f getSide() {
        return _myInternalSideVector;
    }

    /**
     *
     * @return Matrix3f
     */
    public Matrix3f getRotationMatrix() {
        return _myTransformMatrix;
    }

    /**
     *
     * @return Matrix3f
     */
    public Matrix3f getInversRotationMatrix() {
        return _myInversRotationMatrix;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f position() {
        return position;
    }

    /**
     *
     * @param thePosition Vector3f
     */
    public void setPositionRef(Vector3f thePosition) {
        position = thePosition;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f lookat() {
        return lookat;
    }

    /**
     *
     * @param theLookAtRef Vector3f
     */
    public void setLookAtRef(Vector3f theRef) {
        lookat = theRef;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f rotation() {
        return rotation;
    }

    /**
     *
     * @param theRef Vector3f
     */
    public void setRotationRef(Vector3f theRef) {
        rotation = theRef;
    }

    /**
     *
     * @return Vector3f
     */
    public Vector3f upvector() {
        return upvector;
    }

    /**
     *
     * @param theRef Vector3f
     */
    public void setUpVectorRef(Vector3f theRef) {
        upvector = theRef;
    }

    /**
     *
     * @return Viewport
     */
    public Viewport viewport() {
        return viewport;
    }

    /**
     *
     * @return boolean
     */
    public boolean isActive() {
        return _myIsActive;
    }

    /**
     *
     * @param theActive boolean
     */
    public void setActive(boolean theActive) {
        _myIsActive = theActive;
    }

    /**
     *
     * @param theMouseX float
     * @param theMouseY float
     * @return Vector3f
     */
    public Vector3f toWorld(float theMouseX, float theMouseY) {
        final Vector3f myPosition = new Vector3f(position());
        final float myScreenDistance = getDistanceToZeroPlane();
        myPosition.add(mathematik.Util.scale(getForward(), -myScreenDistance));
        myPosition.add(mathematik.Util.scale(getSide(), theMouseX));
        myPosition.add(mathematik.Util.scale(getUp(), theMouseY));
        return myPosition;
    }

    public float getDistanceToZeroPlane() {
        final float v = viewport().height * 0.5f;
        final float alpha = fovy * 0.5f;
        final float u = v / (float)Math.tan(Math.toRadians(alpha));
        return u;
    }

    public static float getDistanceToZeroPlane(final float theFOVY, final int theViewPortHeight) {
        final float v = theViewPortHeight * 0.5f;
        final float alpha = theFOVY * 0.5f;
        final float u = v / (float)Math.tan(Math.toRadians(alpha));
        return u;
    }

    /**
     * @deprecated use plugins instead ?
     * @param theDrawable
     */
    public void add(Drawable theDrawable) {
        if (children == null) {
            children = new RenderBin();
        }
        children.add(theDrawable);
    }

    public String toString() {
        return "camera " + "\n" + "position     : " + position + "\n" + "rotation     : " + rotation + "\n" + "upvector     : " + upvector + "\n" + "viewport     : " + viewport + "\n" + "mode         : " + _myCameraMode + "\n" + "lookat       : " + lookat + "\n" + "nearclipping : " + nearclipping + "\n" + "farclipping  : " + farclipping + "\n" + "fovy         : " + fovy + "\n" + "frustumoffset: " + frustumoffset + "\n" + "cullingmode  : " + culling + "\n";
    }

    public void draw(final GLContext theRenderContext) {

        (theRenderContext).camera = this;

        if (AUTO_UPDATE_MATRIX) {
            updateRotationMatrix();
        }

        final GL gl = theRenderContext.gl;

        /*
         * there was an issue with near clipping plane set to 0.
         * i m not sure if this stuff below is necessary but i ll
         * keep it for safety reasons.
         * reading the redbook the behavior might result from
         * a division by zero.
         */

        if (nearclipping <= 0) {
            nearclipping = 0.01f;
        }

        /* culling */
        gl.glFrontFace(GL.GL_CCW);

        switch (culling) {
            case CAMERA_CULLING_FRONTFACE:
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glCullFace(GL.GL_FRONT);
                break;
            case CAMERA_CULLING_BACKFACE:
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glCullFace(GL.GL_BACK);
                break;
            case CAMERA_CULLING_FRONT_AND_BACKFACE:
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glCullFace(GL.GL_FRONT_AND_BACK);
                break;
            case CAMERA_CULLING_NONE:
                gl.glDisable(GL.GL_CULL_FACE);
                break;
            default:

                /* UNDEFINED or value out of range */
                gl.glDisable(GL.GL_CULL_FACE);
                break;
        }

        /*
         * NAME
         * glCullFace - specify whether front- or back-facing facets can be culled
         *
         * C SPECIFICATION
         * void glCullFace( GLenum mode )
         *
         * PARAMETERS
         * mode Specifies whether front- or back-facing facets are candidates for
         * culling. Symbolic constants GL_FRONT, GL_BACK, and
         * GL_FRONT_AND_BACK are accepted. The initial value is GL_BACK.
         *
         * DESCRIPTION
         * glCullFace specifies whether front- or back-facing facets are culled
         * (as specified by mode) when facet culling is enabled. Facet culling is
         * initially disabled. To enable and disable facet culling, call the
         * glEnable and glDisable commands with the argument GL_CULL_FACE. Facets
         * include triangles, quadrilaterals, polygons, and rectangles.
         *
         * glFrontFace specifies which of the clockwise and counterclockwise
         * facets are front-facing and back-facing. See glFrontFace.
         *
         * NOTES
         * If mode is GL_FRONT_AND_BACK, no facets are drawn, but other primi-
         * tives such as points and lines are drawn.
         *
         */

        /* viewport */
        gl.glViewport(viewport.x,
                      viewport.y,
                      viewport.width,
                      viewport.height);

        /* projection matrix */
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        JoglUtil.gluPerspective(gl,
                                fovy,
                                (float)(viewport.width) / (float)(viewport.height),
                                nearclipping,
                                farclipping,
                                frustumoffset);

        if (STORE_MATRICES) {
            gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, _myProjectionMatrix, 0);
        }

        /* reset texture matrix -- this is just done for convenience and is only good for the current texture unit */
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();

        /* model-view matrix */
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glMultMatrixf(getRotationMatrix().toArray4f(), 0);
        gl.glTranslatef(-position().x, -position().y, -position().z);

        if (STORE_MATRICES) {
            gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, _myModelViewMatrix, 0);
        }

        /* handle plugins */
        {
            final Iterator<CameraPlugin> myIterator = plugins.iterator();
            while (myIterator.hasNext()) {
                final CameraPlugin myPlugin = (CameraPlugin)myIterator.next();
                myPlugin.begin(theRenderContext);
            }
        }

        /* handle children */
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                final Drawable myDrawable = children.getDataRef()[i];
                if (myDrawable != null && myDrawable.isActive()) {
                    myDrawable.draw(theRenderContext);
                }
            }
        }

        /* handle plugins */
        {
            final Iterator<CameraPlugin> myIterator = plugins.iterator();
            while (myIterator.hasNext()) {
                final CameraPlugin myPlugin = myIterator.next();
                myPlugin.end(theRenderContext);
            }
        }
    }

    public float[] projectionmatrix() {
        return _myProjectionMatrix;
    }

    public float[] modelviewmatrix() {
        return _myModelViewMatrix;
    }
}
