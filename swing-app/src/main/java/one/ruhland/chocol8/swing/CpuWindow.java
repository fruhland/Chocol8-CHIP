package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.LockSupport;

class CpuWindow extends JFrame {

    private static final String WINDOW_TITLE = "Cpu Controller";

    private final CpuPanel cpuPanel;
    private final RegisterTable registerTable;
    private final CpuControlPanel controlPanel;

    private boolean isRunning = false;

    CpuWindow(Machine machine) {
        registerTable = new RegisterTable(machine);
        cpuPanel = new CpuPanel(machine);
        controlPanel = new CpuControlPanel(machine);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        var gridPanel = new JPanel(new GridLayout(1, 2));

        gridPanel.add(cpuPanel);
        gridPanel.add(registerTable);

        registerTable.setRowHeight(20);

        add(gridPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if(isRunning) {
                    return;
                }

                isRunning = true;

                new Thread(() -> {
                    while (isRunning) {
                        ((AbstractTableModel) registerTable.getModel()).fireTableDataChanged();
                        cpuPanel.refresh();
                        controlPanel.refresh();

                        long sleepTime = (long) ((1.0 / machine.getCpu().getClock().getFrequency()) * 1000000000);

                        if(sleepTime > 100000000L) {
                            sleepTime = 100000000L;
                        }

                        LockSupport.parkNanos(sleepTime);
                    }
                }, "CpuControllerThread").start();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                isRunning = false;
            }
        });
    }
}
