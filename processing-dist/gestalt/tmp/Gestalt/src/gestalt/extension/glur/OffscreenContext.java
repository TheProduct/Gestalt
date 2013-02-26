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


package gestalt.extension.glur;

import gestalt.Gestalt;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.extension.framebufferobject.JoglTexCreator;
import gestalt.extension.framebufferobject.JoglTexCreatorFBO_DepthRGBA;
import gestalt.render.bin.RenderBin;
import gestalt.render.bin.TwoSidedBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.FrameSetup;
import gestalt.shape.DrawableFactory;
import gestalt.shape.Plane;

import mathematik.Vector2i;

import java.util.Vector;


public class OffscreenContext {

    private final RenderBin _myAdminBin;

    private final TwoSidedBin _myShapeBin;

    private final JoglFrameBufferObject _myFBO;

    private final Vector2i _myDimensions;

    private final Vector<Plane> _myPlane;

    private final FrameSetup _myFrameSetup;

    public OffscreenContext(DrawableFactory theFactory, Vector2i theDimensions, JoglTexCreator theTexCreator) {
        _myAdminBin = new RenderBin();
        _myShapeBin = new TwoSidedBin();
        _myDimensions = new Vector2i(theDimensions);

        _myFrameSetup = theFactory.frameSetup();

        _myFBO = setupFBO(theFactory, theTexCreator);
        _myFBO.add(_myAdminBin);
        _myFBO.add(_myShapeBin);

        _myPlane = new Vector<Plane>();
    }

    public OffscreenContext(DrawableFactory theFactory, Vector2i theDimensions) {
        this(theFactory, theDimensions, new JoglTexCreatorFBO_DepthRGBA());
    }

    public Vector<Plane> displays() {
        return _myPlane;
    }

    public Plane createDisplay(DrawableFactory theFactory) {
        Plane myPlane = theFactory.plane();

        /* create plane to show FBO */
        myPlane.material().addPlugin(_myFBO);
        myPlane.setPlaneSizeToTextureSize();
        myPlane.origin(Gestalt.SHAPE_ORIGIN_TOP_LEFT);
        return myPlane;
    }

    public Camera camera() {
        return _myFBO.camera();
    }

    public void setCameraRef(Camera theCamera) {
        _myFBO.setCameraRef(theCamera);
    }

    public TwoSidedBin bin() {
        return _myShapeBin;
    }

    public RenderBin adminbin() {
        return _myAdminBin;
    }

    public JoglFrameBufferObject fbo() {
        return _myFBO;
    }

    public FrameSetup framesetup() {
        return _myFrameSetup;
    }

    private JoglFrameBufferObject setupFBO(DrawableFactory theFactory, JoglTexCreator theTexCreator) {
        /* create a framesetup for the FBO the clears the screen */
        _myFrameSetup.colorbufferclearing = true;
        _myFrameSetup.depthbufferclearing = true;
        _myAdminBin.add(_myFrameSetup);

        /* create a camera for the framebuffer object */
        Camera myCamera = theFactory.camera();
        myCamera.position().z = _myDimensions.y;
        myCamera.viewport().width = _myDimensions.x;
        myCamera.viewport().height = _myDimensions.y;
        myCamera.farclipping = _myDimensions.y * 2;

        /* create framebuffer object */
        JoglFrameBufferObject myFBO = new JoglFrameBufferObject(_myDimensions.x,
                                                                _myDimensions.y,
                                                                myCamera,
                                                                theTexCreator);
        /* set backgroundcolor of FBO */
        myFBO.backgroundcolor().set(0, 0, 0, 1);

        /* add camera to framebuffer object renderbin */
        _myAdminBin.add(myCamera);

        return myFBO;
    }
}
