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


public class InterpolatePeak
    implements InterpolatorKernel, Serializable {

    private static final long serialVersionUID = -1379387791349309032L;

    private final float _myPeak;

    public InterpolatePeak(final float thePeak) {
        _myPeak = thePeak;
    }


    public float get(float theDelta) {
        if (theDelta < _myPeak) {
            theDelta = theDelta / _myPeak;
        } else {
            theDelta = 1.0f - (theDelta - _myPeak) / (1.0f - _myPeak);
        }
        return theDelta;
    }
}
