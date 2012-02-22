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


package gestalt.material.texture;


import java.io.InputStream;

import gestalt.material.texture.bitmap.ByteBitmap;


/**
 * create bitmaps from images, movies and fonts.
 */
public abstract class Bitmaps {

    private static int _myAutomaticName = -1;

    private Bitmaps() {
    }


    /* bitmap */
    /**
     *
     */
    public static ByteBitmap getBitmap(String theFilePath) {
        return BitmapProducer.load(theFilePath);
    }

    /**
     *
     */
    public static ByteBitmap getBitmap(InputStream theStream, String theFilename) {
        return BitmapProducer.load(theStream, theFilename);
    }

    /**
     *
     */
    public static ByteBitmap getBitmap(InputStream theStream) {
        _myAutomaticName++;
        return BitmapProducer.load(theStream, Integer.toString(_myAutomaticName));
    }


    /* font */
    /**
     *
     */
    public static FontProducer getFontProducer(String theFilename) {
        return new FontProducer(theFilename);
    }

    /**
     *
     */
    public static FontProducer getFontProducer(InputStream theFilename) {
        return new FontProducer(theFilename);
    }


    /* movie */
    /**
     *
     */
    public static MovieProducer getMovieProducer(String theFilename) {
        return new MovieProducer(theFilename);
    }

    /**
     *
     * @param theFilename String
     * @param thePreloadFlag boolean
     * @return MovieProducer
     */
    public static MovieProducer getMovieProducer(String theFilename, boolean thePreloadFlag) {
        return new MovieProducer(theFilename, thePreloadFlag);
    }

    public static MovieProducer getMovieProducer(String theFilename, boolean thePreloadFlag, Class theBitmapClass) {
        return new MovieProducer(theFilename, thePreloadFlag, theBitmapClass);
    }

    /**
     * start the movieproducer in extra thread; no need to call 'update()'.
     * @param theFilename String
     * @param thePreloadFlag boolean
     * @param theUpdateFPS int
     * @return MovieProducer
     */
    public static MovieProducer getMovieProducer(String theFilename, boolean thePreloadFlag, int theUpdateFPS) {
        return new MovieProducer(theFilename, thePreloadFlag, theUpdateFPS);
    }
}
