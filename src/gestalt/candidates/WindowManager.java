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
import gestalt.render.bin.Bin;
import gestalt.render.controller.Camera;
import gestalt.shape.AbstractDrawable;

import java.util.Vector;


/**
 * the windowmanager renders bins into the same view.
 */
public class WindowManager
        extends AbstractDrawable {

    private Vector<Camera> _myCamera;

    private Vector<Bin> _myBin;

    public WindowManager() {
        _myCamera = new Vector<Camera>();
        _myBin = new Vector<Bin>();
    }

    public void add(Camera theCamera, Bin theBin) {
        if (theCamera != null && theBin != null) {
            _myCamera.add(theCamera);
            _myBin.add(theBin);
        }
    }

    public Camera camera(int theID) {
        return _myCamera.get(theID);
    }

    public Bin bin(int theID) {
        return _myBin.get(theID);
    }

    public int size() {
        if (_myCamera.size() != _myBin.size()) {
            System.err.println("### WARNING @ WindowManager / data possibly corrupted.");
        }
        return _myCamera.size();
    }

    public void draw(GLContext theRenderContext) {
        for (int i = 0; i < _myCamera.size(); i++) {
            final Camera myCamera = _myCamera.get(i);
            final Bin myBin = _myBin.get(i);
            myCamera.draw(theRenderContext);
            myBin.draw(theRenderContext);
        }
    }
}
