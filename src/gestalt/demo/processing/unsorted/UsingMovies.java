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


package gestalt.demo.processing.unsorted;


import gestalt.Gestalt;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;

import processing.core.PApplet;
import processing.video.Movie;


/**
 * this demo shows how to use processing movie library with gestalt.
 */

public class UsingMovies
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Plane _myMoviePlane;

    private Movie _myMovie;

    private TexturePlugin _myTexture;

    public void setup() {
        System.err.println("### " + getClass().getSimpleName() + " seems to be broken since processing 148++.");

        /* setup p5 */
        size(640, 480, OPENGL);
        rectMode(CENTER);
        noStroke();

        gestalt = new GestaltPlugIn(this);

        _myMovie = new Movie(this, "data/demo/common/movie.mov");
        _myMovie.loop();
        _myMovie.read();

        /* create a texture with reference to movie data 'myMovie.pixels' */
        _myTexture = gestalt.drawablefactory().texture();
        _myTexture.setFilterType(Gestalt.TEXTURE_FILTERTYPE_NEAREST);
        _myTexture.load(GestaltPlugIn.createGestaltBitmap(_myMovie));

        /* create planes */
        _myMoviePlane = gestalt.drawablefactory().plane();
        _myMoviePlane.material().addPlugin(_myTexture);
        _myMoviePlane.setPlaneSizeToTextureSize();
        _myMoviePlane.scale().scale(4);
        gestalt.bin(Gestalt.BIN_3D).add(_myMoviePlane);
    }


    public void movieEvent(Movie theMovie) {
        theMovie.read();
    }


    public void draw() {
        /* clear screen */
        background(255, 0, 128);

        /* glue shapes to mouse */
        _myMoviePlane.position().x = mouseX;
        _myMoviePlane.position().y = mouseY;

        /* update pixels in texture */
        _myTexture.reload();
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingMovies.class.getName()});
    }
}
