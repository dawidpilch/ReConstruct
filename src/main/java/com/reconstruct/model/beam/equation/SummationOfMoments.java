package com.reconstruct.model.beam.equation;

import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.value.Position;

import java.util.List;

public class SummationOfMoments {
    private final List<VerticalPointLoad> verticalPointLoads;
    private final List<BendingMoment> bendingMoments;

    public SummationOfMoments(List<VerticalPointLoad> verticalPointLoads, List<BendingMoment> bendingMoments) {
        this.verticalPointLoads = verticalPointLoads;
        this.bendingMoments = bendingMoments;
    }

    public double valueAtPosition(Position position) {
        double result = 0f;
        for (VerticalPointLoad pointLoad : verticalPointLoads) {
            double distanceFromMomentSummationPosition = Math.abs(position.doubleValue() - pointLoad.position().doubleValue());
            double value = pointLoad.magnitude(position).doubleValue() * distanceFromMomentSummationPosition;
            result += value;
        }

        for (BendingMoment bendingMoment : bendingMoments) {
            result += bendingMoment.magnitude().doubleValue();
        }

        return result;
    }
}