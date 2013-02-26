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


import gestalt.extension.quadline.QuadLine;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Color;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.util.CameraMover;

import mathematik.Vector3f;

import werkzeug.interpolation.InterpolateLinear;
import werkzeug.interpolation.Interpolator;

import data.Resource;


public class UsingQuadLines
    extends AnimatorRenderer {

    private QuadLine _myQuadLine;

    private float _myCounter;

    public void setup() {
        /* grab a texure */
        TexturePlugin myImageTexture = drawablefactory().texture();
        myImageTexture.setFilterType(TEXTURE_FILTERTYPE_LINEAR);
        myImageTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));

        /* create line */
        _myQuadLine = drawablefactory().extensions().quadline();
        _myQuadLine.material().transparent = true;
        _myQuadLine.material().addPlugin(myImageTexture);
        _myQuadLine.setLineWidthInterpolator(new Interpolator(100, 400, new InterpolateLinear()));

        /*
         * there are three ways to set the linewidths.
         *
         * the most simple way is to just set the linewidth
         *    _myQuadLine.linewidth = 20;
         *
         * another way is to supply a float array of linewidths
         *    _myQuadLine.linewidths = new float[]{10, 20, 30};
         *
         * a third way is to supply an interpolator
         *    _myQuadLine.setLineWidthInterpolator(new Interpolator());
         *
         * if a linewidth array is supplied the standard linewidth is
         * overridden. if an interpolator is supplied the linewidth array
         * is overridden.
         *
         */

        bin(BIN_3D).add(_myQuadLine);
    }


    public void loop(float theDeltaTime) {

        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        _myCounter += theDeltaTime;
        float mySin = (float) Math.sin(_myCounter);
        float myCos = (float) Math.cos(_myCounter);
        final float myOffset = 100;

        _myQuadLine.points = new Vector3f[] {
                             new Vector3f(displaycapabilities().width / 2 - myOffset,
                                          0,
                                          0),
                             new Vector3f(displaycapabilities().width / 4 - myOffset / 2,
                                          myCos * 15,
                                          mySin * 20),
                             new Vector3f(myCos * 20,
                                          mySin * 15,
                                          0),
                             new Vector3f(event().mouseX,
                                          event().mouseY,
                                          0),
                             new Vector3f(mySin * 20 - displaycapabilities().width / 4 + myOffset / 2,
                                          myCos * 15,
                                          100),
                             new Vector3f( -displaycapabilities().width / 2 + myOffset,
                                          0,
                                          0)};
        _myQuadLine.colors = new Color[] {
                             new Color(1, 1, 1, 0.25f),
                             new Color(1, 1, 1, 0.5f),
                             new Color(1, 1, 1, 1),
                             new Color(1, 1, 1, 1),
                             new Color(1, 1, 0, 1),
                             new Color(1, 0, 0, 1)};

        _myQuadLine.update();
    }


    public static void main(String[] arg) {
        new UsingQuadLines().init();
    }
}
