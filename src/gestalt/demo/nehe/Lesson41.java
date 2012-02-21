/*
 * Lesson 41
 * volumetric fog.
 */


package gestalt.demo.nehe;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;
import gestalt.material.texture.Bitmaps;

import data.Resource;


public class Lesson41
    extends AnimatorRenderer {

    float[] fogColor = {0.6f, 0.3f, 0.0f, 1.0f}; // Fog Colour

    float camz; // Camera Z Depth

    float camx; // Camera Z Depth

    TexturePlugin texture; // One Texture (For The Walls)

    public void setup() {
        RenderBin myRenderBin = new RenderBin(3);
        setBinRef(myRenderBin);
        myRenderBin.add(event());
        myRenderBin.add(new Init());
        myRenderBin.add(new Display());
        framerate(30);
    }


    public static void main(String[] arg) {
        new Lesson41().init();
    }


    public void loop(float theDeltaTime) {

        if (event().keyDown) {
            if (event().key == 'w') {
                camz += 1.0f; // Move camera in
            }

            if (event().key == 's') {
                camz -= 1.0f; // Move camera out
            }

            if (event().key == 'a') {
                camx += 1.0f; // Move camera left
            }

            if (event().key == 'd') {
                camx -= 1.0f; // Move camera right
            }
        }
    }


    private class Init
        extends AbstractDrawable {

        private boolean _myIsActive;

        private Init() {
            _myIsActive = true;
        }


        public void draw(final GLContext theContext) {
            GL gl = (  theContext).gl;
            GLU glu = (  theContext).glu;

            if (!gl.isExtensionAvailable("GL_EXT_fog_coord")) {
                System.out.println("### INFO / GL_EXT_fog_coord not supported.");
            }

            gl.glViewport(0,
                          0,
                          theContext.displaycapabilities.width,
                          theContext.displaycapabilities.height);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f,
                               (float) theContext.displaycapabilities.width /
                               (float) theContext.displaycapabilities.height,
                               1.0,
                               2000.0);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
            gl.glClearDepth(1.0f); // Depth Buffer Setup
            gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing
            gl.glEnable(GL.GL_DEPTH_TEST); // Enable Depth Testing
            gl.glShadeModel(GL.GL_SMOOTH); // Select Smooth Shading
            gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Set Perspective Calculations To Most Accurate

            gl.glDisable(GL.GL_TEXTURE_2D);

            /* Set Up Fog */
            gl.glEnable(GL.GL_FOG); // Enable Fog
            gl.glFogi(GL.GL_FOG_MODE, GL.GL_LINEAR); // Fog Fade Is Linear
            /** @todo JSR-231 -- added 0 */
            gl.glFogfv(GL.GL_FOG_COLOR, fogColor, 0); // Set The Fog Color
            gl.glFogf(GL.GL_FOG_START, 1.0f); // Set The Fog Start
            gl.glFogf(GL.GL_FOG_END, 0.0f); // Set The Fog End
            gl.glHint(GL.GL_FOG_HINT, GL.GL_NICEST); // Per-Pixel Fog Calculation
            gl.glFogi(GL.GL_FOG_COORDINATE_SOURCE_EXT, GL.GL_FOG_COORDINATE_EXT); // Set Fog Based On Vertice Coordinates

            camz = -19.0f; // Set Camera Z Position To -19.0f
            camx = 0.0f; // Set Camera X Position To 0.0f

            texture = new TexturePlugin();
            texture.load(Bitmaps.getBitmap(Resource.getPath("demo/nehe/lesson41/wall.png")));
            texture.update(gl, glu);
            texture.enable(gl);

            _myIsActive = false;
        }


        public boolean isActive() {
            return _myIsActive;
        }
    }


    private class Display
        extends AbstractDrawable {

        public void draw(final GLContext theContext) {
            GL gl = (  theContext).gl;

            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear Screen And Depth Buffer
            gl.glLoadIdentity(); // Reset The Modelview Matrix

            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTextureID());

            gl.glTranslatef(camx, 0.0f, camz); // Move To Our Camera Z/X Position

            // glFogCoordEXT is very similar to glVertex3f. If you understand
            // the way vertexs are applied in OpenGL, you will not have any dificulty
            // understanding glFogCoordEXT.

            // In this tutorial we are applyng the fog in a corridor, so the fog
            // goes from the less density (the minor z) to a bigger density (the biggest z).
            // If you check the fog_start and fog_eng, it's 0 and 1.

            // So, we will pass to the function glFogCoordEXT, the fog value corresponding
            // with the glVertex3f value.If we are drawing a quad from z minus to z major,
            // we do exactly the same with glFogCoord.

            // For example, in the first quad, is vertex coordinates in the Z field are all
            // -15.0f. So we want the fog to completely fill this quad, so we assign 0 to all
            // the glFogCoordExt.

            gl.glBegin(GL.GL_QUADS); // Back Wall
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -2.5f, -2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(2.5f, -2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(2.5f, 2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -2.5f, 2.5f, -15.0f);
            gl.glEnd();

            gl.glBegin(GL.GL_QUADS); // Floor
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -2.5f, -2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(2.5f, -2.5f, -15.0f);
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(2.5f, -2.5f, 15.0f);
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -2.5f, -2.5f, 15.0f);
            gl.glEnd();

            gl.glBegin(GL.GL_QUADS); // Roof
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -2.5f, 2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(2.5f, 2.5f, -15.0f);
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(2.5f, 2.5f, 15.0f);
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -2.5f, 2.5f, 15.0f);
            gl.glEnd();

            gl.glBegin(GL.GL_QUADS); // Right Wall
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(2.5f, -2.5f, 15.0f);
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(2.5f, 2.5f, 15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(2.5f, 2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(2.5f, -2.5f, -15.0f);
            gl.glEnd();

            gl.glBegin(GL.GL_QUADS); // Left Wall
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f( -2.5f, -2.5f, 15.0f);
            gl.glFogCoordfEXT(1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f( -2.5f, 2.5f, 15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f( -2.5f, 2.5f, -15.0f);
            gl.glFogCoordfEXT(0.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f( -2.5f, -2.5f, -15.0f);
            gl.glEnd();

            gl.glFlush(); // Flush The GL Rendering Pipeline
        }
    }
}
