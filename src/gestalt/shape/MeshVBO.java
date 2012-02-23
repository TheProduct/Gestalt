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


package gestalt.shape;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.util.JoglUtil;

import com.sun.opengl.util.BufferUtil;

import java.nio.FloatBuffer;
import java.util.Vector;

import javax.media.opengl.GL;


/** @todo override methods for data manipulation */
/** @todo replace glDrawArrays with glDrawElements -- http://www.sci.utah.edu/~bavoil/opengl/bavoil_trimeshes_2005.pdf */
/** @todo JSR-231 mapping of data is broken when VBO is not supported */
public class MeshVBO
        extends Mesh {

    /* vbo helpers */
    private static final long serialVersionUID = -160967145700062544L;

    private int[] _myBufferIDs;

    private static final int VERTEX_POINTER = 0;

    private static final int COLOR_POINTER = 1;

    private static final int NORMAL_POINTER = 2;

    private static final int TEXCOORD_POINTER = 3;

    private static boolean _myVBOsupported;

    private Drawable _myDrawable;

    private boolean _myUpdateData;

    private final boolean _myUseColors;

    private final boolean _myUseNormals;

    private final boolean _myUseTexCoords;

    /* callback mechanism for VBO manipualtion */
    private Vector<VBOModifier> _myVBOModifiers;

    public MeshVBO(float[] theVertices,
                   int theVertexComponents,
                   float[] theColors,
                   int theColorComponents,
                   float[] theTexCoords,
                   int theTexCoordComponents,
                   float[] theNormals,
                   int thePrimitive) {

        super(theVertices,
              theVertexComponents,
              theColors,
              theColorComponents,
              theTexCoords,
              theTexCoordComponents,
              theNormals,
              thePrimitive);

        /* setup inital state */
        _myDrawable = new Init();
        _myUpdateData = false;

        /*
         * store the inital data state of the VBO mesh this is important because
         * as of now we can t add new data types after the VBO is created
         */
        if (_myColors != null) {
            _myUseColors = true;
        } else {
            _myUseColors = false;
        }
        /* normals */
        if (_myNormals != null) {
            _myUseNormals = true;
        } else {
            _myUseNormals = false;
        }
        /* texcoords */
        if (_myTexCoords != null) {
            _myUseTexCoords = true;
        } else {
            _myUseTexCoords = false;
        }

        if (_myVertices == null) {
            System.err.print("### ERROR @ JoglVBOMesh / vertex data can not be 'null'.");
            _myVertices = new float[0];
        }
    }


    /* VBO modifiers */
    public void addModifier(VBOModifier theModifier) {
        if (_myVBOModifiers == null) {
            _myVBOModifiers = new Vector<VBOModifier>();
        }
        _myVBOModifiers.add(theModifier);
    }

    public boolean removeModifier(VBOModifier theModifier) {
        if (_myVBOModifiers == null) {
            return false;
        }
        return _myVBOModifiers.remove(theModifier);
    }

    public interface VBOModifier {

        void modifyVertexData(FloatBuffer theVertexData);
    }


    /* direct data access */
    public float[] colors() {
        if (!_myUseColors) {
            System.err.println("### WARNING @ JoglVBOMesh / data must be upload on instantion.");
        }
        return _myColors;
    }

    public float[] normals() {
        if (!_myUseNormals) {
            System.err.println("### WARNING @ JoglVBOMesh / data must be upload on instantion.");
        }
        return _myNormals;
    }

    public float[] texcoords() {
        if (!_myUseTexCoords) {
            System.err.println("### WARNING @ JoglVBOMesh / data must be upload on instantion.");
        }
        return _myTexCoords;
    }

    public void draw(GLContext theRenderContext) {
        if (_myVertices != null) {
            _myDrawable.draw(theRenderContext);
        }
    }

    public void updateData() {
        _myUpdateData = true;
    }


    /* vbo helpers */
    private void enable(GL gl) {
        /**
         * <pre>
         * enable vbo one of the following types
         * GL_VERTEX_ARRAY
         * GL_COLOR_ARRAY
         * GL_INDEX_ARRAY
         * GL_NORMAL_ARRAY
         * GL_TEXTURE_COORD_ARRAY
         * GL_EDGE_FLAG_ARRAY
         * </pre>
         */
        /** @todo JSR-231 check 'BufferUtil.bufferOffset(0)' replaced by '0' */
        /** @todo
         * JSR-231 ugly performance hit when VBO is not supported.
         * we should wrap the float array in a buffer with class scope.
         */

        /* vertex */
        {
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            if (_myVBOsupported) {
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[VERTEX_POINTER]);
                gl.glVertexPointer(_myNumberOfVertexComponents,
                                   GL.GL_FLOAT,
                                   0,
                                   0);
            } else {
                FloatBuffer myBuffer = BufferUtil.newFloatBuffer(_myVertices.length);
                myBuffer.put(_myVertices);
                myBuffer.rewind();
                gl.glVertexPointer(_myNumberOfVertexComponents,
                                   GL.GL_FLOAT,
                                   0,
                                   myBuffer);
            }
        }
        /* color4f */
        if (_myColors != null) {
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            if (_myVBOsupported) {
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[COLOR_POINTER]);
                gl.glColorPointer(_myNumberOfColorComponents,
                                  GL.GL_FLOAT,
                                  0,
                                  0);
            } else {
                FloatBuffer myBuffer = BufferUtil.newFloatBuffer(_myColors.length);
                myBuffer.put(_myColors);
                myBuffer.rewind();
                gl.glColorPointer(_myNumberOfColorComponents,
                                  GL.GL_FLOAT,
                                  0,
                                  myBuffer);
            }
        }
        /* normals */
        if (_myNormals != null) {
            gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            if (_myVBOsupported) {
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[NORMAL_POINTER]);
                gl.glNormalPointer(GL.GL_FLOAT,
                                   0,
                                   0);
            } else {
                FloatBuffer myBuffer = BufferUtil.newFloatBuffer(_myNormals.length);
                myBuffer.put(_myNormals);
                myBuffer.rewind();
                gl.glNormalPointer(GL.GL_FLOAT,
                                   0,
                                   myBuffer);
            }
        }
        /* texcoords */
        if (_myTexCoords != null) {
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            if (_myVBOsupported) {
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[TEXCOORD_POINTER]);
                gl.glTexCoordPointer(_myNumberOfTexCoordComponents,
                                     GL.GL_FLOAT,
                                     0,
                                     0);
            } else {
                FloatBuffer myBuffer = BufferUtil.newFloatBuffer(_myTexCoords.length);
                myBuffer.put(_myTexCoords);
                myBuffer.rewind();
                gl.glTexCoordPointer(_myNumberOfTexCoordComponents,
                                     GL.GL_FLOAT,
                                     0,
                                     myBuffer);
            }
        }
    }

    private void disable(GL gl) {
        if (_myVBOsupported) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        }
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        if (_myColors != null) {
            gl.glDisableClientState(GL.GL_COLOR_ARRAY);
        }
        if (_myNormals != null) {
            gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        }
        if (_myTexCoords != null) {
            gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        }
    }

    private static FloatBuffer mapBuffer(final GL gl,
                                         final int thePointer) {
        /**
         * map buffer to cpu memory READ_ONLY_ARB 0x88B8 WRITE_ONLY_ARB 0x88B9
         * READ_WRITE_ARB 0x88BA
         */

        /* manipulate vertex buffer */
        if (_myVBOsupported) {
            return JoglUtil.mapBuffer(gl, thePointer, GL.GL_WRITE_ONLY);
        }
        return null;
    }

    private static void unmapBuffer(final GL gl,
                                    final int thePointer) {
        if (_myVBOsupported) {
            JoglUtil.unmapBuffer(gl, thePointer);
        }
    }

    private class Init
            extends AbstractDrawable {

        public void draw(final GLContext theContext) {
            final GL gl = (theContext).gl;

            /* check for VBO extension. */
            try {
                _myVBOsupported = gl.isFunctionAvailable("glGenBuffers")
                        && gl.isFunctionAvailable("glBindBuffer")
                        && gl.isFunctionAvailable("glBufferData")
                        && gl.isFunctionAvailable("glDeleteBuffers");
            } catch (Exception theException) {
                System.err.println("### WARNING @"
                        + getClass().getName() + " / "
                        + theException);
                _myVBOsupported = false;
            }

            if (!_myVBOsupported) {
                System.err.println("### WARNING @"
                        + getClass().getName()
                        + " / VBOs are not supported. bumpy road ahead.");
                return;
            }

            /* allocate buffer storage */
            /** @todo we don t always need 4 buffers! */
            _myBufferIDs = new int[4];

            /* create buffers storage */
            gl.glGenBuffers(_myBufferIDs.length, _myBufferIDs, 0);

            /**
             * load data into opengl memory the following modes can be specified
             *
             * STREAM_DRAW_ARB
             * The data store contents will be specified once by
             * the application, and used at most a few times as the source of a
             * GL (drawing) command.
             *
             * STREAM_READ_ARB
             * The data store contents will be specified once by reading data from
             * the GL, and queried at most a few times by the application.
             *
             * STREAM_COPY_ARB
             * The data store contents will be specified once by reading data from the
             * GL, and used at most a few times as the source of a GL (drawing)
             * command.
             *
             * STATIC_DRAW_ARB The data store contents will be specified once by
             * the application, and used many times as the source for GL (drawing)
             * commands.
             *
             * STATIC_READ_ARB
             * The data store contents will be specified once by reading data from
             * the GL, and queried many times by the application.
             *
             * STATIC_COPY_ARB
             * The data store contents will be specified once by reading data from the
             * GL, and used many times as the source for GL (drawing) commands.
             *
             * DYNAMIC_DRAW_ARB
             * The data store contents will be respecified
             * repeatedly by the application, and used many times as the source
             * for GL (drawing) commands.
             *
             * DYNAMIC_READ_ARB
             * The data store contents will be respecified repeatedly by reading
             * data from the GL, and queried many times by the application.
             *
             * DYNAMIC_COPY_ARB
             * The data store contents will be respecified repeatedly by reading
             * data from the GL, and used many times as the source for GL
             * (drawing) commands.
             */
            /**
             * use one of the following methods to point to specific data
             *
             * <pre>
             * glVertexPointer(GLint size, GLenum type, GLsizei stride, const GLvoid * pointer);
             * glColorPointer(GLint size, GLenum type, GLsizei stride, const GLvoid * pointer);
             * glNormalPointer(GLenum type, GLsizei stride, const GLvoid * pointer);
             * glTexturePointer(GLint size, GLenum type, GLsizei stride, const GLvoid * pointer);
             * glEdgeFlagPointer(GLsizei stride, const GLvoid * pointer);
             * </pre>
             */
            /** @todo make this accessible from the outside */
//            final int myUsageType = GL.GL_DYNAMIC_DRAW;
            final int myUsageType = GL.GL_STATIC_DRAW;

            /* vertex */
            {
                /** @todo JSR-231 performance hit? */
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[VERTEX_POINTER]);
                gl.glBufferData(GL.GL_ARRAY_BUFFER,
                                _myNumberOfAtoms * _myNumberOfVertexComponents * BufferUtil.SIZEOF_FLOAT,
                                FloatBuffer.wrap(_myVertices),
                                myUsageType);
            }
            /* color4f */
            if (_myColors != null) {
                /** @todo JSR-231 performance hit? */
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[COLOR_POINTER]);
                gl.glBufferData(GL.GL_ARRAY_BUFFER,
                                _myNumberOfAtoms * _myNumberOfColorComponents * BufferUtil.SIZEOF_FLOAT,
                                FloatBuffer.wrap(_myColors),
                                myUsageType);
            }
            /* normals */
            if (_myNormals != null) {
                /** @todo JSR-231 performance hit? */
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[NORMAL_POINTER]);
                gl.glBufferData(GL.GL_ARRAY_BUFFER,
                                _myNumberOfAtoms * NUMBER_OF_NORMAL_COMPONENTS * BufferUtil.SIZEOF_FLOAT,
                                FloatBuffer.wrap(_myNormals),
                                myUsageType);
            }
            /* texcoords */
            if (_myTexCoords != null) {
                /** @todo JSR-231 performance hit? */
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
                                _myBufferIDs[TEXCOORD_POINTER]);
                gl.glBufferData(GL.GL_ARRAY_BUFFER,
                                _myNumberOfAtoms * _myNumberOfTexCoordComponents * BufferUtil.SIZEOF_FLOAT,
                                FloatBuffer.wrap(_myTexCoords),
                                myUsageType);
            }

            /* change state to display */
            _myDrawable = new Display();
            _myDrawable.draw(theContext);
        }
    }

    private class Display
            extends AbstractDrawable {

        public void draw(final GLContext theRenderContext) {
            final GL gl = theRenderContext.gl;

            /* map VBO to memory and upload data */
            /** @todo check this with JSR-231 */
            if (_myUpdateData) {
                _myUpdateData = false;

                /* vertex */
                if (_myVBOsupported) {
                    {
                        FloatBuffer myBuffer = mapBuffer(gl, _myBufferIDs[VERTEX_POINTER]);
                        myBuffer.put(_myVertices);
                        myBuffer.rewind();
                        unmapBuffer(gl, _myBufferIDs[VERTEX_POINTER]);
                    }
                    /* color4f */
                    if (_myUseColors) {
                        FloatBuffer myBuffer = mapBuffer(gl, _myBufferIDs[COLOR_POINTER]);
                        myBuffer.put(_myColors);
                        myBuffer.rewind();
                        unmapBuffer(gl, _myBufferIDs[COLOR_POINTER]);
                    }
                    /* normals */
                    if (_myUseNormals) {
                        FloatBuffer myBuffer = mapBuffer(gl, _myBufferIDs[NORMAL_POINTER]);
                        myBuffer.put(_myNormals);
                        myBuffer.rewind();
                        unmapBuffer(gl, _myBufferIDs[NORMAL_POINTER]);
                    }
                    /* texcoords */
                    if (_myUseTexCoords && !material.disableTextureCoordinates) {
                        FloatBuffer myBuffer = mapBuffer(gl, _myBufferIDs[TEXCOORD_POINTER]);
                        myBuffer.put(_myTexCoords);
                        myBuffer.rewind();
                        unmapBuffer(gl, _myBufferIDs[TEXCOORD_POINTER]);
                    }
                }
            }

            /* material */
            material.begin(theRenderContext);

            /** @todo handling textures is missing here */

            /* geometrie */
            gl.glPushMatrix();
            JoglUtil.applyTransform(gl,
                                    _myTransformMode,
                                    transform,
                                    rotation,
                                    scale);

            /* enable vbo */
            enable(gl);

            if (_myVBOModifiers != null) {
                /**
                 * @todo
                 * optimize mapping mechanism!
                 * other buffers need to be mapped as well.
                 */
                for (int i = 0; i < _myVBOModifiers.size(); i++) {
                    VBOModifier myModifier = (VBOModifier)_myVBOModifiers.get(i);
                    final FloatBuffer myBuffer = mapBuffer(gl, _myBufferIDs[VERTEX_POINTER]);
                    myModifier.modifyVertexData(myBuffer);
                    unmapBuffer(gl, _myBufferIDs[VERTEX_POINTER]);
                }
            }

            /**
             * <pre>
             * void glDrawArrays( GLenum mode, GLint first, GLsizei count ) mode
             * specifies the primitve type
             * GL_POINTS
             * GL_LINE_STRIP
             * GL_LINE_LOOP
             * GL_LINES
             * GL_TRIANGLE_STRIP
             * GL_TRIANGLE_FAN
             * GL_TRIANGLES
             * GL_QUAD_STRIP
             * GL_QUADS
             * GL_POLYGON
             * </pre>
             */
            gl.glDrawArrays(_myPrimitive, _myDrawStart, _myDrawLength);

//            gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
//            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
//            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
//            gl.glNormalPointer(GL.GL_FLOAT, 0, FloatBuffer.wrap(_myNormals));
//            gl.glVertexPointer(3, GL.GL_FLOAT, 0, FloatBuffer.wrap(_myVertices));
//            gl.glTexCoordPointer(3, GL.GL_FLOAT, 0, FloatBuffer.wrap(_myTexCoords));
//            gl.glDrawElements(primitiveType, indices.capacity(),
//                              GL.GL_UNSIGNED_INT, indices);
//            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
//            gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
//            gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);

            /* finish drawing */
            gl.glPopMatrix();

            /* disable vbo */
            disable(gl);

            /* material */
            material.end(theRenderContext);
        }
    }

    public void dispose(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        gl.glDeleteBuffers(_myBufferIDs.length, _myBufferIDs, 0);
    }
}
