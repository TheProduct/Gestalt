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

import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.atom.AtomLine;

import mathematik.Vector3f;

import javax.media.opengl.GL;

import static gestalt.Gestalt.*;


public class Line
        extends AbstractShape {

    public Vector3f[] points;

    public Color[] colors;

    public float linewidth;

    public boolean smooth;

    /** @todo move stippling to material plugins 'JoglMaterialPluginStippleLine' */
    public boolean stipple;

    public int stipplefactor;

    public short stipplepattern;

    protected int _myPrimitive;

    public Line() {
        stipple = false;
        smooth = false;
        linewidth = 1;
        stipplepattern = (short)0xFFFF;
        stipplefactor = 1;
        material.transparent = false;
        setPrimitive(LINE_PRIMITIVE_TYPE_LINE_STRIP);

        material = new Material();
    }

    /**
     * defines a stipplepattern for a lign. 'stipple' needs to be set to 'true' for the 'stipplepattern' to take effect.<br/>
     * <br/>
     * ( also see OpenGL documentation )
     *
     * @param theStippleBits String
     */
    public void setStipplePattern(String theStippleBits) {
        stipplepattern = (short)Integer.parseInt(theStippleBits, 2);
    }

    public void draw(final GLContext theRenderContext) {
        if (points != null) {
            final GL gl = theRenderContext.gl;

            /* begin material */
            material.begin(theRenderContext);

            /* smooth */
            if (smooth) {
                gl.glEnable(GL.GL_LINE_SMOOTH);
            }

            /* stipple */
            if (stipple) {
                gl.glEnable(GL.GL_LINE_STIPPLE);
                gl.glLineStipple(stipplefactor, stipplepattern);
            }

            /* draw shape */
            AtomLine.draw(gl,
                              points,
                              colors,
                              linewidth,
                              _myPrimitive);

            /* stipple */
            if (stipple) {
                gl.glDisable(GL.GL_LINE_STIPPLE);
            }

            /* smooth */
            if (smooth) {
                gl.glDisable(GL.GL_LINE_SMOOTH);
            }

            /* end material */
            material.end(theRenderContext);
        }
    }

    /**
     * there are different kinds of line primitives. defined in 'Gestalt' constants.<br/>
     * <pre>
     *    LINE_PRIMITIVE_TYPE_LINES
     *    LINE_PRIMITIVE_TYPE_LINE_LOOP
     *    LINE_PRIMITIVE_TYPE_LINE_STRIP
     * </pre>
     * 'LINES' draws a per two points defined in the 'points' array.<br/>
     * 'LINE_LOOP' draws a continous line starting with the first point in the array and ending with the last.<br/>
     * 'LINE_PRIMITIVE_TYPE_LINE_STRIP' is the same as above except that it connects the last with the first point.<br/>
     * <br/>
     * ( also see OpenGL documentation )
     *
     * @param theGestaltPrimitive int
     */
    public final void setPrimitive(int theGestaltPrimitive) {
        switch (theGestaltPrimitive) {
            case LINE_PRIMITIVE_TYPE_LINES:
                _myPrimitive = GL.GL_LINES;
                break;
            case LINE_PRIMITIVE_TYPE_LINE_LOOP:
                _myPrimitive = GL.GL_LINE_LOOP;
                break;
            case LINE_PRIMITIVE_TYPE_LINE_STRIP:
                _myPrimitive = GL.GL_LINE_STRIP;
                break;
        }
    }
    
//    /** @todo is it okey to use the transform on lines? */
//    public void _draw(final GLContext theRenderContext) {
//        if (points != null) {
//            final GL gl = (  theRenderContext).gl;
//
//            /* begin material */
//            material.begin(theRenderContext);
//
//            /* geometrie */
//            gl.glPushMatrix();
//            if (_myTransformMode == SHAPE_TRANSFORM_MATRIX ||
//                _myTransformMode == SHAPE_TRANSFORM_MATRIX_POSITION_ROTATION) {
//                /** @todo we need to remove the z component in 2D mode */
//                gl.glMultMatrixf(transform.toArray());
//            }
//
//            if (_myTransformMode == SHAPE_TRANSFORM_POSITION_ROTATION ||
//                _myTransformMode == SHAPE_TRANSFORM_MATRIX_POSITION_ROTATION) {
//
//                /** @todo we need to remove the z component in 2D mode */
//                gl.glTranslatef(position.x, position.y, position.z);
//
//                /** @todo replace with faster 'to degree' calculation */
//                if (rotation.x != 0.0f) {
//                    gl.glRotatef( (float) Math.toDegrees(rotation.x), 1, 0, 0);
//                }
//                if (rotation.y != 0.0f) {
//                    gl.glRotatef( (float) Math.toDegrees(rotation.y), 0, 1, 0);
//                }
//                if (rotation.z != 0.0f) {
//                    gl.glRotatef( (float) Math.toDegrees(rotation.z), 0, 0, 1);
//                }
//            }
//
//            /* finally scale the shape */
//            gl.glScalef(scale.x, scale.y, 1);
//
//            /* draw shape */
//            AtomLine.draw(gl,
//                              points,
//                              colors,
//                              linewidth,
//                              closed);
//            gl.glPopMatrix();
//
//            /* end material */
//            material.end();
//        }
//    }
}
