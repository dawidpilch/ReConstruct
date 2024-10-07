package com.reconstruct.model.beam.load.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.Position;

public sealed class PointLoad permits VerticalPointLoad
{
    private final Position _position;
    private final Magnitude _magnitude;

    protected PointLoad(Position position, Magnitude magnitude)
    {
        _position = position;
        _magnitude = magnitude;
    }

    public Position Position()
    {
        return _position;
    }

    public Magnitude Magnitude()
    {
        return _magnitude;
    }
}