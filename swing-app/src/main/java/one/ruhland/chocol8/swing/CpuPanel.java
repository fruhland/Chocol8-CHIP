package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.chip.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CpuPanel extends JPanel {

    private final Machine machine;
    
    private final JPanel rootPanel = new JPanel(new GridLayout(6,1));

    private final JLabel frequencyLabel = new JLabel();
    private final JLabel programCounterLabel = new JLabel();
    private final JLabel indexRegisterLabel = new JLabel();
    private final JLabel timerLabel = new JLabel();
    private final JLabel soundTimerLabel = new JLabel();
    private final JLabel soundFrequencyLabel = new JLabel();

    private final JTextField frequencyField = new JTextField(8);
    private final JTextField programCounterField = new JTextField(8);
    private final JTextField indexRegisterField = new JTextField(8);
    private final JTextField timerField = new JTextField(8);
    private final JTextField soundTimerField = new JTextField(8);
    private final JTextField soundFrequencyField = new JTextField(8);

    CpuPanel(Machine machine) {
        this.machine = machine;

        setupFrequencyPanel();
        setupProgramCounterPanel();
        setupIndexRegisterField();
        setupTimerField();
        setupSoundTimerField();
        setupSoundFrequencyField();

        add(rootPanel);
    }

    private void setupFrequencyPanel() {
        var frequencyPanel = new JPanel(new GridLayout(2, 2));
        var frequencyButton = new JButton("Set");

        ActionListener listener = (ActionEvent e) -> {
            if (frequencyField.getText().isBlank()) {
                return;
            }

            double frequency = Double.parseDouble(frequencyField.getText().replace(',', '.'));

            if (frequency <= 0) {
                frequency = 1;
            }

            machine.getCpu().getClock().setFrequency(frequency);
            frequencyField.setText("");
        };

        frequencyField.addActionListener(listener);
        frequencyButton.addActionListener(listener);

        frequencyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        frequencyPanel.add(new JLabel("Clock Frequency:"));
        frequencyPanel.add(frequencyLabel);
        frequencyPanel.add(frequencyField);
        frequencyPanel.add(frequencyButton);

        rootPanel.add(frequencyPanel);
    }
    
    private void setupProgramCounterPanel() {
        var programCounterPanel = new JPanel(new GridLayout(2, 2));
        var programCounterButton = new JButton("Set");

        ActionListener listener = (ActionEvent e) -> {
            if (programCounterField.getText().isBlank()) {
                return;
            }

            short programCounter;

            if (programCounterField.getText().startsWith("0x")) {
                programCounter = (short) Integer.parseUnsignedInt(programCounterField.getText().substring(2), 16);
            } else {
                programCounter = (short) Integer.parseUnsignedInt(programCounterField.getText(), 10);
            }

            if (programCounter < 0) {
                programCounter = 0;
            }

            if (programCounter > machine.getMemory().getSize()) {
                programCounter = (short) (machine.getMemory().getSize() - 1);
            }

            machine.getCpu().setProgramCounter(programCounter);
            programCounterField.setText("");
        };

        programCounterField.addActionListener(listener);
        programCounterButton.addActionListener(listener);

        programCounterLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        programCounterPanel.add(new JLabel("Program Counter:"));
        programCounterPanel.add(programCounterLabel);
        programCounterPanel.add(programCounterField);
        programCounterPanel.add(programCounterButton);

        rootPanel.add(programCounterPanel);
    }

    private void setupIndexRegisterField() {
        var indexRegisterPanel = new JPanel(new GridLayout(2, 2));
        var indexRegisterButton = new JButton("Set");

        ActionListener listener = (ActionEvent e) -> {
            if (indexRegisterField.getText().isBlank()) {
                return;
            }

            short indexRegister;

            if (indexRegisterField.getText().startsWith("0x")) {
                indexRegister = (short) Integer.parseUnsignedInt(indexRegisterField.getText().substring(2), 16);
            } else {
                indexRegister = (short) Integer.parseUnsignedInt(indexRegisterField.getText(), 10);
            }

            if (indexRegister > machine.getMemory().getSize()) {
                indexRegister = (short) (machine.getMemory().getSize() - 1);
            }

            machine.getCpu().setIndexRegister(indexRegister);
            indexRegisterField.setText("");
        };

        indexRegisterField.addActionListener(listener);
        indexRegisterButton.addActionListener(listener);

        indexRegisterLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        indexRegisterPanel.add(new JLabel("Index Register:"));
        indexRegisterPanel.add(indexRegisterLabel);
        indexRegisterPanel.add(indexRegisterField);
        indexRegisterPanel.add(indexRegisterButton);

        rootPanel.add(indexRegisterPanel);
    }
    
    private void setupTimerField() {
        var timerPanel = new JPanel(new GridLayout(2, 2));
        var timerButton = new JButton("Set");

        ActionListener listener = (ActionEvent e) -> {
            if (timerField.getText().isBlank()) {
                return;
            }

            byte timer;

            if (timerField.getText().startsWith("0x")) {
                timer = (byte) Integer.parseUnsignedInt(timerField.getText().substring(2), 16);
            } else {
                timer = (byte) Integer.parseUnsignedInt(timerField.getText(), 10);
            }

            machine.getTimer().setCounter(timer);
            timerField.setText("");
        };

        timerField.addActionListener(listener);
        timerButton.addActionListener(listener);

        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        timerPanel.add(new JLabel("Timer:"));
        timerPanel.add(timerLabel);
        timerPanel.add(timerField);
        timerPanel.add(timerButton);

        rootPanel.add(timerPanel);
    }

    private void setupSoundTimerField() {
        var soundTimerPanel = new JPanel(new GridLayout(2, 2));
        var soundTimerButton = new JButton("Set");

        ActionListener listener = (ActionEvent e) -> {
            if (soundTimerField.getText().isBlank()) {
                return;
            }

            byte soundTimer;

            if (soundTimerField.getText().startsWith("0x")) {
                soundTimer = (byte) Integer.parseUnsignedInt(soundTimerField.getText().substring(2), 16);
            } else {
                soundTimer = (byte) Integer.parseUnsignedInt(soundTimerField.getText(), 10);
            }

            machine.getSound().setCounter(soundTimer);
            soundTimerField.setText("");
        };

        soundTimerField.addActionListener(listener);
        soundTimerButton.addActionListener(listener);

        soundTimerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        soundTimerPanel.add(new JLabel("Sound Timer:"));
        soundTimerPanel.add(soundTimerLabel);
        soundTimerPanel.add(soundTimerField);
        soundTimerPanel.add(soundTimerButton);

        rootPanel.add(soundTimerPanel);
    }

    private void setupSoundFrequencyField() {
        var soundFrequencyPanel = new JPanel(new GridLayout(2, 2));
        var soundFrequencyButton = new JButton("Set");

        ActionListener listener = (ActionEvent e) -> {
            if (soundFrequencyField.getText().isBlank()) {
                return;
            }

            machine.getSound().setToneFrequency(Integer.parseUnsignedInt(soundFrequencyField.getText()));
            soundFrequencyField.setText("");
        };

        soundFrequencyField.addActionListener(listener);
        soundFrequencyButton.addActionListener(listener);

        soundFrequencyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        soundFrequencyPanel.add(new JLabel("Sound Frequency:"));
        soundFrequencyPanel.add(soundFrequencyLabel);
        soundFrequencyPanel.add(soundFrequencyField);
        soundFrequencyPanel.add(soundFrequencyButton);

        rootPanel.add(soundFrequencyPanel);
    }
    
    void refresh() {
        int currentInstruction = ((machine.getMemory().getByte(machine.getCpu().getProgramCounter() + 1) & 0xff) |
                ((machine.getMemory().getByte(machine.getCpu().getProgramCounter()) & 0xff) << 8));
        int currentIndexValue = ((machine.getMemory().getByte(machine.getCpu().getIndexRegister() + 1) & 0xff) |
                ((machine.getMemory().getByte(machine.getCpu().getIndexRegister()) & 0xff) << 8));

        frequencyLabel.setText(ValueFormatter.formatValue(machine.getCpu().getClock().getFrequency(), "Hz"));
        programCounterLabel.setText(String.format("[%04x] = %04x", machine.getCpu().getProgramCounter(), currentInstruction));
        indexRegisterLabel.setText(String.format("[%04x] = %04x", machine.getCpu().getIndexRegister(), currentIndexValue));
        timerLabel.setText(String.format("%02x", machine.getTimer().getCounter()));
        soundTimerLabel.setText(String.format("%02x", machine.getSound().getCounter()));
        soundFrequencyLabel.setText(ValueFormatter.formatValue(machine.getSound().getToneFrequency(), "Hz"));
    }
}
