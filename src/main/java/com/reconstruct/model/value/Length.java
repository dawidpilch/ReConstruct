package com.reconstruct.model.value;

public class Length implements DoubleValue {
    public static Length of(double doubleValue) {
        if (doubleValue <= 0)
            throw new IllegalArgumentException("Value must be > 0");
        return new Length(doubleValue);
    }

    private final double doubleValue;

    private Length(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }
}