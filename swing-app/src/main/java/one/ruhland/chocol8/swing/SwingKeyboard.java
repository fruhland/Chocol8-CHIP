package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class SwingKeyboard extends Keyboard implements KeyListener {

    private final Map<Integer, Key> keyMap = new HashMap<>();

    private Integer pressedKey = null;

    public SwingKeyboard() {
        keyMap.put(KeyEvent.VK_1, Key.KEY_1);
        keyMap.put(KeyEvent.VK_2, Key.KEY_2);
        keyMap.put(KeyEvent.VK_3, Key.KEY_3);
        keyMap.put(KeyEvent.VK_4, Key.KEY_C);
        keyMap.put(KeyEvent.VK_Q, Key.KEY_4);
        keyMap.put(KeyEvent.VK_W, Key.KEY_5);
        keyMap.put(KeyEvent.VK_E, Key.KEY_6);
        keyMap.put(KeyEvent.VK_R, Key.KEY_D);
        keyMap.put(KeyEvent.VK_A, Key.KEY_7);
        keyMap.put(KeyEvent.VK_S, Key.KEY_8);
        keyMap.put(KeyEvent.VK_D, Key.KEY_9);
        keyMap.put(KeyEvent.VK_F, Key.KEY_E);
        keyMap.put(KeyEvent.VK_Y, Key.KEY_A);
        keyMap.put(KeyEvent.VK_X, Key.KEY_0);
        keyMap.put(KeyEvent.VK_C, Key.KEY_B);
        keyMap.put(KeyEvent.VK_V, Key.KEY_F);
    }

    @Override
    protected boolean isKeyPressed(final Key key) {
        return keyMap.get(pressedKey) == key;
    }

    @Override
    protected Key getPressedKey() {
        return keyMap.get(pressedKey);
    }

    @Override
    public void keyPressed(final KeyEvent keyEvent) {
        pressedKey = keyEvent.getKeyCode();
    }

    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        pressedKey = null;
    }

    @Override
    public void keyTyped(final KeyEvent keyEvent) {}
}
