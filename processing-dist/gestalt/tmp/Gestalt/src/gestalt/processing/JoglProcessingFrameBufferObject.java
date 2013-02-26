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

package gestalt.processing;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.extension.framebufferobject.BufferInfo;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.render.Drawable;
import gestalt.shape.AbstractDrawable;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.bitmap.ByteBufferBitmap;

import processing.core.PApplet;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;


/**
 * @deprecated this class seems to contain errors...
 */
public class JoglProcessingFrameBufferObject
    extends TexturePlugin {

    private boolean _myIsInitalized = false;

    private final BufferInfo _myBufferID;

    private final int _myTextureWidth;

    private final int _myTextureHeight;

    private final boolean _myIsActive = true;

    private JoglTexCreator _myTexCreator;

    private final PApplet _myParent;

    private static int _myMaxTextureSize;

    private static boolean ourStaticInitalized = false;

    public int getMaxTextureSize() {
        if (!ourStaticInitalized) {
            System.err.println("### WARNING @ " + getClass().getName() +
                               " / can t validate bitmap size. opengl has not been initalized.");
        }
        return _myMaxTextureSize;
    }


    public JoglProcessingFrameBufferObject(final int theWidth,
                                           final int theHeight,
                                           final JoglTexCreator theTexGenerator,
                                           final GestaltPlugIn theGestaltPlugin,
                                           final PApplet theParent,
                                           final boolean thePresentationModeFlag) {
        super(false);
        _myTextureWidth = theParent.width;
        _myTextureHeight = theParent.height;

        _myParent = theParent;

        scale().set(1, 1, 1);

        _myTexCreator = theTexGenerator;
        _myBufferID = new BufferInfo();

        setTextureTarget(GL.GL_TEXTURE_2D);
        setTextureUnit(GL.GL_TEXTURE0);

        /* reset gestalt */
        theGestaltPlugin.displaycapabilities().width = theWidth;
        theGestaltPlugin.displaycapabilities().height = theHeight;
        theGestaltPlugin.camera().position().z = theHeight;
        theGestaltPlugin.camera().viewport().width = theWidth;
        theGestaltPlugin.camera().viewport().height = theHeight;

        /* connect to processing */
        UnBindFBO myUnbindFBO = new UnBindFBO();
        theGestaltPlugin.bin(Gestalt.BIN_FRAME_SETUP).add(myUnbindFBO);
        theGestaltPlugin.bin(Gestalt.BIN_FRAME_SETUP).swap(theGestaltPlugin.event(), myUnbindFBO);

        theGestaltPlugin.preDrawables().add(new BindBuffer());
        theGestaltPlugin.preDrawables().add(new ResetProperies());

        /* hack processing */
        ComponentListener[] myListeners = _myParent.getComponentListeners();
        for (int i = 0; i < myListeners.length; i++) {
            if (myListeners[i] instanceof ComponentAdapter) {
                _myParent.removeComponentListener(myListeners[i]);
            }
        }
        _myParent.size(_myTextureWidth, _myTextureHeight);
        /** @todo this is broken. why??? */
//        ( (PGraphicsOpenGL) _myParent.g).canvas.setBounds(0, 0, theWidth, theHeight);

        /* resize window */
        if (!thePresentationModeFlag) {
            _myParent.frame.setSize(theWidth, theHeight);
            _myParent.frame.setLocation(0, 0);
        }

        Dimension myFrameSize = _myParent.frame.getSize();
        _myParent.setBounds( (myFrameSize.width - theWidth) / 2,
                            (myFrameSize.height - theHeight) / 2,
                            theWidth, theHeight);

    }


    private class BindBuffer
        implements GestaltPlugIn.GLFragments {
        public void draw(GL gl) {
            bindBuffer(gl);
        }
    }


    private class ResetProperies
        implements GestaltPlugIn.GLFragments {

        public void draw(GL gl) {
            System.out.println("P5-TODO PGraphics.projection is gone...");
//            PMatrix projection = _myParent.g.projection;
//
//            gl.glViewport(0,
//                          0,
//                          _myTextureWidth,
//                          _myTextureHeight);
//
//            gl.glMatrixMode(GL.GL_PROJECTION);
//            float[] projectionFloats = new float[] {
//                                       projection.m00, projection.m10, projection.m20, projection.m30,
//                                       projection.m01, projection.m11, projection.m21, projection.m31,
//                                       projection.m02, projection.m12, projection.m22, projection.m32,
//                                       projection.m03, projection.m13, projection.m23, projection.m33
//            };
//            FloatBuffer projectionFloatBuffer = BufferUtil.newFloatBuffer(16);
//            projectionFloatBuffer.put(projectionFloats);
//            projectionFloatBuffer.rewind();
//            gl.glLoadMatrixf(projectionFloatBuffer);

            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glScalef(1, -1, 1);
        }
    }


    public void initStatic(final GL gl) {
        /* query maximum texture size */
        int[] myValue = new int[1];
        gl.glGetIntegerv(GL.GL_MAX_RENDERBUFFER_SIZE_EXT, myValue, 0);
        _myMaxTextureSize = myValue[0];
        ourStaticInitalized = true;
    }


    public int getPixelWidth() {
        return _myTextureWidth;
    }


    public int getPixelHeight() {
        return _myTextureHeight;
    }


    public int getTextureID() {
        return _myBufferID.texture;
    }


    public void add(final Drawable theDrawable) {
        System.err.println("### WARNING @" + getClass().getName() + " / do not add shapes here.");
    }


    protected final void init(final GL gl) {
        _myIsInitalized = true;

        /* handle texture creation */
        _myTexCreator.create(gl, _myTextureWidth, _myTextureHeight, _myBufferID);

        checkFrameBufferStatus(gl);

        /* unbind objects */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }


    public final void bindBuffer(GL gl) {
        if (_myIsInitalized) {
            /* bind framebuffer object */
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, _myBufferID.framebuffer_object);
        }
    }


    public final void unbindBuffer(GL gl) {
        if (_myIsInitalized) {
            /* release framebuffer object */
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
        }
    }


    private boolean checkFrameBufferStatus(GL gl) {
        int myStatus;
        myStatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
        switch (myStatus) {
            case GL.GL_FRAMEBUFFER_COMPLETE_EXT:

//                System.out.println("### INFO fbo complete.");
                return true;
            case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                System.err.println("### ERROR @ FrameBufferObject / GL_FRAMEBUFFER_UNSUPPORTED_EXT found.");
                System.err.println("checkFrameBufferStatus: " + myStatus);
                return false;
            default:
                System.err.println("### ERROR @ FrameBufferObject / will fail on all hardware");
                {
                    switch (myStatus) {
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT: " + myStatus);
                            break;
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT: " + myStatus);
                            break;
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT: " + myStatus);
                            break;
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT: " + myStatus);
                            break;
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT: " + myStatus);
                            break;
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT: " + myStatus);
                            break;
                        case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                            System.err.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT: " + myStatus);
                            break;
                        default:
                            System.err.println("checkFrameBufferStatus: " + myStatus);
                    }
                }
                return false;
        }
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
        gl.glBindTexture(getTextureTarget(), _myBufferID.texture);

        /* update properties */
        if (_myWrapModeChanged) {
            updateWrapMode(gl);
            _myWrapModeChanged = false;
        }

        if (_myFilterTypeChanged) {
            updateFilterType(gl);
            _myFilterTypeChanged = false;
        }
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


    public void load(Bitmap theBitmap) {
        System.err.println("### WARNING @" + getClass().getName() + " / unsupported method.");
    }


    public void reload() {
        System.err.println("### WARNING @" + getClass().getName() + " / unsupported method.");
    }


    public Bitmap bitmap() {
        if (_myBitmap == null) {
            _myBitmap = ByteBufferBitmap.getDefaultImageBitmap(_myTextureWidth, _myTextureHeight);
        }
        return _myBitmap;
    }


    public void setBitmapRef(final Bitmap theBitmap) {
        _myBitmap = theBitmap;
    }


    public void begin(GLContext theRenderContext, Material theParent) {
        final GL gl = (  theRenderContext).gl;
        final GLU glu = (  theRenderContext).glu;

        if (!theParent.wireframe) {
            /* choose texture unit */
            gl.glActiveTexture(getTextureUnit());

            /* enable texture target */
            gl.glEnable(getTextureTarget());
        }

        /* enable and bind texture */
        update(gl, glu);

        /* handle wireframe OR texturematrix */
        if (theParent.wireframe) {
            gl.glDisable(getTextureTarget());
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        } else {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPushMatrix();
            gl.glTranslatef(position().x, position().y, 0);
            gl.glScalef(scale().x * nonpoweroftwotexturerescale().x,
                        scale().y * nonpoweroftwotexturerescale().y,
                        scale().z);

            if (_myHintFlipYAxis) {
                gl.glTranslatef(0, -1, 0);
            }

            gl.glMatrixMode(GL.GL_MODELVIEW);
        }
    }


    public void end(GLContext theRenderContext, Material theParent) {
        final GL gl = (  theRenderContext).gl;

        /* turn off texture again */
        if (!theParent.wireframe) {
            gl.glDisable(getTextureTarget());
        }

        /* handle wireframe OR texturematrix */
        if (theParent.wireframe) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        } else {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW);
        }
    }


    public boolean isActive() {
        return _myIsActive;
    }


    public float getSortValue() {
        return 0.0f;
    }


    public void setSortValue(float theSortValue) {
    }


    public float[] getSortData() {
        return null;
    }


    public boolean isSortable() {
        return false;
    }


    private class UnBindFBO
        extends AbstractDrawable {
        public void draw(GLContext theRenderContext) {
            final GL gl = (  theRenderContext).gl;
            unbindBuffer(gl);
        }
    }
}
