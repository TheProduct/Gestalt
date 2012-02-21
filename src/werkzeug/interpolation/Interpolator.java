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


import java.io.Serializable;


public class Interpolator
    implements InterpolatorKernel, Serializable {

    private final float _myStart;

    private final float _myEnd;

    private final float _myDifference;

    private InterpolatorKernel _myKernel;

    public Interpolator(float theStart, float theEnd, InterpolatorKernel theKernel) {
        _myStart = theStart;
        _myEnd = theEnd;
        _myDifference = _myEnd - _myStart;
        _myKernel = theKernel;
    }


    public Interpolator(InterpolatorKernel theKernel) {
        this(0, 1, theKernel);
    }


    public void setKernel(InterpolatorKernel theKernel) {
        _myKernel = theKernel;
    }


    public float get(final float theDelta) {
        return _myStart + _myDifference * _myKernel.get(theDelta);
    }
}
