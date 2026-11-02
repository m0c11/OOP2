package edu.oop.ecosystem.model;

import java.util.ArrayList;
import java.util.List;

/** Простое состояние мира: поле, трава и животные. */
public class World {
    private final int width;
    private final int height;
    private final int[][] grass;
    private final List<Animal> animals;
    private final int generation;

    /** Создаёт состояние мира. */
    public World(int width, int height, int[][] grass, List<Animal> animals, int generation) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("World size must be positive");
        }
        if (grass.length != height) {
            throw new IllegalArgumentException("Wrong grass height");
        }
        for (int[] row : grass) {
            if (row.length != width) {
                throw new IllegalArgumentException("Wrong grass width");
            }
        }

        this.width = width;
        this.height = height;
        this.grass = copyGrass(grass);
        this.animals = new ArrayList<>();
        for (Animal animal : animals) {
            if (!isInside(animal.getPosition())) {
                throw new IllegalArgumentException("Animal is outside world");
            }
            this.animals.add(animal.copy());
        }
        this.generation = generation;
    }

    /** @return ширина поля */
    public int getWidth() { return width; }

    /** @return высота поля */
    public int getHeight() { return height; }

    /** @return номер поколения */
    public int getGeneration() { return generation; }

    /** Возвращает количество травы в клетке. */
    public int getGrass(int row, int column) { return grass[row][column]; }

    /** @return копия массива травы */
    public int[][] getGrassCopy() { return copyGrass(grass); }

    /** @return копии животных */
    public List<Animal> getAnimals() {
        List<Animal> copy = new ArrayList<>();
        for (Animal animal : animals) {
            copy.add(animal.copy());
        }
        return copy;
    }

    /** @return число травоядных */
    public int getHerbivoreCount() {
        int count = 0;
        for (Animal animal : animals) {
            if (animal instanceof Herbivore) count++;
        }
        return count;
    }

    /** @return число хищников */
    public int getPredatorCount() {
        int count = 0;
        for (Animal animal : animals) {
            if (animal instanceof Predator) count++;
        }
        return count;
    }

    /** @return общее количество травы */
    public int getTotalGrass() {
        int sum = 0;
        for (int[] row : grass) {
            for (int value : row) sum += value;
        }
        return sum;
    }

    /** Проверяет, находится ли клетка на поле. */
    public boolean isInside(Position position) {
        return position.getRow() >= 0 && position.getRow() < height
                && position.getColumn() >= 0 && position.getColumn() < width;
    }

    private static int[][] copyGrass(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
