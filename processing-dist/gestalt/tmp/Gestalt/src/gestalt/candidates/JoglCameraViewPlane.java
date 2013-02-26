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
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.util.JoglUtil;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import javax.media.opengl.GL;


public class JoglCameraViewPlane
        extends RenderBin {

    private final Vector3f _myScale;

    private final Camera _myCamera;

    private final TransformMatrix4f _myTransformMatrix;

    public JoglCameraViewPlane(Camera theCamera) {
        _myCamera = theCamera;
        _myScale = new Vector3f(1, 1, 1);
        _myTransformMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
    }

    public void toLocal(final Vector3f thePosition) {
        /* translation */
        thePosition.sub(_myTransformMatrix.translation);
        /* rotation */
        _myCamera.getRotationMatrix().transform(thePosition);
        /* scale */
        thePosition.divide(_myScale);
    }

    public void toGlobal(final Vector3f thePosition) {
        /* scale */
        thePosition.scale(_myScale);
        /* rotation */
        _myCamera.getInversRotationMatrix().transform(thePosition);
        /* translation */
        thePosition.add(_myTransformMatrix.translation);
    }

    public Camera camera() {
        return _myCamera;
    }

    public void draw(GLContext theRenderContext) {
        /* update rotation and position */
        _myTransformMatrix.translation.set(_myCamera.position());
        _myTransformMatrix.translation.add(mathematik.Util.scale(_myCamera.getForward(),
                                                                 -_myCamera.getDistanceToZeroPlane()));
        _myTransformMatrix.rotation.set(_myCamera.getInversRotationMatrix());

        /* draw shapes */
        final GL gl = (theRenderContext).gl;

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                Gestalt.SHAPE_TRANSFORM_MATRIX,
                                _myTransformMatrix,
                                null,
                                _myScale);
        super.draw(theRenderContext);

        /* draw shape */
        gl.glPopMatrix();
    }
}
