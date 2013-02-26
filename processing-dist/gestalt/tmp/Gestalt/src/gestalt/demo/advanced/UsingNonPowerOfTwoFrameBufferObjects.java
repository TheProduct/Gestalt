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


import gestalt.G;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.render.SketchRenderer;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;
import gestalt.util.CameraMover;


public class UsingNonPowerOfTwoFrameBufferObjects
        extends SketchRenderer {

    private JoglFrameBufferObject _myFBO;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create FBO */
        _myFBO = JoglFrameBufferObject.createRectangular(320, 240);
        _myFBO.backgroundcolor().set(1);
        _myFBO.camera().setMode(CAMERA_MODE_LOOK_AT);
        bin(BIN_FRAME_SETUP).add(_myFBO);

        /* add some candy */
        for (int i = 0; i < 100; i++) {
            final Cuboid myCube = G.cuboid(_myFBO.bin());
            myCube.scale(20, 20, 20);
            myCube.position(random(-100, 100), random(-100, 100), random(-100, 100));
            myCube.material().color4f().set(random(), random(), random());
        }

        /* create a display */
        {
            final Plane myPlane = G.plane();
            myPlane.material().addTexture(_myFBO);
            myPlane.setPlaneSizeToTextureSize();
            myPlane.rotation().set(0.1f, 0.33f, 0.05f);
        }
        {
            final Plane myPlane = G.plane();
            myPlane.material().addTexture(_myFBO);
            myPlane.setPlaneSizeToTextureSize();
            myPlane.rotation().set(-0.75f, -0.53f, 0.15f);
        }
        {
            final Plane myPlane = G.plane();
            myPlane.material().addTexture(_myFBO);
            myPlane.setPlaneSizeToTextureSize();
            myPlane.rotation().set(-0.15f, 0.3f, -0.67f);
        }
    }

    public void loop(final float theDeltaTime) {
        CameraMover.handleKeyEvent(_myFBO.camera(), event(), theDeltaTime);
    }

    public static void main(String[] args) {
        G.init(UsingNonPowerOfTwoFrameBufferObjects.class);
    }
}
