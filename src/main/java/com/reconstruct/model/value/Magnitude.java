package com.reconstruct.model.value;

import java.util.Objects;

public class Magnitude implements DoubleValue {
    public static Magnitude of(double value) {
        return new Magnitude(value);
    }

    public static Magnitude zero() {
        return Magnitude.of(0);
    }

    private final double value;

    private Magnitude(double value) {
        this.value = value;
    }

    public Magnitude negated() {
        return new Magnitude(-value);
    }

    public Magnitude negative() {
        return new Magnitude(-Math.abs(value));
    }

    public Magnitude positive() {
        return new Magnitude(Math.abs(value));
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Magnitude magnitude = (Magnitude) o;
        return Double.compare(value, magnitude.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}