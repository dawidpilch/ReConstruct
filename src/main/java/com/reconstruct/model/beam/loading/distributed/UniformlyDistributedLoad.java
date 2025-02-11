package com.reconstruct.model.beam.loading.distributed;

import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;

public class UniformlyDistributedLoad {
    private final Position startPosition;
    private final Position endPosition;
    private final Magnitude magnitude;

    public static UniformlyDistributedLoad of(Position startPosition, Position endPosition, Magnitude magnitude) {
        if (startPosition.isToTheRightOf(endPosition) || startPosition.equals(endPosition)) {
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
        Position resultantPosition = resultantPosition();
        Magnitude resultantMagnitude = Magnitude.of(magnitude.doubleValue() * length().doubleValue());
        return VerticalPointLoad.of(resultantPosition, resultantMagnitude);
    }

    public Position resultantPosition() {
        double x = length().doubleValue();
        double position = (x / 2) + startPosition.doubleValue();
        return Position.of(position);
    }

    public boolean isDirectedUpwards() {
        return magnitude.doubleValue() >= 0;
    }

    public boolean isDirectedDownwards() {
        return !isDirectedUpwards();
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

    public Length length() {
        return Length.of(endPosition.doubleValue() - startPosition.doubleValue());
    }

    public String unit() {
        return "[kN/m]";
    }
}
