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


package gestalt.render;


import java.util.Vector;

import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;


public class Disposer
        extends AbstractDrawable {

    private final Vector<Disposable> _myDisposables = new Vector<Disposable>();

    /**
     *
     * @param theDisposable Disposable
     */
    public void addDisposable(final Disposable theDisposable) {
        _myDisposables.add(theDisposable);
    }

    public void add(Drawable theDrawable) {
        if (theDrawable instanceof Disposable) {
            addDisposable((Disposable)theDrawable);
        } else {
            System.out.println("### WARNING @" + getClass().getSimpleName() + " / didn t add '" + theDrawable + "'. it is not of type 'Disposable'.");
        }
    }

    public void draw(GLContext theRenderContext) {
        if (_myDisposables.isEmpty()) {
            return;
        }

        for (final Disposable myDisposable : _myDisposables) {
            myDisposable.dispose(theRenderContext);
        }
        _myDisposables.clear();
    }
}
