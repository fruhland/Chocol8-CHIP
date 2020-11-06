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
    protected void setDisplayResolution(final int resolutionX, final int resolutionY) {
        panel.setResolution(resolutionX, resolutionY);
    }

    @Override
    protected void drawScreen(boolean[] frameBuffer) {
        panel.draw(frameBuffer);
    }

    static final class GraphicsPanel extends JPanel {

        private int resolutionX;
        private int resolutionY;

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

        void setResolution(final int resolutionX, final int resolutionY) {
            if (resolutionX < 1 || resolutionY < 1) {
                throw new IllegalArgumentException("The resolution must be a positive number greater than zero!");
            }

            this.resolutionX = resolutionX;
            this.resolutionY = resolutionY;
            frameBuffer = new boolean[resolutionX * resolutionY];

            repaint();

            var parent = getParent();
            if (parent != null && getParent() instanceof JFrame) {
                ((JFrame) getParent()).pack();
            }
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

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, scaleFactor * resolutionX, scaleFactor * resolutionY);

            g.setColor(Color.BLACK);

            for (int i = 0; i < resolutionX; i++) {
                for (int j = 0; j < resolutionY; j++) {
                    int index = i + j * resolutionX;

                    if (index < frameBuffer.length && frameBuffer[index]) {
                        g.fillRect(i * scaleFactor, j * scaleFactor, scaleFactor, scaleFactor);
                    }
                }
            }
        }
    }
}
