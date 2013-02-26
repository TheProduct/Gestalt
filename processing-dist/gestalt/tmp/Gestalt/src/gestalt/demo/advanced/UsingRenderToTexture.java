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

import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.IntegerBitmap;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.FrameBufferCopy;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;


public class UsingRenderToTexture
        extends AnimatorRenderer {

    private Cuboid _myCube;

    private Plane _myPlane;

    public void setup() {

        /* gestalt */
        framerate(30);
        displaycapabilities().backgroundcolor.set(0.2f, 0);

        /* create an empty dummy bitmap */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.setFilterType(TEXTURE_FILTERTYPE_LINEAR);
        myTexture.load(IntegerBitmap.getDefaultImageBitmap(width, height));

        /* we don t need to flip textures as this one comes from opengl not java */
        myTexture.scale().y = 1;

        /* create a plane to display our texture */
        _myPlane = drawablefactory().plane();
        _myPlane.material().addPlugin(myTexture);
        _myPlane.material().depthtest = false;
        _myPlane.material().depthmask = false;
        _myPlane.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myPlane.material().color4f().a = 0.99f;
        _myPlane.scale().set(width, height);
        _myPlane.scale().scale(0.98f);
        bin(BIN_3D).add(_myPlane);

        /* create cuboid */
        _myCube = drawablefactory().cuboid();
        _myCube.scale().set(200, 200, 200);
        bin(BIN_3D).add(_myCube);

        /* create a screengrabber */
        FrameBufferCopy myFrameBufferCopy = new FrameBufferCopy(myTexture);
        myFrameBufferCopy.colorbufferclearing = false;
        myFrameBufferCopy.depthbufferclearing = false;
        myFrameBufferCopy.width = width;
        myFrameBufferCopy.height = height;
        bin(BIN_2D_FOREGROUND).add(myFrameBufferCopy);
    }

    public void loop(float theDeltaTime) {
        _myCube.rotation().x += 0.9f * theDeltaTime;
        _myCube.rotation().y += 0.6f * theDeltaTime;

        /* stick textured plane to mouse */
        _myPlane.position().set(event().mouseX * 0.25f, event().mouseY * 0.25f);
    }

    public static void main(String[] args) {
        new UsingRenderToTexture().init();
    }
}
