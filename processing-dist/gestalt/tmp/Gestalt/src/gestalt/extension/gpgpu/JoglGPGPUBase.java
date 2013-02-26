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


package gestalt.extension.gpgpu;

import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;
import gestalt.material.texture.TextureInfo;
import gestalt.util.JoglUtil;

import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public abstract class JoglGPGPUBase
        extends AbstractDrawable {

    private final JoglGPGPUFBO[] _myFBOs;

    private int _myTextureReadID = 0;

    protected static final int WRITE_FBO = 0;

    protected static final int READ_FBO = 1;

    protected static final int READ_FBO_0 = 1;

    protected static final int READ_FBO_1 = 2;

    protected static final int READ_FBO_2 = 3;

    protected static final int READ_FBO_3 = 4;

    private boolean _myIsActive = true;

    private JoglFrameBufferObject _myReadFBO = null;

    private JoglFrameBufferObject _myWriteFBO = null;

    private int _myAvailableTextureUnit = GL.GL_TEXTURE0;

    private Vector<TextureInfo> _myExternalTextures = new Vector<TextureInfo>();

    public JoglGPGPUBase(final JoglTexCreator theTexCreator,
                         final int theWidth,
                         final int theHeight) {
        this(theTexCreator, theWidth, theHeight, 0);
    }

    public JoglGPGPUBase(final JoglTexCreator theTexCreator,
                         final int theWidth,
                         final int theHeight,
                         final int theNumberOfAdditionalTextures) {
        _myFBOs = new JoglGPGPUFBO[2];
        for (int i = 0; i < _myFBOs.length; i++) {
            setAvailableTextureUnit(GL.GL_TEXTURE0);
            _myFBOs[i] = new JoglGPGPUFBO(theWidth, theHeight, getNextAvailableTextureUnit(), theTexCreator,
                                          theNumberOfAdditionalTextures);
            _myFBOs[i].fbo().name = "JoglGPGPUFBO[" + i + "]";
            if (_myFBOs[i].fbo().additional_textures() != null) {
                for (int j = 0; j < _myFBOs[i].fbo().additional_textures().length; j++) {
                    _myFBOs[i].fbo().additional_texture(j).setTextureUnit(getNextAvailableTextureUnit());
                }
            }
        }
    }

    protected Vector<TextureInfo> external_textures() {
        return _myExternalTextures;
    }

    protected void setAvailableTextureUnit(final int theAvailableTextureUnit) {
        _myAvailableTextureUnit = theAvailableTextureUnit;
    }

    protected int getNextAvailableTextureUnit() {
        final int myCurrentTextureUnit = _myAvailableTextureUnit;
        _myAvailableTextureUnit++;
        return myCurrentTextureUnit;
    }

    public JoglGPGPUFBO[] FBOs() {
        return _myFBOs;
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        /* save states */
        push_states(gl);

        /* ping pong FBOs */
        _myReadFBO = getFBObyOffset(READ_FBO);
        _myWriteFBO = getFBObyOffset(WRITE_FBO);

        /* ATI workaround -- i really hate someone for this! */
        gl.glDisable(GL.GL_BLEND);

        /* init FBOs */
        for (int i = 0; i < _myFBOs.length; i++) {
            if (!_myFBOs[i].fbo().isInitialized()) {
                _myFBOs[i].fbo().init(gl);
                JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".draw().initFBO[" + i + "]", true);
            }
        }

        /* check if any write data is scheduled */
        for (int i = 0; i < _myFBOs.length; i++) {
            final JoglGPGPUFBO myJoglGPGPUFBO = _myFBOs[i];
            if (myJoglGPGPUFBO.isWriteDataScheduled()) {
                myJoglGPGPUFBO.writeData(gl, glu);
            }
        }

        /* set uniform variables in shader */
        beginShader(gl, glu);

        /* save previously bound FBO */
        final int myFBOBufferID = JoglFrameBufferObject.getCurrentlyBoundFBOTexID(gl);

        /* prepare fbo viewport */
        // todo maybe we should 'just' draw the necessary stuff not the whole FBO?
        _myWriteFBO.draw(theRenderContext);

        /* bind textures */
        bindTextures(gl, glu);

        /* process fragments */
        processFragments(_myReadFBO, gl, _myWriteFBO);

        /* unbind texture */
        unbindTextures(gl, glu);

        /* release */
        endShader(gl, glu);

        JoglFrameBufferObject.unbindBuffer(gl, myFBOBufferID);

        nextTextureID();

        /* restore states */
        pop_states(gl);
    }

    protected void push_states(final GL gl) {
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
    }

    protected void pop_states(final GL gl) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
        gl.glPopAttrib();
    }

    protected void bind(final GL gl, final TextureInfo theTex) {
        gl.glActiveTexture(theTex.getTextureUnit());
        gl.glBindTexture(theTex.getTextureTarget(),
                         theTex.getTextureID());
    }

    protected void unbind(final GL gl, final TextureInfo theTex) {
        gl.glActiveTexture(theTex.getTextureUnit());
        gl.glBindTexture(theTex.getTextureTarget(), 0);
    }

    protected void bindTextures(final GL gl, final GLU glu) {
        bind(gl, readBuffer());
        if (readBuffer().additional_textures() != null) {
            for (int j = 0; j < readBuffer().additional_textures().length; j++) {
                bind(gl, readBuffer().additional_texture(j));
            }
        }
        for (final TextureInfo myTex : external_textures()) {
            bind(gl, myTex);
        }
        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".bindTextures");
    }

    protected void unbindTextures(final GL gl, final GLU glu) {
        unbind(gl, readBuffer());
        if (readBuffer().additional_textures() != null) {
            for (int j = 0; j < readBuffer().additional_textures().length; j++) {
                unbind(gl, readBuffer().additional_texture(j));
            }
        }
        for (final TextureInfo myTex : external_textures()) {
            unbind(gl, myTex);
        }
        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".unbindTextures");
    }

    private void processFragments(final JoglFrameBufferObject myReadFBO, final GL gl, final JoglFrameBufferObject myWriteFBO) {
        /* the buffer needs to be bound a second time :( no good. */
        myWriteFBO.bindBuffer(gl);
        /* draw fullscreen quad */
        if (myReadFBO.additional_textures() != null) {
            gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
            final int[] myAttachmentPoints = new int[myReadFBO.additional_textures().length + 1];
            myAttachmentPoints[0] = myReadFBO.getBufferInfo().attachment_point;
            for (int i = 0; i < myReadFBO.getBufferInfo().additional_attachment_points.length; i++) {
                myAttachmentPoints[i + 1] = myReadFBO.getBufferInfo().additional_attachment_points[i];
            }
            gl.glDrawBuffers(myAttachmentPoints.length, myAttachmentPoints, 0);
        }
        JoglGPGPUFBO.quadFullscreenQuad(gl, myWriteFBO.getPixelWidth(), myWriteFBO.getPixelHeight());
        if (myReadFBO.additional_textures() != null) {
            gl.glPopAttrib();
        }
        JoglUtil.printGLError(gl, getClass().getSimpleName() + ".processFragments");
    }

    protected JoglFrameBufferObject getFBObyOffset(int theOffset) {
        return _myFBOs[ (_myTextureReadID + theOffset) % _myFBOs.length].fbo();
    }

    public JoglFrameBufferObject readBuffer() {
        return getFBObyOffset(READ_FBO);
    }

    public JoglFrameBufferObject writeBuffer() {
        return getFBObyOffset(WRITE_FBO);
    }

    protected void nextTextureID() {
        _myTextureReadID++;
        _myTextureReadID %= _myFBOs.length;
    }

    public boolean isActive() {
        return _myIsActive;
    }

    public void setActive(boolean theActiveState) {
        _myIsActive = theActiveState;
    }

    public int width() {
        return _myFBOs[0].fbo().getPixelWidth();
    }

    public int height() {
        return _myFBOs[0].fbo().getPixelHeight();
    }

    protected abstract void beginShader(final GL gl, final GLU glu);

    protected abstract void endShader(final GL gl, final GLU glu);
}
