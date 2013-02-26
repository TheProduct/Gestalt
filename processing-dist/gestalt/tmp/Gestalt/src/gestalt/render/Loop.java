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


package gestalt.render;


import gestalt.util.Timer;


public class Loop {

    private Loopable[] _myLoopables;

    private int _myLoopableCounter;

    private int _myFramerate;

    private boolean _myDrawFlag;

    private boolean _myQuitFlag;

    private int _myArrayBlockSize;

    private Timer _myTimer;

    private float _myDeltaWaitTime;

    private long _myDeltaWorkingTime;

    private float _myAveragedDeltaTime;

    private int _myCurrentFramerate;

    public Loop(int theArrayBlockSize) {
        _myArrayBlockSize = theArrayBlockSize;
        _myLoopables = new Loopable[theArrayBlockSize];
        _myLoopableCounter = 0;
        _myFramerate = 30;
        _myDrawFlag = true;
        _myQuitFlag = false;
        _myTimer = new Timer();
        _myDeltaWaitTime = 0.5f * (float) _myFramerate / 1000f;
        _myAveragedDeltaTime = 0.5f * (float) _myFramerate / 1000f;
    }


    public void execute() {
        /* setup clients */
        for (int i = 0; i < _myLoopableCounter; ++i) {
            _myLoopables[i].setup();
        }

        /* loop all clients */
        while (!_myQuitFlag) {
            if (_myDrawFlag) {
                loop();
            }
        }
        /* clean up */
        _myLoopables = null;
    }


    private void loop() {
        /* go to work */
        _myCurrentFramerate = (int) (1f / (_myAveragedDeltaTime + _myDeltaWaitTime));
        for (int i = 0; i < _myLoopableCounter; ++i) {
            /* delta time is added because it is sampled twice in one frame. */
            _myLoopables[i].update(_myAveragedDeltaTime + _myDeltaWaitTime);
        }
        /* sample render time */
        _myTimer.loop();
        _myDeltaWaitTime = _myTimer.getDeltaTime();
        _myDeltaWorkingTime = _myTimer.getLongDeltaTime();
        /* wait */
        delay(_myDeltaWorkingTime);
        /* sample wait time */
        _myTimer.loop();
        _myAveragedDeltaTime = _myTimer.getDeltaTime();
    }


    public void framerate(int theFramerate) {
        _myFramerate = theFramerate;
    }


    public int getFramerate() {
        return _myFramerate;
    }


    public void start() {
        _myDrawFlag = true;
    }


    public void stop() {
        _myDrawFlag = false;
    }


    public void quit() {
        _myQuitFlag = true;
    }


    private void delay(long theDeltaTime) {
        if (_myFramerate > 0) {
            long mySingleFrameDuration = 1000 / _myFramerate;
            long myWaitTime = mySingleFrameDuration - theDeltaTime;
            if (myWaitTime > 0) {
                try {
                    Thread.sleep(myWaitTime);
                } catch (InterruptedException ex) {
                    System.err.println("### ERROR @ " + this.getClass() + " / couldn t wait. " + ex);
                }
            }
        }
    }


    public void add(Loopable loopable) {
        if (_myLoopableCounter >= _myLoopables.length) {
            Loopable[] myCopyArray = new Loopable[_myLoopables.length + _myArrayBlockSize];
            System.arraycopy(_myLoopables, 0,
                             myCopyArray, 0,
                             _myLoopables.length);
            _myLoopables = myCopyArray;
        }
        _myLoopables[_myLoopableCounter] = loopable;
        _myLoopableCounter++;
    }


    public void remove(Loopable loopable) {
        for (int i = 0; i < _myLoopableCounter; ++i) {
            if (_myLoopables[i] == loopable) {
                --_myLoopableCounter; for (; i < _myLoopableCounter; ++i) {
                    _myLoopables[i] = _myLoopables[i + 1];
                }
                break;
            }
        }
    }


    public int getCurrentFramerate() {
        return _myCurrentFramerate;
    }
}
