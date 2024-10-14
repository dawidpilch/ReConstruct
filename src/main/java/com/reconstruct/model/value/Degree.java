package com.reconstruct.model.value;

public record Degree(double value) {
    public Degree {
        if (!(value >= 0d) && !(value <= 180d))
            throw new IllegalArgumentException("Degree doubleValue must be >= 0 and <= 180");
    }
}