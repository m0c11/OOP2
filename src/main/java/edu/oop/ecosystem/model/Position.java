package edu.oop.ecosystem.model;

import java.util.Objects;

/** Координаты клетки поля. */
public class Position {
    private final int row;
    private final int column;

    /** Создаёт позицию. */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /** @return строка */
    public int getRow() { return row; }

    /** @return столбец */
    public int getColumn() { return column; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position other)) return false;
        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
