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

import gestalt.input.EventHandler;
import gestalt.render.controller.Camera;

import static gestalt.Gestalt.*;


public class CameraMover {

    /**
     * the speed in units per second in which the camera is moved.
     */
    public static float movespeed = 300.0f;

    /**
     * the speed in units per second in which the camera is rotated.
     */
    public static float turnspeed = 150.0f;

    /**
     * keycode value of the key that triggers a forward movement.
     */
    public static int FORWARD = KEYCODE_UP;

    /**
     * keycode value of the key that triggers a backwards movement.
     */
    public static int BACKWARDS = KEYCODE_DOWN;

    /**
     * keycode value of the key that triggers a left movement.
     */
    public static int LEFT = KEYCODE_LEFT;

    /**
     * keycode value of the key that triggers a right movement.
     */
    public static int RIGHT = KEYCODE_RIGHT;

    /**
     * keycode value of the key that triggers an up movement.
     */
    public static int UP = KEYCODE_Q;

    /**
     * keycode value of the key that triggers a down movement.
     */
    public static int DOWN = KEYCODE_A;

    /**
     * keycode value of the key that dumps camera properties.
     */
    public static int DUMP = KEYCODE_X;

    /**
     * reset camera position
     */
    public static int RESET = KEYCODE_Y;

    /**
     * keycode value of the key that increases the camera field of vision (Y).
     */
    public static int FOVY_INCREASE = KEYCODE_W;

    /**
     * keycode value of the key that decreases the camera field of vision (Y).
     */
    public static int FOVY_DECREASE = KEYCODE_S;

    /**
     * keycode value of the key that switches the camera to 'CAMERA_MODE_ROTATE_XYZ'.
     */
    public static int MODE_ROTATE = KEYCODE_E;

    /**
     * keycode value of the key that switches the camera to 'CAMERA_MODE_LOOK_AT'.
     */
    public static int MODE_LOOKAT = KEYCODE_D;

    /**
     * set some camera properties to values that could be considered 'default'.
     *
     * @param theCamera Camera
     * @param theScreenHeight int
     */
    public static void reset(Camera theCamera) {
        theCamera.rotation().set(0, 0, 0);
        theCamera.frustumoffset.set(0, 0);
        theCamera.lookat().set(0, 0, 0);
//        theCamera.nearclipping = 1;
//        theCamera.farclipping = 2000;
//        theCamera.fovy = CAMERA_A_HANDY_ANGLE;
//        theCamera.upvector = new Vector3f(0, 1, 0);
        float myZ = (float)((-theCamera.viewport().height * 0.5f) / Math.tan(Math.toRadians(theCamera.fovy / 2)));
        theCamera.position().set(0, 0, myZ);
    }

    /**
     * handle key events produced by the eventhandler with the specified camera
     * object in relation to the delta time in seconds.
     *
     * @param theCamera Camera
     * @param theEvent EventHandler
     * @param theDeltaTime float
     */
    public static void handleKeyEvent(Camera theCamera, EventHandler theEvent, float theDeltaTime) {

        float mySpeed = movespeed * theDeltaTime;
        float myTurnSpeed = turnspeed * theDeltaTime;

        if (theEvent.keyDown) {
            /* dump camera properties */
            if (theEvent.keyCode == DUMP) {
                System.out.println(theCamera);
            }
            /* switch camera mode */
            if (theEvent.keyCode == MODE_ROTATE) {
                theCamera.setMode(CAMERA_MODE_ROTATE_XYZ);
            }
            if (theEvent.keyCode == MODE_LOOKAT) {
                theCamera.setMode(CAMERA_MODE_LOOK_AT);
            }
            /* reset */
            if (theEvent.keyCode == RESET) {
                reset(theCamera);
            }

            if (theEvent.shift) {
                /* change properties depending on camera mode */
                switch (theCamera.getMode()) {
                    case CAMERA_MODE_ROTATE_XYZ:
                        if (theEvent.keyCode == LEFT) {
                            theCamera.rotation().y += myTurnSpeed * (PI / 360);
                        }
                        if (theEvent.keyCode == RIGHT) {
                            theCamera.rotation().y -= myTurnSpeed * (PI / 360);
                        }
                        if (theEvent.keyCode == FORWARD) {
                            theCamera.rotation().x -= myTurnSpeed * (PI / 360);
                        }
                        if (theEvent.keyCode == BACKWARDS) {
                            theCamera.rotation().x += myTurnSpeed * (PI / 360);
                        }
                        if (theEvent.keyCode == DOWN) {
                            theCamera.rotation().z -= myTurnSpeed * (PI / 360);
                        }
                        if (theEvent.keyCode == UP) {
                            theCamera.rotation().z += myTurnSpeed * (PI / 360);
                        }
                        break;
                    case CAMERA_MODE_LOOK_AT:
                        if (theEvent.keyCode == LEFT) {
                            theCamera.lookat().x -= mySpeed;
                        }
                        if (theEvent.keyCode == RIGHT) {
                            theCamera.lookat().x += mySpeed;
                        }
                        if (theEvent.keyCode == FORWARD) {
                            theCamera.lookat().y -= mySpeed;
                        }
                        if (theEvent.keyCode == BACKWARDS) {
                            theCamera.lookat().y += mySpeed;
                        }
                        if (theEvent.keyCode == UP) {
                            theCamera.lookat().z -= mySpeed;
                        }
                        if (theEvent.keyCode == DOWN) {
                            theCamera.lookat().z += mySpeed;
                        }
                        break;
                }
            } else {
                /* move camera */
                if (theEvent.keyCode == FORWARD) {
                    theCamera.forward(-mySpeed);
                }
                if (theEvent.keyCode == BACKWARDS) {
                    theCamera.forward(mySpeed);
                }
                if (theEvent.keyCode == LEFT) {
                    theCamera.side(-mySpeed);
                }
                if (theEvent.keyCode == RIGHT) {
                    theCamera.side(mySpeed);
                }
                if (theEvent.keyCode == DOWN) {
                    theCamera.up(-mySpeed);
                }
                if (theEvent.keyCode == UP) {
                    theCamera.up(mySpeed);
                }
                if (theEvent.keyCode == FOVY_INCREASE) {
                    theCamera.fovy += myTurnSpeed;
                }
                if (theEvent.keyCode == FOVY_DECREASE) {
                    theCamera.fovy -= myTurnSpeed;
                }
            }
        }
    }
}
