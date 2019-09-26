package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.LockSupport;

class MemoryWindow extends JFrame {

    private static final String WINDOW_TITLE = "Memory Inspector";
    private static final int DEFAULT_ROW_COUNT = 16;
    private static final int DEFAULT_COLUMN_COUNT = 8;

    private final Machine machine;
    private final MemoryTable memoryTable;

    private boolean isRunning = false;

    MemoryWindow(Machine machine) {
        this.machine = machine;
        memoryTable = new MemoryTable(machine, DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        add(memoryTable, BorderLayout.CENTER);
        setupOptionsPanel();

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
                        ((AbstractTableModel) memoryTable.getModel()).fireTableDataChanged();
                        LockSupport.parkNanos((long) ((1.0 / machine.getCpu().getClock().getFrequency()) * 1000000000));
                    }
                }, "MemoryInspectorThread").start();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                isRunning = false;
            }
        });
    }

    private void setupOptionsPanel() {
        var panel = new JPanel(new GridLayout(1, 2));
        var addressPanel = new JPanel(new FlowLayout());
        var sizePanel = new JPanel(new GridLayout(2, 1));
        var rowPanel = new JPanel(new FlowLayout());
        var colPanel = new JPanel(new FlowLayout());

        var addressInput = new JTextField(8);
        var setButton = new JButton("Set Address");

        var addRowButton = new JButton("+");
        var removeRowButton = new JButton("-");
        var addColButton = new JButton("+");
        var removeColButton = new JButton("-");

        addRowButton.addActionListener((ActionEvent e) -> {
            memoryTable.setRowCount(memoryTable.getModel().getRowCount() + 1);
            pack();
        });
        removeRowButton.addActionListener((ActionEvent e) -> {
            memoryTable.setRowCount(memoryTable.getModel().getRowCount() - 1);
            pack();
        });
        addColButton.addActionListener((ActionEvent e) -> {
            memoryTable.setColumnCount(memoryTable.getModel().getColumnCount() + 1);
            pack();
        });
        removeColButton.addActionListener((ActionEvent e) -> {
            memoryTable.setColumnCount(memoryTable.getModel().getColumnCount() - 1);
            pack();
        });

        setButton.addActionListener((ActionEvent e) -> {
            int address;

            if (addressInput.getText().startsWith("0x")) {
                address = Integer.parseUnsignedInt(addressInput.getText().substring(2), 16);
            } else {
                address = Integer.parseUnsignedInt(addressInput.getText(), 10);
            }

            if(address > 0 && address < machine.getMemory().getSize()) {
                memoryTable.setAddress(address);
            }
        });

        addressPanel.add(addressInput);
        addressPanel.add(setButton);

        rowPanel.add(new JLabel("Rows:"));
        rowPanel.add(addRowButton);
        rowPanel.add(removeRowButton);

        colPanel.add(new JLabel("Columns:"));
        colPanel.add(addColButton);
        colPanel.add(removeColButton);

        sizePanel.add(rowPanel);
        sizePanel.add(colPanel);

        panel.add(addressPanel);
        panel.add(sizePanel);

        add(panel, BorderLayout.SOUTH);
    }
}
