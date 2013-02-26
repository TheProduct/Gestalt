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
import gestalt.util.FPSCounter;

import werkzeug.interpolation.InterpolateExponential;
import werkzeug.interpolation.Interpolator;


/**
 * this demo shows how to use bezier curved quad lines.
 * note that these curves can be textured as they are made up of series of quads.
 * also note the interpolators that can be applied to color and width.
 */
public class UsingBezierCurves
    extends AnimatorRenderer {

    private QuadBezierCurve[] _myBezierLine;

    private float _myCounter;

    private FPSCounter _myFPSCounter;

    public void setup() {

        /* g1 */
        camera().culling = CAMERA_CULLING_BACKFACE;
        framerate(UNDEFINED);

        QuadProducer.VERBOSE = true;

        /* create bezier line */
        _myBezierLine = new QuadBezierCurve[50];
        for (int i = 0; i < _myBezierLine.length; ++i) {
            _myBezierLine[i] = drawablefactory().extensions().quadbeziercurve();
            _myBezierLine[i].linewidth = 50;
            _myBezierLine[i].setResolution(50);
            _myBezierLine[i].material().transparent = true;
            _myBezierLine[i].material().depthtest = false;
            _myBezierLine[i].material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
            _myBezierLine[i].begincolor.a = 0.01f;
            _myBezierLine[i].endcolor.a = 0.03f;
            _myBezierLine[i].setColorRedInterpolator(new Interpolator(0, 0.5f, new InterpolateExponential(1.0f)));
            _myBezierLine[i].setColorGreenInterpolator(new Interpolator(0, 0.5f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].setColorBlueInterpolator(new Interpolator(0, 1f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].setColorAlphaInterpolator(new Interpolator(0.01f, 0.08f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].begin.set( -displaycapabilities().width / 2 + 150, 0, 0);
            _myBezierLine[i].end.set(displaycapabilities().width / 2 - 150, 0, 0);
            _myBezierLine[i].setLineWidthInterpolator(new Interpolator(10, 100, new InterpolateExponential(0.5f)));
            bin(BIN_3D).add(_myBezierLine[i]);
        }

        /* fps counter */
        _myFPSCounter = new FPSCounter();
        _myFPSCounter.setInterval(60);
        _myFPSCounter.display().position.set(displaycapabilities().width / -2 + 20, displaycapabilities().height / 2 - 20);
        _myFPSCounter.display().color.set(1);
        bin(BIN_2D_FOREGROUND).add(_myFPSCounter.display());
    }


    public void loop(float theDeltaTime) {
        _myCounter += 0.125f * theDeltaTime;
        for (int i = 0; i < _myBezierLine.length; ++i) {
            float myRadius = 50;
            double myCounter = _myCounter + PI * 2 * (float) i / (float) _myBezierLine.length;
            float myX = (float) Math.sin(myCounter) * myRadius + event().mouseX;
            float myY = (float) Math.cos(myCounter) * myRadius + event().mouseY;
            float myZ = (float) Math.cos(myCounter) * myRadius * 5;

            _myBezierLine[i].begincontrol.set(myX, myY, myZ);
            _myBezierLine[i].endcontrol.set(myX, myY, myZ);

            _myBezierLine[i].update();
        }

        /* fps counter */
        _myFPSCounter.loop();
    }


    public static void main(String[] args) {
        new UsingBezierCurves().init();
    }
}
