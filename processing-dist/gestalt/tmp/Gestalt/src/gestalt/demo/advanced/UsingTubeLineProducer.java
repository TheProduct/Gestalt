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


import gestalt.extension.quadline.TubeLineProducer;
import gestalt.extension.quadline.QuadFragment;
import gestalt.render.SketchRenderer;
import gestalt.material.Color;
import mathematik.Vector3f;


public class UsingTubeLineProducer
        extends SketchRenderer {

    private TubeLineProducer mProducer;

    private Vector3f[] mLines;

    private Color[] mColors;

    private float[] mWidthSet;

    public void setup() {
        setupDefaults();
        backgroundcolor().set(0.2f);

        mProducer = new TubeLineProducer();
        mLines = new Vector3f[10];
        mWidthSet = new float[mLines.length];
        mColors = new Color[mLines.length];

        randomizeSet();
    }

    public void loop(float theDeltaTime) {
        if (mouseDown && mouseButton == MOUSEBUTTON_RIGHT) {
            randomizeSet();
        }

        QuadFragment[][] mFragments = mProducer.produce(mLines, mColors, mWidthSet, 6);

        g().wireframe = true;
        g().wireframe_color.set(1, 0.5f);
        for (int i = 0; i < mFragments.length; i++) {
            for (int j = 0; j < mFragments[i].length - 1; j++) {
                final QuadFragment f1 = mFragments[i][j];
                final QuadFragment f2 = mFragments[i][j + 1];
                g().color(f1.colorA);
                g().quad(f1.pointA, f2.pointA, f2.pointB, f1.pointB);
                Vector3f mPosition = mathematik.Util.add(f1.pointA, f1.pointB);
                mPosition.scale(0.5f);
                g().lineto(mPosition, mathematik.Util.scale(f1.normal, 10));
            }
        }
    }

    private void randomizeSet() {
        final float mRadius = 100;
        final float mStepSize = TWO_PI / (float)(mLines.length - 1);
        for (int i = 0; i < mLines.length - 1; i++) {
            final float mAngle = mStepSize * i;
            final float x = sin(mAngle) * mRadius;
            final float y = cos(mAngle) * mRadius;
            mLines[i] = new Vector3f(x, y, 0);
            mLines[i].add(0, random(-10, 10), random(-10, 10));
            mWidthSet[i] = random(1, 10);
            mColors[i] = new Color(1, random(0.4f, 1.0f), random(0.4f, 1.0f));
        }
        mLines[mLines.length - 1] = mLines[0];
        mWidthSet[mWidthSet.length - 1] = mWidthSet[0];
        mColors[mColors.length - 1] = mColors[0];
    }

    public static void main(String[] args) {
        new UsingTubeLineProducer().init();
    }
}
