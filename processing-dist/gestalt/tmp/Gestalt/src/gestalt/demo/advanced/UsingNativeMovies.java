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


package gestalt.demo.advanced;

import gestalt.candidates.NativeMovieTextureProducer;
import gestalt.context.DisplayCapabilities;
import gestalt.extension.materialplugin.JoglMaterialPluginNonPowerOfTwoTextureReference;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;

import data.Resource;


public class UsingNativeMovies
        extends AnimatorRenderer {

    private static final String SAMPLE_MOVIE_PATH = "demo/common/tree.mov";

    private float _myPassedTime = 0;

    private NativeMovieTextureProducer _myMovieProducer;

    private JoglMaterialPluginNonPowerOfTwoTextureReference _myMovieTexture;

    private Plane _myPlane;

    private Cuboid _myCube;

    public void setup() {

        framerate(UNDEFINED);
        fpscounter(true);

        System.out.println("The Library path is: " + System.getProperty("java.library.path"));

        /* Cuboid */
        _myCube = drawablefactory().cuboid();
        _myCube.scale().set(100, 100, 100);

        bin(BIN_3D).add(_myCube);

        /* Plane */
        _myPlane = drawablefactory().plane();

        bin(BIN_3D).add(_myPlane);

        /* The texture referencing the movie */
        _myMovieTexture = new JoglMaterialPluginNonPowerOfTwoTextureReference(true);

        /* Movie Producer drawable */
        _myMovieProducer = new NativeMovieTextureProducer(
                "NativeMovieTextureProducer", _myMovieTexture, Resource.getPath(SAMPLE_MOVIE_PATH), 25.0f);

        bin(BIN_3D_FINISH).add(_myMovieProducer);

        _myPlane.material().addPlugin(_myMovieTexture);

        _myCube.material().addPlugin(_myMovieTexture);

    }

    public void loop(float theDeltaTime) {
        _myPassedTime += theDeltaTime;

        _myCube.rotation().set(Math.sin(_myPassedTime),
                               Math.sin(_myPassedTime * 2), Math.sin(_myPassedTime * -1.4f));
        _myCube.position().set(-(float)event().mouseX,
                               -(float)event().mouseY, 100);

        if (_myMovieProducer.isInitialized()) {
            _myPlane.setPlaneSizeToTextureSize();
        }

    }

    public DisplayCapabilities createDisplayCapabilities() {

        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();

        myDisplayCapabilities.name = "Native Movie";

        myDisplayCapabilities.width = 1024;

        myDisplayCapabilities.height = 768;

        myDisplayCapabilities.centered = true;

        myDisplayCapabilities.backgroundcolor.set(0.12f, 0.06f, 0.03f, 1f);

        myDisplayCapabilities.cursor = false;

        myDisplayCapabilities.synctovblank = false;

        DisplayCapabilities.listDisplayDevices();

        return myDisplayCapabilities;
    }

    public static void main(String[] args) {
        new UsingNativeMovies().init();
    }
}
