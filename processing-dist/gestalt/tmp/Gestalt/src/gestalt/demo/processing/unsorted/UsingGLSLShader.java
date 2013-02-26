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


package gestalt.demo.processing.unsorted;


import gestalt.Gestalt;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.processing.GestaltPlugIn;

import data.Resource;
import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;


/**
 * this demo shows how to the GLSL shadermanager.
 */

public class UsingGLSLShader
    extends PApplet {

    private GestaltPlugIn gestalt;

    private ShaderManager _myShaderManager;

    private ShaderProgram _myShaderProgram;

    private float[][] _mySpherePosition;

    private float _myRotation;

    public void setup() {
        /* setup p5 */
        size(640, 480, OPENGL);
        sphereDetail(4);
        noStroke();

        gestalt = new GestaltPlugIn(this);

        /* setup shader */
        _myShaderManager = gestalt.drawablefactory().extensions().shadermanager();
        _myShaderManager.init( ( (PGraphicsOpenGL) g).gl, ( (PGraphicsOpenGL) g).glu);

        _myShaderProgram = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myShaderProgram, Resource.getStream("demo/shader/toon.vsh"));
        _myShaderManager.attachFragmentShader(_myShaderProgram, Resource.getStream("demo/shader/toon.fsh"));

        gestalt.bin(Gestalt.BIN_FRAME_SETUP).add(_myShaderManager);

        _myShaderManager.enable(_myShaderProgram);
        _myShaderManager.setUniform(_myShaderProgram, "thresholds", new float[] {0.1f, 0.2f, 0.5f, 0.95f});

        /* create sphere positions */
        _mySpherePosition = new float[1000][4];
        for (int i = 0; i < _mySpherePosition.length; i++) {
            _mySpherePosition[i][0] = random(width);
            _mySpherePosition[i][1] = random(height);
            _mySpherePosition[i][2] = random( -10, 10);
            _mySpherePosition[i][3] = random(10, 20);
        }
    }


    public void draw() {
        background(64, 64, 64);

        float myX = (mouseX / (float) width - 0.5f) * 2.0f;
        float myY = (mouseY / (float) height - 0.5f) * 2.0f;
        directionalLight(255, 255, 255, myX, myY, 1);

        translate(width / 2, height / 2);
        _myRotation += 0.03f;
        rotateX(sin(_myRotation) * 0.1f);
        rotateY(cos(_myRotation * 0.65f) * 0.1f);
        translate(width / -2, height / -2);

        for (int i = 0; i < _mySpherePosition.length; i++) {
            pushMatrix();
            translate(_mySpherePosition[i][0], _mySpherePosition[i][1], _mySpherePosition[i][2]);
            sphere(_mySpherePosition[i][3]);
            popMatrix();
        }
    }


    public void mousePressed() {
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingGLSLShader.class.getName()});
    }
}
