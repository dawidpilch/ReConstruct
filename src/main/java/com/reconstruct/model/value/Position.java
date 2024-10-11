package com.reconstruct.model.value;

public record Position(double value) {
    public Position {
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
    }
}