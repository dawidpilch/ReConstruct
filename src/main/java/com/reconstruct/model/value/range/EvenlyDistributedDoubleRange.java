package com.reconstruct.model.value.range;

import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;

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
            return List.of();
        }
        if (min >= max) {
            return List.of();
        }

        List<Double> values = new ArrayList<>();
        double step = (max - min) / (numbersInRange - 1);

        for (int i = 0; i < numbersInRange; i++) {
            double value = min + i * step;
            values.add(value);
        }

        // Ensure the middle value is exactly the midpoint of the range
        if (numbersInRange % 2 != 0) {
            int middleIndex = numbersInRange / 2;
            values.set(middleIndex, (min + max) / 2);
        }

        return values;
    }
}
