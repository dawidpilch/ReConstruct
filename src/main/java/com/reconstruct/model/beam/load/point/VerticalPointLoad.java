package com.reconstruct.model.beam.load.point;

import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.Position;

public abstract sealed class VerticalPointLoad extends PointLoad
{
    private VerticalPointLoad(Position position, Magnitude negative) {
        super(position, negative);
    }

    public static VerticalPointLoad DirectedUpwards(Position position, Magnitude magnitude)
    {
        return (magnitude.value() == 0)
                ? new NullVerticalPointLoad(position)
                : new VerticalPointLoadDirectedUpwards(position, magnitude);
    }

    public static VerticalPointLoad DirectedDownwards(Position position, Magnitude magnitude)
    {
        return (magnitude.value() == 0)
                ? new NullVerticalPointLoad(position)
                : new VerticalPointLoadDirectedDownwards(position, magnitude);
    }

    public static VerticalPointLoad Null(Position position)
    {
        return new NullVerticalPointLoad(position);
    }

    public abstract Magnitude Magnitude(Position pointOfRotation);

    private static final class VerticalPointLoadDirectedDownwards extends VerticalPointLoad
    {
        public VerticalPointLoadDirectedDownwards(Position position, Magnitude magnitude) {
            super(position, magnitude.negative());
        }

        public Magnitude Magnitude(Position pointOfRotation)
        {
            if (pointOfRotation == Position())
                return new Magnitude(0);
            if (Position().asDouble() < pointOfRotation.asDouble())
                return Magnitude().negated();
            return Magnitude();
        }
    }

    private static final class VerticalPointLoadDirectedUpwards extends VerticalPointLoad {
        public VerticalPointLoadDirectedUpwards(Position position, Magnitude magnitude) {
            super(position, magnitude.positive());
        }

        @Override
        public Magnitude Magnitude(Position pointOfRotation)
        {
            if (pointOfRotation == Position())
                return new Magnitude(0);
            if (Position().asDouble() > pointOfRotation.asDouble())
                return Magnitude();
            return Magnitude().negated();
        }
    }

    private final static class NullVerticalPointLoad extends VerticalPointLoad
    {
        public NullVerticalPointLoad(Position position) {
            this(position, new Magnitude(0));
        }

        private NullVerticalPointLoad(Position position, Magnitude magnitude) {
            super(position, magnitude);
        }

        @Override
        public Magnitude Magnitude(Position pointOfRotation)
        {
            return Magnitude();
        }
    }
}
