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
import gestalt.material.Material;
import gestalt.render.Drawable;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import java.io.Serializable;
import java.util.Vector;

import static gestalt.Gestalt.*;


public abstract class AbstractShape
        implements Drawable,
                   Serializable {

    protected TransformMatrix4f transform;

    protected Vector3f scale;

    protected Vector3f rotation;

    protected Material material;

    protected int _myTransformMode;

    protected float _mySortValue;

    protected boolean _myIsActive;

    protected Vector<Drawable> _myChildren;

    public AbstractShape() {
        transform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        scale = new Vector3f(1, 1, 1);
        rotation = new Vector3f(0, 0, 0);

        material = new Material();

        _myTransformMode = SHAPE_TRANSFORM_MATRIX_AND_ROTATION;
        _myIsActive = true;
    }

    /**
     * returns a reference to the material.
     *
     * @return Material
     */
    public Material material() {
        return material;
    }

    /**
     * sets the referenced material for this shape.
     *
     * @param theMaterial Material
     */
    public void setMaterialRef(Material theMaterial) {
        material = theMaterial;
    }

    /**
     * returns the reference to the transform matrix used by this shape.
     *
     * @return TransformMatrix4f
     */
    public TransformMatrix4f transform() {
        return transform;
    }

    /**
     * sets the referenced transform matrix for this shape.
     *
     * @param theTransform TransformMatrix4f
     */
    public void setTransformRef(TransformMatrix4f theTransform) {
        transform = theTransform;
    }

    /**
     * returns the position as stored in the 'translation' of the matrix
     * returned by 'transform()'.
     *
     * @return Vector3f
     */
    public Vector3f position() {
        return transform.translation;
    }

    public void position(float x, float y, float z) {
        transform.translation.set(x, y, z);
    }

    public void position(float x, float y) {
        transform.translation.set(x, y);
    }

    public void position(final Vector3f thePosition) {
        transform.translation.set(thePosition);
    }

    /**
     * sets the referenced position that is stored in the 'translation'
     * of the matrix returned by 'transform()'.
     *
     * @param thePosition Vector3f
     */
    public void setPositionRef(Vector3f thePosition) {
        transform.translation = thePosition;
    }

    /**
     * returns the reference to the scale.
     *
     * @return Vector3f
     */
    public Vector3f scale() {
        return scale;
    }

    public void scale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public void scale(float x, float y) {
        scale.set(x, y);
    }

    /**
     * sets the referenced scale for this shape.
     *
     * @param theScale Vector3f
     */
    public void setScaleRef(Vector3f theScale) {
        scale = theScale;
    }

    /**
     * returns the reference to the rotation.<br/>
     * note that this is not the same as the rotation stored in the
     * transform matrix. this rotation defines the rotation about
     * the x, y and z axis in radiants in this order.<br/>
     * the rotation from the matrix and this rotation can be used
     * at the same time.<br/>
     * to specify this behavior 'setTransformMode(int theTransformMode)'
     * can be used.<br/>
     *
     * @return Vector3f
     */
    public Vector3f rotation() {
        return rotation;
    }

    public void rotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    /**
     * sets the referenced rotation for this shape.
     *
     * @param theRotation Vector3f
     */
    public void setRotationRef(Vector3f theRotation) {
        rotation = theRotation;
    }

    /**
     * sets the transform mode.
     * see 'rotation()'
     *
     * @param theTransformMode int
     */
    public void setTransformMode(int theTransformMode) {
        _myTransformMode = theTransformMode;
    }

    /**
     * gets the transform mode.
     *
     * @return int
     */
    public int getTransformMode() {
        return _myTransformMode;
    }

    /**
     * sets the active state of the shape.
     *
     * @param theVisibility boolean
     */
    public void setActive(boolean theVisibility) {
        _myIsActive = theVisibility;
    }

    /**
     * sets a reference to a container of children used by 'add()' and 'remove()'.
     * @todo removed the generic type Vector<Drawable> for 1.4 backwards
     * compatibilty. hmmmm.
     *
     * @param theChildrenContainer Vector
     */
    public void setChildContainer(Vector<Drawable> theChildrenContainer) {
        _myChildren = theChildrenContainer;
    }


    /* sortable interface */
    public float getSortValue() {
        return _mySortValue;
    }

    /**
     * returns the position of the shape as an array.
     * this value is used to calculate a sort value.
     *
     * @return float[]
     */
    public float[] getSortData() {
        return transform.translation.toArray();
    }

    public void setSortValue(float theSortValue) {
        _mySortValue = theSortValue;
    }

    /**
     * returns whether the this shape is transparent.
     *
     * @return boolean
     */
    public boolean isSortable() {
        if (material == null) {
            return false;
        }
        return material.transparent;
    }


    /* drawable interface */
    public boolean isActive() {
        return _myIsActive;
    }

    /**
     * adds a drawable to the container specified by
     * 'setChildContainer(Vector theChildrenContainer)'
     *
     * @param theDrawable Drawable
     */
    public void add(Drawable theDrawable) {
        if (_myChildren != null) {
            _myChildren.add(theDrawable);
        } else {
            System.err.println("### ERROR @ " + this.getClass() + " / must create and set child container first.");
        }
    }

//    /**
//     * removes a drawable from the container specified by
//     * 'setChildContainer(Vector theChildrenContainer)'
//     * @param theDrawable Drawable
//     * @return boolean
//     */
//    public Drawable remove(Drawable theDrawable) {
//        if (_myChildren != null) {
//            if (_myChildren.remove(theDrawable)) {
//                return theDrawable;
//            } else {
//                return null;
//            }
//        } else {
//            System.err.println("### ERROR @ " + this.getClass() + " / must create and set child container first.");
//            return null;
//        }
//    }
    /**
     * invokes the 'draw' methods of all children stored in the container specified
     * by 'setChildContainer(Vector theChildrenContainer)'.
     *
     * @param theRenderContext GLContext
     */
    protected void drawChildren(final GLContext theRenderContext) {
        if (_myChildren != null) {
            for (int i = 0; i < _myChildren.size(); i++) {
                final Drawable myDrawable = _myChildren.get(i);
                if (myDrawable.isActive()) {
                    myDrawable.draw(theRenderContext);
                }
            }
        }
    }
}
