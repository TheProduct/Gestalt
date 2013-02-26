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

import gestalt.render.controller.FrameBufferCopy;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cuboid;
import gestalt.material.Material;
import gestalt.shape.Plane;
import gestalt.material.MaterialPlugin;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.IntegerBitmap;

import data.Resource;


public class UsingGLSLImageProcessing
    extends AnimatorRenderer {

    private Cuboid _myCube;

    private Plane _myPlane;

    private JoglGLSLGaussianBlur _myBlur;

    public void setup() {

        /* gestalt */
        framerate(UNDEFINED);
        int width = displaycapabilities().width;
        int height = displaycapabilities().height;

        /* create an empty dummy bitmap */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(IntegerBitmap.getDefaultImageBitmap(width, height));

        /* we don t need to flip textures as this one comes from opengl not java */
        myTexture.scale().y = 1;

        /* create a plane to display our texture */
        _myPlane = drawablefactory().plane();
        _myPlane.material().addPlugin(myTexture);
        _myPlane.material().depthtest = false;
        _myPlane.material().blendmode = MATERIAL_BLEND_ALPHA;
        _myPlane.scale().set(width, height);
        bin(BIN_2D_FOREGROUND).add(_myPlane);

        /* create cuboid */
        createCube();

        /* create a screengrabber */
        FrameBufferCopy myFrameBufferCopy = new FrameBufferCopy(myTexture);
        myFrameBufferCopy.colorbufferclearing = false;
        myFrameBufferCopy.depthbufferclearing = false;
        myFrameBufferCopy.width = width;
        myFrameBufferCopy.height = height;
        bin(BIN_2D_FOREGROUND_SETUP).add(myFrameBufferCopy);

        /* shader manager */
        ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
        ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
        bin(BIN_FRAME_SETUP).add(myShaderManager);

        /* add shader material to plane */
        _myBlur = new JoglGLSLGaussianBlur(myShaderManager,
                                           myShaderProgram,
                                           Resource.getStream("demo/shader/simple.vsh"),
                                           Resource.getStream("demo/shader/blur.fsh"),
                                           _myPlane.material().texture().getPixelHeight());
        _myPlane.material().addPlugin(_myBlur);
    }


    private void createCube() {
        /* create cuboid */
        _myCube = drawablefactory().cuboid();
        _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_WRAP_AROUND);
        _myCube.scale().set(200, 200, 200);

        /* create texture */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/cube.png")));
        _myCube.material().addPlugin(myTexture);
        _myCube.material().color4f().set(1, 0, 0);
        bin(BIN_3D).add(_myCube);
    }


    public void loop(float theDeltaTime) {
        _myCube.rotation().x += 0.3f * theDeltaTime;
        _myCube.rotation().y += 0.2f * theDeltaTime;

        _myBlur.blursize = 1 + 20f * ( (float) event().mouseY / (float) displaycapabilities().height + 0.5f);
        _myBlur.blurspread = 1f * ( (float) event().mouseX / (float) displaycapabilities().width + 0.5f);
    }


    public DisplayCapabilities createDisplayCapabilities() {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 512;
        myDisplayCapabilities.height = 512;
        myDisplayCapabilities.backgroundcolor.set(0.0f);
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

        public JoglGLSLGaussianBlur(final ShaderManager theMaterialShaderManager,
                                    final ShaderProgram theMaterialShaderProgram,
                                    final InputStream theVertexShaderCode,
                                    final InputStream theFragmentShaderCode,
                                    final int theTextureSize) {
            _myMaterialShaderManager = theMaterialShaderManager;
            _myMaterialShaderProgram = theMaterialShaderProgram;

            _myMaterialShaderManager.attachVertexShader(_myMaterialShaderProgram, theVertexShaderCode);
            _myMaterialShaderManager.attachFragmentShader(_myMaterialShaderProgram, theFragmentShaderCode);

            _myTextureSize = 1.0f / theTextureSize;
        }


        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myMaterialShaderManager.enable(_myMaterialShaderProgram);

            getGaussianOffsets(_myOffset,
                               _myWeight,
                               blursize);

            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "textureunit", 0);
            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "weight", _myWeight);
            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "offset", _myOffset);
            _myMaterialShaderManager.setUniform(_myMaterialShaderProgram, "spread", blurspread);
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
        new UsingGLSLImageProcessing().init();
    }
}
