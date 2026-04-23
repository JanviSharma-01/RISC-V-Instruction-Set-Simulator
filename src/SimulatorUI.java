import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SimulatorUI {
    private JFrame frame;
    private JTextArea programArea;
    private JTextArea regArea;
    private JTextArea memArea;
    private JTextArea pipelineArea;
    private Simulator simulator;

    public SimulatorUI() {
        simulator = new Simulator();
    }

    public void createAndShowGUI() {
        frame = new JFrame("RISC-V 5-stage Pipeline Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,700);

        programArea = new JTextArea(20,40);
        regArea = new JTextArea(10,30);
        memArea = new JTextArea(10,30);
        pipelineArea = new JTextArea(20,30);
        pipelineArea.setEditable(false);

        JButton loadBtn = new JButton("Load Program");
        JButton stepBtn = new JButton("Step (1 cycle)");
        JButton runBtn = new JButton("Run (to completion)");
        JButton resetBtn = new JButton("Reset");

        loadBtn.addActionListener(e -> {
            simulator.reset();
            simulator.loadProgram(programArea.getText());
            updateDisplays();
        });

        stepBtn.addActionListener(e -> {
            simulator.stepCycle();
            updateDisplays();
        });

        runBtn.addActionListener(e -> {
            while(!simulator.isHalted() && simulator.getCycleCount()<10000) {
                simulator.stepCycle();
            }
            updateDisplays();
        });

        resetBtn.addActionListener(e -> {
            simulator.reset();
            updateDisplays();
        });

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Program (one assembly instruction per line)"), BorderLayout.NORTH);
        left.add(new JScrollPane(programArea), BorderLayout.CENTER);

        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.add(new JLabel("Registers"), BorderLayout.NORTH);
        rightTop.add(new JScrollPane(regArea), BorderLayout.CENTER);

        JPanel rightMid = new JPanel(new BorderLayout());
        rightMid.add(new JLabel("Memory (addr:value per line)"), BorderLayout.NORTH);
        rightMid.add(new JScrollPane(memArea), BorderLayout.CENTER);

        JPanel rightBot = new JPanel(new BorderLayout());
        rightBot.add(new JLabel("Pipeline (per-stage info)"), BorderLayout.NORTH);
        rightBot.add(new JScrollPane(pipelineArea), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(loadBtn);
        buttons.add(stepBtn);
        buttons.add(runBtn);
        buttons.add(resetBtn);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(rightTop);
        right.add(rightMid);
        right.add(rightBot);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(left, BorderLayout.WEST);
        frame.getContentPane().add(right, BorderLayout.CENTER);
        frame.getContentPane().add(buttons, BorderLayout.SOUTH);

        frame.setVisible(true);
        updateDisplays();
    }

    private void updateDisplays() {
        regArea.setText(simulator.registers.dump());
        memArea.setText(simulator.memory.dump());
        pipelineArea.setText(simulator.dumpPipeline());
    }
}
