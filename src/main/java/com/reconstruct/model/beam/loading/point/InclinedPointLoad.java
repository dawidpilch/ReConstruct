package com.reconstruct.model.beam.loading.point;

import com.reconstruct.model.value.Degree;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Radian;

import java.util.Objects;

public abstract sealed class InclinedPointLoad extends PointLoad
{
    public static InclinedPointLoad directedUpwards(Position position, Magnitude magnitude, Degree degree) {
        return new InclinedPointLoadDirectedUpwards(position, magnitude, degree);
    }

    public static InclinedPointLoad directedDownwards(Position position, Magnitude magnitude, Degree degree) {
        return new InclinedPointLoadDirectedDownwards(position, magnitude, degree);
    }

    private final Degree degree;

    private InclinedPointLoad(Position position, Magnitude magnitude, Degree degree) {
        super(position, magnitude);
        this.degree = Objects.requireNonNull(degree);
    }

    private Degree componentsCalculationDegree() {
        double degree = this.degree.doubleValue();
        if (degree > 90d)
            degree = 180d - degree;
        return Degree.of(degree);
    }

    protected abstract VerticalPointLoad verticalDirectedComponent(Position position, Magnitude magnitude);

    public VerticalPointLoad verticalComponent() {
        Radian radian = Radian.of(componentsCalculationDegree());
        Magnitude vMagnitude = Magnitude.of(magnitude().doubleValue() * Math.sin(radian.doubleValue()));
        return verticalDirectedComponent(position(), vMagnitude);
    }

    public HorizontalPointLoad horizontalComponent() {
        Radian radian = Radian.of(componentsCalculationDegree());
        Magnitude hMagnitude = Magnitude.of(magnitude().doubleValue() * Math.cos(radian.doubleValue()));
        return degree.doubleValue() <= 90
                ? HorizontalPointLoad.directedRightwards(position(), hMagnitude)
                : HorizontalPointLoad.directedLeftwards(position(), hMagnitude);
    }

    private final static class InclinedPointLoadDirectedDownwards extends InclinedPointLoad {
        public InclinedPointLoadDirectedDownwards(Position position, Magnitude magnitude, Degree degree) {
            super(position, magnitude, degree);
        }

        @Override
        protected VerticalPointLoad verticalDirectedComponent(Position position, Magnitude magnitude) {
            return VerticalPointLoad.directedDownwards(position, magnitude);
        }
    }

    private final static class InclinedPointLoadDirectedUpwards extends InclinedPointLoad {
        public InclinedPointLoadDirectedUpwards(Position position, Magnitude magnitude, Degree degree) {
            super(position, magnitude, degree);
        }

        @Override
        protected VerticalPointLoad verticalDirectedComponent(Position position, Magnitude magnitude) {
            return VerticalPointLoad.directedUpwards(position, magnitude);
        }
    }
}