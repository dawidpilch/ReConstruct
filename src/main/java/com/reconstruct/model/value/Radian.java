package com.reconstruct.model.value;

public final class Radian implements DoubleValue {
    public static Radian of(Degree degree) {
        return new Radian(degree.doubleValue() * (Math.PI / 180));
    }

    public static Radian of(double value) {
        if (value < 0 || value > (2 * Math.PI))
            throw new IllegalArgumentException();
        return new Radian(value);
    }

    private final double value;

    private Radian(double value) {
        this.value = value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}