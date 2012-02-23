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


import gestalt.material.PointSprite;
import gestalt.extension.glsl.ShaderManager;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.material.texture.Bitmaps;

import data.Resource;
import gestalt.extension.gpgpu.particles.GPGPU2DParticlesSimulation;


/**
 * this is well worth reading: http://www.mathematik.uni-dortmund.de/~goeddeke/gpgpu/tutorial.html
 */
public class UsingGPGPU2DParticles
    extends AnimatorRenderer {

    private ShaderManager _myShaderManager;

    private GPGPU2DParticlesSimulation _mySimulation;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);
        cameramover(true);
        fpscounter(true);
        framerate(60);

        /* setup shader */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        _mySimulation = new GPGPU2DParticlesSimulation(_myShaderManager,
                                                       256, 256,
                                                       width, height);
        bin(BIN_FRAME_SETUP).add(_mySimulation);

        /* create view */
        final GPGPU2DParticlesSimulation.ParticleDrawer myParticleDrawer = _mySimulation.view();
        final PointSprite myPointSprites = new PointSprite();
        myPointSprites.load(Bitmaps.getBitmap(Resource.getStream("demo/common/particle.png")));
        myPointSprites.quadric = new float[] {
                                 10,
                                 0.002f,
                                 0.000001f};
        myPointSprites.pointsize = 20;
        myPointSprites.minpointsize = 10;
        myPointSprites.maxpointsize = 512;
        myParticleDrawer.material().addPlugin(myPointSprites);
        myParticleDrawer.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        myParticleDrawer.material().depthtest = false;
        myParticleDrawer.material().color4f().set(1.0f, 0.15f);
        bin(BIN_3D).add(myParticleDrawer);
    }


    public void loop(final float theDeltaTime) {
        _mySimulation.setDeltaTime(theDeltaTime);
    }


    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 1024;
        myDisplayCapabilities.height = 768;
        myDisplayCapabilities.synctovblank = true;
        new UsingGPGPU2DParticles().init(myDisplayCapabilities);
    }
}
