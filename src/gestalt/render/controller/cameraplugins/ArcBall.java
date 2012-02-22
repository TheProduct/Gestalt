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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.render.controller.Camera;

import mathematik.Quaternion;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;
import mathematik.Vector4f;


public class ArcBall
        implements CameraPlugin {

    private final Vector3f _myCenter;

    private float _myRadius;

    private final Vector3f _myDownPosition;

    private final Vector3f _myDragPosition;

    private Quaternion _myCurrentQuaternion;

    private Quaternion _myDownQuaternion;

    private Quaternion _myDragQuaternion;

    private boolean _myLastActiveState = false;

    private int _myTriggerButton;

    private float _myMouseWheelScale;

    public ArcBall() {
        _myCenter = new Vector3f();
        _myRadius = 480.0f;

        _myMouseWheelScale = 10;

        _myDownPosition = new Vector3f();
        _myDragPosition = new Vector3f();

        _myCurrentQuaternion = new Quaternion();
        _myDownQuaternion = new Quaternion();
        _myDragQuaternion = new Quaternion();

        _myTriggerButton = Gestalt.MOUSEBUTTON_LEFT;
    }

    public void radius(float theRadius) {
        _myRadius = theRadius;
    }

    public void mousewheelscale(float theMouseWheelScale) {
        _myMouseWheelScale = theMouseWheelScale;
    }

    public Vector3f center() {
        return _myCenter;
    }

    private void mousePressed(float theX, float theY) {
        _myDownPosition.set(mouse_to_sphere(theX, theY));
        _myDownQuaternion.set(_myCurrentQuaternion);
        _myDragQuaternion.reset();
    }

    private void mouseDragged(float theX, float theY) {
        _myDragPosition.set(mouse_to_sphere(theX, theY));
        _myDragQuaternion.set(_myDownPosition.dot(_myDragPosition), mathematik.Util.cross(_myDownPosition, _myDragPosition));
    }

    private Vector3f mouse_to_sphere(float x, float y) {
        final Vector3f v = new Vector3f();
        v.x = (x - _myCenter.x) / _myRadius;
        v.y = (y - _myCenter.y) / _myRadius;

        float myLengthSquared = v.x * v.x + v.y * v.y;
        if (myLengthSquared > 1.0f) {
            v.normalize();
        } else {
            v.z = (float)Math.sqrt(1.0f - myLengthSquared);
        }
        return v;
    }

    public void triggerbutton(final int theTriggerButton) {
        _myTriggerButton = theTriggerButton;
    }

    public void begin(final GLContext theRenderContext) {
        final boolean myActiveState = theRenderContext.event.mouseDown && theRenderContext.event.mouseButton == _myTriggerButton;
        final float mouseX = theRenderContext.event.mouseX;
        final float mouseY = theRenderContext.event.mouseY;

        if (theRenderContext.camera.getMode() != Gestalt.CAMERA_MODE_ROTATION_AXIS) {
            /* TODO should we comment on this? */
        }

        theRenderContext.camera.setMode(Gestalt.CAMERA_MODE_ROTATION_AXIS);

        if (myActiveState) {
            if (!_myLastActiveState) {
                mousePressed(mouseX, mouseY);
            }
            mouseDragged(mouseX, mouseY);
        } else {
            if (_myLastActiveState) {
            }
        }
        _myLastActiveState = myActiveState;

        /* radius */
        _myRadius += theRenderContext.event.mousewheel * _myMouseWheelScale;

        /* apply transform */
        final TransformMatrix4f t = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        final TransformMatrix4f m = new TransformMatrix4f(TransformMatrix4f.IDENTITY);

        t.translation.set(_myCenter.x, _myCenter.y, _myCenter.z);
        _myCurrentQuaternion.multiply(_myDragQuaternion, _myDownQuaternion);
        final Vector4f myRotationAxisAngle = _myCurrentQuaternion.getVectorAndAngle();
        if (!myRotationAxisAngle.isNaN()) {
            m.rotation.setRotation(myRotationAxisAngle);
        }

        final TransformMatrix4f r = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        r.multiply(m);
        r.multiply(t);

        final Camera myCamera = theRenderContext.camera;
        myCamera.getRotationMatrix().set(r.rotation);

        r.rotation.transpose();
        Vector3f v = new Vector3f(0, 0, _myRadius);
        r.transform(v);
        myCamera.position().set(v);
    }

    public void end(final GLContext theRenderContext) {
    }
}

