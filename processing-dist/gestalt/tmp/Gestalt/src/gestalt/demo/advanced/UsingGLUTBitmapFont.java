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


import gestalt.shape.FastBitmapFont;
import gestalt.render.AnimatorRenderer;


/**
 * this demo shows how to use the GLUT bitmapfont.
 * GLUT fonts are fonts built into opengl and don t require
 * any external fontrendering library. they are fast but
 * typograhpically not very challanging.
 */

public class UsingGLUTBitmapFont
    extends AnimatorRenderer {

    private FastBitmapFont _myFont;

    public void setup() {
        _myFont = new FastBitmapFont();
        _myFont.color.set(1, 1);
        _myFont.align = FastBitmapFont.CENTERED;
        bin(BIN_2D_FOREGROUND).add(_myFont);
    }


    public void loop(float theDeltaTime) {
        _myFont.position.set(event().mouseX, event().mouseY);
        _myFont.text = _myFont.position.toString();
    }


    public static void main(String[] args) {
        new UsingGLUTBitmapFont().init();
    }
}
