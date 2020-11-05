package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Cpu;
import one.ruhland.chocol8.chip.Machine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;

public class MainWindow extends JFrame {

    private static final String WINDOW_TITLE = "Chocol8 CHIP";
    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("CHIP-8", "ch8");

    private final Machine machine;
    private final SwingGraphics.GraphicsPanel graphicsPanel;
    private final MemoryWindow memoryWindow;
    private final CpuWindow cpuWindow;

    private String lastFolder;

    public MainWindow(final Machine machine) {
        this.machine = machine;
        graphicsPanel = ((SwingGraphics) machine.getGraphics()).getPanel();
        memoryWindow = new MemoryWindow(machine);
        cpuWindow = new CpuWindow(machine);

        if (!(machine.getGraphics() instanceof SwingGraphics)) {
            throw new IllegalStateException("Trying to initialize the Swing frontend with a graphics implementation " +
                    "of type " + machine.getGraphics().getClass().getCanonicalName() + "! " +
                    "This frontend only works with " + SwingGraphics.class.getCanonicalName() + "!");
        }

        machine.getCpu().setCompatibilityMode(Cpu.CompatibilityMode.CHIP_8);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        var iconStream = MainWindow.class.getClassLoader().getResourceAsStream("icon.png");

        if (iconStream != null) {
            try {
                setIconImage(ImageIO.read(iconStream));
            } catch (IOException ignored) {}
        }

        setupMenu();

        add(graphicsPanel, BorderLayout.CENTER);

        addKeyListener((SwingKeyboard) machine.getKeyboard());
        setFocusable(true);
        requestFocus();

        pack();
    }

    private void setupMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        var optionsMenu = new JMenu("Options");
        var toolsMenu = new JMenu("Tools");

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(toolsMenu);

        // Setup file menu
        var openItem = new JMenuItem("Open");
        openItem.addActionListener(actionEvent -> {
            var chooser = new JFileChooser(lastFolder);
            chooser.setFileFilter(FILTER);

            int ret = chooser.showOpenDialog(this);
            lastFolder = chooser.getCurrentDirectory().getAbsolutePath();

            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    machine.loadProgram(chooser.getSelectedFile().getAbsolutePath());
                    machine.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        var closeItem = new JMenuItem("Close");
        closeItem.addActionListener(actionEvent -> machine.reset());

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

        for (int i = 1; i <= 16; i++) {
            final int factor = i;

            var item = new JMenuItem(factor + "X");
            item.addActionListener(actionEvent -> {
                graphicsPanel.setScaleFactor(factor);
                pack();
            });

            scaleMenu.add(item);
        }

        var compatibilityMenu = new JMenu("Compatibility Mode");

        var chip8Item = new JRadioButtonMenuItem("CHIP-8");
        var superChipItem = new JRadioButtonMenuItem("S-CHIP");

        chip8Item.addActionListener(actionEvent -> machine.getCpu().setCompatibilityMode(Cpu.CompatibilityMode.CHIP_8));
        superChipItem.addActionListener(actionEvent -> machine.getCpu().setCompatibilityMode(Cpu.CompatibilityMode.SUPER_CHIP));

        ButtonGroup compatibilityGroup = new ButtonGroup();
        compatibilityGroup.add(chip8Item);
        compatibilityGroup.add(superChipItem);

        compatibilityMenu.add(chip8Item);
        compatibilityMenu.add(superChipItem);

        compatibilityMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent menuEvent) {
                switch (machine.getCpu().getCompatibilityMode()) {
                    case CHIP_8:
                        chip8Item.setSelected(true);
                        break;
                    case SUPER_CHIP:
                        superChipItem.setSelected(true);
                        break;
                }
            }

            @Override
            public void menuDeselected(MenuEvent menuEvent) {}

            @Override
            public void menuCanceled(MenuEvent menuEvent) {}
        });

        optionsMenu.add(scaleMenu);
        optionsMenu.add(compatibilityMenu);

        // Setup tools menu
        var memoryItem = new JMenuItem("Memory Inspector");
        memoryItem.addActionListener(actionEvent -> memoryWindow.setVisible(true));

        var cpuItem = new JMenuItem("Cpu Controller");
        cpuItem.addActionListener(actionEvent -> cpuWindow.setVisible(true));

        toolsMenu.add(cpuItem);
        toolsMenu.add(memoryItem);

        setJMenuBar(menuBar);
    }
}
