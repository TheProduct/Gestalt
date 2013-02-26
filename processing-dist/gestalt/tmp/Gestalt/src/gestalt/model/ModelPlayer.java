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


package gestalt.model;


import java.util.Vector;


public class ModelPlayer {

    public static int DEFAULT_FRAMES_PER_SECOND = 25;

    public static final int STOP = 0;

    public static final int PAUSE = 1;

    public static final int PLAY = 2;

    private int _myFramesPerSecond;

    private final Model _myModel;

    private int _myCurrentFrame;

    private final FrameData _myPlayArea;

    private int _myOldAnimationIndex;

    private int _myNewAnimationIndex;

    private String _myCurrentAnimation;

    private int _myState;

    private float _myPlayVelocity;

    private float _myNextFrameWaitTime;

    private float _myCurrentTime;

    private boolean isLooping;

    public ModelPlayer(Model theModel) {
        _myModel = theModel;
        _myCurrentFrame = 0;
        _myState = STOP;
        _myPlayArea = new FrameData(); // int[2];
        _myPlayArea.start = 0;
        _myPlayArea.duration = _myModel.getNumberOfFrames();
        _myFramesPerSecond = DEFAULT_FRAMES_PER_SECOND;
        _myPlayVelocity = 1f;
        _myNextFrameWaitTime = 2f;
        _myCurrentAnimation = "";
        isLooping = false;
    }


    public void loop(float theDeltaTime) {
        _myCurrentTime += theDeltaTime;
        int myFrameIncreaser = (int) (_myCurrentTime / (1f / (float) _myFramesPerSecond) * _myPlayVelocity);
        if (_myCurrentTime > _myNextFrameWaitTime) {
            _myCurrentTime = 0;
            if (_myState == PLAY) {
                FrameData myFrame = nextFrame(myFrameIncreaser);
                _myModel.setViewStart(myFrame.start);
                _myModel.setViewLength(myFrame.duration);
            }
        }
    }


    public void setLooping(boolean theLooping) {
        isLooping = theLooping;
    }


    public void setDuration(final int theDuration) {
        _myPlayArea.duration = theDuration;
    }


    private class FrameData {
        public int start;

        public int duration;
    }


    private FrameData nextFrame(int theFrameIncreaser) {
        _myCurrentFrame += theFrameIncreaser;
        if (_myCurrentFrame > _myPlayArea.duration) {
            if (isLooping) {
                _myCurrentFrame = _myPlayArea.start;
            } else {
                _myCurrentFrame = _myPlayArea.duration;
            }
        } else if (_myCurrentFrame < _myPlayArea.start) {
            if (isLooping) {
                _myCurrentFrame = _myPlayArea.duration;
            } else {
                _myCurrentFrame = _myPlayArea.start;
            }
        }
        FrameData myData = new FrameData();
        myData.start = _myModel.getNumberOfVerticesPerFrame() * _myCurrentFrame;
        myData.duration = _myModel.getNumberOfVerticesPerFrame();
        return myData;
    }


    private void setPlayArea() {
        Vector<ModelAnimation> myAnimations = _myModel.getAnimations();
        ModelAnimation myAnimation = myAnimations.get(_myNewAnimationIndex);
        _myPlayArea.start = myAnimation.startFrame;
        _myPlayArea.duration = myAnimation.stopFrame - myAnimation.startFrame;
        if (_myPlayArea.duration >= _myModel.getNumberOfFrames()) {
            _myPlayArea.duration = _myModel.getNumberOfFrames() - 1;
        }
        if (_myOldAnimationIndex != _myNewAnimationIndex) {
            _myCurrentFrame = _myPlayArea.start;
        }
        _myState = PLAY;
        _myOldAnimationIndex = _myNewAnimationIndex;
    }


    private float getNextFrameWaitTime(float thePlayVelocity,
                                       int theFramesPerSecond) {
        return (1f / (float) theFramesPerSecond) / thePlayVelocity;
    }


    public float getPlayVelocity() {
        return _myPlayVelocity;
    }


    public void setFramesPerSecond(int theFramesPerSecond) {
        _myFramesPerSecond = theFramesPerSecond;
    }


    public int getFramesPerSecond() {
        return _myFramesPerSecond;
    }


    public void play(float thePlayVelocity) {
        _myPlayVelocity = thePlayVelocity;
        _myNextFrameWaitTime = getNextFrameWaitTime(Math.abs(thePlayVelocity), _myFramesPerSecond);
        if (_myState != PLAY) {
            _myCurrentTime = 0;
            _myNewAnimationIndex = 0;
            setPlayArea();
        }
    }


    public void play(String theAnimation,
                     float thePlayVelocity) {
        _myPlayVelocity = thePlayVelocity;
        _myNextFrameWaitTime = getNextFrameWaitTime(Math.abs(thePlayVelocity), _myFramesPerSecond);
        if (_myState != PLAY || !theAnimation.equals(_myCurrentAnimation)) {
            _myCurrentAnimation = theAnimation;
            _myNewAnimationIndex = 0;
            Vector<ModelAnimation> myAnimations = _myModel.getAnimations();
            for (int i = 0; i < myAnimations.size(); i++) {
                ModelAnimation myAnimation = myAnimations.get(i);
                if (myAnimation.name.equalsIgnoreCase(theAnimation)) {
                    _myNewAnimationIndex = i;
                    break;
                }
            }
            setPlayArea();
        }
    }


    public void pause() {
        _myState = PAUSE;
    }


    public void stop() {
        _myState = STOP;
        _myCurrentFrame = _myPlayArea.start;
    }


    public void goToFrame(int theFrame) {
        if (theFrame < 0) {
            theFrame = 0;
        }
        if (theFrame > _myModel.getNumberOfFrames()) {
            theFrame = _myModel.getNumberOfFrames();
        }
        _myCurrentFrame = theFrame;
        _myModel.setViewStart(_myModel.getNumberOfVerticesPerFrame() * _myCurrentFrame);
    }


    public boolean isDone() {
        if (_myPlayVelocity >= 0) {
            if (_myCurrentFrame == _myPlayArea.duration) {
                return true;
            }
            return false;
        } else {
            if (_myCurrentFrame == _myPlayArea.start) {
                return true;
            }
            return false;
        }
    }


    public float howMuchIsAlreadyPlayed() {
        if (_myPlayArea.duration - _myPlayArea.start == 0) {
            return 0;
        }
        return (float) (_myCurrentFrame - _myPlayArea.start) / (float) (_myPlayArea.duration - _myPlayArea.start);
    }


    public int getCurrentFrame() {
        return _myCurrentFrame;
    }
}
