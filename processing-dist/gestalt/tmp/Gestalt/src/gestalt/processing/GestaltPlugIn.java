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


package gestalt.processing;

import gestalt.Gestalt;
import gestalt.context.Display;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.input.EventHandler;
import gestalt.material.texture.bitmap.IntegerBitmap;
import gestalt.render.BasicRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.shape.DrawableFactory;
import gestalt.util.CameraMover;
import gestalt.util.FPSCounter;
import gestalt.util.JoglUtil;

import mathematik.TransformMatrix4f;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


/*
 * ADDITIONAL NOTES. gestalt and p5 are basically two different worlds that
 * share the same OpenGL context. first processing is drawn then gestalt.
 * fortunately this will only become visible in situations where transparency,
 * copying pixels from the framebuffer or 2D modes are involved. i chose this
 * drawing order for several reasons. one is that you can still use background()
 * to clear the framebuffer and an other is that advanced pixel operations on
 * the framebuffer can be invoked from within the gestalt context and still
 * affect pixels drawn by processing.
 */
public class GestaltPlugIn
        extends BasicRenderer {

    public static boolean SKIP_FIRST_FRAME = false;

    public boolean copybackgroundcolor = false;

    private PApplet parent;

    private DisplayCapabilities _myDisplayCapabilites;

    private GLContext _myGLContext;

    private boolean _myDrawBeforeP5;

    private boolean _myIsEnabled;

    private Vector<GLFragments> _myPreGLDrawables;

    private Vector<GLFragments> _myPostGLDrawables;

    private boolean _myUseCameraMover;

    private FPSCounter _myFPSCounter;

    public GestaltPlugIn(PApplet theParent) {
        this(theParent, true);
    }

    public GestaltPlugIn(PApplet theParent, boolean theMakeP5Friendly) {
        parent = theParent;
        _myDrawBeforeP5 = false;
        _myIsEnabled = true;
        _myUseCameraMover = false;

        if (!(parent.g instanceof PGraphicsOpenGL)) {
            System.err.println("### ERROR @ GestaltPlugIn / "
                    + "context is not of type OPENGL. / "
                    + "use 'size(width, height, OPENGL)'.");
        }

        /* setup gestalt */
        _myGLContext = new GLContext();
        _myDisplayCapabilites = new DisplayCapabilities();
        _myDisplayCapabilites.width = parent.width;
        _myDisplayCapabilites.height = parent.height;
        setBackgroundColor(_myDisplayCapabilites);

        /* these capabilities don t make sense in the processing context
         *
         * name
         * undecorated
         * fullscreen
         * centered
         * antialiasinglevel
         * cursor
         * headless
         *
         */

        create(_myDisplayCapabilites);
        if (theMakeP5Friendly) {
            makeP5Friendly();
        }

        /* grab opengl references for the first time */
        /** @todo JSR-231 */
        _myGLContext.gl = ((PGraphicsOpenGL)parent.g).gl;
        _myGLContext.glu = ((PGraphicsOpenGL)parent.g).glu;

        /* register callbacks */
        parent.registerPre(this);
        parent.registerDraw(this);
        parent.registerPost(this);
        parent.registerDispose(this);

        /* pre and post fragments */
        _myPreGLDrawables = new Vector<GLFragments>();
        _myPostGLDrawables = new Vector<GLFragments>();
    }

    public void unplug() {
        bin().clear();
        parent.unregisterPre(this);
        parent.unregisterDraw(this);
        parent.unregisterPost(this);
        parent.unregisterDispose(this);
    }

    public void enable() {
        _myIsEnabled = true;
    }

    public void disable() {
        _myIsEnabled = false;
    }

    protected void drawBeforeProcessing(boolean theDrawBeforeP5) {
        _myDrawBeforeP5 = theDrawBeforeP5;
    }

    public boolean hasDrawError() {
        return display().hasDrawError();
    }


    /* gestalt */
    @SuppressWarnings("deprecation")
    public final void create(DisplayCapabilities theDisplayCapabilities) {
        if (!_myIsInitalized) {
            _myIsInitalized = true;

            /* core */
            setDrawablefactoryRef(DrawableFactory.getFactory());
            setEventRef(drawablefactory().eventhandler());

            setDisplayRef(new P5Display());
            display().initialize();

            setBinRef(new RenderBin(20));

            /* plugins */
            createPlugins(theDisplayCapabilities);

            /* create default structure */
            setupDefaultRenderbins();

            /* register event listener */
            /** @todo JSR-231 -- gl here are you */
            parent.addMouseMotionListener(event());
            parent.addMouseWheelListener(event());
            parent.addMouseListener(event());
            parent.addKeyListener(event());
//            ( (PGraphicsOpenGL) parent.g).canvas.addMouseMotionListener( (JoglEventHandler) event());
//            ( (PGraphicsOpenGL) parent.g).canvas.addMouseWheelListener( (JoglEventHandler) event());
//            ( (PGraphicsOpenGL) parent.g).canvas.addMouseListener( (JoglEventHandler) event());
//            ( (PGraphicsOpenGL) parent.g).canvas.addKeyListener( (JoglEventHandler) event());
        }
    }

    private void makeP5Friendly() {
        /*
         * align the values of input events from gestalt and processing. gestalt
         * usually has (0, 0, 0) in the middle of the screen and is looking the
         * other way. basically gestalt ist setup like opengl and processing is
         * setup like java awt.
         */
        EventHandler.EVENT_FLIP_MOUSE_Y = false;
        EventHandler.EVENT_CENTER_MOUSE = false;

        camera().position().x += parent.width / 2;
        camera().position().y += parent.height / 2;
        camera().position().z *= -1;
        camera().rotation().x += Gestalt.PI;
        /* processing default setup */
        camera().position().z = -(parent.height / 2.0f) / PApplet.tan(PI * 60.0f / 360.0f);
        camera().fovy = PApplet.degrees(PI / 3.0f);
        camera().nearclipping = -camera().position().z / 10.0f;
        camera().farclipping = -camera().position().z * 10.0f;

        origin().position.x -= parent.width / 2;
        origin().position.y += parent.height / 2;
        origin().rotation.x += Gestalt.PI;

        /* prevent gestalt from deleting the framebuffer */
        framesetup().colorbufferclearing = false;
        framesetup().depthbufferclearing = true;

        /* copy processing background color each frame */
        copybackgroundcolor = true;
    }

    private void setBackgroundColor(DisplayCapabilities theDisplayCapabilities) {
        theDisplayCapabilities.backgroundcolor.r = parent.red(((PGraphicsOpenGL)parent.g).backgroundColor) / 255f;
        theDisplayCapabilities.backgroundcolor.g = parent.green(((PGraphicsOpenGL)parent.g).backgroundColor) / 255f;
        theDisplayCapabilities.backgroundcolor.b = parent.blue(((PGraphicsOpenGL)parent.g).backgroundColor) / 255f;
        theDisplayCapabilities.backgroundcolor.a = 1;
    }


    /* p5 callbacks */
    public void pre() {
        if (_myIsEnabled) {
            /* draw pre fragments */
            if (_myGLContext.gl != null) {
                for (int i = 0; i < _myPreGLDrawables.size(); i++) {
                    final GLFragments myFragment = _myPreGLDrawables.get(i);
                    myFragment.draw(getGL());
                }
            }

            if (_myDrawBeforeP5) {
                display().display();
            }
        }
    }

    public void draw() {
        final float myDeltaTime = 1.0f / parent.frameRate;

        if (_myUseCameraMover) {
            CameraMover.handleKeyEvent(camera(), event(), myDeltaTime);
        }

        if (_myFPSCounter != null) {
            _myFPSCounter.loop(myDeltaTime);
        }

        if (_myIsEnabled && !_myDrawBeforeP5) {
            display().display();
        }
    }

    public void post() {
        /* draw post fragments */
        if (_myGLContext.gl != null) {
            for (int i = 0; i < _myPostGLDrawables.size(); i++) {
                if (_myPostGLDrawables.get(i) instanceof GLFragments) {
                    final GLFragments myFragment = (GLFragments)_myPostGLDrawables.get(i);
                    myFragment.draw(getGL());
                }
            }
        }
    }

    public void dispose() {
        display().finish();
    }

    public void size(int width, int height) {
        _myDisplayCapabilites.width = width;
        _myDisplayCapabilites.height = height;
    }

    /**
     * use with care!
     * using the instance might not work at all times, as opengl
     * needs to be in the right mood to receive calls.
     *
     * @return GL
     */
    public GL getGL() {
        return _myGLContext.gl;
    }

    /**
     * use with care!
     * using the instance might not work at all times, as opengl
     * needs to be in the right mood to receive calls.
     *
     * @return GLU
     */
    public GLU getGLU() {
        return _myGLContext.glu;
    }

    /**
     * applies the state of a gestalt camera to the processing
     * model-view-matrix.
     *
     * @param theCamera Camera
     */
    public void applyCamera(Camera theCamera) {
        applyCamera(parent.g, theCamera);
    }

    /**
     * applies the state of a gestalt camera to the processing
     * model-view-matrix.
     *
     * @param g PGraphics
     * @param theCamera Camera
     */
    public static void applyCamera(PGraphics g, Camera theCamera) {

        theCamera.updateRotationMatrix();

        TransformMatrix4f myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myTransform.rotation.set(theCamera.getInversRotationMatrix());
        float[] _myArrayRepresentation = myTransform.toArray();

        g.resetMatrix();

        g.scale(1, -1, 1);

        g.applyMatrix(_myArrayRepresentation[0],
                      _myArrayRepresentation[1],
                      _myArrayRepresentation[2],
                      _myArrayRepresentation[3],
                      _myArrayRepresentation[4],
                      _myArrayRepresentation[5],
                      _myArrayRepresentation[6],
                      _myArrayRepresentation[7],
                      _myArrayRepresentation[8],
                      _myArrayRepresentation[9],
                      _myArrayRepresentation[10],
                      _myArrayRepresentation[11],
                      _myArrayRepresentation[12],
                      _myArrayRepresentation[13],
                      _myArrayRepresentation[14],
                      _myArrayRepresentation[15]);

        g.translate(-theCamera.position().x,
                    -theCamera.position().y,
                    -theCamera.position().z);
    }

    /**
     * this method activates a camera mover.
     * ( see also gestalt.util.CameraMover )
     *
     * @param theState boolean
     */
    public void cameramover(boolean theState) {
        _myUseCameraMover = theState;
    }

    public FPSCounter fpscounter(boolean theState) {
        if (theState) {
            if (_myFPSCounter == null) {
                _myFPSCounter = new FPSCounter();
                _myFPSCounter.setInterval(60);
                _myFPSCounter.display().position.set(displaycapabilities().width / -2 + 20,
                                                     displaycapabilities().height / 2 - 20);
                _myFPSCounter.display().color.set(0.5f);
                bin(BIN_2D_FOREGROUND).add(_myFPSCounter.display());
            }
        } else {
            if (_myFPSCounter != null) {
                bin(BIN_2D_FOREGROUND).remove(_myFPSCounter.display());
                _myFPSCounter = null;
            }
        }
        return _myFPSCounter;
    }

    /**
     * create a gestalt 'Bitmap' from a processing 'PImage'.<br/>
     * note that this method only copies the reference to the pixel data of
     * the PImage.
     *
     * @param thePImage PImage
     * @return IntegerBitmap
     */
    public static IntegerBitmap createGestaltBitmap(final PImage thePImage) {
        return new IntegerBitmap(thePImage.pixels,
                                 thePImage.width,
                                 thePImage.height,
                                 Gestalt.BITMAP_COMPONENT_ORDER_BGRA);
    }


    /* display */
    private class P5Display
            implements Display {

        private boolean _hasDrawError;

        private GL gl;

        private GLU glu;

        public void finish() {
            /* we might need to clean up in here */
        }

        public void initialize() {
            /* not used */
        }

        public boolean isDone() {
            /* not used */
            return false;
        }

        public boolean hasDrawError() {
            return _hasDrawError;
        }

        public GLContext glcontext() {
            return _myGLContext;
        }

        public void updateDisplayCapabilities() {
            /**
             * all the context related aspects are handled by p5
             * so we don t need to update displaycapabilites.
             */
        }

        public void display() {
            /* check for errors */
            _hasDrawError = false;

            /* align background colors */
            if (copybackgroundcolor) {
                setBackgroundColor(_myDisplayCapabilites);
            }

            /* update opengl references */
            /** @todo JSR-231 -- gl here are you */
            gl = ((PGraphicsOpenGL)parent.g).gl;
            glu = ((PGraphicsOpenGL)parent.g).glu;

            /* write data to gl context */
            _myGLContext.gl = gl;
            _myGLContext.glu = glu;
            /** @todo JSR-231 -- */
//            (  _myGLContext).drawable = ( (PGraphicsOpenGL) parent.g).canvas;
            _myGLContext.event = event();
            _myGLContext.camera = camera();
            _myGLContext.displaycapabilities = displaycapabilities();

            /* store opengl states */
            if (!SKIP_FIRST_FRAME) {
                gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
            }
            gl.glPushMatrix();

            /* handle drawables */
            if (_myIsInitalized) {
                if (bin() != null) {
                    bin().draw(_myGLContext);
                }
            }

            /* restore opengl states */
            if (!SKIP_FIRST_FRAME) {
                gl.glPopAttrib();
            } else {
                SKIP_FIRST_FRAME = false;
            }
            gl.glPopMatrix();

            /* check for errors */
            _hasDrawError = JoglUtil.printGLError(gl,
                                                  glu,
                                                  "GestaltPlugIn",
                                                  true);
        }

        public DisplayCapabilities displaycapabilities() {
            return _myDisplayCapabilites;
        }
    }


    /* fullscreen helper */
    public static void decoration(PApplet theParent, boolean theDecoration) {
        theParent.frame.dispose();
        theParent.frame.setUndecorated(!theDecoration);
        theParent.frame.setVisible(true);
    }

    public void decoration(boolean theDecoration) {
        decoration(parent, theDecoration);
    }

    public void fullscreen(boolean theSwitchResolution) {
        fullscreen(parent, theSwitchResolution);
    }

    public static void fullscreen(PApplet theParent, boolean theSwitchResolution) {
        System.err.println("### WARNING / fullscreen might not work in processing 148++");

        final GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

//        Component[] x = theParent.frame.getComponents();
//        for (int i = 0; i < x.length; i++) {
//            Component component = x[i];
//            System.out.println("component " + component);
//            System.out.println(component.getClass());
//            if (component == theParent) {
//                System.out.println("YEAH!");
//            }
//        }

        /* remove decoration */
        if (myGraphicsDevice.isFullScreenSupported()) {
            try {
                theParent.frame.setVisible(false);
                theParent.frame.remove(theParent);

                theParent.frame = new Frame();
                theParent.frame.setLayout(null);
                theParent.frame.setVisible(true);
                theParent.frame.setUndecorated(true);
                theParent.frame.setSize(theParent.width, theParent.height);
                theParent.frame.add(theParent);

                myGraphicsDevice.setFullScreenWindow(theParent.frame);

//                if (theParent.frame.isDisplayable()) {
//                    theParent.frame.dispose();
//                }
//                theParent.frame.setUndecorated(true);
//                theParent.frame.add(theParent);
            } catch (Exception theException) {
                System.err.println("### WARNING / can t remove decoration from frame. / " + theException);
            }
        } else {
            System.err.println("### WARNING / fullscreen is not supported.");
        }


        /* try to switch resolution -- might fail any time */
        if (theSwitchResolution) {
            if (myGraphicsDevice.isDisplayChangeSupported()) {
                DisplayMode myDisplayMode = new DisplayMode(
                        theParent.width,
                        theParent.height,
                        32,
                        DisplayMode.REFRESH_RATE_UNKNOWN);
                myGraphicsDevice.setDisplayMode(myDisplayMode);
            }
        }

        /* reposition frame just in case */
        theParent.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        theParent.setLocation((theParent.frame.getWidth() - theParent.width) / 2, (theParent.frame.getHeight() - theParent.height) / 2);

//        theParent.frame.setLocation(0, 0);
//        try {
//            /** @todo this is only tested on OS X */
//            Panel myPanel = ((Panel)(theParent.frame.getComponents()[0]));
//            /** @todo could also center this component */
//            if (theSwitchResolution) {
//                myPanel.setLocation(0, 0);
//            } else {
//                /* center display */
//                Rectangle mySize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
//                myPanel.setLocation((mySize.width - theParent.width) / 2,
//                                    (mySize.height - theParent.height) / 2);
//                /* add black background -- glcanvas is heavy weight, should be always on top */
//                Panel myBlackPanel = new Panel();
//                theParent.frame.add(myBlackPanel);
//                myBlackPanel.setSize(mySize.width, mySize.height);
//                myBlackPanel.setBackground(Color.BLACK);
//            }
//
////            GLCanvas myGLCanvas = (GLCanvas) ( myPanel.getComponents()[0]);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public void noFullscreen() {
        GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        myGraphicsDevice.setFullScreenWindow(null);

        try {
            parent.frame.dispose();
            parent.frame.setUndecorated(false);
            parent.frame.setVisible(true);
        } catch (Exception theException) {
            System.err.println("### WARNING / can t add decorataion to frame. / " + theException);
        }
    }


    /* collections of objects that are rendered before and after the main render loop */
    public Vector<GLFragments> preDrawables() {
        return _myPreGLDrawables;
    }

    public Vector<GLFragments> postDrawables() {
        return _myPostGLDrawables;
    }

    public interface GLFragments {

        void draw(GL gl);
    }
}
