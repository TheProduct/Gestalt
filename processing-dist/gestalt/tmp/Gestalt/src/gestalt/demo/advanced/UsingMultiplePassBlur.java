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


import java.io.InputStream;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_DepthRGBA;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.Camera;
import gestalt.render.controller.FrameSetup;
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.shape.Plane;
import gestalt.material.MaterialPlugin;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


public class UsingMultiplePassBlur
    extends AnimatorRenderer {

    private Model _myObject;

    private JoglGLSLGaussianBlur _myBlurPassOne;

    private JoglGLSLGaussianBlur _myBlurPassTwo;

    public void setup() {
        /* gestalt */
        framerate(60);

        /*
         * create scene.
         */

        final Plane mySceneCanvas;
        {
            /* create FBO */
            JoglFrameBufferObject myFBO = createFBO(displaycapabilities().width,
                                                    displaycapabilities().height,
                                                    drawablefactory().camera());
            bin(BIN_3D_FINISH).add(myFBO);

            /* create a plane to display our texture */
            mySceneCanvas = drawablefactory().plane();
            mySceneCanvas.material().addPlugin(myFBO);
            mySceneCanvas.material().depthtest = false;
            mySceneCanvas.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
            mySceneCanvas.scale().set(displaycapabilities().width, displaycapabilities().height);
//            bin(BIN_2D_FOREGROUND).add(mySceneCanvas);

            /* shader manager */
            ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
            ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
            bin(BIN_FRAME_SETUP).add(myShaderManager);

            /* add shader material to plane */
            _myBlurPassOne = new JoglGLSLGaussianBlur(myShaderManager,
                                                      myShaderProgram,
                                                      Resource.getStream("demo/shader/simple.vsh"),
                                                      Resource.getStream("demo/shader/blur.fsh"),
                                                      mySceneCanvas.material().texture().getPixelHeight(),
                                                      myFBO.getTextureID());
            mySceneCanvas.material().addPlugin(_myBlurPassOne);

            /*
             * model
             */

            /* create model with texture */
            _myObject = createModel();
            _myObject.mesh().material().color4f().set(80 / 255f, 170 / 255f, 255 / 255f);

            TexturePlugin myTexture = drawablefactory().texture();
            myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/stripes.png")));
            _myObject.mesh().material().addPlugin(myTexture);

            /* put model into the FBO */
            Model myGlowObject = createModel();
            myGlowObject.mesh().material().color4f().set(1, 0.8f);
            myGlowObject.mesh().setRotationRef(_myObject.mesh().rotation());
            myFBO.add(myGlowObject);

            /* add model to normal scene */
            bin(BIN_3D).add(_myObject);
        }

        /*
         * create flip flop FBOs.
         */
        {
            /* create FBO */
            JoglFrameBufferObject myFBO = createFBO(displaycapabilities().width,
                                                    displaycapabilities().height,
                                                    drawablefactory().camera());
            bin(BIN_3D_FINISH).add(myFBO);

            /* create a plane to display our texture */
            Plane myFilterCanvas = drawablefactory().plane();
            myFilterCanvas.material().addPlugin(myFBO);
            myFilterCanvas.material().depthtest = false;
            myFilterCanvas.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
            myFilterCanvas.scale().set(displaycapabilities().width, displaycapabilities().height);

            /* shader manager */
            ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
            ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
            bin(BIN_FRAME_SETUP).add(myShaderManager);

            /* add shader material to plane */
            _myBlurPassTwo = new JoglGLSLGaussianBlur(myShaderManager,
                                                      myShaderProgram,
                                                      Resource.getStream("demo/shader/simple.vsh"),
                                                      Resource.getStream("demo/shader/blur.fsh"),
                                                      myFilterCanvas.material().texture().getPixelHeight(),
                                                      myFBO.getTextureID());
            myFilterCanvas.material().addPlugin(_myBlurPassTwo);

            /* add scene plane */
            myFBO.add(mySceneCanvas);
            bin(BIN_2D_FOREGROUND).add(myFilterCanvas);
        }
    }


    private Model createModel() {
        Model myModel;
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);

        myModel = drawablefactory().model(myModelData, myModelMesh);
        myModelMesh.material().transparent = true;
        myModelMesh.material().depthtest = false;
        return myModel;
    }


    private JoglFrameBufferObject createFBO(int theWidth, int theHeight, Camera theCamera) {
        JoglFrameBufferObject myFBO = new JoglFrameBufferObject(theWidth,
                                                                theHeight,
                                                                theCamera,
                                                                new JoglTexCreatorFBO_DepthRGBA());

        /* set backgroundcolor of FBO */
        myFBO.backgroundcolor().set(0, 1);

        /* connect camera to the FBO */
        myFBO.setCameraRef(camera());

        /* create a framesetup for the FBO the clears the screen */
        FrameSetup myFrameSetup = drawablefactory().frameSetup();
        myFrameSetup.colorbufferclearing = true;
        myFrameSetup.depthbufferclearing = true;
        myFBO.add(myFrameSetup);

        return myFBO;
    }


    public void loop(float theDeltaTime) {
        _myObject.mesh().rotation().x += 0.3f * theDeltaTime;
        _myObject.mesh().rotation().y += 0.2f * theDeltaTime;

        _myBlurPassOne.blursize = 1f + 10f * ( (float) event().mouseY / (float) displaycapabilities().height + 0.5f);
        _myBlurPassOne.blurspread = 1f + 2f * ( (float) event().mouseX / (float) displaycapabilities().width + 0.5f);
        _myBlurPassTwo.blursize = _myBlurPassOne.blursize;
        _myBlurPassTwo.blurspread = _myBlurPassOne.blurspread;
        System.out.println(getCurrentFramerate());
    }


    public DisplayCapabilities createDisplayCapabilities() {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 1024;
        myDisplayCapabilities.height = 1024;
        myDisplayCapabilities.backgroundcolor.set(0.2f);
        return myDisplayCapabilities;
    }


    private class JoglGLSLGaussianBlur
        implements MaterialPlugin {

        private static final int KERNEL_SIZE = 16;

        private final ShaderManager _myMaterialShaderManager;

        private final ShaderProgram _myMaterialShaderProgram;

        private final float[] _myWeight = new float[KERNEL_SIZE];

        private final float[] _myOffset = new float[KERNEL_SIZE];

        public float blursize = 10;

        public float blurspread = 1;

        private final float _myTextureSize;

        private final int _myOpenGLTextureID;

        public JoglGLSLGaussianBlur(final ShaderManager theMaterialShaderManager,
                                    final ShaderProgram theMaterialShaderProgram,
                                    final InputStream theVertexShaderCode,
                                    final InputStream theFragmentShaderCode,
                                    final int theTextureSize,
                                    final int theOpenGLTextureID) {
            _myMaterialShaderManager = theMaterialShaderManager;
            _myMaterialShaderProgram = theMaterialShaderProgram;

            _myMaterialShaderManager.attachVertexShader(_myMaterialShaderProgram, theVertexShaderCode);
            _myMaterialShaderManager.attachFragmentShader(_myMaterialShaderProgram, theFragmentShaderCode);

            _myTextureSize = 1.0f / theTextureSize;
            _myOpenGLTextureID = theOpenGLTextureID;
        }


        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myMaterialShaderManager.enable(_myMaterialShaderProgram);

            getGaussianOffsets(_myOffset,
                               _myWeight,
                               blursize);

            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "weight", _myWeight);
            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "offset", _myOffset);
            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "spread", blurspread);
            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "textureunit", _myOpenGLTextureID);
        }


        public void end(final GLContext theRenderContext,
                        final Material theParent) {
            _myMaterialShaderManager.disable();
        }


        private float gaussianDistribution(float theValue, float theRadius) {
            theRadius *= theRadius;
            double myValue = 1 / Math.sqrt(2.0 * Math.PI * theRadius);
            return (float) (myValue * Math.exp( (theValue * theValue) / ( -2.0 * theRadius)));
        }


        private void getGaussianOffsets(float[] theOffset,
                                        float[] theWeight,
                                        float theBlurSize) {
            theWeight[0] = 1.0f * gaussianDistribution(0.0f, theBlurSize);
            theOffset[0] = 0.0f;
            theOffset[1] = 0.0f;

            for (int i = 1; i < KERNEL_SIZE - 1; i += 2) {
                theOffset[i] = (float) i * _myTextureSize;
                theOffset[i + 1] = (float) ( -i) * _myTextureSize;
                theWeight[i] = 2.0f * gaussianDistribution(i, theBlurSize);
                theWeight[i + 1] = 2.0f * gaussianDistribution(i + 1.0f, theBlurSize);
            }
        }
    }


    public static void main(String[] args) {
        new UsingMultiplePassBlur().init();
    }
}
