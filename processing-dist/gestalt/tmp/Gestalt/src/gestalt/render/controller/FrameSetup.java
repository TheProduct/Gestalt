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


package gestalt.render.controller;

import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;

import javax.media.opengl.GL;


public class FrameSetup
        extends AbstractDrawable {

    public boolean colorbufferclearing = true;

    public boolean depthbufferclearing = true;

    protected boolean _myFirstFrame = true;

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* set clear color */
        gl.glClearColor(theRenderContext.displaycapabilities.backgroundcolor.r,
                        theRenderContext.displaycapabilities.backgroundcolor.g,
                        theRenderContext.displaycapabilities.backgroundcolor.b,
                        theRenderContext.displaycapabilities.backgroundcolor.a);

        /* clear framebuffer at least once */
        if (_myFirstFrame) {
            _myFirstFrame = false;
            gl.glClearDepth(1.0f);
            gl.glDepthMask(true);
            gl.glColorMask(true, true, true, true); /* dito */
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        }

        /* clearing */
        if (colorbufferclearing && depthbufferclearing) {
            /*
             * this is important otherwise the depthbuffer is possibly ignored by glClear
             */
            gl.glDepthMask(true);
            gl.glColorMask(true, true, true, true); /* dito */
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        } else if (colorbufferclearing && !depthbufferclearing) {
            gl.glColorMask(true, true, true, true);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        } else if (!colorbufferclearing && depthbufferclearing) {
            gl.glDepthMask(true);
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        }

        /* depth testing */
        gl.glDepthFunc(GL.GL_LEQUAL);
        /*
         * GL_NEVER Never passes. GL_LESS Passes if the incoming depth value is less than the stored depth value.
         * GL_EQUAL Passes if the incoming depth value is equal to the stored depth value. GL_LEQUAL Passes if the
         * incoming depth value is less than or equal to the stored depth value. GL_GREATER Passes if the incoming depth
         * value is greater than the stored depth value. GL_NOTEQUAL Passes if the incoming depth value is not equal to
         * the stored depth value. GL_GEQUAL Passes if the incoming depth value is greater than or equal to the stored
         * depth value. GL_ALWAYS Always passes.
         */

        /* viewport */
        gl.glEnable(GL.GL_DEPTH_TEST);

        /* alpha test */
        /** @todo we can move this at some point to material. */
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0.0f);
    }
}
