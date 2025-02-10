package com.reconstruct.model.beam.loading.moment;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

public class BendingMoment {
    public static BendingMoment of(Position position, Magnitude magnitude) {
        if (magnitude.doubleValue() >= 0) {
            return counterClockwise(position, magnitude);
        } return clockwise(position, magnitude);
    }

    public static BendingMoment clockwise(Position position, Magnitude magnitude) {
        return new BendingMoment(position, magnitude.negative());
    }

    public static BendingMoment counterClockwise(Position position, Magnitude magnitude) {
        return new BendingMoment(position, magnitude.positive());
    }

    private final Position position;
    private final Magnitude magnitude;

    private BendingMoment(Position position, Magnitude magnitude) {
        this.position = position;
        this.magnitude = magnitude;
    }

    public boolean isClockwise() {
        return magnitude.doubleValue() < 0;
    }

    public boolean isCounterClockwise() {
        return !isClockwise();
    }

    public Position position() {
        return position;
    }

    public Magnitude magnitude() {
        return magnitude;
    }

    public String unit() {
        return "[kN/m]";
    }
}