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


package gestalt.extension.picking;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import static gestalt.Gestalt.*;
import gestalt.context.GLContext;
import gestalt.extension.picking.Pickable;
import gestalt.extension.picking.Picker;
import gestalt.context.GLContext;
import gestalt.input.EventHandler;
import gestalt.util.JoglUtil;

import com.sun.opengl.util.BufferUtil;


public class JoglPicker
    extends Picker {

    private int _my2DBufferDepth;

    public JoglPicker() {
        _my2DBufferDepth = 20;
    }


    public void draw(GLContext theContext) {
        GLContext myJoglContext =  theContext;
        selectOrthoPickables(myJoglContext);
        selectSpatialPickables(myJoglContext);
    }


    protected void selectSpatialPickables(GLContext theRenderContext) {
        GL gl = theRenderContext.gl;
        GLU glu = theRenderContext.glu;

        ByteBuffer directBuffer = BufferUtil.newByteBuffer(128);
        ByteOrder.nativeOrder();
        IntBuffer myIntBuffer = directBuffer.asIntBuffer();

        float myMouseX = theRenderContext.event.mouseX;
        float myMouseY = theRenderContext.event.mouseY;

        /**
         * @todo
         * this part is probably broken if the camera viewport doesn t
         * equal the screen dimensions.
         */
        if (EventHandler.EVENT_CENTER_MOUSE) {
            myMouseX += theRenderContext.displaycapabilities.width / 2;
            myMouseY += theRenderContext.displaycapabilities.height / 2;
        }

        if (!EventHandler.EVENT_FLIP_MOUSE_Y) {
            myMouseY = theRenderContext.displaycapabilities.height - myMouseY;
        }

        int[] myViewPort = new int[4];
        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_VIEWPORT, myViewPort, 0);
        gl.glSelectBuffer(myIntBuffer.capacity(), myIntBuffer);
        gl.glRenderMode(GL.GL_SELECT);
        gl.glInitNames();
        gl.glPushName( -1);

        if (theRenderContext.camera != null) {
            theRenderContext.camera.draw(theRenderContext);
        }

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        /** @todo JSR-231 -- added 0 */
        glu.gluPickMatrix(myMouseX, myMouseY, 2f, 2f, myViewPort, 0);
        /** @todo do we need the camera s culling block as well? */
        if (theRenderContext.camera != null) {
            JoglUtil.gluPerspective(gl,
                                    theRenderContext.camera.fovy,
                                    (float) (theRenderContext.displaycapabilities.width) /
                                    (float) (theRenderContext.displaycapabilities.height),
                                    theRenderContext.camera.nearclipping,
                                    theRenderContext.camera.farclipping,
                                    theRenderContext.camera.frustumoffset);
        }
        gl.glMatrixMode(GL.GL_MODELVIEW);
        drawPickables(bin[PICKING_BIN_3D].getDataRef(), theRenderContext);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();

        int myHits = 0;
        myHits = gl.glRenderMode(GL.GL_RENDER);
        processPickedObjects(bin[PICKING_BIN_3D].getDataRef(), myHits,
                             myIntBuffer);
    }


    protected void selectOrthoPickables(GLContext theRenderContext) {
        GL gl = theRenderContext.gl;
        GLU glu = theRenderContext.glu;

        ByteBuffer directBuffer = BufferUtil.newByteBuffer(128);
        ByteOrder.nativeOrder();
        IntBuffer myIntBuffer = directBuffer.asIntBuffer();

        int myHits = 0;
        int[] myViewPort = new int[4];

        float myMouseX = theRenderContext.event.mouseX;
        float myMouseY = theRenderContext.event.mouseY;

        /**
         * @todo
         * this part is probably broken if the camera viewport doesn t
         * equal the screen dimensions.
         */
        if (EventHandler.EVENT_CENTER_MOUSE) {
            myMouseX += theRenderContext.displaycapabilities.width / 2;
            myMouseY += theRenderContext.displaycapabilities.height / 2;
        }

        if (!EventHandler.EVENT_FLIP_MOUSE_Y) {
            myMouseY = theRenderContext.displaycapabilities.height - myMouseY;
        }

        /** @todo JSR-231 -- added 0 */
        gl.glGetIntegerv(GL.GL_VIEWPORT, myViewPort, 0);
        gl.glSelectBuffer(myIntBuffer.capacity(), myIntBuffer);
        gl.glRenderMode(GL.GL_SELECT);
        gl.glInitNames();
        gl.glPushName( -1);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        /** @todo JSR-231 -- added 0 */
        glu.gluPickMatrix(myMouseX, myMouseY, 2f, 2f, myViewPort, 0);
        if (theRenderContext.camera != null) {
            JoglUtil.gluPerspective(gl, CAMERA_A_HANDY_ANGLE,
                                    (float) (theRenderContext.displaycapabilities.width)
                                    / (float) (theRenderContext.displaycapabilities.height),
                                    theRenderContext.displaycapabilities.height - _my2DBufferDepth,
                                    theRenderContext.displaycapabilities.height + _my2DBufferDepth,
                                    theRenderContext.camera.frustumoffset);
        }
        gl.glMatrixMode(GL.GL_MODELVIEW);
        drawPickables(bin[PICKING_BIN_2D].getDataRef(), theRenderContext);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();

        myHits = gl.glRenderMode(GL.GL_RENDER);
        processPickedObjects(bin[PICKING_BIN_2D].getDataRef(), myHits,
                             myIntBuffer);
    }


    protected void drawPickables(Pickable[] thePickables,
                                 GLContext theContext) {
        if (thePickables != null) {
            for (int i = 0; i < thePickables.length; ++i) {
                if (thePickables[i] != null) {
                    int myName = i + 1;
                    theContext.gl.glLoadName(myName);
                    thePickables[i].pickDraw(theContext);
                }
            }
        }
    }


    private void processPickedObjects(Pickable[] thePickables,
                                      int theHits,
                                      IntBuffer theIntBuffer) {
        if (thePickables != null) {
            if (theHits > 0) {
                int[] myPickedObjects = new int[thePickables.length];
                int myArrayIndex = 0;
                for (int j = 0; j < theIntBuffer.limit(); j += 4) {
                    //                    int myRefDepth = theIntBuffer.get(j + 2);
                    int myRefObject = theIntBuffer.get(j + 3);
                    if (myRefObject != 0) {
                        myPickedObjects[myArrayIndex] = myRefObject;
                        myArrayIndex++;
                    }
                }
                myArrayIndex = 0;
                for (int i = 0; i < thePickables.length; ++i) {
                    if (thePickables[i] != null) {
                        boolean isInPickedArray = false;
                        int myPickableIndex = 0;
                        for (int j = 0; j < myPickedObjects.length; j++) {
                            myPickableIndex = myPickedObjects[j];
                            if (myPickableIndex != 0) {
                                myPickableIndex -= 1;
                                if (i == myPickableIndex) {
                                    isInPickedArray = true;
                                }
                            }
                        }
                        if (isInPickedArray) {
                            if (thePickables[i].isPicked()) {
                                thePickables[i].mouseWithin();
                            } else {
                                thePickables[i].mouseEnter();
                            }
                        } else {
                            if (thePickables[i].isPicked()) {
                                thePickables[i].mouseLeave();
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < thePickables.length; ++i) {
                    if (thePickables[i] != null) {
                        if (thePickables[i].isPicked()) {
                            thePickables[i].mouseLeave();
                        }
                    }
                }
            }
        }
    }
}
