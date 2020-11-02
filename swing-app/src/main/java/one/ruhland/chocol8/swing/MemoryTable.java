package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class MemoryTable extends JTable {

    private final Machine machine;

    MemoryTable(Machine machine, int rowCount, int columnCount) {
        this.machine = machine;

        setModel(new MemoryTableModel(rowCount, columnCount));
        setDefaultRenderer(String.class, new CellRenderer());
    }

    void setRowCount(final int rowCount) {
        ((MemoryTableModel) getModel()).setRowCount(rowCount);
    }

    void setColumnCount(final int columnCount) {
        ((MemoryTableModel) getModel()).setColumnCount(columnCount);
    }

    void setAddress(int address) {
        ((MemoryTableModel) getModel()).setStartAddress(address);
    }

    private final class MemoryTableModel extends DefaultTableModel {

        private int startAddress = 0;

        private MemoryTableModel(int rowCount, int columnCount) {
            super(rowCount + 1, columnCount + 1);
        }

        void setStartAddress(final int startAddress) {
            this.startAddress = startAddress;
        }

        @Override
        public void setRowCount(int rowCount) {
            if (rowCount > 0) {
                super.setRowCount(rowCount);
            }
        }

        @Override
        public void setColumnCount(int columnCount) {
            if (columnCount > 0) {
                super.setColumnCount(columnCount);
            }
        }

        @Override
        public String getColumnName(int i) {
            return "";
        }

        @Override
        public Class<?> getColumnClass(int i) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (row == 0 || col == 0) {
                return false;
            }

            int index = startAddress + (row - 1) * (getColumnCount() - 1) + (col - 1);
            return index >= 0 && index < machine.getMemory().getSize();
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (row == 0 && col == 0) {
                return "";
            }

            if (row == 0) {
                return String.format("%02x", col - 1);
            }

            if (col == 0) {
                return String.format("%04x", startAddress + (row -1) * (getColumnCount() - 1));
            }

            int index = startAddress + (row - 1) * (getColumnCount() - 1) + (col - 1);

            if (index < 0 || index > machine.getMemory().getSize()) {
                return "";
            }

            return String.format("%02x", machine.getMemory().getByte(index));
        }

        @Override
        public void setValueAt(Object o, int row, int col) {
            if (row == 0 || col == 0) {
                return;
            }

            int index = startAddress + (row - 1) * (getColumnCount() - 1) + (col - 1);

            if (index < 0 || index > machine.getMemory().getSize()) {
                return;
            }

            machine.getMemory().setByte(startAddress + (row -1) * (getColumnCount() - 1) + (col - 1),
                    (byte) Integer.parseInt(o.toString(), 16));
        }
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            var ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if ((row == 0 || col == 0) && ret instanceof JLabel) {
                var label = (JLabel) ret;

                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setBorder(BorderFactory.createEtchedBorder());
                label.setBackground(Color.LIGHT_GRAY);
                label.setHorizontalAlignment(SwingConstants.CENTER);
            } else if (ret instanceof JLabel) {
                var label = (JLabel) ret;

                label.setBackground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            }

            return ret;
        }
    }
}
