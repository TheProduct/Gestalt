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


import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;


public class JoglMaterialPluginTexture3D
        extends TexturePlugin {

    private static int _myMaxTextureSize;

    private static boolean ourStatic3DInitalized = false;

    public JoglMaterialPluginTexture3D(final boolean theHintFlipYAxis) {
        super(theHintFlipYAxis);
        setTextureTarget(GL.GL_TEXTURE_3D);
    }

    public void update(final GL gl, final GLU glu) {
        /* initialize texture */
        if (!ourStatic3DInitalized) {
            initStatic(gl);
        }
        super.update(gl, glu);
    }

    public int getMaxTextureSize() {
        return _myMaxTextureSize;
    }

    public void initStatic(final GL gl) {
        /* query maximum texture size */
        int[] myValue = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_MAX_3D_TEXTURE_SIZE, myValue, 0);
        _myMaxTextureSize = myValue[0];
        ourStatic3DInitalized = true;
    }

    protected void updateData(final GL gl) {
        /*      void glTexSubImage3D( GLenum target,
        GLint level,
        GLint xoffset,
        GLint yoffset,
        GLint zoffset,
        GLsizei width,
        GLsizei height,
        GLsizei depth,
        GLenum format,
        GLenum type,
        const GLvoid *pixels )
         */

        /** @todo what about MIPMAPSs here. */
        /** @todo here we could really use some idea about updating 2D portions only */
        final ByteBitmap3D myBitmap = (ByteBitmap3D)_myBitmap;
        final int x = 0;
        final int y = 0;
        final int z = 0;

        /** @todo JSR-231 performance hit? */
        ByteBuffer myBuffer = ByteBuffer.wrap(myBitmap.getByteDataRef());
        gl.glTexSubImage3D(getTextureTarget(),
                           0,
                           x,
                           y,
                           z,
                           myBitmap.getWidth(),
                           myBitmap.getHeight(),
                           myBitmap.getDepth(),
                           getFormat(myBitmap.getComponentOrder()),
                           getOpenGLType(myBitmap.getComponentOrder()),
                           myBuffer);
    }

    protected void changeData(final GL gl, final GLU glu) {
        /*   void glTexImage3D( GLenum target,
        GLint level,
        GLint internalFormat,
        GLsizei width,
        GLsizei height,
        GLsizei depth,
        GLint border,
        GLenum format,
        GLenum type,
        const GLvoid *pixels )
         */
        if (_myBitmap instanceof ByteBitmap3D) {
            final ByteBitmap3D myBitmap = (ByteBitmap3D)_myBitmap;

            if (getFilterType() == TEXTURE_FILTERTYPE_LINEAR || getFilterType() == TEXTURE_FILTERTYPE_NEAREST) {
                /** @todo JSR-231 performance hit? */
                ByteBuffer myBuffer = ByteBuffer.wrap(myBitmap.getByteDataRef());
                gl.glTexImage3D(getTextureTarget(),
                                0,
                                GL.GL_RGBA,
                                myBitmap.getWidth(),
                                myBitmap.getHeight(),
                                myBitmap.getDepth(),
                                0,
                                getFormat(myBitmap.getComponentOrder()),
                                getOpenGLType(myBitmap.getComponentOrder()),
                                myBuffer);
            } else if (getFilterType() == TEXTURE_FILTERTYPE_MIPMAP) {
                /** @todo JSR-231 performance hit? */
                ByteBuffer myBuffer = ByteBuffer.wrap(myBitmap.getByteDataRef());
                glu.gluBuild3DMipmaps(getTextureTarget(),
                                      GL.GL_RGBA,
                                      myBitmap.getWidth(),
                                      myBitmap.getHeight(),
                                      myBitmap.getDepth(),
                                      getFormat(_myBitmap.getComponentOrder()),
                                      getOpenGLType(_myBitmap.getComponentOrder()),
                                      myBuffer);
            }
        } else {
            System.err.println("### WARNING @ " + getClass().getName() +
                    " / bitmap is of unsupported type. / " + _myBitmap);
        }
    }

    public void setBitmapRef(final Bitmap theBitmap) {
        if (!(theBitmap instanceof ByteBitmap3D)) {
            System.err.println("### WARNING @ " + getClass().getName() +
                    " / bitmap is of unsupported type. / " + theBitmap);
        } else if (_myBitmap != null) {
            final ByteBitmap3D myNewBitmap = (ByteBitmap3D)theBitmap;
            final ByteBitmap3D myOldBitmap = (ByteBitmap3D)_myBitmap;
            if (myNewBitmap.getWidth() != myOldBitmap.getWidth() &&
                    myNewBitmap.getHeight() != myOldBitmap.getHeight() &&
                    myNewBitmap.getDepth() != myOldBitmap.getDepth()) {
                System.err.println("### WARNING @ " + getClass().getName() +
                        " / new bitmap s dimensions don t match. / " + theBitmap);
            }
        }
        _myBitmap = theBitmap;
    }

    protected void setNPOTTextureScale() {
        /* this method is not needed */
    }
}
