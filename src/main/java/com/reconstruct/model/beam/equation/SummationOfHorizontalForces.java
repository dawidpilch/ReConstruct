package com.reconstruct.model.beam.equation;

import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.value.Magnitude;

import java.util.List;

public class SummationOfHorizontalForces {
    private final List<HorizontalPointLoad> horizontalPointLoads;

    public SummationOfHorizontalForces(List<HorizontalPointLoad> horizontalPointLoads) {
        this.horizontalPointLoads = horizontalPointLoads;
    }

    public Magnitude value() {
        return Magnitude.of(horizontalPointLoads.stream()
                .mapToDouble(value -> value.magnitude().doubleValue())
                .sum()
        );
    }
}