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


import gestalt.shape.FastBitmapFont;
import gestalt.render.Loopable;


public class FPSCounter
    implements Loopable {

    public static boolean VERBOSE = false;

    private float _myTime;

    private int _myFrameCounterInterval;

    private int _myFrameCounter;

    private float _myCurrentFramesPerSecond;

    private long _myCurrentTime;

    private long _myLastTime;

    private FastBitmapFont _myDisplay;

    public FPSCounter() {
        setup();
    }


    public void setup() {
        _myTime = 0;
        _myFrameCounter = 0;
        _myFrameCounterInterval = 60;
        _myCurrentFramesPerSecond = 0;
        _myCurrentTime = System.currentTimeMillis();
        _myLastTime = _myCurrentTime;
    }


    public FastBitmapFont display() {
        if (_myDisplay == null) {
            _myDisplay = new FastBitmapFont();
            _myDisplay.text = "*";
        }
        return _myDisplay;
    }


    public void setInterval(int theFrameCounterInterval) {
        _myFrameCounterInterval = theFrameCounterInterval;
    }


    public float getFramesPerSecond() {
        return _myCurrentFramesPerSecond;
    }


    public void loop(float theDeltaTime) {
        _myFrameCounter++;
        if (_myFrameCounter >= _myFrameCounterInterval) {
            _myCurrentFramesPerSecond = (float) _myFrameCounterInterval / _myTime;
            _myFrameCounter = 0;
            _myTime = 0;
            if (VERBOSE) {
                System.out.println("### FPS: " + _myCurrentFramesPerSecond);
            }
        }
        _myTime += theDeltaTime;

        if (_myDisplay != null) {
            _myDisplay.text = "FPS: " + (int) _myCurrentFramesPerSecond;
        }
    }


    public void loop() {
        _myCurrentTime = System.currentTimeMillis();
        final float myDeltaTime = (_myCurrentTime - _myLastTime) / 1000f;
        _myLastTime = _myCurrentTime;

        _myFrameCounter++;
        if (_myFrameCounter >= _myFrameCounterInterval) {
            _myCurrentFramesPerSecond = (float) _myFrameCounterInterval / _myTime;
            _myFrameCounter = 0;
            _myTime = 0;
            if (VERBOSE) {
                System.out.println("### FPS!: " + _myCurrentFramesPerSecond);
            }
        }

        _myTime += myDeltaTime;

        if (_myDisplay != null) {
            _myDisplay.text = "FPS: " + (int) _myCurrentFramesPerSecond;
        }
    }


    public String toString() {
        return "### FPS: " + _myCurrentFramesPerSecond;
    }


    public void update(float theDeltaTime) {
        loop(theDeltaTime);
    }
}
