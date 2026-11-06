package edu.oop.ecosystem.app;

import edu.oop.ecosystem.model.Animal;
import edu.oop.ecosystem.model.Herbivore;
import edu.oop.ecosystem.model.Position;
import edu.oop.ecosystem.model.World;
import edu.oop.ecosystem.simulation.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Простое графическое окно экосистемы. */
public class GameWindow extends JFrame {
    private Simulation simulation;
    private final Timer timer;
    private final JLabel[][] cells = new JLabel[10][10];
    private final JTextField seedField = new JTextField("42");
    private final JTextField turnsField = new JTextField("30");
    private final JLabel statsLabel = new JLabel();
    private final JTextArea log = new JTextArea();
    private final JButton startButton = new JButton("Старт");
    private final JButton pauseButton = new JButton("Пауза");
    private final JButton stepButton = new JButton("Один ход");
    private int maxTurns = 30;

    /** Создаёт окно программы. */
    public GameWindow() {
        super("Экосистема на решётке");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        timer = new Timer(650, event -> nextTurn());

        JPanel board = new JPanel(new GridLayout(10, 10, 2, 2));
        board.setPreferredSize(new Dimension(520, 520));
        board.setBackground(Color.WHITE);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setFont(new Font("SansSerif", Font.BOLD, 14));
                cells[row][col] = cell;
                board.add(cell);
            }
        }
        JPanel left = new JPanel(new BorderLayout());
        left.add(board, BorderLayout.CENTER);
        left.add(new JLabel("Синий: травоядные (Т)     Оранжевый: хищники (Х)"), BorderLayout.SOUTH);
        add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setPreferredSize(new Dimension(290, 520));
        JPanel options = new JPanel(new GridLayout(5, 2, 5, 5));
        options.add(new JLabel("Seed:"));
        options.add(seedField);
        options.add(new JLabel("Ходов:"));
        options.add(turnsField);
        options.add(startButton);
        options.add(pauseButton);
        options.add(stepButton);
        JButton resetButton = new JButton("Заново");
        options.add(resetButton);
        options.add(new JLabel("Показатели:"));
        options.add(statsLabel);
        right.add(options, BorderLayout.NORTH);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createTitledBorder("Журнал событий"));
        right.add(scroll, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        startButton.addActionListener(event -> start());
        pauseButton.addActionListener(event -> pause());
        stepButton.addActionListener(event -> nextTurn());
        resetButton.addActionListener(event -> reset());

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        reset();
        pack();
        setLocationRelativeTo(null);
    }

    private void reset() {
        try {
            long seed = Long.parseLong(seedField.getText().trim());
            int turns = Integer.parseInt(turnsField.getText().trim());
            if (turns < 1 || turns > 1000) {
                throw new IllegalArgumentException("Число ходов должно быть от 1 до 1000.");
            }
            timer.stop();
            maxTurns = turns;
            simulation = new Simulation(10, 10, 20, 5, seed);
            log.setText("Мир создан, seed = " + seed + "\n");
            updateScreen();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введи целые числа для seed и ходов.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void start() {
        if (!finished()) {
            timer.start();
            updateScreen();
        }
    }

    private void pause() {
        timer.stop();
        updateScreen();
    }

    private boolean finished() {
        World world = simulation.getWorld();
        return world.getGeneration() >= maxTurns
                || (world.getHerbivoreCount() == 0 && world.getPredatorCount() == 0);
    }

    private void nextTurn() {
        if (finished()) return;
        List<String> events = simulation.nextGeneration();
        World world = simulation.getWorld();
        log.append("\nПоколение " + world.getGeneration() + "\n");
        for (String event : events) {
            String text = event.replace("Predator ate herbivore at", "Хищник съел травоядное:")
                    .replace("New herbivore at", "Родилось травоядное:")
                    .replace("New predator at", "Родился хищник:");
            log.append(text + "\n");
        }
        if (finished()) {
            timer.stop();
            log.append("Симуляция завершена.\n");
        }
        log.setCaretPosition(log.getDocument().getLength());
        updateScreen();
    }

    private void updateScreen() {
        World world = simulation.getWorld();
        int[][] herbivores = new int[10][10];
        int[][] predators = new int[10][10];
        for (Animal animal : world.getAnimals()) {
            Position p = animal.getPosition();
            if (animal instanceof Herbivore) {
                herbivores[p.getRow()][p.getColumn()]++;
            } else {
                predators[p.getRow()][p.getColumn()]++;
            }
        }

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int grass = world.getGrass(row, col);
                JLabel cell = cells[row][col];
                cell.setBackground(new Color(236 - 8 * grass, 245 - 3 * grass, 228 - 10 * grass));
                String h = herbivores[row][col] == 0 ? "" : "<font color='#3265CC'>Т" + herbivores[row][col] + "</font>";
                String p = predators[row][col] == 0 ? "" : "<font color='#C95F21'>Х" + predators[row][col] + "</font>";
                cell.setText("<html>" + h + " " + p + "</html>");
                cell.setToolTipText("Трава: " + grass + ", травоядных: "
                        + herbivores[row][col] + ", хищников: " + predators[row][col]);
            }
        }
        statsLabel.setText("<html>Ход " + world.getGeneration() + "/" + maxTurns
                + "<br>Т: " + world.getHerbivoreCount()
                + " | Х: " + world.getPredatorCount()
                + "<br>Трава: " + world.getTotalGrass() + "</html>");
        startButton.setEnabled(!timer.isRunning() && !finished());
        pauseButton.setEnabled(timer.isRunning());
        stepButton.setEnabled(!timer.isRunning() && !finished());
        startButton.setText(world.getGeneration() == 0 ? "Старт" : "Продолжить");
    }
}
