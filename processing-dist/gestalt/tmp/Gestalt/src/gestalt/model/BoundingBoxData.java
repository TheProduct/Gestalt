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


package gestalt.model;


import mathematik.Vector3f;


public class BoundingBoxData {

    /* front */

    // 0----3
    // |    |
    // |    |
    // 1----2

    public Vector3f p0 = new Vector3f();

    public Vector3f p1 = new Vector3f();

    public Vector3f p2 = new Vector3f();

    public Vector3f p3 = new Vector3f();

    /* back */

    // 4----7
    // |    |
    // |    |
    // 5----6

    public Vector3f p4 = new Vector3f();

    public Vector3f p5 = new Vector3f();

    public Vector3f p6 = new Vector3f();

    public Vector3f p7 = new Vector3f();

    /* center */

    public Vector3f center = new Vector3f();

    /* size */

    public Vector3f size = new Vector3f();

    public BoundingBoxData() {}


    public String toString() {
        return new String("BoundingBoxData of Box: " +
                          "\np0:     " + p0 +
                          "\np1:     " + p1 +
                          "\np2:     " + p2 +
                          "\np3:     " + p3 +
                          "\np4:     " + p4 +
                          "\np5:     " + p5 +
                          "\np6:     " + p6 +
                          "\np7:     " + p7 +
                          "\ncenter: " + center +
                          "\nsize:   " + size);
    }
}
