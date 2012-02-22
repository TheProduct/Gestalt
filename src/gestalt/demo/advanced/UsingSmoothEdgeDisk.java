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

import gestalt.shape.SmoothDisk;
import gestalt.shape.SmoothRing;
import gestalt.render.SketchRenderer;


public class UsingSmoothEdgeDisk
        extends SketchRenderer {

    private SmoothDisk mDisk;

    private SmoothRing mRing;

    public void setup() {
        backgroundcolor().set(0.2f);

        mDisk = new SmoothDisk();
        mDisk.position().x = 150;
        mDisk.scale(100, 100);
        bin(BIN_3D).add(mDisk);

        mRing = new SmoothRing();
        mRing.position().x = -150;
        mRing.scale(100, 100);
        bin(BIN_3D).add(mRing);
    }

    public void loop(final float theDeltaTime) {
        mDisk.edge_width(event().normalized_mouseX * 50);
        mRing.edge_width(event().normalized_mouseX * 50);
        mRing.line_width(event().normalized_mouseY * 50);
    }

    public void mousePressed() {
        mRing.compile_into_displaylist();
        mDisk.compile_into_displaylist();
    }

    public static void main(String[] args) {
        SketchRenderer.init(UsingSmoothEdgeDisk.class);
    }
}
