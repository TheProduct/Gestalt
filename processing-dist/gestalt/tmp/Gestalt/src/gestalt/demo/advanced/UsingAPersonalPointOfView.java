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

import gestalt.candidates.JoglPersonalPointView;
import gestalt.candidates.JoglTextureReader;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.TwoSidedBin;
import gestalt.render.controller.Viewport;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;
import gestalt.util.CameraMover;
import gestalt.util.ImageUtil;

import mathematik.Random;


/**
 * this demo shows how to render a personal view into a texture.<br/>
 * see also 'UsingMultipleViews'
 */

public class UsingAPersonalPointOfView
    extends AnimatorRenderer {

    private JoglPersonalPointView _myPersonalView;

    private Plane _myEgoDisplay;

    private Cuboid _myPersonalViewEntity;

    private JoglTextureReader _myReader;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);
        light().enable = true;
        light().position().set(200, 300, 0);
        camera().position().set(400, 800, 0);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().fovy = 90;

        /* create a bin to share between windows */
        TwoSidedBin mySharedBin = new TwoSidedBin(100);
        bin(BIN_3D).add(mySharedBin);

        /* create world and add it to the shared bin */
        for (int i = 0; i < 100; i++) {
            Cuboid myCube = drawablefactory().cuboid();
            myCube.position().x = new Random().getFloat( -1000, 1000);
            myCube.position().z = new Random().getFloat( -1000, 1000);
            myCube.scale().x = 100 - Math.abs(myCube.position().x) * 0.1f;
            myCube.scale().y = 100;
            myCube.scale().z = 100 - Math.abs(myCube.position().z) * 0.1f;
            myCube.material().lit = true;
            myCube.material().color4f().set(1, 0, 0);
            mySharedBin.add(myCube);
        }

        /* create personal view */
        _myPersonalView = new JoglPersonalPointView(mySharedBin,
                                                    new Viewport(0, 0, 256, 128),
                                                    drawablefactory().camera(),
                                                    drawablefactory().texture());
        bin(BIN_3D_SETUP).add(_myPersonalView);

        /* the personal view should be drawn before the camera() */
        bin(BIN_3D_SETUP).swap(_myPersonalView, camera());

        /* set some values in the personal view */
        _myPersonalView.camera().add(light());
        _myPersonalView.camera().fovy = CAMERA_A_HANDY_ANGLE;
        _myPersonalView.framebuffercopy().backgroundcolor.set(displaycapabilities().backgroundcolor);

        /* create a display for the personal point of view */
        _myEgoDisplay = drawablefactory().plane();
        _myEgoDisplay.material().color4f().set(1, 0.5f, 0.5f, 0.75f);
        _myEgoDisplay.material().addPlugin(_myPersonalView.texture());
        _myEgoDisplay.setPlaneSizeToTextureSize();
        _myEgoDisplay.origin(SHAPE_ORIGIN_TOP_LEFT);
        _myEgoDisplay.position().set(displaycapabilities().width / -2,
                                  displaycapabilities().height / 2);
        bin(BIN_2D_FOREGROUND).add(_myEgoDisplay);

        /* create a shape to reveal the position of the personal point of view */
        _myPersonalViewEntity = drawablefactory().cuboid();
        _myPersonalViewEntity.setPositionRef(_myPersonalView.camera().position());
        _myPersonalViewEntity.scale().set(20, 20, 80);
        bin(BIN_3D).add(_myPersonalViewEntity);

        /* get texture data back */
        _myReader = new JoglTextureReader(_myPersonalView.texture());
        bin(BIN_ARBITRARY).add(_myReader);
    }


    public void loop(float theDeltaTime) {
        CameraMover.handleKeyEvent(_myPersonalView.camera(), event(), theDeltaTime);

        /* align the entity rotation with the camera rotation */
        _myPersonalViewEntity.transform().rotation.set(
            _myPersonalView.camera().getInversRotationMatrix());

        /* display current texture */
        if (event().keyPressed && event().keyCode == KEYCODE_SPACE) {
            ImageUtil.displayBitmap(_myReader.bitmap(), "", false);
        }
    }


    public static void main(String[] args) {
        new UsingAPersonalPointOfView().init();
    }
}
