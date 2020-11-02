package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class RegisterTable extends JTable {

    private final Machine machine;

    RegisterTable(Machine machine) {
        this.machine = machine;

        setModel(new RegisterTableModel());
        setDefaultRenderer(String.class, new CellRenderer());
    }

    private final class RegisterTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return machine.getCpu().getVRegisters().length;
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
                return String.format("V%x", row);
            }

            if (col >= machine.getCpu().getVRegisters().length) {
                return "";
            }

            return String.format("%02x", machine.getCpu().getVRegisters()[row]);
        }

        @Override
        public void setValueAt(Object o, int row, int col) {
            if (col == 0 || col >= machine.getCpu().getVRegisters().length) {
                return;
            }

            machine.getCpu().getVRegisters()[row] = (byte) Integer.parseInt(o.toString(), 16);
        }
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            var ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if ((col == 0) && ret instanceof JLabel) {
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
