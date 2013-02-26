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


package gestalt.demo.advanced;


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.util.CameraMover;

import data.Resource;


/**
 * 'UsingBillboards' demonstrates how to use the camera transformation matrix
 * to rotate a shape in a way that it always faces the camera.
 */


public class UsingBillboards
    extends AnimatorRenderer {

    private Plane[] _myImagePlane;

    private float _myDuration;

    public void setup() {

        /* setup view */
        displaycapabilities().backgroundcolor.set(0.2f);
        camera().position().set(0, -400, 1000);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* create planes that will act as billboards */
        TexturePlugin myImageTexture = drawablefactory().texture();
        myImageTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/glow.png")));
        _myImagePlane = new Plane[360];
        for (int i = 0; i < _myImagePlane.length; i++) {
            _myImagePlane[i] = drawablefactory().plane();
            _myImagePlane[i].material().addPlugin(myImageTexture);
            _myImagePlane[i].material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
            _myImagePlane[i].material().color4f().set(1, 0.1f);
            _myImagePlane[i].material().depthtest = false;
            _myImagePlane[i].setPlaneSizeToTextureSize();
            final float myPercent = (float) (i + 1) / (float) _myImagePlane.length;
            _myImagePlane[i].scale().scale(0.5f + 1.5f * myPercent);
            bin(BIN_3D).add(_myImagePlane[i]);
        }
    }


    public void loop(float theDeltaTime) {

        /* use keys to move camera */
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
        camera().updateRotationMatrix();

        /**
         * to make a plane face the camera the inverted camera rotation matrix
         * needs to be set as the rotation matrix of the plane.
         */

        _myDuration += theDeltaTime;
        for (int i = 0; i < _myImagePlane.length; i++) {
            /* press the mouse to rotate planes to face the camera */
            if (event().mouseDown) {
                _myImagePlane[i].transform().rotation.set(camera().getInversRotationMatrix());
            }
            /* set plane position in 3D space */
            final float myPercent = (float) (i + 1) / (float) _myImagePlane.length;
            final float myRadius = 150 * myPercent + 50;
            final float myX = myRadius *
                              (float) Math.sin(_myDuration * 4f * myPercent);
            final float myY = myRadius *
                              (float) Math.cos(_myDuration * 2f * myPercent);
            final float myZ = 2f * myRadius *
                              (float) Math.cos(Math.toRadians(i)) *
                              (float) Math.sin(_myDuration);
            _myImagePlane[i].transform().translation.set(myX, myY, myZ);
        }
    }


    public static void main(String[] args) {
        new UsingBillboards().init();
    }
}
