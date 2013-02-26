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


package gestalt.extension.quadline;


import gestalt.material.Color;

import mathematik.Vector3f;


public class QuadPrimitive {

    public final Vector3f[] points;

    public final Color[] colors;

    public final Vector3f[] normals;

    public QuadPrimitive() {
        points = new Vector3f[4];
        points[0] = new Vector3f();
        points[1] = new Vector3f();
        points[2] = new Vector3f();
        points[3] = new Vector3f();
        colors = new Color[4];
        colors[0] = new Color();
        colors[1] = new Color();
        colors[2] = new Color();
        colors[3] = new Color();
        normals = new Vector3f[2];
        normals[0] = new Vector3f();
        normals[1] = new Vector3f();
    }
}
