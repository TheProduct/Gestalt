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


package gestalt.context;

import gestalt.Gestalt;
import gestalt.input.EventHandler;
import gestalt.render.BasicRenderer;
import gestalt.render.MinimalRenderer;
import gestalt.util.JoglUtil;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;


public class JoglDisplay
        implements Display,
                   GLEventListener {

    public static boolean SET_AUTO_SWAP_BUFFER_MODE = true;

    public static boolean SET_IGNORE_REPAINT = true;

    public static boolean ENABLE_STENCIL_BUFFER = false;

    public static boolean ENABLE_ACCUMULATION_BUFFER = false;

    public static boolean ENABLE_STEREO_VIEW = false;

    public static boolean PRINT_OPENGL_INFO = false;

    public static boolean PRINT_WARNINGS = true;

    public static boolean VERBOSE = true;

    public static boolean RESIZABLE = false;

    public static int BITS_COLORBUFFER_PER_COMPONENT = 8;

    public static int BITS_DEPTHBUFFER = 24;

    public static int BITS_STENCILBUFFER = 8;

    public static boolean SWITCH_RESOLUTION = true;

    public static boolean SYNC_TO_VBLANK = false;

    protected final MinimalRenderer _myRenderer;

    protected final EventHandler _myEventHandler;

    protected final GLContext _myGLContext;

    protected int _myRefreshRate;

    protected boolean _myInitialized;

    protected boolean _myIsDone;

    protected boolean _hasDrawError;

    protected GLCanvas _myCanvas;

    private final Frame _myFrame;

    private DisplayMode _myInitalDisplayMode;

    public static boolean FULLSCREEN_WINDOWS_HACK = false;

    public JoglDisplay(DisplayCapabilities theDisplayCapabilities,
                       MinimalRenderer theRenderer,
                       EventHandler theEventHandler) {

        _myInitialized = false;
        _myIsDone = false;
        _hasDrawError = false;

        _myRenderer = theRenderer;
        _myEventHandler = theEventHandler;
        _myGLContext = new GLContext();
        _myGLContext.displaycapabilities = theDisplayCapabilities;
        _myRefreshRate = DisplayMode.REFRESH_RATE_UNKNOWN;

        /* setup jogl */
        GLCapabilities myGLCapabilities = new GLCapabilities();

        /* color depth */
        myGLCapabilities.setRedBits(BITS_COLORBUFFER_PER_COMPONENT);
        myGLCapabilities.setBlueBits(BITS_COLORBUFFER_PER_COMPONENT);
        myGLCapabilities.setGreenBits(BITS_COLORBUFFER_PER_COMPONENT);
        myGLCapabilities.setAlphaBits(BITS_COLORBUFFER_PER_COMPONENT);
        myGLCapabilities.setDepthBits(BITS_DEPTHBUFFER);

        /* stencil buffer */
        if (ENABLE_STENCIL_BUFFER) {
            myGLCapabilities.setStencilBits(BITS_STENCILBUFFER);
        }

        /* stereo buffer */
        myGLCapabilities.setStereo(ENABLE_STEREO_VIEW);

        /* accumulation buffer */
        if (ENABLE_ACCUMULATION_BUFFER) {
            myGLCapabilities.setAccumRedBits(BITS_COLORBUFFER_PER_COMPONENT);
            myGLCapabilities.setAccumGreenBits(BITS_COLORBUFFER_PER_COMPONENT);
            myGLCapabilities.setAccumBlueBits(BITS_COLORBUFFER_PER_COMPONENT);
            myGLCapabilities.setAccumAlphaBits(BITS_COLORBUFFER_PER_COMPONENT);
        }

        /* full scene antialiasing */
        if (_myGLContext.displaycapabilities.antialiasinglevel > 0) {
            myGLCapabilities.setSampleBuffers(true);
            myGLCapabilities.setNumSamples(_myGLContext.displaycapabilities.antialiasinglevel);
        }

        /* setup gldrawable */
        _myCanvas = new GLCanvas(myGLCapabilities, null, null, null);

        _myCanvas.setAutoSwapBufferMode(SET_AUTO_SWAP_BUFFER_MODE);
        if (!SET_AUTO_SWAP_BUFFER_MODE && PRINT_WARNINGS) {
            System.err.println("### INFO @ " + this.getClass() + " / disabled SET_AUTO_BUFFER_MODE");
        }

        _myCanvas.setIgnoreRepaint(SET_IGNORE_REPAINT);
        if (!SET_IGNORE_REPAINT && PRINT_WARNINGS) {
            System.err.println("### INFO @ " + this.getClass() + " / disabled SET_IGNORE_REPAINT");
        }

        addListeners();

        if (FULLSCREEN_WINDOWS_HACK) {
            _myFrame = new Frame(theDisplayCapabilities.name);
            window_fullscreen_hack();
        } else {
            if (!theDisplayCapabilities.headless) {
                /* get frame */
                GraphicsConfiguration myDefaultGraphicsConfiguration;
                if (_myGLContext.displaycapabilities.device == Gestalt.UNDEFINED) {
                    myDefaultGraphicsConfiguration =
                            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                } else {
                    try {
                        GraphicsDevice[] myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                        myDefaultGraphicsConfiguration = myGraphicsDevice[_myGLContext.displaycapabilities.device].getDefaultConfiguration();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        myDefaultGraphicsConfiguration =
                                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().
                                getDefaultConfiguration();
                    }
                }
                _myFrame = new Frame(theDisplayCapabilities.name, myDefaultGraphicsConfiguration);
                _myFrame.setResizable(RESIZABLE);

                /* add window closing adapter */
                _myFrame.addWindowListener(new WindowAdapter() {

                    public void windowClosing(WindowEvent e) {
                        _myIsDone = true;
                    }
                });
            } else {
                _myFrame = null;
            }

            /* initialize from display capabilites */
            updateDisplayCapabilities();
            if (!theDisplayCapabilities.headless) {
                _myFrame.add(_myCanvas);
            }
            _myCanvas.requestFocus();
        }
    }

    public GLCanvas getGLCanvas() {
        return _myCanvas;
    }

    public void setGLCanvas(GLCanvas theCanvas) {
        removeListeners();
        _myCanvas = theCanvas;
        addListeners();
    }

    public Frame getFrame() {
        return _myFrame;
    }


    /* --> jogl obligations */
    public void init(GLAutoDrawable theDrawable) {
        final GL gl = theDrawable.getGL();
        final GLU glu = new GLU();
        (_myGLContext).gl = gl;
        (_myGLContext).glu = glu;

        /* Sync to vertical blank */
        if (SYNC_TO_VBLANK) {
            gl.setSwapInterval(1);
        } else {
            if (_myGLContext.displaycapabilities.synctovblank) {
                gl.setSwapInterval(1);
            }
        }

        if (PRINT_OPENGL_INFO) {
            printGLInfo(gl);
        }
        _myInitialized = true;
    }

    private void printGLInfo(GL gl) {
        System.out.println("\n\n-------------------------------------------------------");
        System.out.println("INIT GL IS: " + gl.getClass().getName());
        System.out.println("CANVAS GL IS: " + _myCanvas.getGL().getClass().getName());
        System.out.println("CANVAS GLU IS: " + new GLU().getClass().getName());
        System.out.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.out.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.out.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
        System.out.println("-------------------------------------------------------\n\n");
        JoglUtil.printExtensions(gl);
    }

    public void initialize() {
        /* do nothing */
    }

    public void finish() {
        removeListeners();
        if (_myCanvas != null) {
            _myCanvas.setEnabled(false);
            _myCanvas.setVisible(false);
        }
        if (_myFrame != null && !_myGLContext.displaycapabilities.headless) {
            if (_myCanvas != null) {
                _myFrame.remove(_myCanvas);
            }
            _myFrame.dispose();
            _myFrame.setEnabled(false);
            _myFrame.setVisible(false);
        }
        _myInitalDisplayMode = null;
    }

    private void updateGLContext(GLAutoDrawable theDrawable) {
        /* set opengl reference */
        (_myGLContext).gl = theDrawable.getGL();
        (_myGLContext).glu = new GLU();
        (_myGLContext).drawable = theDrawable;

        /** @todo this cast is not very elegant. */
        if (_myRenderer instanceof BasicRenderer) {
            BasicRenderer myRenderer = (BasicRenderer)_myRenderer;
            _myGLContext.event = myRenderer.event();
            _myGLContext.camera = myRenderer.camera();
            _myGLContext.displaycapabilities = myRenderer.displaycapabilities();
        }
    }

    public void display(GLAutoDrawable theDrawable) {
        /* check for errors */
        _hasDrawError = false;

        /* write data to gl context */
        updateGLContext(theDrawable);

        /* handle drawables */
        if (_myInitialized) {
            if (_myRenderer != null) {
                drawRenderbins(_myGLContext);
            }
        } else {
            System.err.println("### WARNING @ " + this.getClass() + " / display not initialized.");
        }

        final GL gl = theDrawable.getGL();
        gl.glFinish();

        /* check for errors */
        _hasDrawError = JoglUtil.printGLError(theDrawable.getGL(),
                                              new GLU(),
                                              "JoglDisplay",
                                              true);
    }

    public void display() {
        _myCanvas.display();
        /*
         * NOTE 2005 01 17 obviously the init(GLAutoDrawable drawable) method is
         * never called if we don t call the GLCanvas.display() method. this i
         * find a bit confusing because i d expect the init method to be called
         * right after the canvas was created. it seems that the first call to
         * GLCanvas.display() hit s the init method and all others just go to
         * the display method.
         *
         */
    }

    /**
     * never used.
     *
     * @param drawable GLAutoDrawable
     * @param modeChanged boolean
     * @param deviceChanged boolean
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    /**
     *
     * @param drawable GLAutoDrawable
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        /** @todo this has not been tested yet. */
        _myGLContext.displaycapabilities.height = height;
        _myGLContext.displaycapabilities.width = width;
    }

    public boolean isDone() {
        return _myIsDone;
    }

    public boolean hasDrawError() {
        return _hasDrawError;
    }

    public DisplayCapabilities displaycapabilities() {
        return _myGLContext.displaycapabilities;
    }

    public GLContext glcontext() {
        return _myGLContext;
    }

    private void window_fullscreen_hack() {
        _myFrame.setResizable(RESIZABLE);

        /* add window closing adapter */
        _myFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                _myIsDone = true;
            }
        });

        _myFrame.setUndecorated(true);
        _myFrame.add(_myCanvas);

        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        _myFrame.setLocation(0, 0);
        _myFrame.setSize(dimension);

        _myFrame.setBackground(new java.awt.Color(0, 0, 0, 255));

        /* get graphics device */
        final GraphicsDevice myGraphicsDevice = _myFrame.getGraphicsConfiguration().getDevice();
        myGraphicsDevice.setFullScreenWindow(_myFrame);

        _myFrame.setVisible(true);

        _myCanvas.setSize(displaycapabilities().width, displaycapabilities().height);
        _myCanvas.setLocation((dimension.width - _myGLContext.displaycapabilities.width) / 2,
                              (dimension.height - _myGLContext.displaycapabilities.height) / 2);

        _myCanvas.requestFocus();

        /* cursor */
        if (!_myGLContext.displaycapabilities.cursor) {
            _myFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().
                    createImage(new byte[] {}),
                                                                              new Point(0, 0),
                                                                              "null"));
        } else {
            _myFrame.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void updateDisplayCapabilities() {
        if (FULLSCREEN_WINDOWS_HACK) {
            return;
        }
        if (_myFrame != null && !_myGLContext.displaycapabilities.headless) {

//            /* move the canvas inside the frame */
//            if (!_myGLContext.displaycapabilities.undecorated) {
//                _myFrame.setSize(_myGLContext.displaycapabilities.width,
//                                 _myGLContext.displaycapabilities.height + _myFrame.getInsets().top);
//                _myCanvas.setLocation(_myGLContext.displaycapabilities.canvaslocation.x,
//                                      _myGLContext.displaycapabilities.canvaslocation.y + _myFrame.getInsets().top);
//            } else {
//                _myFrame.setSize(_myGLContext.displaycapabilities.width,
//                                 _myGLContext.displaycapabilities.height);
//                _myCanvas.setLocation(_myGLContext.displaycapabilities.canvaslocation.x,
//                                      _myGLContext.displaycapabilities.canvaslocation.y);
//            }

            /* handle window location */
            if (_myGLContext.displaycapabilities.location != null) {
                _myFrame.setLocation(_myGLContext.displaycapabilities.location.x,
                                     _myGLContext.displaycapabilities.location.y);
            } /* center the window in the middle of the screen */ else if (_myGLContext.displaycapabilities.centered) {
                if (!_myGLContext.displaycapabilities.fullscreen) {
                    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                    _myFrame.setLocation((dimension.width - _myGLContext.displaycapabilities.width) / 2,
                                         (dimension.height - _myGLContext.displaycapabilities.height) / 2);
                } else {
                    _myFrame.setLocation(0, 0);
                }
            }

            /* get graphics device */
            GraphicsDevice myGraphicsDevice;
            if (_myGLContext.displaycapabilities.device == Gestalt.UNDEFINED) {
                myGraphicsDevice = _myFrame.getGraphicsConfiguration().getDevice();
            } else {
                try {
                    myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().
                            getScreenDevices()[_myGLContext.displaycapabilities.device];
                } catch (Exception ex) {
                    myGraphicsDevice = _myFrame.getGraphicsConfiguration().getDevice();
                }
            }

            /* store inital display mode */
            _myInitalDisplayMode = myGraphicsDevice.getDisplayMode();

            /* undecorated */
            if (!_myFrame.isDisplayable()) {
                _myFrame.setUndecorated(_myGLContext.displaycapabilities.undecorated);
            } else {
                _myFrame.dispose();
                _myFrame.setUndecorated(_myGLContext.displaycapabilities.undecorated);
                _myFrame.setVisible(true);
            }

            /* set fullscreen */
            if (_myGLContext.displaycapabilities.fullscreen && myGraphicsDevice.getFullScreenWindow() != _myFrame) {
                if (myGraphicsDevice.isFullScreenSupported()) {
                    if (_myFrame.isDisplayable()) {
                        _myFrame.dispose();
                    }
                    _myFrame.setUndecorated(true);

                    /**
                     * @todo there seems to be an issue with this code.
                     * it does not selecting the right divide for fullscreen mode.
                     * java / sun bug?
                     */
                    myGraphicsDevice.setFullScreenWindow(_myFrame);
                    if (SWITCH_RESOLUTION) {
                        if (_myGLContext.displaycapabilities.switchresolution) {
                            if (myGraphicsDevice.isDisplayChangeSupported()) {
                                final DisplayMode myDisplayMode = new DisplayMode(_myGLContext.displaycapabilities.width,
                                                                                  _myGLContext.displaycapabilities.height,
                                                                                  BITS_COLORBUFFER_PER_COMPONENT * 4,
                                                                                  _myRefreshRate);
                                myGraphicsDevice.setDisplayMode(myDisplayMode);
                            } else {
                                System.err.println(
                                        "### ERROR @ JoglDisplay / resolution switching is not supported on this graphicsDevice");
                            }
                        } else {
                            final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                            _myFrame.setLocation((dimension.width - _myGLContext.displaycapabilities.width) / 2,
                                                 (dimension.height - _myGLContext.displaycapabilities.height) / 2);
                        }
                    }
                    _myFrame.setVisible(true);
                } else {
                    System.err.println("### ERROR @ JoglDisplay /  fullscreen is not supported on this graphicsDevice");
                }
            } else if (!_myGLContext.displaycapabilities.fullscreen && myGraphicsDevice.getFullScreenWindow() != null) {
                /* restore resolution if fullscreen was switched */
                if (SWITCH_RESOLUTION) {
                    if (_myGLContext.displaycapabilities.switchresolution) {
                        if (myGraphicsDevice.isDisplayChangeSupported()) {
                            myGraphicsDevice.setDisplayMode(_myInitalDisplayMode);
                        }
                    }
                }
                if (myGraphicsDevice.isFullScreenSupported()) {
                    myGraphicsDevice.setFullScreenWindow(null);
                }
            }

            /* cursor */
            if (!_myGLContext.displaycapabilities.cursor) {
                _myCanvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().
                        createImage(new byte[] {}),
                                                                                   new Point(0, 0),
                                                                                   "null"));
            } else {
                _myCanvas.setCursor(Cursor.getDefaultCursor());
            }

            /* make frame visible */
            if (!_myFrame.isDisplayable()) {
                /**
                 * @todo make a frame visible and active. there are still
                 * several things unclear. hmm. for example how to change
                 * the decoration of a frame that is already visible. or
                 * how to properly dispose a frame.
                 */
                _myFrame.setVisible(true);
            }

            /* move the canvas inside the frame */
            if (!_myGLContext.displaycapabilities.undecorated) {
                _myFrame.setSize(_myGLContext.displaycapabilities.width,
                                 _myGLContext.displaycapabilities.height + _myFrame.getInsets().top);
                _myCanvas.setLocation(_myGLContext.displaycapabilities.canvaslocation.x,
                                      _myGLContext.displaycapabilities.canvaslocation.y + _myFrame.getInsets().top);
            } else {
                _myFrame.setSize(_myGLContext.displaycapabilities.width,
                                 _myGLContext.displaycapabilities.height);
                _myCanvas.setLocation(_myGLContext.displaycapabilities.canvaslocation.x,
                                      _myGLContext.displaycapabilities.canvaslocation.y);
            }

            /* set frame background color */
            _myFrame.setBackground(new java.awt.Color(Math.min(1f, Math.max(0f,
                                                                            _myGLContext.displaycapabilities.backgroundcolor.r)),
                                                      Math.min(1f, Math.max(0f,
                                                                            _myGLContext.displaycapabilities.backgroundcolor.g)),
                                                      Math.min(1f, Math.max(0f,
                                                                            _myGLContext.displaycapabilities.backgroundcolor.b)),
                                                      Math.min(1f, Math.max(0f,
                                                                            _myGLContext.displaycapabilities.backgroundcolor.a))));
        }

        /* set size of canvas */
        _myCanvas.setSize(_myGLContext.displaycapabilities.width, _myGLContext.displaycapabilities.height);

        /* move the canvas inside the frame */
        /* --> moved to top */
    }

    private void drawRenderbins(GLContext theGLContext) {
        if (_myRenderer.bin() != null) {
            _myRenderer.bin().draw(theGLContext);
        }
    }

    private void addListeners() {
        if (_myCanvas != null) {
            _myCanvas.addGLEventListener(this);
            if (_myEventHandler != null) {
                _myCanvas.addMouseMotionListener(_myEventHandler);
                _myCanvas.addMouseWheelListener(_myEventHandler);
                _myCanvas.addMouseListener(_myEventHandler);
                _myCanvas.addKeyListener(_myEventHandler);
            }
        }
    }

    private void removeListeners() {
        if (_myCanvas != null) {
            _myCanvas.removeGLEventListener(this);
            if (_myEventHandler != null) {
                _myCanvas.removeMouseMotionListener(_myEventHandler);
                _myCanvas.removeMouseWheelListener(_myEventHandler);
                _myCanvas.removeMouseListener(_myEventHandler);
                /**
                 * @todo there seems to be a problem when removing the keylistener. funny.
                 */
                // _myCanvas.removeKeyListener(_myEventHandler);
            }
        }
    }
}
