package com.reconstruct.model.beam.value;

import com.reconstruct.model.value.DoubleValue;

import java.util.Objects;

public class Position implements DoubleValue {
    public static Position of(double value) {
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
        return new Position(value);
    }

    private final double value;

    public Position (double value) {
        this.value = value;
    }

    public boolean isToTheLeftOf(Position other) {
        return value < other.doubleValue();
    }

    public boolean isToTheRightOf(Position other) {
        return value > other.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(value, position.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public double doubleValue() {
        return value;
    }
}