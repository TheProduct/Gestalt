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
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.material.texture.bitmap.IntegerBitmap;
import gestalt.util.JoglUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;


public class JoglMaterialPluginNonPowerOfTwoTexture
        extends TexturePlugin {

    private boolean _myNormalizeTextureCoordinates = true;

    public JoglMaterialPluginNonPowerOfTwoTexture(final boolean theHintFlipYAxis, boolean theNormalizeTextureCoordinates) {
        this(theHintFlipYAxis);
        _myNormalizeTextureCoordinates = theNormalizeTextureCoordinates;
    }

    public JoglMaterialPluginNonPowerOfTwoTexture(final boolean theHintFlipYAxis) {
        super(theHintFlipYAxis);
        setTextureTarget(GL.GL_TEXTURE_RECTANGLE_ARB);
        setWrapMode(TEXTURE_WRAPMODE_CLAMP_TO_BORDER);
    }

    public void initStatic(final GL gl) {
        super.initStatic(gl);
        testExtension(gl);
    }

    private void testExtension(final GL gl) {
        /* check extension */
        if (!JoglUtil.testExtensionAvailability(gl, "GL_ARB_texture_rectangle")) {
            System.err.println("### WARNING @ "
                    + JoglMaterialPluginNonPowerOfTwoTexture.class.getName()
                    + " / GL_ARB_texture_rectangle is not implemented. revert to standard texture plugin.");
        }
    }

    public void begin(GLContext theRenderContext, Material theParent) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        final boolean myWireframe;
        if (theParent == null) {
            myWireframe = false;
        } else {
            myWireframe = theParent.wireframe;
        }

        if (!myWireframe) {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());
        }


        /* update texture properties */
        update(gl, glu);

        /* handle wireframe OR texturematrix */
        if (myWireframe) {
            gl.glDisable(getTextureTarget());
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        } else {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPushMatrix();
            /** @todo do we need / want to load the identity matrix? -- gl.glLoadIdentity(); */
            gl.glTranslatef(position().x, position().y, 0);
            gl.glScalef(scale().x * (_myNormalizeTextureCoordinates ? getPixelWidth() : 1),
                        scale().y * (_myNormalizeTextureCoordinates ? getPixelHeight() : 1),
                        1);

            if (_myHintFlipYAxis) {
                gl.glTranslatef(0, -1, 0);
            }

            gl.glMatrixMode(GL.GL_MODELVIEW);
        }
    }

    protected void changeData(final GL gl, final GLU glu) {

        final int myWidth = _myBitmap.getWidth();
        final int myHeight = _myBitmap.getHeight();

        if (getFilterType() == TEXTURE_FILTERTYPE_LINEAR || getFilterType() == TEXTURE_FILTERTYPE_NEAREST) {
            if (_myBitmap instanceof ByteBitmap) {
                /** @todo JSR-231 performance hit? */
                ByteBuffer myBuffer = ByteBuffer.wrap((byte[])_myBitmap.getDataRef());
                gl.glTexImage2D(getTextureTarget(),
                                0,
                                GL.GL_RGBA, // components
                                myWidth,
                                myHeight,
                                0,
                                getFormat(_myBitmap.getComponentOrder()),
                                getOpenGLType(_myBitmap.getComponentOrder()),
                                myBuffer);
            } else if (_myBitmap instanceof IntegerBitmap) {
                /** @todo JSR-231 performance hit? */
                IntBuffer myBuffer = IntBuffer.wrap((int[])_myBitmap.getDataRef());
                gl.glTexImage2D(getTextureTarget(),
                                0,
                                GL.GL_RGBA, // components
                                myWidth,
                                myHeight,
                                0,
                                getFormat(_myBitmap.getComponentOrder()),
                                getOpenGLType(_myBitmap.getComponentOrder()),
                                myBuffer);
            } else if (_myBitmap instanceof ByteBufferBitmap) {
            }
        } else if (getFilterType() == TEXTURE_FILTERTYPE_MIPMAP) {
            if (_myBitmap instanceof ByteBitmap) {
                /** @todo JSR-231 performance hit? */
                ByteBuffer myBuffer = ByteBuffer.wrap((byte[])_myBitmap.getDataRef());
                glu.gluBuild2DMipmaps(getTextureTarget(),
                                      GL.GL_RGBA,
                                      myWidth,
                                      myHeight,
                                      getFormat(_myBitmap.getComponentOrder()),
                                      getOpenGLType(_myBitmap.getComponentOrder()),
                                      myBuffer);
            } else if (_myBitmap instanceof IntegerBitmap) {
                /** @todo JSR-231 performance hit? */
                IntBuffer myBuffer = IntBuffer.wrap((int[])_myBitmap.getDataRef());
                glu.gluBuild2DMipmaps(getTextureTarget(),
                                      GL.GL_RGBA,
                                      myWidth,
                                      myHeight,
                                      getFormat(_myBitmap.getComponentOrder()),
                                      getOpenGLType(_myBitmap.getComponentOrder()),
                                      myBuffer);
            } else if (_myBitmap instanceof ByteBufferBitmap) {
            }
        }
    }
}
