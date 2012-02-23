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

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.shadow.JoglGLSLShadowMap;
import gestalt.extension.shadow.JoglGLSLShadowMaterial4ud;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.Camera;
import gestalt.shape.Plane;
import gestalt.shape.Sphere;
import gestalt.util.CameraMover;

import data.Resource;


public class UsingGLSLShadows
    extends AnimatorRenderer {

    private boolean _myToggleCamera;

    private JoglGLSLShadowMap _myShadowMapExtension;

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    public void setup() {
        /* gestalt */
        framerate(60);
        displaycapabilities().backgroundcolor.set(0.5f, 0.5f, 0.5f);

        /* camera */
        camera().position().set( -400, 1000, 1000);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().nearclipping = 100;
        camera().farclipping = 10000;

        /* shader */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram,
                                            Resource.getStream("demo/shader/shadow/projectivetexture.vs"));
        _myShaderManager.attachFragmentShader(_myShaderProgram,
                                              Resource.getStream("demo/shader/shadow/shadowmap4d.fs"));
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        /* setup shadow map */
        final int myShadowMapWidth = 512;
        final int myShadowMapHeight = 512;
        _myShadowMapExtension = new JoglGLSLShadowMap(light(), myShadowMapWidth, myShadowMapHeight);
        _myShadowMapExtension.lightcamera.nearclipping = camera().nearclipping;
        _myShadowMapExtension.lightcamera.farclipping = camera().farclipping;
        bin(BIN_FRAME_SETUP).add(_myShadowMapExtension);

        /* create shapes */
        Sphere mySphereA = new Sphere();
        mySphereA.position().set(100, 50, -100);
        mySphereA.scale().set(100, 100, 100);
        mySphereA.material().lit = true;
        mySphereA.material().color4f().set(1, 0, 0, 1);
        mySphereA.material().addPlugin(new JoglGLSLShadowMaterial4ud(_myShaderManager,
                                                                     _myShaderProgram,
                                                                     _myShadowMapExtension,
                                                                     mySphereA));

        Sphere mySphereB = new Sphere();
        mySphereB.position().set(0, 100, 0);
        mySphereB.scale().set(100, 100, 100);
        mySphereB.material().lit = true;
        mySphereB.material().color4f().set(1, 0, 0, 1);
        mySphereB.material().addPlugin(new JoglGLSLShadowMaterial4ud(_myShaderManager,
                                                                     _myShaderProgram,
                                                                     _myShadowMapExtension,
                                                                     mySphereB));
        /** @todo textures don t work. */
//        TexturePlugin mySphereTexture = drawablefactory().texture();
//        mySphereTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/mask256.png")));
//        mySphereB.material().addPlugin(mySphereTexture);

        Sphere mySphereC = new Sphere();
        mySphereC.position().set(100, 350, 0);
        mySphereC.scale().set(100, 100, 100);
        mySphereC.material().lit = true;
        mySphereC.material().color4f().set(1, 0.5f, 0, 1);
        mySphereC.material().addPlugin(new JoglGLSLShadowMaterial4ud(_myShaderManager,
                                                                     _myShaderProgram,
                                                                     _myShadowMapExtension,
                                                                     mySphereC));

        Plane myPlane = new Plane();
        myPlane.origin(SHAPE_ORIGIN_BOTTOM_LEFT); // all other origins don t seem to work properly. hidden 'glTranslate' :(
        myPlane.scale().set(1000, 1000, 1);
        myPlane.position( -500, 0, 500);
        myPlane.rotation().x = -PI_HALF;
        myPlane.material().lit = true;
        myPlane.material().color4f().set(1, 1, 1, 1);
        myPlane.material().normalizenormals = true;
        myPlane.material().transparent = true;
        myPlane.material().addPlugin(new JoglGLSLShadowMaterial4ud(_myShaderManager,
                                                                   _myShaderProgram,
                                                                   _myShadowMapExtension,
                                                                   myPlane));

        /* add shapes to bins */
        bin(BIN_3D).add(mySphereA);
        bin(BIN_3D).add(mySphereB);
        bin(BIN_3D).add(mySphereC);
        bin(BIN_3D).add(myPlane);

        _myShadowMapExtension.addShape(mySphereA);
        _myShadowMapExtension.addShape(mySphereB);
        _myShadowMapExtension.addShape(mySphereC);

        /* light */
        light().enable = true;
        light().position().set(450, 720, 230);
        light().diffuse.set(1, 1, 1, 1);
        light().ambient.set(0, 0, 0, 1);
    }


    public void loop(float theDeltaTime) {

        JoglGLSLShadowMaterial4ud.epsilon = event().normalized_mouseX * 10f;
        JoglGLSLShadowMaterial4ud.shadowedVal = event().normalized_mouseY;

        /* toggle camera()s */
        if (event().keyPressed && event().key == 'c') {
            System.out.println("toggled camera().");
            _myToggleCamera = !_myToggleCamera;
        }

        Camera myCamera;
        if (_myToggleCamera) {
            myCamera = _myShadowMapExtension.lightcamera;
        } else {
            myCamera = camera();
        }

        /* move camera */
        CameraMover.handleKeyEvent(myCamera, event(), theDeltaTime);
    }


    public static void main(String[] args) {
        new UsingGLSLShadows().init(800, 600);
    }
}
