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


package gestalt.util.meshcreator;


import gestalt.shape.FastBitmapFont;
import gestalt.extension.quadline.QuadBezierCurve;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.util.CameraMover;

import werkzeug.interpolation.InterpolateExponential;
import werkzeug.interpolation.Interpolator;


public class TestQuadBezierCurveTranslator
    extends AnimatorRenderer {

    private QuadBezierCurve[] _myBezierLine;

    private Material _myMaterial;

    private boolean _myCreatedVBO = false;

    private Mesh _myVBO;

    private FastBitmapFont _myFont;

    public void setup() {
        framerate(UNDEFINED);

        /* material */
        _myMaterial = drawablefactory().material();
        _myMaterial.transparent = true;
        _myMaterial.depthtest = false;
        _myMaterial.blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myMaterial.disableTextureCoordinates = true;

        /* create bezier line */
        createBezierLines();

        /* display framerate */
        _myFont = new FastBitmapFont();
        _myFont.color.set(1);
        _myFont.align = FastBitmapFont.LEFT;
        _myFont.position.set(displaycapabilities().width / -2 + 20, displaycapabilities().height / 2 - 20);
        bin(BIN_2D_FOREGROUND).add(_myFont);
    }


    public void loop(float theDeltaTime) {
        if (!_myCreatedVBO) {
            for (int i = 0; i < _myBezierLine.length; ++i) {
                float myRadius = 50;
                double myCounter = PI * 2 * (float) i / (float) _myBezierLine.length;
                float myX = (float) Math.sin(myCounter) * myRadius + event().mouseX;
                float myY = (float) Math.cos(myCounter) * myRadius + event().mouseY;
                float myZ = (float) Math.cos(myCounter) * myRadius * 5;

                _myBezierLine[i].begincontrol.set(myX, myY, myZ);
                _myBezierLine[i].endcontrol.set(myX, myY, myZ);
            }
        } else {
            camera().setMode(CAMERA_MODE_LOOK_AT);
        }

        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
        _myFont.text = "FRAMERATE : " + (int) (1 / theDeltaTime);
    }


    public void mousePressed(int x, int y, int thePressedMouseButton) {
        if (!_myCreatedVBO) {
            System.out.println("### INFO / creating VBO.");
            _myCreatedVBO = true;
            MeshCreator myMeshCreator = new MeshCreator();
            myMeshCreator.translators().add(new QuadBezierCurveTranslator());

            _myVBO = myMeshCreator.parse(bin(BIN_3D), drawablefactory());
            _myVBO.setMaterialRef(_myMaterial);

            bin(BIN_3D).clear();
            _myBezierLine = null;

            bin(BIN_3D).add(_myVBO);
        } else {
            System.out.println("### INFO / removing VBO.");
            _myCreatedVBO = false;
            bin(BIN_3D).clear();
            _myVBO = null;
            createBezierLines();
        }
    }


    private void createBezierLines() {
        _myBezierLine = new QuadBezierCurve[200];
        for (int i = 0; i < _myBezierLine.length; ++i) {
            _myBezierLine[i] = drawablefactory().extensions().quadbeziercurve();
            _myBezierLine[i].linewidth = 50;
            _myBezierLine[i].setResolution(50);
            _myBezierLine[i].setMaterialRef(_myMaterial);
            _myBezierLine[i].begincolor.a = 0.01f;
            _myBezierLine[i].endcolor.a = 0.03f;
            _myBezierLine[i].setColorRedInterpolator(new Interpolator(0, 0.5f, new InterpolateExponential(1.0f)));
            _myBezierLine[i].setColorGreenInterpolator(new Interpolator(0, 0.5f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].setColorBlueInterpolator(new Interpolator(0, 1f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].setColorAlphaInterpolator(new Interpolator(0.01f, 0.08f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].begin.set( -displaycapabilities().width / 2 + 150, 0, 0);
            _myBezierLine[i].end.set(displaycapabilities().width / 2 - 150, 0, 0);
            _myBezierLine[i].setLineWidthInterpolator(new Interpolator(10, 100, new InterpolateExponential(0.5f)));
            _myBezierLine[i].autoupdate(true);
            bin(BIN_3D).add(_myBezierLine[i]);
        }
    }


    public static void main(String[] args) {
        new TestQuadBezierCurveTranslator().init();
    }
}
