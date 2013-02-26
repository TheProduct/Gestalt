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


package gestalt.util.pdfwriter;


import java.awt.Polygon;

import gestalt.extension.quadline.QuadLine;
import gestalt.render.Drawable;

import mathematik.Vector3f;


public class QuadLinePDFTranslator
    implements DrawablePDFTranslator {

    public boolean isClass(final Drawable theDrawable) {
        return theDrawable instanceof QuadLine;
    }


    public void parse(final PDFWriter theParent, final Drawable theDrawable) {
        final QuadLine _myQuadline = (QuadLine) theDrawable;

        final Vector3f myPointA = new Vector3f();
        final Vector3f myPointB = new Vector3f();
        final Vector3f myPointC = new Vector3f();
        final Vector3f myPointD = new Vector3f();

        theParent.g().setStroke(null);
        theParent.g().setColor(_myQuadline.material().color4f().createAWTColor());

        if (_myQuadline.getLineFragments().length > 1) {
            for (int i = 1; i < _myQuadline.getLineFragments().length; i++) {
                theParent.worldToScreenPosition(_myQuadline.getLineFragments()[i - 1].pointA, myPointA);
                theParent.worldToScreenPosition(_myQuadline.getLineFragments()[i - 1].pointB, myPointB);
                theParent.worldToScreenPosition(_myQuadline.getLineFragments()[i].pointA, myPointC);
                theParent.worldToScreenPosition(_myQuadline.getLineFragments()[i].pointB, myPointD);

                final Polygon myPolygon = new Polygon();
                myPolygon.addPoint( (int) myPointA.x, (int) myPointA.y);
                myPolygon.addPoint( (int) myPointB.x, (int) myPointB.y);
                myPolygon.addPoint( (int) myPointD.x, (int) myPointD.y);
                myPolygon.addPoint( (int) myPointC.x, (int) myPointC.y);
                theParent.g().fill(myPolygon);
            }
        }
    }
}
