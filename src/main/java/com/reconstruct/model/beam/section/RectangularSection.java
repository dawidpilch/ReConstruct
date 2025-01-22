package com.reconstruct.model.beam.section;

import com.reconstruct.model.value.PositiveDouble;

import java.util.Objects;

public record RectangularSection(PositiveDouble depth, PositiveDouble width) implements Section {
    public RectangularSection {
        Objects.requireNonNull(depth);
        Objects.requireNonNull(width);
    }

    @Override
    public PositiveDouble area() {
        return new PositiveDouble(depth.doubleValue() * width.doubleValue());
    }

    @Override
    public String toString() {
        return String.format("R %sx%s", width, depth);
    }

    @Override
    public PositiveDouble depth() {
        return depth;
    }

    @Override
    public PositiveDouble width() {
        return width;
    }
}