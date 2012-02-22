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

import gestalt.G;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.gpgpu.cawater.GPGPUCAWater;
import gestalt.render.SketchRenderer;
import gestalt.shape.Plane;


public class UsingGPGPUCAWater
        extends SketchRenderer {

    private GPGPUCAWater _mySimulation;

    private Plane _myWaterView;

    private JoglFrameBufferObject _myInputEnergyMap;

    public void setup() {
        cameramover(true);
        framerate(60);
        displaycapabilities().backgroundcolor.set(0.5f);

        bin(BIN_2D_FOREGROUND).add(stats_view());

        /* setup shader */
        final ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(myShaderManager);

        /* heightmap */
        _myInputEnergyMap = JoglFrameBufferObject.createRectangular(width / 2, height);
        _myInputEnergyMap.add(g());
        _myInputEnergyMap.scale().set(width / 2, height);
        bin(BIN_FRAME_SETUP).add(_myInputEnergyMap);

        final Plane myHeightfieldView = G.plane();
        myHeightfieldView.material().addTexture(_myInputEnergyMap);
        myHeightfieldView.setPlaneSizeToTextureSize();
        myHeightfieldView.position().x -= myHeightfieldView.scale().x * 0.5f;

        /* simulation */
        _mySimulation = new GPGPUCAWater(myShaderManager,
                                         _myInputEnergyMap,
                                         "demo/shader/gpgpu/CAWaterSimulation.fs",
                                         "demo/shader/gpgpu/CAWaterDrawer.fs");
        bin(BIN_FRAME_SETUP).add(_mySimulation);

        /* simulation view */
        _myWaterView = G.plane();
        _mySimulation.attachWater(_myWaterView.material());
        _myWaterView.setPlaneSizeToTextureSize();
        _myWaterView.position().x += _myWaterView.scale().x * 0.5f + 1;
    }

    public void loop(float theDeltaTime) {
        _mySimulation.damping = 0.99f;
//        _mySimulation.flow_direction.set(event().normalized_mouseX - 0.5f,
//                                         event().normalized_mouseY - 0.5f);
//        _mySimulation.flow_direction.normalize();

        g().line(event().mouseX + 2, event().mouseY + 2,
                 event().pmouseX - 2, event().pmouseY - 2);
        g().line(event().mouseX - 2, event().mouseY + 2,
                 event().pmouseX + 2, event().pmouseY - 2);
        g().line(100, 100, -100, -100);
        g().line(-100, 100, 100, -100);

        addStatistic("FPS", 1.0f / theDeltaTime);
        addStatistic("damping", _mySimulation.damping);
    }

    public static void main(String[] args) {
        G.init(UsingGPGPUCAWater.class);
    }
}
