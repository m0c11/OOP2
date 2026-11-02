package edu.oop.ecosystem.model;

/** Общий класс для животных. */
public abstract class Animal {
    private Position position;
    private int energy;
    private int age;

    /** Создаёт животное. */
    protected Animal(Position position, int energy, int age) {
        this.position = position;
        this.energy = energy;
        this.age = age;
    }

    /** @return позиция */
    public Position getPosition() { return position; }

    /** @return энергия */
    public int getEnergy() { return energy; }

    /** @return возраст */
    public int getAge() { return age; }

    /** Устанавливает позицию. */
    public void setPosition(Position position) { this.position = position; }

    /** Устанавливает энергию. */
    public void setEnergy(int energy) { this.energy = energy; }

    /** Устанавливает возраст. */
    public void setAge(int age) { this.age = age; }

    /** @return копия животного */
    public abstract Animal copy();
}
