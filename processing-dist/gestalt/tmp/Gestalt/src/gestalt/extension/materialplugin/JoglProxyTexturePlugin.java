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


package gestalt.extension.materialplugin;


import gestalt.context.GLContext;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.material.TexturePlugin;
import gestalt.material.Material;
import gestalt.material.texture.Bitmap;


public class JoglProxyTexturePlugin
        extends TexturePlugin {

    private final TexturePlugin _myParentTexture;

    public JoglProxyTexturePlugin(final TexturePlugin theParentTexture) {
        super(theParentTexture.hint_flip_axis());
        _myParentTexture = theParentTexture;
        setWrapMode(_myParentTexture.getWrapMode());
        setTextureTarget(_myParentTexture.getTextureTarget());
        setTextureUnit(_myParentTexture.getTextureUnit());
        setFilterType(_myParentTexture.getFilterType());
        nonpoweroftwotexturerescale().set(_myParentTexture.nonpoweroftwotexturerescale());
        position().set(_myParentTexture.position());
        scale().set(_myParentTexture.scale());
    }

    protected void init(final GL gl) {
    }

    public void begin(GLContext theRenderContext, Material theParent) {
        super.begin(theRenderContext, theParent);
    }

    public void end(GLContext theRenderContext, Material theParent) {
        super.end(theRenderContext, theParent);
    }

    public int getTextureID() {
        return _myParentTexture.getTextureID();
    }

    public void update(final GL gl, final GLU glu) {
        if (!_myParentTexture.isInitialized()) {
            _myParentTexture.update(gl, glu);
        }

        _myOpenGLTextureID = getTextureID();

        /* enable and bind texture */
        bind(gl);

        /* update properties */
        if (_myWrapModeChanged) {
            updateWrapMode(gl);
            _myWrapModeChanged = false;
        }

        if (_myFilterTypeChanged) {
            updateFilterType(gl);
            _myFilterTypeChanged = false;
        }

        if (_myBorderColorChanged) {
            updateBorderColor(gl);
            _myBorderColorChanged = false;
        }
    }

    public int getPixelWidth() {
        return _myParentTexture.getPixelWidth();
    }

    public int getPixelHeight() {
        return _myParentTexture.getPixelHeight();
    }

    protected void handleScheduledBitmap(GL gl, GLU glu, Bitmap theScheduledBitmap) {
    }

    protected void updateData(final GL gl) {
    }

    protected void changeData(final GL gl, final GLU glu) {
    }

    public void dispose(GLContext theRenderContext) {
    }
}
