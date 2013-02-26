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


import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.shape.AbstractDrawable;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.material.texture.bitmap.IntegerBitmap;

import com.sun.opengl.util.BufferUtil;


public class JoglTextureReader
    extends AbstractDrawable {

    /** @todo
     * maybe there is a way to make this more flexible in terms of
     * non-power-of-two and changing bitmap formats.
     */

    private final TexturePlugin _myTexture;

    private final int _myLevel = 0;

    private Bitmap _myBitmap;

    private boolean _myIsActive = true;

    public JoglTextureReader(final TexturePlugin theTexture) {
        _myTexture = (TexturePlugin) theTexture;
    }


    public Bitmap bitmap() {
        return _myBitmap;
    }


    public boolean isActive() {
        return _myIsActive;
    }


    public void setActive(boolean theIsActive) {
        _myIsActive = theIsActive;
    }


    public void draw(GLContext theRenderContext) {

        /** @todo what about non power of two textures? getNextPowerOf2(int theValue) */
        /** @todo cache bitmap to prevent constant allocation of byte arrays. check for size and type. */

        final GL gl = (  theRenderContext).gl;

        if (_myTexture.bitmap() == null) {
            return;
        }

        if (_myTexture.bitmap() instanceof ByteBitmap) {
            /* create bitmap */
            _myBitmap = ByteBitmap.getDefaultImageBitmap(_myTexture.getPixelWidth(),
                                                         _myTexture.getPixelHeight());
            /* upload data */
            /** @todo JSR-231 check this! */
            ByteBuffer myBuffer = BufferUtil.newByteBuffer(_myTexture.getPixelWidth() *
                                                           _myTexture.getPixelHeight() *
                                                           ByteBitmap.NUMBER_OF_PIXEL_COMPONENTS);
            gl.glGetTexImage(_myTexture.getTextureTarget(),
                             _myLevel,
                             _myTexture.getFormat(_myBitmap.getComponentOrder()),
                             _myTexture.getOpenGLType(_myBitmap.getComponentOrder()),
                             myBuffer);
            _myBitmap = new ByteBitmap(myBuffer.array(),
                                       _myTexture.getPixelWidth(),
                                       _myTexture.getPixelHeight(),
                                       Gestalt.BITMAP_COMPONENT_ORDER_RGBA);
        } else if (_myTexture.bitmap() instanceof IntegerBitmap) {
            /** @todo JSR-231 check this! */

//            /* create bitmap */
//            _myBitmap = IntegerBitmap.getDefaultImageBitmap(_myTexture.getBitmapWidth(),
//                                                            _myTexture.getBitmapHeight());
//            /* upload data */
//            gl.glGetTexImage(_myTexture.getTextureTarget(),
//                             _myLevel,
//                             _myTexture.getFormat(_myBitmap.getComponentOrder()),
//                             _myTexture.getOpenGLType(_myBitmap.getComponentOrder()),
//                             (int[]) _myBitmap.getDataRef());

            IntBuffer myBuffer = BufferUtil.newIntBuffer(_myTexture.getPixelWidth() *
                                                         _myTexture.getPixelHeight() *
                                                         IntegerBitmap.NUMBER_OF_PIXEL_COMPONENTS);

            gl.glGetTexImage(_myTexture.getTextureTarget(),
                             _myLevel,
                             _myTexture.getFormat(_myBitmap.getComponentOrder()),
                             _myTexture.getOpenGLType(_myBitmap.getComponentOrder()),
                             myBuffer);
            _myBitmap = new IntegerBitmap(myBuffer.array(),
                                          _myTexture.getPixelWidth(),
                                          _myTexture.getPixelHeight(),
                                          Gestalt.BITMAP_COMPONENT_ORDER_RGBA);
        } else if (_myTexture.bitmap() instanceof ByteBufferBitmap) {
            /* create bitmap */
            _myBitmap = ByteBufferBitmap.getDefaultImageBitmap(_myTexture.getPixelWidth(),
                                                               _myTexture.getPixelHeight());
            /* upload data */
            gl.glGetTexImage(_myTexture.getTextureTarget(),
                             _myLevel,
                             _myTexture.getFormat(_myBitmap.getComponentOrder()),
                             _myTexture.getOpenGLType(_myBitmap.getComponentOrder()),
                             ( (ByteBufferBitmap) _myBitmap).getByteBufferDataRef());
        } else {
            System.err.println("### WARNING @" + getClass().getName() + " / 'Bitmap' type not recognized.");
            _myBitmap = null;
        }
    }

    /*
     NAME
       glGetTexImage - return a texture image

     C SPECIFICATION
       void glGetTexImage( GLenum target,
                           GLint level,
                           GLenum format,
                           GLenum type,
                           GLvoid *pixels )

     PARAMETERS
       target  Specifies  which  texture  is  to  be obtained.  GL_TEXTURE_1D,
               GL_TEXTURE_2D, and GL_TEXTURE_3D  are accepted.

       level   Specifies the level-of-detail  number  of  the  desired  image.
               Level  0  is  the  base image level.  Level n is the nth mipmap
               reduction image.

       format  Specifies a pixel  for the returned data.  The  supported  for-
               mats  are  GL_RED, GL_GREEN, GL_BLUE, GL_ALPHA, GL_RGB, GL_BGR,
               GL_RGBA, GL_BGRA, GL_LUMINANCE, and GL_LUMINANCE_ALPHA.

       type    Specifies a pixel type for the returned  data.   The  supported
               types   are   GL_UNSIGNED_BYTE,   GL_BYTE,   GL_UNSIGNED_SHORT,
               GL_SHORT,       GL_UNSIGNED_INT,       GL_INT,        GL_FLOAT,
               GL_UNSIGNED_BYTE_3_3_2,             GL_UNSIGNED_BYTE_2_3_3_REV,
               GL_UNSIGNED_SHORT_5_6_5,           GL_UNSIGNED_SHORT_5_6_5_REV,
               GL_UNSIGNED_SHORT_4_4_4_4,       GL_UNSIGNED_SHORT_4_4_4_4_REV,
               GL_UNSIGNED_SHORT_5_5_5_1,       GL_UNSIGNED_SHORT_1_5_5_5_REV,
               GL_UNSIGNED_INT_8_8_8_8,           GL_UNSIGNED_INT_8_8_8_8_REV,
               GL_UNSIGNED_INT_10_10_10_2, and GL_UNSIGNED_INT_2_10_10_10_REV.

       pixels  Returns  the texture image.  Should be a pointer to an array of
               the type specified by type.

     DESCRIPTION
       glGetTexImage returns a texture image into  pixels.   target  specifies
       whether  the  desired texture image is one specified by glTexImage1D (-
       GL_TEXTURE_1D),  glTexImage2D  (GL_TEXTURE_2D),  or   glTexImage3D   (-
       GL_TEXTURE_3D).   level  specifies  the  level-of-detail  number of the
       desired image.  format and type specify the  and type  of  the  desired
       image array.  See the reference pages glTexImage1D and glDrawPixels for
       a description of the acceptable values for the format and type  parame-
       ters, respectively.

       To  understand  the  operation  of glGetTexImage, consider the selected
       internal four-component texture image to be an RGBA  color  buffer  the
       size  of  the image.  The semantics of glGetTexImage are then identical
       to those of glReadPixels, with the exception  that  no  pixel  transfer
       operations  are  performed,  when called with the same format and type,
       with x and y set to 0, width set to the  width  of  the  texture  image
       (including  border  if  one  was specified), and height set to 1 for 1D
       images, or to the height of the texture image (including border if  one
       was specified) for 2D images.  Because the internal texture image is an
       RGBA  image,  pixel  formats  GL_COLOR_INDEX,   GL_STENCIL_INDEX,   and
       GL_DEPTH_COMPONENT  are  not  accepted, and pixel type GL_BITMAP is not
       accepted.

       If the selected texture image does not  contain  four  components,  the
       following  mappings are applied.  Single-component textures are treated
       as RGBA buffers with red set to the single-component value,  green  set
       to  0,   blue set to 0, and alpha set to 1.  Two-component textures are
       treated as RGBA buffers with red set to the value  of  component  zero,
       alpha  set  to the value of component one, and green and blue set to 0.
       Finally, three-component textures are treated as RGBA buffers with  red
       set  to  component zero, green set to component one, blue set to compo-
       nent two, and alpha set to 1.

       To determine the required size of pixels, use glGetTexLevelParameter to
       determine  the dimensions of the internal texture image, then scale the
       required number of pixels by the storage required for each pixel, based
       on  format and type.  Be sure to take the pixel storage parameters into
       account, especially GL_PACK_ALIGNMENT.


     */
}
