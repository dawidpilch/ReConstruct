package com.reconstruct.model.value;

public class Degree implements DoubleValue {
    public static Degree of(double value) {
        if (!(value >= 0d) && !(value <= 180d))
            throw new IllegalArgumentException("Degree doubleValue must be >= 0 and <= 180");
        return new Degree(value);
    }

    private final double doubleValue;

    private Degree(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }
}