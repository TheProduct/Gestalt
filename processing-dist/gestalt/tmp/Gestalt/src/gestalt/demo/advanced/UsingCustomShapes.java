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

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.render.Drawable;

import mathematik.Vector3f;


/**
 * this demo shows how to create a custom shape using the 'drawable' interface.
 * in this example we use the interface 'Drawable', it also possible to extend the
 * abstract class 'AbstractDrawable' where only the 'draw()' method needs to be
 * implemented.
 */

public class UsingCustomShapes
    extends AnimatorRenderer {

    private JoglCustomShape _myCustomShape;

    public void setup() {
        /* set background to white */
        displaycapabilities().backgroundcolor.set(1);

        /* create custom shape and add it to the renderer */
        _myCustomShape = new JoglCustomShape();
        bin(BIN_3D).add(_myCustomShape);
        _myCustomShape.scale.set(100, 100, 100);
    }


    public void loop(float theDeltaTime) {
        _myCustomShape.rotation.y = 180 * event().mouseX / (float) displaycapabilities().width;
        _myCustomShape.rotation.x = -180 * event().mouseY / (float) displaycapabilities().height;
    }


    private class JoglCustomShape
        implements Drawable {

        public Vector3f position;

        public Vector3f rotation;

        public Vector3f scale;

        private float _mySortValue;

        public JoglCustomShape() {
            position = new Vector3f();
            rotation = new Vector3f();
            scale = new Vector3f(1, 1, 1);
        }


        public void draw(GLContext theRenderContext) {
            final GL gl = (  theRenderContext).gl;

            gl.glPushMatrix();

            gl.glTranslatef(position.x, position.y, position.z);

            gl.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(rotation.z, 0.0f, 0.0f, 1.0f);

            gl.glScalef(scale.x, scale.y, scale.z);

            gl.glColor3f(0, 0, 0);

            gl.glBegin(GL.GL_QUADS);
            // Front Face
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            // Back Face
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            // Top Face
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            // Bottom Face
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            // Right face
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glVertex3f(1.0f, -1.0f, 1.0f);
            // Left Face
            gl.glVertex3f( -1.0f, -1.0f, -1.0f);
            gl.glVertex3f( -1.0f, -1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, 1.0f);
            gl.glVertex3f( -1.0f, 1.0f, -1.0f);
            gl.glEnd();

            gl.glPopMatrix();
        }


        public void add(Drawable theDrawable) {
            /* shape doesn t accept children. */
        }


        public boolean isActive() {
            return true;
        }


        public float getSortValue() {
            return _mySortValue;
        }


        public void setSortValue(float theSortValue) {
            _mySortValue = theSortValue;
        }


        public float[] getSortData() {
            return position.toArray();
        }


        public boolean isSortable() {
            return true;
        }
    }


    public static void main(String[] args) {
        new UsingCustomShapes().init();
    }
}
