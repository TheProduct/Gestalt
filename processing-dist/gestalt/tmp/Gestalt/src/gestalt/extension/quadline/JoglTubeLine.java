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


package gestalt.extension.quadline;


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.extension.quadline.TubeLine;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.util.JoglUtil;


public class JoglTubeLine
        extends TubeLine {

    public JoglTubeLine() {
        material = new Material();
    }

    public void draw(final GLContext theRenderContext) {

        if (mAutoUpdate) {
            update();
        }

        final GL gl = theRenderContext.gl;

        /* begin material */
        material.begin(theRenderContext);

        /* draw quads */
        if (fragments() != null) {
            for (int i = 0; i < fragments().length; i++) {
                JoglUtil.drawQuadFragments(gl, fragments()[i], material.disableTextureCoordinates);
            }
        }

        /* end material */
        material.end(theRenderContext);

        /* draw children */
        drawChildren(theRenderContext);
    }
}


