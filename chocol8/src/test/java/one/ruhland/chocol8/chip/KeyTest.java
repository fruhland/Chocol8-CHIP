package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyTest {

    @Test
    public void testFromInt() {
        assertEquals(Keyboard.Key.KEY_0, Keyboard.Key.fromInt(0));
        assertEquals(Keyboard.Key.KEY_1, Keyboard.Key.fromInt(1));
        assertEquals(Keyboard.Key.KEY_2, Keyboard.Key.fromInt(2));
        assertEquals(Keyboard.Key.KEY_3, Keyboard.Key.fromInt(3));
        assertEquals(Keyboard.Key.KEY_4, Keyboard.Key.fromInt(4));
        assertEquals(Keyboard.Key.KEY_5, Keyboard.Key.fromInt(5));
        assertEquals(Keyboard.Key.KEY_6, Keyboard.Key.fromInt(6));
        assertEquals(Keyboard.Key.KEY_7, Keyboard.Key.fromInt(7));
        assertEquals(Keyboard.Key.KEY_8, Keyboard.Key.fromInt(8));
        assertEquals(Keyboard.Key.KEY_9, Keyboard.Key.fromInt(9));
        assertEquals(Keyboard.Key.KEY_A, Keyboard.Key.fromInt(10));
        assertEquals(Keyboard.Key.KEY_B, Keyboard.Key.fromInt(11));
        assertEquals(Keyboard.Key.KEY_C, Keyboard.Key.fromInt(12));
        assertEquals(Keyboard.Key.KEY_D, Keyboard.Key.fromInt(13));
        assertEquals(Keyboard.Key.KEY_E, Keyboard.Key.fromInt(14));
        assertEquals(Keyboard.Key.KEY_F, Keyboard.Key.fromInt(15));
    }

    @Test
    public void testGetValue() {
        assertEquals(0, Keyboard.Key.KEY_0.getValue());
        assertEquals(1, Keyboard.Key.KEY_1.getValue());
        assertEquals(2, Keyboard.Key.KEY_2.getValue());
        assertEquals(3, Keyboard.Key.KEY_3.getValue());
        assertEquals(4, Keyboard.Key.KEY_4.getValue());
        assertEquals(5, Keyboard.Key.KEY_5.getValue());
        assertEquals(6, Keyboard.Key.KEY_6.getValue());
        assertEquals(7, Keyboard.Key.KEY_7.getValue());
        assertEquals(8, Keyboard.Key.KEY_8.getValue());
        assertEquals(9, Keyboard.Key.KEY_9.getValue());
        assertEquals(10, Keyboard.Key.KEY_A.getValue());
        assertEquals(11, Keyboard.Key.KEY_B.getValue());
        assertEquals(12, Keyboard.Key.KEY_C.getValue());
        assertEquals(13, Keyboard.Key.KEY_D.getValue());
        assertEquals(14, Keyboard.Key.KEY_E.getValue());
        assertEquals(15, Keyboard.Key.KEY_F.getValue());
    }
}
