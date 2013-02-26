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


package gestalt.render.bin;

import gestalt.render.Drawable;


public interface Bin
        extends Drawable {

    void add(Drawable theDrawable);

    void add(Drawable[] theDrawables);

    Drawable remove(Drawable theDrawable);

    Drawable remove(int theIndex);

    void remove(Drawable[] theDrawables);

    void set(int theID, Drawable theDrawable);

    void swap(int theIDA,
              int theIDB);

    void swap(Drawable theDrawableA,
              Drawable theDrawableB);

    void replace(Drawable theDrawableOld,
                 Drawable theDrawableNew);

    int find(Drawable theDrawable);

    int size();

    Drawable get(int theID);

    void clear();

    Drawable[] getDataRef();

    String toString();

    void setActive(boolean theState);
}
