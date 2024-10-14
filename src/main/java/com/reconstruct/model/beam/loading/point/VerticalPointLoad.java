package com.reconstruct.model.beam.loading.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

public abstract sealed class VerticalPointLoad extends PointLoad {
    private VerticalPointLoad(Position position, Magnitude magnitude) {
        super(position, magnitude);
    }

    public static VerticalPointLoad of(Position position, Magnitude magnitude) {
        if (magnitude.doubleValue() > 0)
            return VerticalPointLoad.directedUpwards(position, magnitude);

        if (magnitude.doubleValue() < 0)
            return VerticalPointLoad.directedDownwards(position, magnitude);

        return VerticalPointLoad.zero(position);
    }

    public static VerticalPointLoad directedUpwards(Position position, Magnitude magnitude) {
        return new VerticalPointLoadDirectedUpwards(position, magnitude);
    }

    public static VerticalPointLoad directedDownwards(Position position, Magnitude magnitude) {
        return new VerticalPointLoadDirectedDownwards(position, magnitude);
    }

    public static VerticalPointLoad zero(Position position) {
        return new ZeroVerticalPointLoad(position);
    }

    public abstract Magnitude magnitude(Position pointOfRotation);

    private static final class VerticalPointLoadDirectedDownwards extends VerticalPointLoad {
        public VerticalPointLoadDirectedDownwards(Position position, Magnitude magnitude) {
            super(position, magnitude.negative());
        }

        public Magnitude magnitude(Position pointOfRotation) {
            if (pointOfRotation == position())
                return Magnitude.zero();
            if (position().doubleValue() < pointOfRotation.doubleValue())
                return this.magnitude().negated();
            return this.magnitude();
        }
    }

    private static final class VerticalPointLoadDirectedUpwards extends VerticalPointLoad {
        public VerticalPointLoadDirectedUpwards(Position position, Magnitude magnitude) {
            super(position, magnitude.positive());
        }

        @Override
        public Magnitude magnitude(Position pointOfRotation) {
            if (pointOfRotation == position())
                return Magnitude.zero();
            if (position().doubleValue() > pointOfRotation.doubleValue())
                return this.magnitude();
            return this.magnitude().negated();
        }
    }

    private final static class ZeroVerticalPointLoad extends VerticalPointLoad {
        public ZeroVerticalPointLoad(Position position) {
            this(position, Magnitude.of(0));
        }

        private ZeroVerticalPointLoad(Position position, Magnitude magnitude) {
            super(position, magnitude);
        }

        @Override
        public Magnitude magnitude(Position pointOfRotation) {
            return this.magnitude();
        }
    }
}