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


public class MovieProducer {

    private QTMovieProducer _myMovieImageProducer;

    private Class _myBitmapClass = null;

    public MovieProducer(String theFilename) {
        this(theFilename, false);
    }

    public MovieProducer(String theFilename, boolean thePreloadFlag) {
        _myMovieImageProducer = new QTMovieProducer(theFilename, thePreloadFlag);
        _myMovieImageProducer.init();
    }

    public MovieProducer(String theFilename, boolean thePreloadFlag, Class theBitmapClass) {
        _myBitmapClass = theBitmapClass;
        _myMovieImageProducer = new QTMovieProducer(theFilename, thePreloadFlag, theBitmapClass);
        _myMovieImageProducer.init();
    }

    public MovieProducer(String theFilename, boolean thePreloadFlag, int theUpdateFPS) {
        _myMovieImageProducer = new QTMovieProducer(theFilename, thePreloadFlag, theUpdateFPS);
    }

    public void play(float theSpeed) {
        _myMovieImageProducer.play(theSpeed);
    }

    public void update() {
        _myMovieImageProducer.update();
    }

    public void stop() {
        _myMovieImageProducer.stop();
    }

    public void setLooping(boolean looping) {
        _myMovieImageProducer.setLooping(looping);
    }

    public boolean isFinished() {
        return _myMovieImageProducer.isFinished();
    }

    public float getDuration() {
        return _myMovieImageProducer.getDuration();
    }

    public void goToStart() {
        _myMovieImageProducer.frame(0);
    }

    /**
     * @todo this might not work.
     * @param frame int
     */
    public void frame(int frame) {
        _myMovieImageProducer.frame(frame);
    }

    /**
     * @todo this might not work.
     */
    public void nextFrame() {
        _myMovieImageProducer.nextFrame();
    }

    /**
     * @todo this might not work.
     */
    public void previousFrame() {
        _myMovieImageProducer.previousFrame();
    }

    public float getCurrentTime() {
        return _myMovieImageProducer.getCurrentTime();
    }

    public static void destroyAll() {
        QTMovieProducer.destroyAll();
    }

    public byte[] getData() {
        return _myMovieImageProducer.getData();
    }

    public Bitmap getBitmap() {
        return _myMovieImageProducer.bitmap();
    }

    public float jumpToNextFrame(final int theDirection) {
        return _myMovieImageProducer.jumpToNextFrame(theDirection);
    }

    public void destroy() {
        _myMovieImageProducer.destroy();
        _myMovieImageProducer = null;
    }

    public void _destroy() {
        _myMovieImageProducer._destroy();
        _myMovieImageProducer = null;
    }
}
