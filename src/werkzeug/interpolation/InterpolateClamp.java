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


public class InterpolateClamp
    implements InterpolatorKernel, Serializable {

    private static final long serialVersionUID = 7629226103013387934L;

    private final float _myMin;

    private final float _myMax;

    public InterpolateClamp(final float theMin, final float theMax) {
        _myMin = theMin;
        _myMax = theMax;
    }


    public float get(final float theDelta) {
        if (theDelta <= _myMin) {
            return 0.0f;
        } else if (theDelta >= _myMax) {
            return 1.0f;
        } else {
            return (theDelta - _myMin) / (_myMax - _myMin);
        }
    }
}
