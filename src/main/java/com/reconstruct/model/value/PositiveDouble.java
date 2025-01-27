package com.reconstruct.model.value;

public final class PositiveDouble implements DoubleValue {
    public static PositiveDouble of(double value) {
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
        return new PositiveDouble(value);
    }

    private final double value;

    private PositiveDouble(double value) {
        this.value = value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}