package com.reconstruct.model.beam.load.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.Position;

import java.util.Objects;

public abstract sealed class PointLoad permits HorizontalPointLoad, InclinedPointLoad, VerticalPointLoad
{
    private final Position position;
    private final Magnitude magnitude;

    protected PointLoad(Position position, Magnitude magnitude)
    {
        this.position = Objects.requireNonNull(position);
        this.magnitude = Objects.requireNonNull(magnitude);
    }

    public Position position()
    {
        return position;
    }

    public Magnitude magnitude()
    {
        return magnitude;
    }
}