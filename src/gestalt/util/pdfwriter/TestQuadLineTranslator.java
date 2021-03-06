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


package gestalt.util.pdfwriter;

import gestalt.extension.quadline.QuadLine;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Color;

import mathematik.Matrix4f;
import mathematik.Random;
import mathematik.Vector3f;
import mathematik.Vector4f;


public class TestQuadLineTranslator
        extends AnimatorRenderer {

    private PDFWriter _myPDFWriter;

    public void setup() {
        /* create line */
        bin(BIN_3D).add(makeQuadline());
        bin(BIN_3D).add(makeQuadline());

        _myPDFWriter = new PDFWriter("../thisisatest.pdf", displaycapabilities().width, displaycapabilities().height);
        bin(BIN_3D_FINISH).add(_myPDFWriter);
    }

    public void loop(float theDeltaTime) {
        if (event().keyPressed('s')) {
//            _myPDFWriter.g.drawLine(10, 10, 100, 100);
//            _myPDFWriter.close();
            _myPDFWriter.page(displaycapabilities().width, displaycapabilities().height);
            _myPDFWriter.drawBin(bin(BIN_3D));
//            _myPDFWriter.g().drawLine(10, 10, 100, 100);
            _myPDFWriter.close();
        }
        if (event().keyPressed('S')) {
            _myPDFWriter.close();
        }
    }

    private QuadLine makeQuadline() {
        QuadLine _myQuadLine = drawablefactory().extensions().quadline();
        _myQuadLine.points = new Vector3f[] {
            getRandomVector(0),
            getRandomVector(50),
            getRandomVector(100),
            getRandomVector(150)};
        _myQuadLine.colors = new Color[] {
            new Color(1, 0),
            new Color(1, 0.33f),
            new Color(1, 0.6f),
            new Color(1, 1)};
        _myQuadLine.linewidth = 20;
        _myQuadLine.update();
        return _myQuadLine;
    }

    private Vector3f getRandomVector(float theValue) {
        Random myRandom = new Random();
        return new Vector3f(myRandom.getFloat(0, theValue),
                            myRandom.getFloat(0, theValue),
                            myRandom.getFloat(0, theValue));
    }

    public static void main(String[] args) {
        new TestQuadLineTranslator().init();
    }
}
