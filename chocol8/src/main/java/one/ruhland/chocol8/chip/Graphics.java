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

    void drawSprite(final int x, final int y, final int height, final int address) {
        for(int i = 0; i < height; i++) {
            byte currentLine = memory.getByte(address + i);

            for(int j = 0; j < 8; j++) {
                if((currentLine & (1 << j)) > 0) {
                    flipPixel(x + j, y + i);
                }
            }
        }

        flush();
    }

    protected int getResolutionX() {
        return resolutionX;
    }

    protected int getResolutionY() {
        return resolutionY;
    }

    protected abstract void flipPixel(int x, int y);

    protected abstract void reset();

    protected abstract void flush();
}
