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


package gestalt.candidates;

import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.bitmap.ByteBufferBitmap;
import gestalt.render.bin.Bin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.FrameBufferCopy;
import gestalt.render.controller.Viewport;
import gestalt.shape.AbstractDrawable;


/**
 * draws the bin in a specified area of the view and grabs the area
 * into a texture.<br/>
 * this class is great for creating subjective views for entities in a world.<br/>
 * see also 'UsingAPersonalPointOfView'
 */
public class JoglPersonalPointView
        extends AbstractDrawable {

    private final Viewport _myViewport;

    private final Camera _myCamera;

    private final Bin _myBin;

    private final TexturePlugin _myTexture;

    private final FrameBufferCopy _myJoglFrameBufferCopy;

    public JoglPersonalPointView(final Bin theBin,
                                 final Viewport theViewport,
                                 final Camera theCamera,
                                 final TexturePlugin theTexture) {
        _myBin = theBin;
        _myViewport = new Viewport(theViewport);

        _myTexture = theTexture;
        _myTexture.load(ByteBufferBitmap.getDefaultImageBitmap(_myViewport.width,
                                                               _myViewport.height));

        /* we don t need to flip textures as this one comes from opengl not java */
        _myTexture.scale().y = 1;

        _myCamera = theCamera;

        _myJoglFrameBufferCopy = new FrameBufferCopy(_myTexture);
        _myJoglFrameBufferCopy.colorbufferclearing = true;
        _myJoglFrameBufferCopy.depthbufferclearing = true;
        _myJoglFrameBufferCopy.width = _myViewport.width;
        _myJoglFrameBufferCopy.height = _myViewport.height;
    }

    public Camera camera() {
        return _myCamera;
    }

    public Bin bin() {
        return _myBin;
    }

    public TexturePlugin texture() {
        return _myTexture;
    }

    public FrameBufferCopy framebuffercopy() {
        return _myJoglFrameBufferCopy;
    }

    public void draw(GLContext theRenderContext) {
        /* width and height need to be aligned */
        _myCamera.viewport().width = _myViewport.width;
        _myCamera.viewport().height = _myViewport.height;

        _myJoglFrameBufferCopy.width = _myViewport.width;
        _myJoglFrameBufferCopy.height = _myViewport.height;

        /* draw bin */
        _myCamera.draw(theRenderContext);
        _myBin.draw(theRenderContext);

        /* copy view to texture */
        _myJoglFrameBufferCopy.draw(theRenderContext);
    }
}
