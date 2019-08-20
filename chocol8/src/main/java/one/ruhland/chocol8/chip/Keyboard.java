package one.ruhland.chocol8.chip;

import java.util.Arrays;

public abstract class Keyboard {

    public enum Key {
        KEY_0(0), KEY_1(1), KEY_2(2), KEY_3(3),
        KEY_4(4), KEY_5(5), KEY_6(6), KEY_7(7),
        KEY_8(8), KEY_9(9), KEY_A(10), KEY_B(11),
        KEY_C(12), KEY_D(13), KEY_E(14), KEY_F(15);

        private static final Key[] VALUES;

        static {
            int arrayLength = Arrays.stream(values())
                    .mapToInt(element -> element.value).max().orElseThrow(IllegalArgumentException::new) + 1;

            VALUES = new Key[arrayLength];

            for (Key element : Key.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Key(final int value) {
            this.value = value;
        }

        public static Key fromInt(final int value) {
            return VALUES[value];
        }

        public byte getValue() {
            return (byte) value;
        }
    }

    protected Keyboard() {}

    protected abstract boolean isKeyPressed(final Key key);

    protected abstract Key getPressedKey();
}
