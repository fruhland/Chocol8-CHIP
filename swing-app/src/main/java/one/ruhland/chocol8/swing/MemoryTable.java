package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;

class MemoryTable extends JTable {

    private static final int COLUMN_COUNT = 8;

    private final Machine machine;

    private int rowCount;

    MemoryTable(Machine machine, int rowCount) {
        this.machine = machine;
        this.rowCount = rowCount;

        setModel(new MemoryTableModel());

        Timer updateTimer = new Timer(100,
                (ActionEvent e) -> ((AbstractTableModel) getModel()).fireTableDataChanged());
        updateTimer.start();
    }

    void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        setModel(new MemoryTableModel());
    }

    private final class MemoryTableModel extends AbstractTableModel {

        MemoryTableModel() {}

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return COLUMN_COUNT;
        }

        @Override
        public String getColumnName(int i) {
            return Integer.toHexString(i);
        }

        @Override
        public Class<?> getColumnClass(int i) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            int index = machine.getCpu().getProgramCounter() + row * COLUMN_COUNT + col;

            return index >= 0 && index <= machine.getMemory().getSize();
        }

        @Override
        public Object getValueAt(int row, int col) {
            int index = machine.getCpu().getProgramCounter() + row * COLUMN_COUNT + col;

            if(index < 0 || index > machine.getMemory().getSize()) {
                return "";
            }

            return String.format("%02x", machine.getMemory().getByte(index));
        }

        @Override
        public void setValueAt(Object o, int row, int col) {
            int index = machine.getCpu().getProgramCounter() + row * COLUMN_COUNT + col;

            if(index < 0 || index > machine.getMemory().getSize()) {
                return;
            }

            machine.getMemory().setByte(machine.getCpu().getProgramCounter() + row * COLUMN_COUNT + col,
                    (byte) Integer.parseInt(o.toString(), 16));
        }
    }
}
