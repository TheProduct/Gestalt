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


package gestalt.util.scenewriter;


import gestalt.extension.quadline.TubeLine;
import gestalt.render.SketchRenderer;
import gestalt.material.Color;
import mathematik.Vector3f;


public class TestTubeLineTranslator
        extends SketchRenderer {

    private TubeLine mTubeLine;

    public void setup() {
        setupDefaults();
        backgroundcolor().set(0.2f);

        mTubeLine = drawablefactory().extensions().tubeline();
        bin(BIN_3D).add(mTubeLine);

        mTubeLine.points = new Vector3f[10];
        mTubeLine.linewidthset = new float[mTubeLine.points.length];
        mTubeLine.colors = new Color[mTubeLine.points.length];
        mTubeLine.steps = 8;

        randomizeSet();
    }

    public void loop(float theDeltaTime) {
        if (mouseDown && mouseButton == MOUSEBUTTON_RIGHT) {
            randomizeSet();
        }

        if (event().keyPressed('s')) {
            new SceneWriter(System.getProperty("user.home") + "/Desktop/" + getClass().getSimpleName() + "-" + werkzeug.Util.now() + ".obj",
                            bin(BIN_3D));
        }
    }

    private void randomizeSet() {
        final float mRadius = 100;
        final float mStepSize = TWO_PI / (float)(mTubeLine.points.length - 1);
        for (int i = 0; i < mTubeLine.points.length - 1; i++) {
            final float mAngle = mStepSize * i;
            final float x = sin(mAngle) * mRadius;
            final float y = cos(mAngle) * mRadius;
            mTubeLine.points[i] = new Vector3f(x, y, 0);
            mTubeLine.points[i].add(0, random(-10, 10), random(-10, 10));
            mTubeLine.linewidthset[i] = random(2, 20);
            mTubeLine.colors[i] = new Color(1, random(0.4f, 1.0f), random(0.4f, 1.0f));
        }
        mTubeLine.points[mTubeLine.points.length - 1] = mTubeLine.points[0];
        mTubeLine.linewidthset[mTubeLine.linewidthset.length - 1] = mTubeLine.linewidthset[0];
        mTubeLine.colors[mTubeLine.colors.length - 1] = mTubeLine.colors[0];

        mTubeLine.update();
    }

    public static void main(String[] args) {
        new TestTubeLineTranslator().init();
    }
}
