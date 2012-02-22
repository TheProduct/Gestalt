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


package gestalt.demo.processing.unsorted;


import gestalt.Gestalt;
import gestalt.render.controller.FrameBufferCopy;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.processing.GestaltPlugIn;
import gestalt.shape.Mesh;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.IntegerBitmap;
import gestalt.util.CameraMover;

import data.Resource;
import processing.core.PApplet;


/**
 * this demo shows how to grab a sketch into a texture and then render it onto a wavefront 3D model.
 * the interesting part is that the 3D model can be modelled in a 3D application. a placeholder
 * for the sketch is then placed onto the model to generate the right UV coordinates. finally the
 * model is exported as a 'wavefront' .obj file.
 */

/** @todo
 * there are still some issues. for example lighting doesn t properly work yet.
 */

public class UsingModelAsSketchCanvas
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Model _myModel;

    public void setup() {
        /* setup p5 */
        size(1024, 512, OPENGL);
        ellipseMode(CENTER);
        smooth();

        gestalt = new GestaltPlugIn(this);

        /*
         * keep gestalt from clearing any buffers.
         * we do this after we copied the framebuffer into a texture
         */
        gestalt.framesetup().depthbufferclearing = false;
        gestalt.framesetup().colorbufferclearing = false;

        /* create an empty dummy bitmap */
        TexturePlugin myTexture = gestalt.drawablefactory().texture();
        myTexture.load(IntegerBitmap.getDefaultImageBitmap(width, height));

        /* create the model */
        /* model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));
        Mesh myModelMesh = gestalt.drawablefactory().mesh(true,
                                                          myModelData.vertices, 3,
                                                          myModelData.vertexColors, 4,
                                                          myModelData.texCoordinates, 2,
                                                          myModelData.normals,
                                                          myModelData.primitive);

        _myModel = gestalt.drawablefactory().model(myModelData, myModelMesh);
        _myModel.mesh().material().addPlugin(myTexture);
        myTexture.setWrapMode(Gestalt.TEXTURE_WRAPMODE_CLAMP);
        myTexture.scale().y = 1;
        _myModel.mesh().material().lit = true;
        _myModel.mesh().material().transparent = false;
        gestalt.bin(Gestalt.BIN_3D).add(_myModel);

        /* camera */
        gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        gestalt.camera().position().set(150, 100, 450);

        /* light */
        gestalt.light().enable = true;
        gestalt.light().setPositionRef(gestalt.camera().position());

        /* create a screengrabber */
        FrameBufferCopy myFrameBufferCopy = new FrameBufferCopy(myTexture);
        myFrameBufferCopy.backgroundcolor.set(0.2f, 1);
        myFrameBufferCopy.width = width;
        myFrameBufferCopy.height = height;
        gestalt.bin(Gestalt.BIN_3D_SETUP).add(myFrameBufferCopy);
    }


    public void draw() {
        /* clear screen */
        background(0, 255, 127);

        /* draw processing stuff */
        fill(255, 255);
        stroke(0, 255);
        strokeWeight(20);
        ellipse(mouseX, mouseY, 200, 200);

        /* move camera */
        final float myDeltaTime = 1 / 30f;
        CameraMover.handleKeyEvent(gestalt.camera(), gestalt.event(), myDeltaTime);
        gestalt.camera().side(myDeltaTime * 20);

        /* change wrap mode */
        if (gestalt.event().mouseDown) {
            if (gestalt.event().mouseButton == Gestalt.MOUSEBUTTON_LEFT) {
                gestalt.enable();
            } else {
                gestalt.disable();
            }
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {UsingModelAsSketchCanvas.class.getName()});
    }
}
