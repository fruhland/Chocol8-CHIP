package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Graphics;
import one.ruhland.chocol8.chip.Memory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;

public class SwingGraphics extends Graphics {

    private final static int DEFAULT_FRAMES_PER_SECOND = 60;

    private final GraphicsPanel panel;
    private final RepaintThread repaintThread = new RepaintThread(DEFAULT_FRAMES_PER_SECOND);

    public SwingGraphics(final int resolutionX, final int resolutionY, final Memory memory) {
        super(resolutionX, resolutionY, memory);
        panel = new GraphicsPanel(resolutionX, resolutionY);

        repaintThread.start();
    }

    GraphicsPanel getPanel() {
        return panel;
    }

    void setFramesPerSecond(int framesPerSecond) {
        repaintThread.setFramesPerSecond(framesPerSecond);
    }

    @Override
    protected boolean flipPixel(int x, int y) {
        return panel.flipPixel(x, y);
    }

    @Override
    protected void reset() {
        panel.reset();
    }

    static final class GraphicsPanel extends JPanel {

        private final int resolutionX;
        private final int resolutionY;

        private final boolean[] frameBuffer;

        private int scaleFactor = 8;

        GraphicsPanel(final int resolutionX, final int resolutionY) {
            this.resolutionX = resolutionX;
            this.resolutionY = resolutionY;
            frameBuffer = new boolean[resolutionX * resolutionY];
        }

        public int getScaleFactor() {
            return scaleFactor;
        }

        void setScaleFactor(int scaleFactor) {
            if (scaleFactor < 1) {
                throw new IllegalArgumentException("Scale factor must be at least 1!");
            }

            this.scaleFactor = scaleFactor;
            repaint();
        }

        boolean flipPixel(int x, int y) {
            int pos = x + y * resolutionX;

            if (pos < 0 || pos >= frameBuffer.length) {
                return false;
            }

            frameBuffer[pos] = !frameBuffer[pos];
            return !frameBuffer[pos];
        }

        void reset() {
            Arrays.fill(frameBuffer, false);
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(resolutionX * scaleFactor, resolutionY * scaleFactor);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < resolutionX; i++) {
                for (int j = 0; j < resolutionY; j++) {
                    g.setColor(frameBuffer[i + j * resolutionX] ? Color.BLACK : Color.WHITE);
                    g.fillRect(i * scaleFactor, j * scaleFactor, scaleFactor, scaleFactor);
                }
            }
        }
    }

    private final class RepaintThread extends Thread {

        private int framesPerSecond;
        private boolean isRunning = true;

        RepaintThread(int framesPerSecond) {
            super("RepaintThread");
            this.framesPerSecond = framesPerSecond;
        }

        @Override
        public void run() {
            while (isRunning) {
                long start = System.nanoTime();
                long frameTime = 1000000000L / framesPerSecond;

                panel.repaint();

                long end = System.nanoTime();
                long sleepTime = frameTime - (end - start);

                long slept = 0;
                while (sleepTime > slept) {
                    LockSupport.parkNanos(sleepTime - slept);
                    slept = System.nanoTime() - end;
                }
            }
        }

        void setFramesPerSecond(int framesPerSecond) {
            this.framesPerSecond = framesPerSecond;
        }

        void exit() {
            isRunning = false;
        }
    }
}
