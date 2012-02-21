/*
 * Lesson 06
 * using textures.
 *
 * in this lesson the 'bitmapfactory' is introduced. it supplies methods to
 * read and create 'bitmaps'. 'bitmaps' can be created from imagefiles, fonts,
 * movies or from scratch at runtime. 'bitmaps' are simple data structures that
 * can be loaded into a 'texture'. 'textures' are created by the
 * 'texturemanager'. the 'texturemanager' is responsable for managing all
 * 'textures' in gestalt.
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


public class Lesson06
    extends AnimatorRenderer {

    public void setup() {
        RenderBin myRenderBin = new RenderBin(2);
        setBinRef(myRenderBin);
        /**
         * texturemanager needs to be added before init and display,
         * otherwise the calls to texturemanager would go into nirvana.
         */
        myRenderBin.add(event());
        myRenderBin.add(new MyDrawable());
        framerate(100);
    }


    public static void main(String[] arg) {
        new Lesson06().init();
    }


    private class MyDrawable
        extends AbstractDrawable {

        private boolean isInitialized = false;

        private int _myWidth;

        private int _myHeight;

        private TexturePlugin texture;

        private float xrot;

        private float yrot;

        private float zrot;

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

            gl.glEnable(GL.GL_DEPTH_TEST);

            ByteBitmap myBitmap = Bitmaps.getBitmap(Resource.getStream("demo/nehe/lesson06/NeHe.png"));
            myBitmap = ImageUtil.flipBitmap(myBitmap);

            texture = new TexturePlugin();
            texture.setFilterType(TEXTURE_FILTERTYPE_NEAREST);
            texture.load(myBitmap);
            texture.update(gl, glu);
            texture.enable(gl);
        }


        public void display(GL gl, GLU glu) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            gl.glLoadIdentity();
            gl.glTranslatef(0.0f, 0.0f, -5.0f);

            gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

            texture.bind(gl);

            gl.glBegin(GL.GL_QUADS);
            // Front Face
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            // Back Face
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            // Top Face
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            // Bottom Face
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            // Right face
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            // Left Face
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glEnd();

            xrot += 0.3f;
            yrot += 0.2f;
            zrot += 0.4f;
        }
    }
}
