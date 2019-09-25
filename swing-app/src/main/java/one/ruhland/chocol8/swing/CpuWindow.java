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

    private final Machine machine;
    private final RegisterTable registerTable;

    private boolean isRunning = false;

    CpuWindow(Machine machine) {
        this.machine = machine;
        registerTable = new RegisterTable(machine);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        add(registerTable, BorderLayout.CENTER);

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
                        LockSupport.parkNanos((long) ((1.0 / machine.getCpu().getClock().getFrequency()) * 1000000000));
                    }
                }).start();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                isRunning = false;
            }
        });
    }
}
