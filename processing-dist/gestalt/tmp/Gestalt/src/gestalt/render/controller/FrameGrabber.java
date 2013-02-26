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


package gestalt.render.controller;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.shape.AbstractDrawable;
import gestalt.util.ImageUtil;

import mathematik.Vector2i;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.media.opengl.GL;


public class FrameGrabber
        extends AbstractDrawable {

    protected boolean _myIsActive;

    protected String _myFileName;

    protected int _myImageFileFormat;

    protected int _myFrameCounter;

    protected int _myDigits;

    protected final Vector2i _myGrabOffset = new Vector2i();

    protected final Vector2i _myGrabScale = new Vector2i();

    private boolean _myCaptureSingleFrame = false;

    public FrameGrabber() {
        _myIsActive = false;
        _myFileName = "frame";
        _myImageFileFormat = Gestalt.IMAGE_FILEFORMAT_PNG;
        _myFrameCounter = 0;
        _myDigits = 5;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* force execution of gl commands */
        gl.glFlush();

        if (_myImageFileFormat == Gestalt.IMAGE_FILEFORMAT_TGA) {
            /* we strongly discourage using this mechanism, it seems to have a severe memory leak */
            grabFrame(gl,
                      region_offset().x,
                      region_offset().y,
                      region_scale().x == 0 ? theRenderContext.displaycapabilities.width : region_scale().x,
                      region_scale().y == 0 ? theRenderContext.displaycapabilities.height : region_scale().y,
                      _myFileName + _myFrameCounter + ".tga");
        } else {
            final ByteBitmap myBitmap = grabFrame(gl,
                                                  region_offset().x,
                                                  region_offset().y,
                                                  region_scale().x == 0 ? theRenderContext.displaycapabilities.width : region_scale().x,
                                                  region_scale().y == 0 ? theRenderContext.displaycapabilities.height : region_scale().y);
            /* save to disc */
//            String myExtension = "";
//            if (_myImageFileFormat == Gestalt.IMAGE_FILEFORMAT_PNG) {
//                myExtension = ".png";
//            } else if (_myImageFileFormat == Gestalt.IMAGE_FILEFORMAT_JPEG) {
//                myExtension = ".jpg";
//            }
            ImageUtil.save(ImageUtil.flip(ImageUtil.convertByteBitmap2BufferedImageBGR(myBitmap), ImageUtil.VERTICAL),
                           _myFileName + (_myCaptureSingleFrame ? "" : werkzeug.Util.formatNumber(_myFrameCounter, _myDigits)),// + myExtension,
                           _myImageFileFormat);
        }
        /* capture one frame only */
        if (_myCaptureSingleFrame) {
            _myCaptureSingleFrame = false;
            _myIsActive = false;
        }
        _myFrameCounter++;
    }

    private ByteBitmap grabFrame(GL gl,
                                 int x,
                                 int y,
                                 int width,
                                 int height) {

        /** @todo JSR-231 -- is this the best way to do it? */
        final byte[] myData = new byte[width * height * ByteBitmap.NUMBER_OF_PIXEL_COMPONENTS];
        ByteBuffer myBuffer = ByteBuffer.wrap(myData);
        gl.glReadPixels(x, y, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, myBuffer);
        ByteBitmap myBitmap = new ByteBitmap(myData,
                                             width,
                                             height,
                                             Gestalt.BITMAP_COMPONENT_ORDER_RGBA);
        return myBitmap;
    }


    /* grabbed this from cwei @ the jogl forum */
    public static final int TARGA_HEADER_SIZE = 18;

    private void grabFrame(GL gl,
                           int x,
                           int y,
                           int width,
                           int height,
                           String theFileName) {
        try {
            try {
                /* create directory if needed */
                File myParent = new File(theFileName).getParentFile();
                if (!myParent.exists()) {
                    myParent.mkdirs();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            RandomAccessFile out = new RandomAccessFile(new File(theFileName), "rw");
            FileChannel ch = out.getChannel();
            int fileLength = TARGA_HEADER_SIZE + width * height * 3;
            out.setLength(fileLength);
            MappedByteBuffer image = ch.map(FileChannel.MapMode.READ_WRITE, 0, fileLength);

            // write the TARGA header
            image.put(0, (byte)0).put(1, (byte)0);
            image.put(2, (byte)2); // uncompressed type
            image.put(12, (byte)(width & 0xFF));
            image.put(13, (byte)(width >> 8));
            image.put(14, (byte)(height & 0xFF));
            image.put(15, (byte)(height >> 8));
            image.put(16, (byte)24); // pixel size

            // go to image data position
            image.position(TARGA_HEADER_SIZE);
            // jogl needs a sliced buffer
            ByteBuffer bgr = image.slice();
            // read the BGR values into the image buffer
            gl.glReadPixels(x,
                            y,
                            width,
                            height,
                            GL.GL_BGR,
                            GL.GL_UNSIGNED_BYTE,
                            bgr);

            // close the file channel
            ch.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this method captures one frame only, in contrast to the 'start()/stop()' mechanism.
     */
    public void grabSingleFrame() {
        _myCaptureSingleFrame = true;
        _myIsActive = true;
    }

    public void region(int x, int y, int width, int height) {
        _myGrabOffset.set(x, y);
        _myGrabScale.set(width, height);
    }

    public Vector2i region_offset() {
        return _myGrabOffset;
    }

    public Vector2i region_scale() {
        return _myGrabScale;
    }

    public void number_of_digits(final int theNumberOfDigits) {
        _myDigits = theNumberOfDigits;
    }

    public void setFrameCounter(int theFrameCounter) {
        _myFrameCounter = theFrameCounter;
    }

    public void setImageFileFormat(int theFileFormat) {
        _myImageFileFormat = theFileFormat;
    }

    public void setFileName(String theFilename) {
        _myFileName = theFilename;
    }

    public void start() {
        _myIsActive = true;
    }

    public void stop() {
        _myIsActive = false;
    }

    public boolean isActive() {
        return _myIsActive;
    }
}
