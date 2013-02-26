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

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;
import gestalt.material.TexturePlugin;

import javax.media.opengl.GL;


public class ColorBufferCopy
        extends AbstractDrawable {

    private final TexturePlugin _myTexture;

    private final int _myWidth;

    private final int _myHeight;

    public ColorBufferCopy(TexturePlugin theTexture, int theWidth, int theHeight) {
        _myTexture = theTexture;
        _myWidth = theWidth;
        _myHeight = theHeight;
    }

    public void draw(GLContext theRenderContext) {

        /**
         * @todo there seems to be an issue here:
         * why is 'begin()' never used?
         * how is the texture created then?
         * */
        final GL gl = theRenderContext.gl;

        gl.glActiveTexture(_myTexture.getTextureUnit());
        gl.glEnable(_myTexture.getTextureTarget());
        gl.glBindTexture(GL.GL_TEXTURE_2D,
                         ((TexturePlugin)_myTexture).getTextureID());
        gl.glCopyTexImage2D(GL.GL_TEXTURE_2D,
                            0,
                            GL.GL_RGB,
                            0,
                            0,
                            _myWidth,
                            _myHeight, 0);
        gl.glActiveTexture(_myTexture.getTextureUnit());
        gl.glDisable(_myTexture.getTextureTarget());
    }
}
