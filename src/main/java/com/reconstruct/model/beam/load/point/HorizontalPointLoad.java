package com.reconstruct.model.beam.load.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.Position;

public final class HorizontalPointLoad extends PointLoad {
    public static HorizontalPointLoad directedRightwards(Position position, Magnitude magnitude)
    {
        return new HorizontalPointLoad(position, magnitude.positive());
    }

    public static HorizontalPointLoad directedLeftwards(Position position, Magnitude magnitude)
    {
        return new HorizontalPointLoad(position, magnitude.negative());
    }

    public static HorizontalPointLoad zero(Position position)
    {
        return new HorizontalPointLoad(position, Magnitude.zero());
    }

    private HorizontalPointLoad(Position position, Magnitude magnitude) {
        super(position, magnitude);
    }
}