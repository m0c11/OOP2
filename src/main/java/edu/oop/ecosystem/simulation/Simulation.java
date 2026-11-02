package edu.oop.ecosystem.simulation;

import edu.oop.ecosystem.model.Animal;
import edu.oop.ecosystem.model.Herbivore;
import edu.oop.ecosystem.model.Position;
import edu.oop.ecosystem.model.Predator;
import edu.oop.ecosystem.model.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Простая симуляция экосистемы. */
public class Simulation {
    private static final int MAX_GRASS = 10;

    private final Random random;
    private World world;

    /** Создаёт случайный начальный мир. */
    public Simulation(int width, int height, int herbivores, int predators, long seed) {
        if (herbivores < 0 || predators < 0) {
            throw new IllegalArgumentException("Animal count cannot be negative");
        }

        random = new Random(seed);

        int[][] grass = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                grass[row][column] = random.nextInt(MAX_GRASS + 1);
            }
        }

        List<Animal> animals = new ArrayList<>();
        for (int i = 0; i < herbivores; i++) {
            animals.add(new Herbivore(randomPosition(width, height), 12, 0));
        }
        for (int i = 0; i < predators; i++) {
            animals.add(new Predator(randomPosition(width, height), 16, 0));
        }

        world = new World(width, height, grass, animals, 0);
    }

    /** Создаёт симуляцию из готового мира. */
    public Simulation(World world, long seed) {
        this.world = world;
        random = new Random(seed);
    }

    /** Считает одно поколение и возвращает журнал событий. */
    public List<String> nextGeneration() {
        World oldWorld = world;
        int[][] newGrass = growGrass(oldWorld);
        List<Animal> animals = moveAnimals(oldWorld);

        feedHerbivores(animals, newGrass);

        List<String> events = new ArrayList<>();
        hunt(animals, events);
        reproduce(animals, events);

        world = new World(
                oldWorld.getWidth(),
                oldWorld.getHeight(),
                newGrass,
                animals,
                oldWorld.getGeneration() + 1
        );

        return events;
    }

    /** @return текущее состояние мира */
    public World getWorld() {
        return world;
    }

    private int[][] growGrass(World oldWorld) {
        int[][] result = oldWorld.getGrassCopy();
        for (int row = 0; row < oldWorld.getHeight(); row++) {
            for (int column = 0; column < oldWorld.getWidth(); column++) {
                if (result[row][column] < MAX_GRASS) {
                    result[row][column]++;
                }
            }
        }
        return result;
    }

    private List<Animal> moveAnimals(World oldWorld) {
        List<Animal> result = new ArrayList<>();

        for (Animal oldAnimal : oldWorld.getAnimals()) {
            Animal animal = oldAnimal.copy();
            animal.setAge(animal.getAge() + 1);
            animal.setEnergy(animal.getEnergy() - (animal instanceof Herbivore ? 1 : 2));

            if (animal.getEnergy() <= 0 || animal.getAge() > 20) {
                continue;
            }

            animal.setPosition(choosePosition(oldAnimal, oldWorld));
            result.add(animal);
        }

        return result;
    }

    private Position choosePosition(Animal animal, World oldWorld) {
        List<Position> cells = neighbourCells(animal.getPosition(), oldWorld);

        if (animal instanceof Herbivore) {
            int bestGrass = -1;
            List<Position> best = new ArrayList<>();

            for (Position cell : cells) {
                int grass = oldWorld.getGrass(cell.getRow(), cell.getColumn());
                if (grass > bestGrass) {
                    bestGrass = grass;
                    best.clear();
                    best.add(cell);
                } else if (grass == bestGrass) {
                    best.add(cell);
                }
            }

            return best.get(random.nextInt(best.size()));
        }

        List<Position> preyCells = new ArrayList<>();
        for (Position cell : cells) {
            for (Animal other : oldWorld.getAnimals()) {
                if (other instanceof Herbivore && other.getPosition().equals(cell)) {
                    preyCells.add(cell);
                    break;
                }
            }
        }

        if (!preyCells.isEmpty()) {
            return preyCells.get(random.nextInt(preyCells.size()));
        }

        return cells.get(random.nextInt(cells.size()));
    }

    private List<Position> neighbourCells(Position position, World oldWorld) {
        List<Position> cells = new ArrayList<>();
        cells.add(position);

        addCell(cells, position.getRow() - 1, position.getColumn(), oldWorld);
        addCell(cells, position.getRow() + 1, position.getColumn(), oldWorld);
        addCell(cells, position.getRow(), position.getColumn() - 1, oldWorld);
        addCell(cells, position.getRow(), position.getColumn() + 1, oldWorld);

        return cells;
    }

    private void addCell(List<Position> cells, int row, int column, World world) {
        Position position = new Position(row, column);
        if (world.isInside(position)) {
            cells.add(position);
        }
    }

    private void feedHerbivores(List<Animal> animals, int[][] grass) {
        for (Animal animal : animals) {
            if (animal instanceof Herbivore) {
                int row = animal.getPosition().getRow();
                int column = animal.getPosition().getColumn();
                int eaten = Math.min(3, grass[row][column]);

                grass[row][column] -= eaten;
                animal.setEnergy(animal.getEnergy() + eaten * 2);
            }
        }
    }

    private void hunt(List<Animal> animals, List<String> events) {
        List<Animal> eaten = new ArrayList<>();

        for (Animal animal : animals) {
            if (!(animal instanceof Predator)) {
                continue;
            }

            for (Animal prey : animals) {
                if (prey instanceof Herbivore
                        && !eaten.contains(prey)
                        && prey.getPosition().equals(animal.getPosition())) {
                    eaten.add(prey);
                    animal.setEnergy(animal.getEnergy() + 8);
                    events.add("Predator ate herbivore at " + animal.getPosition());
                    break;
                }
            }
        }

        animals.removeAll(eaten);
    }

    private void reproduce(List<Animal> animals, List<String> events) {
        List<Animal> children = new ArrayList<>();

        for (Animal animal : animals) {
            if (animal.getAge() >= 3 && animal.getEnergy() >= 16 && random.nextDouble() < 0.20) {
                animal.setEnergy(animal.getEnergy() - 5);

                if (animal instanceof Herbivore) {
                    children.add(new Herbivore(animal.getPosition(), 7, 0));
                    events.add("New herbivore at " + animal.getPosition());
                } else {
                    children.add(new Predator(animal.getPosition(), 9, 0));
                    events.add("New predator at " + animal.getPosition());
                }
            }
        }

        animals.addAll(children);
    }

    private Position randomPosition(int width, int height) {
        return new Position(random.nextInt(height), random.nextInt(width));
    }
}
