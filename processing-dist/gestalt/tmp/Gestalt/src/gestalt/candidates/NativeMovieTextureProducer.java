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


package gestalt.candidates;

import gestalt.context.GLContext;
import gestalt.extension.materialplugin.JoglMaterialPluginNonPowerOfTwoTextureReference;
import gestalt.shape.AbstractDrawable;


public class NativeMovieTextureProducer
        extends AbstractDrawable
        implements
        Runnable {
        
    private final String _movieFileName;

    private int _myOpenGLTextureID;

    private JoglMaterialPluginNonPowerOfTwoTextureReference _myTexturePlugin;

    private boolean _myIsInitialized = false;

    /* threading */
    private Thread _myRunner;

    private float _myUpdateFPS = 25;

    public NativeMovieTextureProducer(String theNativeLibrary,
                                      JoglMaterialPluginNonPowerOfTwoTextureReference theTexture, String theFileName,
                                      float theFPS) {
        System.loadLibrary(theNativeLibrary);
        _movieFileName = theFileName;
        _myTexturePlugin = theTexture;
        _myUpdateFPS = theFPS;

        _myRunner = new Thread(this);
    }


    /* Init the native library */
    public native void init(String theFileName, int theOpenGLTextureID);


    /* Get new frame from movie file. Keep in memory */
    public native void tryToReadNewFrame();


    /* Get the Texture ID */
    public native int requestOpenGlTextureId();


    /* Upload frame to if there is a new one texture */
    public native void update();


    /* Get the texture/movie width */
    public native int getWidth();

    public native int getHeight();

    /**
     * Gets the texture ID with which the native movie library was set up
     *
     *
     * @return The used OpenGL texture ID
     */
    public int getTextureID() {
        return _myOpenGLTextureID;
    }

    public boolean isInitialized() {
        return _myIsInitialized;
    }

    public void draw(GLContext theRenderContext) {
        if (_myIsInitialized) {
            update();
        } else {

            if (_myTexturePlugin.isInitialized()) {
                _myOpenGLTextureID = _myTexturePlugin.getTextureID();
                init(_movieFileName, _myOpenGLTextureID);

                /* Set texture size */
                _myTexturePlugin.setPixelWidth(getWidth());
                _myTexturePlugin.setPixelHeight(getHeight());

                _myIsInitialized = true;

                /* Start the action */
                _myRunner.start();

            }

        }
    }

    public void run() {
        while (Thread.currentThread() == _myRunner) {
            try {
                Thread.sleep((int)(1000 / _myUpdateFPS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tryToReadNewFrame();
        }
    }
}
