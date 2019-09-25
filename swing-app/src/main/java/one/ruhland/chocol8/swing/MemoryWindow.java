package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.LockSupport;

public class MemoryWindow extends JFrame {

    private static final String WINDOW_TITLE = "Memory Inspector";
    private static final int DEFAULT_ROW_COUNT = 16;
    private static final int DEFAULT_COLUMN_COUNT = 8;

    private final MemoryTable memoryTable;
    private boolean isRunning = true;

    MemoryWindow(Machine machine) {
        memoryTable = new MemoryTable(machine, DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        add(memoryTable, BorderLayout.CENTER);

        pack();

        new Thread(() -> {
            while (isRunning) {
                ((AbstractTableModel) memoryTable.getModel()).fireTableDataChanged();
                LockSupport.parkNanos((long) ((1.0 / machine.getCpu().getClock().getFrequency()) * 1000000000));
            }
        }).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isRunning = false;
            }
        });
    }
}
