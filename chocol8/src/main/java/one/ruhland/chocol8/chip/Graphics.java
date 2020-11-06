package one.ruhland.chocol8.chip;

import java.util.Arrays;

public abstract class Graphics {

    private int resolutionX;
    private int resolutionY;
    private boolean[] frameBuffer;

    private final Memory memory;

    protected Graphics(final int resolutionX, final int resolutionY, final Memory memory) {
        if (resolutionX < 1 || resolutionY < 1) {
            throw new IllegalArgumentException("The resolution must be a positive number greater than zero!");
        }

        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.frameBuffer = new boolean[resolutionX * resolutionY];
        this.memory = memory;
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

        drawScreen(Arrays.copyOf(frameBuffer, frameBuffer.length));

        return flippedFromSetToUnset;
    }

    protected void reset() {
        Arrays.fill(frameBuffer, false);
        drawScreen(Arrays.copyOf(frameBuffer, frameBuffer.length));
    }

    protected void setResolution(final int resolutionX, final int resolutionY) {
        if (resolutionX < 1 || resolutionY < 1) {
            throw new IllegalArgumentException("The resolution must be a positive number greater than zero!");
        }

        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.frameBuffer = new boolean[resolutionX * resolutionY];

        setDisplayResolution(resolutionX, resolutionY);
    }

    protected abstract void setDisplayResolution(final int resolutionX, final int resolutionY);

    protected abstract void drawScreen(boolean[] screen);
}
