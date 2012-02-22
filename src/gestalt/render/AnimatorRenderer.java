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


import gestalt.context.DisplayCapabilities;
import gestalt.util.CameraMover;
import gestalt.util.FPSCounter;


public abstract class AnimatorRenderer
        extends BasicRenderer
        implements Loopable {

    private int _myNumberOfLoopables;

    private Loop _myRenderloop;

    private boolean _myUseCameraMover;

    private FPSCounter _myFPSCounter;

    private int _myFixFrameRate;

    protected int width;

    protected int height;

    protected boolean exit_on_error = false;

    public AnimatorRenderer() {
        _myNumberOfLoopables = 1;
        _myRenderloop = new Loop(_myNumberOfLoopables);
        _myRenderloop.add(this);
        _myUseCameraMover = false;
        _myFixFrameRate = UNDEFINED;
    }

    /**
     * override this method to setup things.
     */
    public void setup() {
    }

    /**
     * override this method to get a loop. <br/>
     * 'loop()' is called continously after 'setup()' was called.
     * @param theDeltaTime float duration of the last frame in seconds.
     */
    public void loop(float theDeltaTime) {
    }


    /*
     * don t use this method.
     * it is just added to prevent its accidental use.
     */
    public final void loop() {
    }

    /**
     * is called after the last loop.
     */
    public void finish() {
    }

    /**
     * this method activates a camera mover.
     * ( see also gestalt.util.CameraMover )
     * @param theState boolean
     */
    public void cameramover(boolean theState) {
        _myUseCameraMover = theState;
    }

    /**
     *
     * @param theState boolean
     * @return FPSCounter
     */
    public FPSCounter fpscounter(boolean theState) {
        if (theState) {
            if (_myFPSCounter == null) {
                _myFPSCounter = new FPSCounter();
                _myFPSCounter.setInterval(60);
                _myFPSCounter.display().position.set(displaycapabilities().width / -2 + 10,
                                                     displaycapabilities().height / 2 - 24);
                _myFPSCounter.display().color.set(0.5f);
                bin(BIN_2D_FOREGROUND).add(_myFPSCounter.display());
                _myRenderloop.add(_myFPSCounter);
            }
        } else {
            if (_myFPSCounter != null) {
                bin(BIN_2D_FOREGROUND).remove(_myFPSCounter.display());
                _myRenderloop.remove(_myFPSCounter);
                _myFPSCounter = null;
            }
        }
        return _myFPSCounter;
    }

    public void update(float theDeltaTime) {
        try {
            /* handle quit events */
            if ((display() != null && display().isDone()) ||
                    (event() != null && (event().keyCode == KEYCODE_ESCAPE ||
                    event().sending_quit))) {
                quit();
            } else {
                /* handle drawables */
                if (display() != null) {
                    display().display();
                }
                /* invoke loop */
                final float myDeltaTime;
                if (_myFixFrameRate > UNDEFINED) {
                    myDeltaTime = 1.0f / (float)_myFixFrameRate;
                } else {
                    myDeltaTime = theDeltaTime;
                }
                loop(myDeltaTime);
                if (_myUseCameraMover) {
                    CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
                }
                width = displaycapabilities().width;
                height = displaycapabilities().height;
            }
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass().getSimpleName() + ".update(float)");
            ex.printStackTrace();
            if (exit_on_error) {
                System.exit(-1);
            }
        }
    }

    /**
     * call this method to start the renderer.
     */
    public void init() {
        init(createDisplayCapabilities());
    }

    /**
     * call this method to start the renderer.
     * @param theDisplayCapabilities DisplayCapabilities define the display
     */
    public void init(DisplayCapabilities theDisplayCapabilities) {
        if (!_myIsInitalized) {
            create(theDisplayCapabilities);
        }
        width = displaycapabilities().width;
        height = displaycapabilities().height;
        _myRenderloop.execute();
    }

    public void init(int theWidth, int theHeight) {
        init(theWidth, theHeight, false);
    }

    public void init(int theWidth, int theHeight, boolean theUndecorated) {
        final DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = theWidth;
        myDisplayCapabilities.height = theHeight;
        myDisplayCapabilities.undecorated = theUndecorated;
        init(myDisplayCapabilities);
    }

    /**
     * start animation loop.
     */
    public void start() {
        _myRenderloop.start();
    }

    /**
     * stop animation loop.
     */
    public void stop() {
        _myRenderloop.stop();
    }

    /**
     * quit renderer.
     */
    public void quit() {
        finish();
        _myRenderloop.quit();
        super.quit();
    }

    /**
     * set the framerate in frames per second (FPS).
     * @param framerate int
     */
    public void framerate(int framerate) {
        _myRenderloop.framerate(framerate);
    }

    public void fixFramerate(int theFixedFramerate) {
        _myFixFrameRate = theFixedFramerate;
    }

    public int getCurrentFramerate() {
        return _myRenderloop.getCurrentFramerate();
    }

    public int getDesiredFramerate() {
        return _myRenderloop.getFramerate();
    }

    public void addToRenderLoop(Loopable theLoopable) {
        _myRenderloop.add(theLoopable);
    }

    public Loop renderloop() {
        return _myRenderloop;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
