package com.reconstruct.model.beam.equation;

import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.value.Position;

import java.util.ArrayList;
import java.util.List;

public class SummationOfMoments {
    private final Loading loading;

    public SummationOfMoments(Loading loading) {
        this.loading = loading;
    }

    public double valueAtPosition(Position position) {
        double result = 0f;

        List<VerticalPointLoad> verticalPointLoads = new ArrayList<>(loading.verticalPointLoads());
        for (var udl : loading.uniformlyDistributedLoads()) {
            verticalPointLoads.add(udl.resultantForce());
        }

        for (VerticalPointLoad pointLoad : verticalPointLoads) {
            double distanceFromMomentSummationPosition = Math.abs(position.doubleValue() - pointLoad.position().doubleValue());
            double value = pointLoad.magnitude(position).doubleValue() * distanceFromMomentSummationPosition;
            result += value;
        }

        for (BendingMoment bendingMoment : loading.bendingMoments()) {
            result += bendingMoment.magnitude().doubleValue();
        }

        return result;
    }
}