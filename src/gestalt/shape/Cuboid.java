/*
 * Gestalt
 *
 * Copyright (C) 2011 Patrick Kochlik + Dennis Paul
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


package gestalt.shape;

import gestalt.material.Material;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.atom.AtomCube;
import gestalt.shape.atom.AtomCubeWrappedTexture;
import gestalt.material.TexturePlugin;
import gestalt.util.JoglUtil;

import javax.media.opengl.GL;

import static gestalt.Gestalt.*;


public class Cuboid
        extends AbstractShape {

    protected int _myOrigin;

    protected int _myTextureMode;

    public Cuboid() {
        material.transparent = false;
        origin(SHAPE_ORIGIN_CENTERED);
        _myTextureMode = SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE;

        material = new Material();
    }

    public void setCubeSizeToTextureSize() {
        TexturePlugin myTexturePlugin = material.texture();
        if (myTexturePlugin != null) {
            scale.set(myTexturePlugin.getPixelWidth(),
                      myTexturePlugin.getPixelHeight());
            scale.z = (scale.x + scale.y) / 2f;
        }
    }

    public void setTextureMode(int theTextureMode) {
        _myTextureMode = theTextureMode;
    }

    public final void origin(int theOrigin) {
        _myOrigin = theOrigin;
    }

    public int origin() {
        return _myOrigin;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* begin material */
        material.begin(theRenderContext);

        /* geometrie */
        gl.glPushMatrix();
        JoglUtil.applyTransform(gl,
                                _myTransformMode,
                                transform,
                                rotation,
                                scale);

        /* draw shape */
        switch (_myTextureMode) {
            case SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE:
                AtomCube.draw(gl,
                              _myOrigin);
                break;
            case SHAPE_CUBE_TEXTURE_WRAP_AROUND:
                AtomCubeWrappedTexture.draw(gl,
                                            _myOrigin);
                break;
        }

        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);
    }
}
