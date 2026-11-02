package edu.oop.ecosystem.model;

/** Травоядное животное. */
public class Herbivore extends Animal {

    /** Создаёт травоядное. */
    public Herbivore(Position position, int energy, int age) {
        super(position, energy, age);
    }

    @Override
    public Animal copy() {
        return new Herbivore(getPosition(), getEnergy(), getAge());
    }
}
