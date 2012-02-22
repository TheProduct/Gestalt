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


package gestalt.candidates;

import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.PointSprite;
import gestalt.material.texture.Bitmap;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.shape.AbstractDrawable;

import mathematik.Vector3f;

import java.util.Vector;


public abstract class JoglLEDDisplay
        extends AbstractDrawable {

    public static JoglLEDDisplay create(final int theWidth, final int theHeight,
                                        final Bin theFBOBin,
                                        final Bitmap thePointSprite,
                                        final boolean theRunOnGPU) {
        return theRunOnGPU ? new JoglLEDDisplayGPU(theWidth,
                                                   theHeight,
                                                   theFBOBin,
                                                   thePointSprite) : new JoglLEDDisplayCPU(theWidth,
                                                                                           theHeight,
                                                                                           theFBOBin,
                                                                                           thePointSprite);
    }

    public abstract void addMask(Bitmap theBitmap);

    public abstract Vector3f scale();

    public abstract Color backgroundcolor();

    public abstract RenderBin bin();

    public abstract Camera camera();

    public abstract PointSprite getPointSpriteMaterialPlugin();

    public abstract Material material();

    public abstract Vector<Vector3f> points();

    public abstract JoglFrameBufferObject fbo();
}
