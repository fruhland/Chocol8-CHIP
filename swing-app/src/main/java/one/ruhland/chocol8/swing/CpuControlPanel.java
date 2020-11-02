package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import java.awt.event.ActionEvent;

class CpuControlPanel extends JPanel {

    private final Machine machine;

    private final JButton playPauseButton = new JButton("▶");
    private final JButton stopButton = new JButton("⏹️");
    private final JButton nextButton = new JButton("⏭️");

    CpuControlPanel(Machine machine) {
        this.machine = machine;

        stopButton.addActionListener((ActionEvent e) -> {
            machine.getCpu().setProgramCounter((short) 0x0200);
        });

        playPauseButton.addActionListener((ActionEvent e) -> {
            if (machine.isRunning()) {
                machine.stop();
            } else {
                machine.start();
            }
        });

        nextButton.addActionListener((ActionEvent e) -> {
            if (!machine.isRunning()) {
                machine.getCpu().runCycle();
            }
        });

        add(stopButton);
        add(playPauseButton);
        add(nextButton);
    }

    void refresh() {
        playPauseButton.setText(machine.isRunning() ? "⏸️" : "▶");
    }
}
