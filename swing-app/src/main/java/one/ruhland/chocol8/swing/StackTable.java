package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class StackTable extends JTable {

    private final Machine machine;

    StackTable(Machine machine) {
        this.machine = machine;

        setModel(new StackTableModel());
        setDefaultRenderer(String.class, new StackCellRenderer());
    }

    private final class StackTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return machine.getCpu().getStack().getSize();
        }

        @Override
        public int getColumnCount() {
            return 2;
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
            return col != 0;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return String.format("%x", row);
            }

            if (col >= machine.getCpu().getVRegisters().length) {
                return "";
            }

            return String.format("%04x", machine.getCpu().getStack().peekAt(row));
        }

        @Override
        public void setValueAt(Object o, int row, int col) {
            if (col == 0 || col >= machine.getCpu().getVRegisters().length) {
                return;
            }

            machine.getCpu().getStack().setAt(row, (short) Integer.parseInt(o.toString(), 16));
        }
    }
}
