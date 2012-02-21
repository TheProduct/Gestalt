/*
 * Lesson 02
 * draw the first polygons.
 *
 * in this lesson the 'drawable' interface is introduced by the class
 * 'AbstractDrawable'. a 'drawable' is the most basic element that can be stored
 * in a gestalt 'bin'. the 'add' method of a 'bin' is used to store a 'drawable'
 * in a 'bin'.
 *
 * a 'bin' is a container which stores, and in time draws the 'drawable' by
 * calling its 'draw' method and passing the current opengl context. note how
 * to get the 'gl' reference from the opengl context.
 *
 * the 'setBin' method replaces the default 'bin' setup.
 *
 * note how the 'setup' method is used instead of the constructor. the reason
 * for this is that after the renderer has been initialized several other setups
 * are being performed which could override things set in the constructor.
 */


package gestalt.demo.workshop;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;


public class Lesson02
    extends AnimatorRenderer {

    public static void main(String[] arg) {
        new Lesson02().init();
    }


    private Lesson02() {
        /* never use the constructor to setup the renderer */
    }


    public void setup() {
        RenderBin myRenderBin = new RenderBin(1);
        setBinRef(myRenderBin);
        myRenderBin.add(new MyDrawable());
    }


    private class MyDrawable
        extends AbstractDrawable {

        private boolean isInitialized = false;

        private int _myWidth;

        private int _myHeight;

        public void draw(final GLContext theContext) {
            GL gl = (  theContext).gl;
            GLU glu = (  theContext).glu;

            if (!isInitialized) {
                _myWidth = theContext.displaycapabilities.width;
                _myHeight = theContext.displaycapabilities.height;
                isInitialized = true;
                init(gl, glu);
            } else {
                display(gl, glu);
            }
        }


        public void init(GL gl, GLU glu) {
            gl.glViewport(0, 0, _myWidth, _myHeight);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f,
                               (float) _myWidth / (float) _myHeight,
                               1.0,
                               20.0);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

        }


        public void display(GL gl, GLU glu) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();

            gl.glTranslatef( -1.5f, 0.0f, -6.0f);

            gl.glBegin(GL.GL_TRIANGLES);
            gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top
            gl.glVertex3f( -1.0f, -1.0f, 0.0f); // Bottom Left
            gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom Right
            gl.glEnd();

            gl.glTranslatef(3.0f, 0.0f, 0.0f);

            gl.glBegin(GL.GL_QUADS);
            gl.glVertex3f( -1.0f, 1.0f, 0.0f); // Top Left
            gl.glVertex3f(1.0f, 1.0f, 0.0f); // Top Right
            gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom Right
            gl.glVertex3f( -1.0f, -1.0f, 0.0f); // Bottom Left
            gl.glEnd();
        }
    }
}
