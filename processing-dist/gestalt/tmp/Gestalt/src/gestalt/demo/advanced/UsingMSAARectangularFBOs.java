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


public class UsingMSAARectangularFBOs
        extends SketchRenderer {

    private Cuboid[] _myCube;

    private JoglFrameBufferObject _myFBO;

    public void setup() {
        /* g1 */
        setupDefaults();
        framerate(UNDEFINED);
        backgroundcolor().set(1, 0, 0);

        /* create FBO */
        _myFBO = JoglFrameBufferObject.createRectangular(width, height, 1);
        _myFBO.backgroundcolor().set(0.2f);
        bin(BIN_ARBITRARY).add(_myFBO);

        /* create cuboid */
        _myCube = new Cuboid[25];
        for (int i = 0; i < _myCube.length; i++) {
            _myCube[i] = drawablefactory().cuboid();
            _myCube[i].scale().set(100 + i * 100, i * 0.5f + 1, i * 0.5f + 1);
            _myFBO.add(_myCube[i]);
        }

        /* create plane to show FBO */
        final Plane _myPlane = drawablefactory().plane();
        _myPlane.material().addPlugin(_myFBO);
        _myPlane.setPlaneSizeToTextureSize();
        bin(BIN_3D).add(_myPlane);
    }

    public void loop(float theDeltaTime) {
        for (int i = 0; i < _myCube.length; i++) {
            final Cuboid myCube = _myCube[i];
            myCube.rotation().y -= 0.0004f * i + 0.0002f;
            myCube.rotation().z -= 0.00053f * i + 0.00023f;
        }

        addStatistic("FPS", 1.0f / theDeltaTime);
    }

    public static void main(String[] args) {
        G.init(UsingMSAARectangularFBOs.class);
    }
}
