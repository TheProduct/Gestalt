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
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.util.JoglUtil;

import mathematik.Vector3f;

import java.util.Vector;

import javax.media.opengl.GL;


public class PointCloud
        extends AbstractShape {

    private static final long serialVersionUID = -6066988682687912389L;

    private final Vector<Vector3f> _myVerticesVector;

    private Vector<Color> _myColors;

    private final int _myPrimitive;

    private final int _myDrawLength;

    public PointCloud() {
        material = new Material();
        _myVerticesVector = new Vector<Vector3f>();
        _myPrimitive = GL.GL_POINTS;
        _myDrawLength = -1;
        _myColors = new Vector<Color>();
    }

    public void add(final Vector3f thePosition,
                    final Color theColor) {
        _myVerticesVector.add(thePosition);
        if (_myColors != null) {
            _myColors.add(theColor);
        }
    }

    public void remove(final int i) {
        _myVerticesVector.remove(i);
        if (_myColors != null) {
            _myColors.remove(i);
        }
    }

    public Vector<Vector3f> vertices() {
        return _myVerticesVector;
    }

    public Vector<Color> colors() {
        return _myColors;
    }

    public void setColorsRef(Vector<Color> theColorsRef) {
        _myColors = theColorsRef;
    }


    /* -- */
    public void draw(final GLContext theRenderContext) {

        if (vertices().isEmpty()) {
            return;
        }

        final GL gl = theRenderContext.gl;

        /* material */
        material.begin(theRenderContext);

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        final int myDrawLength = _myDrawLength == -1 ? _myVerticesVector.size() : _myDrawLength;

        gl.glBegin(_myPrimitive);
        final boolean USE_COLOR = _myColors != null && _myColors.size() == _myVerticesVector.size();
        for (int i = 0; i < myDrawLength; i++) {
            /* color4f */
            if (USE_COLOR) {
                gl.glColor4f(_myColors.get(i).r,
                             _myColors.get(i).g,
                             _myColors.get(i).b,
                             _myColors.get(i).a);
            }
            /* vertex */
            gl.glVertex3f(_myVerticesVector.get(i).x,
                          _myVerticesVector.get(i).y,
                          _myVerticesVector.get(i).z);
        }
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();

        /* material */
        material.end(theRenderContext);
    }
}
