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


package gestalt.material.texture;


import gestalt.Gestalt;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.material.texture.bitmap.IntegerBitmap;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.MoviePlayer;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.Pict;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.MovieDrawingComplete;
import quicktime.std.movies.TimeInfo;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.VideoMedia;
import quicktime.util.QTUtils;
import quicktime.util.RawEncodedImage;


public class QTMovieProducer
        implements StdQTConstants,
                   MovieDrawingComplete,
                   Runnable {

    public static boolean VERBOSE = false;

    private static final boolean USE_PICT_METHOD = false;

    public final boolean PRELOAD;

    private int _myChannels;

    private Bitmap _myBitmap;

    private Class _myBitmapClass;

    private boolean _myLoadStatus;

    private QTFile _myFile;

    private String _myFileName;

    private Movie _myMovie;

    private int _myCurrentFrame;

    private QDGraphics _myQDGraphics;

    private QDRect _myBox;

    private RawEncodedImage _myRawImage;

    private PixMap _myPixMap;

    private MoviePlayer _myMoviePlayer;

    private MovieController _myMovieController;

    private boolean _myIsBusy;

    /* threading */
    private Thread _myRunner;

    private int _myUpdateFPS = 30;

    /**
     * start the movieproducer in extra thread; no need to call 'update()'.
     * @param theFileName String
     * @param thePreloadFlag boolean
     * @param theUpdateFPS int
     */
    public QTMovieProducer(String theFileName, boolean thePreloadFlag, int theUpdateFPS) {
        this(theFileName, thePreloadFlag);
        _myUpdateFPS = theUpdateFPS;
        init();
        _myRunner = new Thread(this);
        _myRunner.start();
    }

    /**
     *
     * @param theFileName String
     * @param thePreloadFlag boolean
     */
    public QTMovieProducer(String theFileName, boolean thePreloadFlag) {
        PRELOAD = thePreloadFlag;
        _myFileName = theFileName;
        _myLoadStatus = false;
        _myCurrentFrame = 0;
        _myIsBusy = false;
        _myBitmap = null;
    }

    public QTMovieProducer(String theFileName, boolean thePreloadFlag, Class theBitmapClass) {
        this(theFileName, thePreloadFlag);
        _myBitmapClass = theBitmapClass;
    }

    public void run() {
        while (Thread.currentThread() == _myRunner) {
            try {
                Thread.sleep(1000 / _myUpdateFPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            update();
        }
    }

    public boolean getStatus() {
        if (_myLoadStatus) {
            return true;
        } else {
            return false;
        }
    }

    public int getWidth() {
        if (_myLoadStatus) {
            return _myBitmap.getWidth();
        } else {
            return 0;
        }
    }

    public int getHeight() {
        if (_myLoadStatus) {
            return _myBitmap.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * @deprecated use 'bitmap()' instead.
     * @return
     */
    public byte[] getData() {
        if (_myLoadStatus && _myBitmap instanceof ByteBitmap) {
            final byte[] myData = ((ByteBitmap)_myBitmap).getByteDataRef();
            return myData;
        } else {
            return null;
        }
    }

    public Bitmap bitmap() {
        return _myBitmap;
    }

    /* --> processing data */
    public void init() {
        try {
            if (!QTSession.isInitialized()) {
                QTSession.open();
                if (VERBOSE) {
                    System.out.print("### QUICKTIME VERSION " +
                            QTSession.getQTMajorVersion() +
                            "." +
                            QTSession.getQTMinorVersion());
                }
            }
            /* get movie */
            _myFile = new QTFile(_myFileName);
            OpenMovieFile openMovieFile = OpenMovieFile.asRead(_myFile);
            _myMovie = Movie.fromFile(openMovieFile);
            if (PRELOAD) {
                _myMovie.loadIntoRam(0, _myMovie.getDuration(), 0);
            }
            _myMovie.setDrawingCompleteProc(0, this);
            /* dimensions */
            _myBox = _myMovie.getBox();

            int myWidth = _myBox.getWidth();
            int myHeight = _myBox.getHeight();
            if (_myBitmapClass == ByteBitmap.class || _myBitmapClass == null) {
                _myBitmap = ByteBitmap.getDefaultImageBitmap(myWidth, myHeight);
            } else if (_myBitmapClass == IntegerBitmap.class) {
                _myBitmap = IntegerBitmap.getDefaultImageBitmap(myWidth, myHeight);
            } else {
                System.out.println("### bitmap type not supported. " + _myBitmapClass);
            }
            /* data */
            _myQDGraphics = new QDGraphics(_myBox);
            _myMoviePlayer = new MoviePlayer(_myMovie);
            _myMovieController = new MovieController(_myMovie);
            _myMoviePlayer.setGWorld(_myQDGraphics);
            _myPixMap = _myQDGraphics.getPixMap();

            _myChannels = _myPixMap.getPixelSize() / 8;
            if (_myChannels != 4) {
                System.err.println("### WARNING @ " +
                        this.getClass() +
                        " / " + _myFileName +
                        " / only RGBA (ARGB) format is really supported.");
            }

//            _myData = new byte[_myChannels * _myWidth * _myHeight];
            /* load first frame */
            _myLoadStatus = true;
        } catch (QTException ex) {
            _myLoadStatus = false;
            System.err.println("### ERROR @ " + this.getClass() +
                    ".init() / " + _myFileName +
                    " / " + ex);
            QTSession.close();
        }
        update();
    }

    public int execute(Movie theMovie) {
        if (theMovie == _myMovie) {
            _myIsBusy = false;
        } else {
            if (VERBOSE) {
                System.out.println("### INFO not same movie " + theMovie +
                        " / " + _myFileName +
                        " / " + _myMovie);
            }
        }
        return 0;
    }

    public void update() {
        try {
            if (!_myIsBusy) {
                _myIsBusy = true;
                _myMovie.task(30);
                _myMovie.update();

                /* get data from the QDGraphics */
                if (!USE_PICT_METHOD) {
                    _myRawImage = _myPixMap.getPixelData();
                } else {
                    Pict myPict = _myMovie.getPict(_myMovie.getTime()); // returns an int
                    myPict.draw(_myQDGraphics, _myBox);
                    PixMap myPixmap = _myQDGraphics.getPixMap();
                    _myRawImage = myPixmap.getPixelData();
                }

                /* copy to the data array */
                if (_myBitmap instanceof ByteBitmap) {
                    byte[] myData = ((ByteBitmap)_myBitmap).getByteDataRef();
                    _myRawImage.copyToArray(0, myData, 0, myData.length);
                } else if (_myBitmap instanceof IntegerBitmap) {
                    int[] myData = ((IntegerBitmap)_myBitmap).getIntDataRef();
                    _myRawImage.copyToArray(0, myData, 0, myData.length);
                }
            } else {
                if (VERBOSE) {
                    System.out.println("### INFO @" + this.getClass() +
                            " / " + _myFileName +
                            " / drawing not completed.");
                }
            }
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".update() / " + _myFileName +
                    " / " + ex);
        }
    }


    /* --> movie controll */
    /**
     * @todo this is not working correctly
     */
    public void nextFrame() {
        frame(_myCurrentFrame++);
    }

    /**
     * @todo this is not working correctly
     */
    public void previousFrame() {
        frame(_myCurrentFrame--);
    }

    public void play(float theSpeed) {
        try {
            _myMovieController.play(theSpeed);
        } catch (StdQTException ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".play() / " + _myFileName +
                    " / " + ex);
        }
    }

    public void stop() {
        try {
            _myMovieController.play(0);
        } catch (StdQTException ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".stop() / " + _myFileName +
                    " / " + ex);
        }
    }

    public void setLooping(boolean looping) {
        try {
            if (looping) {
                _myMovie.getTimeBase().setFlags(StdQTConstants.loopTimeBase);
            } else {
                _myMovie.getTimeBase().setFlags(0);
            }
        } catch (StdQTException ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".setLooping() / " + _myFileName +
                    " / " + ex);
        }
    }

    /**
     * @todo this method might not work.
     * @param frame int
     */
    public void frame(int frame) {
        try {
            _myMovieController.goToTime(new TimeRecord(_myMovie.getTimeScale(), 0));
            _myMovieController.step(frame);
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".frame() / " + _myFileName +
                    " / " + ex);
        }
    }


//    /**
//     * this is not correct because fps is not always 30!
//     * @deprecated
//     * @return int
//     */
//    public int getCurrentFrame() {
//        try {
//            int fps = 30;
//            return _myMovieController.getCurrentTime() * fps / _myMovie.getTimeScale();
//        } catch (Exception ex) {
//            System.err.println("### ERROR @ " + this.getClass() +
//                               " / " + _myFileName +
//                               " / " + ex);
//            return 0;
//        }
//    }
    /**
     *
     * @param theDirection
     */
    public float jumpToNextFrame(final int theDirection) {
        try {
            Track myGoodTrack = null;
            for (int i = 1; i <= _myMovie.getTrackCount(); i++) {
                final Track myTrack = _myMovie.getTrack(i);
                if (Media.fromTrack(myTrack) instanceof VideoMedia) {
                    myGoodTrack = myTrack;
                    break;
                }
            }
            if (myGoodTrack != null) {
                final TimeInfo myTimeInfo = myGoodTrack.getNextInterestingTime(StdQTConstants.nextTimeMediaSample,
                                                                               _myMovie.getTime(),
                                                                               theDirection);
                _myMovie.setTime(new TimeRecord(_myMovie.getTimeScale(), myTimeInfo.time));
                return (float)myTimeInfo.time / (float)_myMovie.getTimeScale();
            } else {
                System.err.println("### ERROR @ " + this.getClass() +
                        " / no video track available / " + _myFileName);
            }
        } catch (Exception ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".jumpToNextFrame() / " + _myFileName +
                    " / " + ex);
        }
        return Gestalt.UNDEFINED;
    }

    public float getCurrentTime() {
        try {
            return (float)_myMovie.getTime() / (float)_myMovie.getTimeScale();
        } catch (StdQTException ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".getCurrentTime() / " + _myFileName +
                    " / " + ex);
        }
        return Gestalt.UNDEFINED;
    }

    public void setTime(float where) {
        try {
            int scaledTime = (int)(where * _myMovie.getTimeScale());
            _myMovie.setTimeValue(scaledTime);
        } catch (StdQTException ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".setTime() / " + _myFileName +
                    " / " + ex);
        }
    }

    public float getDuration() {
        try {
            return (float)_myMovie.getDuration() / (float)_myMovie.getTimeScale();
        } catch (StdQTException ex) {
            System.err.println("### ERROR @ " + this.getClass() +
                    ".getDuration() / " + _myFileName +
                    " / " + ex);
            return 0;
        }
    }

    public boolean isFinished() {
        try {
            if (_myMovieController.getCurrentTime() >= _myMovie.getDuration()) {
                return true;
            }
        } catch (StdQTException ex) {
            System.out.println("### ERROR @ " + this.getClass() +
                    ".isFinished() / " + _myFileName +
                    " / isFinished() / " + ex);
        }
        return false;
    }

    public void destroy() {
        try {
            _myQDGraphics.disposeQTObject();
        } catch (QTException ex) {
            System.err.println("### ERROR @ QTMovieProducer / QDGraphics " + ex);
        }
        try {
            _myRawImage.disposeQTObject();
        } catch (QTException ex) {
            System.err.println("### ERROR @ QTMovieProducer / RawImage " + ex);
        }
        try {
            _myPixMap.disposeQTObject();
        } catch (QTException ex) {
            System.err.println("### ERROR @ QTMovieProducer / PixMap " + ex);
        }
        try {
            _myMovie.disposeQTObject();
        } catch (QTException ex) {
            System.err.println("### ERROR @ QTMovieProducer / Movie " + ex);
        }
        QTUtils.reclaimMemory();
    }

    public void _destroy() {
        QuicktimeDisposer.dispose(_myMovieController, _myMovie);
    }

    public static void destroyAll() {
        try {
            QTSession.close();
        } catch (Exception ex) {
            System.err.println("### ERROR @ QTMovieProducer / " + ex);
        }
    }
}
