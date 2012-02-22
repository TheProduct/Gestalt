/*
 * Werkzeug
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


package werkzeug;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;


public class Output {

    private static TextArea textarea;

    private static Frame frame;

    public static void init() {
        frame = new Frame();
        frame.setResizable(false);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));

        textarea = new TextArea(40, 120);
        textarea.setFont(new Font("Courier", Font.PLAIN, 11));
        frame.add(textarea);

        frame.pack();
        frame.setVisible(true);
    }


    public static void snitchSystemOutput() {
        /* out */
        PrintStream myStream = appendPrintStream("OUT: ");
        System.setOut(myStream);
        /* err */
        PrintStream myErrStream = appendPrintStream("ERR: ");
        System.setErr(myErrStream);
    }


    private static PrintStream getSnitchablePrintStream(final String theInfo,
                                                        final TextArea theTextOutput) {

        if (frame == null || textarea == null) {
            init();
        }

        final PrintStream myStream = new PrintStream(
            new OutputStream() {

            private StringBuffer myLine = new StringBuffer();

            public void write(int b) {
                myLine.append( (char) b);
                if (b == '\n') {
                    theTextOutput.append(theInfo);
                    theTextOutput.append(myLine.toString());
                    myLine = new StringBuffer();
                }
            }
        }
        );
        return myStream;
    }


//    public static PrintStream snitchPrintStream(Window theFrame) {
//        TextArea myTextOutput = new TextArea(40, 120);
//        myTextOutput.setFont(new Font("Courier", Font.PLAIN, 11));
//
//        PrintStream myStream = getSnitchablePrintStream("", myTextOutput);
//
//        if (theFrame == null) {
//            final Frame myFrame = new Frame();
//            myFrame.setResizable(false);
//            myFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
//
//            myFrame.add(myTextOutput);
//
//            myFrame.pack();
//            myFrame.setVisible(true);
//        } else {
//            theFrame.add(myTextOutput);
//        }
//
//        return myStream;
//    }


//    public static PrintStream snitchPrintStream() {
//        return snitchPrintStream( (String)null);
//    }
//
//
//    public static PrintStream snitchPrintStream(String theWindowName) {
//        final Frame myFrame = new Frame(theWindowName);
//        myFrame.setResizable(false);
//        myFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
//
//        PrintStream myStream = snitchPrintStream(myFrame);
//
//        myFrame.pack();
//        myFrame.setVisible(true);
//
//        return myStream;
//    }

    public static PrintStream devnull() {
        final PrintStream myStream = new PrintStream(
            new OutputStream() {
            public void write(int b) {
            }
        }
        );
        return myStream;
    }


    public static PrintStream fileStream(String theFile) {
        PrintStream myStream = null;
        File myFile = null;

        try {
            myFile = new File(theFile);
//            myStream = new PrintStream(myFile);
            myStream = new PrintStream(new FileOutputStream(myFile),
                                       true,
                                       "UTF-16");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return myStream;
    }


    public static PrintStream appendPrintStream(String theName) {
        if (frame == null || textarea == null) {
            init();
        }

        PrintStream myStream = getSnitchablePrintStream(theName, textarea);
        return myStream;
    }
}
