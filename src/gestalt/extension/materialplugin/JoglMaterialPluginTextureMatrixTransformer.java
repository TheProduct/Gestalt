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


package gestalt.extension.materialplugin;


import javax.media.opengl.GL;

import static gestalt.Gestalt.*;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class JoglMaterialPluginTextureMatrixTransformer
    implements MaterialPlugin {

    public TransformMatrix4f transform;

    public Vector3f scale;

    public Vector3f rotation;

    private int _myTransformMode;

    private final int[] _myCurrentMatrixMode;

    public JoglMaterialPluginTextureMatrixTransformer() {
        transform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        scale = new Vector3f(1, 1, 1);
        rotation = new Vector3f(0, 0, 0);
        _myTransformMode = SHAPE_TRANSFORM_MATRIX_AND_ROTATION;
        _myCurrentMatrixMode = new int[1];
    }


    public Vector3f position() {
        return transform.translation;
    }


    public void setPositionRef(Vector3f thePosition) {
        transform.translation = thePosition;
    }


    public void setTransformMode(int theTransformMode) {
        _myTransformMode = theTransformMode;
    }


    public int getTransformMode() {
        return _myTransformMode;
    }


    public void begin(final GLContext theRenderContext,
                      final Material theParent) {

        final GL gl = (  theRenderContext).gl;

        /* save current matrix mode */
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_MATRIX_MODE, _myCurrentMatrixMode, 0);

        /* select the texture matrix stack */
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPushMatrix();

        /* transform matrix */
        if (_myTransformMode == SHAPE_TRANSFORM_MATRIX ||
            _myTransformMode == SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            /** @todo JSR-231 -- added 0 */
            gl.glMultMatrixf(transform.toArray(), 0);
        }

        if (_myTransformMode == SHAPE_TRANSFORM_POSITION_AND_ROTATION) {
            gl.glTranslatef(transform.translation.x,
                            transform.translation.y,
                            transform.translation.z);
        }

        if (_myTransformMode == SHAPE_TRANSFORM_POSITION_AND_ROTATION ||
            _myTransformMode == SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            if (rotation.x != 0.0f) {
                gl.glRotatef( (float) Math.toDegrees(rotation.x), 1, 0, 0);
            }
            if (rotation.y != 0.0f) {
                gl.glRotatef( (float) Math.toDegrees(rotation.y), 0, 1, 0);
            }
            if (rotation.z != 0.0f) {
                gl.glRotatef( (float) Math.toDegrees(rotation.z), 0, 0, 1);
            }
        }

        /* finally scale the shape */
        gl.glScalef(scale.x, scale.y, scale.z);
    }


    public void end(final GLContext theRenderContext,
                    final Material theParent) {
        final GL gl = (  theRenderContext).gl;
        gl.glPopMatrix();
        gl.glMatrixMode(_myCurrentMatrixMode[0]);
    }
}
