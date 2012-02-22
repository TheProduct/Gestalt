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


package gestalt.extension.materialplugin.joglutiltexture;


import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.media.opengl.GL;

import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.spi.DDSImage;


public class TextureDataSequence
    extends Thread {

    private final TextureData[] _myBuffer;

    private int _myBufferPointer;

    private int _myCachePointer;

    private int _myFilePointer;

    private final Vector<File> _myFiles;

    private int _myUpdateSleep = 30;

    private boolean _myQuit;

    public TextureDataSequence(final int theCacheSize, final Vector<File> theFiles) {
        _myFiles = theFiles;
        _myBuffer = new TextureData[theCacheSize];
        _myBufferPointer = 0;
        _myFilePointer = 0;
        _myCachePointer = 0;
        setPriority(Thread.MIN_PRIORITY);
        _myQuit = false;
    }


    public void run() {
        while (!_myQuit) {
            fillCache();
        }
    }


    public void fillCache() {
        if (getChacheStatus() == _myBuffer.length - 1) {
            try {
                sleep(_myUpdateSleep);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        while (getChacheStatus() < _myBuffer.length - 1) {
            updateBuffer();
        }
    }


    private void updateBuffer() {
        try {
            final TextureData myTextureData;
            myTextureData = getTextureData();

            _myFilePointer++;
            _myFilePointer %= _myFiles.size();

            if (_myBuffer[_myCachePointer] != null) {
                _myBuffer[_myCachePointer].flush();
            }

            _myBuffer[_myCachePointer] = myTextureData;
            _myCachePointer++;
            _myCachePointer %= _myBuffer.length;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private final TextureData getTextureData() throws RuntimeException, IOException {
        DDSImage _myImage = DDSImage.read(_myFiles.get(_myFilePointer));
        DDSImage.ImageInfo _myInfo = _myImage.getMipMap(0);
        TextureData.Flusher _myFlusher = new TextureData.Flusher() {
            public void flush() {
//                    image.close();
            }
        };

        int myPixelFormat = 0;
        int myInternalFormat = 0;

        if (myPixelFormat == 0) {
            switch (_myImage.getPixelFormat()) {
                case DDSImage.D3DFMT_R8G8B8:
                    myPixelFormat = GL.GL_RGB;
                    break;
                default:
                    myPixelFormat = GL.GL_RGBA;
                    break;
            }
        }
        if (_myInfo.isCompressed()) {
            switch (_myInfo.getCompressionFormat()) {
                case DDSImage.D3DFMT_DXT1:
                    myInternalFormat = GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
                    break;
                case DDSImage.D3DFMT_DXT3:
                    myInternalFormat = GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
                    break;
                case DDSImage.D3DFMT_DXT5:
                    myInternalFormat = GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
                    break;
                default:
                    throw new RuntimeException("Unsupported DDS compression format \"" +
                                               DDSImage.getCompressionFormatName(_myInfo.getCompressionFormat()) + "\"");
            }
        }
        if (myInternalFormat == 0) {
            switch (_myImage.getPixelFormat()) {
                case DDSImage.D3DFMT_R8G8B8:
                    myPixelFormat = GL.GL_RGB;
                    break;
                default:
                    myPixelFormat = GL.GL_RGBA;
                    break;
            }
        }

        return new TextureData(myInternalFormat,
                               _myInfo.getWidth(),
                               _myInfo.getHeight(),
                               0,
                               myPixelFormat,
                               GL.GL_UNSIGNED_BYTE,
                               false,
                               _myInfo.isCompressed(),
                               true,
                               _myInfo.getData(),
                               _myFlusher);
    }


    public int getChacheStatus() {
        int myDelta = _myCachePointer - _myBufferPointer;
        /* wrap delta */
        if (myDelta < 0) {
            myDelta += _myBuffer.length;
        }

        return myDelta;
    }


    public TextureData current() {
        return _myBuffer[_myBufferPointer];
    }


    public TextureData next() {
        final TextureData myTextureData = _myBuffer[_myBufferPointer];
        if (getChacheStatus() > 0) {
            _myBufferPointer++;
            _myBufferPointer %= _myBuffer.length;
            return myTextureData;
        } else {
            System.err.println("### WARNING @" + getClass().getName() + " / buffer underrun.");
            return null;
        }
    }


    public void quit() {
        _myQuit = true;
    }
}
