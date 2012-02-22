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


/**
 * takes care of creating the opengl context or window for the renderer. it also
 * passes key and mouse events to the event handler as they are usually bound to
 * a window resource.
 */

public interface Display {

    public abstract void finish();


    public abstract void updateDisplayCapabilities();


    public abstract void display();


    public abstract void initialize();


    public abstract boolean isDone();


    public abstract boolean hasDrawError();


    public abstract DisplayCapabilities displaycapabilities();


    public abstract GLContext glcontext();

}
