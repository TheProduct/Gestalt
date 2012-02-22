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



package gestalt.demo.advanced;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;


/**
 * gestalt uses a 'bin' concept to organize the rendering of 'drawables'. a bin
 * is a container that collects drawables. all bins derive from the
 * 'AbstractBin'.<br/>
 * <br/>
 * currently there are 'renderbins', 'shapebins' and 'bilateralbins'.<br/>
 * 'renderbins' are targeted to simply handle 'drawables' without any additional
 * functionality.<br/>
 * <br/>
 * 'shapebins' extend this functionality by adding sorting algorithms.<br/>
 * 'bilateralbins' are used to store transparent and nontransparent drawables.
 * thus it is internally divided into two areas, whereas the added drawables are
 * distributed by the value of their transparent flag in the material. this bin
 * also provides the functionality of sorting all transparent drawables.<br/>
 * <br/>
 * gestalt initializes a default setup of renderbins.
 */

public class UsingCustomRenderPipeline
    extends AnimatorRenderer {

    public void setup() {
        /*
         * create a new renderbin with space for two drawables. the first added
         * drawable handles the framesetup, the second one is a custom drawable
         * that draws quads on the screen. after adding the drawables the
         * ppublic field 'bin' is set to the newly created renderbin.
         */
        RenderBin myRenderBin = new RenderBin(2);
        myRenderBin.add(new MyFrameSetup());
        myRenderBin.add(new MyDrawable());
        setBinRef(myRenderBin);
    }


    private class MyFrameSetup
        extends AbstractDrawable {

        public void draw(final GLContext theRenderContext) {
            final GL gl = (  theRenderContext).gl;
            GLU glu = (  theRenderContext).glu;
            /* setup gl */
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(CAMERA_A_HANDY_ANGLE,
                               (float) (theRenderContext.displaycapabilities.width) /
                               (float) (theRenderContext.displaycapabilities.height),
                               1, 2000);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glEnable(GL.GL_BLEND);
            gl.glDisable(GL.GL_DEPTH_TEST);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        }
    }


    private class MyDrawable
        extends AbstractDrawable {

        private float _myCounter;

        public void draw(final GLContext theRenderContext) {
            final GL gl = (  theRenderContext).gl;
            /* clear screen */
            gl.glColor4f(0, 0, 0, 0);
            gl.glPushMatrix();
            gl.glTranslatef(0, 0, -theRenderContext.displaycapabilities.height);
            gl.glScalef(theRenderContext.displaycapabilities.width,
                        theRenderContext.displaycapabilities.height,
                        1);
            gl.glBegin(GL.GL_QUADS);
            gl.glVertex3f( -1, -1, 0);
            gl.glVertex3f(1, -1, 0);
            gl.glVertex3f(1, 1, 0);
            gl.glVertex3f( -1, 1, 0);
            gl.glEnd();
            gl.glPopMatrix();
            /* draw shapes */
            gl.glTranslatef(0, 0, -80);
            gl.glRotatef(_myCounter, 0.6f + _myCounter % 0.5f, 0.4f + _myCounter % 0.3f, 0);
            gl.glRotatef(_myCounter * 0.3f, 0, 0, 1);
            final int myNumberOfShapes = 2000;
            for (int i = 0; i < myNumberOfShapes; ++i) {
                gl.glRotatef(0.1f, 0.5f, 0.5f, 0);
                gl.glTranslatef(0, i % 0.015f, i % -0.025f);
                gl.glColor4f(1, 1, 1, 0.1f * i / (float) myNumberOfShapes);
                gl.glBegin(GL.GL_QUADS);
                gl.glVertex3f( -1, -1, 0);
                gl.glVertex3f(1, -1, 0);
                gl.glVertex3f(1, 1, 0);
                gl.glVertex3f( -1, 1, 0);
                gl.glEnd();
            }
            _myCounter += 2;
        }
    }


    public static void main(String[] arg) {
        new UsingCustomRenderPipeline().init();
    }
}
