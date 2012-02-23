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

import gestalt.G;
import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_DepthRGBA;
import gestalt.extension.quadline.QuadLine;
import gestalt.material.TexturePlugin;
import gestalt.shape.Disk;
import gestalt.shape.Plane;
import gestalt.util.FPSCounter;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class G5
        extends G {

    private static PApplet _myParent;

    public static void init_processing(Class<? extends PApplet> theClass) {
        PApplet.main(new String[] {theClass.getName()});
    }

    public static G5 setup(final PApplet theParent, boolean theMakeP5Friendly) {
        if (_myInstance == null) {
            _myInstance = new G5(theParent, theMakeP5Friendly);
            _myParent = theParent;
        }
        return (G5)_myInstance;
    }

    public static G5 setup(final PApplet theParent) {
        setup(theParent, true);
        return (G5)_myInstance;
    }

    protected static boolean die() {
        if (G.die() || _myParent == null) {
            System.err.println("### ERROR @ G5 / initialize gestalt first by calling 'Ge.setup(...)'.");
            return true;
        } else {
            return false;
        }
    }

    private G5(final PApplet theParent, boolean theMakeP5Friendly) {
        _myGestalt = new GestaltPlugIn(theParent, theMakeP5Friendly);
    }

    public static Model model(String theModelFile,
                              String theImageFile,
                              boolean theUseVBO) {
        return model(_myParent.createInput(theModelFile),
                     _myParent.createInput(theImageFile),
                     theUseVBO);
    }

    public static Model model(String theModelFile, boolean theUseVBO) {
        if (die()) {
            return null;
        }

        return model(_myParent.createInput(theModelFile), theUseVBO);
    }

    public static Plane plane(String theFile) {
        if (die()) {
            return null;
        }

        return plane(_myParent.createInput(theFile));
    }

    public static Disk disk(String theFile) {
        if (die()) {
            return null;
        }

        return disk(_myParent.createInput(theFile));
    }

    public static QuadLine quadline(String theFile) {
        if (die()) {
            return null;
        }

        final QuadLine myQuadLine = quadline(_myParent.createInput(theFile));

        return myQuadLine;
    }

    public static TexturePlugin texture(String theString) {
        if (die()) {
            return null;
        }

        return texture(_myParent.createInput(theString));
    }


    /* gl */
    public static GL gl() {
        if (die()) {
            return null;
        }
        return ((PGraphicsOpenGL)_myParent.g).gl;
    }

    public static GLU glu() {
        if (die()) {
            return null;
        }
        return ((PGraphicsOpenGL)_myParent.g).glu;
    }


    /* p5 specific */
    public static void decoration(boolean theDecoration) {
        if (die()) {
            return;
        }
        ((GestaltPlugIn)_myGestalt).decoration(theDecoration);
    }


    /* fullscreen */
    public static void fullscreen(boolean theSwitchResolution) {
        System.err.println("### WARNING / fullscreen() is under construction and probably does not work.");
        if (die()) {
            return;
        }
        ((GestaltPlugIn)_myGestalt).fullscreen(theSwitchResolution);
    }

    public static void fullscreen(PApplet theParent, boolean theSwitchResolution) {
        GestaltPlugIn.fullscreen(theParent, theSwitchResolution);
    }

    public static void noFullscreen() {
        if (die()) {
            return;
        }
        ((GestaltPlugIn)_myGestalt).noFullscreen();
    }

    /**
     * returns a reference to a texture that contains the current processing sketch.<br/>
     * gestalt MUST be intialized as processing-unfriendly.<br/>
     * note that this texture is sensible to the presentation mode.
     *
     * @param theParent PApplet
     * @param theWindowWidth int
     * @param theWindowHeight int
     * @param thePresentationModeFlag boolean
     * @return JoglProcessingFrameBufferObject
     */
    public static JoglProcessingFrameBufferObject sketchTexture(final PApplet theParent,
                                                                final int theWindowWidth,
                                                                final int theWindowHeight,
                                                                final boolean thePresentationModeFlag) {

        if (die()) {
            return null;
        }

        /* create p5 framebuffer object. the first two values define the size of the actual frame the sketch will run in. */
        JoglProcessingFrameBufferObject myFBO = new JoglProcessingFrameBufferObject(theWindowWidth, theWindowHeight,
                                                                                    new JoglTexCreatorFBO_DepthRGBA(),
                                                                                    (GestaltPlugIn)_myGestalt,
                                                                                    theParent,
                                                                                    thePresentationModeFlag);

        return myFBO;
    }

    public static GestaltPlugIn gestalt() {
        return (GestaltPlugIn)_myGestalt;
    }

    public static void applycamera() {
        if (die()) {
            return;
        }

        ((GestaltPlugIn)_myGestalt).applyCamera(_myGestalt.camera());
    }

    public static void cameramover(boolean theState) {
        if (die()) {
            return;
        }

        ((GestaltPlugIn)_myGestalt).cameramover(theState);
    }

    public static FPSCounter fpscounter(boolean theState) {
        if (die()) {
            return null;
        }

        return ((GestaltPlugIn)_myGestalt).fpscounter(theState);
    }


    /* frame buffer objects */
    public static FBO fbo(int theWidth, int theHeight) {
        if (die()) {
            return null;
        }
        return new FBO(theWidth, theHeight);
    }

    public static class FBO {

        private Plane _myPlane;

        private final JoglFrameBufferObject _myFBO;

        private final int _myHeight;

//        private final int _myWidth;
        public FBO(int theWidth, int theHeight) {

            die();

            _myHeight = theHeight;
//            _myWidth = theWidth;

            _myFBO = new JoglFrameBufferObject(theWidth,
                                               theHeight,
                                               null,
                                               new JoglTexCreatorFBO_DepthRGBA());
            /* handle bins */
            _myGestalt.bin(Gestalt.BIN_ARBITRARY).add(_myFBO);

            /* create plane to show FBO */
            _myPlane = plane();
            _myPlane.material().addPlugin(_myFBO);
            _myPlane.scale().set(theWidth, -theHeight);

            _myGestalt.bin(Gestalt.BIN_3D).add(_myPlane);
        }

        public Plane display() {
            return _myPlane;
        }

        public void bind() {
            /* start fbo drawing */
            _myFBO.bindBuffer(gl());

            /** @todo doesn t work if FBO is bigger then window */
            gl().glViewport(0, -(_myParent.height - _myHeight), _myParent.width, _myParent.height);
        }

        public void update() {
            _myFBO.update(gl(), glu());
        }

        public void unbind() {
            JoglFrameBufferObject.unbindBuffer(gl(), 0);
            gl().glViewport(0, 0, _myParent.width, _myParent.height);
        }

        public int getTextureID() {
            return _myFBO.getTextureID();
        }
    }


    /* displaycapabilities */
    public static DisplayCapabilities displaycapabilities() {
        if (die()) {
            return null;
        }
        return _myGestalt.displaycapabilities();
    }
}
