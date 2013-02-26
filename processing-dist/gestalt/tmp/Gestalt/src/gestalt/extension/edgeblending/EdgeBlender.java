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


package gestalt.extension.edgeblending;

import gestalt.Gestalt;
import gestalt.render.BasicRenderer;
import gestalt.material.TexturePlugin;


public abstract class EdgeBlender {

    public static void setup(BasicRenderer theRenderer, float theBlendArea) {
        /* copy texture */
        TexturePlugin myCopyTexture = new TexturePlugin(true);

        /* buffer copy */
        ColorBufferCopy myBufferCopy = new ColorBufferCopy(myCopyTexture,
                                                           theRenderer.displaycapabilities().width,
                                                           theRenderer.displaycapabilities().height);
        theRenderer.bin(Gestalt.BIN_3D_FINISH).add(myBufferCopy);

        /* create plane to show FBO */
        SoftEdger mySoftEdger = new SoftEdger(myCopyTexture,
                                              theRenderer.displaycapabilities().width,
                                              theRenderer.displaycapabilities().height,
                                              theBlendArea);
        theRenderer.bin(Gestalt.BIN_ARBITRARY).add(mySoftEdger);
    }
}
