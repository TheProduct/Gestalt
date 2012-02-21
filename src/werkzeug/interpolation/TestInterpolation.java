/*
 * Werkzeug
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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


package werkzeug.interpolation;


import gestalt.render.bin.DisposableBin;
import gestalt.render.AnimatorRenderer;


/* demonstrate different types of interpolation */


public class TestInterpolation
    extends AnimatorRenderer {

    private DisposableBin _myBin;

    private float _myCounter;

    private Interpolator _myInterpolator;

    public void setup() {
        /* g1 */
        framerate(120);
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create a disposable point bin */
        _myBin = new DisposableBin();
        _myBin.scale.set(100, 100);
        _myBin.position.set(_myBin.scale.x / -2, _myBin.scale.y / -2);
        _myBin.autoclear = false;
        bin(BIN_3D).add(_myBin);

        DisposableBin myCornerBin = new DisposableBin();
        myCornerBin.scale = _myBin.scale;
        myCornerBin.position = _myBin.position;
        myCornerBin.autoclear = false;
        myCornerBin.point(0, 0);
        myCornerBin.point(1, 0);
        myCornerBin.point(1, 1);
        myCornerBin.point(0, 1);
        bin(BIN_3D).add(myCornerBin);

        /* interpolate */
        _myInterpolator = new Interpolator(new InterpolateLinear());
        _myCounter = 0;
    }


    public void loop(float theDeltaTime) {
        /* interpolate between 0 and 1 */
        if (_myCounter < 1.0f) {
            float myX = _myCounter;
            float myY = _myInterpolator.get(_myCounter);
            _myBin.point(myX, myY);
            _myCounter += 0.005f;
        }

        /* change interpolator */
        if (event().keyPressed) {
            if (event().key == '1') {
                _myInterpolator = new Interpolator(new InterpolatePeak(0.9f));
            }
            if (event().key == '2') {
                _myInterpolator = new Interpolator(new InterpolateExponential(3f));
            }
            if (event().key == '3') {
                _myInterpolator = new Interpolator(new InterpolateOffset(0.2f, 0.7f));
            }
            if (event().key == '4') {
                _myInterpolator = new Interpolator(new InterpolateSinus(0, 0, 0.5f, 1.0f));
            }
            if (event().key == '5') {
                _myInterpolator = new Interpolator(new InterpolateSmoothstep(0.1f, 0.9f));
            }
            if (event().key == '6') {
                _myInterpolator = new Interpolator(new InterpolateBezier(0.7f, 0.2f));
            }
            if (event().key == '7') {
                InterpolatorKernel myInner = new InterpolateExponential(3f);
                InterpolatorKernel myOutter = new InterpolateBezier(0.7f, 0.2f);
                InterpolatorKernel myCombiner = new InterpolatorKernelCombiner(myInner, myOutter);
                InterpolatorKernel myOutterCombiner = new InterpolatorKernelCombiner(new InterpolateClamp(0.2f, 0.8f),
                                                                                     myCombiner);
                _myInterpolator = new Interpolator(myOutterCombiner);
            }
            if (event().key == '8') {
                _myInterpolator = new Interpolator(new InterpolateRandom());
            }
            if (event().key == '9') {
                _myInterpolator = new Interpolator(new InterpolateInvert());
            }
            if (event().key == '0') {
                _myInterpolator = new Interpolator(new InterpolateConstant(0.5f));
            }
            if (event().key == 'a') {
                _myInterpolator = new Interpolator(new InterpolateParabola(8));
            }

            _myBin.clear();
            _myCounter = 0;
        }
    }


    public static void main(String[] args) {
        new TestInterpolation().init();
    }
}
