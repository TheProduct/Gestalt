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


package gestalt.context;

import gestalt.input.EventHandler;
import gestalt.render.controller.Camera;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;


/**
 * is a structure that is passed to drawables that receive
 * the draw event. it can hold information about texturemanager,
 * camera and eventhandler but is not obligated to do so.
 */
public class GLContext {

    public EventHandler event;

    public Camera camera;

    public DisplayCapabilities displaycapabilities;

    public GL gl;

    public GLU glu;

    public GLAutoDrawable drawable;
}
