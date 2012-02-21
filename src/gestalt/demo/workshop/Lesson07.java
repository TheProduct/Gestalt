/*
 * Lesson 07
 * using textures.
 *
 * in this lesson the 'bitmapfactory' is introduced. it supplies methods to
 * read and create 'bitmaps'. 'bitmaps' can be created from imagefiles, fonts,
 * movies or from scratch at runtime. 'bitmaps' are simple data structures that
 * can be loaded into a 'texture'.
 *
 * gestalt supplies an 'event' object which handles key and mouse inputs.
 */


package gestalt.demo.workshop;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;

import data.Resource;


public class Lesson07
    extends AnimatorRenderer {

    public void setup() {
        RenderBin myRenderBin = new RenderBin(2);
        setBinRef(myRenderBin);
        myRenderBin.add(event());
        myRenderBin.add(new MyDrawable());
        framerate(100);
    }


    public static void main(String[] arg) {
        new Lesson07().init();
    }


    private class MyDrawable
        extends AbstractDrawable {

        private boolean isInitialized = false;

        private int _myWidth;

        private int _myHeight;

        private float[] LightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};

        private float[] LightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};

        private float[] LightPosition = {0.0f, 0.0f, 2.0f, 1.0f};

        private boolean light;

        private boolean lp;

        private boolean fp;

        private float xrot;

        private float yrot;

        private float xspeed;

        private float yspeed;

        private float z = -5.0f;

        private int filter;

        private TexturePlugin[] texture = new TexturePlugin[3];

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

            gl.glShadeModel(GL.GL_SMOOTH);
            gl.glEnable(GL.GL_DEPTH_TEST);
            /** @todo JSR-231 -- added 0 */
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, LightAmbient, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, LightDiffuse, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, LightPosition, 0);
            gl.glEnable(GL.GL_LIGHT1);

            ByteBitmap myBitmap = Bitmaps.getBitmap(Resource.getStream("demo/nehe/lesson07/crate.png"));
            myBitmap = ImageUtil.flipBitmap(myBitmap);

            /* near filtering */
            texture[0] = new TexturePlugin();
            texture[0].setFilterType(TEXTURE_FILTERTYPE_NEAREST);
            texture[0].load(myBitmap);
            texture[0].update(gl, glu);
            /* linear filtering */
            texture[1] = new TexturePlugin();
            texture[1].setFilterType(TEXTURE_FILTERTYPE_LINEAR);
            texture[1].load(myBitmap);
            texture[1].update(gl, glu);
            /* mipmap filtering */
            texture[2] = new TexturePlugin();
            texture[2].setFilterType(TEXTURE_FILTERTYPE_MIPMAP);
            texture[2].load(myBitmap);
            texture[2].update(gl, glu);
        }


        public void display(GL gl, GLU glu) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            gl.glLoadIdentity();
            gl.glTranslatef(0.0f, 0.0f, z);

            gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);

            texture[filter].enable(gl);
            texture[filter].bind(gl);

            gl.glBegin(GL.GL_QUADS);
            // Front Face
            gl.glNormal3f(0.0f, 0.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            // Back Face
            gl.glNormal3f(0.0f, 0.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            // Top Face
            gl.glNormal3f(0.0f, 1.0f, 0.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            // Bottom Face
            gl.glNormal3f(0.0f, -1.0f, 0.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            // Right face
            gl.glNormal3f(1.0f, 0.0f, 0.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            // Left Face
            gl.glNormal3f( -1.0f, 0.0f, 0.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glEnd();

            xrot += xspeed;
            yrot += yspeed;

            /* toggle light */
            if (event().keyCode == KEYCODE_L & !lp) {
                lp = true;
                light = !light;
                if (!light) {
                    gl.glDisable(GL.GL_LIGHTING);
                }
                if (light) {
                    gl.glEnable(GL.GL_LIGHTING);
                }
            }
            if (event().keyCode != KEYCODE_L) {
                lp = false;
            }

            /* toggle filtering */
            if (event().keyCode == KEYCODE_F && !fp) {
                fp = true;
                filter += 1;
                if (filter > 2) {
                    filter = 0;
                }
            }
            if (event().keyCode != KEYCODE_F) {
                fp = false;
            }

            if (event().keyCode == KEYCODE_PAGE_UP) {
                z -= 0.02f;
            }
            if (event().keyCode == KEYCODE_PAGE_DOWN) {
                z += 0.02f;
            }
            if (event().keyCode == KEYCODE_UP) {
                xspeed -= 0.01f;
            }
            if (event().keyCode == KEYCODE_DOWN) {
                xspeed += 0.01f;
            }
            if (event().keyCode == KEYCODE_RIGHT) {
                yspeed += 0.01f;
            }
            if (event().keyCode == KEYCODE_LEFT) {
                yspeed -= 0.01f;
            }
        }
    }
}
