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


import java.io.File;

import gestalt.G;
import gestalt.extension.materialplugin.joglutiltexture.TextureSequenceDisplay;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;

import data.Resource;


public class UsingTextureSequence
    extends AnimatorRenderer {

    private TextureSequenceDisplay _myMovieDisplays;

    private int _myClipCounter;

    private String[] _myClips = {"DDS-CLIP-1", "DDS-CLIP-2", "DDS-CLIP-3"};

    private static final int CACHE_SIZE = 5;

    public void setup() {
        framerate(60);
        fpscounter(true);

        final TextureSequenceDisplay myMovieDisplay = new TextureSequenceDisplay();
        bin(BIN_3D).add(myMovieDisplay.display());
    }


    public void loop(float theDeltaTime) {
        _myMovieDisplays.loop(theDeltaTime);
    }


    public void keyPressed(char c, int i) {
        _myMovieDisplays.changeSequence(new File(Resource.getPath(_myClips[_myClipCounter])), CACHE_SIZE);
        _myClipCounter++;
        _myClipCounter %= _myClips.length;
    }


    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 1024;
        myDisplayCapabilities.height = 768;
        myDisplayCapabilities.backgroundcolor.set(0.6f);
        myDisplayCapabilities.undecorated = true;
        G.init(UsingTextureSequence.class, myDisplayCapabilities);
    }
}
