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


package gestalt.util.meshcreator;


import java.util.Vector;

import gestalt.Gestalt;
import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.material.Color;
import gestalt.shape.DrawableFactory;
import gestalt.shape.Mesh;

import mathematik.Vector2f;
import mathematik.Vector3f;


public class MeshCreator {

    private final Vector<DrawableMeshTranslator> _myTranslators;

    private final Vector<Float> _myVertexCache;

    private final Vector<Float> _myTextureCoordinatesCache;

    private final Vector<Float> _myNormalCache;

    private final Vector<Float> _myColorCache;

    private int _myPrimitiveType;

    private boolean _myCreateVBO = true;

    public static boolean VERBOSE = false;

    public MeshCreator() {
        _myTranslators = new Vector<DrawableMeshTranslator> ();
        _myVertexCache = new Vector<Float> ();
        _myTextureCoordinatesCache = new Vector<Float> ();
        _myNormalCache = new Vector<Float> ();
        _myColorCache = new Vector<Float> ();

        _myPrimitiveType = Gestalt.MESH_QUADS;
    }


    public void createVBO(boolean theCreateVBO) {
        _myCreateVBO = theCreateVBO;
    }


    public Vector<DrawableMeshTranslator> translators() {
        return _myTranslators;
    }


    public void setPrimitveType(int thePrimitiveType) {
        _myPrimitiveType = thePrimitiveType;
    }


    public Mesh parse(final Bin theBin, final DrawableFactory theFactory) {
        /* parse objects */
        Drawable[] mySortables = theBin.getDataRef();
        for (int i = 0; i < theBin.size(); i++) {
            final Drawable myDrawable = mySortables[i];
            if (myDrawable != null && myDrawable.isActive()) {
                parseDrawables(myDrawable);
            }
        }

        /* create VBO mesh */
        final Mesh myMesh = createMesh(theFactory);

        /* clear cache */
//        System.out.println("_myVertexCache            : " + _myVertexCache.size());
//        System.out.println("_myTextureCoordinatesCache: " + _myTextureCoordinatesCache.size());
//        System.out.println("_myNormalCache            : " + _myNormalCache.size());
//        System.out.println("_myColorCache             : " + _myColorCache.size());
        _myVertexCache.clear();
        _myTextureCoordinatesCache.clear();
        _myNormalCache.clear();
        _myColorCache.clear();

        return myMesh;
    }


    private float[] toArray(Vector<Float> theVector) {
        final int mySize = theVector.size();
        if (mySize == 0) {
            return null;
        } else {
            float[] myResult = new float[mySize];
            for (int i = 0; i < theVector.size(); i++) {
                myResult[i] = theVector.get(i);
            }
            return myResult;
        }
    }


    private Mesh createMesh(DrawableFactory theFactory) {
        float[] myVertices = toArray(_myVertexCache);
        float[] myColors = toArray(_myColorCache);
        float[] myTexCoords = toArray(_myTextureCoordinatesCache);
        float[] myNormals = toArray(_myNormalCache);

        /** @todo this is a fix to prevent mesh from crashing in case the  */
        if (myVertices == null) {
            myVertices = new float[0];
        }

        return theFactory.mesh(_myCreateVBO,
                               myVertices, 3,
                               myColors, 4,
                               myTexCoords, 2,
                               myNormals,
                               _myPrimitiveType);
    }


    public void addVertex(final Vector3f theVertex) {
        _myVertexCache.add(theVertex.x);
        _myVertexCache.add(theVertex.y);
        _myVertexCache.add(theVertex.z);
    }


    public Vector<Float> vertex() {
        return _myVertexCache;
    }


    public void addColor(Color theColor) {
        _myColorCache.add(theColor.r);
        _myColorCache.add(theColor.g);
        _myColorCache.add(theColor.b);
        _myColorCache.add(theColor.a);
    }


    public Vector<Float> color() {
        return _myColorCache;
    }


    public void addTexCoord(Vector2f theTexCoords) {
        _myTextureCoordinatesCache.add(theTexCoords.x);
        _myTextureCoordinatesCache.add(theTexCoords.y);
    }


    public Vector<Float> texcoord() {
        return _myTextureCoordinatesCache;
    }


    public void addNormal(Vector3f theNormal) {
        _myNormalCache.add(theNormal.x);
        _myNormalCache.add(theNormal.y);
        _myNormalCache.add(theNormal.z);
    }


    public Vector<Float> normal() {
        return _myNormalCache;
    }


    private void parseDrawables(final Drawable theDrawable) {
        for (final DrawableMeshTranslator myTranslator : _myTranslators) {
            if (myTranslator.isClass(theDrawable)) {
                myTranslator.parse(this, theDrawable);
                return;
            }
        }

        if (VERBOSE) {
            System.out.println("### WARNING / drawable type unsupported. / " + theDrawable.getClass());
        }
    }
}
