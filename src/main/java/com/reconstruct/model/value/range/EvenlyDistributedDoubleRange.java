package com.reconstruct.model.value.range;

import org.apache.commons.math3.util.Precision;

import java.util.List;
import java.util.stream.DoubleStream;

public class EvenlyDistributedDoubleRange implements Range<List<Double>> {
    private final double min;
    private final double max;
    private final int numbersInRange;

    public EvenlyDistributedDoubleRange(double min, double max, int numbersInRange) {
        this.min = min;
        this.max = max;
        this.numbersInRange = numbersInRange;
    }

    @Override
    public List<Double> values() {
        if (numbersInRange <= 0) {
            throw new IllegalArgumentException("Count must be positive.");
        }
        if (min >= max) {
            throw new IllegalArgumentException("Minimum bound must be less than maximum bound.");
        }

        double step = Precision.round((max - min) / (numbersInRange - 1), 6);
        return DoubleStream.iterate(min, d -> d + step)
                .limit(numbersInRange)
                .boxed()
                .toList();
    }
}
