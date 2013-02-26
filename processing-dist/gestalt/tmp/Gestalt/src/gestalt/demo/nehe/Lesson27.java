/*
 * Lesson 27 shadows with the stencilbuffer.
 */


package gestalt.demo.nehe;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import java.awt.event.KeyEvent;

import gestalt.context.GLContext;
import gestalt.context.JoglDisplay;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;

import data.Resource;


public class Lesson27
    extends AnimatorRenderer {

    // holds information on which keys are held down.
    boolean[] keys = new boolean[256];

    float[] GLvector4f = new float[4]; // Typedef's For VMatMult Procedure

    float[] GLmatrix16f = new float[16]; // Typedef's For VMatMult Procedure

    glObject obj = new glObject(); // Object

    float xrot = 0, xspeed = 0; // X Rotation & X Speed

    float yrot = 0, yspeed = 0; // Y Rotation & Y Speed

    float[] LightPos = {0.0f, 5.0f, -4.0f, 1.0f}; // Light Position

    float[] LightAmb = {0.2f, 0.2f, 0.2f, 1.0f}; // Ambient Light Values

    float[] LightDif = {0.6f, 0.6f, 0.6f, 1.0f}; // Diffuse Light Values

    float[] LightSpc = { -0.2f, -0.2f, -0.2f, 1.0f}; // Specular Light Values

    float[] MatAmb = {0.4f, 0.4f, 0.4f, 1.0f}; // Material - Ambient Values

    float[] MatDif = {0.2f, 0.6f, 0.9f, 1.0f}; // Material - Diffuse Values

    float[] MatSpc = {0.0f, 0.0f, 0.0f, 1.0f}; // Material - Specular

    // Values

    float[] MatShn = {0.0f}; // Material - Shininess

    float[] ObjPos = { -2.0f, -2.0f, -5.0f}; // Object Position

    GLUquadric quadratic; // Quadratic For Drawing A Sphere

    float[] SpherePos = { -4.0f, -5.0f, -6.0f};

    public void setup() {
        RenderBin myRenderBin = new RenderBin(3);
        setBinRef(myRenderBin);
        myRenderBin.add(event());
        myRenderBin.add(new Init());
        myRenderBin.add(new Display());
        framerate(100);

        /* lesson27 setup */
        // stencilBits = 8; // request 8 stencil bits
    }


    public static void main(String[] arg) {
        JoglDisplay.ENABLE_STENCIL_BUFFER = true;
        new Lesson27().init();
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
            _myIsActive = false;

            /* opengl init method */

            ReadObject("demo/nehe/lesson27/Object2.txt", obj); // Read Object2
            // Into obj

            SetConnectivity(obj); // Set Face To Face Connectivity

            for (int i = 0; i < obj.nPlanes; i++) { // Loop Through All Object
                // Planes
                CalcPlane(obj, obj.planes[i]);
            } // Compute Plane Equations For All Faces

            gl.glShadeModel(GL.GL_SMOOTH); // Enable Smooth Shading
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
            gl.glClearDepth(1.0f); // Depth Buffer Setup
            gl.glClearStencil(0); // Stencil Buffer Setup
            gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
            gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
            gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really
            // Nice
            // Perspective
            // Calculations

            /** @todo JSR-231 -- added 0 */

            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, LightPos, 0); // Set
            // Light1
            // Position
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, LightAmb, 0); // Set
            // Light1
            // Ambience
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, LightDif, 0); // Set
            // Light1
            // Diffuse
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, LightSpc, 0); // Set
            // Light1
            // Specular
            gl.glEnable(GL.GL_LIGHT1); // Enable Light1
            gl.glEnable(GL.GL_LIGHTING); // Enable Lighting

            gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, MatAmb, 0); // Set
            // Material
            // Ambience
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, MatDif, 0); // Set
            // Material
            // Diffuse
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, MatSpc, 0); // Set
            // Material
            // Specular
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, MatShn, 0); // Set
            // Material
            // Shininess

            gl.glCullFace(GL.GL_BACK); // Set Culling Face To Back Face
            gl.glEnable(GL.GL_CULL_FACE); // Enable Culling
            gl.glClearColor(0.1f, 1.0f, 0.5f, 1.0f); // Set Clear Color
            // (Greenish Color)

            quadratic = glu.gluNewQuadric(); // Initialize Quadratic
            glu.gluQuadricNormals(quadratic, GL.GL_SMOOTH); // Enable Smooth
            // Normal Generation
            glu.gluQuadricTexture(quadratic, false); // Disable Auto Texture
            // Coords

            // Reset The Current Viewport And Perspective Transformation
            gl.glViewport(0, 0, displaycapabilities().width, displaycapabilities().height);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45f,
                               (float) displaycapabilities().width /
                               (float) displaycapabilities().height,
                               0.1f, 100f);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

        }


        // function for computing a plane equation given 3 points
        void CalcPlane(glObject o,
                       sPlane plane) {
            sPoint[] v = new sPoint[4];
            int i;

            for (i = 0; i < 4; i++) {
                v[i] = new sPoint();
            }

            for (i = 0; i < 3; i++) {
                v[i + 1].x = o.points[plane.p[i]].x;
                v[i + 1].y = o.points[plane.p[i]].y;
                v[i + 1].z = o.points[plane.p[i]].z;
            }
            plane.PlaneEq.a = v[1].y * (v[2].z - v[3].z) + v[2].y * (v[3].z - v[1].z) + v[3].y * (v[1].z - v[2].z);
            plane.PlaneEq.b = v[1].z * (v[2].x - v[3].x) + v[2].z * (v[3].x - v[1].x) + v[3].z * (v[1].x - v[2].x);
            plane.PlaneEq.c = v[1].x * (v[2].y - v[3].y) + v[2].x * (v[3].y - v[1].y) + v[3].x * (v[1].y - v[2].y);
            plane.PlaneEq.d = - (v[1].x * (v[2].y * v[3].z - v[3].y * v[2].z) + v[2].x
                                 * (v[3].y * v[1].z - v[1].y * v[3].z) + v[3].x * (v[1].y * v[2].z - v[2].y * v[1].z));
        }


        // load object
        void ReadObject(String file,
                        glObject o) {
            // set up an input stream to our file from a jar
            InputStream is = Resource.getStream(file);

            // wrap a buffer to make reading more efficient (faster)
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            StringBuffer fileContents = new StringBuffer();

            try {
                // make one big string out of the entire file contents
                // (string tokenizer will sort it out later)
                String line;
                while ( (line = in.readLine()) != null) {
                    fileContents.append(line + "\n");
                }

                in.close();
            } catch (IOException ioe) {
                System.out.println("A failure occured while reading " + file);
                ioe.printStackTrace();
                try {
                    in.close();
                } catch (IOException ioe2) {
                }
            }
            // create a tokenizer to break up the text into tokens
            StringTokenizer tok = new StringTokenizer(fileContents.toString());

            int i;
            // points
            o.nPoints = Integer.parseInt(tok.nextToken());

            for (i = 1; i <= o.nPoints; i++) {
                o.points[i].x = Float.parseFloat(tok.nextToken());
                o.points[i].y = Float.parseFloat(tok.nextToken());
                o.points[i].z = Float.parseFloat(tok.nextToken());
            }
            // planes
            o.nPlanes = Integer.parseInt(tok.nextToken());
            for (i = 0; i < o.nPlanes; i++) {
                o.planes[i].p[0] = Integer.parseInt(tok.nextToken());
                o.planes[i].p[1] = Integer.parseInt(tok.nextToken());
                o.planes[i].p[2] = Integer.parseInt(tok.nextToken());

                o.planes[i].normals[0].x = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[0].y = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[0].z = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[1].x = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[1].y = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[1].z = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[2].x = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[2].y = Integer.parseInt(tok.nextToken());
                o.planes[i].normals[2].z = Integer.parseInt(tok.nextToken());
            }
        }


        // connectivity procedure - based on Gamasutra's article
        // hard to explain here
        void SetConnectivity(glObject o) {
            int p1i, p2i, p1j, p2j;
            int P1i, P2i, P1j, P2j;
            int i, j, ki, kj;

            for (i = 0; i < o.nPlanes - 1; i++) {
                for (j = i + 1; j < o.nPlanes; j++) {
                    for (ki = 0; ki < 3; ki++) {
                        if (! (o.planes[i].neigh[ki] != 0)) {
                            for (kj = 0; kj < 3; kj++) {
                                p1i = ki;
                                p1j = kj;
                                p2i = (ki + 1) % 3;
                                p2j = (kj + 1) % 3;

                                p1i = o.planes[i].p[p1i];
                                p2i = o.planes[i].p[p2i];
                                p1j = o.planes[j].p[p1j];
                                p2j = o.planes[j].p[p2j];

                                P1i = ( (p1i + p2i) - Math.abs(p1i - p2i)) / 2;
                                P2i = ( (p1i + p2i) + Math.abs(p1i - p2i)) / 2;
                                P1j = ( (p1j + p2j) - Math.abs(p1j - p2j)) / 2;
                                P2j = ( (p1j + p2j) + Math.abs(p1j - p2j)) / 2;

                                if ( (P1i == P1j) && (P2i == P2j)) { // they are
                                    // neighbours
                                    o.planes[i].neigh[ki] = j + 1;
                                    o.planes[j].neigh[kj] = i + 1;
                                }
                            }
                        }
                    }
                }
            }
        }


        public boolean isActive() {
            return _myIsActive;
        }
    }


    private class Display
        extends AbstractDrawable {

        public void draw(final GLContext theContext) {
            GL gl = (  theContext).gl;
            GLU glu = (  theContext).glu;

            /* opengl display method */
            // GLmatrix16f Minv;
            float[] Minv = new float[16];
            // GLvector4f wlp, lp;
            float[] wlp = new float[4];
            float[] lp = new float[4];

            // Clear Color Buffer, Depth Buffer, Stencil Buffer
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

            gl.glLoadIdentity(); // Reset Modelview Matrix
            gl.glTranslatef(0.0f, 0.0f, -20.0f); // Zoom Into Screen 20 Units
            /** @todo JSR-231 -- added 0 */
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, LightPos, 0); // Position
            // Light1
            gl.glTranslatef(SpherePos[0], SpherePos[1], SpherePos[2]); // Position
            // The
            // Sphere
            glu.gluSphere(quadratic, 1.5, 32, 16); // Draw A Sphere

            // calculate light's position relative to local coordinate system
            // dunno if this is the best way to do it, but it actually works
            // if u find another aproach, let me know ;)

            // we build the inversed matrix by doing all the actions in reverse
            // order
            // and with reverse parameters (notice -xrot, -yrot, -ObjPos[],
            // etc.)
            gl.glLoadIdentity(); // Reset Matrix
            gl.glRotatef( -yrot, 0.0f, 1.0f, 0.0f); // Rotate By -yrot On Y Axis
            gl.glRotatef( -xrot, 1.0f, 0.0f, 0.0f); // Rotate By -xrot On X Axis
            /** @todo JSR-231 -- added 0 */
            gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, Minv, 0); // Retrieve
            // ModelView Matrix
            // (Stores In Minv)
            lp[0] = LightPos[0]; // Store Light Position X In lp[0]
            lp[1] = LightPos[1]; // Store Light Position Y In lp[1]
            lp[2] = LightPos[2]; // Store Light Position Z In lp[2]
            lp[3] = LightPos[3]; // Store Light Direction In lp[3]
            VMatMult(Minv, lp); // We Store Rotated Light Vector In 'lp' Array
            gl.glTranslatef( -ObjPos[0], -ObjPos[1], -ObjPos[2]); // Move
            // Negative
            // On All
            // Axis
            // Based On
            // ObjPos[]
            // Values
            // (X, Y, Z)
            /** @todo JSR-231 -- added 0 */
            gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, Minv, 0); // Retrieve
            // ModelView Matrix
            // From Minv
            wlp[0] = 0.0f; // World Local Coord X To 0
            wlp[1] = 0.0f; // World Local Coord Y To 0
            wlp[2] = 0.0f; // World Local Coord Z To 0
            wlp[3] = 1.0f;
            VMatMult(Minv, wlp); // We Store The Position Of The World Origin
            // Relative To The
            // Local Coord. System In 'wlp' Array
            lp[0] += wlp[0]; // Adding These Two Gives Us The
            lp[1] += wlp[1]; // Position Of The Light Relative To
            lp[2] += wlp[2]; // The Local Coordinate System

            gl.glColor4f(0.7f, 0.4f, 0.0f, 1.0f); // Set Color To An Orange
            gl.glLoadIdentity(); // Reset Modelview Matrix
            gl.glTranslatef(0.0f, 0.0f, -20.0f); // Zoom Into The Screen 20
            // Units
            DrawGLRoom(gl); // Draw The Room
            gl.glTranslatef(ObjPos[0], ObjPos[1], ObjPos[2]); // Position The
            // Object
            gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f); // Spin It On The X Axis By
            // xrot
            gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f); // Spin It On The Y Axis By
            // yrot
            DrawGLObject(obj, gl); // Procedure For Drawing The Loaded Object
            CastShadow(obj, lp, gl); // Procedure For Casting The Shadow
            // Based On The Silhouette

            gl.glColor4f(0.7f, 0.4f, 0.0f, 1.0f); // Set Color To Purplish
            // Blue
            gl.glDisable(GL.GL_LIGHTING); // Disable Lighting
            gl.glDepthMask(false); // Disable Depth Mask
            gl.glTranslatef(lp[0], lp[1], lp[2]); // Translate To Light's
            // Position
            // Notice We're Still In Local Coordinate System
            glu.gluSphere(quadratic, 0.2f, 16, 8); // Draw A Little Yellow
            // Sphere (Represents Light)
            gl.glEnable(GL.GL_LIGHTING); // Enable Lighting
            gl.glDepthMask(true); // Enable Depth Mask

            xrot += xspeed; // Increase xrot By xspeed
            yrot += yspeed; // Increase yrot By yspeed

            ProcessKeyboard();
        }


        void ProcessKeyboard() { // Process Keyboard Results
            // Spin Object
            if (event().keyCode == KEYCODE_LEFT) {
                yspeed -= 0.1f; // 'Arrow Left' Decrease yspeed
            }
            if (event().keyCode == KEYCODE_RIGHT) {
                yspeed += 0.1f; // 'Arrow Right' Increase yspeed
            }
            if (event().keyCode == KEYCODE_UP) {
                xspeed -= 0.1f; // 'Arrow Up' Decrease xspeed
            }
            if (event().keyCode == KEYCODE_DOWN) {
                xspeed += 0.1f; // 'Arrow Down' Increase xspeed
            }

            // Adjust Light's Position
            if (event().key == 'L') {
                LightPos[0] += 0.05f; // 'L' Moves Light Right
            }
            if (event().key == 'J') {
                LightPos[0] -= 0.05f; // 'J' Moves Light Left
            }

            if (event().key == 'I') {
                LightPos[1] += 0.05f; // 'I' Moves Light Up
            }
            if (event().key == 'K') {
                LightPos[1] -= 0.05f; // 'K' Moves Light Down
            }

            if (event().key == 'O') {
                LightPos[2] += 0.05f; // 'O' Moves Light Toward Viewer
            }
            if (event().key == 'U') {
                LightPos[2] -= 0.05f; // 'U' Moves Light Away From Viewer
            }

            // Adjust Object's Position
            if (keys[KeyEvent.VK_NUMPAD6]) {
                ObjPos[0] += 0.05f; // 'Numpad6' Move Object Right
            }
            if (keys[KeyEvent.VK_NUMPAD4]) {
                ObjPos[0] -= 0.05f; // 'Numpad4' Move Object Left
            }

            if (keys[KeyEvent.VK_NUMPAD8]) {
                ObjPos[1] += 0.05f; // 'Numpad8' Move Object Up
            }
            if (keys[KeyEvent.VK_NUMPAD5]) {
                ObjPos[1] -= 0.05f; // 'Numpad5' Move Object Down
            }

            if (keys[KeyEvent.VK_NUMPAD9]) {
                ObjPos[2] += 0.05f; // 'Numpad9' Move Object Toward Viewer
            }
            if (keys[KeyEvent.VK_NUMPAD7]) {
                ObjPos[2] -= 0.05f; // 'Numpad7' Move Object Away From Viewer
            }

            // Adjust Ball's Position
            if (keys['D']) {
                SpherePos[0] += 0.05f; // 'D' Move Ball Right
            }
            if (keys['A']) {
                SpherePos[0] -= 0.05f; // 'A' Move Ball Left
            }

            if (keys['W']) {
                SpherePos[1] += 0.05f; // 'W' Move Ball Up
            }
            if (keys['S']) {
                SpherePos[1] -= 0.05f; // 'S' Move Ball Down
            }

            if (keys['E']) {
                SpherePos[2] += 0.05f; // 'E' Move Ball Toward Viewer
            }
            if (keys['Q']) {
                SpherePos[2] -= 0.05f; // 'Q' Move Ball Away From Viewer
            }
        }


        private void VMatMult(float[] M,
                              float[] v) {
            float[] res = new float[4]; // Hold Calculated Results
            res[0] = M[0] * v[0] + M[4] * v[1] + M[8] * v[2] + M[12] * v[3];
            res[1] = M[1] * v[0] + M[5] * v[1] + M[9] * v[2] + M[13] * v[3];
            res[2] = M[2] * v[0] + M[6] * v[1] + M[10] * v[2] + M[14] * v[3];
            res[3] = M[3] * v[0] + M[7] * v[1] + M[11] * v[2] + M[15] * v[3];
            v[0] = res[0]; // Results Are Stored Back In v[]
            v[1] = res[1];
            v[2] = res[2];
            v[3] = res[3]; // Homogenous Coordinate
        }


        private void DrawGLRoom(GL gl) { // Draw The Room (Box)
            gl.glBegin(GL.GL_QUADS); // Begin Drawing Quads
            // Floor
            gl.glNormal3f(0.0f, 1.0f, 0.0f); // Normal Pointing Up
            gl.glVertex3f( -10.0f, -10.0f, -20.0f); // Back Left
            gl.glVertex3f( -10.0f, -10.0f, 20.0f); // Front Left
            gl.glVertex3f(10.0f, -10.0f, 20.0f); // Front Right
            gl.glVertex3f(10.0f, -10.0f, -20.0f); // Back Right
            // Ceiling
            gl.glNormal3f(0.0f, -1.0f, 0.0f); // Normal Point Down
            gl.glVertex3f( -10.0f, 10.0f, 20.0f); // Front Left
            gl.glVertex3f( -10.0f, 10.0f, -20.0f); // Back Left
            gl.glVertex3f(10.0f, 10.0f, -20.0f); // Back Right
            gl.glVertex3f(10.0f, 10.0f, 20.0f); // Front Right
            // Front Wall
            gl.glNormal3f(0.0f, 0.0f, 1.0f); // Normal Pointing Away From
            // Viewer
            gl.glVertex3f( -10.0f, 10.0f, -20.0f); // Top Left
            gl.glVertex3f( -10.0f, -10.0f, -20.0f); // Bottom Left
            gl.glVertex3f(10.0f, -10.0f, -20.0f); // Bottom Right
            gl.glVertex3f(10.0f, 10.0f, -20.0f); // Top Right
            // Back Wall
            gl.glNormal3f(0.0f, 0.0f, -1.0f); // Normal Pointing Towards
            // Viewer
            gl.glVertex3f(10.0f, 10.0f, 20.0f); // Top Right
            gl.glVertex3f(10.0f, -10.0f, 20.0f); // Bottom Right
            gl.glVertex3f( -10.0f, -10.0f, 20.0f); // Bottom Left
            gl.glVertex3f( -10.0f, 10.0f, 20.0f); // Top Left
            // Left Wall
            gl.glNormal3f(1.0f, 0.0f, 0.0f); // Normal Pointing Right
            gl.glVertex3f( -10.0f, 10.0f, 20.0f); // Top Front
            gl.glVertex3f( -10.0f, -10.0f, 20.0f); // Bottom Front
            gl.glVertex3f( -10.0f, -10.0f, -20.0f); // Bottom Back
            gl.glVertex3f( -10.0f, 10.0f, -20.0f); // Top Back
            // Right Wall
            gl.glNormal3f( -1.0f, 0.0f, 0.0f); // Normal Pointing Left
            gl.glVertex3f(10.0f, 10.0f, -20.0f); // Top Back
            gl.glVertex3f(10.0f, -10.0f, -20.0f); // Bottom Back
            gl.glVertex3f(10.0f, -10.0f, 20.0f); // Bottom Front
            gl.glVertex3f(10.0f, 10.0f, 20.0f); // Top Front
            gl.glEnd(); // Done Drawing Quads
        }


        // procedure for drawing the object - very simple
        void DrawGLObject(glObject o,
                          GL gl) {
            int i, j;

            gl.glBegin(GL.GL_TRIANGLES);
            for (i = 0; i < o.nPlanes; i++) {
                for (j = 0; j < 3; j++) {
                    gl.glNormal3f(o.planes[i].normals[j].x, o.planes[i].normals[j].y, o.planes[i].normals[j].z);
                    gl.glVertex3f(o.points[o.planes[i].p[j]].x,
                                  o.points[o.planes[i].p[j]].y,
                                  o.points[o.planes[i].p[j]].z);
                }
            }
            gl.glEnd();
        }


        void CastShadow(glObject o,
                        float[] lp,
                        GL gl) {
            int i, j, k, jj;
            int p1, p2;
            sPoint v1 = new sPoint();
            sPoint v2 = new sPoint();
            float side;

            // set visual parameter
            for (i = 0; i < o.nPlanes; i++) {
                // check to see if light is in front or behind the plane (face
                // plane)
                side = o.planes[i].PlaneEq.a * lp[0] + o.planes[i].PlaneEq.b * lp[1] + o.planes[i].PlaneEq.c * lp[2]
                       + o.planes[i].PlaneEq.d * lp[3];
                if (side > 0) {
                    o.planes[i].visible = true;
                } else {
                    o.planes[i].visible = false;
                }
            }

            gl.glDisable(GL.GL_LIGHTING);
            gl.glDepthMask(false);
            gl.glDepthFunc(GL.GL_LEQUAL);

            gl.glEnable(GL.GL_STENCIL_TEST);
            gl.glColorMask(false, false, false, false);
            gl.glStencilFunc(GL.GL_ALWAYS, 1, -1);

            // first pass, stencil operation decreases stencil value
            gl.glFrontFace(GL.GL_CCW);
            gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR);
            for (i = 0; i < o.nPlanes; i++) {
                if (o.planes[i].visible) {
                    for (j = 0; j < 3; j++) {
                        k = o.planes[i].neigh[j];
                        if ( (k > 0) || (!o.planes[k - 1].visible)) {
                            // here we have an edge, we must draw a polygon
                            p1 = o.planes[i].p[j];
                            jj = (j + 1) % 3;
                            p2 = o.planes[i].p[jj];

                            // calculate the length of the vector
                            v1.x = (o.points[p1].x - lp[0]) * 100;
                            v1.y = (o.points[p1].y - lp[1]) * 100;
                            v1.z = (o.points[p1].z - lp[2]) * 100;

                            v2.x = (o.points[p2].x - lp[0]) * 100;
                            v2.y = (o.points[p2].y - lp[1]) * 100;
                            v2.z = (o.points[p2].z - lp[2]) * 100;

                            // draw the polygon
                            gl.glBegin(GL.GL_TRIANGLE_STRIP);
                            gl.glVertex3f(o.points[p1].x, o.points[p1].y, o.points[p1].z);
                            gl.glVertex3f(o.points[p1].x + v1.x, o.points[p1].y + v1.y, o.points[p1].z + v1.z);

                            gl.glVertex3f(o.points[p2].x, o.points[p2].y, o.points[p2].z);
                            gl.glVertex3f(o.points[p2].x + v2.x, o.points[p2].y + v2.y, o.points[p2].z + v2.z);
                            gl.glEnd();
                        }
                    }
                }
            }

            // second pass, stencil operation increases stencil value
            gl.glFrontFace(GL.GL_CW);
            gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR);
            for (i = 0; i < o.nPlanes; i++) {
                if (o.planes[i].visible) {
                    for (j = 0; j < 3; j++) {
                        k = o.planes[i].neigh[j];
                        if ( (k > 0) || (!o.planes[k - 1].visible)) {
                            // here we have an edge, we must draw a polygon
                            p1 = o.planes[i].p[j];
                            jj = (j + 1) % 3;
                            p2 = o.planes[i].p[jj];

                            // calculate the length of the vector
                            v1.x = (o.points[p1].x - lp[0]) * 100;
                            v1.y = (o.points[p1].y - lp[1]) * 100;
                            v1.z = (o.points[p1].z - lp[2]) * 100;

                            v2.x = (o.points[p2].x - lp[0]) * 100;
                            v2.y = (o.points[p2].y - lp[1]) * 100;
                            v2.z = (o.points[p2].z - lp[2]) * 100;

                            // draw the polygon
                            gl.glBegin(GL.GL_TRIANGLE_STRIP);
                            gl.glVertex3f(o.points[p1].x, o.points[p1].y, o.points[p1].z);
                            gl.glVertex3f(o.points[p1].x + v1.x, o.points[p1].y + v1.y, o.points[p1].z + v1.z);

                            gl.glVertex3f(o.points[p2].x, o.points[p2].y, o.points[p2].z);
                            gl.glVertex3f(o.points[p2].x + v2.x, o.points[p2].y + v2.y, o.points[p2].z + v2.z);
                            gl.glEnd();
                        }
                    }
                }
            }

            gl.glFrontFace(GL.GL_CCW);
            gl.glColorMask(true, true, true, true);

            // draw a shadowing rectangle covering the entire screen
            gl.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glStencilFunc(GL.GL_NOTEQUAL, 0, 0xffffffff);
            gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            gl.glVertex3f( -0.1f, 0.1f, -0.10f);
            gl.glVertex3f( -0.1f, -0.1f, -0.10f);
            gl.glVertex3f(0.1f, 0.1f, -0.10f);
            gl.glVertex3f(0.1f, -0.1f, -0.10f);
            gl.glEnd();
            gl.glPopMatrix();
            gl.glDisable(GL.GL_BLEND);

            gl.glDepthFunc(GL.GL_LEQUAL);
            gl.glDepthMask(true);
            gl.glEnable(GL.GL_LIGHTING);
            gl.glDisable(GL.GL_STENCIL_TEST);
            gl.glShadeModel(GL.GL_SMOOTH);
        }
    }


    // vertex in 3d-coordinate system
    class sPoint {
        float x = 0f;

        float y = 0f;

        float z = 0f;
    };

    // plane equation
    class sPlaneEq {
        float a = 0f;

        float b = 0f;

        float c = 0f;

        float d = 0f;
    };

    // structure describing an object's face
    class sPlane {
        int[] p = new int[3];

        sPoint[] normals = new sPoint[3];

        int[] neigh = new int[3];

        sPlaneEq PlaneEq = new sPlaneEq();

        boolean visible = false;

        sPlane() {
            normals[0] = new sPoint();
            normals[1] = new sPoint();
            normals[2] = new sPoint();
        }
    };

    // object structure
    class glObject {
        int nPlanes = 0;

        int nPoints = 0;

        sPoint[] points = new sPoint[100];

        sPlane[] planes = new sPlane[200];

        glObject() {
            for (int x = 0; x < 200; x++) {
                if (x < 100) {
                    points[x] = new sPoint();
                }
                planes[x] = new sPlane();
            }
        }
    }
}
