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


package gestalt.extension.quadline;


import gestalt.material.Color;

import mathematik.Vector3f;

import werkzeug.interpolation.Interpolator;


public abstract class QuadBezierCurve
    extends QuadLine {

    public Vector3f begin;

    public Vector3f begincontrol;

    public Vector3f end;

    public Vector3f endcontrol;

    public Color begincolor;

    public Color endcolor;

    private int _myResolution;

    private boolean _myResolutionDirty;

    private Color[] _myColor;

    private Vector3f[] _myPoints;

    private Interpolator _myColorRedInterpolator;

    private Interpolator _myColorGreenInterpolator;

    private Interpolator _myColorBlueInterpolator;

    private Interpolator _myColorAlphaInterpolator;

    private Vector3f a = new Vector3f();

    private Vector3f b = new Vector3f();

    private Vector3f c = new Vector3f();

    public QuadBezierCurve() {
        begin = new Vector3f();
        end = new Vector3f();
        begincontrol = new Vector3f();
        endcontrol = new Vector3f();
        a = new Vector3f();
        b = new Vector3f();
        c = new Vector3f();
        begincolor = new Color(1, 1, 1, 1);
        endcolor = new Color(1, 1, 1, 1);
        _myResolution = 20;
        _myResolutionDirty = true;
    }


    public void setResolution(int theResolution) {
        _myResolution = theResolution;
        _myResolutionDirty = true;
    }


    public int getResolution() {
        return _myResolution;
    }


    public void update() {
        buildBezierLine();
        buildColors();
        buildFragments();
        _myProducer.getQuadStrip(_myPoints,
                                 _myColor,
                                 linewidths,
                                 linewidth,
                                 _myQuadFragments);
        _myResolutionDirty = false;
    }


    public void setColorRedInterpolator(Interpolator theInterpolator) {
        _myColorRedInterpolator = theInterpolator;
    }


    public void setColorGreenInterpolator(Interpolator theInterpolator) {
        _myColorGreenInterpolator = theInterpolator;
    }


    public void setColorBlueInterpolator(Interpolator theInterpolator) {
        _myColorBlueInterpolator = theInterpolator;
    }


    public void setColorAlphaInterpolator(Interpolator theInterpolator) {
        _myColorAlphaInterpolator = theInterpolator;
    }


    private void buildFragments() {
        if (_myResolutionDirty) {
            _myQuadFragments = new QuadFragment[_myResolution + 1];
//            for (int i = 0; i < _myQuadFragments.length; i++) {
//                _myQuadFragments[i] = new QuadFragment();
//            }
        }
    }


    private void buildColors() {

        if (_myResolutionDirty) {
            _myColor = new Color[_myResolution + 1];
            for (int i = 0; i < _myColor.length; i++) {
                _myColor[i] = new Color();
            }
        }

        if (_myColorRedInterpolator != null &&
            _myColorGreenInterpolator != null &&
            _myColorBlueInterpolator != null &&
            _myColorAlphaInterpolator != null) {
            for (int i = 0; i < _myColor.length; ++i) {
                final float myPercentage = (float) i / (float) (_myColor.length - 1);
                _myColor[i].r = _myColorRedInterpolator.get(myPercentage);
                _myColor[i].g = _myColorGreenInterpolator.get(myPercentage);
                _myColor[i].b = _myColorBlueInterpolator.get(myPercentage);
                _myColor[i].a = _myColorAlphaInterpolator.get(myPercentage);
            }
        } else {
            final float myRDiff = endcolor.r - begincolor.r;
            final float myGDiff = endcolor.g - begincolor.g;
            final float myBDiff = endcolor.b - begincolor.b;
            final float myADiff = endcolor.a - begincolor.a;
            for (int i = 0; i < _myColor.length; ++i) {
                final float myRatio = (float) i / (float) _myColor.length;
                _myColor[i].set(begincolor.r + myRDiff * myRatio,
                                begincolor.g + myGDiff * myRatio,
                                begincolor.b + myBDiff * myRatio,
                                begincolor.a + myADiff * myRatio);
            }
        }
    }


    private void buildBezierLine() {

        if (_myResolutionDirty) {
            _myPoints = new Vector3f[_myResolution + 1];
            for (int i = 0; i < _myPoints.length; i++) {
                _myPoints[i] = new Vector3f();
            }
        }

        c.set(begincontrol);
        c.sub(begin);
        c.scale(3);
        b.set(endcontrol);
        b.sub(begincontrol);
        b.scale(3);
        b.sub(c);
        a.set(end);
        a.sub(begin);
        a.sub(c);
        a.sub(b);

        for (int i = 0; i < _myResolution; ++i) {
            float t = (float) i / _myResolution;
            _myPoints[i] = new Vector3f(a);
            _myPoints[i].scale(t * t * t);
            Vector3f bComponent = new Vector3f(b);
            bComponent.scale(t * t);
            _myPoints[i].add(bComponent);
            Vector3f cComponent = new Vector3f(c);
            cComponent.scale(t);
            _myPoints[i].add(cComponent);
            _myPoints[i].add(begin);
        }

        _myPoints[_myResolution].set(end);
    }
}
