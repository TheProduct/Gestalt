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


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.MovieProducer;

import data.Resource;


/**
 * this demo shows how to properly dipose texture, in a way that
 * they are not only removed from cpu memory but also from gpu
 * memory. the same technique should be used for VBOs, display lists
 * and everything else that allocates memory on the graphics card.
 *
 * note that in this demo we create a new texture object for every
 * frame, this is NOT good practice. the intention of this demo is
 * to create a big memory footprint and to get rid off it again.
 *
 * for a proper example please refer to the 'UsingMovieTextures' demo.
 */

public class UsingDisposeMechanism
    extends AnimatorRenderer {

    private Plane _myMoviePlane;

    private MovieProducer _myMovieProducer;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.95f);

        _myMoviePlane = drawablefactory().plane();
        _myMovieProducer = Bitmaps.getMovieProducer(Resource.getPath("demo/common/movie.mov"), false, 30);
        _myMovieProducer.play(1.0f);
        _myMovieProducer.setLooping(true);
        _myMoviePlane.scale().set(400, 400);

        _myMoviePlane.material().addTexture().load(_myMovieProducer.getBitmap());

        bin(BIN_3D).add(_myMoviePlane);
    }


    public void loop(float theDeltaTime) {

        /* NOTE, THAT THIS IS NOT THE PREFERRED WAY TO USE TEXTURES! ( see above ) */

        /* we schedule the old texture for disposal and remove it from the plane s material. */
        dispose(_myMoviePlane.material().texture());
        _myMoviePlane.material().removeTexture();

        /* create a new texture */
        _myMoviePlane.material().addTexture().load(_myMovieProducer.getBitmap());
    }


    public void finish() {
        MovieProducer.destroyAll();
    }


    public static void main(String[] arg) {
        new UsingDisposeMechanism().init();
    }
}
