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


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;


public class JoglMaterialPluginStipplePolygon
    implements MaterialPlugin {

    /*
           NAME
              glPolygonStipple - set the polygon stippling pattern

       C SPECIFICATION
              void glPolygonStipple( const GLubyte *mask )

       PARAMETERS
              mask  Specifies  a  pointer  to  a  32x32  stipple pattern that will be
                    unpacked from memory in the same way  that  glDrawPixels  unpacks
                    pixels.

       DESCRIPTION
              Polygon  stippling,  like line stippling (see glLineStipple), masks out
              certain fragments produced by rasterization, creating a pattern.  Stip-
              pling is independent of polygon antialiasing.

              mask  is  a pointer to a 32x32 stipple pattern that is stored in memory
              just like the pixel data supplied to a glDrawPixels  call  with  height
              and width both equal to 32, a pixel of GL_COLOR_INDEX, and data type of
              GL_BITMAP.  That is, the stipple pattern  is  represented  as  a  32x32
              array  of  1-bit  color indices packed in unsigned bytes.  glPixelStore
              parameters like GL_UNPACK_SWAP_BYTES and GL_UNPACK_LSB_FIRST affect the
              assembling  of  the bits into a stipple pattern.  Pixel transfer opera-
              tions (shift, offset, pixel map) are not applied to the stipple  image,
              however.

              To  enable  and  disable polygon stippling, call glEnable and glDisable
              with argument GL_POLYGON_STIPPLE. Polygon stippling is  initially  dis-
              abled. If it's enabled, a rasterized polygon fragment with window coor-
              dinates xw and yw is sent to the next stage of the GL if  and  only  if
              the  (xwmod32)th bit in the (ywmod32)th row of the stipple pattern is 1
              (one).  When polygon stippling is disabled, it is  as  if  the  stipple
              pattern consists of all 1's.

       ERRORS
              GL_INVALID_OPERATION  is  generated  if  glPolygonStipple  is  executed
              between the execution of glBegin and  the  corresponding  execution  of
              glEnd.

       ASSOCIATED GETS
              glGetPolygonStipple
              glIsEnabled with argument GL_POLYGON_STIPPLE

       SEE ALSO
              glDrawPixels(3G),          glLineStipple(3G),         glPixelStore(3G),
              glPixelTransfer(3G)


     */

    public byte[] stipplepattern = new byte[] {
                                   0x00, 0x00, 0x00, 0x00,
                                   0x00, 0x00, 0x00, 0x00,
                                   0x03, (byte) 0x80, 0x01, (byte) 0xC0,
                                   0x06, (byte) 0xC0, 0x03, 0x60,
                                   0x04, 0x60, 0x06, 0x20,
                                   0x04, 0x30, 0x0C, 0x20,
                                   0x04, 0x18, 0x18, 0x20,
                                   0x04, 0x0C, 0x30, 0x20,
                                   0x04, 0x06, 0x60, 0x20,
                                   0x44, 0x03, (byte) 0xC0, 0x22,
                                   0x44, 0x01, (byte) 0x80, 0x22,
                                   0x44, 0x01, (byte) 0x80, 0x22,
                                   0x44, 0x01, (byte) 0x80, 0x22,
                                   0x44, 0x01, (byte) 0x80, 0x22,
                                   0x44, 0x01, (byte) 0x80, 0x22,
                                   0x44, 0x01, (byte) 0x80, 0x22,
                                   0x66, 0x01, (byte) 0x80, 0x66,
                                   0x33, 0x01, (byte) 0x80, (byte) 0xCC,
                                   0x19, (byte) 0x81, (byte) 0x81, (byte) 0x98,
                                   0x0C, (byte) 0xC1, (byte) 0x83, 0x30,
                                   0x07, (byte) 0xe1, (byte) 0x87, (byte) 0xe0,
                                   0x03, 0x3f, (byte) 0xfc, (byte) 0xc0,
                                   0x03, 0x31, (byte) 0x8c, (byte) 0xc0,
                                   0x03, 0x33, (byte) 0xcc, (byte) 0xc0,
                                   0x06, 0x64, 0x26, 0x60,
                                   0x0c, (byte) 0xcc, 0x33, 0x30,
                                   0x18, (byte) 0xCC, 0x33, 0x18,
                                   0x10, (byte) 0xC4, 0x23, 0x08,
                                   0x10, 0x63, (byte) 0xC6, 0x08,
                                   0x10, 0x30, 0x0c, 0x08,
                                   0x10, 0x18, 0x18, 0x08,
                                   0x10, 0x00, 0x00, 0x08};

    public void begin(GLContext theRenderContext, Material theParent) {
        final GL gl = (  theRenderContext).gl;

        gl.glEnable(GL.GL_POLYGON_STIPPLE);
        /** @todo JSR-231 -- added 0 */
        gl.glPolygonStipple(stipplepattern, 0);
    }


    public void end(GLContext theRenderContext, Material theParent) {
        final GL gl = (  theRenderContext).gl;
        gl.glDisable(GL.GL_POLYGON_STIPPLE);
    }
}
