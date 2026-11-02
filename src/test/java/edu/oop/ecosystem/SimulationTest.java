package edu.oop.ecosystem;

import edu.oop.ecosystem.model.Animal;
import edu.oop.ecosystem.model.Herbivore;
import edu.oop.ecosystem.model.Position;
import edu.oop.ecosystem.model.Predator;
import edu.oop.ecosystem.model.World;
import edu.oop.ecosystem.simulation.Simulation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test void positionStoresRow() { assertEquals(2, new Position(2, 3).getRow()); }
    @Test void positionStoresColumn() { assertEquals(3, new Position(2, 3).getColumn()); }
    @Test void equalPositionsAreEqual() { assertEquals(new Position(1, 1), new Position(1, 1)); }
    @Test void differentPositionsAreNotEqual() { assertNotEquals(new Position(1, 1), new Position(1, 2)); }

    @Test
    void herbivoreCopyKeepsData() {
        Herbivore h = new Herbivore(new Position(1, 2), 10, 3);
        Animal copy = h.copy();
        assertEquals(h.getPosition(), copy.getPosition());
        assertEquals(10, copy.getEnergy());
        assertEquals(3, copy.getAge());
    }

    @Test void predatorCopyKeepsType() {
        assertTrue(new Predator(new Position(0, 0), 10, 1).copy() instanceof Predator);
    }

    @Test void worldRejectsZeroWidth() {
        assertThrows(IllegalArgumentException.class,
                () -> new World(0, 2, new int[2][0], List.of(), 0));
    }

    @Test void worldRejectsWrongGrassHeight() {
        assertThrows(IllegalArgumentException.class,
                () -> new World(2, 2, new int[1][2], List.of(), 0));
    }

    @Test void worldRejectsAnimalOutsideField() {
        Herbivore h = new Herbivore(new Position(5, 5), 10, 0);
        assertThrows(IllegalArgumentException.class,
                () -> new World(2, 2, new int[2][2], List.of(h), 0));
    }

    @Test void worldCountsHerbivores() {
        World world = smallWorld(List.of(
                new Herbivore(new Position(0, 0), 10, 0),
                new Herbivore(new Position(1, 1), 10, 0)));
        assertEquals(2, world.getHerbivoreCount());
    }

    @Test void worldCountsPredators() {
        World world = smallWorld(List.of(new Predator(new Position(0, 0), 10, 0)));
        assertEquals(1, world.getPredatorCount());
    }

    @Test void worldCountsGrass() {
        World world = new World(2, 2, new int[][]{{1, 2}, {3, 4}}, List.of(), 0);
        assertEquals(10, world.getTotalGrass());
    }

    @Test void returnedGrassIsCopy() {
        World world = smallWorld(List.of());
        int[][] grass = world.getGrassCopy();
        grass[0][0] = 99;
        assertNotEquals(99, world.getGrass(0, 0));
    }

    @Test void returnedAnimalsAreCopies() {
        World world = smallWorld(List.of(new Herbivore(new Position(0, 0), 10, 0)));
        List<Animal> copy = world.getAnimals();
        copy.get(0).setEnergy(99);
        assertNotEquals(99, world.getAnimals().get(0).getEnergy());
    }

    @Test void simulationRejectsNegativeAnimalCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Simulation(3, 3, -1, 0, 1));
    }

    @Test void simulationCreatesCorrectAnimalCount() {
        Simulation simulation = new Simulation(4, 4, 5, 2, 1);
        assertEquals(5, simulation.getWorld().getHerbivoreCount());
        assertEquals(2, simulation.getWorld().getPredatorCount());
    }

    @Test void generationNumberIncreases() {
        Simulation simulation = new Simulation(4, 4, 2, 1, 1);
        simulation.nextGeneration();
        assertEquals(1, simulation.getWorld().getGeneration());
    }

    @Test void oldWorldIsNotChanged() {
        Simulation simulation = new Simulation(4, 4, 2, 1, 1);
        World old = simulation.getWorld();
        simulation.nextGeneration();
        assertEquals(0, old.getGeneration());
    }

    @Test void grassGrowsWithoutAnimals() {
        World world = new World(2, 2, new int[][]{{0, 0}, {0, 0}}, List.of(), 0);
        Simulation simulation = new Simulation(world, 1);
        simulation.nextGeneration();
        assertEquals(4, simulation.getWorld().getTotalGrass());
    }

    @Test void grassDoesNotGrowAboveTen() {
        World world = new World(2, 2, new int[][]{{10, 10}, {10, 10}}, List.of(), 0);
        Simulation simulation = new Simulation(world, 1);
        simulation.nextGeneration();
        assertEquals(40, simulation.getWorld().getTotalGrass());
    }

    @Test void starvingHerbivoreDies() {
        World world = smallWorld(List.of(new Herbivore(new Position(0, 0), 1, 0)));
        Simulation simulation = new Simulation(world, 1);
        simulation.nextGeneration();
        assertEquals(0, simulation.getWorld().getHerbivoreCount());
    }

    @Test void oldAnimalDies() {
        World world = smallWorld(List.of(new Predator(new Position(0, 0), 20, 20)));
        Simulation simulation = new Simulation(world, 1);
        simulation.nextGeneration();
        assertEquals(0, simulation.getWorld().getPredatorCount());
    }

    @Test void sameSeedGivesSameStart() {
        Simulation a = new Simulation(5, 5, 5, 2, 42);
        Simulation b = new Simulation(5, 5, 5, 2, 42);
        assertEquals(signature(a.getWorld()), signature(b.getWorld()));
    }

    @Test void sameSeedGivesSameFullSimulation() {
        Simulation a = new Simulation(5, 5, 8, 3, 42);
        Simulation b = new Simulation(5, 5, 8, 3, 42);
        for (int i = 0; i < 10; i++) {
            a.nextGeneration();
            b.nextGeneration();
        }
        assertEquals(signature(a.getWorld()), signature(b.getWorld()));
    }

    private static World smallWorld(List<Animal> animals) {
        return new World(2, 2, new int[][]{{0, 0}, {0, 0}}, animals, 0);
    }

    private static String signature(World world) {
        StringBuilder s = new StringBuilder();
        s.append(world.getGeneration()).append('|')
                .append(world.getTotalGrass()).append('|')
                .append(world.getHerbivoreCount()).append('|')
                .append(world.getPredatorCount());

        for (Animal animal : world.getAnimals()) {
            s.append('|').append(animal.getClass().getSimpleName())
                    .append(':').append(animal.getPosition())
                    .append(':').append(animal.getEnergy())
                    .append(':').append(animal.getAge());
        }
        return s.toString();
    }
}
