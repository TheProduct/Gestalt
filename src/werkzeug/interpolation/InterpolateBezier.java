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


public class InterpolateBezier
    implements InterpolatorKernel, Serializable {

    private static final long serialVersionUID = 6358831227147356975L;

    private final float _myStartControl;

    private final float _myEndControl;

    public InterpolateBezier(final float theStartControl, final float theEndControl) {
        _myStartControl = theStartControl;
        _myEndControl = theEndControl;
    }


    public float get(final float theDelta) {
        final float c = 3 * _myStartControl;
        final float b = 3 * (_myEndControl - _myStartControl) - c;
        final float a = 1 - c - b;
        return a * theDelta * theDelta * theDelta + b * theDelta * theDelta + c * theDelta;
    }
}
