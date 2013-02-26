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


import gestalt.context.GLContext;
import gestalt.render.Drawable;


/**
 * a bin that handles drawables.
 */

public class RenderBin
    implements Bin {

    private final int _myArrayBlockSize;

    protected Drawable[] _myDrawables;

    protected int _myDrawableCounter;

    protected boolean _myIsActive = true;

    public RenderBin() {
        this(500);
    }


    public RenderBin(int theArrayBlockSize) {
        _myDrawableCounter = 0;
        if (theArrayBlockSize < 1) {
            _myArrayBlockSize = 10;
        } else {
            _myArrayBlockSize = theArrayBlockSize;
        }
        _myDrawables = new Drawable[_myArrayBlockSize];
    }


    public void add(Drawable[] theSortables) {
        for (int i = 0; i < theSortables.length; ++i) {
            add(theSortables[i]);
        }
    }


    public void add(Drawable theSortable) {
        if (_myDrawableCounter >= _myDrawables.length) {
            Drawable[] myCopyArray = new Drawable[_myDrawables.length + _myArrayBlockSize];
            System.arraycopy(_myDrawables, 0,
                             myCopyArray, 0,
                             _myDrawables.length);
            _myDrawables = myCopyArray;
        }
        _myDrawables[_myDrawableCounter] = theSortable;
        _myDrawableCounter++;
    }


    public void remove(Drawable[] theSortables) {
        for (int i = 0; i < theSortables.length; ++i) {
            remove(theSortables[i]);
        }
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


    public Drawable remove(int theIndex) {
        final Drawable myRemovedSortable = _myDrawables[theIndex];
        --_myDrawableCounter;
        for (; theIndex < _myDrawableCounter; ++theIndex) {
            _myDrawables[theIndex] = _myDrawables[theIndex + 1];
        }
        return myRemovedSortable;
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


    public void set(int theID, Drawable theDrawable) {
        try {
            _myDrawables[theID] = theDrawable;
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ RenderBin.set / element out of bound: " + ex);
        }
    }


    public void replace(Drawable theDrawableOld, Drawable theDrawableNew) {
        final int myIDOld = find(theDrawableOld);
        if (myIDOld != -1) {
            _myDrawables[myIDOld] = theDrawableNew;
        }
    }


    public Drawable get(int theID) {
        try {
            return _myDrawables[theID];
        } catch (NullPointerException ex) {
            System.err.println("### ERROR @ RenderBin.get / element out of bound: " + ex);
            return null;
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
     * @return Sortable[]
     */
    public Drawable[] getDataRef() {
        return _myDrawables;
    }


    public String toString() {
        StringBuffer myStringBuffer = new StringBuffer("RENDERBIN\n");

        if (_myDrawableCounter > 0) {
            for (int i = 0; i < _myDrawableCounter; ++i) {
                if (_myDrawables[i] != null) {
                    myStringBuffer.append(_myDrawables[i].toString());
                } else {
                    myStringBuffer.append(_myDrawables[i]);
                }
                myStringBuffer.append('\n');
            }
        } else {
            myStringBuffer.append("<empty>");
            myStringBuffer.append('\n');
        }
        return myStringBuffer.toString();
    }


    public void draw(GLContext theRenderContext) {
        drawShapes(theRenderContext);
    }


    private final void drawShapes(GLContext theRenderContext) {
        final Drawable[] myDrawables = getDataRef();
        for (int j = 0; j < size(); j++) {
            if (j < myDrawables.length && j >= 0) {
                final Drawable myDrawable = myDrawables[j];
                if (myDrawable != null) {
                    if (myDrawable.isActive()) {
                        myDrawable.draw(theRenderContext);
                    }
                } else {
                    System.err.println("### ERROR @ RenderBin.draw / drawable #" + j + " is not initialized!");
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


    /* unused methods */

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
