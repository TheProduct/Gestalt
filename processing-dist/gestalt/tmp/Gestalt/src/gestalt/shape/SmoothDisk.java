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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.render.Disposable;
import gestalt.shape.AbstractShape;
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;


public class SmoothDisk
        extends AbstractShape
        implements Disposable {

    protected float edgewidth = 10;

    protected Color mEdgeColor = new Color();

    protected int mResolution = 36;

    protected float _myRadius;

    private boolean mCompileIntoList;

    public SmoothDisk() {
        material = new Material();
        mEdgeColor.set(material.color4f());
        mEdgeColor.a = 0.0f;
        mCompileIntoList = false;
    }

    public void compile_into_displaylist() {
        mCompileIntoList = true;
    }

    public void edge_width(final float theValue) {
        edgewidth = theValue;
    }

    public void resolution(int theValue) {
        mResolution = theValue;
    }

    public Color edge_color() {
        return mEdgeColor;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = (theRenderContext).gl;

        /* begin material */
        material.begin(theRenderContext);

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* draw shape */
        _myRadius = scale().length();

        /* start drawing */
        gl.glPushMatrix();
        JoglUtil.applyOrigin(gl, Gestalt.SHAPE_ORIGIN_CENTERED);
        if (mCompileIntoList) {
            createDisplayList(gl);
        } else {
            drawCircle(gl);
        }
        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);
    }


    /* displaylist */
    private boolean _myIsCompiled = false;

    private int _myDisplayList;

    private void createDisplayList(GL gl) {
        if (!_myIsCompiled) {
            _myIsCompiled = true;
            _myDisplayList = gl.glGenLists(1);
            gl.glNewList(_myDisplayList, GL.GL_COMPILE);
            drawCircle(gl);
            gl.glEndList();
        }

        if (_myIsCompiled) {
            /* call display list */
            gl.glCallList(_myDisplayList);
        }
    }

    public void dispose(GLContext theRenderContext) {
        if (_myIsCompiled) {
            final GL gl = (theRenderContext).gl;
            gl.glDeleteLists(_myDisplayList, 1);
        }
    }

    protected void drawCircle(GL gl) {
//        final float myEdgeWidthRatio = (linewidth + 2 * edgewidth) / _myRadius;
        final float myEdgeWidthRatio = (2 * edgewidth) / _myRadius;
        float[] myCircleX = new float[mResolution];
        float[] myCircleY = new float[mResolution];

        gl.glNormal3f(0, 0, 1);

        for (int i = 0; i < myCircleX.length; i++) {
            final float mRadiant = Gestalt.TWO_PI * (float)i / (float)mResolution;
            myCircleX[i] = (float)Math.sin(mRadiant) / 2.0f;
            myCircleY[i] = (float)Math.cos(mRadiant) / 2.0f;
        }

        /* outter */
        gl.glBegin(GL.GL_QUAD_STRIP);
        for (int i = 0; i < myCircleX.length + 1; i++) {
            /* draw circle */
            final int myIndex = i % myCircleX.length;
            final float myCenterX = myCircleX[myIndex] + 0.5f;
            final float myCenterY = myCircleY[myIndex] + 0.5f;
//            final float myOuterX = myCircleX[myIndex] * (myEdgeWidthRatio) + 0.5f;
//            final float myOuterY = myCircleY[myIndex] * (myEdgeWidthRatio) + 0.5f;
            final float myOuterX = myCircleX[myIndex] * (1 + myEdgeWidthRatio) + 0.5f;
            final float myOuterY = myCircleY[myIndex] * (1 + myEdgeWidthRatio) + 0.5f;
            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(myCenterX, myCenterY);
            gl.glVertex2f(myCenterX, myCenterY);
            gl.glColor4f(mEdgeColor.r,
                         mEdgeColor.g,
                         mEdgeColor.b,
                         mEdgeColor.a);
            gl.glTexCoord2f(myOuterX, myOuterY);
            gl.glVertex2f(myOuterX, myOuterY);
        }
        gl.glEnd();

        /* center */
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glColor4f(material.color4f().r,
                     material.color4f().g,
                     material.color4f().b,
                     material.color4f().a);
        gl.glTexCoord2f(0.5f, 0.5f);
        gl.glVertex2f(0.5f, 0.5f);
        for (int i = 0; i < myCircleX.length; i++) {
            /* draw circle */
            final int myNextIndex = (i + 1) % myCircleX.length;
            final float mNextPointX = myCircleX[myNextIndex] + 0.5f;
            final float mNextPointY = myCircleY[myNextIndex] + 0.5f;
            final float myOuterX = myCircleX[i] + 0.5f;
            final float myOuterY = myCircleY[i] + 0.5f;
            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(mNextPointX, mNextPointY);
            gl.glVertex2f(mNextPointX, mNextPointY);

            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(myOuterX, myOuterY);
            gl.glVertex2f(myOuterX, myOuterY);
        }
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();
    }
}
