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
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderMaterial;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.framebufferobject.JoglFloatDataBuffer;
import gestalt.context.GLContext;
import gestalt.render.SketchRenderer;
import gestalt.render.controller.cameraplugins.ArcBall;
import gestalt.material.Material;
import gestalt.shape.Plane;

import data.Resource;


public class UsingFloatDataBuffers
    extends SketchRenderer {

    private JoglFloatDataBuffer mBuffer;

    public void setup() {
        framerate(UNDEFINED);
        bin(BIN_2D_FOREGROUND).add(stats_view());
        camera().plugins().add(new ArcBall());

        mBuffer = new JoglFloatDataBuffer(640, 480);
        for (int i = 0; i < mBuffer.buffer().length; i += JoglFloatDataBuffer.components_per_fragment) {
            mBuffer.buffer()[i + 0] = (float) i / (float) mBuffer.buffer().length;
            mBuffer.buffer()[i + 1] = (float) i / (float) mBuffer.buffer().length;
            mBuffer.buffer()[i + 2] = (float) Math.random();
            mBuffer.buffer()[i + 3] = 1.0f;
        }
        mBuffer.scheduleUpdate();

        final ShaderManager sm = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(sm);
        final ShaderProgram sp = sm.createShaderProgram();
        sm.attachFragmentShader(sp, Resource.getStream("demo/shader/FloatDataBuffers.fs"));

        final ShaderMaterial myShaderMaterial = new ShaderMaterial(sm, sp) {

            public void setUniforms() {
                setUniform("data_buffer", 0);
            }


            public void beginTextures(final GLContext theRenderContext, final Material theParent) {
                mBuffer.begin(theRenderContext, theParent);
            }


            public void endTextures(final GLContext theRenderContext, final Material theParent) {
                mBuffer.end(theRenderContext, theParent);
            }
        };

        final Plane p = G.plane();
        p.scale(640, 480, 1);
        p.material().addPlugin(myShaderMaterial);
    }


    public void loop(final float theDeltaTime) {
        addFPS(theDeltaTime);
    }


    public static void main(String[] args) {
        G.init(UsingFloatDataBuffers.class, 640, 480, 2);
    }
}
