package com.reconstruct.model.value;

public record Magnitude(double value) {
    public Magnitude negated()
    {
        return new Magnitude(-value);
    }

    public Magnitude negative()
    {
        return new Magnitude(-Math.abs(value));
    }

    public Magnitude positive()
    {
        return new Magnitude(Math.abs(value));
    }
}
