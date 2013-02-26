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


package gestalt.candidates;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.shape.AbstractShape;
import gestalt.util.JoglUtil;

import mathematik.Vector3f;

import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;


public class JoglIndexedTriangleMesh
        extends AbstractShape {

    private final int _myPrimitive;

    private final Vector<IndexedTriangle> _myIndexList = new Vector<IndexedTriangle>();

    private final Vector<Vector3f> _myVertices = new Vector<Vector3f>();

    private final Vector<Vector3f> _myNormals = new Vector<Vector3f>();

    private final Vector<Color> _myColors = new Vector<Color>();

    private float _myMinimumDistance = 0.01f;

    public JoglIndexedTriangleMesh() {
        _myPrimitive = JoglUtil.mapGestaltPrimitiveToOpenGLPrimitive(Gestalt.MESH_TRIANGLES);
        material = new Material();
    }

    public void minimumdistance(float theMinimumDistance) {
        _myMinimumDistance = theMinimumDistance;
    }

//    public void divideLongEdges() {
//        /** @todo  */
//    }
//
//    public void getTriangles(int theVertexIndex) {
//    }
//
//    public void getTriangles(int theVertexIndexA, int theVertexIndexB) {
//    }
    public void split(float theMinArea) {
        final Vector<IndexedTriangle> myTriangles = new Vector<IndexedTriangle>();
        for (final IndexedTriangle myTriangle : indexlist()) {
            myTriangles.add(myTriangle);
        }

        /* get and remove old triangle */
        for (final IndexedTriangle myTriangle : myTriangles) {
            splitCentroid(myTriangle, theMinArea);
        }
    }

    public boolean splitCentroid(final IndexedTriangle theTriangle,
                                 final float theMinArea) {
        final float myTriangleArea = mathematik.Util.areaTriangle(vertices().get(theTriangle.a),
                                                                  vertices().get(theTriangle.b),
                                                                  vertices().get(theTriangle.c));
        System.out.println("splitCentroid: " + myTriangleArea);

        if (myTriangleArea < theMinArea) {
            return false;
        }

        removeTriangle(theTriangle);

        /* get center of mass */
        final Vector3f myCenter = new Vector3f();
        myCenter.add(vertices().get(theTriangle.a));
        myCenter.add(vertices().get(theTriangle.b));
        myCenter.add(vertices().get(theTriangle.c));
        myCenter.divide(3.0f);

        /* add vertex to mesh */
        int myNewIndex = addVertex(myCenter, normals().get(theTriangle.a));

        /* create 3 new triangles */
        addTriangle(theTriangle.a, theTriangle.b, myNewIndex);
        addTriangle(theTriangle.b, theTriangle.c, myNewIndex);
        addTriangle(theTriangle.c, theTriangle.a, myNewIndex);

        return true;
    }

    public Vector<Vector3f> vertices() {
        return _myVertices;
    }

    public Vector<Vector3f> normals() {
        return _myNormals;
    }

    public Vector<IndexedTriangle> indexlist() {
        return _myIndexList;
    }

    public void removeTriangle(final IndexedTriangle theIndexedTriangle) {
        for (Iterator<IndexedTriangle> myIterator = _myIndexList.iterator(); myIterator.hasNext();) {
            final IndexedTriangle myIndexedTriangle = myIterator.next();
            if (myIndexedTriangle.a == theIndexedTriangle.a
                    && myIndexedTriangle.b == theIndexedTriangle.b
                    && myIndexedTriangle.c == theIndexedTriangle.c) {
                myIterator.remove();
                return;
            }
        }
    }

    public void addTriangle(IndexedTriangle theTriangle) {
        _myIndexList.add(theTriangle);
    }

    public void addTriangle(int a, int b, int c) {
        final IndexedTriangle t = new IndexedTriangle();
        t.a = a;
        t.b = b;
        t.c = c;
        _myIndexList.add(t);
    }

    public void addTriangle(Vector3f a, Vector3f b, Vector3f c) {
        final Vector3f myNormal = mathematik.Util.createNormal(a, b, c);
        final IndexedTriangle myIndexedTriangle = new IndexedTriangle();
        myIndexedTriangle.a = addVertex(a, myNormal);
        myIndexedTriangle.b = addVertex(b, myNormal);
        myIndexedTriangle.c = addVertex(c, myNormal);
        _myIndexList.add(myIndexedTriangle);
    }

    public void addTriangles(final Vector<Vector3f> theVertices) {
        for (int i = 0; i < theVertices.size(); i += 3) {
            addTriangle(theVertices.get(i + 0),
                        theVertices.get(i + 1),
                        theVertices.get(i + 2));
        }
    }

    public void addTriangles(final float[] theVertices) {
        for (int i = 0; i < theVertices.length; i += 9) {
            final Vector3f a = new Vector3f(theVertices[i + 0],
                                            theVertices[i + 1],
                                            theVertices[i + 2]);
            final Vector3f b = new Vector3f(theVertices[i + 3],
                                            theVertices[i + 4],
                                            theVertices[i + 5]);
            final Vector3f c = new Vector3f(theVertices[i + 6],
                                            theVertices[i + 7],
                                            theVertices[i + 8]);
            final Vector3f myNormal = mathematik.Util.createNormal(a, b, c);
            final IndexedTriangle myIndexedTriangle = new IndexedTriangle();
            myIndexedTriangle.a = addVertex(a, myNormal, null);
            myIndexedTriangle.b = addVertex(b, myNormal, null);
            myIndexedTriangle.c = addVertex(c, myNormal, null);
            _myIndexList.add(myIndexedTriangle);
        }
    }

    public void addTriangles(final float[] theVertices,
                             final float[] theColors,
                             final float[] theTexCoords) {
        System.out.println("theTexCoords not done yet");
        for (int i = 0; i < theVertices.length; i += 9) {
            final Vector3f a = new Vector3f(theVertices[i + 0],
                                            theVertices[i + 1],
                                            theVertices[i + 2]);
            final Vector3f b = new Vector3f(theVertices[i + 3],
                                            theVertices[i + 4],
                                            theVertices[i + 5]);
            final Vector3f c = new Vector3f(theVertices[i + 6],
                                            theVertices[i + 7],
                                            theVertices[i + 8]);
            final Vector3f myNormal = mathematik.Util.createNormal(a, b, c);
            int myColorIndex = i * 4 / 3;
            final Color cA = new Color(theColors[myColorIndex + 0],
                                       theColors[myColorIndex + 1],
                                       theColors[myColorIndex + 2],
                                       theColors[myColorIndex + 3]);
            final Color cB = new Color(theColors[myColorIndex + 4],
                                       theColors[myColorIndex + 5],
                                       theColors[myColorIndex + 6],
                                       theColors[myColorIndex + 7]);
            final Color cC = new Color(theColors[myColorIndex + 8],
                                       theColors[myColorIndex + 9],
                                       theColors[myColorIndex + 10],
                                       theColors[myColorIndex + 11]);
            final IndexedTriangle myIndexedTriangle = new IndexedTriangle();
            myIndexedTriangle.a = addVertex(a, myNormal, cA);
            myIndexedTriangle.b = addVertex(b, myNormal, cB);
            myIndexedTriangle.c = addVertex(c, myNormal, cC);
            _myIndexList.add(myIndexedTriangle);
        }
    }

    public void addVertices(final float[] theVertices) {
        for (int i = 0; i < theVertices.length; i += 3) {
            final Vector3f a = new Vector3f(theVertices[i + 0],
                                            theVertices[i + 1],
                                            theVertices[i + 2]);
            addVertex(a);
        }
    }

    private int addVertex(final Vector3f theVertex) {
        return addVertex(theVertex, null, null);
    }

    public int addVertex(final Vector3f theVertex, final Vector3f theNormal) {
        return addVertex(theVertex, theNormal, null);
    }

    public int addVertex(final Vector3f theVertex,
                         final Vector3f theNormal,
                         final Color theColor) {
        int myIndex = findVertex(theVertex);
        if (myIndex == -1) {
            _myVertices.add(theVertex);
            if (theNormal != null) {
                _myNormals.add(theNormal);
            }
            if (theColor != null) {
                _myColors.add(theColor);
            }
            myIndex = _myVertices.size() - 1;
        }
        return myIndex;
    }

    public int findVertex(final Vector3f theVector) {
        int i = 0;
        for (final Vector3f v : _myVertices) {
            if (almost(v, theVector)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private final boolean almost(final Vector3f a, final Vector3f b) {
        return a.distance(b) < _myMinimumDistance;
    }

    private final boolean _almost(final Vector3f a, final Vector3f b) {
        if (Math.abs(a.x - b.x) < _myMinimumDistance && Math.abs(a.y - b.y) < _myMinimumDistance && Math.abs(a.z - b.z) < _myMinimumDistance) {
            return true;
        } else {
            return false;
        }
    }

    private final void drawVertex(final GL gl,
                                  final int i,
                                  final boolean theDrawNormal,
                                  final boolean theDrawColor) {
        if (theDrawNormal) {
            final Vector3f myNormal = _myNormals.get(i);
            gl.glNormal3f(myNormal.x, myNormal.y, myNormal.z);
        }
        if (theDrawColor) {
            final Color myColor = _myColors.get(i);
            gl.glColor4f(myColor.r, myColor.g, myColor.b, myColor.a);
        }
        final Vector3f myVertex = _myVertices.get(i);
        gl.glVertex3f(myVertex.x, myVertex.y, myVertex.z);
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* material */
        if (material != null) {
            material.begin(theRenderContext);
        }

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* draw data */
        final boolean myDrawNormal = !_myNormals.isEmpty();
        final boolean myDrawColor = !_myColors.isEmpty();
        gl.glBegin(_myPrimitive);
        for (final IndexedTriangle myIndex : _myIndexList) {
            drawVertex(gl, myIndex.a, myDrawNormal, myDrawColor);
            drawVertex(gl, myIndex.b, myDrawNormal, myDrawColor);
            drawVertex(gl, myIndex.c, myDrawNormal, myDrawColor);
        }
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();

        /* material */
        if (material != null) {
            material.end(theRenderContext);
        }
    }

    public void dispose(GLContext theRenderContext) {
    }

    public class IndexedTriangle {

        public int a;

        public int b;

        public int c;
    }
}
