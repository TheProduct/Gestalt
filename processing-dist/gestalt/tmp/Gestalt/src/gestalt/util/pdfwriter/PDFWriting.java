package gestalt.util.pdfwriter;


import java.io.File;
import java.io.FileOutputStream;

import java.awt.Graphics2D;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;


public class PDFWriting {

    private final Document document;

    private final PdfWriter writer;

    private final PdfContentByte content;

    private final DefaultFontMapper mapper;

    private final File file;

    public Graphics2D g;

    public PDFWriting(String theFile, int theWidth, int theHeight) {
        file = new File(theFile);
        document = new Document(new Rectangle(theWidth, theHeight));
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            content = writer.getDirectContent();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem saving the PDF file.");
        }

        mapper = new DefaultFontMapper();

        g = content.createGraphics(theWidth, theHeight, mapper);
    }


    public void nextPage(int theWidth, int theHeight) {
        g.dispose();
        try {
            document.newPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        g = content.createGraphicsShapes(theWidth, theHeight);
    }


    public void close() {
        g.dispose();
        document.close();
    }
}
