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


package gestalt.input;


/*
 * EventHandler collects java.awt events and distributes them to the
 * GestaltKeyListeners and GestaltMouseListeners. There are two different ways
 * to obtain events. 1. event model: implement the GestaltKeyListener and/or
 * GestaltMouseListner interfaces 2. polling: poll the EventHandler
 */
import gestalt.context.GLContext;
import gestalt.render.Drawable;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;

import javax.swing.SwingUtilities;

import static gestalt.Gestalt.*;


public class EventHandler
        implements
        Drawable,
        MouseListener,
        MouseMotionListener,
        MouseWheelListener,
        KeyListener {

    public static boolean EVENT_FLIP_MOUSE_Y = true;

    public static boolean EVENT_CENTER_MOUSE = true;

    public int mouseButton;

    protected Vector<GestaltMouseListener> _myMouseListener;

    protected Vector<GestaltKeyListener> _myKeyListener;

    protected boolean oldMouseClicked = false;

    public boolean mouseDown = false;

    public boolean mouseClicked = false;

    public boolean mouseDoubleClicked = false;

    public boolean mouseReleased = false;

    public boolean mouseDragged = false;

    public int mouseX = 0;

    public int mouseY = 0;

    public float normalized_mouseX = 0;

    public float normalized_mouseY = 0;

    public int pmouseX = 0;

    public int pmouseY = 0;

    public int mousewheel = 0;

    protected int _myMouseWheel;

    protected int _myMouseX;

    protected int _myMouseY;

    protected boolean _myMouseDown;

    protected boolean _myMouseClicked;

    protected boolean _myMouseDoubleClicked;

    protected boolean _myMouseReleased;

    protected boolean _myMouseDragged;

    public boolean keyPressed = false;

    public char key;

    public int keyCode;

    public boolean keyDown = false;

    public boolean keyReleased = false;

    public boolean shift = false;

    public boolean meta = false;

    public boolean control = false;

    public boolean alt = false;

    protected boolean _myKeyDown;

    protected boolean _myKeyPressed;

    protected boolean _myKeyReleased;

    protected char _myKeyChar;

    protected int _myKeyCode;

    protected boolean _myShiftDown = false;

    protected boolean _myMetaDown = false;

    protected boolean _myAltDown = false;

    protected boolean _myControlDown = false;

    public boolean sending_quit = false;

    private char _myOldKey;

    private int _myOldKeyCode;

    public EventHandler() {
        _myMouseListener = new Vector<GestaltMouseListener>();
        _myKeyListener = new Vector<GestaltKeyListener>();
    }

    public void draw(final GLContext theContext) {

        /* mouse */
        pmouseX = mouseX;
        pmouseY = mouseY;
        mouseX = _myMouseX;
        mouseY = _myMouseY;
        if (EVENT_FLIP_MOUSE_Y) {
            mouseY = theContext.displaycapabilities.height - mouseY;
        }

        normalized_mouseX = (float)mouseX / (float)theContext.displaycapabilities.width;
        normalized_mouseY = (float)mouseY / (float)theContext.displaycapabilities.height;

        if (EVENT_CENTER_MOUSE) {
            mouseX -= theContext.displaycapabilities.width / 2;
            mouseY -= theContext.displaycapabilities.height / 2;
        }
        if (theContext.camera != null && theContext.camera.viewport() != null) {
            mouseX -= theContext.camera.viewport().x;
            mouseY -= theContext.camera.viewport().y;
        }

        mousewheel = _myMouseWheel;
        _myMouseWheel = 0;

        mouseDown = _myMouseDown;
        mouseClicked = _myMouseClicked;
        mouseReleased = _myMouseReleased;
        mouseDoubleClicked = _myMouseDoubleClicked;
        _myMouseDoubleClicked = false;
        _myMouseReleased = false;
        _myMouseClicked = false;

        if (oldMouseClicked != mouseDown) {
            oldMouseClicked = mouseDown;
            if (mouseDown) {
                for (int i = 0; i < _myMouseListener.size(); ++i) {
                    GestaltMouseListener myListener = _myMouseListener.get(i);
                    myListener.mousePressed(mouseX, mouseY, mouseButton);
                }
            } else {
                for (int i = 0; i < _myMouseListener.size(); ++i) {
                    GestaltMouseListener myListener = _myMouseListener.get(i);
                    myListener.mouseReleased(mouseX, mouseY, mouseButton);
                }
            }
        }
        if (_myMouseDragged && Math.abs(pmouseX - mouseX) != 0 && Math.abs(pmouseY - mouseY) != 0) {
            mouseDragged = true;
            for (int i = 0; i < _myMouseListener.size(); ++i) {
                GestaltMouseListener myListener = _myMouseListener.get(i);
                myListener.mouseDragged(mouseX, mouseY, mouseButton);
            }
        } else {
            mouseDragged = false;
        }

        /* key */
        shift = _myShiftDown;
        meta = _myMetaDown;
        control = _myControlDown;
        alt = _myAltDown;

        _myOldKey = key;
        _myOldKeyCode = keyCode;
        key = _myKeyChar;
        keyCode = _myKeyCode;

        /* key down */
        if (_myKeyDown && !keyDown) {
            for (int i = 0; i < _myKeyListener.size(); ++i) {
                final GestaltKeyListener myListener = _myKeyListener.get(i);
                myListener.keyPressed(key, keyCode);
            }
        }

        keyDown = _myKeyDown;
        keyPressed = _myKeyPressed;

        /* key release */
        keyReleased = _myKeyReleased;
        if (keyReleased) {
            for (int i = 0; i < _myKeyListener.size(); ++i) {
                final GestaltKeyListener myListener = _myKeyListener.get(i);
                myListener.keyReleased(_myOldKey, _myOldKeyCode);
            }
        }
        _myKeyReleased = false;
    }

    public boolean keyPressed(char theChar) {
        return key == theChar;
    }


    /* mouse listener */
    public void addMouseListener(GestaltMouseListener theMouseListener) {
        _myMouseListener.add(theMouseListener);
    }

    public void removeMouseListener(GestaltMouseListener theMouseListener) {
        _myMouseListener.remove(theMouseListener);
    }


    /* key observer */
    public void addKeyListener(GestaltKeyListener theKeyListener) {
        _myKeyListener.add(theKeyListener);
    }

    public void removeKeyListner(GestaltKeyListener theKeyListener) {
        _myKeyListener.remove(theKeyListener);
    }

    public void mousePressed(MouseEvent e) {
        _myMouseX = e.getX();
        _myMouseY = e.getY();
        _myMouseDown = true;
        _myMouseClicked = true;
        _myMouseReleased = false;
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseButton = MOUSEBUTTON_LEFT;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            mouseButton = MOUSEBUTTON_RIGHT;
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            mouseButton = MOUSEBUTTON_MIDDLE;
        }
    }

    public void mouseReleased(MouseEvent e) {
        _myMouseX = e.getX();
        _myMouseY = e.getY();
        _myMouseDown = false;
        _myMouseReleased = true;
        _myMouseDragged = false;
        _myMouseDoubleClicked = false;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            _myMouseDoubleClicked = true;
        }
    }

    public void mouseMoved(MouseEvent e) {
        _myMouseX = e.getX();
        _myMouseY = e.getY();
    }

    public void mouseDragged(MouseEvent e) {
        _myMouseDragged = true;
        _myMouseX = e.getX();
        _myMouseY = e.getY();
        _myMouseDown = true;
        _myMouseReleased = false;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }


    /* key input */
    public void keyPressed(final KeyEvent pEvent) {
        if ((pEvent.isMetaDown() || pEvent.isControlDown()) && pEvent.getKeyCode() == KeyEvent.VK_Q) {
            sending_quit = true;
        }

        if (pEvent.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            /* special keys */
            if (pEvent.isMetaDown()) {
                _myMetaDown = pEvent.isMetaDown();
            }
            if (pEvent.isShiftDown()) {
                _myShiftDown = pEvent.isShiftDown();
            }
            if (pEvent.isAltDown()) {
                _myAltDown = pEvent.isAltDown();
            }
            if (pEvent.isControlDown()) {
                _myControlDown = pEvent.isControlDown();
            }
            /* keycode. parse to gestalt constants */
            switch (pEvent.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    _myKeyCode = KEYCODE_LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    _myKeyCode = KEYCODE_RIGHT;
                    break;
                case KeyEvent.VK_UP:
                    _myKeyCode = KEYCODE_UP;
                    break;
                case KeyEvent.VK_DOWN:
                    _myKeyCode = KEYCODE_DOWN;
                    break;
                case KeyEvent.VK_PAGE_UP:
                    _myKeyCode = KEYCODE_PAGE_UP;
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    _myKeyCode = KEYCODE_PAGE_DOWN;
                    break;
                /* not supposed to cause events */
                case KeyEvent.VK_SHIFT:
                    _myKeyCode = KEYCODE_SHIFT;
                    break;
                case KeyEvent.VK_META:
                    _myKeyCode = KEYCODE_META;
                    break;

                default:
                    _myKeyCode = pEvent.getKeyCode();
            }

            /* keys */
            if (_myKeyCode != KEYCODE_SHIFT && _myKeyCode != KEYCODE_META) {
                _myKeyDown = true;
                _myKeyPressed = true;
                _myKeyReleased = false;
                _myKeyChar = (char)0; // todo is this necessary?
            }
        } else {
            /* keys */
            _myKeyDown = true;
            _myKeyPressed = true;
            _myKeyReleased = false;
            _myKeyChar = pEvent.getKeyChar();

            /* keycode. parse to gestalt constants */
            switch (pEvent.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    _myKeyCode = KEYCODE_ESCAPE;
                    break;
                case KeyEvent.VK_SPACE:
                    _myKeyCode = KEYCODE_SPACE;
                    break;

                case KeyEvent.VK_A:
                    _myKeyCode = KEYCODE_A;
                    break;
                case KeyEvent.VK_B:
                    _myKeyCode = KEYCODE_B;
                    break;
                case KeyEvent.VK_C:
                    _myKeyCode = KEYCODE_C;
                    break;
                case KeyEvent.VK_D:
                    _myKeyCode = KEYCODE_D;
                    break;
                case KeyEvent.VK_E:
                    _myKeyCode = KEYCODE_E;
                    break;
                case KeyEvent.VK_F:
                    _myKeyCode = KEYCODE_F;
                    break;
                case KeyEvent.VK_G:
                    _myKeyCode = KEYCODE_G;
                    break;
                case KeyEvent.VK_H:
                    _myKeyCode = KEYCODE_H;
                    break;
                case KeyEvent.VK_I:
                    _myKeyCode = KEYCODE_I;
                    break;
                case KeyEvent.VK_J:
                    _myKeyCode = KEYCODE_J;
                    break;
                case KeyEvent.VK_K:
                    _myKeyCode = KEYCODE_K;
                    break;
                case KeyEvent.VK_L:
                    _myKeyCode = KEYCODE_L;
                    break;
                case KeyEvent.VK_M:
                    _myKeyCode = KEYCODE_M;
                    break;
                case KeyEvent.VK_N:
                    _myKeyCode = KEYCODE_N;
                    break;
                case KeyEvent.VK_O:
                    _myKeyCode = KEYCODE_O;
                    break;
                case KeyEvent.VK_P:
                    _myKeyCode = KEYCODE_P;
                    break;
                case KeyEvent.VK_Q:
                    _myKeyCode = KEYCODE_Q;
                    break;
                case KeyEvent.VK_R:
                    _myKeyCode = KEYCODE_R;
                    break;
                case KeyEvent.VK_S:
                    _myKeyCode = KEYCODE_S;
                    break;
                case KeyEvent.VK_T:
                    _myKeyCode = KEYCODE_T;
                    break;
                case KeyEvent.VK_U:
                    _myKeyCode = KEYCODE_U;
                    break;
                case KeyEvent.VK_V:
                    _myKeyCode = KEYCODE_V;
                    break;
                case KeyEvent.VK_W:
                    _myKeyCode = KEYCODE_W;
                    break;
                case KeyEvent.VK_X:
                    _myKeyCode = KEYCODE_X;
                    break;
                case KeyEvent.VK_Y:
                    _myKeyCode = KEYCODE_Y;
                    break;
                case KeyEvent.VK_Z:
                    _myKeyCode = KEYCODE_Z;
                    break;

                case KeyEvent.VK_0:
                    _myKeyCode = KEYCODE_0;
                    break;
                case KeyEvent.VK_1:
                    _myKeyCode = KEYCODE_1;
                    break;
                case KeyEvent.VK_2:
                    _myKeyCode = KEYCODE_2;
                    break;
                case KeyEvent.VK_3:
                    _myKeyCode = KEYCODE_3;
                    break;
                case KeyEvent.VK_4:
                    _myKeyCode = KEYCODE_4;
                    break;
                case KeyEvent.VK_5:
                    _myKeyCode = KEYCODE_5;
                    break;
                case KeyEvent.VK_6:
                    _myKeyCode = KEYCODE_6;
                    break;
                case KeyEvent.VK_7:
                    _myKeyCode = KEYCODE_7;
                    break;
                case KeyEvent.VK_8:
                    _myKeyCode = KEYCODE_8;
                    break;
                case KeyEvent.VK_9:
                    _myKeyCode = KEYCODE_9;
                    break;
                default:
                    _myKeyCode = pEvent.getKeyCode();
            }
        }
    }

    public void keyReleased(final KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_META
                || event.getKeyCode() == KeyEvent.VK_SHIFT
                || event.getKeyCode() == KeyEvent.VK_CONTROL
                || event.getKeyCode() == KeyEvent.VK_ALT) {
//        if (event.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            /* special keys */
            if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                _myShiftDown = false;
            }
            if (event.getKeyCode() == KeyEvent.VK_META) {
                _myMetaDown = false;
            }
            if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
                _myControlDown = false;
            }
            if (event.getKeyCode() == KeyEvent.VK_ALT) {
                _myAltDown = false;
            }
        } else {
            /* keys */
            _myKeyChar = 0;
            _myKeyCode = 0;
            _myKeyDown = false;
            _myKeyPressed = false;
            _myKeyReleased = true;
        }
    }

    public void keyTyped(KeyEvent event) {
    }

    public void mouseWheelMoved(MouseWheelEvent theMouseWheelEvent) {
        _myMouseWheel = theMouseWheelEvent.getWheelRotation();
    }

    /* -> drawable obligations */
    public boolean isActive() {
        return true;
    }

    public void add(Drawable theDrawable) {
    }

    public float getSortValue() {
        return 0.0F;
    }

    public void setSortValue(float theSortValue) {
    }

    public float[] getSortData() {
        return null;
    }

    public boolean isSortable() {
        return false;
    }
}
