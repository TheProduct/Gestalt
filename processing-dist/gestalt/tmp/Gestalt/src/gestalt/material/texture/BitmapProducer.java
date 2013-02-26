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


import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public abstract class BitmapProducer {

    public static boolean HINT_DONT_CACHE = true;

    public static boolean VERBOSE = false;

    private static int _myCacheSize = 1600;

    private static int _myCurrentSlot = 0;

    private static CachedBitmap[] _myCache = new CachedBitmap[_myCacheSize];

    private BitmapProducer() {}


    public static ByteBitmap load(InputStream theStream, String theFilename) {
        ByteBitmap myBitmap = readFromCache(theFilename);
        if (myBitmap != null) {
            return myBitmap;
        } else {
            if (VERBOSE) {
                System.out.println("### INFO @ BitmapProducer.load / loading " + theFilename);
            }
            try {
                final BufferedImage myImage = javax.imageio.ImageIO.read(theStream);
                myBitmap = ImageUtil.convertBufferedImage2ByteBitmap(myImage);
                writeToCache(new CachedBitmap(myBitmap, theFilename));
                theStream.close();
                return myBitmap;
            } catch (Exception ex) {
                System.err.println("### ERROR @ BitmapProducer.load / texture " + theFilename + " is missing: " +
                                   ex);
                return null;
            }
        }
    }


    public static ByteBitmap load(String theFilePath) {
        String[] myPathStrings = theFilePath.split("/");
        String theFilename = myPathStrings[myPathStrings.length - 1];
        try {
            return load(new FileInputStream(theFilePath), theFilename);
        } catch (FileNotFoundException ex) {
            System.err.println("### ERROR @ BitmapProducer.load / couldn t create inputstream " + ex);
            return null;
        }
    }


    private static void writeToCache(CachedBitmap myCachedBitmap) {
        if (HINT_DONT_CACHE) {
            return;
        }
        _myCache[_myCurrentSlot] = myCachedBitmap;
        _myCurrentSlot++;
        _myCurrentSlot %= _myCacheSize;
    }


    private static ByteBitmap readFromCache(String theFilename) {
        ByteBitmap myBitmap = null;
        for (int i = 0; i < _myCache.length; ++i) {
            if (_myCache[i] != null && _myCache[i].getFilename().equals(theFilename)) {
                return _myCache[i].getBitmap();
            }
        }
        return myBitmap;
    }


    public static void setCacheSize(int theSize) {
        _myCacheSize = theSize;
        clearCache();
    }


    public static void clearCache() {
        _myCache = new CachedBitmap[_myCacheSize];
        _myCurrentSlot = 0;
    }
}
