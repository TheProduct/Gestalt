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
import gestalt.render.AnimatorRenderer;
import gestalt.material.texture.Bitmaps;

import data.Resource;
import gestalt.G;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.gpgpu.particles.GPGPU3DParticlesSimulation;
import gestalt.extension.gpgpu.particles.ParticleResetterRandom;
import gestalt.extension.gpgpu.particles.PlainParticleDrawer;
import gestalt.shape.Plane;
import javax.media.opengl.GL;


/**
 * this is well worth reading: http://www.mathematik.uni-dortmund.de/~goeddeke/gpgpu/tutorial.html
 */
public class UsingGPGPU3DParticles
        extends AnimatorRenderer {

    private ShaderManager _myShaderManager;

    private GPGPU3DParticlesSimulation _mySimulation;

    public void setup() {
        cameramover(true);
        fpscounter(true);
        framerate(60);

        /* setup shader */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        /* create heightfield FBO */
        JoglFrameBufferObject myHeightfield = JoglFrameBufferObject.createRectangular(width, height);
        myHeightfield.backgroundcolor().set(0, 1);
        myHeightfield.setTextureUnit(GL.GL_TEXTURE4);
        myHeightfield.name = "heightfield";
        bin(BIN_FRAME_SETUP).add(myHeightfield);

        /* setup heightmap  */
        Plane myHeightfieldBase = G.plane(myHeightfield.bin(), Resource.getStream("demo/common/gpgpuheightfield.png"));
        myHeightfieldBase.material().depthtest = false;

        /* simulation */
        _mySimulation = new GPGPU3DParticlesSimulation(_myShaderManager,
                                                       256, 256,
                                                       width, height,
                                                       myHeightfield,
                                                       "demo/shader/gpgpu/GPGPU3DParticle.vs",
                                                       "demo/shader/gpgpu/GPGPU3DParticle.fs");
        _mySimulation.flow_speed = 0.5f;
        bin(BIN_FRAME_SETUP).add(_mySimulation);
        _mySimulation.setDeltaTime(1 / 60f);

        /* particle resetter */
        ParticleResetterRandom myResetter = new ParticleResetterRandom(_myShaderManager,
                                                                       _mySimulation.getShaderProgram(),
                                                                       "demo/shader/gpgpu/GPGPUParticleReset.fs");
        myResetter.range = new float[] {-width / 2f, width / 2f};
        _mySimulation.setResetter(myResetter);

        /* create view */
        final PlainParticleDrawer myParticleDrawer = new PlainParticleDrawer();
        _mySimulation.setView(myParticleDrawer);

        final PointSprite myPointSprites = new PointSprite();
        myPointSprites.load(Bitmaps.getBitmap(Resource.getStream("demo/common/particle.png")));
        myPointSprites.pointsize = 5;
        myPointSprites.minpointsize = 10;
        myPointSprites.maxpointsize = 512;
        myParticleDrawer.material().addTexture(myPointSprites);
        myParticleDrawer.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        myParticleDrawer.material().depthtest = false;
        myParticleDrawer.material().color4f().set(1.0f, 0.1f);

        bin(BIN_3D).add(myParticleDrawer);
    }

    public static void main(String[] args) {
        G.init(UsingGPGPU3DParticles.class);
    }
}
