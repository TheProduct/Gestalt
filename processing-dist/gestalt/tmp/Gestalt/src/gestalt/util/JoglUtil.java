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

/*
 * this is a loose collection of helpful methods.
 */

package gestalt.util;

import gestalt.candidates.JoglMultiTexPlane;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.quadline.QuadFragment;
import gestalt.render.BasicRenderer;
import gestalt.shape.AbstractShape;
import gestalt.shape.Mesh;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;

import mathematik.TransformMatrix4f;
import mathematik.Vector2f;
import mathematik.Vector3f;
import mathematik.Vertex3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;


public class JoglUtil {

    public static int mapOpenGLPrimitiveToGestaltPrimitive(int theOpenGLPrimitive) {
        int myGestaltPrimitive = UNDEFINED;
        switch (theOpenGLPrimitive) {
            case GL.GL_TRIANGLES:
                myGestaltPrimitive = MESH_TRIANGLES;
                break;
            case GL.GL_QUADS:
                myGestaltPrimitive = MESH_QUADS;
                break;
            case GL.GL_QUAD_STRIP:
                myGestaltPrimitive = MESH_QUAD_STRIP;
                break;
            case GL.GL_POINTS:
                myGestaltPrimitive = MESH_POINTS;
                break;
            case GL.GL_LINES:
                myGestaltPrimitive = MESH_LINES;
                break;
            case GL.GL_LINE_LOOP:
                myGestaltPrimitive = MESH_LINE_LOOP;
                break;
            case GL.GL_POLYGON:
                myGestaltPrimitive = MESH_POLYGON;
                break;
            case GL.GL_TRIANGLE_STRIP:
                myGestaltPrimitive = MESH_TRIANGLE_STRIP;
                break;
            case GL.GL_TRIANGLE_FAN:
                myGestaltPrimitive = MESH_TRIANGLE_FAN;
                break;

        }
        return myGestaltPrimitive;
    }

    public static int mapGestaltPrimitiveToOpenGLPrimitive(int theGestaltPrimitive) {
        int myOpenGLPrimitive = UNDEFINED;
        switch (theGestaltPrimitive) {
            case MESH_TRIANGLES:
                myOpenGLPrimitive = GL.GL_TRIANGLES;
                break;
            case MESH_QUADS:
                myOpenGLPrimitive = GL.GL_QUADS;
                break;
            case MESH_QUAD_STRIP:
                myOpenGLPrimitive = GL.GL_QUAD_STRIP;
                break;
            case MESH_POINTS:
                myOpenGLPrimitive = GL.GL_POINTS;
                break;
            case MESH_LINES:
                myOpenGLPrimitive = GL.GL_LINES;
                break;
            case MESH_LINE_LOOP:
                myOpenGLPrimitive = GL.GL_LINE_LOOP;
                break;
            case MESH_POLYGON:
                myOpenGLPrimitive = GL.GL_POLYGON;
                break;
            case MESH_TRIANGLE_STRIP:
                myOpenGLPrimitive = GL.GL_TRIANGLE_STRIP;
                break;
            case MESH_TRIANGLE_FAN:
                myOpenGLPrimitive = GL.GL_TRIANGLE_FAN;
                break;
            default:
                System.err.println("### WARNING @ Mesh / couldn t identify the primitive type. please refer to the fields MESH_* in the gestalt.Gestalt interface");
                break;
        }
        return myOpenGLPrimitive;
    }

    /**
     * creates a plane that displays a texture on arbitrary texture unit, defined in the texture object.
     *
     * @param theTexture TexturePlugin
     * @param thePosition Vector3f
     * @param theScale Vector3f
     * @return JoglMultiTexPlane
     */
    public static JoglMultiTexPlane createTextureView(TexturePlugin theTexture, Vector3f thePosition, Vector3f theScale) {
        final JoglMultiTexPlane myFBOView = new JoglMultiTexPlane(theTexture.getTextureUnit());
        myFBOView.material().addPlugin(theTexture);
        myFBOView.scale().set(theScale);
        myFBOView.position().set(thePosition);
        myFBOView.origin(SHAPE_ORIGIN_TOP_LEFT);
        return myFBOView;
    }

    /**
     * tries to turn on trail for current context.
     *
     * @param theRenderer BasicRenderer
     * @return Plane
     */
    public static Plane enableTrails(BasicRenderer theRenderer) {
        if (theRenderer.displaycapabilities().antialiasinglevel > 0) {
            System.err.println(
                    "### WARNING @ JoglUtil.enableTrails / using 'antialiasing' might cause the colorbuffer to be cleared automatically. be warned!");
        }

        if (theRenderer.displaycapabilities().synctovblank) {
            System.err.println("### WARNING @ JoglUtil.enableTrails / using 'synctovblank' might cause flickering. be warned!");
        }

        theRenderer.framesetup().colorbufferclearing = false;
        Plane myPlane = theRenderer.drawablefactory().plane();
        myPlane.scale().set(theRenderer.displaycapabilities().width, theRenderer.displaycapabilities().height);
        myPlane.material().color4f().set(1, 0.1f);
        myPlane.material().lit = false;
        myPlane.material().depthtest = false;
        theRenderer.bin(BIN_2D_FOREGROUND).add(myPlane);
        return myPlane;
    }

    public static int _getTextureUnitID(final int theTextureUnitToken) {
        return theTextureUnitToken - GL.GL_TEXTURE0;
    }

    /**
     * transforms opengl texture-id-token to numeric value.
     * example: GL_TEXTURE0 becomes 0, GL_TEXTURE1 becomes 1 ...
     *
     * @param theTextureUnitToken int
     * @return int
     */
    public final static int getTextureUnitID(final int theTextureUnitToken) {
        switch (theTextureUnitToken) {
            case GL.GL_TEXTURE0:
                return 0;
            case GL.GL_TEXTURE1:
                return 1;
            case GL.GL_TEXTURE2:
                return 2;
            case GL.GL_TEXTURE3:
                return 3;
            case GL.GL_TEXTURE4:
                return 4;
            case GL.GL_TEXTURE5:
                return 5;
            case GL.GL_TEXTURE6:
                return 6;
            case GL.GL_TEXTURE7:
                return 7;
            case GL.GL_TEXTURE8:
                return 8;
            case GL.GL_TEXTURE9:
                return 9;
            case GL.GL_TEXTURE10:
                return 10;
            case GL.GL_TEXTURE11:
                return 11;
            case GL.GL_TEXTURE12:
                return 12;
            case GL.GL_TEXTURE13:
                return 13;
            case GL.GL_TEXTURE14:
                return 14;
            case GL.GL_TEXTURE15:
                return 15;
        }
        return UNDEFINED;
    }

    /**
     * apply origin offset to current opengl matrix stack.
     *
     * @param gl GL
     * @param theOrigin int
     */
    public static final void applyOrigin(final GL gl, int theOrigin) {
        switch (theOrigin) {
            case SHAPE_ORIGIN_BOTTOM_LEFT:

                // gl.glTranslatef( 0, 0, 0);
                break;
            case SHAPE_ORIGIN_BOTTOM_RIGHT:
                gl.glTranslatef(-1, 0, 0);
                break;
            case SHAPE_ORIGIN_TOP_LEFT:
                gl.glTranslatef(0, -1, 0);
                break;
            case SHAPE_ORIGIN_TOP_RIGHT:
                gl.glTranslatef(-1, -1, 0);
                break;
            case SHAPE_ORIGIN_CENTERED:
                gl.glTranslatef(-0.5f, -0.5f, 0);
                break;
            case SHAPE_ORIGIN_CENTERED_LEFT:
                gl.glTranslatef(0, -0.5f, 0);
                break;
            case SHAPE_ORIGIN_CENTERED_RIGHT:
                gl.glTranslatef(-1, -0.5f, 0);
                break;
            case SHAPE_ORIGIN_BOTTOM_CENTERED:
                gl.glTranslatef(-0.5f, 0, 0);
                break;
            case SHAPE_ORIGIN_TOP_CENTERED:
                gl.glTranslatef(-0.5f, -1, 0);
                break;
        }
    }

    /**
     * applies the transforms stored in 'transform', 'rotation' and 'scale'
     * as defined in transform mode.
     *
     * @param gl GL
     * @param _myTransformMode int
     * @param transform TransformMatrix4f
     * @param rotation Vector3f
     * @param scale Vector3f
     */
    public static final void applyTransform(final GL gl,
                                            final int theTransformMode,
                                            final TransformMatrix4f theTransform,
                                            final Vector3f theRotation,
                                            final Vector3f theScale) {
        /** @todo we need to remove the z component in 2D mode */
        if (theTransformMode == SHAPE_TRANSFORM_MATRIX
                || theTransformMode == SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
//            /** @todo JSR-231 performance hit? */
//            FloatBuffer myBuffer = FloatBuffer.wrap(theTransform.toArray());
//            gl.glMultMatrixf(myBuffer);
            gl.glMultMatrixf(theTransform.toArray(), 0);
        }

        if (theTransformMode == SHAPE_TRANSFORM_POSITION_AND_ROTATION) {
            /** @todo we need to remove the z component in 2D mode */
            gl.glTranslatef(theTransform.translation.x,
                            theTransform.translation.y,
                            theTransform.translation.z);
        }

        if (theTransformMode == SHAPE_TRANSFORM_POSITION_AND_ROTATION
                || theTransformMode == SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            /** @todo replace with faster 'to degree' calculation */
            if (theRotation.x != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(theRotation.x), 1, 0, 0);
            }
            if (theRotation.y != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(theRotation.y), 0, 1, 0);
            }
            if (theRotation.z != 0.0f) {
                gl.glRotatef((float)Math.toDegrees(theRotation.z), 0, 0, 1);
            }
        }

        /* finally scale the shape */
        gl.glScalef(theScale.x, theScale.y, theScale.z);
    }

    /**
     * applies the transforms stored in 'transform', 'rotation' and 'scale'
     * as defined in transform mode.
     *
     * @param theShape AbstractShape
     * @param theResultPosition Vector3f
     * @return TransformMatrix4f
     */
    public static final TransformMatrix4f applyTransform(final AbstractShape theShape,
                                                         final Vector3f theResultPosition) {
        return applyTransform(theShape.getTransformMode(),
                              theShape.transform(),
                              theShape.rotation(),
                              theShape.scale(),
                              theResultPosition);
    }

    /**
     * applies the transforms stored in 'transform', 'rotation' and 'scale'
     * as defined in transform mode.
     *
     * @param theTransformMode int
     * @param theTransform TransformMatrix4f
     * @param theRotation Vector3f
     * @param theScale Vector3f
     * @param theResultPosition Vector3f
     * @return TransformMatrix4f
     */
    public static final TransformMatrix4f applyTransform(final int theTransformMode,
                                                         final TransformMatrix4f theTransform,
                                                         final Vector3f theRotation,
                                                         final Vector3f theScale,
                                                         final Vector3f theResultPosition) {

        final TransformMatrix4f myResultTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);

        /** @todo we need to remove the z component in 2D mode */
        if (theTransformMode == SHAPE_TRANSFORM_MATRIX
                || theTransformMode == SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            myResultTransform.multiply(theTransform);
        }

        if (theTransformMode == SHAPE_TRANSFORM_POSITION_AND_ROTATION) {
            /** @todo we need to remove the z component in 2D mode */
            final TransformMatrix4f myTranslation = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
            myTranslation.translation.set(theTransform.translation);
            myResultTransform.multiply(myTranslation);
        }

        if (theTransformMode == SHAPE_TRANSFORM_POSITION_AND_ROTATION
                || theTransformMode == SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            final TransformMatrix4f myRotation = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
            myRotation.rotation.setXYZRotation(theRotation);
            myResultTransform.multiply(myRotation);
        }

        /* finally scale the shape */
        final TransformMatrix4f myScale = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myScale.rotation.setScale(theScale);
        myResultTransform.multiply(myScale);

        /* apply transform */
        if (theResultPosition != null) {
            myResultTransform.transform(theResultPosition);
        }

        return myResultTransform;
    }

    /**
     * draws a circle from line segments in opengl.
     *
     * @param gl GL
     * @param thePosition Vector3f
     * @param theRadius float
     */
    public static final void circle(GL gl,
                                    Vector3f thePosition,
                                    float theRadius) {
        gl.glPushMatrix();
        gl.glTranslatef(thePosition.x, thePosition.y, thePosition.z);
        circle(gl, theRadius);
        gl.glPopMatrix();
    }

    /**
     * draws a circle from line segments in opengl.
     *
     * @param gl GL
     * @param theRadius float
     */
    public static final void circle(GL gl,
                                    float theRadius) {
        final float myResolution = TWO_PI / (theRadius / (PI / 4.0f));
        gl.glBegin(GL.GL_LINE_LOOP);
        for (float i = 0; i < TWO_PI; i += myResolution) {
            gl.glVertex3f((float)Math.sin(i) * theRadius, (float)Math.cos(i) * theRadius, 0);
        }
        gl.glEnd();
    }

    /**
     *
     * @param gl GL
     * @param theVertex Vertex3f
     */
    public static final void draw(final GL gl, final Vertex3f theVertex) {
        if (theVertex.normal != null) {
            gl.glNormal3f(theVertex.normal.x, theVertex.normal.y, theVertex.normal.z);
        }
        if (theVertex.texcoord != null) {
            gl.glTexCoord3f(theVertex.texcoord.x, theVertex.texcoord.y, theVertex.texcoord.z);
        }
        if (theVertex.color != null) {
            gl.glColor4f(theVertex.color.r, theVertex.color.g, theVertex.color.b, theVertex.color.a);
        }
        if (theVertex.position != null) {
            gl.glVertex3f(theVertex.position.x, theVertex.position.y, theVertex.position.z);
        }
    }

    /**
     *
     * @param gl GL
     * @param theFOVY float
     * @param theAspect float
     * @param theZNearPlane float
     * @param theZFarPlane float
     * @param theFrustumOffset Vector2f
     */
    public static final void gluPerspective(final GL gl,
                                            final float theFOVY,
                                            final float theAspect,
                                            float theZNearPlane,
                                            float theZFarPlane,
                                            final Vector2f theFrustumOffset) {

        final float myTop = theZNearPlane * (float)Math.tan(theFOVY * PI / 360.0f);
        final float myBottom = -myTop;
        final float myLeft = myBottom * theAspect;
        final float myRight = myTop * theAspect;

        gl.glFrustum(myLeft + theFrustumOffset.x * theAspect * theZNearPlane,
                     myRight + theFrustumOffset.x * theAspect * theZNearPlane,
                     myBottom + theFrustumOffset.y * theZNearPlane,
                     myTop + theFrustumOffset.y * theZNearPlane,
                     theZNearPlane,
                     theZFarPlane);

        JoglUtil.printGLError(gl, "JoglUtil.gluPerspective");
    }

    /**
     * check frame buffer object status.
     *
     * @param gl GL
     * @return boolean
     */
    public static final boolean checkFrameBufferStatus(GL gl) {
        int myStatus;
        myStatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
        switch (myStatus) {
            case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
                return true;
            case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                System.err.println("### ERROR @ FrameBufferObject / GL_FRAMEBUFFER_UNSUPPORTED_EXT found.");
                System.err.println("checkFrameBufferStatus: " + myStatus);
                return false;
            default:
                System.err.println("### ERROR @ FrameBufferObject / will fail on all hardware"); {
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

    public static final boolean printGLError(GL gl, String theLocation) {
        return printGLError(gl, null, theLocation, true);
    }

    public static final boolean printGLError(GL gl, GLU glu, String theLocation) {
        return printGLError(gl, glu, theLocation, true);
    }

    /**
     * print opengl error.
     *
     * @param gl GL
     * @param glu GLU
     * @param theLocation String
     * @param thePrintFlag boolean
     * @return boolean
     */
    public static final boolean printGLError(GL gl,
                                             GLU glu,
                                             String theLocation,
                                             boolean thePrintFlag) {
        int myGLErr;
        boolean myErrorFound = false;

        myGLErr = gl.glGetError();
        while (myGLErr != GL.GL_NO_ERROR) {
            if (thePrintFlag) {
                if (glu != null) {
                    System.err.println("### GL_ERROR @ " + theLocation
                            + " / " + glu.gluErrorString(myGLErr) + " ("
                            + werkzeug.Util.now() + ").");
                } else {
                    System.err.println("### GL_ERROR @ " + theLocation
                            + " / " + myGLErr + " ("
                            + werkzeug.Util.now() + ").");
                }
            }
            myErrorFound = true;
            myGLErr = gl.glGetError();
        }
        return myErrorFound;
    }

    /**
     * print availabe opengl extensions.
     *
     * @param gl GL
     */
    public static final void printExtensions(GL gl) {
        final String[] myExtensions = gl.glGetString(GL.GL_EXTENSIONS).split(" ");
        int i = 0;
        System.out.println("-------------------------------------------------------");
        System.out.println("GL_EXTENSIONS:");
        while (i < myExtensions.length) {
            System.out.println(myExtensions[i++]);
        }
        System.out.println("-------------------------------------------------------");
    }

    /**
     * test availability of an opengl extension.
     *
     * @param gl GL
     * @param theExtension String
     * @return boolean
     */
    public static final boolean testExtensionAvailability(GL gl, String theExtension) {
        if (!gl.isExtensionAvailable(theExtension)) {
            System.err.println("### WARNING couldn t find extension: " + theExtension);
            return false;
        }
        return true;
    }

    public static final GL getGL(GLContext theRenderContext) {
        return theRenderContext.gl;
    }

    public static final GLU getGLU(GLContext theRenderContext) {
        return theRenderContext.glu;
    }


    /* shapes */
    public static Mesh getCubeMesh() {
        final float[] TEX_COORDS = {
            // Front Face
            0, 0,
            1, 0,
            1, 1,
            0, 0,
            1, 1,
            0, 1,
            // Back Face
            0, 0,
            1, 0,
            1, 1,
            0, 0,
            1, 1,
            0, 1,
            // Top Face
            0, 0,
            1, 0,
            1, 1,
            0, 0,
            1, 1,
            0, 1,
            // Bottom Face
            0, 0,
            1, 0,
            1, 1,
            0, 0,
            1, 1,
            0, 1,
            // Right face
            0, 0,
            1, 0,
            1, 1,
            0, 0,
            1, 1,
            0, 1,
            // Left Face
            0, 0,
            1, 0,
            1, 1,
            0, 0,
            1, 1,
            0, 1
        };

        final float[] NORMALS = {
            // Front Face
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            // Back Face
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            // Top Face
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            // Bottom Face
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            // Right face
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            // Left Face
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0
        };

        final float[] VERTICES = {
            // Front Face
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            // Back Face
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            // Top Face
            -0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            // Bottom Face
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            // Right face
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            // Left Face
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f
        };
        return new Mesh(VERTICES, 3,
                        null, 4,
                        TEX_COORDS, 2,
                        NORMALS,
                        MESH_TRIANGLES);
    }

    /* VBO ops */
    public static FloatBuffer mapBuffer(final GL gl, final int thePointer, final int theAccessMode) {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, thePointer);
        ByteBuffer myByteBuffer = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, theAccessMode);
        if (myByteBuffer != null) {
            myByteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer myVertexBuffer = myByteBuffer.asFloatBuffer();
            return myVertexBuffer;
        } else {
            return null;
        }
    }

    public static void unmapBuffer(final GL gl, final int thePointer) {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, thePointer);
        gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
    }

    public static boolean checkExtension(final GL gl,
                                         final String theExtension,
                                         final String theLocations) {
        final boolean myState = gl.isExtensionAvailable(theExtension);
        if (!myState) {
            System.err.println("### ERROR @" + theLocations + " / extension not supported '" + theExtension + "'");
        }
        return myState;
    }

    public static int queryFBOTypeState(GL gl, JoglFrameBufferObject theFBO) {
        System.out.print("### querying FBO (" + theFBO.name + ") state: ");

        int[] tmp = new int[1];
        gl.glGetFramebufferAttachmentParameterivEXT(GL.GL_FRAMEBUFFER_EXT,
                                                    theFBO.getBufferInfo().attachment_point,
                                                    GL.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE_EXT,
                                                    tmp, 0);
        switch (tmp[0]) {
            case GL.GL_RENDERBUFFER_EXT:
                System.out.println("RENDERBUFFER_EXT");
                break;
            case GL.GL_NONE:
                System.out.println("NONE");
                break;
            case GL.GL_TEXTURE:
                System.out.println("TEXTURE");
                break;
        }
        JoglUtil.printGLError(gl, "JoglUtil.queryFBOState");

        return tmp[0];
    }

    public static void drawQuadFragments(final GL gl,
                                         final QuadFragment[] pQuadFragments,
                                         final boolean pDisableTextureCoordinates) {
        /* draw quads */
        if (pQuadFragments != null) {
            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int i = 0; i < pQuadFragments.length; ++i) {
                gl.glNormal3f(pQuadFragments[i].normal.x, pQuadFragments[i].normal.y, pQuadFragments[i].normal.z);
                if (pQuadFragments[i].colorB != null) {
                    gl.glColor4f(pQuadFragments[i].colorB.r, pQuadFragments[i].colorB.g, pQuadFragments[i].colorB.b, pQuadFragments[i].colorB.a);
                }
                if (!pDisableTextureCoordinates) {
                    gl.glTexCoord2f(pQuadFragments[i].texcoordB.x, pQuadFragments[i].texcoordB.y);
                }
                gl.glVertex3f(pQuadFragments[i].pointB.x, pQuadFragments[i].pointB.y, pQuadFragments[i].pointB.z);
                if (pQuadFragments[i].colorA != null) {
                    gl.glColor4f(pQuadFragments[i].colorA.r, pQuadFragments[i].colorA.g, pQuadFragments[i].colorA.b, pQuadFragments[i].colorA.a);
                }
                if (!pDisableTextureCoordinates) {
                    gl.glTexCoord2f(pQuadFragments[i].texcoordA.x, pQuadFragments[i].texcoordA.y);
                }
                gl.glVertex3f(pQuadFragments[i].pointA.x, pQuadFragments[i].pointA.y, pQuadFragments[i].pointA.z);
            }
            gl.glEnd();
        }
    }
}
