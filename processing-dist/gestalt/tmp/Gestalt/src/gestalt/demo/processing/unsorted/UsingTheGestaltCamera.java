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


package gestalt.demo.processing.unsorted;

import gestalt.Gestalt;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.Line;

import mathematik.Vector3f;

import processing.core.PApplet;


/**
 * this demo shows how to use a gestalt camera and apply it to the processing view.
 */
public class UsingTheGestaltCamera
        extends PApplet {

    private GestaltPlugIn gestalt;

    private Line _myLine;

    private int _myCurrentPosition;

    private Vector3f[] _myPoints;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();

        /* gestalt */
        gestalt = new GestaltPlugIn(this);

        /* camera */
        gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        gestalt.camera().position().set(width / 2, height * 0.05f, height * 0.25f);
        gestalt.camera().lookat().set(width / 2, height / 2, 0);
        gestalt.camera().upvector().set(0, 0, 1);
        gestalt.cameramover(true);

        /* create path storage */
        _myPoints = new Vector3f[50];
        for (int i = 0; i < _myPoints.length; i++) {
            _myPoints[i] = new Vector3f();
        }

        /* create path view */
        _myLine = gestalt.drawablefactory().line();
        _myLine.material().color4f().set(1, 0.5f, 0, 0.25f);
        _myLine.material().depthmask = false; /* leave no trace in the depthbuffer */
        _myLine.linewidth = 5;
        _myLine.setPrimitive(Gestalt.LINE_PRIMITIVE_TYPE_LINE_LOOP);
        gestalt.bin(Gestalt.BIN_3D).add(_myLine);

        /* share points */
        _myLine.points = _myPoints;
    }

    public void draw() {
        /* move camera */
        gestalt.camera().side(0.5f);

        /* collect points */
        _myCurrentPosition++;
        _myCurrentPosition %= _myPoints.length;
        _myPoints[_myCurrentPosition].set(mouseX, mouseY, 0);

        /* draw */
        background(50);
        if (!mousePressed) {
            /* set the state of the processing view to match the gestalt camera. */
            gestalt.applyCamera(gestalt.camera());
        }
        connectCollectedPoints();
    }

    private void connectCollectedPoints() {
        for (int i = 0; i < _myPoints.length - 1; i++) {
            /* get current id */
            int myID = i + _myCurrentPosition;
            myID %= _myPoints.length;
            /* get next id */
            int myNextID = myID + 1;
            myNextID %= _myPoints.length;
            /* connect both points */
            stroke(255);
            line(_myPoints[myID].x,
                 _myPoints[myID].y,
                 _myPoints[myID].z,
                 _myPoints[myNextID].x,
                 _myPoints[myNextID].y,
                 _myPoints[myNextID].z);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {UsingTheGestaltCamera.class.getName()});
    }
}
