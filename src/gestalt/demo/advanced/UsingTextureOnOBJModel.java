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


import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.util.CameraMover;

import data.Resource;


public class UsingTextureOnOBJModel
    extends AnimatorRenderer {

    private Model _myModel;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        /* model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);

        _myModel = drawablefactory().model(myModelData, myModelMesh);

        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/styrofoamplates.png")));
        myTexture.setWrapMode(TEXTURE_WRAPMODE_CLAMP);
        _myModel.mesh().material().addPlugin(myTexture);
        _myModel.mesh().material().lit = true;

        /* add model to renderer */
        bin(BIN_3D).add(_myModel);

        /* camera */
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set( -300, 250, 800);

        /* setup light */
        light().enable = true;
        light().setPositionRef(camera().position());
    }


    public void loop(final float theDeltaTime) {
        /* move camera */
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
        camera().side(theDeltaTime * 100);

        /* get plane s texture */
        final TexturePlugin myTexture = _myModel.mesh().material().texture();

        /* change wrap mode */
        if (event().mouseDown) {
            if (event().mouseButton == MOUSEBUTTON_LEFT) {
                myTexture.setWrapMode(TEXTURE_WRAPMODE_REPEAT);
            } else {
                myTexture.setWrapMode(TEXTURE_WRAPMODE_CLAMP);
            }
        }

        /* change texturescale */
        myTexture.position().set( (float) event().mouseX / (float) displaycapabilities().width,
                                 (float) event().mouseY / (float) displaycapabilities().height + 1f);
    }


    public static void main(String[] args) {
        new UsingTextureOnOBJModel().init();
    }
}
