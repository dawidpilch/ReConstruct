package com.reconstruct.model.value;

import org.apache.commons.math3.util.Precision;

public record PositiveNumber(double value) {
    public PositiveNumber {
        value = Precision.round(value, 3);
        if (value < 0)
            throw new IllegalArgumentException("The number must be >= 0");
    }
}