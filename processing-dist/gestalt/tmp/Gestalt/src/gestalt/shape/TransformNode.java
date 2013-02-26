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


package gestalt.shape;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.util.JoglUtil;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import java.util.Arrays;
import java.util.Vector;

import javax.media.opengl.GL;


public class TransformNode
        extends AbstractShape
        implements Bin {

    public TransformNode() {
        _myChildren = new Vector<Drawable>();
    }

    public Vector<Drawable> children() {
        return _myChildren;
    }

    public TransformMatrix4f transformLocalPositionToWorldPosition(Vector3f thePosition) {
        return JoglUtil.applyTransform(this, thePosition);
    }

    public void add(Drawable theDrawable) {
        _myChildren.add(theDrawable);
    }


    /* implement sorting by using the first shape in the list */
    public float getSortValue() {
        if (!_myChildren.isEmpty()) {
            return _myChildren.get(0).getSortValue();
        }
        return 0;
    }

    public void setSortValue(float theSortValue) {
        if (!_myChildren.isEmpty()) {
            _myChildren.get(0).setSortValue(theSortValue);
        }
    }

    public float[] getSortData() {
        if (_myChildren.isEmpty()) {
            return null;
        } else {
            return _myChildren.get(0).getSortData();
        }
    }

    public boolean isSortable() {
        return material.transparent;
    }

    public void add(Drawable[] theDrawables) {
        _myChildren.addAll(Arrays.asList(theDrawables));
    }

    public Drawable remove(int theIndex) {
        return _myChildren.remove(theIndex);
    }

    public void remove(Drawable[] theDrawables) {
        _myChildren.remove(theDrawables);
    }

    public Drawable remove(Drawable theDrawable) {
        if (_myChildren != null) {
            if (_myChildren.remove(theDrawable)) {
                return theDrawable;
            } else {
                return null;
            }
        } else {
            System.err.println("### ERROR @ " + this.getClass() + " / must create and set child container first.");
            return null;
        }
    }

    public void set(int theID, Drawable theDrawable) {
        _myChildren.set(theID, theDrawable);
    }

    public void swap(int theIDA, int theIDB) {
        final Drawable myShape = _myChildren.get(theIDA);
        set(theIDA, _myChildren.get(theIDB));
        set(theIDB, myShape);
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

    public void replace(Drawable theDrawableOld, Drawable theDrawableNew) {
        final int myIDOld = find(theDrawableOld);
        if (myIDOld != -1) {
            set(myIDOld, theDrawableNew);
        }
    }

    public int find(Drawable theDrawable) {
        return _myChildren.indexOf(theDrawable);
    }

    public int size() {
        return _myChildren.size();
    }

    public Drawable get(int theID) {
        return _myChildren.get(theID);
    }

    public void clear() {
        _myChildren.clear();
    }

    public Drawable[] getDataRef() {
        return _myChildren.toArray(new Drawable[_myChildren.size()]);
    }

    public String toString() {
        StringBuffer myString = new StringBuffer();
        myString.append("Transform Node\n");

        for (Drawable myShape : _myChildren) {
            if (myShape != null && myShape.isActive()) {
                myString.append(myShape);
                myString.append('\n');
            }
        }

        return myString.toString();
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* draw shape */
        for (int i = 0; i < _myChildren.size(); i++) {
            final Drawable myShape = (Drawable)_myChildren.get(i);
            if (myShape != null && myShape.isActive()) {
                myShape.draw(theRenderContext);
            }
        }

        gl.glPopMatrix();
    }
}
