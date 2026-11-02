package edu.oop.ecosystem.app;

import edu.oop.ecosystem.model.World;
import edu.oop.ecosystem.simulation.Simulation;

import java.util.List;
import java.util.Scanner;

/** Консольный запуск программы. */
public class Main {

    /** Запускает симуляцию. */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Seed: ");
        long seed = scanner.nextLong();

        System.out.print("Generations: ");
        int generations = scanner.nextInt();

        Simulation simulation = new Simulation(10, 10, 20, 5, seed);

        for (int i = 0; i < generations; i++) {
            List<String> events = simulation.nextGeneration();
            World world = simulation.getWorld();

            System.out.println("\nGeneration " + world.getGeneration());

            for (String event : events) {
                System.out.println(event);
            }

            System.out.println("Herbivores: " + world.getHerbivoreCount());
            System.out.println("Predators: " + world.getPredatorCount());
            System.out.println("Grass: " + world.getTotalGrass());

            if (world.getHerbivoreCount() == 0 && world.getPredatorCount() == 0) {
                break;
            }
        }
    }
}
