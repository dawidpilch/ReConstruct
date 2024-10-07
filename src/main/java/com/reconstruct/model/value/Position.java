package com.reconstruct.model.value;

import java.util.Objects;

public record Position(PositiveNumber value) {
    public Position {
        Objects.requireNonNull(value);
    }

    public double asDouble() {
        return value.value();
    }
}