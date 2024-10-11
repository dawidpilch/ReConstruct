package com.reconstruct.model.value;

public record PositiveDouble(double value) {
    public PositiveDouble {
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
    }
}