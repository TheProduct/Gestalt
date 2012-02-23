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


import javax.media.opengl.GLAutoDrawable;

import gestalt.context.DisplayCapabilities;
import gestalt.context.JoglDisplay;
import gestalt.input.EventHandler;
import gestalt.render.AnimatorRenderer;
import gestalt.render.MinimalRenderer;
import gestalt.shape.Plane;


/**
 * this demo shows how customize the renderer by overriding the display creation
 * to gain more control over the display method.
 * in this example the auto buffer swapping is replaced by a display that
 * manually swaps buffers.
 */

public class UsingCustomizedRenderer
    extends AnimatorRenderer {

    private Plane[] _myPlanes;

    public void setup() {
        /* setup renderer */
        displaycapabilities().backgroundcolor.set(1);

        _myPlanes = new Plane[3];
        for (int i = 0; i < _myPlanes.length; i++) {
            _myPlanes[i] = drawablefactory().plane();
            _myPlanes[i].scale().set(400, 400);
            _myPlanes[i].position().z -= i * 400;
            _myPlanes[i].material().depthtest = true;
            _myPlanes[i].material().color4f().set(0, i * 0.1f + 0.1f);
            bin(BIN_3D).add(_myPlanes[i]);
        }
    }


    public void loop(float theDeltaTime) {
        for (int i = 0; i < _myPlanes.length; i++) {
            _myPlanes[i].rotation().x += (i + 1) * theDeltaTime * 0.2f;
            _myPlanes[i].rotation().y += (i + 1) * theDeltaTime * 0.198f;
        }
    }


    protected void createDisplay(DisplayCapabilities theDisplayCapabilities) {
        _myDisplay = new MyJoglDisplay(theDisplayCapabilities, this, _myEvent);
        _myDisplay.initialize();
    }


    private static class MyJoglDisplay
        extends JoglDisplay {

        public MyJoglDisplay(DisplayCapabilities theDisplayCapabilities,
                             MinimalRenderer theRenderer,
                             EventHandler theEventHandler) {
            super(theDisplayCapabilities, theRenderer, theEventHandler);
        }


        public void display(GLAutoDrawable theDrawable) {

            /* use this for normal mode */
            super.display(theDrawable);

            /* use this for stereo mode */

            /* select right buffer */
            // gl.glDrawBuffer(GL.GL_BACK_RIGHT);
            /* clear buffer */
            /* set camera */
            /* draw geometrie */

            /* select right buffer */
            // gl.glDrawBuffer(GL.GL_BACK_LEFT);
            /* clear buffer */
            /* set camera */
            /* draw geometrie */
        }
    }


    public static void main(String[] args) {
        JoglDisplay.ENABLE_STEREO_VIEW = true;
        new UsingCustomizedRenderer().init();
    }
}
