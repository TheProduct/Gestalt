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


package gestalt.extension.materialplugin.joglutiltexture;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


/**
 * this class provides a proxy for the texture class shipped with the jogl util library.
 * it is by far not fully implemented.
 */
public class JoglUtilTexture
        extends TexturePlugin {

    private Texture _myJoglUtilTexture;

    private TextureData _myScheduledTextureData;

    public JoglUtilTexture(final boolean theHintFlipYAxis) {
        super(theHintFlipYAxis);
        setTextureTarget(GL.GL_TEXTURE_2D);
        setTextureUnit(GL.GL_TEXTURE0);
    }

    public Texture getTextureRef() {
        return _myJoglUtilTexture;
    }

    public void setTextureRef(Texture theJoglUtilTexture) {
        _myJoglUtilTexture = theJoglUtilTexture;
    }

    public void scheduleTextureData(TextureData theScheduledTextureData) {
        _myScheduledTextureData = theScheduledTextureData;
    }

    public void begin(GLContext theRenderContext, Material theParent) {
        if (_myJoglUtilTexture == null) {
            _myJoglUtilTexture = TextureIO.newTexture(super.getTextureTarget());
        }

        if (_myScheduledTextureData != null) {
            _myJoglUtilTexture.updateImage(_myScheduledTextureData);
            _myScheduledTextureData = null;
        }

        super.begin(theRenderContext, theParent);
    }

    public final int getTextureTarget() {
        /** @todo ??? is this good? */
        if (_myJoglUtilTexture == null) {
            return super.getTextureTarget();
        }
        return _myJoglUtilTexture.getTarget();
    }

    public int getTextureID() {
        if (_myJoglUtilTexture == null) {
            return -1;
        }
        return _myJoglUtilTexture.getTextureObject();
    }

    public int getPixelWidth() {
        if (_myScheduledTextureData != null) {
            return _myScheduledTextureData.getWidth();
        }
        if (_myJoglUtilTexture != null) {
            return _myJoglUtilTexture.getImageWidth();
        }
        return 0;
    }

    public int getPixelHeight() {
        if (_myScheduledTextureData != null) {
            return _myScheduledTextureData.getHeight();
        }
        if (_myJoglUtilTexture != null) {
            return _myJoglUtilTexture.getImageHeight();
        }
        return 0;
    }


    /* obsolete TexturePlugin methods */
    public void load(final Bitmap theBitmap) {
    }

    public void reload() {
    }

    public Bitmap bitmap() {
        /** @todo can we actually return the bitmap?  */
        return null;
    }

    public void setBitmapRef(final Bitmap theBitmap) {
    }


    /* obsolete TexturePlugin methods */
    protected void init(final GL gl) {
    }

    protected void handleScheduledBitmap(GL gl, GLU glu, Bitmap theScheduledBitmap) {
    }

    protected void updateData(final GL gl) {
    }

    protected void changeData(final GL gl, final GLU glu) {
    }

    protected void updateWrapMode(final GL gl) {
    }

    protected void updateFilterType(final GL gl) {
    }

    protected void updateBorderColor(final GL gl) {
    }

    public static int getOpenGLType(int theType) {
        return Gestalt.UNDEFINED;
    }

    public int getFormat(int theType) {
        return Gestalt.UNDEFINED;
    }

    public void dispose(GLContext theRenderContext) {
        _myJoglUtilTexture.dispose();
    }
}
