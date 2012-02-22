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
import gestalt.material.Color;
import gestalt.material.TexturePlugin;
import gestalt.shape.AbstractDrawable;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class FrameBufferCopy
        extends AbstractDrawable {

    public Color backgroundcolor = new Color();

    public boolean colorbufferclearing = true;

    public boolean depthbufferclearing = true;

    public int x;

    public int y;

    public int width;

    public int height;

    private final TexturePlugin _myTexture;

    public FrameBufferCopy(TexturePlugin theTexture) {
        if (theTexture instanceof TexturePlugin) {
            _myTexture = (TexturePlugin)theTexture;
        } else {
            _myTexture = null;
            System.out.println("### ERROR @ " + this.getClass().getName()
                    + " / texture needs to be of type 'TexturePlugin'.");
        }
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = (theRenderContext).gl;
        final GLU glu = (theRenderContext).glu;

        if (!_myTexture.isInitialized()) {
            _myTexture.update(gl, glu);
        }

        /* copy framebuffer into texture */
        gl.glEnable(_myTexture.getTextureTarget());
        gl.glBindTexture(_myTexture.getTextureTarget(),
                         _myTexture.getTextureID());

        gl.glCopyTexSubImage2D(_myTexture.getTextureTarget(),
                               0,
                               0,
                               0,
                               x,
                               y,
                               width,
                               height);

        /* clear screen -- color- and depthbuffer */
        if (depthbufferclearing) {
            gl.glDepthMask(true);
        }
        if (colorbufferclearing) {
            gl.glClearColor(backgroundcolor.r,
                            backgroundcolor.g,
                            backgroundcolor.b,
                            backgroundcolor.a);
            gl.glColorMask(true, true, true, true);
        }

        if (depthbufferclearing && colorbufferclearing) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        } else if (depthbufferclearing) {
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        } else if (colorbufferclearing) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        }

        gl.glDisable(_myTexture.getTextureTarget());
    }
}
