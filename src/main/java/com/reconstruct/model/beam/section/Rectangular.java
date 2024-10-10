package com.reconstruct.model.beam.section;

import com.reconstruct.model.value.PositiveDouble;

import java.util.Objects;

public record Rectangular(PositiveDouble depth, PositiveDouble width) implements Section {
    public Rectangular {
        Objects.requireNonNull(depth);
        Objects.requireNonNull(width);
    }

    @Override
    public PositiveDouble area() {
        return new PositiveDouble(depth.value() * width.value());
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