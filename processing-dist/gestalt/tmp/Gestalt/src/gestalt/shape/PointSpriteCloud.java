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
import gestalt.material.PointSprite;
import gestalt.material.texture.Bitmap;
import gestalt.util.JoglUtil;

import mathematik.Vector3f;

import java.util.Vector;

import javax.media.opengl.GL;


public class PointSpriteCloud
        extends AbstractShape {

    private static final long serialVersionUID = -6066988682687916289L;

    public float ATTENUATION_CONSTANT = 10;

    public float ATTENUATION_LINEAR = 0.002f;

    public float ATTENUATION_QUAD = 0.000001f;

    public float POINT_SIZE = 200;

    public float MIN_POINT_SIZE = 4;

    public float MAX_POINT_SIZE = 1024;

    private final Vector<Vector3f> _myVerticesVector;

    private Vector<Color> _myColors;

    private final PointSprite _myPointSprites;

    private final int _myPrimitive;

    private final int _myDrawLength;

    public PointSpriteCloud() {
        material = new Material();
        _myVerticesVector = new Vector<Vector3f>();
        _myPrimitive = GL.GL_POINTS;
        _myDrawLength = -1;
        _myColors = new Vector<Color>();

        _myPointSprites = new PointSprite();
        material().addPlugin(_myPointSprites);
        updateProperties();
    }

    public PointSprite getPointSpriteMaterial() {
        return _myPointSprites;
    }

    public void loadBitmap(final Bitmap theBitmap) {
        _myPointSprites.load(theBitmap);
    }

    public final void updateProperties() {
        // http://wiki.delphigl.com/index.php/glPointParameter
        _myPointSprites.quadric = new float[] {
            ATTENUATION_CONSTANT,
            ATTENUATION_LINEAR,
            ATTENUATION_QUAD};
        _myPointSprites.pointsize = POINT_SIZE;
        _myPointSprites.minpointsize = MIN_POINT_SIZE;
        _myPointSprites.maxpointsize = MAX_POINT_SIZE;
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
        updateProperties();

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
