package com.reconstruct.model.value;

import java.util.Objects;

public class Magnitude implements DoubleValue, Comparable<Magnitude> {
    public static Magnitude of(double value) {
        return new Magnitude(value);
    }

    private static final Magnitude zero = Magnitude.of(0);
    public static Magnitude zero() {
        return zero;
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

    @Override
    public int compareTo(Magnitude o) {
        if (this.equals(o)) {
            return 0;
        } else if (this.doubleValue() > o.doubleValue()) {
            return 1;
        } else {
            return -1;
        }
    }
}