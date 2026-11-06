package edu.oop.ecosystem.app;

import javax.swing.SwingUtilities;

/** Запуск программы с графическим окном. */
public class Main {

    /** Запускает интерфейс симуляции. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
    }
}
