package edu.oop.ecosystem.model;

/** Хищник. */
public class Predator extends Animal {

    /** Создаёт хищника. */
    public Predator(Position position, int energy, int age) {
        super(position, energy, age);
    }

    @Override
    public Animal copy() {
        return new Predator(getPosition(), getEnergy(), getAge());
    }
}
