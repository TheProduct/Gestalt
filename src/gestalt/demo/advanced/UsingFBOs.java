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


import gestalt.candidates.JoglTextureReader;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;
import gestalt.util.CameraMover;
import gestalt.util.ImageUtil;


/**
 * this demo shows how to use framebuffer objects (FBO).
 * FBOs are the best way to create an offscreen rendercontext.
 *
 * this demo also uses the 'JoglTextureReader' which reads a
 * texture from GPU memory back into the CPU memory. this can
 * be very, very slow and is turned of by default.
 */
public class UsingFBOs
        extends AnimatorRenderer {

    private Cuboid _myCube;

    private JoglFrameBufferObject _myFBO;

    private Plane _myPlane;

    private JoglTextureReader _myReader;

    public void setup() {
        /* g1 */
        framerate(60);
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create cuboid */
        _myCube = drawablefactory().cuboid();
        _myCube.material().color4f().set(0.75f);
        _myCube.scale().set(300, 100, 100);
        bin(BIN_3D).add(_myCube);

        /* create FBO and add cuboid */
        _myFBO = JoglFrameBufferObject.createDefault(512, 512);
        _myFBO.backgroundcolor().set(1, 0, 0, 0.5f);
        _myFBO.add(_myCube);
        bin(BIN_ARBITRARY).add(_myFBO);

        /* create plane to show FBO */
        _myPlane = drawablefactory().plane();
        _myPlane.material().addTexture(_myFBO);
        _myPlane.scale().set(100, 100);
        _myPlane.position().set(10 + displaycapabilities().width / -2,
                                -10 + displaycapabilities().height / 2);
        _myPlane.origin(SHAPE_ORIGIN_TOP_LEFT);
        bin(BIN_3D).add(_myPlane);

        /* get texture data back from opengl memory */
        _myReader = new JoglTextureReader(_myFBO);
        _myReader.setActive(false);
        bin(BIN_ARBITRARY).add(_myReader);
    }

    public void loop(float theDeltaTime) {
        /* move camera */
        CameraMover.handleKeyEvent(_myFBO.camera(), event(), theDeltaTime);

        /* move cuboid */
        _myCube.rotation().y -= 0.001f;
        _myCube.rotation().z -= 0.0013f;

        /* en/disable texture reader */
        if (event().mouseClicked && event().mouseButton == MOUSEBUTTON_RIGHT) {
            _myReader.setActive(!_myReader.isActive());
        }

        /* display last read texture */
        if (_myReader.isActive()) {
            if (event().keyPressed && event().keyCode == KEYCODE_SPACE) {
                ImageUtil.displayBitmap(_myReader.bitmap(), "", false);
            }
        }
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        new UsingFBOs().init(myDisplayCapabilities);
    }
}
