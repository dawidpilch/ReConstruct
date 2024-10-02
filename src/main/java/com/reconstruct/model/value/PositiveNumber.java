package com.reconstruct.model.value;

public record PositiveNumber(double value) {
    public PositiveNumber {
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
    }
}