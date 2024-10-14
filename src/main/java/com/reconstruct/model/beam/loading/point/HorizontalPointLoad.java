package com.reconstruct.model.beam.loading.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

public final class HorizontalPointLoad extends PointLoad {

    public static HorizontalPointLoad of(Position position, Magnitude magnitude) {
        if (magnitude.doubleValue() > 0)
            return HorizontalPointLoad.directedRightwards(position, magnitude);

        if (magnitude.doubleValue() < 0)
            return HorizontalPointLoad.directedLeftwards(position, magnitude);

        return HorizontalPointLoad.zero(position);
    }

    public static HorizontalPointLoad directedRightwards(Position position, Magnitude magnitude) {
        return new HorizontalPointLoad(position, magnitude.positive());
    }

    public static HorizontalPointLoad directedLeftwards(Position position, Magnitude magnitude) {
        return new HorizontalPointLoad(position, magnitude.negative());
    }

    public static HorizontalPointLoad zero(Position position) {
        return new HorizontalPointLoad(position, Magnitude.zero());
    }

    private HorizontalPointLoad(Position position, Magnitude magnitude) {
        super(position, magnitude);
    }
}