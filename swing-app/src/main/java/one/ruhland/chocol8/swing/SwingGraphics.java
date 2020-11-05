package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Graphics;
import one.ruhland.chocol8.chip.Memory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;

public class SwingGraphics extends Graphics {

    private final GraphicsPanel panel;

    public SwingGraphics(final int resolutionX, final int resolutionY, final Memory memory) {
        super(resolutionX, resolutionY, memory);
        panel = new GraphicsPanel(resolutionX, resolutionY);
    }

    GraphicsPanel getPanel() {
        return panel;
    }

    @Override
    protected void draw(boolean[] frameBuffer) {
        panel.draw(frameBuffer);
    }

    static final class GraphicsPanel extends JPanel {

        private final int resolutionX;
        private final int resolutionY;

        private boolean[] frameBuffer;

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

        void draw(boolean[] frameBuffer) {
            this.frameBuffer = frameBuffer;
            this.repaint();
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
}
