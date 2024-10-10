package com.reconstruct.model.value;

public record Radian(double value)
{
    public Radian {
        if (value < 0 || value > (2 * Math.PI))
            throw new IllegalArgumentException();
    }

    public static Radian from(Degree degree)
    {
        return new Radian(degree.value() * (Math.PI / 180));
    }
}