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


package gestalt.render.bin;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.Sort;
import gestalt.render.controller.Camera;
import gestalt.util.CameraUtil;

import mathematik.Vector3f;


/**
 * a bin that handles drawables. in addition this bin stores unsorted
 * drawables in one half of the list and sorted drawables in the other.
 * the bin must be updated as a drawable to execute the sorting algoritm.
 */
public class TwoSidedBin
        implements Bin,
                   Gestalt {

    private boolean _myIsActive;

    private final int _myArrayBlockSize;

    private Drawable[] _myDrawables;

    private int _mySortStyle;

    private boolean _mySortFlag;

    private int _myDrawableCounter;

    private int _myDivider;

    private final Vector3f _myTransformedPosition;

    public TwoSidedBin() {
        this(500);
    }

    public TwoSidedBin(int theArrayBlockSize) {
        _mySortStyle = SHAPEBIN_SORT_BY_Z_DISTANCE_TO_CAMERAPLANE;
        _mySortFlag = true;
        _myDrawableCounter = 0;
        _myDivider = 0;
        if (theArrayBlockSize < 1) {
            _myArrayBlockSize = 10;
        } else {
            _myArrayBlockSize = theArrayBlockSize;
        }
        _myDrawables = new Drawable[_myArrayBlockSize];
        _myIsActive = true;
        _myTransformedPosition = new Vector3f();
    }

    public void setSortFlag(boolean theSortFlag) {
        _mySortFlag = theSortFlag;
    }

    public boolean getSortFlag() {
        return _mySortFlag;
    }

    public void setSortStyle(int theSortStyle) {
        _mySortStyle = theSortStyle;
    }

    private void sort(Camera theCamera,
                      int theStart,
                      int theEnd) {
        switch (_mySortStyle) {
            case SHAPEBIN_SORT_BY_Z_POSITION:
                sortByZPosition(theStart, theEnd);
                break;
            case SHAPEBIN_SORT_BY_DISTANCE_TO_CAMERA:
                sortByDistanceToCamera(theCamera, theStart, theEnd);
                break;
            case SHAPEBIN_SORT_BY_Z_DISTANCE_TO_CAMERAPLANE:
                sortBinByZDistanceToCameraPlane(theCamera, theStart, theEnd);
                break;
        }
    }

    private void sortByZPosition(int theStart,
                                 int theEnd) {
        boolean isSortable = true;
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] != null) {
                float[] myObjectPosition = _myDrawables[i].getSortData();
                if (myObjectPosition != null) {
                    _myDrawables[i].setSortValue(myObjectPosition[2]);
                }
            } else {
                isSortable = false;
            }
        }
        if (isSortable) {
            Sort.shellSort(_myDrawables, theStart, theEnd);
        }
    }

    private void sortByDistanceToCamera(Camera theCamera,
                                        int theStart,
                                        int theEnd) {
        boolean isSortable = true;
        float[] myCameraPosition = theCamera.position().toArray();
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] != null) {
                float[] myObjectPosition = _myDrawables[i].getSortData();
                if (myObjectPosition != null) {
                    _myDrawables[i].setSortValue(-((myCameraPosition[0] - myObjectPosition[0])
                            * (myCameraPosition[0] - myObjectPosition[0])
                            + (myCameraPosition[1] - myObjectPosition[1])
                            * (myCameraPosition[1] - myObjectPosition[1])
                            + (myCameraPosition[2] - myObjectPosition[2])
                            * (myCameraPosition[2] - myObjectPosition[2])));
                }
            } else {
                isSortable = false;
            }

        }
        if (isSortable) {
            Sort.shellSort(_myDrawables, theStart, theEnd);
        }
    }

    private void sortBinByZDistanceToCameraPlane(Camera theCamera,
                                                 int theStart,
                                                 int theEnd) {
        boolean isSortable = true;
        float[] myObjectPosition;
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] != null) {
                myObjectPosition = _myDrawables[i].getSortData();
                if (myObjectPosition != null) {
                    _myTransformedPosition.set(myObjectPosition);
                    CameraUtil.toCameraSpace(theCamera, _myTransformedPosition);
                    _myDrawables[i].setSortValue(_myTransformedPosition.z);
                }
            } else {
                isSortable = false;
            }
        }
        if (isSortable) {
            Sort.shellSort(_myDrawables, theStart, theEnd);
        }
    }

    public void swap(Drawable theShapeA,
                     Drawable theShapeB) {
        if (theShapeA == theShapeB) {
            return;
        }
        int myIDA = -1;
        int myIDB = -1;
        int i = 0;
        while (i < _myDrawableCounter && (myIDA == -1 || myIDB == -1)) {
            if (_myDrawables[i] == theShapeA) {
                myIDA = i;
            }
            if (_myDrawables[i] == theShapeB) {
                myIDB = i;
            }
            ++i;
        }
        if (myIDA != -1 && myIDB != -1) {
            swap(myIDA, myIDB);
        }
    }

    public void swap(int theIDA,
                     int theIDB) {
        Drawable myShape = _myDrawables[theIDA];
        _myDrawables[theIDA] = _myDrawables[theIDB];
        _myDrawables[theIDB] = myShape;
    }

    public int find(Drawable theDrawable) {
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] == theDrawable) {
                return i;
            }
        }
        return -1;
    }

    public void draw(GLContext theContext) {
        /* sort drawables */
        if (_mySortFlag
                && theContext != null
                && theContext.camera != null) {
            /* clean up nontransparent shapes */
            for (int i = 0; i < _myDivider; ++i) {
                final Drawable myShape = _myDrawables[i];
                if (!myShape.isSortable()) {
                    /* nothing */
                } else if (myShape.isSortable()) {
                    if (i == _myDivider - 1) {
                        /* last in segment */
                        --_myDivider;
                    } else if (_myDrawables[_myDivider - 1].isSortable()) {
                        /*
                         * last element is transparent -- swapping wouldn t change
                         * anything
                         */
                        --_myDivider;
                        --i;
                    } else {
                        /* swap with last element */
                        swap(i, _myDivider - 1);
                        --_myDivider;
                    }
                } else {
                    System.err.println("### WARNING transparency setting not recognized.");
                }
            }

            /* clean up transparent shapes */
            for (int i = _myDivider; i < _myDrawableCounter; ++i) {
                Drawable myShape = _myDrawables[i];
                if (!myShape.isSortable()) {
                    if (i == _myDivider) {
                        /* last in segment */
                        ++_myDivider;
                    } else if (!_myDrawables[_myDivider].isSortable()) {
                        /** @todo can this happen? - and is the response correct? */
                        ++_myDivider;
                        --i;
                    } else {
                        swap(i, _myDivider);
                        ++_myDivider;
                    }
                } else if (myShape.isSortable()) {
                    /* nothing */
                } else {
                    System.err.println("### WARNING transparency setting not recognized.");
                }
            }

            /* sort shapes */
            if (theContext != null && theContext.camera != null) {
                sort(theContext.camera, _myDivider, _myDrawableCounter);
            }
        }

        /* draw shapes */
        Drawable[] myDrawables = getDataRef();
        for (int j = 0; j < size(); j++) {
            Drawable myDrawable = myDrawables[j];
            if (myDrawable != null) {
                if (myDrawable.isActive()) {
                    myDrawable.draw(theContext);
                }
            } else {
                System.err.println("### ERROR @ TwoSidedBin.draw / drawable #" + j + " is not initialized!");
            }
        }
    }

    public boolean isActive() {
        return _myIsActive;
    }

    public void setActive(boolean theState) {
        _myIsActive = theState;
    }


    /* --> abstractbin obligations */
    public void add(Drawable[] theDrawables) {
        for (int i = 0; i < theDrawables.length; ++i) {
            add(theDrawables[i]);
        }
    }

    public void add(Drawable theDrawable) {
        checkArraySize();
        /* add shape as usual */
        _myDrawables[_myDrawableCounter] = theDrawable;

        /* manage transparency */
        if (_myDrawables[_myDrawableCounter] != null) {
            if (!_myDrawables[_myDrawableCounter].isSortable()) {
                swap(_myDrawableCounter, _myDivider);
                _myDivider++;
            } else if (_myDrawables[_myDrawableCounter].isSortable()) {
                /* nothing */
            } else {
                System.err.println("### WARNING transparency not recognized.");
            }
            _myDrawableCounter++;
        } else {
            System.err.println("### WARNING @ TwoSidedBin.add() / drawable is not initalized.");
        }
    }

    private void checkArraySize() {
        if (_myDrawableCounter >= _myDrawables.length) {
            Drawable[] myCopyArray = new Drawable[_myDrawables.length + _myArrayBlockSize];
            System.arraycopy(_myDrawables, 0, myCopyArray, 0, _myDrawables.length);
            _myDrawables = myCopyArray;
        }
    }

    public void remove(Drawable[] theDrawables) {
        for (int i = 0; i < theDrawables.length; i++) {
            remove(theDrawables[i]);
        }
    }

    public Drawable remove(int theIndex) {
        Drawable myRemovedDrawable = _myDrawables[theIndex];

        /* manage transparency */
        if (!myRemovedDrawable.isSortable()) {
            --_myDivider;
        } else if (myRemovedDrawable.isSortable()) {
            /* nothing */
        } else {
            System.err.println("### WARNING transparency not recognized.");
        }

        --_myDrawableCounter;
        for (; theIndex < _myDrawableCounter; ++theIndex) {
            _myDrawables[theIndex] = _myDrawables[theIndex + 1];
        }
        return myRemovedDrawable;
    }

    public Drawable remove(Drawable theDrawable) {
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] == theDrawable) {
                return remove(i);
            }
        }
        /** @todo we might want to downsize the array sometimes */
        return null;
    }

    public int size() {
        return _myDrawableCounter;
    }

    public Drawable get(int theID) {
        try {
            return _myDrawables[theID];
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ TwoSidedBin.get / element out of bound: " + ex);
            return null;
        }
    }

    public void set(int theID, Drawable theDrawable) {
        try {
            _myDrawables[theID] = theDrawable;
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ TwoSidedBin.set / element out of bound: " + ex);
        }
    }

    public void replace(Drawable theDrawableOld, Drawable theDrawableNew) {
        final int myIDOld = find(theDrawableOld);
        if (myIDOld != -1) {
            _myDrawables[myIDOld] = theDrawableNew;
        }
    }

    public void clear() {
        for (int i = 0; i < _myDrawableCounter; ++i) {
            _myDrawables[i] = null;
        }
        _myDrawableCounter = 0;
    }

    public Drawable[] getDataRef() {
        /**
         * get the array size by the size() method not by .length
         *
         * @return Drawable[]
         */
        return _myDrawables;
    }

    public String toString() {
        final StringBuilder myStringBuffer = new StringBuilder(getClass().getSimpleName() + "(" + size() + ")" + "\n");
        if (_myDrawableCounter > 0) {
            for (int i = 0; i < _myDrawableCounter; i++) {
                if (i == _myDivider) {
                    myStringBuffer.append("---\n");
                }
                myStringBuffer.append(_myDrawables[i].toString());
                myStringBuffer.append('\n');
            }
        } else {
            myStringBuffer.append("<empty>");
            myStringBuffer.append('\n');
        }
        return myStringBuffer.toString();
    }


    /* unused methods */
    public float getSortValue() {
        return 0.0F;
    }

    public void setSortValue(float theSortValue) {
    }

    public float[] getSortData() {
        return null;
    }

    public boolean isSortable() {
        return false;
    }
}
