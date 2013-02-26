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


import gestalt.extension.quadline.QuadBezierCurve;
import gestalt.extension.quadline.QuadProducer;
import gestalt.render.AnimatorRenderer;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


/**
 * this demo shows how to use bezier curves.
 */

public class UsingBezierCurveTextured
    extends AnimatorRenderer {

    private QuadBezierCurve _myBezierLine;

    public void setup() {
        /* g1 */
        camera().culling = CAMERA_CULLING_BACKFACE;
        framerate(UNDEFINED);
        fpscounter(true);
        displaycapabilities().backgroundcolor.set(0.2f);
        QuadProducer.VERBOSE = true;

        /* grab a texure */
        TexturePlugin myImageTexture = drawablefactory().texture();
        myImageTexture.setFilterType(TEXTURE_FILTERTYPE_MIPMAP);
        myImageTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));

        /* create bezier line */
        final float myOffset = 150;
        _myBezierLine = drawablefactory().extensions().quadbeziercurve();
        _myBezierLine.linewidth = 256;
        _myBezierLine.setResolution(50);
        _myBezierLine.material().transparent = true;
        _myBezierLine.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myBezierLine.material().addPlugin(myImageTexture);
        _myBezierLine.begincolor.a = 1f;
        _myBezierLine.endcolor.a = 0.5f;
        _myBezierLine.begin.set(displaycapabilities().width / 2 - myOffset, 0, 0);
        _myBezierLine.end.set( -displaycapabilities().width / 2 + myOffset, 0, 0);
        bin(BIN_3D).add(_myBezierLine);
    }


    public void loop(float theDeltaTime) {
        float myX = event().mouseX;
        float myY = event().mouseY;
        float myZ = 0;

        _myBezierLine.begincontrol.set(myX, myY, myZ);
        _myBezierLine.endcontrol.set(myX, myY, myZ);
        _myBezierLine.update();
    }


    public static void main(String[] args) {
        new UsingBezierCurveTextured().init();
    }
}
