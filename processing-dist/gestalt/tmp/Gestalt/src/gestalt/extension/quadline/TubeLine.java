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


public abstract class TubeLine
        extends AbstractShape {

    public Vector3f[] points;

    public Color[] colors;

    public float[] linewidthset;

    public float linewidth;

    public int steps;

    protected TubeLineProducer mProducer;

    protected boolean mAutoUpdate;

    private QuadFragment[][] mFragments;

    public TubeLine() {
        mProducer = new TubeLineProducer();
        linewidth = 5;
        steps = 3;
        mAutoUpdate = false;
    }

    public void propagateUpVector(boolean theFlag) {
        mProducer.UPVECTOR_PROPAGATION = theFlag;
    }

    public void autoupdate(boolean theAutoUpdate) {
        mAutoUpdate = theAutoUpdate;
    }

    public Vector3f upvector() {
        return mProducer.upvector();
    }

    public void update() {
        if (points != null) {
            mFragments = mProducer.produce(points,
                                           colors,
                                           linewidthset,
                                           linewidth,
                                           steps);
        }
    }

    public TubeLineProducer producer() {
        return mProducer;
    }

    public QuadFragment[][] fragments() {
        return mFragments;
    }


    /* --> Drawable obligation */
    public float getSortValue() {
        return _mySortValue;
    }

    public float[] getSortData() {
        return position().toArray();
    }

    public void setSortValue(float theSortValue) {
        _mySortValue = theSortValue;
    }
}

