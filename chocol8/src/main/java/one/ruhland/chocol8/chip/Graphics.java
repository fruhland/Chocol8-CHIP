package one.ruhland.chocol8.chip;

import java.util.Arrays;

public abstract class Graphics {

    private final int resolutionX;
    private final int resolutionY;
    private boolean[] frameBuffer;

    private final Memory memory;

    protected Graphics(final int resolutionX, final int resolutionY, final Memory memory) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.memory = memory;

        frameBuffer = new boolean[resolutionX * resolutionY];
    }

    boolean drawSprite(final int x, final int y, final int height, final int address) {
        boolean flippedFromSetToUnset = false;

        for (int i = 0; i < height; i++) {
            byte currentLine = memory.getByte(address + i);

            for (int j = 0; j < 8; j++) {
                if ((currentLine & (1 << (7 - j))) > 0) {
                    int posX = (x + j) % resolutionX;
                    int posY = (y + i) % resolutionY;

                    if (!flippedFromSetToUnset) {
                        flippedFromSetToUnset = frameBuffer[resolutionX * posY + posX];
                    }

                    frameBuffer[resolutionX * posY + posX] = !frameBuffer[resolutionX * posY + posX];
                }
            }
        }

        draw(Arrays.copyOf(frameBuffer, frameBuffer.length));

        return flippedFromSetToUnset;
    }

    protected int getResolutionX() {
        return resolutionX;
    }

    protected int getResolutionY() {
        return resolutionY;
    }

    protected void reset() {
        Arrays.fill(frameBuffer, false);
        draw(Arrays.copyOf(frameBuffer, frameBuffer.length));
    }

    protected abstract void draw(boolean[] screen);
}
