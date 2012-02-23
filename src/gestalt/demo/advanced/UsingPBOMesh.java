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
import gestalt.extension.framebufferobject.JoglPBOIndexedMesh;
import gestalt.extension.framebufferobject.JoglPBOIndexedPointMesh;
import gestalt.extension.framebufferobject.JoglPBOIndexedTriangleMesh;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_RGBA32Float;
import gestalt.render.controller.Camera;
import gestalt.shape.Plane;
import gestalt.render.SketchRenderer;
import gestalt.render.controller.cameraplugins.ArcBall;
import mathematik.PerlinNoise;


/**
 * this demo shows how to use texture memory ( via FBO ) to control the vertex positions of a VBO mesh.
 */
public class UsingPBOMesh
    extends SketchRenderer {

    private JoglFrameBufferObject _myFBO;

    private JoglPBOIndexedMesh _myPBOMesh;

    private float _myCounter;

    public void setup() {
        framerate(UNDEFINED);
        bin(BIN_2D_FOREGROUND).add(stats_view());
        cameramover(true);
        camera().plugins().add(new ArcBall());
        backgroundcolor().set(0.3f, 1.0f);

        _myFBO = JoglFrameBufferObject.create(16, 31);
        /* also works for float FBOs */
        _myFBO = JoglFrameBufferObject.createDefault(16, 31,
                                                     new Camera(),
                                                     true, true,
                                                     new JoglTexCreatorFBO_RGBA32Float());

        _myFBO.backgroundcolor().set(1, 0, 0, 1);
        _myFBO.add(g());
        bin(BIN_FRAME_SETUP).add(_myFBO);

        _myPBOMesh = new JoglPBOIndexedTriangleMesh(_myFBO, false, false, false);
        /* alternatively a point mesh can be used */
//        _myPBOMesh = new JoglPBOIndexedPointMesh(_myFBO, false);
        _myPBOMesh.position().set( -64, -64, -64);
        _myPBOMesh.scale().set(128, 128, 128);
        _myPBOMesh.material().color4f().set(1, 0.25f);
        _myPBOMesh.material().wireframe = true;
        bin(BIN_3D).add(_myPBOMesh);

        final Plane myFloor = new Plane();
        myFloor.scale().set(_myPBOMesh.scale());
        myFloor.material().wireframe = true;
        myFloor.material().color4f().set(1, 1, 0, 1);
        bin(BIN_3D).add(myFloor);
    }


    public void loop(final float theDeltaTime) {
        _myCounter += theDeltaTime * 0.5f;
        for (int x = 0; x < _myFBO.getPixelWidth(); x++) {
            for (int y = 0; y < _myFBO.getPixelHeight(); y++) {
                final float myX = (float) x / (float) _myFBO.getPixelWidth();
                final float myY = (float) y / (float) _myFBO.getPixelHeight();
                g().color().set(myX, myY, PerlinNoise.noise(myX + _myCounter, myY), 1);
                g().point(x - _myFBO.getPixelWidth() / 2, y - _myFBO.getPixelHeight() / 2);
            }
        }

        addStatistic("FPS", 1.0f / theDeltaTime);
    }


    public static void main(String[] args) {
        G.init(UsingPBOMesh.class, 640, 480, 2);
    }
}
