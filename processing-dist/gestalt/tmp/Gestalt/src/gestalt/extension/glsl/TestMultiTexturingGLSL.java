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


package gestalt.extension.glsl;


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Material;
import gestalt.shape.Plane;
import gestalt.material.MaterialPlugin;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;

import data.Resource;


public class TestMultiTexturingGLSL
    extends AnimatorRenderer {

    private Plane _myPlane;

    public void setup() {
        /* glsl shader */
        ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(myShaderManager);

        ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
        myShaderManager.attachVertexShader(myShaderProgram, Resource.getStream("demo/shader/multitex.vsh"));
        myShaderManager.attachFragmentShader(myShaderProgram, Resource.getStream("demo/shader/multitex.fsh"));

        ShaderMaterial _myShaderMaterial = new ShaderMaterial(myShaderManager, myShaderProgram);

        /* plane */
        _myPlane = drawablefactory().plane();

        /* create a texture */
        TexturePlugin myTexture0 = drawablefactory().texture();
        myTexture0.load(Bitmaps.getBitmap(Resource.getStream("demo/common/stripes.png")));
        myTexture0.setTextureUnit(GL.GL_TEXTURE0);

        /* create a texture */
        TexturePlugin myTexture1 = drawablefactory().texture();
        myTexture1.load(Bitmaps.getBitmap(Resource.getStream("demo/common/mask256.png")));
        myTexture1.setTextureUnit(GL.GL_TEXTURE1);

        /* set the texture in the material of the shape */
        _myPlane.material().addPlugin(myTexture0);
        _myPlane.material().addPlugin(myTexture1);
        _myPlane.material().addPlugin(_myShaderMaterial);
        _myPlane.setPlaneSizeToTextureSize();

        /* add the plane to the renderer */
        bin(BIN_3D).add(_myPlane);
    }


    public void loop(final float theDeltaTime) {
        _myPlane.rotation().add(0.05f, 0.01f, 0.02f);
    }


    public class ShaderMaterial
        implements MaterialPlugin {

        private ShaderManager _myShaderManager;

        private ShaderProgram _myShaderProgram;

        public ShaderMaterial(ShaderManager theShaderManager,
                              ShaderProgram theShaderProgram) {
            _myShaderManager = theShaderManager;
            _myShaderProgram = theShaderProgram;
        }


        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myShaderManager.enable(_myShaderProgram);

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myShaderProgram, "FirstTexture", 0);
            _myShaderManager.setUniform(_myShaderProgram, "SecondTexture", 1);
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] arg) {
        new TestMultiTexturingGLSL().init();
    }
}
