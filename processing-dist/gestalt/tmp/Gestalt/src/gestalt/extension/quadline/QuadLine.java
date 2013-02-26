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


import gestalt.shape.AbstractShape;
import gestalt.material.Color;

import mathematik.Vector3f;

import werkzeug.interpolation.Interpolator;


public abstract class QuadLine
    extends AbstractShape {

    public Vector3f[] points;

    public Color[] colors;

    public float[] linewidths;

    public float linewidth;

    private int _myPointArrayLength;

    protected QuadProducer _myProducer;

    protected QuadFragment[] _myQuadFragments;

    protected boolean _myAutoUpdate;

    public QuadLine() {
        _myProducer = new QuadProducer();
        linewidth = 5;
        _myPointArrayLength = -1;
        _myAutoUpdate = false;
    }


    public void propagateUpVector(boolean theFlag) {
        _myProducer.UPVECTOR_PROPAGATION = theFlag;
    }


    public void autoupdate(boolean theAutoUpdate) {
        _myAutoUpdate = theAutoUpdate;
    }


    public Vector3f upvector() {
        return _myProducer.upvector();
    }


    public void update() {
        if (points != null) {
            if (_myPointArrayLength == -1 || _myPointArrayLength != points.length) {
                /* line is uninitialized */
                buildFragments();
            }
            _myPointArrayLength = points.length;
            _myProducer.getQuadStrip(points,
                                     colors,
                                     linewidths,
                                     linewidth,
                                     _myQuadFragments);
        }
    }


    public void setLineWidthInterpolator(Interpolator theInterpolator) {
        _myProducer.setLineWidthInterpolator(theInterpolator);
    }


    private void buildFragments() {
        if (_myQuadFragments == null || _myQuadFragments.length != points.length) {
            _myQuadFragments = new QuadFragment[points.length];
        }
    }


    public QuadProducer getProducer() {
        return _myProducer;
    }


    public QuadFragment[] getLineFragments() {
        return _myQuadFragments;
    }


    /* --> Drawable obligation */
    public float getSortValue() {
        return _mySortValue;
    }


    public float[] getSortData() {
        /**
         * @todo
         * guess what, this is unimplemented.
         * actually sorting a line with arbitrary
         * start and endpoints does not makes sense.
         * rather all quad fragments should be
         * sorted, which is not yet implemented.
         * this version is also faster.
         * as of now 'position' can be used for
         * basic sorting.
         */
        return position().toArray();
    }


    public void setSortValue(float theSortValue) {
        _mySortValue = theSortValue;
    }
}
