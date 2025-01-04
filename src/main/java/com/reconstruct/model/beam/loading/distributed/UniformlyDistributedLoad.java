package com.reconstruct.model.beam.loading.distributed;

import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Magnitude;

public class UniformlyDistributedLoad {
    private final Position startPosition;
    private final Position endPosition;
    private final Magnitude magnitude;

    public static UniformlyDistributedLoad of(Position startPosition, Position endPosition, Magnitude magnitude) {
        if (startPosition.isToTheLeftOf(endPosition) || startPosition.equals(endPosition)) {
            throw new IllegalArgumentException("End position must be greater than start position");
        }

        return new UniformlyDistributedLoad(startPosition, endPosition, magnitude);
    }

    private UniformlyDistributedLoad(Position startPosition, Position endPosition, Magnitude magnitude) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.magnitude = magnitude;
    }

    public VerticalPointLoad resultantForce() {
        double x = endPosition.doubleValue() - startPosition.doubleValue();
        Position resultantPosition = Position.of(endPosition.doubleValue() / 2);
        Magnitude resultantMagnitude = Magnitude.of(magnitude.doubleValue() * x);
        return VerticalPointLoad.of(resultantPosition, resultantMagnitude);
    }

    public Position startPosition() {
        return startPosition;
    }

    public Position endPosition() {
        return endPosition;
    }

    public Magnitude magnitude() {
        return magnitude;
    }
}
