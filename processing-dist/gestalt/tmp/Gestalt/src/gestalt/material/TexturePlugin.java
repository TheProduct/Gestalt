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


package gestalt.material;

import gestalt.context.GLContext;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.TextureInfo;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.material.texture.bitmap.IntegerBitmap;
import gestalt.render.Disposable;
import gestalt.util.ImageUtil;
import gestalt.util.JoglUtil;

import mathematik.Vector2f;
import mathematik.Vector3f;

import com.sun.opengl.util.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;


public class TexturePlugin
        implements MaterialPlugin,
                   Disposable,
                   TextureInfo {

    protected final boolean _myHintFlipYAxis;

    protected boolean _myIsInitalized;

    protected boolean _myWrapModeChanged;

    protected boolean _myFilterTypeChanged;

    protected boolean _myBorderColorChanged;

    protected Bitmap _myScheduledBitmap;

    protected Bitmap _myBitmap;

    protected Color _myBorderColor;

    private Vector2f _myNPOTReScale;

    private Vector2f _myPosition;

    private Vector3f _myScale;

    private Vector3f _myRotation;

    private int _myWrapMode;

    private int _myFilterType;

    private int _myTextureTarget;

    private int _myTextureUnit;

    protected int _myOpenGLTextureID;

    public String name = "";

    /** @todo check out 'glTexEnv{if}{v}(GLenum target, GLenum pname, TYPEparam)' */
    protected static int _myMaxTextureSize;

    protected static boolean ourStaticInitalized = false;

    public TexturePlugin() {
        this(true);
    }

    public TexturePlugin(final boolean theHintFlipYAxis) {
        _myPosition = new Vector2f(0, 0);
        /* the -1 makes up for the different texture spaces in java
         * and opengl. by adapting opengl to a different space
         * we can save the 180" rotation of 'external' images.
         * this can be very handy espacially when using movie textures.
         * by using the texture matrix we can still operate with
         * regular opengl texture parameters.
         */
        _myHintFlipYAxis = theHintFlipYAxis;
        if (_myHintFlipYAxis) {
            _myScale = new Vector3f(1, -1, 1);
        } else {
            _myScale = new Vector3f(1, 1, 1);
        }

        _myNPOTReScale = new Vector2f(1, 1);

        _myRotation = new Vector3f();

        _myOpenGLTextureID = UNDEFINED;
        _myWrapMode = TEXTURE_WRAPMODE_REPEAT;
        _myFilterType = TEXTURE_FILTERTYPE_LINEAR;
        _myIsInitalized = false;
        _myBorderColor = new Color(0, 0, 0, 0);
        /* changed flags */
        _myWrapModeChanged = true;
        _myFilterTypeChanged = true;
        _myBorderColorChanged = true;

        _myOpenGLTextureID = UNDEFINED;
        setTextureTarget(GL.GL_TEXTURE_2D);
        setTextureUnit(GL.GL_TEXTURE0);
    }

    protected void init(final GL gl) {
        /* create opengl texture ID */
        int[] myIDs = new int[1];
        /** @todo JSR-231 performance hit! */
        gl.glGenTextures(myIDs.length, myIDs, 0);
        _myOpenGLTextureID = myIDs[0];
    }

    public void initStatic(final GL gl) {
        /* query maximum texture size */
        int[] myValue = new int[1];
        /** @todo JSR-231 performance hit! */
        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, myValue, 0);
        _myMaxTextureSize = myValue[0];

        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, myValue, 0);
//        System.out.println("### MAX_TEXTURE_UNITS: " + myValue[0]);

        ourStaticInitalized = true;
    }

    public void bind(GL gl) {
        gl.glBindTexture(getTextureTarget(), getTextureID());
    }

    public void unbind(GL gl) {
        gl.glBindTexture(getTextureTarget(), 0);
    }

    public void enable(GL gl) {
        gl.glEnable(getTextureTarget());
    }

    public void disable(GL gl) {
        gl.glDisable(getTextureTarget());
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

            if (rotation().x != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().x), 1, 0, 0);
            }
            if (rotation().y != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().y), 0, 1, 0);
            }
            if (rotation().z != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(rotation().z), 0, 0, 1);
            }

            gl.glTranslatef(position().x,
                            position().y,
                            0);

            gl.glScalef(scale().x * nonpoweroftwotexturerescale().x,
                        scale().y * nonpoweroftwotexturerescale().y,
                        scale().z);

            if (_myHintFlipYAxis) {
                gl.glTranslatef(0, -1, 0);
            }

            gl.glMatrixMode(GL.GL_MODELVIEW);
        }
        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".begin '" + this + "'.", true);
    }

    public void end(GLContext theRenderContext, Material theParent) {
        final GL gl = theRenderContext.gl;
        final boolean myWireframe;
        if (theParent == null) {
            myWireframe = false;
        } else {
            myWireframe = theParent.wireframe;
        }

        /* turn off texture again */
        if (!myWireframe) {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glDisable(getTextureTarget());
        }

        /* handle wireframe OR texturematrix */
        if (myWireframe) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        } else {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());

            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW);

            /* enable texture target */
            gl.glDisable(getTextureTarget());
        }

        final GLU glu = theRenderContext.glu;
        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".end '" + this + "'.", true);
    }

    public void update(final GL gl, final GLU glu) {
        /* initialize texture */
        if (!ourStaticInitalized) {
            initStatic(gl);
        }

        if (!_myIsInitalized) {
            _myIsInitalized = true;
            init(gl);
        }

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

        if (_myScheduledBitmap != null) {
            handleScheduledBitmap(gl, glu, _myScheduledBitmap);
        }

        if (_myBorderColorChanged) {
            updateBorderColor(gl);
            _myBorderColorChanged = false;
        }
    }

    protected void handleScheduledBitmap(GL gl, GLU glu, Bitmap theScheduledBitmap) {
        /* validate bitmap */
        if (theScheduledBitmap == null) {
            System.err.println("### WARNING @ " + getClass().getName() + " / bitmap is 'null'.");
            theScheduledBitmap = getErrorBitmap(ERROR_BITMAP_REFERANCE_NULL);
        } else if (!validateBitmapSize(theScheduledBitmap)) {
            theScheduledBitmap = getErrorBitmap(ERROR_EXCEEDED_SIZE);
        }

        /**
         * check whether data needs to be changed (glTexImage2D) or
         * data only needs to be updated (glTexSubImage2D).
         */
        if (_myBitmap == null) {
            /* first upload */
            setBitmapRef(theScheduledBitmap);
            setNPOTTextureScale();
            changeData(gl, glu);
        } else if (_myBitmap.getWidth() != theScheduledBitmap.getWidth()
                || _myBitmap.getHeight() != theScheduledBitmap.getHeight()) {
            /* size changed */
            setBitmapRef(theScheduledBitmap);
            setNPOTTextureScale();
            changeData(gl, glu);
        } else if (_myBitmap.getClass() != theScheduledBitmap.getClass()) {
            /* type changed */
            setBitmapRef(theScheduledBitmap);
            changeData(gl, glu);
        } else {
            /* just update */
            setBitmapRef(theScheduledBitmap);
            updateData(gl);
        }
        /* removed scheduled bitmap */
        _myScheduledBitmap = null;
    }

    protected void updateData(final GL gl) {
        /* void glTexSubImage2D( GLenum target,
         * GLint level,
         * GLint xoffset,
         * GLint yoffset,
         * GLsizei width,
         * GLsizei height,
         * GLenum format,
         * GLenum type,
         * const GLvoid *pixels )
         */

        /** @todo what about MIPMAPSs here. */
        final int x = 0;
        final int y = 0;
        final int myWidth = _myBitmap.getWidth();
        final int myHeight = _myBitmap.getHeight();

        if (_myBitmap instanceof ByteBitmap) {
            /** @todo JSR-231 performance hit? */
            final ByteBuffer myBuffer = ByteBuffer.wrap((byte[])_myBitmap.getDataRef());
            gl.glTexSubImage2D(getTextureTarget(),
                               0,
                               x,
                               y,
                               myWidth,
                               myHeight,
                               getFormat(_myBitmap.getComponentOrder()),
                               getOpenGLType(_myBitmap.getComponentOrder()),
                               myBuffer);
        } else if (_myBitmap instanceof IntegerBitmap) {
            /** @todo JSR-231 performance hit? */
            final IntBuffer myBuffer = IntBuffer.wrap((int[])_myBitmap.getDataRef());
            gl.glTexSubImage2D(getTextureTarget(),
                               0,
                               x,
                               y,
                               myWidth,
                               myHeight,
                               getFormat(_myBitmap.getComponentOrder()),
                               getOpenGLType(_myBitmap.getComponentOrder()),
                               myBuffer);
        } else if (_myBitmap instanceof ByteBufferBitmap) {
            gl.glTexSubImage2D(getTextureTarget(),
                               0,
                               x,
                               y,
                               myWidth,
                               myHeight,
                               getFormat(_myBitmap.getComponentOrder()),
                               getOpenGLType(_myBitmap.getComponentOrder()),
                               ((ByteBufferBitmap)_myBitmap).getByteBufferDataRef());
        }
    }

    protected void changeData(final GL gl, final GLU glu) {
        /* void glTexImage2D( GLenum target,
         * GLint level,
         * GLint internalFormat,
         * GLsizei width,
         * GLsizei height,
         * GLint border,
         * GLenum format,
         * GLenum type,
         * const GLvoid *pixels )
         */

        final int myPOTWidth = ImageUtil.getNextPowerOf2(_myBitmap.getWidth());
        final int myPOTHeight = ImageUtil.getNextPowerOf2(_myBitmap.getHeight());

        if (getFilterType() == TEXTURE_FILTERTYPE_LINEAR || getFilterType() == TEXTURE_FILTERTYPE_NEAREST) {
            if (_myBitmap instanceof ByteBitmap) {
                /** @todo JSR-231 performance hit? */
                gl.glTexImage2D(getTextureTarget(),
                                0,
                                GL.GL_RGBA, // components
                                myPOTWidth,
                                myPOTHeight,
                                0,
                                getFormat(_myBitmap.getComponentOrder()),
                                getOpenGLType(_myBitmap.getComponentOrder()),
                                BufferUtil.newByteBuffer(myPOTWidth * myPOTHeight
                        * ByteBitmap.NUMBER_OF_PIXEL_COMPONENTS));
            } else if (_myBitmap instanceof IntegerBitmap) {
                /** @todo JSR-231 performance hit? */
                gl.glTexImage2D(getTextureTarget(),
                                0,
                                GL.GL_RGBA, // components
                                myPOTWidth,
                                myPOTHeight,
                                0,
                                getFormat(_myBitmap.getComponentOrder()),
                                getOpenGLType(_myBitmap.getComponentOrder()),
                                BufferUtil.newIntBuffer(myPOTWidth * myPOTHeight));
            } else if (_myBitmap instanceof ByteBufferBitmap) {
                /** @todo is it ok to clear this with an empty texture. */
                /** @todo JSR-231 performance hit? */
                gl.glTexImage2D(getTextureTarget(),
                                0,
                                GL.GL_RGBA, // components
                                myPOTWidth,
                                myPOTHeight,
                                0,
                                getFormat(_myBitmap.getComponentOrder()),
                                getOpenGLType(_myBitmap.getComponentOrder()),
                                BufferUtil.newByteBuffer(myPOTWidth * myPOTHeight * 4));
            }
        } else if (getFilterType() == TEXTURE_FILTERTYPE_MIPMAP) {

            if (ImageUtil.getNextPowerOf2(_myBitmap.getWidth()) != _myBitmap.getWidth()
                    || ImageUtil.getNextPowerOf2(_myBitmap.getHeight()) != _myBitmap.getHeight()) {
                System.err.println("### ERROR @"
                        + getClass().getName()
                        + " / filter-type MIPMAP texture-size should be power of two. for example ("
                        + ImageUtil.getNextPowerOf2(_myBitmap.getWidth())
                        + "; "
                        + ImageUtil.getNextPowerOf2(_myBitmap.getHeight())
                        + ").");
            }

            if (_myBitmap instanceof ByteBitmap) {
                /** @todo JSR-231 performance hit? */
                ByteBuffer myBuffer = ByteBuffer.wrap((byte[])_myBitmap.getDataRef());
                glu.gluBuild2DMipmaps(getTextureTarget(),
                                      GL.GL_RGBA,
                                      myPOTWidth,
                                      myPOTHeight,
                                      getFormat(_myBitmap.getComponentOrder()),
                                      getOpenGLType(_myBitmap.getComponentOrder()),
                                      myBuffer);
            } else if (_myBitmap instanceof IntegerBitmap) {
                /** @todo JSR-231 performance hit? */
                IntBuffer myBuffer = IntBuffer.wrap((int[])_myBitmap.getDataRef());
                glu.gluBuild2DMipmaps(getTextureTarget(),
                                      GL.GL_RGBA,
                                      myPOTWidth,
                                      myPOTHeight,
                                      getFormat(_myBitmap.getComponentOrder()),
                                      getOpenGLType(_myBitmap.getComponentOrder()),
                                      myBuffer);
            } else if (_myBitmap instanceof ByteBufferBitmap) {
                glu.gluBuild2DMipmaps(getTextureTarget(),
                                      GL.GL_RGBA,
                                      myPOTWidth,
                                      myPOTHeight,
                                      getFormat(_myBitmap.getComponentOrder()),
                                      getOpenGLType(_myBitmap.getComponentOrder()),
                                      ((ByteBufferBitmap)_myBitmap).getByteBufferDataRef());
            }
        }

        /* upload real data */
        updateData(gl);
    }

    protected void updateWrapMode(final GL gl) {
        switch (getWrapMode()) {
            case TEXTURE_WRAPMODE_CLAMP:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP);
                break;
            case TEXTURE_WRAPMODE_REPEAT:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_REPEAT);
                break;
            case TEXTURE_WRAPMODE_CLAMP_TO_BORDER:
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_BORDER);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_BORDER);
                gl.glTexParameterf(getTextureTarget(), GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_BORDER);
                break;
        }
    }

    protected void updateFilterType(final GL gl) {
        switch (getFilterType()) {
            case TEXTURE_FILTERTYPE_LINEAR:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                break;
            case TEXTURE_FILTERTYPE_MIPMAP:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
                break;
            case TEXTURE_FILTERTYPE_NEAREST:
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                gl.glTexParameteri(getTextureTarget(), GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
                break;
        }
    }

    protected void updateBorderColor(final GL gl) {
        gl.glTexParameterfv(getTextureTarget(), GL.GL_TEXTURE_BORDER_COLOR,
                            FloatBuffer.wrap(new float[] {_myBorderColor.r,
                                                          _myBorderColor.g,
                                                          _myBorderColor.b,
                                                          _myBorderColor.a}));
    }

    public static int getOpenGLType(final int theType) {
        final int myGLType;

        if (theType == BITMAP_COMPONENT_ORDER_BGRA && MACOSX) {
            myGLType = GL.GL_UNSIGNED_INT_8_8_8_8_REV;
        } else {
            myGLType = GL.GL_UNSIGNED_BYTE;
        }
        return myGLType;
    }

    public int getFormat(int theType) {
        int myGLFormat;
        if (theType == BITMAP_COMPONENT_ORDER_BGRA) {
            myGLFormat = GL.GL_BGRA;
        } else {
            myGLFormat = GL.GL_RGBA;
        }
        return myGLFormat;
    }

    public int getMaxTextureSize() {
        if (!ourStaticInitalized) {
            System.err.println("### WARNING @ " + getClass().getName()
                    + " / can t validate bitmap size. opengl has not been initalized.");
        }
        return _myMaxTextureSize;
    }

    public String getTextureTargetAsString() {
        int myTarget = getTextureTarget();
        switch (myTarget) {
            case GL.GL_TEXTURE_RECTANGLE_ARB:
                return "TEXTURE_RECTANGLE";
            case GL.GL_TEXTURE_2D:
                return "TEXTURE_2D";
            case GL.GL_TEXTURE_3D:
                return "TEXTURE_3D";
            case GL.GL_TEXTURE_CUBE_MAP:
                return "TEXTURE_CUBE_MAP";
        }
        return "?(" + myTarget + ")";
    }

    public void dispose(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        gl.glDeleteTextures(1, new int[] {getTextureID()}, 0);
    }

    public String toString() {
        return "('" + name + "', " + getPixelWidth() + ", " + getPixelHeight() + ")";
    }

    public final boolean hint_flip_axis() {
        return _myHintFlipYAxis;
    }

    public final boolean isInitialized() {
        return _myIsInitalized;
    }

    public final Vector2f position() {
        _myBorderColorChanged = true;
        return _myPosition;
    }

    public Color bordercolor() {
        return _myBorderColor;
    }

    public int getPixelWidth() {
        /** @todo
         * this is make do.
         * if the bitmap turns out to be invalid these values might change
         * unnotified.
         */
        if (_myScheduledBitmap != null) {
            return _myScheduledBitmap.getWidth();
        } else if (_myBitmap != null) {
            return _myBitmap.getWidth();
        }
        return 1;
    }

    public int getPixelHeight() {
        /** @todo
         * this is make do.
         * if the bitmap turns out to be invalid these values might change
         * unnotified.
         */
        if (_myScheduledBitmap != null) {
            return _myScheduledBitmap.getHeight();
        } else if (_myBitmap != null) {
            return _myBitmap.getHeight();
        }
        return 1;
    }

    public final Vector3f scale() {
        return _myScale;
    }

    public final Vector3f rotation() {
        return _myRotation;
    }

    public final Vector2f nonpoweroftwotexturerescale() {
        return _myNPOTReScale;
    }

    public final void setTextureTarget(int theTextureTarget) {
        _myTextureTarget = theTextureTarget;
    }

    public int getTextureTarget() {
        return _myTextureTarget;
    }

    public final void setTextureUnit(int theTextureUnit) {
        _myTextureUnit = theTextureUnit;
    }

    public final int getTextureUnit() {
        return _myTextureUnit;
    }

    public final int getFilterType() {
        return _myFilterType;
    }

    /**
     * @deprecated
     * @return
     */
    public int getOpenGLTextureID() {
        return getTextureID();
    }

    /**
     * @deprecated
     * @param theOpenGLTextureID
     */
    public void setOpenGLTextureID(int theOpenGLTextureID) {
        setTextureID(theOpenGLTextureID);
    }

    public int getTextureID() {
        return _myOpenGLTextureID;
    }

    public void setTextureID(int theOpenGLTextureID) {
        _myOpenGLTextureID = theOpenGLTextureID;
    }

    public final int getWrapMode() {
        return _myWrapMode;
    }

//    public abstract int getMaxTextureSize();
    public boolean validateBitmapSize(final Bitmap theBitmap) {
        if (theBitmap.getWidth() > getMaxTextureSize() || theBitmap.getHeight() > getMaxTextureSize()) {
            System.err.println("### WARNING @ " + getClass().getName()
                    + " / texture size ("
                    + theBitmap.getWidth() + ", " + theBitmap.getHeight()
                    + ") exceeds maximum size of "
                    + getMaxTextureSize() + ".");
            return false;
        } else if (theBitmap.getWidth() <= 0 || theBitmap.getHeight() <= 0) {
            System.err.println("### WARNING @ " + getClass().getName()
                    + " / texture size is too small.");
        }
        return true;
    }

    public void setWrapMode(final int theWrapMode) {
        if (theWrapMode == _myWrapMode) {
            return;
        }
        _myWrapMode = theWrapMode;
        _myWrapModeChanged = true;
    }

    public void setFilterType(final int theFilterType) {
        if (theFilterType == _myFilterType) {
            return;
        }

        if (_myIsInitalized && theFilterType == TEXTURE_FILTERTYPE_MIPMAP) {
            System.err.println("### WARNING @ " + getClass().getName()
                    + " / currently MIPMAP only works if set in the beginning.");
        }

        _myFilterType = theFilterType;
        _myFilterTypeChanged = true;
    }

    public void load(final Bitmap theBitmap) {
        /* schedule for upload */
        _myScheduledBitmap = theBitmap;
    }

    public void reload() {
        /** @todo check integrity of referenced bitmap */
        if (_myBitmap == null) {
            return;
        }
        _myScheduledBitmap = _myBitmap;
    }

    protected void setNPOTTextureScale() {
        final int myWidth = _myBitmap.getWidth();
        final int myHeight = _myBitmap.getHeight();
        final float myPowerOf2Width = ImageUtil.getNextPowerOf2(myWidth);
        final float myPowerOf2Height = ImageUtil.getNextPowerOf2(myHeight);
        if (myWidth == myPowerOf2Width) {
            _myNPOTReScale.x = 1f;
        } else {
            _myNPOTReScale.x = myWidth / myPowerOf2Width;
        }
        if (myHeight == myPowerOf2Height) {
            _myNPOTReScale.y = 1f;
        } else {
            _myNPOTReScale.y = myHeight / myPowerOf2Height;
        }
    }

    public static final int ERROR_EXCEEDED_SIZE = 0;

    public static final int ERROR_BITMAP_REFERANCE_NULL = 1;

    protected Bitmap getErrorBitmap(int theErrorColor) {

        /** @todo parse theErrorColor */
        byte _one = (byte)255;
        byte zero = (byte)0;
        ByteBitmap myBitmap = null;
        byte[] myPixels = null;

        switch (theErrorColor) {
            case ERROR_EXCEEDED_SIZE:
                myPixels = new byte[] {
                    _one, zero, zero, _one, _one, _one, zero, _one,
                    _one, _one, zero, _one, _one, zero, zero, _one};
                myBitmap = new ByteBitmap(myPixels, 2, 2, BITMAP_COMPONENT_ORDER_RGBA);
                break;
            case ERROR_BITMAP_REFERANCE_NULL:
                myPixels = new byte[] {
                    zero, zero, _one, _one, _one, zero, _one, _one,
                    _one, zero, _one, _one, zero, zero, _one, _one};
                myBitmap = new ByteBitmap(myPixels, 2, 2, BITMAP_COMPONENT_ORDER_RGBA);
                break;
        }
        return myBitmap;
    }

    public Bitmap bitmap() {
        if (_myScheduledBitmap != null) {
            return _myScheduledBitmap;
        } else {
            return _myBitmap;
        }
    }

    public void setBitmapRef(final Bitmap theBitmap) {
        if (!(theBitmap instanceof ByteBitmap)
                && !(theBitmap instanceof IntegerBitmap)
                && !(theBitmap instanceof ByteBufferBitmap)) {
            System.err.println("### WARNING @ " + getClass().getName()
                    + " / bitmap is of unsupported type.");
        }
        _myBitmap = theBitmap;
    }
}


/*
 * @TODO expose 'glPixelTransfer' bias and scale.
 *
 * GLPIXELTRANSFER
 *
 * NAME
 * glPixelTransferf, glPixelTransferi - set pixel transfer modes
 *
 * C SPECIFICATION
 * void glPixelTransferf( GLenum pname,
 * GLfloat param )
 * void glPixelTransferi( GLenum pname,
 * GLint param )
 *
 * PARAMETERS
 * pname Specifies the symbolic name of the pixel transfer parameter to
 * be set. Must be one of the following: GL_MAP_COLOR,
 * GL_MAP_STENCIL, GL_INDEX_SHIFT, GL_INDEX_OFFSET, GL_RED_SCALE,
 * GL_RED_BIAS, GL_GREEN_SCALE, GL_GREEN_BIAS, GL_BLUE_SCALE,
 * GL_BLUE_BIAS, GL_ALPHA_SCALE, GL_ALPHA_BIAS, GL_DEPTH_SCALE, or
 * GL_DEPTH_BIAS.
 *
 * Additionally, if the GL_ARB_imaging extension is supported, the
 * following symbolic names are accepted:
 * GL_POST_COLOR_MATRIX_RED_SCALE,
 * GL_POST_COLOR_MATRIX_GREEN_SCALE,
 * GL_POST_COLOR_MATRIX_BLUE_SCALE,
 * GL_POST_COLOR_MATRIX_ALPHA_SCALE, GL_POST_COLOR_MATRIX_RED_BIAS,
 * GL_POST_COLOR_MATRIX_GREEN_BIAS, GL_POST_COLOR_MATRIX_BLUE_BIAS,
 * GL_POST_COLOR_MATRIX_ALPHA_BIAS, GL_POST_CONVOLUTION_RED_SCALE,
 * GL_POST_CONVOLUTION_GREEN_SCALE, GL_POST_CONVOLUTION_BLUE_SCALE,
 * GL_POST_CONVOLUTION_ALPHA_SCALE, GL_POST_CONVOLUTION_RED_BIAS,
 * GL_POST_CONVOLUTION_GREEN_BIAS, GL_POST_CONVOLUTION_BLUE_BIAS,
 * and GL_POST_CONVOLUTION_ALPHA_BIAS.
 *
 * param Specifies the value that pname is set to.
 *
 * DESCRIPTION
 * glPixelTransfer sets pixel transfer modes that affect the operation of
 * subsequent glCopyPixels, glCopyTexImage1D, glCopyTexImage2D,
 * glCopyTexSubImage1D, glCopyTexSubImage2D, glCopyTexSubImage3D,
 * glDrawPixels, glReadPixels, glTexImage1D, glTexImage2D, glTexImage3D,
 * glTexSubImage1D, glTexSubImage2D, and glTexSubImage3D commands. Addi-
 * tionally, if the GL_ARB_imaging subset is supported, the routines
 * glColorTable, glColorSubTable, glConvolutionFilter1D,
 * glConvolutionFilter2D, glHistogram, glMinmax, and glSeparableFilter2D
 * are also affected. The algorithms that are specified by pixel transfer
 * modes operate on pixels after they are read from the frame buffer (-
 * glCopyPixels glCopyTexImage1D, glCopyTexImage2D, glCopyTexSubImage1D,
 * glCopyTexSubImage2D, glCopyTexSubImage3D, and glReadPixels), or
 * unpacked from client memory (glDrawPixels, glTexImage1D, glTexImage2D,
 * glTexImage3D, glTexSubImage1D, glTexSubImage2D, and glTexSubImage3D).
 * Pixel transfer operations happen in the same order, and in the same
 * manner, regardless of the command that resulted in the pixel operation.
 * Pixel storage modes (see glPixelStore) control the unpacking of pixels
 * being read from client memory, and the packing of pixels being written
 * back into client memory.
 *
 * Pixel transfer operations handle four fundamental pixel types: color,
 * color index, depth, and stencil. Color pixels consist of four float-
 * ing-point values with unspecified mantissa and exponent sizes, scaled
 * such that 0 represents zero intensity and 1 represents full intensity.
 * Color indices comprise a single fixed-point value, with unspecified
 * precision to the right of the binary point. Depth pixels comprise a
 * single floating-point value, with unspecified mantissa and exponent
 * sizes, scaled such that 0.0 represents the minimum depth buffer value,
 * and 1.0 represents the maximum depth buffer value. Finally, stencil
 * pixels comprise a single fixed-point value, with unspecified precision
 * to the right of the binary point.
 *
 * The pixel transfer operations performed on the four basic pixel types
 * are as follows:
 *
 * Color Each of the four color components is multiplied by a scale
 * factor, then added to a bias factor. That is, the red com-
 * ponent is multiplied by GL_RED_SCALE, then added to
 * GL_RED_BIAS; the green component is multiplied by
 * GL_GREEN_SCALE, then added to GL_GREEN_BIAS; the blue com-
 * ponent is multiplied by GL_BLUE_SCALE, then added to
 * GL_BLUE_BIAS; and the alpha component is multiplied by
 * GL_ALPHA_SCALE, then added to GL_ALPHA_BIAS. After all
 * four color components are scaled and biased, each is
 * clamped to the range [0,1]. All color, scale, and bias
 * values are specified with glPixelTransfer.
 *
 * If GL_MAP_COLOR is true, each color component is scaled by
 * the size of the corresponding color-to-color map, then
 * replaced by the contents of that map indexed by the scaled
 * component. That is, the red component is scaled by
 * GL_PIXEL_MAP_R_TO_R_SIZE, then replaced by the contents of
 * GL_PIXEL_MAP_R_TO_R indexed by itself. The green component
 * is scaled by GL_PIXEL_MAP_G_TO_G_SIZE, then replaced by the
 * contents of GL_PIXEL_MAP_G_TO_G indexed by itself. The
 * blue component is scaled by GL_PIXEL_MAP_B_TO_B_SIZE, then
 * replaced by the contents of GL_PIXEL_MAP_B_TO_B indexed by
 * itself. And the alpha component is scaled by
 * GL_PIXEL_MAP_A_TO_A_SIZE, then replaced by the contents of
 * GL_PIXEL_MAP_A_TO_A indexed by itself. All components
 * taken from the maps are then clamped to the range [0,1].
 * GL_MAP_COLOR is specified with glPixelTransfer. The con-
 * tents of the various maps are specified with glPixelMap.
 *
 * If the GL_ARB_imaging extension is supported, each of the
 * four color components may be scaled and biased after tran-
 * formation by the color matrix. That is, the red component
 * is multiplied by GL_POST_COLOR_MATRIX_RED_SCALE, then added
 * to GL_POST_COLOR_MATRIX_RED_BIAS; the green component is
 * multiplied by GL_POST_COLOR_MATRIX_GREEN_SCALE, then added
 * to GL_POST_COLOR_MATRIX_GREEN_BIAS; the blue component is
 * multiplied by GL_POST_COLOR_MATRIX_BLUE_SCALE, then added
 * to GL_POST_COLOR_MATRIX_BLUE_BIAS; and the alpha component
 * is multiplied by GL_POST_COLOR_MATRIX_ALPHA_SCALE, then
 * added to GL_POST_COLOR_MATRIX_ALPHA_BIAS. After all four
 * color components are scaled and biased, each is clamped to
 * the range [0,1].
 *
 * Similiarly, if the GL_ARB_imaging extension is supported,
 * each of the four color components may be scaled and biased
 * after processing by the enabled convolution filter. That
 * is, the red component is multiplied by
 * GL_POST_CONVOLUTION_RED_SCALE, then added to
 * GL_POST_CONVOLUTION_RED_BIAS; the green component is multi-
 * plied by GL_POST_CONVOLUTION_GREEN_SCALE, then added to
 * GL_POST_CONVOLUTION_GREEN_BIAS; the blue component is mul-
 * tiplied by GL_POST_CONVOLUTION_BLUE_SCALE, then added to
 * GL_POST_CONVOLUTION_BLUE_BIAS; and the alpha component is
 * multiplied by GL_POST_CONVOLUTION_ALPHA_SCALE, then added
 * to GL_POST_CONVOLUTION_ALPHA_BIAS. After all four color
 * components are scaled and biased, each is clamped to the
 * range [0,1].
 *
 * Color index Each color index is shifted left by GL_INDEX_SHIFT bits;
 * any bits beyond the number of fraction bits carried by the
 * fixed-point index are filled with zeros. If GL_INDEX_SHIFT
 * is negative, the shift is to the right, again zero filled.
 * Then GL_INDEX_OFFSET is added to the index. GL_INDEX_SHIFT
 * and GL_INDEX_OFFSET are specified with
 * glPixelTransfer.
 *
 * From this point, operation diverges depending on the
 * required of the resulting pixels. If the resulting pixels
 * are to be written to a color index buffer, or if they are
 * being read back to client memory in GL_COLOR_INDEX , the
 * pixels continue to be treated as indices. If GL_MAP_COLOR
 * is true, each index is masked by (2^n)-1, where n is
 * GL_PIXEL_MAP_I_TO_I_SIZE, then replaced by the contents of
 * GL_PIXEL_MAP_I_TO_I indexed by the masked value.
 * GL_MAP_COLOR is specified with glPixelTransfer. The con-
 * tents of the index map is specified with glPixelMap.
 *
 * If the resulting pixels are to be written to an RGBA color
 * buffer, or if they are read back to client memory in a
 * other than GL_COLOR_INDEX, the pixels are converted from
 * indices to colors by referencing the four maps
 * GL_PIXEL_MAP_I_TO_R, GL_PIXEL_MAP_I_TO_G,
 * GL_PIXEL_MAP_I_TO_B, and GL_PIXEL_MAP_I_TO_A. Before being
 * dereferenced, the index is masked by (2^n)-1, where n is
 * GL_PIXEL_MAP_I_TO_R_SIZE for the red map,
 * GL_PIXEL_MAP_I_TO_G_SIZE for the green map,
 * GL_PIXEL_MAP_I_TO_B_SIZE for the blue map, and
 * GL_PIXEL_MAP_I_TO_A_SIZE for the alpha map. All components
 * taken from the maps are then clamped to the range [0,1].
 * The contents of the four maps is specified with glPixelMap.
 *
 * Depth Each depth value is multiplied by GL_DEPTH_SCALE, added to
 * GL_DEPTH_BIAS, then clamped to the range [0,1].
 *
 * Stencil Each index is shifted GL_INDEX_SHIFT bits just as a color
 * index is, then added to GL_INDEX_OFFSET. If GL_MAP_STENCIL
 * is true, each index is masked by (2^n)-1, where n is
 * GL_PIXEL_MAP_S_TO_S_SIZE, then replaced by the contents of
 * GL_PIXEL_MAP_S_TO_S indexed by the masked value.
 *
 * The following table gives the type, initial value, and range of valid
 * values for each of the pixel transfer parameters that are set with
 * glPixelTransfer.
 *
 * pname (Type, Initial Value, Valid Range)
 * GL_MAP_COLOR (boolean, false, true/false)
 * GL_MAP_STENCIL (boolean, false, true/false)
 * GL_INDEX_SHIFT (integer, 0, -inf/inf)
 * GL_INDEX_OFFSET (integer, 0, -inf/inf)
 * GL_RED_SCALE (float, 1, -inf/inf)
 * GL_GREEN_SCALE (float, 1, -inf/inf)
 * GL_BLUE_SCALE (float, 1, -inf/inf)
 * GL_ALPHA_SCALE (float, 1, -inf/inf)
 * GL_DEPTH_SCALE (float, 1, -inf/inf)
 * GL_RED_BIAS (float, 0, -inf/inf)
 * GL_GREEN_BIAS (float, 0, -inf/inf)
 * GL_BLUE_BIAS (float, 0, -inf/inf)
 * GL_ALPHA_BIAS (float, 0, -inf/inf)
 * GL_DEPTH_BIAS (float, 0, -inf/inf)
 * GL_POST_COLOR_MATRIX_RED_SCALE (float, 1, -inf/inf)
 * GL_POST_COLOR_MATRIX_GREEN_SCALE (float, 1, -inf/inf)
 * GL_POST_COLOR_MATRIX_BLUE_SCALE (float, 1, -inf/inf)
 * GL_POST_COLOR_MATRIX_ALPHA_SCALE (float, 1, -inf/inf)
 * GL_POST_COLOR_MATRIX_RED_BIAS (float, 0, -inf/inf)
 * GL_POST_COLOR_MATRIX_GREEN_BIAS (float, 0, -inf/inf)
 * GL_POST_COLOR_MATRIX_BLUE_BIAS (float, 0, -inf/inf)
 * GL_POST_COLOR_MATRIX_ALPHA_BIAS (float, 0, -inf/inf)
 * GL_POST_CONVOLUTION_RED_SCALE (float, 1, -inf/inf)
 * GL_POST_CONVOLUTION_GREEN_SCALE (float, 1, -inf/inf)
 * GL_POST_CONVOLUTION_BLUE_SCALE (float, 1, -inf/inf)
 * GL_POST_CONVOLUTION_ALPHA_SCALE (float, 1, -inf/inf)
 * GL_POST_CONVOLUTION_RED_BIAS (float, 0, -inf/inf)
 * GL_POST_CONVOLUTION_GREEN_BIAS (float, 0, -inf/inf)
 * GL_POST_CONVOLUTION_BLUE_BIAS (float, 0, -inf/inf)
 * GL_POST_CONVOLUTION_ALPHA_BIAS (float, 0, -inf/inf)
 *
 * glPixelTransferf can be used to set any pixel transfer parameter. If
 * the parameter type is boolean, 0 implies false and any other value
 * implies true. If pname is an integer parameter, param is rounded to
 * the nearest integer.
 *
 * Likewise, glPixelTransferi can be used to set any of the pixel transfer
 * parameters. Boolean parameters are set to false if param is 0 and to
 * true otherwise. param is converted to floating point before being
 * assigned to real-valued parameters.
 *
 * NOTES
 * If a glColorTable, glColorSubTable, glConvolutionFilter1D,
 * glConvolutionFilter2D, glCopyPixels, glCopyTexImage1D,
 * glCopyTexImage2D, glCopyTexSubImage1D, glCopyTexSubImage2D,
 * glCopyTexSubImage3D, glDrawPixels, glReadPixels, glSeparableFilter2D,
 * glTexImage1D, glTexImage2D, glTexImage3D, glTexSubImage1D,
 * glTexSubImage2D, or glTexSubImage3D. command is placed in a display
 * list (see glNewList and glCallList), the pixel transfer mode settings
 * in effect when the display list is executed are the ones that are used.
 * They may be different from the settings when the command was compiled
 * into the display list.
 *
 * ERRORS
 * GL_INVALID_ENUM is generated if pname is not an accepted value.
 *
 * GL_INVALID_OPERATION is generated if glPixelTransfer is executed
 * between the execution of glBegin and the corresponding execution of
 * glEnd.
 *
 * ASSOCIATED GETS
 * glGet with argument GL_MAP_COLOR
 * glGet with argument GL_MAP_STENCIL
 * glGet with argument GL_INDEX_SHIFT
 * glGet with argument GL_INDEX_OFFSET
 * glGet with argument GL_RED_SCALE
 * glGet with argument GL_RED_BIAS
 * glGet with argument GL_GREEN_SCALE
 * glGet with argument GL_GREEN_BIAS
 * glGet with argument GL_BLUE_SCALE
 * glGet with argument GL_BLUE_BIAS
 * glGet with argument GL_ALPHA_SCALE
 * glGet with argument GL_ALPHA_BIAS
 * glGet with argument GL_DEPTH_SCALE
 * glGet with argument GL_DEPTH_BIAS
 * glGet with argument GL_POST_COLOR_MATRIX_RED_SCALE
 * glGet with argument GL_POST_COLOR_MATRIX_RED_BIAS
 * glGet with argument GL_POST_COLOR_MATRIX_GREEN_SCALE
 * glGet with argument GL_POST_COLOR_MATRIX_GREEN_BIAS
 * glGet with argument GL_POST_COLOR_MATRIX_BLUE_SCALE
 * glGet with argument GL_POST_COLOR_MATRIX_BLUE_BIAS
 * glGet with argument GL_POST_COLOR_MATRIX_ALPHA_SCALE
 * glGet with argument GL_POST_COLOR_MATRIX_ALPHA_BIAS
 * glGet with argument GL_POST_CONVOLUTION_RED_SCALE
 * glGet with argument GL_POST_CONVOLUTION_RED_BIAS
 * glGet with argument GL_POST_CONVOLUTION_GREEN_SCALE
 * glGet with argument GL_POST_CONVOLUTION_GREEN_BIAS
 * glGet with argument GL_POST_CONVOLUTION_BLUE_SCALE
 * glGet with argument GL_POST_CONVOLUTION_BLUE_BIAS
 * glGet with argument GL_POST_CONVOLUTION_ALPHA_SCALE
 * glGet with argument GL_POST_CONVOLUTION_ALPHA_BIAS
 *
 * SEE ALSO
 * glCallList, glColorTable, glColorSubTable, glConvolutionFilter1D,
 * glConvolutionFilter2D, glCopyPixels, glCopyTexImage1D,
 * glCopyTexImage2D, glCopyTexSubImage1D, glCopyTexSubImage2D,
 * glCopyTexSubImage3D, glDrawPixels, glNewList, glPixelMap, glPixelStore,
 * glPixelZoom, glReadPixels, glTexImage1D, glTexImage2D, glTexImage3D,
 * glTexSubImage1D, glTexSubImage2D, glTexSubImage3D
 *
 */