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


import data.Resource;
import gestalt.G;
import gestalt.extension.materialplugin.JoglProxyTexturePlugin;
import gestalt.material.TexturePlugin;
import gestalt.render.SketchRenderer;
import gestalt.shape.Plane;


public class UsingProxyTexture
        extends SketchRenderer {

    public void setup() {
        setupDefaults();
        backgroundcolor().set(0.2f);

        final Plane myPlane = G.plane(Resource.getStream("demo/common/auto.png"));
        final TexturePlugin myTexture = (TexturePlugin)myPlane.material().texture();
        myPlane.position().x = -myPlane.scale().x / 2 - 1;

        final Plane myOtherPlane = G.plane();

        /* create a proxy texture that refers to the original textures opengl ID */
        final JoglProxyTexturePlugin myProxyTexture = new JoglProxyTexturePlugin(myTexture);
        myOtherPlane.material().addTexture(myProxyTexture);
        myOtherPlane.setPlaneSizeToTextureSize();

        /* now texture properties like scale or position are independent of the parent texture  */
        myOtherPlane.material().texture().scale().scale(2.0f);
        myOtherPlane.position().x = myOtherPlane.scale().x / 2 + 1;
    }

    public static void main(String[] args) {
        G.init(UsingProxyTexture.class);
    }
}
