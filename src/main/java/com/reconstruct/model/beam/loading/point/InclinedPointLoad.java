package com.reconstruct.model.beam.loading.point;

import com.reconstruct.model.value.Degree;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Radian;

import java.util.Objects;

public abstract sealed class InclinedPointLoad extends PointLoad
{
    private final Degree degree;

    public static InclinedPointLoad directedUpwards(Position position, Magnitude magnitude, Degree degree) {
        return new InclinedPointLoadDirectedUpwards(position, magnitude, degree);
    }

    public static InclinedPointLoad directedDownwards(Position position, Magnitude magnitude, Degree degree) {
        return new InclinedPointLoadDirectedDownwards(position, magnitude, degree);
    }

    private InclinedPointLoad(Position position, Magnitude magnitude, Degree degree) {
        super(position, magnitude);
        this.degree = Objects.requireNonNull(degree);
    }

    private Degree componentsCalculationDegree() {
        double degree = this.degree.value();
        if (degree > 90d)
            degree = 180d - degree;
        return new Degree(degree);
    }

    protected abstract VerticalPointLoad verticalDirectedComponent(Position position, Magnitude magnitude);

    public VerticalPointLoad verticalComponent() {
        Radian radian = Radian.from(componentsCalculationDegree());
        Magnitude vMagnitude = Magnitude.of(magnitude().doubleValue() * Math.sin(radian.value()));
        return verticalDirectedComponent(position(), vMagnitude);
    }

    public HorizontalPointLoad horizontalComponent() {
        Radian radian = Radian.from(componentsCalculationDegree());
        Magnitude hMagnitude = Magnitude.of(magnitude().doubleValue() * Math.cos(radian.value()));
        return degree.value() <= 90
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