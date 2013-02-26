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

import gestalt.model.BoundingBoxView;
import gestalt.model.Model;
import gestalt.model.ModelAnimation;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.model.ModelPlayer;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.util.CameraMover;

import data.Resource;

import java.util.Vector;


public class UsingOBJModelLoader
        extends AnimatorRenderer {

    private ModelPlayer myModelPlayer;

    private Model myModel;

    public void setup() {
        /*
         * first you load the data that describes the model by using the
         * ModelLoaderOBJ
         */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/explosion.obj"));

        /*
         * then you create a mesh out of the data stored in your modeldata
         */
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices,
                                                  3,
                                                  myModelData.vertexColors,
                                                  4,
                                                  myModelData.texCoordinates,
                                                  2,
                                                  myModelData.normals,
                                                  myModelData.primitive);

        /* having the modeldata and the mesh you can create the actual model */
        myModel = drawablefactory().model(myModelData, myModelMesh);
        BoundingBoxView myBBox = new BoundingBoxView(myModel.getBoundingBoxData());
        myBBox.material().wireframe = true;
        myModel.setBoundingBoxView(myBBox);

        /*
         * create animations for the model; an animation describes an area that
         * should be displayed; these are the vertices that are displayed by the
         * mesh. so basically, playing an animation means shifting the displayed
         * areas in your mesh.
         *
         * animations consist of a name,a startframe and a stopframe.
         *
         * in this case we define three animations: animation "one" displays
         * frame 0 animation "two" displays frame 1 animation "three" displays
         * frame 0 to frame 1
         */
        Vector<ModelAnimation> myAnimations = new Vector<ModelAnimation>();
        myAnimations.add(new ModelAnimation("one", 0, 10));
        myAnimations.add(new ModelAnimation("two", 11, 29));
        myAnimations.add(new ModelAnimation("three", 0, 29));
        myModel.setAnimations(myAnimations);

        /* you can also use an xml-file to create your animations */
        // Vector myAnimations =
        // ModelUtil.getAnimations(Resource.getStream("demo/common/animations.xml"));
        // myModel.setAnimations(myAnimations);
        /*
         * create texture for the model and setup lighting
         *
         * for the lighting to be succesful you need valid normals for your
         * vertices. to complicate things, some applications like cinema4d don t
         * export normals, and there are differences between vertex order of
         * different 3d modelling applications. i.e. maya exports
         * counterclockwise and cinema4d exports clockwise. if there are no
         * exported normals at all, we create them ourselves, depending on the
         * vertex order. so if you experience strange lighting behavior, check
         * the vertex direction we use for generating the normals. to switch it
         * between clcokwise and counterclockwise, use:
         * ModelLoaderOBJ.GET_NORMALS_DIRECTION = ModelLoaderOBJ.GET_NORMALS_CW;
         * ModelLoaderOBJ.GET_NORMALS_DIRECTION =
         * ModelLoaderOBJ.GET_NORMALS_CCW;
         */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getPath("demo/common/vignet.png")));
        myModel.mesh().material().addPlugin(myTexture);
        myModel.mesh().material().lit = true;

        /*
         * create a modelplayer, that plays the animations. the modelplayer
         * times the shifting of the displayed area depending on the velocity
         * you define in the play() method.
         */
        myModelPlayer = new ModelPlayer(myModel);
        myModelPlayer.play("one", 0.7f);
        myModelPlayer.setLooping(true);

        /* add model to renderer */
        bin(BIN_3D).add(myModel);

        /* camera */
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set(-300, 250, 300);

        /* setup light */
        light().enable = true;
        light().setPositionRef(camera().position());

        /* set background color4f */
        displaycapabilities().backgroundcolor.set(0.2f);
    }

    public void loop(final float theDeltaTime) {
        /* update the cameramover */
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        /*
         * loop the modelplayer. it uses deltatime to update the diplayed area.
         */
        myModelPlayer.loop(theDeltaTime);
        myModel.calculateBoundingBox();

        /* decide which animation should be played */
        if (event().keyPressed) {
            if (event().key == '1') {
                myModelPlayer.play("one", 0.7f);
            }
            if (event().key == '2') {
                myModelPlayer.play("two", 0.7f);
            }
            if (event().key == '3') {
                myModelPlayer.play("three", 0.7f);
            }
            if (event().key == 'r') {
                myModel.mesh().rotation().x += 0.1f;
                myModel.calculateBoundingBox();
            }
            if (event().key == 'f') {
                myModel.mesh().scale().y += 0.1f;
                myModel.calculateBoundingBox();
            }
            if (event().key == 't') {
                myModel.mesh().position().z += 10f;
                myModel.calculateBoundingBox();
            }
        }
    }

    public static void main(String[] args) {
        new UsingOBJModelLoader().init();
    }
}
