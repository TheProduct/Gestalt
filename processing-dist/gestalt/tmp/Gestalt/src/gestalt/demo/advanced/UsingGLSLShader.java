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
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;

import data.Resource;
import gestalt.extension.glsl.ShaderMaterial;


/**
 * this demo show how to integrate glsl ( opengl shader language ) into gestalt.
 * note that you need a graphic card that supports glsl.
 * on OS X glsl is only available in 10.4+ and with hardware like
 * for example the Nvidia FX5900 in 12" powerbooks, or the Nvidia FX6800 in G5s.
 */
public class UsingGLSLShader
        extends AnimatorRenderer {

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    public void setup() {
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/vertex.vsh"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/fragment.fsh"));

        int myNumberOfPlanes = 10;
        float myPlaneSize = 25;
        for (int x = 0; x < myNumberOfPlanes; x++) {
            for (int y = 0; y < myNumberOfPlanes; y++) {
                Plane myShadedPlane = drawablefactory().plane();
                myShadedPlane.position().set(x * myPlaneSize - ((myNumberOfPlanes - 1) * myPlaneSize) / 2f,
                                             y * myPlaneSize - ((myNumberOfPlanes - 1) * myPlaneSize) / 2f,
                                             0);
                myShadedPlane.scale().set(myPlaneSize, myPlaneSize, 0);
                ShaderMaterial myShaderMaterial = new ShaderMaterial(_myShaderManager, _myShaderProgram) {

                    public void setUniforms() {
                        setUniform("myX", (float)Math.random() * 100f - 50f);
                        setUniform("myY", (float)Math.random() * 100f - 50f);
                    }
                };
                myShadedPlane.material().addPlugin(myShaderMaterial);
                myShadedPlane.material().color4f().set(1f, 1, 0, 1f);
                bin(BIN_3D).add(myShadedPlane);
            }
        }
    }

    public static void main(String[] args) {
        new UsingGLSLShader().init();
    }
}
