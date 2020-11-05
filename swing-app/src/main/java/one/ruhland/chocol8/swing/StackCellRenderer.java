package one.ruhland.chocol8.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StackCellRenderer extends DefaultTableCellRenderer {

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
