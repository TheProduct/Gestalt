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


import gestalt.Gestalt;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.FrameGrabber;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.FontProducer;

import data.Resource;


/**
 * this demo shows how to grab frames from the opengl context and save them to disk.
 */


public class UsingFrameGrabbing
    extends AnimatorRenderer {

    private Plane[] _myPlanes;

    private TexturePlugin[] _myFontTexture;

    private FontProducer _myFontProducer;

    private FrameGrabber _myFrameGrabber;

    public void setup() {
        /* create a font producer */
        _myFontProducer = Bitmaps.getFontProducer(Resource.getStream("demo/font/silkscreen/slkscr.ttf"));
        _myFontProducer.setSize(48);
        _myFontProducer.setLineWidth(18);
        _myFontProducer.setQuality(Gestalt.FONT_QUALITY_HIGH);

        /* create text planes */
        float myPlaneSize = 50;
        char[] myText = new char[] {'1', 'R', 'G', 'B', '9'};
        _myPlanes = new Plane[myText.length];
        _myFontTexture = new TexturePlugin[myText.length];
        for (int i = 0; i < _myPlanes.length; ++i) {
            _myPlanes[i] = drawablefactory().plane();
            _myFontTexture[i] = drawablefactory().texture();
            _myFontTexture[i].setFilterType(TEXTURE_FILTERTYPE_LINEAR);
            _myFontTexture[i].load(_myFontProducer.getBitmap("" + myText[i]));
            _myPlanes[i].material().addPlugin(_myFontTexture[i]);
            _myPlanes[i].setPlaneSizeToTextureSize();
            _myPlanes[i].origin(SHAPE_ORIGIN_CENTERED);
            _myPlanes[i].position().y = 100;
            _myPlanes[i].position().x = (i - _myPlanes.length / 2) * myPlaneSize;
            _myPlanes[i].material().transparent = true;
        }
        bin(BIN_3D).add(_myPlanes);
        _myPlanes[1].material().color4f().set(1, 0, 0);
        _myPlanes[2].material().color4f().set(0, 1, 0);
        _myPlanes[3].material().color4f().set(0, 0, 1);

        /* create frame grabber */
        _myFrameGrabber = drawablefactory().extensions().framegrabber();

        /* select an output file format
         * IMAGE_FILEFORMAT_TGA
         * IMAGE_FILEFORMAT_JPEG
         * IMAGE_FILEFORMAT_PNG
         */
        _myFrameGrabber.setImageFileFormat(IMAGE_FILEFORMAT_PNG);
        _myFrameGrabber.setFileName(Resource.getPath("") + "UsingFrameGrabbing-frame-");

        bin(BIN_ARBITRARY).add(_myFrameGrabber);
    }


    public void loop(float theDeltaTime) {

        /*
         * it is advised to set 'theDeltaTime' to a fixed value,
         * for example 1/30f if 30 frames per second is the desired fps.
         */
        theDeltaTime = 1 / 30f;

        /* switch recording on/off */
        if (event().keyDown) {
            if (event().key == ',') {
                _myFrameGrabber.start();
            }
            if (event().key == '.') {
                _myFrameGrabber.stop();
            }
        }

        /* rotate shapes */
        for (int i = 0; i < _myPlanes.length; ++i) {
            _myPlanes[i].rotation().z += 60 * theDeltaTime * (float) i / (PI * 100);
        }
    }


    public static void main(String[] arg) {
        new UsingFrameGrabbing().init();
    }
}
