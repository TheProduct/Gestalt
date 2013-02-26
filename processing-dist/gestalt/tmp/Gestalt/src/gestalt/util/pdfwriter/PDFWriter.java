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


import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import java.awt.Graphics2D;

import gestalt.render.controller.cameraplugins.ScreenWorldCoordinates;
import gestalt.context.GLContext;
import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.shape.AbstractDrawable;

import mathematik.Vector3f;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;


public class PDFWriter
    extends AbstractDrawable {

    private final Document _myDocument;

    private final PdfWriter _myWriter;

    private final PdfContentByte _myContent;

    private final DefaultFontMapper _myMapper;

    private final File _myFile;

    private Graphics2D _myGraphics;

    private final Vector<DrawablePDFTranslator> _myTranslators;

    private ScreenWorldCoordinates _myJoglScreenWorldCoordinates;

//    public PDFWriter(String theFile, int theWidth, int theHeight) {
//        _myFile = new File(theFile);
//        _myDocument = new Document(new Rectangle(theWidth, theHeight));
//        try {
//            _myWriter = PdfWriter.getInstance(_myDocument, new FileOutputStream(_myFile));
//            _myDocument.open();
//            _myContent = _myWriter.getDirectContent();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("### ERROR @ PDFWriter / problem saving the PDF file.");
//        }
//
//        _myMapper = new DefaultFontMapper();
//
//        _myGraphics = _myContent.createGraphics(theWidth, theHeight, _myMapper);
//        _myTranslators = new Vector<DrawablePDFTranslator> ();
//    }

    public PDFWriter(String theFile, int theWidth, int theHeight) {
        _myFile = new File(theFile);
        _myDocument = new Document(new Rectangle(theWidth, theHeight));
        try {
            _myWriter = PdfWriter.getInstance(_myDocument, new FileOutputStream(_myFile));
            _myDocument.open();
            _myContent = _myWriter.getDirectContent();
            _myMapper = new DefaultFontMapper();
            _myGraphics = _myContent.createGraphics(theWidth, theHeight, _myMapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("### ERROR @ PDFWriter / problem saving the PDF file.");
        }

        /* plug default translators */
        _myTranslators = new Vector<DrawablePDFTranslator> ();
        _myTranslators.add(new QuadLinePDFTranslator());
    }


    public void page(int theWidth, int theHeight) {
//        if (_myGraphics == null) {
//            _myGraphics = _myContent.createGraphics(theWidth, theHeight, _myMapper);
//        } else {
        _myGraphics.dispose();
        try {
            _myDocument.newPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _myGraphics = _myContent.createGraphicsShapes(theWidth, theHeight);
//        }
    }


    public Graphics2D g() {
        return _myGraphics;
    }


    public Vector<DrawablePDFTranslator> translator() {
        return _myTranslators;
    }


    public void drawBin(final Bin theBin) {
        /* parse objects */
        Drawable[] mySortables = theBin.getDataRef();
        for (int i = 0; i < theBin.size(); i++) {
            final Drawable myDrawable = mySortables[i];
            if (myDrawable != null && myDrawable.isActive()) {
                parseDrawables(myDrawable);
            }
        }
    }


    private void parseDrawables(final Drawable theDrawable) {

        for (final DrawablePDFTranslator myTranslator : _myTranslators) {
            if (myTranslator.isClass(theDrawable)) {
                myTranslator.parse(this, theDrawable);
                return;
            }
        }

        System.err.println("### WARNING / drawable type unsupported. / " + theDrawable.getClass());
    }


    public void close() {
        _myGraphics.dispose();
        _myDocument.close();
    }


    boolean worldToScreenPosition(final Vector3f theWorldPosition,
                                  final Vector3f theResult) {
        if (!_myJoglScreenWorldCoordinates.initalized) {
            System.out.println(
                "### WARNING @ PDFWriter / writer is not capable of transforming 3D positions to screen positions. " +
                "it is not initialized. you might need to drop it into a bin.");
            return false;
        } else {
            return _myJoglScreenWorldCoordinates.worldToScreenPosition(theWorldPosition, theResult);
        }
    }


    public void draw(GLContext theRenderContext) {
        if (_myJoglScreenWorldCoordinates == null) {
            _myJoglScreenWorldCoordinates = new ScreenWorldCoordinates();
        }
        _myJoglScreenWorldCoordinates.draw(theRenderContext);
    }
}
