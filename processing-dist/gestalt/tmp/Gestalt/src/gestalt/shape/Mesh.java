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

import gestalt.material.Material;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Disposable;
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;


public class Mesh
        extends AbstractShape
        implements Disposable {

    protected int _myDrawStart;

    protected int _myDrawLength;

    protected int _myPrimitive;

    protected float[] _myVertices;

    protected float[] _myColors;

    protected float[] _myNormals;

    protected float[] _myTexCoords;

    protected int _myNumberOfColorComponents;

    protected int _myNumberOfVertexComponents;

    protected int _myNumberOfTexCoordComponents;

    protected final int NUMBER_OF_NORMAL_COMPONENTS = 3;

    protected int _myNumberOfAtoms;

    public Mesh(float[] theVertices,
                int theVertexComponents,
                float[] theColors,
                int theColorComponents,
                float[] theTexCoords,
                int theTexCoordComponents,
                float[] theNormals,
                int thePrimitive) {

        _myVertices = theVertices;
        _myColors = theColors;
        _myNormals = theNormals;
        _myTexCoords = theTexCoords;

        _myNumberOfVertexComponents = theVertexComponents;
        _myNumberOfColorComponents = theColorComponents;
        _myNumberOfTexCoordComponents = theTexCoordComponents;

        _myNumberOfAtoms = _myVertices.length / _myNumberOfVertexComponents;
        _myDrawStart = 0;
        _myDrawLength = _myNumberOfAtoms;

        setPrimitive(thePrimitive);

        /* setup material */
        material = new Material();

        /* check data integritiy */
        checkDataIntegrity();
    }

    public float[] vertices() {
        return _myVertices;
    }

    public float[] colors() {
        return _myColors;
    }

    public float[] normals() {
        return _myNormals;
    }

    public float[] texcoords() {
        return _myTexCoords;
    }

    /**
     * @deprecated
     */
    public void setStart(int theStart) {
        _myDrawStart = theStart;
    }

    /**
     * @deprecated
     */
    public void setLength(int theLength) {
        _myDrawLength = theLength;
    }

    /**
     * @deprecated
     */
    public int getStart() {
        return _myDrawStart;
    }

    /**
     * @deprecated
     */
    public int getLength() {
        return _myDrawLength;
    }

    public void drawstart(int theStart) {
        _myDrawStart = theStart;
    }

    public void drawlength(int theLength) {
        _myDrawLength = theLength;
    }

    public int drawstart() {
        return _myDrawStart;
    }

    public int drawlength() {
        return _myDrawLength;
    }

    public int atomcount() {
        return _myNumberOfAtoms;
    }

    public int getNumberOfColorComponents() {
        return _myNumberOfColorComponents;
    }

    public int getNumberOfVertexComponents() {
        return _myNumberOfVertexComponents;
    }

    public int getNumberOfTexCoordComponents() {
        return _myNumberOfTexCoordComponents;
    }

    public void setPrimitive(int theGestaltPrimitive) {
        _myPrimitive = JoglUtil.mapGestaltPrimitiveToOpenGLPrimitive(theGestaltPrimitive);
        checkDataIntegrity();
    }

    public int getPrimitive() {
        return JoglUtil.mapOpenGLPrimitiveToGestaltPrimitive(_myPrimitive);
    }

    public void updateData() {
        /* updating the data is not necessary in Mesh */
    }

    public final boolean checkDataIntegrity() {
        if (_myVertices == null) {
            System.err.print("### WARNING @ Mesh / problems with data reference");
            System.err.println("/ vertex data is 'null'");
            return false;
        }
        if (_myNumberOfAtoms * _myNumberOfVertexComponents != _myVertices.length) {
            System.err.print("### WARNING @ Mesh / problems with data integrity ");
            System.err.println("/ vertex");
            return false;
        }
        if ((_myColors != null)
                && (_myColors.length != 0)
                && (_myColors.length / _myNumberOfColorComponents != _myNumberOfAtoms)) {
            System.err.print("### WARNING @ Mesh / problems with data integrity ");
            System.err.println("/ color");
            return false;
        }
        if ((_myTexCoords != null)
                && (_myTexCoords.length != 0)
                && (_myTexCoords.length / _myNumberOfTexCoordComponents != _myNumberOfAtoms)) {
            System.err.print("### WARNING @ Mesh / problems with data integrity ");
            System.err.println("/ texture coordinates");
            return false;
        }

        if ((_myNormals != null)
                && (_myNormals.length != 0)
                && (_myNormals.length / NUMBER_OF_NORMAL_COMPONENTS != _myNumberOfAtoms)) {
            System.err.print("### WARNING @ Mesh / problems with data integrity ");
            System.err.println("/ normals");
            return false;
        }

        /** @todo also test primitive type VS number of components */
        return true;
    }


    /* -- */
    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* material */
        if (material != null) {
            material.begin(theRenderContext);
        }

        /** @todo handling textures is missing here */

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* model */
        int myNormalIndex = _myDrawStart * NUMBER_OF_NORMAL_COMPONENTS;
        int myTexCoordIndex = _myDrawStart * _myNumberOfTexCoordComponents;
        int myColorIndex = _myDrawStart * _myNumberOfColorComponents;
        int myVertexIndex = _myDrawStart * _myNumberOfVertexComponents;
        gl.glBegin(_myPrimitive);
        for (int i = 0; i < _myDrawLength; i++) {
            /* normals */
            if (_myNormals != null
                    && _myNormals.length != 0) {
                gl.glNormal3f(_myNormals[myNormalIndex], _myNormals[myNormalIndex + 1], _myNormals[myNormalIndex + 2]);
                myNormalIndex += NUMBER_OF_NORMAL_COMPONENTS;
            }
            /* texcoords */
            if (_myTexCoords != null
                    && _myTexCoords.length != 0
                    && material != null
                    && !material.disableTextureCoordinates) {
                if (_myNumberOfTexCoordComponents == 2) {
                    gl.glTexCoord2f(_myTexCoords[myTexCoordIndex], _myTexCoords[myTexCoordIndex + 1]);
                } else if (_myNumberOfTexCoordComponents == 1) {
                    gl.glTexCoord1f(_myTexCoords[myTexCoordIndex]);
                } else if (_myNumberOfTexCoordComponents == 3) {
                    gl.glTexCoord3f(_myTexCoords[myTexCoordIndex],
                                    _myTexCoords[myTexCoordIndex + 1],
                                    _myTexCoords[myTexCoordIndex + 2]);
                }
                myTexCoordIndex += _myNumberOfTexCoordComponents;
            }
            /* color4f */
            if (_myColors != null
                    && _myColors.length != 0) {
                if (_myNumberOfColorComponents == 3) {
                    gl.glColor3f(_myColors[myColorIndex], _myColors[myColorIndex + 1], _myColors[myColorIndex + 2]);
                } else if (_myNumberOfColorComponents == 4) {
                    gl.glColor4f(_myColors[myColorIndex],
                                 _myColors[myColorIndex + 1],
                                 _myColors[myColorIndex + 2],
                                 _myColors[myColorIndex + 3]);
                }
                myColorIndex += _myNumberOfColorComponents;
            }
            /* vertex */
            if (_myNumberOfVertexComponents == 3) {
                gl.glVertex3f(_myVertices[myVertexIndex],
                              _myVertices[myVertexIndex + 1],
                              _myVertices[myVertexIndex + 2]);
            } else if (_myNumberOfVertexComponents == 2) {
                gl.glVertex2f(_myVertices[myVertexIndex], _myVertices[myVertexIndex + 1]);
            } else if (_myNumberOfVertexComponents == 4) {
                gl.glVertex4f(_myVertices[myVertexIndex],
                              _myVertices[myVertexIndex + 1],
                              _myVertices[myVertexIndex + 2],
                              _myVertices[myVertexIndex + 3]);
            }
            myVertexIndex += _myNumberOfVertexComponents;
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
}
