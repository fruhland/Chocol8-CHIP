package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.LockSupport;

class MemoryTable extends JTable {

    private final Machine machine;

    MemoryTable(Machine machine, int rowCount, int columnCount) {
        this.machine = machine;

        setModel(new MemoryTableModel(rowCount, columnCount));
    }

    void setTableSize(int rowCount, int columnCount) {
        setModel(new MemoryTableModel(rowCount, columnCount));
    }

    private final class MemoryTableModel extends AbstractTableModel {

        private final int rowCount;
        private final int columnCount;

        MemoryTableModel(int rowCount, int columnCount) {
            this.rowCount = rowCount;
            this.columnCount = columnCount;
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return columnCount;
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
            int index = machine.getCpu().getProgramCounter() + row * columnCount + col;

            return index >= 0 && index <= machine.getMemory().getSize();
        }

        @Override
        public Object getValueAt(int row, int col) {
            int index = machine.getCpu().getProgramCounter() + row * columnCount + col;

            if(index < 0 || index > machine.getMemory().getSize()) {
                return "";
            }

            return String.format("%02x", machine.getMemory().getByte(index));
        }

        @Override
        public void setValueAt(Object o, int row, int col) {
            int index = machine.getCpu().getProgramCounter() + row * columnCount + col;

            if(index < 0 || index > machine.getMemory().getSize()) {
                return;
            }

            machine.getMemory().setByte(machine.getCpu().getProgramCounter() + row * columnCount + col,
                    (byte) Integer.parseInt(o.toString(), 16));
        }
    }
}
