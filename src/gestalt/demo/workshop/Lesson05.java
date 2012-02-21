/*
 * Lesson 05
 * rotate 3D shapes.
 */


package gestalt.demo.workshop;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;


public class Lesson05
    extends AnimatorRenderer {

    public void setup() {
        RenderBin myRenderBin = new RenderBin(1);
        setBinRef(myRenderBin);
        myRenderBin.add(new MyDrawable());
        framerate(UNDEFINED);
    }


    public static void main(String[] arg) {
        new Lesson05().init();
    }


    private class MyDrawable
        extends AbstractDrawable {

        private boolean isInitialized = false;

        private int _myWidth;

        private int _myHeight;

        private float rquad = 0.0f;

        private float rtri = 0.0f;

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
            gl.glEnable(GL.GL_DEPTH_TEST);
        }


        public void display(GL gl, GLU glu) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            gl.glLoadIdentity();
            gl.glTranslatef( -1.5f, 0.0f, -6.0f);
            gl.glRotatef(rtri, 0.0f, 1.0f, 0.0f);
            gl.glBegin(GL.GL_TRIANGLES);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Front)
            gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
            gl.glVertex3f( -1.0f, -1.0f, 1.0f); // Left Of Triangle (Front)
            gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
            gl.glVertex3f(1.0f, -1.0f, 1.0f); // Right Of Triangle (Front)
            gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
            gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Right)
            gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
            gl.glVertex3f(1.0f, -1.0f, 1.0f); // Left Of Triangle (Right)
            gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
            gl.glVertex3f(1.0f, -1.0f, -1.0f); // Right Of Triangle (Right)
            gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
            gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Back)
            gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
            gl.glVertex3f(1.0f, -1.0f, -1.0f); // Left Of Triangle (Back)
            gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
            gl.glVertex3f( -1.0f, -1.0f, -1.0f); // Right Of Triangle (Back)
            gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
            gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Left)
            gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
            gl.glVertex3f( -1.0f, -1.0f, -1.0f); // Left Of Triangle (Left)
            gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
            gl.glVertex3f( -1.0f, -1.0f, 1.0f); // Right Of Triangle (Left)
            gl.glEnd();

            gl.glLoadIdentity();
            gl.glTranslatef(1.5f, 0.0f, -6.0f);
            gl.glRotatef(rquad, 1.0f, 1.0f, 1.0f);

            gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
            gl.glVertex3f( -1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
            gl.glVertex3f( -1.0f, 1.0f, 1.0f); // Bottom Left Of The Quad (Top)
            gl.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right Of The Quad (Top)

            gl.glColor3f(1.0f, 0.5f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f); // Top Right Of The Quad (Bottom)
            gl.glVertex3f( -1.0f, -1.0f, 1.0f); // Top Left Of The Quad (Bottom)
            gl.glVertex3f( -1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
            gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Bottom)

            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)
            gl.glVertex3f( -1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Front)
            gl.glVertex3f( -1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Front)
            gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Front)

            gl.glColor3f(1.0f, 1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Back)
            gl.glVertex3f( -1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
            gl.glVertex3f( -1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Back)
            gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Back)

            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Left)
            gl.glVertex3f( -1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Left)
            gl.glVertex3f( -1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
            gl.glVertex3f( -1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Left)

            gl.glColor3f(1.0f, 0.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Right)
            gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Right)
            gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Right)
            gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Right)
            gl.glEnd();

            rtri += 0.2f;
            rquad += 0.15f;
        }
    }
}
