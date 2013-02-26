/*
 * Lesson 03
 * draw colored polygons.
 */


package gestalt.demo.nehe;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.render.Drawable;
import gestalt.render.bin.RenderBin;


public class Lesson03
    extends AnimatorRenderer {

    public void setup() {
        RenderBin myRenderBin = new RenderBin(2);
        setBinRef(myRenderBin);
        myRenderBin.add(new MyDrawable());
    }


    public static void main(String[] arg) {
        new Lesson03().init();
    }


    private class MyDrawable
        implements Drawable {

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


        public boolean isActive() {
            return true;
        }


        public void init(GL gl, GLU glu) {
            gl.glViewport(0,
                          0,
                          _myWidth,
                          _myHeight);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f,
                               (float) _myWidth / (float) _myHeight,
                               1.0,
                               20.0);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
        }


        public void display(GL gl, GLU glu) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            gl.glTranslatef( -1.5f, 0.0f, -6.0f);
            gl.glBegin(GL.GL_TRIANGLES); // Drawing Using Triangles
            gl.glColor3f(1.0f, 0.0f, 0.0f); // Set the current drawing color to red
            gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top
            gl.glColor3f(0.0f, 1.0f, 0.0f); // Set the current drawing color to green
            gl.glVertex3f( -1.0f, -1.0f, 0.0f); // Bottom Left
            gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the current drawing color to blue
            gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom Right
            gl.glEnd(); // Finished Drawing The Triangle
            gl.glTranslatef(3.0f, 0.0f, 0.0f);
            gl.glBegin(GL.GL_QUADS); // Draw A Quad
            gl.glColor3f(0.5f, 0.5f, 1.0f); // Set the current drawing color to light blue
            gl.glVertex3f( -1.0f, 1.0f, 0.0f); // Top Left
            gl.glVertex3f(1.0f, 1.0f, 0.0f); // Top Right
            gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom Right
            gl.glVertex3f( -1.0f, -1.0f, 0.0f); // Bottom Left
            gl.glEnd(); // Done Drawing The Quad
            gl.glFlush();
        }


        public void add(Drawable theDrawable) {
        }


        public float getSortValue() {
            return 0.0F;
        }


        public void setSortValue(float theSortValue) {
        }


        public float[] getSortData() {
            return null;
        }


        public boolean isSortable() {
            return false;
        }
    }
}
