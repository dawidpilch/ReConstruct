package com.reconstruct.model.beam.load.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.Position;

import java.util.Objects;

public abstract sealed class PointLoad permits HorizontalPointLoad, InclinedPointLoad, VerticalPointLoad
{
    private final Position _position;
    private final Magnitude _magnitude;

    protected PointLoad(Position position, Magnitude magnitude)
    {
        _position = Objects.requireNonNull(position);
        _magnitude = Objects.requireNonNull(magnitude);
    }

    public Position position()
    {
        return _position;
    }

    public Magnitude magnitude()
    {
        return _magnitude;
    }
}