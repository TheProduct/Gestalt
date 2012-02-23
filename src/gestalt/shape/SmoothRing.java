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


import javax.media.opengl.GL;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.render.Disposable;
import gestalt.shape.AbstractShape;
import gestalt.material.Color;
import gestalt.util.JoglUtil;


public class SmoothRing
    extends SmoothDisk {

    private float linewidth = 10;

    public void line_width(final float theValue) {
        linewidth = theValue;
    }


    protected void drawCircle(GL gl) {
        final float myEdgeWidthRatio = (linewidth + 2 * edgewidth) / _myRadius;
        final float myLineWidthRatio = linewidth / _myRadius;
        float[] myCircleX = new float[mResolution];
        float[] myCircleY = new float[mResolution];

        gl.glNormal3f(0, 0, 1);

        for (int i = 0; i < myCircleX.length; i++) {
            final float mRadiant = Gestalt.TWO_PI * (float) i / (float) mResolution;
            myCircleX[i] = (float) Math.sin(mRadiant) / 2.0f;
            myCircleY[i] = (float) Math.cos(mRadiant) / 2.0f;
        }

        /* inner */
        gl.glBegin(GL.GL_QUAD_STRIP);
        for (int i = 0; i < myCircleX.length + 1; i++) {
            /* draw circle */
            final int myIndex = i % myCircleX.length;
            final float myInnerX = myCircleX[myIndex] * (1 - myEdgeWidthRatio) + 0.5f;
            final float myInnerY = myCircleY[myIndex] * (1 - myEdgeWidthRatio) + 0.5f;
            final float myCenterX = myCircleX[myIndex] * (1 - myLineWidthRatio) + 0.5f;
            final float myCenterY = myCircleY[myIndex] * (1 - myLineWidthRatio) + 0.5f;
            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(myCenterX, myCenterY);
            gl.glVertex3f(myCenterX, myCenterY, 0);
            gl.glColor4f(mEdgeColor.r,
                         mEdgeColor.g,
                         mEdgeColor.b,
                         mEdgeColor.a);
            gl.glTexCoord2f(myInnerX, myInnerY);
            gl.glVertex3f(myInnerX, myInnerY, 0);
        }
        gl.glEnd();

        /* outter */
        gl.glBegin(GL.GL_QUAD_STRIP);
        for (int i = 0; i < myCircleX.length + 1; i++) {
            /* draw circle */
            final int myIndex = i % myCircleX.length;
            final float myCenterX = myCircleX[myIndex] * (1 + myLineWidthRatio) + 0.5f;
            final float myCenterY = myCircleY[myIndex] * (1 + myLineWidthRatio) + 0.5f;
            final float myOuterX = myCircleX[myIndex] * (1 + myEdgeWidthRatio) + 0.5f;
            final float myOuterY = myCircleY[myIndex] * (1 + myEdgeWidthRatio) + 0.5f;
            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(myCenterX, myCenterY);
            gl.glVertex3f(myCenterX, myCenterY, 0);
            gl.glColor4f(mEdgeColor.r,
                         mEdgeColor.g,
                         mEdgeColor.b,
                         mEdgeColor.a);
            gl.glTexCoord2f(myOuterX, myOuterY);
            gl.glVertex3f(myOuterX, myOuterY, 0);
        }
        gl.glEnd();

        /* center */
        gl.glBegin(GL.GL_QUAD_STRIP);
        for (int i = 0; i < myCircleX.length + 1; i++) {
            /* draw circle */
            final int myIndex = i % myCircleX.length;
            final float myInnerX = myCircleX[myIndex] * (1 - myLineWidthRatio) + 0.5f;
            final float myInnerY = myCircleY[myIndex] * (1 - myLineWidthRatio) + 0.5f;
            final float myOuterX = myCircleX[myIndex] * (1 + myLineWidthRatio) + 0.5f;
            final float myOuterY = myCircleY[myIndex] * (1 + myLineWidthRatio) + 0.5f;
            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(myInnerX, myInnerY);
            gl.glVertex3f(myInnerX, myInnerY, 0);
            gl.glColor4f(material.color4f().r,
                         material.color4f().g,
                         material.color4f().b,
                         material.color4f().a);
            gl.glTexCoord2f(myOuterX, myOuterY);
            gl.glVertex3f(myOuterX, myOuterY, 0);
        }
        gl.glEnd();

        /* finish drawing */
        gl.glPopMatrix();
    }

}
