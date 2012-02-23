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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.PointSprite;
import gestalt.material.texture.Bitmap;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.OrthoSetup;
import gestalt.shape.Plane;
import gestalt.util.JoglUtil;

import mathematik.Vector3f;

import com.sun.opengl.util.BufferUtil;

import java.nio.FloatBuffer;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.UNDEFINED;


public class JoglLEDDisplayGPU
        extends JoglLEDDisplay {

    private final JoglFrameBufferObject _myFBO;

    private final PointSprite _myLEDMaterial;

    private final Material _myMaterial;

    private final Vector3f _myScale;

    private final int[] _myVBOs = new int[] {UNDEFINED, UNDEFINED};

    private static final int VERTEX = 0;

    private static final int COLOR = 1;

    private static final int NUMBER_OF_VERTEX_COMPONENTS = 3;

    private static final int NUMBER_OF_COLOR_COMPONENTS = 4;

    private final Vector<Vector3f> _myPoints;

    private boolean _myDataChanged = true;

    private final RenderBin _myBin;

    private final Plane _myMask;

    public JoglLEDDisplayGPU(final int theWidth, final int theHeight,
                             final Bin theFBOBin,
                             final Bitmap thePointSprite) {
        _myFBO = JoglFrameBufferObject.createRectangular(theWidth, theHeight);

        /* set inital vertex position */
        _myPoints = new Vector<Vector3f>();
        for (int i = 0; i < theWidth * theHeight; i++) {
            final Vector3f v = new Vector3f();
            final float x = i % theWidth - theWidth / 2;
            final float y = i / theWidth - theHeight / 2;
            final float z = 0;
            v.set(x, y, z);
            _myPoints.add(v);
        }

        /* set intial scale */
        _myScale = new Vector3f(1, 1, 1);

        /* getPointSpriteMaterialPlugin */
        _myMaterial = new Material();
        _myMaterial.blendmode = Gestalt.MATERIAL_BLEND_INVERS_MULTIPLY;
        _myMaterial.depthtest = false;

        /* pointsprites */
        _myLEDMaterial = new PointSprite();
        _myLEDMaterial.load(thePointSprite);
        _myLEDMaterial.quadric[PointSprite.ATTENUATION_CONSTANT] = 10;
        _myLEDMaterial.quadric[PointSprite.ATTENUATION_LINEAR] = 0.002f;
        _myLEDMaterial.quadric[PointSprite.ATTENUATION_QUAD] = 0.000001f;
        _myLEDMaterial.pointsize = 16;
        _myLEDMaterial.minpointsize = 8;
        _myLEDMaterial.maxpointsize = 128;
        _myMaterial.addPlugin(_myLEDMaterial);

        theFBOBin.add(_myFBO);

        /* setup bin */
        _myBin = new RenderBin();

        /* setup mask */
        _myMask = new Plane();
        _myMask.material().depthtest = false;
        _myMask.material().depthmask = true;
        _myMask.material().transparent = true;
        _myMask.material().blendmode = Gestalt.MATERIAL_BLEND_ALPHA;
        _myMask.scale(theWidth, theHeight);
        _myMask.setActive(false);

        /* setup fbo */
        _myFBO.add(_myBin);
        _myFBO.add(new OrthoSetup());
        _myFBO.add(_myMask);
    }

    /* JoglLEDDisplay */
    public void addMask(Bitmap theBitmap) {
        _myMask.setActive(true);
        _myMask.material().addTexture().load(theBitmap);
    }

    public RenderBin bin() {
        return _myBin;
    }

    public Camera camera() {
        return _myFBO.camera();
    }

    public Vector<Vector3f> points() {
        _myDataChanged = true;
        return _myPoints;
    }

    public Vector3f scale() {
        return _myScale;
    }

    public Color backgroundcolor() {
        return _myFBO.backgroundcolor();
    }

    public PointSprite getPointSpriteMaterialPlugin() {
        return _myLEDMaterial;
    }

    public Material material() {
        return _myMaterial;
    }

    /* Drawable */
    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        final GLU glu = theRenderContext.glu;

        if (_myVBOs[VERTEX] == UNDEFINED && _myVBOs[COLOR] == UNDEFINED) {
            createVBO(gl, glu, _myFBO);
        }

        /* copy data */
        transferDataToVBO(gl, glu, _myFBO);

        /* draw */
        gl.glPushMatrix();
        gl.glScalef(_myScale.x, _myScale.y, _myScale.z);

        _myMaterial.begin(theRenderContext);
        display(gl, glu, _myFBO);
        _myMaterial.end(theRenderContext);

        gl.glPopMatrix();
    }

    private void reloadVertexData(GL gl) {
        final FloatBuffer myBuffer = JoglUtil.mapBuffer(gl,
                                                        _myVBOs[VERTEX],
                                                        GL.GL_WRITE_ONLY);
        myBuffer.put(werkzeug.Util.toArray3f(_myPoints));
        myBuffer.rewind();
        JoglUtil.unmapBuffer(gl, _myVBOs[VERTEX]);
    }

    private void display(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
        final int w = theFBO.getPixelWidth();
        final int h = theFBO.getPixelHeight();

        /* update data */
        if (_myDataChanged) {
            _myDataChanged = false;
            reloadVertexData(gl);
        }

        /* VERTEX */
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                        _myVBOs[VERTEX]);
        gl.glVertexPointer(NUMBER_OF_VERTEX_COMPONENTS,
                           GL.GL_FLOAT,
                           0, 0);

        /* COLORS */
        gl.glEnableClientState(GL.GL_COLOR_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                        _myVBOs[COLOR]);
        gl.glColorPointer(NUMBER_OF_COLOR_COMPONENTS,
                          GL.GL_FLOAT,
                          0, 0);

        /* draw */
        gl.glDrawArrays(GL.GL_POINTS, 0, w * h);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL.GL_COLOR_ARRAY);

        JoglUtil.printGLError(gl, glu, "display()", true);
    }

    private void createVBO(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
        final int w = theFBO.getPixelWidth();
        final int h = theFBO.getPixelHeight();

        /* create intial data */
        final float[] myVertices = werkzeug.Util.toArray3f(_myPoints);

        final float[] myColors = new float[w * h * NUMBER_OF_COLOR_COMPONENTS];
        for (int i = 0; i < myColors.length; i += NUMBER_OF_COLOR_COMPONENTS) {
            myColors[i + 0] = 0.0f;
            myColors[i + 1] = 0.0f;
            myColors[i + 2] = 0.0f;
            myColors[i + 3] = 1.0f;
        }

        /* setup 2 buffers for color4f and vertices */
        gl.glGenBuffers(_myVBOs.length, _myVBOs, 0);

        /* VERTEX */
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBOs[VERTEX]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                        w * h * NUMBER_OF_VERTEX_COMPONENTS * BufferUtil.SIZEOF_FLOAT,
                        FloatBuffer.wrap(myVertices),
                        GL.GL_STATIC_DRAW);

        /* COLOR */
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBOs[COLOR]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                        w * h * NUMBER_OF_COLOR_COMPONENTS * BufferUtil.SIZEOF_FLOAT,
                        FloatBuffer.wrap(myColors),
                        GL.GL_STREAM_COPY);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".createVBO()", true);
    }

    private void transferDataToVBO(GL gl, GLU glu, final JoglFrameBufferObject theFBO) {
        final int w = theFBO.getPixelWidth();
        final int h = theFBO.getPixelHeight();

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT,
                                theFBO.getBufferInfo().framebuffer_object);

        /* read the pixel colors into the VBO mesh */
        gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myVBOs[COLOR]);
        gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
        gl.glReadPixels(0, 0,
                        w, h,
                        GL.GL_RGBA,
                        GL.GL_FLOAT,
                        0);
        gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, 0);

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".transferDataToVBO()", true);
    }

    public JoglFrameBufferObject fbo() {
        return _myFBO;
    }
}
