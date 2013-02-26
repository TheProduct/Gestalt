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


package gestalt.extension.picking;


public class PickingBin {

    private final int _myArrayBlockSize;

    private Pickable[] _myPickables;

    private int _myShapeCounter;

    public PickingBin() {
        this(10);
    }


    public PickingBin(int theArrayBlockSize) {
        _myShapeCounter = 0;
        if (theArrayBlockSize < 1) {
            _myArrayBlockSize = 10;
        } else {
            _myArrayBlockSize = theArrayBlockSize;
        }
        _myPickables = new Pickable[_myArrayBlockSize];
    }


    public void add(Pickable[] thePickables) {
        for (int i = 0; i < thePickables.length; ++i) {
            add(thePickables[i]);
        }
    }


    public void add(Pickable theShape) {
        if (theShape instanceof Pickable) {
            if (_myShapeCounter >= _myPickables.length) {
                Pickable[] myCopyArray = new Pickable[_myPickables.length + _myArrayBlockSize];
                System.arraycopy(_myPickables, 0,
                                 myCopyArray, 0,
                                 _myPickables.length);
                _myPickables = myCopyArray;
            }
            _myPickables[_myShapeCounter] = (Pickable) theShape;
            _myShapeCounter++;
        } else {
            System.err.println("### WARNING  @ PickingBin.add /" +
                               "tried to add a openglpickable " +
                               "that is not of type 'OpenGLPickable': " + theShape);
        }
    }


    public void remove(Pickable[] thePickables) {
        for (int i = 0; i < thePickables.length; ++i) {
            remove(thePickables[i]);
        }
    }


    public void remove(Pickable drawable) {
        for (int i = 0; i < _myShapeCounter; ++i) {
            if (_myPickables[i] == drawable) {
                --_myShapeCounter;
                for (; i < _myShapeCounter; ++i) {
                    _myPickables[i] = _myPickables[i + 1];
                }
                break;
            }
        }
        /** @todo we might want to downsize the array sometimes */
    }


    public int size() {
        return _myShapeCounter;
    }


    public Pickable get(int i) {
        try {
            return _myPickables[i];
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ PickingBin.get / element out of bound: " + ex);
            return null;
        }
    }


    public void clear() {
        for (int i = 0; i < _myShapeCounter; ++i) {
            _myPickables[i] = null;
        }
        _myShapeCounter = 0;
    }


    /**
     * get the array size by the size() method not by .length
     * @return Drawable[]
     */
    public Pickable[] getDataRef() {
        return _myPickables;
    }


    public String toString() {
        StringBuffer myStringBuffer = new StringBuffer("PICKINGBIN\n");

        if (_myShapeCounter > 0) {
            for (int i = 0; i < _myShapeCounter; ++i) {
                myStringBuffer.append(_myPickables[i].toString());
                myStringBuffer.append('\n');
            }
        } else {
            myStringBuffer.append("<empty>");
            myStringBuffer.append('\n');
        }
        return myStringBuffer.toString();
    }
}
