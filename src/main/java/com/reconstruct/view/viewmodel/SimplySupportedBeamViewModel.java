package com.reconstruct.view.viewmodel;

import com.reconstruct.model.beam.loading.distributed.UniformlyDistributedLoad;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import java.util.*;
import java.util.function.Function;

public class SimplySupportedBeamViewModel {
    public final AppendableValue<Double> beamLengthValue;
    public final AppendableValue<Double> pinnedSupportPositionValue;
    public final AppendableValue<Double> rollerSupportPositionValue;
    public final AppendableValue<Collection<VerticalPointLoad>> verticalPointLoadsValue;
    public final AppendableValue<Collection<HorizontalPointLoad>> horizontalPointLoadsValue;
    public final AppendableValue<Collection<BendingMoment>> bendingMomentsValue;
    public final AppendableValue<Collection<UniformlyDistributedLoad>> uniformlyDistributedLoadsValue;

    public SimplySupportedBeamViewModel() {
        this.beamLengthValue = new AppendableValue<>(10d, "Beam Length") {
            @Override
            public ValueErrors validateNewValue(Double newValue) {
                List<String> errors = new ArrayList<>();
                if (newValue <= 0) {
                    errors.add("The length must be greater than 0");
                } else if (!allPositionableObjectsInRange(newValue)) {
                    errors.add("The length cannot be changed because there are supports defined outside of the beam");
                }
                return new ValueErrors(errors);
            }
        };

        Function<Double, ValueErrors> positionValueErrorsFunc = aDouble -> {
            List<String> errors = new ArrayList<>(2);
            if ((aDouble < 0) || (aDouble > beamLengthValue.value())) {
                errors.add("Position must be in range of the beam");
            } return new ValueErrors(errors);
        };

        Function<Collection<Double>, ValueErrors> positionValueErrorsFuncForCollection = doubles -> {
            for (var value : doubles) {
                ValueErrors errors = positionValueErrorsFunc.apply(value);
                if(!errors.isEmpty()) {
                    return errors;
                }
            } return ValueErrors.empty();
        };

        this.verticalPointLoadsValue = new AppendableValue<>(Collections.emptyList(),"Vertical point loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<VerticalPointLoad> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(verticalPointLoad -> verticalPointLoad.position().doubleValue()).toList());
            }
        };

        this.horizontalPointLoadsValue = new AppendableValue<>(Collections.emptyList(),"Horizontal point loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<HorizontalPointLoad> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(horizontalPointLoad -> horizontalPointLoad.position().doubleValue()).toList());
            }
        };

        this.bendingMomentsValue = new AppendableValue<>(Collections.emptyList(),"Horizontal point loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<BendingMoment> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(moment -> moment.position().doubleValue()).toList());
            }
        };

        this.uniformlyDistributedLoadsValue = new AppendableValue<>(Collections.emptyList(), "Uniformly distributed loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<UniformlyDistributedLoad> newValue) {
                for (var udl : newValue) {
                    ValueErrors startPosErrors = positionValueErrorsFunc.apply(udl.startPosition().doubleValue());
                    ValueErrors endPosErrors = positionValueErrorsFunc.apply(udl.endPosition().doubleValue());
                    if (!startPosErrors.isEmpty()) {
                        return startPosErrors;
                    }
                    if (!endPosErrors.isEmpty()) {
                        return endPosErrors;
                    }
                } return ValueErrors.empty();
            }
        };

        this.pinnedSupportPositionValue = new AppendableValue<>(0d, "Pinned support position") {
            @Override
            public ValueErrors validateNewValue(Double newValue) {
                return positionValueErrorsFunc.apply(newValue);
            }
        };

        this.rollerSupportPositionValue = new AppendableValue<>(10d, "Roller support position") {
            @Override
            public ValueErrors validateNewValue(Double newValue) {
                return positionValueErrorsFunc.apply(newValue);
            }
        };
    }

    private boolean allPositionableObjectsInRange(double range) {
        final Set<Double> positionsForRangeCheck = new HashSet<>();
        positionsForRangeCheck.add(pinnedSupportPositionValue.value());
        positionsForRangeCheck.add(rollerSupportPositionValue.value());
        verticalPointLoadsValue.value().forEach(pointLoad -> positionsForRangeCheck.add(pointLoad.position().doubleValue()));
        bendingMomentsValue.value().forEach(bendingMoment -> positionsForRangeCheck.add(bendingMoment.position().doubleValue()));
        uniformlyDistributedLoadsValue.value().forEach(uniformlyDistributedLoad -> {
            positionsForRangeCheck.add(uniformlyDistributedLoad.startPosition().doubleValue());
            positionsForRangeCheck.add(uniformlyDistributedLoad.endPosition().doubleValue());
        });

        for (var position : positionsForRangeCheck) {
            if (position > range) {
                return false;
            }
        } return true;
    }
}
