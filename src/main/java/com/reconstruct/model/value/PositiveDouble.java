package com.reconstruct.model.value;

import org.apache.commons.math3.util.Precision;

public record PositiveDouble(double value) {
    public PositiveDouble {
        value = Precision.round(value, 3);
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
    }
}