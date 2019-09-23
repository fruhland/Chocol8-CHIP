package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;

public class MainWindow extends JFrame {

    private static final String WINDOW_TITLE = "Chocol8 CHIP";
    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("CHIP-8", "ch8");

    private final Machine machine;
    private final SwingGraphics.GraphicsPanel graphicsPanel;
    private final MemoryTable memoryTable;

    private String lastFolder;

    public MainWindow(final Machine machine) {
        this.machine = machine;
        graphicsPanel = ((SwingGraphics) machine.getGraphics()).getPanel();
        memoryTable = new MemoryTable(machine, graphicsPanel.getScaleFactor() * 2);

        if(!(machine.getGraphics() instanceof SwingGraphics)) {
            throw new IllegalStateException("Trying to initialize the Swing frontend with a graphics implementation " +
                    "of type " + machine.getGraphics().getClass().getCanonicalName() + "! " +
                    "This frontend only works with " + SwingGraphics.class.getCanonicalName() + "!");
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        setupMenu();

        add(graphicsPanel, BorderLayout.CENTER);
        add(memoryTable, BorderLayout.EAST);

        addKeyListener((SwingKeyboard) machine.getKeyboard());
        setFocusable(true);
        requestFocus();

        pack();
    }

    private void setupMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        var optionsMenu = new JMenu("Options");

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);

        // Setup file menu
        var openItem = new JMenuItem("Open");
        openItem.addActionListener(actionEvent -> {
            var chooser = new JFileChooser(lastFolder);
            chooser.setFileFilter(FILTER);

            int ret = chooser.showOpenDialog(this);
            lastFolder = chooser.getCurrentDirectory().getAbsolutePath();

            if(ret == JFileChooser.APPROVE_OPTION) {
                try {
                    machine.loadProgram(chooser.getSelectedFile().getAbsolutePath());
                    machine.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        var closeItem = new JMenuItem("Close");
        closeItem.addActionListener(actionEvent -> {
            machine.reset();
        });

        var exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(actionEvent -> {
            setVisible(false);
            dispose();
            System.exit(0);
        });

        fileMenu.add(openItem);
        fileMenu.add(closeItem);
        fileMenu.add(exitItem);

        // Setup options menu
        var scaleMenu = new JMenu("Scale factor");

        for(int i = 1; i <= 16; i++) {
            final int factor = i;

            var item = new JMenuItem(factor + "X");
            item.addActionListener(actionEvent -> {
                graphicsPanel.setScaleFactor(factor);
                memoryTable.setRowCount(factor * 2);
                pack();
            });

            scaleMenu.add(item);
        }
        
        var frequencyItem = new JMenuItem("CPU frequency");
        frequencyItem.addActionListener(actionEvent -> {
            var string = JOptionPane.showInputDialog(null, "Please enter the desired frequency in Hz (1-1000000):");

            if(string == null || string.isEmpty()) {
                return;
            }

            var frequency = Integer.parseInt(string);

            if (frequency < 1 || frequency > 1000000) {
                throw new IllegalArgumentException("Frequency must be between 1 and 1000000 Hz!");
            }

            machine.getCpu().getClock().setFrequency(frequency);
        });

        optionsMenu.add(frequencyItem);
        optionsMenu.add(scaleMenu);

        setJMenuBar(menuBar);
    }
}
