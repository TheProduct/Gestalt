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


package gestalt.extension.framebufferobject;


import com.sun.opengl.util.BufferUtil;
import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.render.Disposable;
import gestalt.shape.AbstractShape;
import gestalt.util.JoglUtil;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import java.nio.FloatBuffer;


public abstract class JoglPBOIndexedMesh
    extends AbstractShape implements Disposable {

    private int _myVBO = Gestalt.UNDEFINED;

    private int _myIBO = Gestalt.UNDEFINED;

    private int _myAttachmentPointVertices;

    private int _myAttachmentPointColors;

    private int _myAttachmentPointNormals;

    private int _myAttachmentPointTexCoords;

    private JoglFrameBufferObject _myFBO;

    protected final int w;

    protected final int h;

    private final int _myNumberOfIndices;

    private final int _myPrimitives;

    public boolean USE_COLORS;

    public boolean USE_NORMALS;

    public boolean USE_TEX_COORDS;

    public int PIXEL_TYPE = GL.GL_RGB;

    private int PIXEL_TYPE_COLOR = GL.GL_RGB; /* TODO something is a bit weird with RGBA. */

    private int _myColorBuffer;

    private int _myNormalBuffer;

    private int _myTexCoordBuffer;

    private int _myColorStride = 0;

    private int _myNormalStride = 0;

    private int _myVertexStride = 0;

    private int _myTexCoordStride = 0;

    private int _myVertexNumberOfCoordinates = 3;

    private int _myColorNumberOfComponents = 3;

    private int _myTexCoordNumberOfCoordinates = 3;

    public JoglPBOIndexedMesh(final JoglFrameBufferObject theFrameBufferObject,
                              final boolean theUseColors,
                              final boolean theUseNormals,
                              final boolean theUseTexCoords) {
        _myFBO = theFrameBufferObject;
        USE_COLORS = theUseColors;
        USE_NORMALS = theUseNormals;
        USE_TEX_COORDS = theUseTexCoords;
        w = _myFBO.getPixelWidth();
        h = _myFBO.getPixelHeight();
        _myNumberOfIndices = getNumberOfVertices(w, h);
        _myPrimitives = getPrimitiveType();
        material = new Material();
        _myAttachmentPointVertices = GL.GL_COLOR_ATTACHMENT0_EXT;
        _myAttachmentPointColors = GL.GL_COLOR_ATTACHMENT1_EXT;
        _myAttachmentPointNormals = GL.GL_COLOR_ATTACHMENT2_EXT;
        _myAttachmentPointTexCoords = GL.GL_COLOR_ATTACHMENT3_EXT;
    }


    public void setFBORef(final JoglFrameBufferObject theFrameBufferObject) {
        _myFBO = theFrameBufferObject;
    }


    public void number_of_components_colors(final int theNumberOfComponents) {
        _myColorNumberOfComponents = theNumberOfComponents;
    }


    public void number_of_coordinates_vertices(final int theNumberOfCoordinates) {
        _myVertexNumberOfCoordinates = theNumberOfCoordinates;
    }


    public void number_of_coordinates_texcoords(final int theNumberOfCoordinates) {
        _myTexCoordNumberOfCoordinates = theNumberOfCoordinates;
    }


    public void stride_vertices(int theStride) {
        _myVertexStride = theStride;
    }


    public void stride_colors(int theStride) {
        _myColorStride = theStride;
    }


    public void stride_normals(int theStride) {
        _myNormalStride = theStride;
    }


    public void stride_texcoords(int theStride) {
        _myTexCoordStride = theStride;
    }


    public void attachmentpoint_colors(final int theAttachmentPoint) {
        _myAttachmentPointColors = theAttachmentPoint;
    }


    public void attachmentpoint_normals(final int theAttachmentPoint) {
        _myAttachmentPointNormals = theAttachmentPoint;
    }


    public void attachmentpoint_vertices(final int theAttachmentPoint) {
        _myAttachmentPointVertices = theAttachmentPoint;
    }


    public void attachmentpoint_texcoords(final int theAttachmentPoint) {
        _myAttachmentPointTexCoords = theAttachmentPoint;
    }


    public void draw(final GLContext theRenderContext) {
        final GL gl = (  theRenderContext).gl;
        final GLU glu = (  theRenderContext).glu;

        if (!_myFBO.isInitialized()) {
            System.err.println("### WARNING @ " + getClass().getSimpleName() + " / initialize FBO first.");
            return;
        }

        if (_myVBO == Gestalt.UNDEFINED && _myIBO == Gestalt.UNDEFINED) {
//            System.out.println("### creating VBO and IBO.");
            createVBO(gl, glu);
            JoglUtil.printGLError(gl, glu, "createVBO()", true);
        }

        copyVertexData(gl, glu);
        JoglUtil.printGLError(gl, glu, "copyVertexData()", true);

        /* begin material */
        material.begin(theRenderContext);
        JoglUtil.printGLError(gl, glu, "begin material()", true);

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* draw VBO */
        drawVBO(gl, glu);
        JoglUtil.printGLError(gl, glu, "drawVBO()", true);

        /* finish drawing */
        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);
    }


    private void createVBO(final GL gl, final GLU glu) {
        /* create buffer 'names' */
        int myBuffers = 2;
        if (USE_COLORS) {
            myBuffers++;
        }
        if (USE_NORMALS) {
            myBuffers++;
        }
        if (USE_TEX_COORDS) {
            myBuffers++;
        }
        final int[] tmp = new int[myBuffers];
        gl.glGenBuffers(tmp.length, tmp, 0);
        int myIndexCounter = 0;

        /* create vertex buffer */
        _myVBO = tmp[myIndexCounter];
        myIndexCounter++;
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBO);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                        w * h * _myVertexNumberOfCoordinates * BufferUtil.SIZEOF_FLOAT,
                        null,
                        GL.GL_STREAM_COPY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        /* create color4f buffers */
        if (USE_COLORS) {
            _myColorBuffer = tmp[myIndexCounter];
            myIndexCounter++;
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myColorBuffer);
            gl.glBufferData(GL.GL_ARRAY_BUFFER,
                            w * h * _myColorNumberOfComponents * BufferUtil.SIZEOF_FLOAT,
                            null,
                            GL.GL_STREAM_COPY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        }

        /* create normal buffers */
        if (USE_NORMALS) {
            _myNormalBuffer = tmp[myIndexCounter];
            myIndexCounter++;
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myNormalBuffer);
            gl.glBufferData(GL.GL_ARRAY_BUFFER,
                            w * h * 3 * BufferUtil.SIZEOF_FLOAT,
                            null,
                            GL.GL_STREAM_COPY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        }

        /* create texcoords buffers */
        if (USE_TEX_COORDS) {
            _myTexCoordBuffer = tmp[myIndexCounter];
            myIndexCounter++;
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myTexCoordBuffer);
            gl.glBufferData(GL.GL_ARRAY_BUFFER,
                            w * h * _myTexCoordNumberOfCoordinates * BufferUtil.SIZEOF_FLOAT,
                            null,
                            GL.GL_STREAM_COPY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        }

        /* create index buffer */
        final int[] myIndices = setupIndexList(_myNumberOfIndices, w, h);

        _myIBO = tmp[myIndexCounter];
        myIndexCounter++;
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, _myIBO);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER_ARB,
                        _myNumberOfIndices * BufferUtil.SIZEOF_INT,
                        IntBuffer.wrap(myIndices),
                        GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
    }


    private void drawVBO(final GL gl, final GLU glu) {
        /* render vertex array as triangle mesh */
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        if (USE_COLORS) {
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
        }
        if (USE_NORMALS) {
            gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        }
        if (USE_TEX_COORDS) {
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        }
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myVBO);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, _myIBO);
        gl.glVertexPointer(_myVertexNumberOfCoordinates,
                           GL.GL_FLOAT,
                           _myVertexStride * BufferUtil.SIZEOF_FLOAT,
                           0);

        if (USE_COLORS) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myColorBuffer);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, _myIBO);
            gl.glColorPointer(_myColorNumberOfComponents,
                              GL.GL_FLOAT,
                              _myColorStride * BufferUtil.SIZEOF_FLOAT,
                              0);
        }
        if (USE_NORMALS) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myNormalBuffer);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, _myIBO);
            gl.glNormalPointer(GL.GL_FLOAT,
                               _myNormalStride * BufferUtil.SIZEOF_FLOAT,
                               0);
        }
        if (USE_TEX_COORDS) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, _myTexCoordBuffer);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, _myIBO);
            gl.glTexCoordPointer(_myTexCoordNumberOfCoordinates,
                                 GL.GL_FLOAT,
                                 _myTexCoordStride * BufferUtil.SIZEOF_FLOAT,
                                 0);
        }

        /* draw vbo */
        gl.glDrawElements(_myPrimitives, _myNumberOfIndices, GL.GL_UNSIGNED_INT, 0);

        /* clean up */
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        if (USE_COLORS) {
            gl.glDisableClientState(GL.GL_COLOR_ARRAY);
        }
        if (USE_NORMALS) {
            gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        }
        if (USE_TEX_COORDS) {
            gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        }
    }


    private void copyVertexData(final GL gl, final GLU glu) {
        /* ATI workaround -- i really hate someone for this! */
        gl.glDisable(GL.GL_BLEND);

        /* save currently bound FBO */
        final int[] tmp = new int[1];
        gl.glGetIntegerv(GL.GL_FRAMEBUFFER_BINDING_EXT, tmp, 0);
        final int myFBOBufferID = tmp[0];

        /* copy data from FBO to vertex buffer */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, _myFBO.getBufferInfo().framebuffer_object);
        gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myVBO);

        gl.glReadBuffer(_myAttachmentPointVertices);
        gl.glReadPixels(0, 0,
                        w, h,
                        PIXEL_TYPE,
                        GL.GL_FLOAT,
                        0);
        if (USE_COLORS) {
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myColorBuffer);
            gl.glReadBuffer(_myAttachmentPointColors);
            gl.glReadPixels(0, 0,
                            w, h,
                            PIXEL_TYPE_COLOR,
                            GL.GL_FLOAT,
                            0);
        }
        if (USE_NORMALS) {
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myNormalBuffer);
            gl.glReadBuffer(_myAttachmentPointNormals);
            gl.glReadPixels(0, 0,
                            w, h,
                            PIXEL_TYPE,
                            GL.GL_FLOAT,
                            0);
        }
        if (USE_TEX_COORDS) {
            gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, _myTexCoordBuffer);
            gl.glReadBuffer(_myAttachmentPointTexCoords);
            gl.glReadPixels(0, 0,
                            w, h,
                            PIXEL_TYPE,
                            GL.GL_FLOAT,
                            0);
        }
        gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_ARB, 0);

        /* restore FBO */
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, myFBOBufferID);
        JoglUtil.printGLError(gl, glu, getClass().getSimpleName() + ".copyVertexData", true);
    }


    protected abstract int getPrimitiveType();


    protected abstract int getNumberOfVertices(int w, int h);


    protected abstract int[] setupIndexList(int theNumberOfIndices, int w, int h);


    public void dispose(GLContext theRenderContext) {
        System.out.println("### dispose " + this.getClass().getSimpleName() + " properly!");
    }
}
