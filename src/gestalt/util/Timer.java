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


public class Timer {

    public static boolean VERBOSE = false;

    private float _myMaxDeltaTime;

    private float _myMinDeltaTime;

    private float[] _myAveragedDeltaTime;

    private float _myAveragedDeltaTimeSummed;

    private int _myAverageDeltaTimePointer;

    private long _myLastTime;

    private float _myDeltaTime;

    private long _myLongDeltaTime;

    private float _myLastDeltaTime;

    public Timer(int theSamplerSize) {
        _myAveragedDeltaTime = new float[theSamplerSize];
        _myMaxDeltaTime = 1.0f;
        _myMinDeltaTime = 0.00001f;
        reset();
    }


    public Timer() {
        this(60);
    }


    public float getDeltaTime() {
        float myDeltaTime = _myAveragedDeltaTimeSummed / _myAveragedDeltaTime.length;
        if (myDeltaTime <= 0) {
            return _myMinDeltaTime;
        }
        return myDeltaTime;
    }


    public long getLongDeltaTime() {
        return _myLongDeltaTime;
    }


    public void setMinDeltaTime(float theMinDeltaTime) {
        _myMinDeltaTime = theMinDeltaTime;
    }


    public void setMaxDeltaTime(float theMaxDeltaTime) {
        _myMaxDeltaTime = theMaxDeltaTime;
    }


    private void calculateAveragedDeltaTime(float theDeltaTime) {
        /* contrain delta time */
        if (theDeltaTime > _myMaxDeltaTime) {
            theDeltaTime = _myMaxDeltaTime;
        } else if (theDeltaTime < _myMinDeltaTime) {
            theDeltaTime = _myMinDeltaTime;
        }

        /* calculate average */
        _myAverageDeltaTimePointer++;
        _myAverageDeltaTimePointer %= _myAveragedDeltaTime.length;
        _myAveragedDeltaTimeSummed -= _myAveragedDeltaTime[_myAverageDeltaTimePointer];
        _myAveragedDeltaTime[_myAverageDeltaTimePointer] = theDeltaTime;
        _myAveragedDeltaTimeSummed += theDeltaTime;
    }


    public void reset() {
        _myDeltaTime = 0;
        _myLastDeltaTime = 0;

        _myAveragedDeltaTimeSummed = 0;
        for (int i = 0; i < _myAveragedDeltaTime.length; i++) {
            _myAveragedDeltaTime[i] = 0.025f;
            _myAveragedDeltaTimeSummed += _myAveragedDeltaTime[i];
        }

        _myLastTime = System.currentTimeMillis();
    }


    public void loop() {
        /* get poll time */
        long myCurrentTime = System.currentTimeMillis();

        /* calculate delta time */
        if (myCurrentTime >= _myLastTime) {
            _myLongDeltaTime = myCurrentTime - _myLastTime;
            _myDeltaTime = _myLongDeltaTime / 1000.0f;
            /* contrain delta time */
            if (_myDeltaTime > _myMaxDeltaTime) {
                if (VERBOSE) {
                    System.err.println("### WARNING @ Timer / deltatime is greater than MAX_DELTA_TIME (" +
                                       _myMaxDeltaTime +
                                       "): " + _myDeltaTime + " / diff (" + myCurrentTime + " / " + _myLastTime + ").");
                }
                /* use last measured delta if it has a sensible value */
                if (_myLastDeltaTime > _myMaxDeltaTime) {
                    _myDeltaTime = _myMaxDeltaTime;
                    _myLongDeltaTime = (long) (_myMaxDeltaTime * 1000);
                } else {
                    _myDeltaTime = _myLastDeltaTime;
                    _myLongDeltaTime = (long) (_myLastDeltaTime * 1000);
                }
            } else if (_myDeltaTime < 0) {
                _myDeltaTime = 0;
                _myLongDeltaTime = 0;
            }
            _myLastDeltaTime = _myDeltaTime;
        } else {
            _myDeltaTime = _myLastDeltaTime;
            _myLongDeltaTime = (long) (_myLastDeltaTime * 1000);
            if (VERBOSE) {
                System.err.println(
                    "### WARNING @ Timer / system time is lower than previously measured time.");
            }
        }

        /* calculate averaged delta time */
        calculateAveragedDeltaTime(_myDeltaTime);

        /* store absolut time */
        _myLastTime = myCurrentTime;
    }
}
