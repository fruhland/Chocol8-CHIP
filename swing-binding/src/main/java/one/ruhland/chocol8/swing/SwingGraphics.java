package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Graphics;
import one.ruhland.chocol8.chip.Memory;

import javax.swing.*;

public class SwingGraphics extends Graphics {

    private final GraphicsPanel panel;
    private final JFrame frame;

    public SwingGraphics(final int resolutionX, final int resolutionY, final Memory memory) throws InterruptedException {
        super(resolutionX, resolutionY, memory);

        panel = new GraphicsPanel(resolutionX, resolutionY);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Chocol8 Chip");

        setupMenu();

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void setupMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        var optionsMenu = new JMenu("Options");

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);

        // Setup file menu
        var closeItem = new JMenuItem("Close");
        closeItem.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
        });

        fileMenu.add(closeItem);

        // Setup options menu
        var scaleMenu = new JMenu("Scale factor");

        for(int i = 1; i <= 16; i++) {
            final int factor = i;

            var item = new JMenuItem(factor + "X");
            item.addActionListener(e -> {
                panel.setScaleFactor(factor);
                frame.pack();
            });

            scaleMenu.add(item);
        }

        optionsMenu.add(scaleMenu);

        frame.setJMenuBar(menuBar);
    }

    @Override
    protected boolean flipPixel(int x, int y) {
        return panel.flipPixel(x, y);
    }

    @Override
    protected void reset() {
        panel.reset();
    }

    @Override
    protected void flush() {
        panel.repaint();
    }
}
