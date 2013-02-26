

package gestalt.material.texture;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import quicktime.QTException;
import quicktime.QTObject;
import quicktime.QTSession;


/**
 * Disposes of Quicktime objects safely.
 *
 * Disposing of Quicktime objects is not trivial. If we just let them be garbage
 * collected then their disposeQTObject() will be called sometime later as part of
 * finalize(). This will be on the finalizer thread though, and there is some wicked
 * bug in QT which sometimes blows the VM apart if disposeQTObject is called on the
 * finalizer (or any random?) thread.
 *
 * The solution to this problem is to explicitly take QTSession.terminationLock()
 * before disposing. This causes deadlocks however, because all of QTJ
 * synchronizes on this lock. If we try to take the QT lock on a thread which has another lock
 * (the event queue lock is typical) then another thread, holding the QT lock, may
 * block on the lock the first thread is holding (typically by trying to invokeAndWait()
 * to dispatch on the event queue). The result is a deadly embrace.
 *
 * We don't actually care when the Quicktime objects are disposed, just that when
 * it happens we have the terminationLock. So we have a queue of objects awaiting
 * diposal and a seperate thread (which holds no locks itself, and so cannot deadlock)
 * to take the terminationLock and dispose.
 *
 * @author duncan
 *
 */
public class QuicktimeDisposer {

    private BlockingQueue<QTObject[]> queue = new LinkedBlockingQueue<QTObject[]>();

    static QuicktimeDisposer instance;


    static {
        instance = new QuicktimeDisposer();
        instance.start();
    }

    protected QuicktimeDisposer() {
    }

    private void start() {
        Thread thread = new Thread("QuicktimeDiposer") {

            public void run() {
                while (true) {
                    loop();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void dispose(final QTObject... objects) {
        instance.queueForDisposal(objects);
    }

    void queueForDisposal(QTObject... o) {
        if (o == null) {
            return;
        }
        try {
            queue.put(o);
        } catch (InterruptedException e) {
//            logging.log(Level.SEVERE, "Could not add to queue", e);
        }
    }

    protected void loop() {
        try {
            QTObject[] batch = queue.take();
            synchronized (QTSession.terminationLock()) {
                for (QTObject element : batch) {
                    disposeQTObject(element);
                }
            }
        } catch (InterruptedException e) {
//            logging.log(Level.SEVERE, "Unexpected interrupt", e);
        } catch (QTException e) {
//            logging.log(Level.SEVERE, "Could not dispose QTObject", e);
        }
    }

    protected void disposeQTObject(QTObject o) throws QTException {
        if (o == null) {
            return;
        }
        o.disposeQTObject();
    }
}