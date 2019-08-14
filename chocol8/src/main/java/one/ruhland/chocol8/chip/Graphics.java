package one.ruhland.chocol8.chip;

public abstract class Graphics {

    private final int resolutionX;
    private final int resolutionY;

    private final Memory memory;

    protected Graphics(final int resolutionX, final int resolutionY, final Memory memory) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.memory = memory;
    }

    boolean drawSprite(final int x, final int y, final int height, final int address) {
        boolean flippedFromSetToUnset = false;

        for(int i = 0; i < height; i++) {
            byte currentLine = memory.getByte(address + i);

            for(int j = 0; j < 8; j++) {
                if((currentLine & (1 << (7 - j))) > 0) {
                    if(flipPixel(x + j, y + i)) {
                        flippedFromSetToUnset = true;
                    }
                }
            }
        }

        flush();
        return flippedFromSetToUnset;
    }

    protected int getResolutionX() {
        return resolutionX;
    }

    protected int getResolutionY() {
        return resolutionY;
    }

    protected abstract boolean flipPixel(int x, int y);

    protected abstract void reset();

    protected abstract void flush();
}
