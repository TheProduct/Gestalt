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


package gestalt.extension.materialplugin.joglutiltexture;


import java.io.File;
import java.util.Vector;

import gestalt.shape.Plane;

import com.sun.opengl.util.texture.TextureData;


public class TextureSequenceDisplay {

    private final Plane _myPlane;

    private TextureDataSequence _mySequence;

    private JoglUtilTexture _myTexture;

    public TextureSequenceDisplay(final File theFolder, final int theCacheSize) {
        this();
        createSequence(theFolder, theCacheSize);
    }


    public TextureSequenceDisplay() {
        _myTexture = new JoglUtilTexture(true);

        _myPlane = new Plane();
        _myPlane.material().addTexture(_myTexture);
        _myPlane.setPlaneSizeToTextureSize();
    }


    private final void createSequence(final File theFolder, final int theCacheSize) {
        final Vector<File> myFiles = werkzeug.Util.getFilesInDirectory(theFolder);
        _mySequence = new TextureDataSequence(theCacheSize, myFiles);
        _mySequence.start();
        _myTexture.scheduleTextureData(_mySequence.current());
    }


    public void changeSequence(final File theFolder, final int theCacheSize) {
        if (_mySequence != null) {
            _mySequence.quit();
        }
        createSequence(theFolder, theCacheSize);
    }


    public Plane display() {
        return _myPlane;
    }


    public void loop(float theDeltaTime) {
        if (_mySequence != null) {
            final TextureData myData = _mySequence.next();
            if (myData != null) {
                _myTexture.scheduleTextureData(myData);
            }
        }
    }


    public void fillCache() {
        if (_mySequence != null) {
            _mySequence.fillCache();
        }
    }
}
