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

import static gestalt.Gestalt.*;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.Sort;
import gestalt.render.controller.Camera;
import gestalt.util.CameraUtil;

import mathematik.Vector3f;


/**
 * a bin that handles drawables.
 * it can also sort its content.
 */

public class ShapeBin
    implements Bin {

    protected boolean _myIsActive;

    private final int _myArrayBlockSize;

    protected Drawable[] _myDrawables;

    private int _mySortStyle;

    private boolean _mySortFlag;

    protected int _myDrawableCounter;

    private final Vector3f _myTransformedPosition;

    public ShapeBin() {
        this(500);
    }


    public ShapeBin(int theArrayBlockSize) {
        _mySortStyle = SHAPEBIN_SORT_BY_Z_DISTANCE_TO_CAMERAPLANE;
        _mySortFlag = true;
        _myDrawableCounter = 0;
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


    public void draw(GLContext theContext) {
        /* sort shapes */
        if (_mySortFlag &&
            theContext != null &&
            theContext.camera != null) {
            sort(theContext.camera);
        }

        /* draw shapes */
        final Drawable[] mySortables = getDataRef();
        for (int j = 0; j < size(); j++) {
            if (j < mySortables.length && j >= 0) {
                final Drawable mySortable = mySortables[j];
                if (mySortable != null) {
                    if (mySortable.isActive()) {
                        mySortable.draw(theContext);
                    }
                } else {
                    System.err.println("### ERROR @ ShapeBin.draw / drawable #" + j + " is not initialized!");
                }
            }
        }
    }


    public boolean isActive() {
        return _myIsActive;
    }


    public void setActive(boolean theState) {
        _myIsActive = theState;
    }


    public void sort(Camera theCamera) {
        switch (_mySortStyle) {
            case SHAPEBIN_SORT_BY_Z_POSITION:
                sortByZPosition();
                break;
            case SHAPEBIN_SORT_BY_DISTANCE_TO_CAMERA:
                sortByDistanceToCamera(theCamera);
                break;
            case SHAPEBIN_SORT_BY_Z_DISTANCE_TO_CAMERAPLANE:
                sortBinByZDistanceToCameraPlane(theCamera);
                break;
        }
    }


    private void sortByZPosition() {
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
            Sort.shellSort(_myDrawables, 0, _myDrawableCounter);
        }
    }


    private void sortByDistanceToCamera(Camera theCamera) {
        boolean isSortable = true;
        float[] myCameraPosition = theCamera.position().toArray();
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] != null) {
                float[] myObjectPosition = _myDrawables[i].getSortData();
                if (myObjectPosition != null) {
                    _myDrawables[i].setSortValue( - ( (myCameraPosition[0] - myObjectPosition[0])
                                                     * (myCameraPosition[0] - myObjectPosition[0]) +
                                                     (myCameraPosition[1] - myObjectPosition[1])
                                                     * (myCameraPosition[1] - myObjectPosition[1]) +
                                                     (myCameraPosition[2] - myObjectPosition[2])
                                                     * (myCameraPosition[2] - myObjectPosition[2])));
                }
            } else {
                isSortable = false;
            }

        }
        if (isSortable) {
            Sort.shellSort(_myDrawables, 0, _myDrawableCounter);
        }
    }


    private void sortBinByZDistanceToCameraPlane(Camera theCamera) {
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
            Sort.shellSort(_myDrawables, 0, _myDrawableCounter);
        }
    }


    /* --> abstractbin obligations */

    public void add(Drawable[] theSortables) {
        for (int i = 0; i < theSortables.length; ++i) {
            add(theSortables[i]);
        }
    }


    public void add(Drawable theShape) {
        if (_myDrawableCounter >= _myDrawables.length) {
            Drawable[] myCopyArray = new Drawable[_myDrawables.length + _myArrayBlockSize];
            System.arraycopy(_myDrawables, 0, myCopyArray, 0, _myDrawables.length);
            _myDrawables = myCopyArray;
        }
        _myDrawables[_myDrawableCounter] = (Drawable) theShape;
        _myDrawableCounter++;
    }


    public void remove(Drawable[] theSortables) {
        for (int i = 0; i < theSortables.length; ++i) {
            remove(theSortables[i]);
        }
    }


    public Drawable remove(int theIndex) {
        final Drawable myRemovedSortable = _myDrawables[theIndex];
        --_myDrawableCounter;
        for (; theIndex < _myDrawableCounter; ++theIndex) {
            _myDrawables[theIndex] = _myDrawables[theIndex + 1];
        }
        return myRemovedSortable;
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


    public int find(Drawable theDrawable) {
        for (int i = 0; i < _myDrawableCounter; ++i) {
            if (_myDrawables[i] == theDrawable) {
                return i;
            }
        }
        return -1;
    }


    public void swap(int theIDA, int theIDB) {
        Drawable myShape = _myDrawables[theIDA];
        _myDrawables[theIDA] = _myDrawables[theIDB];
        _myDrawables[theIDB] = myShape;
    }


    public void swap(Drawable theDrawableA, Drawable theDrawableB) {
        if (theDrawableA == theDrawableB) {
            return;
        }
        int myIDA = find(theDrawableA);
        int myIDB = find(theDrawableB);
        if (myIDA != -1 && myIDB != -1) {
            swap(myIDA, myIDB);
        }
    }


    public int size() {
        return _myDrawableCounter;
    }


    public Drawable get(int i) {
        try {
            return _myDrawables[i];
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ ShapeBin.get / element out of bound: " + ex);
            return null;
        }
    }


    public void set(int theID, Drawable theDrawable) {
        try {
            _myDrawables[theID] = theDrawable;
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ ShapeBin.set / element out of bound: " + ex);
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


    /**
     * get the array size by the size() method not by .length
     *
     * @return Sortable[]
     */
    public Drawable[] getDataRef() {
        return _myDrawables;
    }


    public String toString() {
        StringBuffer myStringBuffer = new StringBuffer("SHAPEBIN\n");

        if (_myDrawableCounter > 0) {
            for (int i = 0; i < _myDrawableCounter; ++i) {
                myStringBuffer.append(_myDrawables[i].toString());
                myStringBuffer.append('\n');
            }
        } else {
            myStringBuffer.append("<empty>");
            myStringBuffer.append('\n');
        }
        return myStringBuffer.toString();
    }


    /* unsued methods */

    public float getSortValue() {
        return 0.0f;
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
