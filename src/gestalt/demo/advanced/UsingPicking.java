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


import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.extension.picking.Pickable;
import gestalt.extension.picking.Picker;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import mathematik.Vector3f;

import data.Resource;


/**
 * this demo shows how to obtain use the picking mechanism of gestalt.<br/>
 * <br/>
 * the picking mechanism is opengl based.<br/>
 * short introduction, of how the openglpicking works:<br/>
 * basically, the picking is a second renderpass. this means, you draw the
 * objects you want to pick a second time in gl.glRenderMode(GL.GL_RENDER).
 * as you draw the objects in a very small viewport that is bound to the cursor,
 * that is no performance drwawback.<br/>
 *
 * Steps to use picking:<br/>
 * 1. create shape; this shape must implement interface openglpickable<br/>
 * 2. register shape on renderer; if you don t do that, you won see what you pick<br/>
 * 3. register the shape on openglpicker; then you receive your events
 */

public class UsingPicking
    extends AnimatorRenderer {

    private Picker _myPicker;

    private OpenGLPickingPlane _myOpenGLPickingPlane;

    public void setup() {
        _myPicker = drawablefactory().extensions().openglpicker();
        bin(BIN_3D).add(_myPicker);

        createOpenGLPickingPlane();
    }


    public void loop(float theDeltaTime) {
        _myOpenGLPickingPlane.loop(theDeltaTime);
        display().hasDrawError();
    }


    private void createOpenGLPickingPlane() {
        OpenGLPickingPlane myOpenGlPickingPlane = new OpenGLPickingPlane(
            BIN_3D,
            new Vector3f( -128, 0, 0));
        _myPicker.bin[PICKING_BIN_3D].add(myOpenGlPickingPlane);

        _myOpenGLPickingPlane = new OpenGLPickingPlane(
            BIN_3D,
            new Vector3f(0, 0, 0));
        _myPicker.bin[PICKING_BIN_3D].add(_myOpenGLPickingPlane);

        myOpenGlPickingPlane = new OpenGLPickingPlane(
            BIN_2D_FOREGROUND,
            new Vector3f(128, 0, 0));
        _myPicker.bin[PICKING_BIN_2D].add(myOpenGlPickingPlane);
    }


    public static void main(String[] arg) {
        /* set display properties */
        DisplayCapabilities myDisplay = new DisplayCapabilities();
        myDisplay.backgroundcolor.set(0.2f);
        myDisplay.width = 640;
        myDisplay.height = 480;
        new UsingPicking().init(myDisplay);
    }


    public class OpenGLPickingPlane
        implements Pickable {

        public boolean isPicked;

        private Plane _myPlane;

        public OpenGLPickingPlane(int theDimension, Vector3f thePosition) {
            createPlane(theDimension, thePosition);
        }


        private void createPlane(int theDimension, Vector3f thePosition) {
            TexturePlugin myTexture = drawablefactory().texture();
            myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));
            _myPlane = drawablefactory().plane();
            _myPlane.scale().set(128, 128);
            _myPlane.material().color4f().set(1);
            _myPlane.material().addPlugin(myTexture);
            _myPlane.material().depthmask = true;
            _myPlane.material().depthtest = false;
            _myPlane.origin(SHAPE_ORIGIN_CENTERED);
            _myPlane.transform().translation.set(thePosition);
            bin(theDimension).add(_myPlane);
        }


        public void loop(float theDeltaTime) {
            _myPlane.rotation().y += theDeltaTime * PI * 0.25f;
            _myPlane.rotation().x += theDeltaTime * PI * 0.33f;
            _myPlane.rotation().z += theDeltaTime * PI * 0.53f;
        }


        /* OpenGLPickable obligations */

        public void pickDraw(GLContext theRenderContext) {
            _myPlane.draw(theRenderContext);
        }


        public void mouseEnter() {
            isPicked = true;
            _myPlane.material().color4f().set(0.5f);
        }


        public void mouseLeave() {
            isPicked = false;
            _myPlane.material().color4f().set(1);
        }


        public void mouseWithin() {
            isPicked = true;
            _myPlane.material().color4f().set(0);
        }


        public boolean isPicked() {
            return isPicked;
        }
    }
}
