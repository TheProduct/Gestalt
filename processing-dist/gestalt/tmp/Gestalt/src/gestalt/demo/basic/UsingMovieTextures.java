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


package gestalt.demo.basic;

import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.MovieProducer;

import data.Resource;


/**
 * this demo shows how to use movies as textures in gestalt.<br/>
 * see also 'UsingImageTextures'<br/>
 * <br/>
 * movies are always read from movie files. since quicktime is
 * the movie rendering api, all formats that quicktime support
 * are supported by gestalt. although different codecs perform
 * very differently.<br/>
 * for example:<br/>
 * the 'animation' is one of the rare 'codecs' that can store 32bit data
 * thus has an alpha channel.<br/>
 * some codecs are small in filesize but take long to decompress.<br/>
 * see for yourself.<br/>
 * <br/>
 * a movieproducer genereates bitmaps from a moviefile.<br/>
 */
public class UsingMovieTextures
        extends AnimatorRenderer {

    private TexturePlugin _myMovieTexture;

    private Plane _myMoviePlane;

    private MovieProducer _myMovieProducer;

    private TexturePlugin _mySecondMovieTexture;

    private Plane _mySecondMoviePlane;

    private MovieProducer _mySecondMovieProducer;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.95f);

        /* create a plane that carries the texture */
        _myMoviePlane = drawablefactory().plane();

        /* create a texture */
        _myMovieTexture = drawablefactory().texture();

        /*
         * get a movieproducer from the Bitmaps.
         * the movieproducer provides certain methods to control
         * your movie and to get the actual data
         * here we load a movie that contains an alpha channel
         */
        _myMovieProducer = Bitmaps.getMovieProducer(Resource.getPath("demo/common/movie.mov"), false);

        /*
         * start the movie
         *
         * using play() is not mandatory. you can also play your
         * movie through calling 'nextFrame()' in the loop().
         * but using 'play()' also plays the sound that is contained in
         * the movie!
         *
         * play(0.0f) = pause
         * play(1.0f) = play forward with normal speed
         * play(x) = play forward with adjusted speed
         * play(-1.0f)= play backwards with normal speed
         * play(-x) = play backwards with adjusted speed
         *
         */
        _myMovieProducer.play(1.0f);
        _myMovieProducer.setLooping(true);

        /*
         * now you actually load your data.
         * _myMovieProducer.getBitmap() returns the current frame of your
         * movie as a gestalt bitmap, then you tell the texturemanager
         * to load that bitmap into a texture using the id
         */
        _myMovieTexture.load(_myMovieProducer.getBitmap());

        /* set the texture in the material of your shape */
        _myMoviePlane.material().addPlugin(_myMovieTexture);

        /* set scale of the plane */
        _myMoviePlane.setPlaneSizeToTextureSize();
        _myMoviePlane.scale().scale(4);

        /* add your shape to the renderer */
        bin(BIN_3D).add(_myMoviePlane);

        /* now we do the same thing again for another movie */
        _mySecondMoviePlane = drawablefactory().plane();
        _mySecondMovieTexture = drawablefactory().texture();
        _mySecondMovieProducer = Bitmaps.getMovieProducer(Resource.getPath("demo/common/movie.mov"));
        _mySecondMovieProducer.play(0.9f);
        _mySecondMovieProducer.setLooping(true);
        _mySecondMovieTexture.load(_mySecondMovieProducer.getBitmap());
        _mySecondMoviePlane.material().addPlugin(_mySecondMovieTexture);
        _mySecondMoviePlane.material().color4f().set(0.6f, 0.6f, 0.6f, 1f);
        _mySecondMoviePlane.setPlaneSizeToTextureSize();
        _mySecondMoviePlane.scale().scale(4);
        bin(BIN_3D).add(_mySecondMoviePlane);
    }

    public void loop(float theDeltaTime) {
        /*
         * a movie texture needs to be updated each frame.
         * if we wouldn t have used 'play()' in the creation
         * of the movie texture (see above), we would have to call
         * 'nextFrame()' to play the movie.
         *
         * there seems to be a problem with small movie sizes.
         * the number of channels is always 4.
         */
        _myMovieProducer.update();

        /*
         * again load the framedata into the texture using your textureID
         */
        _myMovieTexture.load(_myMovieProducer.getBitmap());

        /*
         * now the second movie.
         * alternatively just the data of the bitmap can be replaced.
         * this saves a bitmap creation.
         * hmmm.
         */
        _mySecondMovieProducer.update();
        _mySecondMovieTexture.bitmap().setDataRef(_mySecondMovieProducer.getData());
        _mySecondMovieTexture.reload();
    }

    public void finish() {
        MovieProducer.destroyAll();
    }

    public static void main(String[] arg) {
        new UsingMovieTextures().init();
    }
}
