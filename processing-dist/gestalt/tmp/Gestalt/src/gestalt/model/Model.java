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


package gestalt.model;


import java.util.Vector;

import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.shape.Mesh;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class Model
    implements Drawable {

    private boolean _myIsActive;

    private BoundingBoxData _myBoundingBoxData;

    private BoundingBoxView _myBoundingBoxView;

    protected final Mesh _myModelView;

    private boolean isInitialized;

    private boolean isBoundingBoxCalculated;

    private int _myViewStart;

    private int _myViewLength;

    private ModelData _myModelData;

    public Model(ModelData theModelData, Mesh theModelView) {
        _myModelData = theModelData;
        _myModelView = theModelView;

        _myIsActive = true;
        isBoundingBoxCalculated = false;
        isInitialized = false;

        showAllFrames(true);

        _myBoundingBoxData = new BoundingBoxData();
        calculateBoundingBox();
    }


    public void showAllFrames(boolean theShowAllFrames) {
        if (theShowAllFrames) {
            _myViewStart = 0;
            _myViewLength = _myModelData.vertices.length / _myModelData.numberOfVertexComponents;
        } else {
            _myViewStart = 0;
            _myViewLength = _myModelData.numberOfVerticesPerFrame;
        }
    }


    public int getNumberOfFrames() {
        return _myModelData.numberOfObjects;
    }


    public int getNumberOfVerticesPerFrame() {
        return _myModelData.numberOfVerticesPerFrame;
    }


    public String name() {
        return _myModelData.name;
    }


//    public int[][] getAnimationTimes() {
//        return _myModelData._myAnimationTimes;
//    }


    public void setAnimations(final Vector<ModelAnimation> theAnimations) {
        _myModelData.animations = theAnimations;
    }


    public Vector<ModelAnimation> getAnimations() {
        return _myModelData.animations;
    }


    public void setViewStart(int theStart) {
        _myViewStart = theStart;
    }


    public void setViewLength(int theLength) {
        _myViewLength = theLength;
    }


    public void setBoundingBoxView(BoundingBoxView theBoundingBoxView) {
        _myBoundingBoxView = theBoundingBoxView;
        calculateBoundingBox();
    }


    public void removeBoundingBoxView() {
        _myBoundingBoxView = null;
    }


    public void calculateBoundingBox() {
        float MAX_VALUE = 10000000;
        float MIN_VALUE = -10000000;
        float myMinX = MAX_VALUE;
        float myMaxX = MIN_VALUE;
        float myMinY = MAX_VALUE;
        float myMaxY = MIN_VALUE;
        float myMinZ = MAX_VALUE;
        float myMaxZ = MIN_VALUE;

        TransformMatrix4f myTransform = new TransformMatrix4f();
        myTransform.translation.set(_myModelView.position());
        myTransform.rotation.setXYZRotation(_myModelView.rotation());

        int myVertexIndex = _myViewStart * _myModelData.numberOfVertexComponents;

        for (int i = 0; i < _myViewLength; i++) {
            Vector3f myVertex = new Vector3f();
            myVertex.x = _myModelView.vertices()[myVertexIndex + 0];
            myVertex.y = _myModelView.vertices()[myVertexIndex + 1];
            myVertex.z = _myModelView.vertices()[myVertexIndex + 2];

            myTransform.transform(myVertex);
            myVertex.scale(_myModelView.scale());

            /* x */
            if (myVertex.x < myMinX) {
                myMinX = myVertex.x;
            } else if (myVertex.x > myMaxX) {
                myMaxX = myVertex.x;
            }

            /* y */
            if (myVertex.y < myMinY) {
                myMinY = myVertex.y;
            } else if (myVertex.y > myMaxY) {
                myMaxY = myVertex.y;
            }

            /* z */
            if (myVertex.z > myMaxZ) {
                myMaxZ = myVertex.z;
            } else if (myVertex.z < myMinZ) {
                myMinZ = myVertex.z;
            }

            myVertexIndex += _myModelData.numberOfVertexComponents;
        }

        _myBoundingBoxData.p0.set(myMinX, myMaxY, myMaxZ);
        _myBoundingBoxData.p1.set(myMinX, myMinY, myMaxZ);
        _myBoundingBoxData.p2.set(myMaxX, myMinY, myMaxZ);
        _myBoundingBoxData.p3.set(myMaxX, myMaxY, myMaxZ);
        _myBoundingBoxData.p4.set(myMinX, myMaxY, myMinZ);
        _myBoundingBoxData.p5.set(myMinX, myMinY, myMinZ);
        _myBoundingBoxData.p6.set(myMaxX, myMinY, myMinZ);
        _myBoundingBoxData.p7.set(myMaxX, myMaxY, myMinZ);

        /* calculate center */
        Vector3f myCenter = new Vector3f();
        myCenter.add(_myBoundingBoxData.p0);
        myCenter.add(_myBoundingBoxData.p1);
        myCenter.add(_myBoundingBoxData.p2);
        myCenter.add(_myBoundingBoxData.p3);
        myCenter.add(_myBoundingBoxData.p4);
        myCenter.add(_myBoundingBoxData.p5);
        myCenter.add(_myBoundingBoxData.p6);
        myCenter.add(_myBoundingBoxData.p7);
        myCenter.scale(1f / 8f);
        _myBoundingBoxData.center.set(myCenter);

        /* calculate size */
        Vector3f mySize = new Vector3f();
        mySize.x = myMaxX - myMinX;
        mySize.y = myMaxY - myMinY;
        mySize.z = myMaxZ - myMinZ;
        _myBoundingBoxData.size.set(mySize);

        isBoundingBoxCalculated = true;
    }


    public void draw(final GLContext theRenderContext) {
        _myModelView.drawstart(_myViewStart);
        _myModelView.drawlength(_myViewLength);

        if (!isInitialized) {
            isInitialized = true;
            calculateBoundingBox();
        }
        _myModelView.draw(theRenderContext);

        /* draw bounding box */
        if (_myBoundingBoxView != null) {
            _myBoundingBoxView.draw(theRenderContext);
        }
    }


    /**
     * @deprecated
     * @return Mesh
     */
    public Mesh getView() {
        return _myModelView;
    }


    public Mesh mesh() {
        return _myModelView;
    }


    public BoundingBoxData getBoundingBoxData() {
        if (!isBoundingBoxCalculated) {
            calculateBoundingBox();
        }
        return _myBoundingBoxData;
    }


    public void setActive(boolean theActiveState) {
        _myIsActive = theActiveState;
    }


    public void add(Drawable theDrawable) {
    }


    public boolean isActive() {
        return _myIsActive;
    }


    public float getSortValue() {
        return _myModelView.getSortValue();
    }


    public void setSortValue(float theSortValue) {
        _myModelView.setSortValue(theSortValue);
    }


    public float[] getSortData() {
        return _myModelView.getSortData();
    }


    public boolean isSortable() {
        return _myModelView.isSortable();
    }
}
