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


import gestalt.context.Display;
import gestalt.context.JoglDisplay;
import gestalt.material.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.BreakIterator;
import java.util.Calendar;
import java.util.Vector;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Window;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import mathematik.Vector3f;
import nanoxml.XMLElement;


public class Util {

    /* numbers */
    public static String formatNumber(int theNumber, int theNumberOfDigits) {
        StringBuffer s = new StringBuffer(Integer.toString(theNumber));
        while (s.length() < theNumberOfDigits) {
            s.insert(0, '0');
        }
        return s.toString();
    }

    public static float decimalPlace(float theValue, int theDecimalPlaces) {
        final float myDecimalPlaceClamp = (float)Math.pow(10, theDecimalPlaces);
        return ((int)(theValue * myDecimalPlaceClamp)) / myDecimalPlaceClamp;
    }

    public static int random(int theRange) {
        double myRandomNumber = Math.random() * (double)(theRange + 1);
        return (int)myRandomNumber;
    }

    public static boolean randomEvent(float theChance, float theRange) {
        return Math.random() < (theChance / (theRange + 1.0f));
    }

    public static int random(int theStart, int theEnd) {
        int myRandomNumber = (int)(Math.random() * (Math.abs(theStart)
                + Math.abs(theEnd) + 2) - (Math.abs(theStart) + 1));
        return Math.min(theEnd, Math.max(myRandomNumber, theStart));
    }

    public static float random(float theStart, float theEnd) {
        float myRandomNumber = (float)(Math.random() * (Math.abs(theStart)
                + Math.abs(theEnd)) - Math.abs(theStart));
        return Math.min(theEnd, Math.max(myRandomNumber, theStart));
    }


    /* print arrays */
    public static void printArray(int[] array) {
        if (array != null) {
            for (int i = 0; i < array.length - 1; i++) {
                System.out.print(array[i] + ", ");
            }
            System.out.println(array[array.length - 1]);
        }
    }

    public static void printArray(int[][] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    printArray(array[i]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void printArray(byte[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                System.out.print(toBitString(array[i]) + ", ");
            }
            System.out.println();
        }
    }

    public static void printArray(float[] array) {
        if (array != null) {
            for (int i = 0; i < array.length - 1; i++) {
                System.out.print(array[i] + ", ");
            }
            System.out.println(array[array.length - 1]);
        }
    }

    public static void printArray(double[] array) {
        if (array != null) {
            for (int i = 0; i < array.length - 1; i++) {
                System.out.print(array[i] + ", ");
            }
            System.out.println(array[array.length - 1]);
        }
    }

    public static void printArray(Object[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                System.out.print(array[i].toString() + ", ");
            }
            System.out.println();
        }
    }

    public static float[] addToArray(float[] theArray, float theNewItem) {
        float[] newArray = new float[theArray.length + 1];
        System.arraycopy(theArray, 0, newArray, 0, theArray.length);
        newArray[theArray.length] = theNewItem;
        return newArray;
    }

    public static int[] mergeArrays(int[] theArrayA, int[] theArrayB) {
        int[] myCopyArray = new int[theArrayA.length + theArrayB.length];
        System.arraycopy(theArrayA,
                         0,
                         myCopyArray,
                         0,
                         theArrayA.length);
        System.arraycopy(theArrayB,
                         0,
                         myCopyArray,
                         theArrayA.length,
                         theArrayB.length);
        return myCopyArray;
    }

    public static byte[] convertIntArrayToByteArray(int[] myIntArray, int bytesPerInt) {
        /* create a byte array bytesPerFloat times the size of the byte array */
        byte[] myByteArray = new byte[myIntArray.length * bytesPerInt];
        for (int i = 0; i < myIntArray.length; i++) {
            byte[] myBytes = convertIntToByteArray(myIntArray[i], bytesPerInt);
            for (int j = 0; j < bytesPerInt; j++) {
                myByteArray[i * bytesPerInt + (bytesPerInt - j - 1)] = myBytes[j];
            }
        }
        return myByteArray;
    }

    public static int[] convertByteArrayToIntArray(byte[] myByteArray, int bytesPerInt, boolean signed) {
        /* create an int array 1/bytesPerFloat the size of the byte array */
        int[] myIntArray = new int[myByteArray.length / bytesPerInt];
        for (int i = 0; i < myIntArray.length; i++) {
            int myInt = 0;
            for (int j = 0; j < bytesPerInt; j++) {
                myInt += toUnsignedInt(myByteArray[i * bytesPerInt + j]) << (j * 8);
            }
            myIntArray[i] = myInt;
        }
        if (signed) {
            return unsignedIntArray2signedIntArray(myIntArray, bytesPerInt);
        } else {
            return myIntArray;
        }
    }

    private static int[] unsignedIntArray2signedIntArray(int[] myIntArray, int numberOfBytes) {
        for (int i = 0; i < myIntArray.length; i++) {
            if (myIntArray[i] >= 2 << (numberOfBytes * 8 - 2)) {
                myIntArray[i] -= 2 << (numberOfBytes * 8 - 1);
            }
        }
        return myIntArray;
    }


    /* number / format conversions */
    public static void longToBytes(long l, byte[] b) {
        if (b.length < 8) {
            throw new IllegalArgumentException("Byte array should contain at least 8bytes ");
        }
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte)(l >>> (8 * i));
        }
    }

    public static long bytesToLong(byte[] b) {
        if (b.length < 8) {
            throw new IllegalArgumentException("Byte array should contain at least 8 bytes");
        }

        long l = 0;
        for (int i = 0; i < 8; i++) {
            l += (getUnsignedByte(b[i]) << (8 * i));
        }

        return l;
    }

    public static long getUnsignedByte(byte b) {
        return (b & 128) + (b & 127);
    }

    public static byte[] convertIntToByteArray(int value, int numberOfBytes) {
        byte b[] = new byte[numberOfBytes];
        int i, shift;
        for (i = 0, shift = (b.length - 1) * 8; i < b.length; i++, shift -= 8) {
            b[i] = (byte)(0xFF & (value >> shift));
        }
        return b;
    }

    public static byte fromUnsignedInt(int value) {
        return (byte)value;
    }

    public static byte fromChar(char c) {
        return (byte)(c & 0xFF);
    }

    public static int toUnsignedInt(byte value) {
        return value & 0xff;
    }

    public static char toChar(byte value) {
        return (char)toUnsignedInt(value);
    }

    public static String toBitString(byte value) {
        char[] chars = new char[8];
        int mask = 1;
        for (int i = 0; i < 8; ++i) {
            chars[7 - i] = (value & mask) != 0 ? '1' : '0';
            mask <<= 1;
        }
        return new String(chars);
    }


    /* date */
    public static int second() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public static int minute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static int hour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int day() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int month() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int year() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String now() {
        final String myNow = formatNumber((Calendar.getInstance().get(Calendar.YEAR)), 4)
                + formatNumber((Calendar.getInstance().get(Calendar.MONTH) + 1), 2)
                + formatNumber((Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), 2)
                + formatNumber((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)), 2)
                + formatNumber((Calendar.getInstance().get(Calendar.MINUTE)), 2)
                + formatNumber((Calendar.getInstance().get(Calendar.SECOND)), 2);
        return myNow;
    }

    public static String today() {
        final String myNow = formatNumber((Calendar.getInstance().get(Calendar.YEAR)), 4)
                + formatNumber((Calendar.getInstance().get(Calendar.MONTH) + 1), 2)
                + formatNumber((Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), 2);
        return myNow;
    }


    /* file operations */
    public static InputStream getInputStreamFromURL(String theFilePath) {
        try {
            URL myUrl = new URL(theFilePath);
            InputStream myInputStream = myUrl.openStream();
            return myInputStream;
        } catch (Exception ex) {
            System.out.println("### ERROR @ Util.getInputStreamFromURL / " + ex);
            return null;
        }
    }

    public static OutputStream getOutputStreamFromURL(String theFilePath) {
        try {
            URL myUrl = new URL(theFilePath);
            OutputStream myOutputStream = myUrl.openConnection().getOutputStream();
            return myOutputStream;
        } catch (Exception ex) {
            System.out.println("### ERROR @ Util.getInputStreamFromURL / " + ex);
            return null;
        }
    }

    public static InputStream getInputStream(String thePath) {
        try {
            return new FileInputStream(thePath);
        } catch (FileNotFoundException ex) {
            System.err.println("### ERROR @ Util.getInputStream / couldn t create inputstream " + ex);
            return null;
        }
    }

    public static OutputStream getOutputStream(String theFilePath) {
        try {
            final OutputStream myOutputStream = new FileOutputStream(theFilePath);
            return myOutputStream;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void saveBytes(String filename, byte[] byteData) {
        try {
            createPath(filename);
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(byteData);
            fileOutputStream.close();
        } catch (Exception e) {
            System.err.println("### ERROR @ Util / problem writing data. " + e.toString());
        }
    }

    /**
     *
     * @param theInputStream
     * @return
     */
    public static byte[] loadBytes(final InputStream theInputStream) {
        System.err.println("### WARNING @" + Util.class.getSimpleName() + ".loadBytes(InputStream theInputStream) / method is not tested. please watch memory, data etc.");
        try {
            final BufferedInputStream myBufferedInputStream = new BufferedInputStream(theInputStream);
            final Vector<Byte> mOutput = new Vector<Byte>();

            int myValue = myBufferedInputStream.read();
            while (myValue != -1) {
                mOutput.add((byte)myValue);
                myValue = myBufferedInputStream.read();
            }
            myBufferedInputStream.close();

            /* stoopid way to copy the data */
            byte[] mReturnData = new byte[mOutput.size()];
            for (int i = 0; i < mReturnData.length; i++) {
                mReturnData[i] = mOutput.get(i);
            }
            return mReturnData;
        } catch (IOException e) {
            System.err.println("### ERROR @ loadBytes / " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @deprecated
     * @param theInputStream
     * @return
     */
    public static byte[] _loadBytes(InputStream theInputStream) {
        try {
            final BufferedInputStream myBufferedInputStream = new BufferedInputStream(theInputStream);
            final ByteArrayOutputStream myByteArrayOutputStream = new ByteArrayOutputStream();

            int myValue = myBufferedInputStream.read();
            while (myValue != -1) {
                myByteArrayOutputStream.write(myValue);
                myValue = myBufferedInputStream.read();
            }

            myBufferedInputStream.close();

            return myByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.err.println("### ERROR @ loadBytes / " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] loadBytes(final String theFilePath) {
        try {
            final File file = new File(theFilePath);
            final InputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int)length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            /* close inputstream */
            is.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveInt16bit(String filename, int[] intData) {
        saveBytes(filename, convertIntArrayToByteArray(intData, 2));
    }

    public static void saveString(String theFilePath, String theString) {
        createPath(theFilePath);
        saveString(getOutputStream(theFilePath), theString);
    }

    public static void saveString(OutputStream output, String theString) {
        try {
            final OutputStreamWriter myOutputStreamWriter = new OutputStreamWriter(output, "UTF-8");
            final PrintWriter myPrintWriter = new PrintWriter(myOutputStreamWriter);
            myPrintWriter.println(theString);
            myPrintWriter.flush();
            myOutputStreamWriter.close();
            myPrintWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveStrings(String theFilePath, String[] theStrings) {
        createPath(theFilePath);
        saveStrings(getOutputStream(theFilePath), theStrings);
    }

    public static void saveStrings(OutputStream theOutput, String[] theStrings) {
        try {
            final OutputStreamWriter myOutputStreamWriter = new OutputStreamWriter(theOutput, "UTF-8");
            final PrintWriter myPrintWriter = new PrintWriter(myOutputStreamWriter);
            for (int i = 0; i < theStrings.length; i++) {
                myPrintWriter.println(theStrings[i]);
            }
            myPrintWriter.flush();
            myOutputStreamWriter.close();
            myPrintWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveStrings(OutputStream theOutput, Vector<String> theStrings) {
        try {
            OutputStreamWriter myOutputStreamWriter = new OutputStreamWriter(theOutput, "UTF-8");
            PrintWriter myPrintWriter = new PrintWriter(myOutputStreamWriter);
            for (int i = 0; i < theStrings.size(); i++) {
                myPrintWriter.println(theStrings.get(i));
            }
            myPrintWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveStrings(String theFilePath, Vector<String> theStrings) {
        createPath(theFilePath);
        saveStrings(getOutputStream(theFilePath), theStrings);
    }

    public static Vector<String> loadStrings(final String theFilePath) {
        return loadStrings(getInputStream(theFilePath));
    }

    public static Vector<String> loadStrings(final InputStream theInputStream) {
        try {
            final BufferedReader myBufferedReader = new BufferedReader(new InputStreamReader(theInputStream, "UTF-8"));
            final Vector<String> myStrings = new Vector<String>();
            String myLine = null;
            while ((myLine = myBufferedReader.readLine()) != null) {
                myStrings.add(myLine);
            }
            myBufferedReader.close();
            return myStrings;
        } catch (IOException e) {
            System.err.println("### ERROR @ loadStrings / " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static String loadString(final InputStream theInputStream) {
        try {
            final BufferedReader myBufferedReader = new BufferedReader(new InputStreamReader(theInputStream, "UTF-8"));
            final StringBuffer myStrings = new StringBuffer();
            String myLine = null;
            while ((myLine = myBufferedReader.readLine()) != null) {
                myStrings.append(myLine);
            }
            myBufferedReader.close();
            return myStrings.toString();
        } catch (IOException e) {
            System.err.println("### ERROR @ loadStrings / " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static void createPath(String filename) {
        final File file = new File(filename);
        final String parent = file.getParent();
        if (parent != null) {
            File unit = new File(parent);
            if (!unit.exists()) {
                unit.mkdirs();
            }
        }
    }

    public static PrintStream routeSystemOutToFile(String theOutFilename) {
        try {
            final PrintStream myOutput = new PrintStream(new FileOutputStream(theOutFilename));
            System.setOut(myOutput);
            return myOutput;
        } catch (FileNotFoundException ex) {
            System.err.println("### ERROR @ routeSystemOutToFile / couldn t set new output stream.");
        }
        return null;
    }

    public static PrintStream routeSystemErrToFile(String theErrFilename) {
        try {
            PrintStream myErrOutput = new PrintStream(new FileOutputStream(theErrFilename));
            System.setErr(myErrOutput);
            return myErrOutput;
        } catch (FileNotFoundException ex) {
            System.err.println("### ERROR @ routeSystemErrToFile / couldn t set new output stream.");
        }
        return null;
    }

    public static boolean contains(String theString, CharSequence theContainedString) {
        return theString.indexOf(theContainedString.toString()) > -1;
    }

    public static String wrapText(String string, int theWrapLength) {
        return wrapText(string, "\n", theWrapLength);
    }

    public static String wrapText(String theString, String theLineSeparator, int theWrapLength) {
        final int myStringLength = theString.length();

        if (myStringLength > theWrapLength) {
            StringBuffer myBuffer = new StringBuffer(myStringLength
                    + ((myStringLength / theWrapLength) * 2 * theLineSeparator.length()));
            BreakIterator myLineIterator = BreakIterator.getLineInstance();
            myLineIterator.setText(theString);
            int myStart = myLineIterator.first();
            int myLineStart = myStart;

            for (int myEnd = myLineIterator.next(); myEnd != BreakIterator.DONE; myStart = myEnd, myEnd = myLineIterator.next()) {
                if (myEnd - myLineStart < theWrapLength) {
                    myBuffer.append(theString.substring(myStart, myEnd));
                } else {
                    if (true || myEnd - myStart < theWrapLength) {
                        myBuffer.append(theLineSeparator);
                        myBuffer.append(theString.substring(myStart, myEnd));
                    }
                    myLineStart = myEnd;
                }
            }
            theString = myBuffer.toString();
        }

        return theString;
    }

    public static void snitchSystemOutput(Window theFrame) {
        TextArea myTextOutput = new TextArea(40, 120);
        myTextOutput.setFont(new Font("Courier", Font.PLAIN, 11));
        theFrame.add(myTextOutput);
        /* out */
        PrintStream myStream = getSnitchablePrintStream("OUT: ", myTextOutput);
        System.setOut(myStream);
        /* err */
        PrintStream myErrStream = getSnitchablePrintStream("ERR: ", myTextOutput);
        System.setErr(myErrStream);
    }

    public static void snitchSystemOutput() {
        final Frame myFrame = new Frame();
        myFrame.setResizable(false);
        myFrame.setLayout(new FlowLayout(FlowLayout.LEFT));

        snitchSystemOutput(myFrame);

        myFrame.pack();
        myFrame.setVisible(true);
    }

    public static PrintStream getSnitchablePrintStream(final String theInfo,
                                                       final TextArea theTextOutput) {
        final PrintStream myStream = new PrintStream(
                new OutputStream() {

                    private StringBuffer myLine = new StringBuffer();

                    public void write(int b) {
                        myLine.append((char)b);
                        if (b == '\n') {
                            theTextOutput.append(theInfo);
                            theTextOutput.append(myLine.toString());
                            myLine = new StringBuffer();
                        }
                    }
                });
        return myStream;
    }

    public static Vector<String> getFilesInDirectory(String theFolderPath) {
        return getFilesInDirectory(theFolderPath, true);
    }

    public static Vector<String> getFilesInDirectory(String theFolderPath, String... theExceptions) {
        return getFilesInDirectory(theFolderPath, theExceptions, true);
    }

    public static Vector<String> getFilesInDirectory(String theFolderPath, boolean theFullPath) {
        return getFilesInDirectory(theFolderPath, new String[] {"."}, theFullPath);
    }

    public static Vector<String> getFilesInDirectory(String theFolderPath,
                                                     String[] theExceptions,
                                                     boolean theFullPath) {
        final Vector<String> myEntities = new Vector<String>();
        try {
            final File myFolderFile = new File(theFolderPath);
            if (myFolderFile.isDirectory()) {
                final File[] myFilesInDirectory = myFolderFile.listFiles(new ExcludeFileFilter(theExceptions));
                for (int i = 0; i < myFilesInDirectory.length; i++) {
                    final File myCurrentFile = myFilesInDirectory[i];
                    if (myCurrentFile.isFile()) {
                        if (theFullPath) {
                            myEntities.add(myCurrentFile.getAbsolutePath());
                        } else {
                            myEntities.add(myCurrentFile.getName());
                        }
                    }
                }
            } else {
                System.out.println("### WARNING @ "
                        + Util.class.getName()
                        + ".getFilesInDirectory() / specified path is not a directory ("
                        + theFolderPath + ").");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return myEntities;
    }

    public static Vector<File> getFilesInDirectory(File theFolderPath) {
        final Vector<File> myEntities = new Vector<File>();
        if (theFolderPath.isDirectory()) {
            final File[] myFilesInDirectory = theFolderPath.listFiles(new ExcludeFileFilter(new String[] {"."}));
            for (int i = 0; i < myFilesInDirectory.length; i++) {
                final File myCurrentFile = myFilesInDirectory[i];
                if (myCurrentFile.isFile()) {
                    myEntities.add(myCurrentFile);
                }
            }
        } else {
            System.out.println("### WARNING @ "
                    + Util.class.getName()
                    + ".getFilesInDirectory() / specified path is not a directory ("
                    + theFolderPath + ").");
        }
        return myEntities;
    }

    public static Vector<String> getFilesInDirectoryContaining(final String theFolderPath, String... theIncludes) {
        final boolean theFullPath = true;
        final Vector<String> myEntities = new Vector<String>();
        try {
            final File myFolderFile = new File(theFolderPath);
            if (myFolderFile.isDirectory()) {
                final File[] myFilesInDirectory = myFolderFile.listFiles(new IncludeFileFilter(theIncludes));
                for (int i = 0; i < myFilesInDirectory.length; i++) {
                    final File myCurrentFile = myFilesInDirectory[i];
                    if (myCurrentFile.isFile()) {
                        if (theFullPath) {
                            myEntities.add(myCurrentFile.getAbsolutePath());
                        } else {
                            myEntities.add(myCurrentFile.getName());
                        }
                    }
                }
            } else {
                System.out.println("### WARNING @ "
                        + Util.class.getName()
                        + ".getFilesInDirectory() / specified path is not a directory ("
                        + theFolderPath + ").");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return myEntities;
    }

    private static class IncludeFileFilter
            implements FilenameFilter {

        private final String[] _myInclude;

        public IncludeFileFilter(final String[] theInclude) {
            _myInclude = theInclude;
        }

        public boolean accept(File theFile, String theName) {
            for (int i = 0; i < _myInclude.length; i++) {
                if (theName.contains(_myInclude[i])) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class ExcludeFileFilter
            implements FilenameFilter {

        private final String[] _myExceptions;

        public ExcludeFileFilter(final String[] theExceptions) {
            _myExceptions = theExceptions;
        }

        public boolean accept(File theFile, String theName) {
            boolean myResult = true;
            for (int i = 0; i < _myExceptions.length; i++) {
                myResult = myResult && !theName.startsWith(_myExceptions[i]);
            }
            return myResult;
        }
    }

    public static String selectFileName(final Display theDisplay,
                                        final String thePath,
                                        final String theSuffix) {
        return selectFileName(((JoglDisplay)theDisplay).getFrame(), thePath, theSuffix, false);
    }

    public static String selectFileName(final Frame theFrame,
                                        final String thePath,
                                        final String theSuffix,
                                        final boolean theAbsolutePath) {
        final JFileChooser myChooser = new JFileChooser();
        myChooser.setCurrentDirectory(new File(thePath));
        final int myReturnValue = myChooser.showOpenDialog(theFrame);
        if (myReturnValue == JFileChooser.APPROVE_OPTION) {
            final File myFile = myChooser.getSelectedFile();
            if (myFile.getName().endsWith(theSuffix)) {
                if (theAbsolutePath) {
                    return myChooser.getSelectedFile().getAbsolutePath();
                } else {
                    return myChooser.getSelectedFile().getName();
                }
            }
        }
        return null;
    }

    public static InputStream selectFileStream(final Display theDisplay,
                                               final String thePath,
                                               final String theSuffix) {
        return selectFileStream(((JoglDisplay)theDisplay).getFrame(), thePath, theSuffix);
    }

    public static InputStream selectFileStream(final Frame theFrame,
                                               final String thePath,
                                               final String theSuffix) {
        final JFileChooser myChooser = new JFileChooser();
        myChooser.setCurrentDirectory(new File(thePath));
        final int myReturnValue = myChooser.showOpenDialog(theFrame);
        if (myReturnValue == JFileChooser.APPROVE_OPTION) {
            final File myFile = myChooser.getSelectedFile();
            if (myFile.getName().endsWith(theSuffix)) {
                try {
                    System.out.println("### selected file '" + myChooser.getSelectedFile().getAbsolutePath() + "'.");
                    return myChooser.getSelectedFile().toURL().openStream();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }


    /* sound */
    private static Synthesizer _mySynthesizer;

    private static final void createSynth() {
        if (_mySynthesizer == null) {
            try {
                _mySynthesizer = MidiSystem.getSynthesizer();
                if (!_mySynthesizer.isOpen()) {
                    _mySynthesizer.open();
                }
            } catch (MidiUnavailableException ex) {
            }
        }
    }

    public static final void playWarning() {
        playNote(10, 64, 127);
    }

    public static final void playNote(int theChannel, int theKey, int theVelocity) {
        createSynth();
        _mySynthesizer.getChannels()[theChannel].noteOn(theKey, theVelocity);
    }

    public static final void stopSynth() {
        if (_mySynthesizer == null) {
            return;
        }
        _mySynthesizer.close();
        _mySynthesizer = null;
    }

    public static void info(String theMessage) {
        JOptionPane.showMessageDialog(null, theMessage);
    }

    public static boolean dialog(String theMessage) {
        int value = JOptionPane.showConfirmDialog(null,
                                                  theMessage,
                                                  null,
                                                  JOptionPane.YES_NO_OPTION);
        if (value == JOptionPane.YES_OPTION) {
            return true;
        } else if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION) {
            return false;
        }
        return false;
    }

    /* array operations */
    public static final int[] toArray(final Vector<Integer> theData) {
        int[] myArray = new int[theData.size()];
        for (int i = 0; i < myArray.length; i++) {
            if (theData.get(i) != null) {
                myArray[i] = theData.get(i);
            }
        }
        return myArray;
    }

    public static final float[] toArrayFloat(final Vector<Float> theData) {
        float[] myArray = new float[theData.size()];
        for (int i = 0; i < myArray.length; i++) {
            if (theData.get(i) != null) {
                myArray[i] = theData.get(i);
            }
        }
        return myArray;
    }

    public static float[] toArrayFloat(final Vector3f[] theData) {
        float[] myArray = new float[theData.length * 3];
        for (int i = 0; i < theData.length; i++) {
            if (theData[i] != null) {
                myArray[i * 3 + 0] = theData[i].x;
                myArray[i * 3 + 1] = theData[i].y;
                myArray[i * 3 + 2] = theData[i].z;
            }
        }
        return myArray;
    }

    public static boolean[] toArrayBoolean(final Vector<Boolean> theData) {
        boolean[] myArray = new boolean[theData.size()];
        for (int i = 0; i < myArray.length; i++) {
            if (theData.get(i) != null) {
                myArray[i] = theData.get(i);
            }
        }
        return myArray;
    }

    public static char[] toArrayChar(final Vector<Character> theData) {
        char[] myArray = new char[theData.size()];
        for (int i = 0; i < myArray.length; i++) {
            if (theData.get(i) != null) {
                myArray[i] = theData.get(i);
            }
        }
        return myArray;
    }

    public static byte[] toArrayByte(final Vector<Byte> theData) {
        byte[] myArray = new byte[theData.size()];
        for (int i = 0; i < myArray.length; i++) {
            if (theData.get(i) != null) {
                myArray[i] = theData.get(i);
            }
        }
        return myArray;
    }

    public static float[] toArray3f(final Vector<Vector3f> theData) {
        float[] myArray = new float[theData.size() * 3];
        for (int i = 0; i < theData.size(); i++) {
            final Vector3f v = theData.get(i);
            if (v != null) {
                myArray[i * 3 + 0] = v.x;
                myArray[i * 3 + 1] = v.y;
                myArray[i * 3 + 2] = v.z;
            }
        }
        return myArray;
    }

    public static Vector3f[] toArrayVector3f(final Vector<Vector3f> theData) {
        Vector3f[] myArray = new Vector3f[theData.size()];
        for (int i = 0; i < theData.size(); i++) {
            myArray[i] = theData.get(i);
        }
        return myArray;
    }

    public static Color[] toArrayColor(final Vector<Color> theData) {
        Color[] myArray = new Color[theData.size()];
        for (int i = 0; i < theData.size(); i++) {
            myArray[i] = theData.get(i);
        }
        return myArray;
    }

    public static float[] toArrayFromColor(final Vector<Color> theData) {
        float[] myArray = new float[theData.size() * 4];
        for (int i = 0; i < theData.size(); i++) {
            final Color v = theData.get(i);
            if (v != null) {
                myArray[i * 4 + 0] = v.r;
                myArray[i * 4 + 1] = v.g;
                myArray[i * 4 + 2] = v.b;
                myArray[i * 4 + 3] = v.b;
            }
        }
        return myArray;
    }

    public static float[] toArrayFromColor(final Color[] theData) {
        float[] myArray = new float[theData.length * 4];
        for (int i = 0; i < theData.length; i++) {
            final Color v = theData[i];
            if (v != null) {
                myArray[i * 4 + 0] = v.r;
                myArray[i * 4 + 1] = v.g;
                myArray[i * 4 + 2] = v.b;
                myArray[i * 4 + 3] = v.b;
            }
        }
        return myArray;
    }

    public static float[] toArray4f(final Vector<Color> theData) {
        float[] myArray = new float[theData.size() * 4];
        for (int i = 0; i < theData.size(); i++) {
            final Color v = theData.get(i);
            if (v != null) {
                myArray[i * 4 + 0] = v.r;
                myArray[i * 4 + 1] = v.g;
                myArray[i * 4 + 2] = v.b;
                myArray[i * 4 + 3] = v.a;
            }
        }
        return myArray;
    }

    public static Vector<Vector3f> loadPoints(String theFileName) {
        final Vector<Vector3f> myPoints = new Vector<Vector3f>();
        loadPoints(theFileName, myPoints);
        return myPoints;
    }

    public static void loadPoints(String theFileName, final Vector<Vector3f> thePoints) {
        Vector<String> myNewString = werkzeug.Util.loadStrings(theFileName);
        for (String s : myNewString) {
            thePoints.add(new Vector3f(s));
        }
        System.out.println("### done loading points");
    }

    public static void savePoints(final String theFilename, final Vector<Vector3f> thePoints) {
        final Vector<String> myPointStrings = new Vector<String>();
        for (Vector3f v : thePoints) {
            myPointStrings.add(v.toString());
        }
        werkzeug.Util.saveStrings(theFilename, myPointStrings);
        System.out.println("### done saving points");
    }

    public int to1D(final int x, final int y, final int width) {
        final int i = x + y * width;
        return i;
    }

    public int[] to2D(final int i, final int width) {
        final int x = i % width;
        final int y = i / width;
        return new int[] {x, y};
    }

    public static byte[] zip(byte[] input) {
        // Create the compressor with highest level of compression
        final Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
        return compressedData;
    }

    public static byte[] unzip(byte[] compressedData) {
        // Create the decompressor and give it the data to compress
        final Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        // Create an expandable byte array to hold the decompressed data
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the decompressed data
        final byte[] decompressedData = bos.toByteArray();
        return decompressedData;
    }

    public static <T extends Serializable> void serialize(final String pFilename, final T pObject) {
        try {
            final FileOutputStream myFileOutputStream = new FileOutputStream(pFilename);
            final ObjectOutputStream myObjectOutputStream = new ObjectOutputStream(myFileOutputStream);
            myObjectOutputStream.writeObject(pObject);
            myObjectOutputStream.close();
            myFileOutputStream.close();
        } catch (IOException e) {
            System.err.println("### ERROR @ serialize / " + e);
        }
    }

    public static <T extends Serializable> T unserialize(final String pFilename, final Class<T> pClass) {
        try {
            final FileInputStream myFileInputStream = new FileInputStream(pFilename);
            final ObjectInputStream myObjectInputStream = new ObjectInputStream(myFileInputStream);
            Object myObject = myObjectInputStream.readObject();
            myObjectInputStream.close();
            myFileInputStream.close();
            if (pClass.isInstance(myObject)) {
                @SuppressWarnings("unchecked")
                final T mObject = (T)myObject;
                return mObject;
            } else {
                System.err.println("### ERROR @ unserialize / unserialized object is not an instance of class " + pClass.getSimpleName());
            }
        } catch (IOException e) {
            System.err.println("### ERROR @ unserialize / " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("### ERROR @ unserialize / " + e);
        }
        return null;
    }

    public static XMLElement loadXML(final String pAbsoluteFilePath) {
        final XMLElement mLocalXML = new XMLElement(new Hashtable(), false, false);
        try {
            mLocalXML.parseFromReader(new InputStreamReader(werkzeug.Util.getInputStream(pAbsoluteFilePath)));
        } catch (IOException ex) {
            System.err.println("### ERROR / couldn t read XML file." + ex);
        }
        return mLocalXML;
    }

    public static void saveXML(final String pAbsoluteFilePath, final XMLElement pXML) {
        try {
            final FileWriter mWriter = new FileWriter(new File(pAbsoluteFilePath));
//            final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
//            mWriter.append(HEADER);
            pXML.write(mWriter);
            mWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
