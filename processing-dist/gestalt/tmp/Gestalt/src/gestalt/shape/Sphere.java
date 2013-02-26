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
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;


public class Sphere
        extends AbstractShape {

    protected boolean _myIsInitlized;

    protected final float _myRadius;

    protected int _mySegments;

    private GLUquadric _myQuadratic;

    public Sphere() {
        _myIsInitlized = false;
        _myRadius = 1;
        _mySegments = 20;

        material = new Material();
    }

    public void setSegments(int theSegments) {
        _mySegments = theSegments;
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        /* create quadric */
        if (!_myIsInitlized) {
            _myIsInitlized = true;
            _myQuadratic = glu.gluNewQuadric();
            glu.gluQuadricNormals(_myQuadratic, GLU.GLU_SMOOTH);
            glu.gluQuadricTexture(_myQuadratic, true);
        }

        /** @todo handling textures is missing here */

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
        glu.gluSphere(_myQuadratic, _myRadius, _mySegments, _mySegments);

        /* finish drawing */
        gl.glPopMatrix();

        /* end materal */
        material.end(theRenderContext);
    }
}
