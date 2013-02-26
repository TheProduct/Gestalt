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


import gestalt.context.GLContext;


/**
 * the drawable is the core interface in the gestalt library.
 * drawables are collected to be drawn at a specific time.
 * they can be everything from controller, plugins or actual shapes.
 */

public interface Drawable {

    /* drawing */

    /**
     * this method is used to draw the drawable, when the context is ready.
     * although the behavior sometimes varies and doesn t always need to
     * have a visual representation depending on the implementation.
     * @param theRenderContext GLContext
     */
    void draw(final GLContext theRenderContext);


    /**
     * this method is commonly used to store children of a drawable.
     * @param theDrawable Drawable
     */
    void add(Drawable theDrawable);


    /**
     * returns whether the drawable is active. it is commonly implemented
     * so that the drawables 'draw' method will be ommitted if 'isActive'
     * return false, although this behavior can vary depending on the
     * implementation.
     * @return boolean
     */
    boolean isActive();


    /* sorting */

    /**
     * returns the last sort value.
     * @return float
     */
    float getSortValue();


    /**
     * caches the current sort value.
     * @param theSortValue float
     */
    void setSortValue(float theSortValue);


    /**
     * returns the data upon which a sort value is calculated.
     * @return float[]
     */
    float[] getSortData();


    /**
     * returns true if the 'drawable' should be sorted before drawing.
     * @return boolean
     */
    boolean isSortable();
}
