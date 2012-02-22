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


package gestalt.shape;

import gestalt.render.Drawable;


/**
 * this class represents a simple implementation of the 'drawable' interface.
 * it is handy in those cases in which only a few of the methods required by
 * the 'drawable' interface are needed.
 * the 'draw' method is intentionally left unimplemented since it is the only
 * method with a 'real' important functionality.
 */
public abstract class AbstractDrawable
        implements Drawable {

    public void add(Drawable theDrawable) {
    }

    public boolean isActive() {
        return true;
    }

    public float getSortValue() {
        return 0.0f;
    }

    public void setSortValue(float theSortValue) {
    }

    public float[] getSortData() {
        return null;
    }

    public boolean isSortable() {
        return false;
    }
}
