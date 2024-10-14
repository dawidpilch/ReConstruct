package com.reconstruct.model.beam.loading.moment;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

public class BendingMoment {
    private final Position position;
    private final Magnitude magnitude;

    public static BendingMoment clockwise(Position position, Magnitude magnitude) {
        return new BendingMoment(position, magnitude.negative());
    }

    public static BendingMoment counterClockwise(Position position, Magnitude magnitude) {
        return new BendingMoment(position, magnitude.positive());
    }

    private BendingMoment(Position position, Magnitude magnitude) {
        this.position = position;
        this.magnitude = magnitude;
    }

    public Position position() {
        return position;
    }

    public Magnitude magnitude() {
        return magnitude;
    }
}