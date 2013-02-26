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


import gestalt.processing.G5;

import processing.core.PApplet;


public class UsingFBOs
    extends PApplet {

    private G5.FBO fbo;

    private float _myReScaleCounter;

    /* FBO size must be powers of two ( POT ) */

    private static final int FBO_WIDTH = 512;

    private static final int FBO_HEIGHT = 256;

    public void setup() {
        /* there are still issues with most window sizes */
        size(1024, 768, OPENGL);

        G5.setup(this);
        fbo = G5.fbo(FBO_WIDTH, FBO_HEIGHT);
        fbo.display().position().set(width / 2, height / 2);
    }


    public void draw() {
        /* draw into window */
        background(255, 0, 0);
        noStroke();

        fill(255, 127, 0);
        rect(10, 10, width - 20, height - 20);

        stroke(255);
        line(0, 0, mouseX, mouseY);
        line(0, height, mouseX, mouseY);
        line(width, height, mouseX, mouseY);
        line(width, 0, mouseX, mouseY);

        /* draw into FBO */
        fbo.bind();

        background(255, 255, 0);
        fill(255);
        rect(10, 10, FBO_WIDTH - 20, FBO_HEIGHT - 20);

        stroke(0);
        line(0, 0, mouseX, mouseY);
        line(0, FBO_HEIGHT, mouseX, mouseY);
        line(FBO_WIDTH, FBO_HEIGHT, mouseX, mouseY);
        line(FBO_WIDTH, 0, mouseX, mouseY);

        fbo.unbind();

        /* rescale FBO display */
        fbo.display().scale().x = FBO_WIDTH + sin(_myReScaleCounter) * FBO_WIDTH * 0.2f;
        fbo.display().scale().y = FBO_HEIGHT + cos(_myReScaleCounter) * FBO_HEIGHT * 0.18f;
        /* we need to flip the display because
         * the gestalt coordinate system
         * starts in the lower left corner.
         */
        fbo.display().scale().y *= -1;

        /* rotate FBO display */
        fbo.display().rotation().x = _myReScaleCounter * 0.24f;
        fbo.display().rotation().y = _myReScaleCounter * 0.07f;

        _myReScaleCounter += 0.5f * 1f / frameRate;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingFBOs.class.getName()});
    }
}
