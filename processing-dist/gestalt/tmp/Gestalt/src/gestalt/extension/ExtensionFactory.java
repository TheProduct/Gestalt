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


package gestalt.extension;

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.picking.JoglPicker;
import gestalt.extension.picking.Picker;
import gestalt.extension.quadline.JoglQuadBezierCurve;
import gestalt.extension.quadline.JoglQuadLine;
import gestalt.extension.quadline.JoglTubeLine;
import gestalt.extension.quadline.QuadBezierCurve;
import gestalt.extension.quadline.QuadLine;
import gestalt.extension.quadline.TubeLine;
import gestalt.render.controller.FrameGrabber;


public class ExtensionFactory {

    /* picker */
    public Picker openglpicker() {
        return new JoglPicker();
    }


    /* quadline */
    public QuadLine quadline() {
        return new JoglQuadLine();
    }

    public TubeLine tubeline() {
        return new JoglTubeLine();
    }

    public QuadBezierCurve quadbeziercurve() {
        return new JoglQuadBezierCurve();
    }


    /* grabber */
    public FrameGrabber framegrabber() {
        return new FrameGrabber();
    }


    /* shader */
    public ShaderManager shadermanager() {
        return new ShaderManager();
    }
}
