package com.reconstruct.model.beam.section;

import com.reconstruct.model.value.PositiveNumber;

import java.util.Objects;

public record Rectangular(PositiveNumber depth, PositiveNumber width) implements Section {
    public Rectangular {
        Objects.requireNonNull(depth);
        Objects.requireNonNull(width);
    }

    @Override
    public PositiveNumber area() {
        return new PositiveNumber(depth.value() * width.value());
    }

    @Override
    public String name() {
        return String.format("R %sx%s", width, depth);
    }

    @Override
    public String toString() {
        return name();
    }
}