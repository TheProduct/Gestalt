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


import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.input.GestaltKeyListener;
import gestalt.input.GestaltMouseListener;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.render.bin.ShapeBin;
import gestalt.render.bin.TwoSidedBin;
import gestalt.render.controller.Bin3DFinish;
import gestalt.render.controller.Camera;
import gestalt.render.controller.cameraplugins.Fog;
import gestalt.render.controller.FrameFinish;
import gestalt.render.controller.FrameSetup;
import gestalt.render.controller.cameraplugins.Light;
import gestalt.render.controller.Origin;


/**
 * basic renderer supplies a basic render setup including the texturemanager,
 * the eventhandler, bitmapproducer, a camera, etc.
 */
public class BasicRenderer
        extends MinimalRenderer
        implements Gestalt,
                   GestaltKeyListener,
                   GestaltMouseListener {

    /* basic */
    private FrameSetup _myFramesetup;

    private FrameFinish _myFramefinish;

    private Bin3DFinish _myBin3DFinish;

    private Camera _myCamera;

    private Fog _myFog;

    private Light _myLight;

    private Origin _myOrigin;

    private Disposer _myDisposer;

    /**
     * creates all context. this method must be called before the renderer is actually used.
     * @param theDisplayCapabilities DisplayCapabilities
     */
    @SuppressWarnings("deprecation")
    public void create(DisplayCapabilities theDisplayCapabilities) {
        if (!_myIsInitalized) {
            _myIsInitalized = true;

            /* core */
            createDrawablefactory(theDisplayCapabilities);
            createEvent();
            /** @todo this is a test to see whether this makes sense */
            _myEvent.addKeyListener(this);
            _myEvent.addMouseListener(this);
            createDisplay(theDisplayCapabilities);
            _myBin = new RenderBin(20);

            /* plugins */
            createPlugins(theDisplayCapabilities);

            setupDefaultRenderbins();
        }
    }

    protected void createPlugins(DisplayCapabilities theDisplayCapabilities) {
        _myCamera = _myDrawablefactory.camera();
        _myCamera.position().z = displaycapabilities().height;
        _myCamera.viewport().width = displaycapabilities().width;
        _myCamera.viewport().height = displaycapabilities().height;
        _myFog = _myDrawablefactory.fog();
        _myFog.enable = false;
        _myFog.color().set(theDisplayCapabilities.backgroundcolor);
        _myLight = _myDrawablefactory.light();
        _myLight.enable = false;
        _myOrigin = _myDrawablefactory.origin();
        _myFramesetup = _myDrawablefactory.frameSetup();
        _myFramefinish = _myDrawablefactory.frameFinish();
        _myBin3DFinish = _myDrawablefactory.bin3DFinish();
        _myDisposer = new Disposer();
    }

    /**
     * quits the renderer.
     */
    public void quit() {
        _myDisplay.finish();
        _myEvent = null;
//        _myBitmapfactory = null;
        _myDrawablefactory = null;
        _myCamera = null;
        _myFog = null;
        _myLight = null;
        _myOrigin = null;
        _myFramesetup = null;
        _myFramefinish = null;
        /**
         * @todo
         * the system exit call is here because the application does not
         * quit on windows machines. very bizarr. very uncool. anyway.
         */
        if (WORKAROUND_FORCE_QUIT) {
            System.exit(0);
        }
    }

    /**
     * creates a default setup of several 2D, 3D and some helper bins.
     * <br/>
     * available bins are:<br/>
     * <pre>
     *    BIN_FRAME_SETUP
     *    BIN_2D_BACKGROUND_SETUP
     *    BIN_2D_BACKGROUND
     *    BIN_2D_BACKGROUND_FINISH
     *    BIN_3D_SETUP
     *    BIN_3D
     *    BIN_3D_FINISH
     *    BIN_2D_FOREGROUND_SETUP
     *    BIN_2D_FOREGROUND
     *    BIN_2D_FOREGROUND_FINISH
     *    BIN_ARBITRARY
     *    BIN_FRAME_FINISH
     * <pre/>
     *
     */
    public void setupDefaultRenderbins() {
        final int myBinSize = 200;
        {
            RenderBin myBin = new RenderBin(7);
            _myBin.add(myBin);
            if (bin(BIN_FRAME_SETUP) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_FRAME_SETUP);
            }
            bin(BIN_FRAME_SETUP).add(_myEvent);
            bin(BIN_FRAME_SETUP).add(_myFramesetup);
        }

        {
            RenderBin myBin = new RenderBin(2);
            _myBin.add(myBin);
            if (bin(BIN_2D_BACKGROUND_SETUP) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_2D_BACKGROUND_SETUP);
            }
            bin(BIN_2D_BACKGROUND_SETUP).add(_myDrawablefactory.orthoSetup());
            bin(BIN_2D_BACKGROUND_SETUP).add(_myOrigin);
        }

        {
            ShapeBin my2DBackgroundBin = new ShapeBin(myBinSize);
            _myBin.add(my2DBackgroundBin);
            if (bin(BIN_2D_BACKGROUND) != my2DBackgroundBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_2D_BACKGROUND);
            }
            my2DBackgroundBin.setSortStyle(SHAPEBIN_SORT_BY_Z_POSITION);
            my2DBackgroundBin.setSortFlag(true);
        }

        {
            RenderBin myBin = new RenderBin(1);
            _myBin.add(myBin);
            if (bin(BIN_2D_BACKGROUND_FINISH) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_2D_BACKGROUND_FINISH);
            }
            bin(BIN_2D_BACKGROUND_FINISH).add(_myDrawablefactory.orthoFinish());
        }

        {
            RenderBin myBin = new RenderBin(10);
            _myBin.add(myBin);
            if (bin(BIN_3D_SETUP) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_3D_SETUP);
            }
            bin(BIN_3D_SETUP).add(_myCamera);
            bin(BIN_3D_SETUP).add(_myLight);
            bin(BIN_3D_SETUP).add(_myFog);
        }

        {
            TwoSidedBin my3DBin = new TwoSidedBin(myBinSize);
            _myBin.add(my3DBin);
            if (bin(BIN_3D) != my3DBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_3D);
            }
            my3DBin.setSortStyle(SHAPEBIN_SORT_BY_Z_DISTANCE_TO_CAMERAPLANE);
            my3DBin.setSortFlag(true);
        }
        {
            RenderBin myBin = new RenderBin(10);
            _myBin.add(myBin);
            if (bin(BIN_3D_FINISH) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_3D_FINISH);
            }
            bin(BIN_3D_FINISH).add(_myBin3DFinish);
        }
        {
            RenderBin myBin = new RenderBin(2);
            _myBin.add(myBin);
            if (bin(BIN_2D_FOREGROUND_SETUP) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_2D_FOREGROUND_SETUP);
            }
            bin(BIN_2D_FOREGROUND_SETUP).add(_myDrawablefactory.orthoSetup());
            bin(BIN_2D_FOREGROUND_SETUP).add(_myOrigin);
        }
        {
            ShapeBin my2DForegroundBin = new ShapeBin(myBinSize);
            _myBin.add(my2DForegroundBin);
            if (bin(BIN_2D_FOREGROUND) != my2DForegroundBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_2D_FOREGROUND);
            }
            my2DForegroundBin.setSortStyle(SHAPEBIN_SORT_BY_Z_POSITION);
            my2DForegroundBin.setSortFlag(true);
        }
        {
            RenderBin myBin = new RenderBin(1);
            _myBin.add(myBin);
            if (bin(BIN_2D_FOREGROUND_FINISH) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_2D_FOREGROUND_FINISH);
            }
            bin(BIN_2D_FOREGROUND_FINISH).add(_myDrawablefactory.orthoFinish());
        }
        {
            RenderBin myBin = new RenderBin(5);
            _myBin.add(myBin);
            if (bin(BIN_ARBITRARY) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_ARBITRARY);
            }
        }
        {
            RenderBin myBin = new RenderBin(2);
            _myBin.add(myBin);
            if (bin(BIN_FRAME_FINISH) != myBin) {
                System.err.println("### ERROR @ BasicRenderer / setting up bins / " + BIN_FRAME_FINISH);
            }
            bin(BIN_FRAME_FINISH).add(_myFramefinish);
            bin(BIN_FRAME_FINISH).add(_myDisposer);
        }
    }

    public String toString() {
        StringBuffer myStringBuffer = new StringBuffer("### INFO\nobjects in renderbins:\n");
        Drawable[] myDrawables = bin().getDataRef();
        for (int i = 0; i < bin().size(); ++i) {
            if (myDrawables[i] != null) {
                myStringBuffer.append("bin #");
                myStringBuffer.append(i);
                myStringBuffer.append('\t');
                myStringBuffer.append('\n');
                myStringBuffer.append(myDrawables[i]);
            } else {
                myStringBuffer.append("bin <null>\n");
            }
        }
        return myStringBuffer.toString();
    }

    /**
     * schedule an object for disposing.
     * @param theDisposable Disposable
     */
    public void dispose(final Disposable theDisposable) {
        _myDisposer.addDisposable(theDisposable);
    }

    public Disposer disposer() {
        return _myDisposer;
    }

    /**
     *
     * @param theBin Bin
     */
    public final void setBinRef(Bin theBin) {
        _myBin = theBin;
    }

    /**
     *
     * @param theFog Fog
     */
    public final void setFogRef(Fog theFog) {
        _myFog = theFog;
    }

    /**
     * return a reference to the current fog.
     * @return Fog
     */
    public final Fog fog() {
        return _myFog;
    }

    /**
     *
     * @param theLight Light
     */
    public final void setLightRef(Light theLight) {
        _myLight = theLight;
    }

    /**
     * return a reference to the current light.
     * @return Light
     */
    public final Light light() {
        return _myLight;
    }

    /**
     *
     * @param theOrigin Origin
     */
    public final void setOriginRef(Origin theOrigin) {
        _myOrigin = theOrigin;
    }

    /**
     * handles the origin in the 2D bins.
     * @return Origin
     */
    public final Origin origin() {
        return _myOrigin;
    }

    /**
     *
     * @return FrameSetup
     */
    public final FrameSetup framesetup() {
        return _myFramesetup;
    }

    /**
     *
     * @param theFrameSetup FrameSetup
     */
    public final void setFramesetupRef(FrameSetup theFrameSetup) {
        _myFramesetup = theFrameSetup;
    }

    /**
     *
     * @return FrameFinish
     */
    public final FrameFinish framefinish() {
        return _myFramefinish;
    }

    /**
     *
     * @param theFrameFinish FrameFinish
     */
    public final void setFramefinishRef(FrameFinish theFrameFinish) {
        _myFramefinish = theFrameFinish;
    }

    /**
     *
     * @param theFrameFinish FrameFinish
     */
    public final void set3DFinishRef(Bin3DFinish theBin3DFinish) {
        _myBin3DFinish = theBin3DFinish;
    }

    /**
     * get reference to current camera.
     * @return Camera
     */
    public final Camera camera() {
        return _myCamera;
    }

    /**
     * set the reference to the camera.
     * @param theCamera Camera
     */
    public final void setCameraRef(Camera theCamera) {
        _myCamera = theCamera;
    }

    /**
     * replace the current camera.
     * @param theCamera Camera
     */
    public final void replaceCamera(Camera theCamera) {
        if (_myCamera != theCamera) {
            bin(BIN_3D_SETUP).replace(_myCamera, theCamera);
            _myCamera = theCamera;
        }
    }

    /**
     * override this method to receive mouse events.
     * @param x int
     * @param y int
     * @param thePressedMouseButton int
     */
    public void mousePressed(int x, int y, int thePressedMouseButton) {
    }

    /**
     * override this method to receive mouse events.
     * @param x int
     * @param y int
     * @param thePressedMouseButton int
     */
    public void mouseReleased(int x, int y, int thePressedMouseButton) {
    }

    /**
     * override this method to receive mouse events.
     * @param x int
     * @param y int
     * @param thePressedMouseButton int
     */
    public void mouseDragged(int x, int y, int thePressedMouseButton) {
    }

    /**
     * override this method to receive key events.
     * @param theKey char
     * @param theKeyCode int
     */
    public void keyPressed(char theKey, int theKeyCode) {
    }

    /**
     * override this method to receive key events.
     * @param theKey char
     * @param theKeyCode int
     */
    public void keyReleased(char theKey, int theKeyCode) {
    }
}
