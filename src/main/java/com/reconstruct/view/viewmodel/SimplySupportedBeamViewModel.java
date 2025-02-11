package com.reconstruct.view.viewmodel;

import com.reconstruct.model.beam.loading.distributed.UniformlyDistributedLoad;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import java.util.*;
import java.util.function.Function;

public class SimplySupportedBeamViewModel {
    public final AppendableProperty<Double> beamLengthProperty;
    public final AppendableProperty<Double> pinnedSupportPositionProperty;
    public final AppendableProperty<Double> rollerSupportPositionProperty;
    public final AppendableProperty<Collection<VerticalPointLoad>> verticalPointLoadsProperty;
    public final AppendableProperty<Collection<HorizontalPointLoad>> horizontalPointLoadsProperty;
    public final AppendableProperty<Collection<BendingMoment>> bendingMomentsProperty;
    public final AppendableProperty<Collection<UniformlyDistributedLoad>> uniformlyDistributedLoadsProperty;

    public SimplySupportedBeamViewModel() {
        this.beamLengthProperty = new AppendableProperty<>(10d, "Beam Length [m]") {
            @Override
            public PropertyErrors validateNewValue(Double newValue) {
                List<String> errors = new ArrayList<>();
                if (newValue <= 0) {
                    errors.add("The length must be greater than 0");
                } else if (!allPositionableObjectsInRange(newValue)) {
                    errors.add("The length cannot be changed because there are supports defined outside of the beam");
                }
                return new PropertyErrors(errors);
            }
        };

        Function<Double, PropertyErrors> positionValueErrorsFunc = aDouble -> {
            List<String> errors = new ArrayList<>(2);
            if ((aDouble < 0) || (aDouble > beamLengthProperty.value())) {
                errors.add("Position must be in range of the beam");
            } return new PropertyErrors(errors);
        };

        Function<Collection<Double>, PropertyErrors> positionValueErrorsFuncForCollection = doubles -> {
            for (var value : doubles) {
                PropertyErrors errors = positionValueErrorsFunc.apply(value);
                if(!errors.isEmpty()) {
                    return errors;
                }
            } return PropertyErrors.empty();
        };

        this.verticalPointLoadsProperty = new AppendableProperty<>(Collections.emptyList(),"Vertical point loads") {
            @Override
            protected PropertyErrors validateNewValue(Collection<VerticalPointLoad> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(verticalPointLoad -> verticalPointLoad.position().doubleValue()).toList());
            }
        };

        this.horizontalPointLoadsProperty = new AppendableProperty<>(Collections.emptyList(),"Horizontal point loads") {
            @Override
            protected PropertyErrors validateNewValue(Collection<HorizontalPointLoad> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(horizontalPointLoad -> horizontalPointLoad.position().doubleValue()).toList());
            }
        };

        this.bendingMomentsProperty = new AppendableProperty<>(Collections.emptyList(),"Horizontal point loads") {
            @Override
            protected PropertyErrors validateNewValue(Collection<BendingMoment> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(moment -> moment.position().doubleValue()).toList());
            }
        };

        this.uniformlyDistributedLoadsProperty = new AppendableProperty<>(Collections.emptyList(), "Uniformly distributed loads") {
            @Override
            protected PropertyErrors validateNewValue(Collection<UniformlyDistributedLoad> newValue) {
                for (var udl : newValue) {
                    PropertyErrors startPosErrors = positionValueErrorsFunc.apply(udl.startPosition().doubleValue());
                    PropertyErrors endPosErrors = positionValueErrorsFunc.apply(udl.endPosition().doubleValue());
                    if (!startPosErrors.isEmpty()) {
                        return startPosErrors;
                    }
                    if (!endPosErrors.isEmpty()) {
                        return endPosErrors;
                    }
                } return PropertyErrors.empty();
            }
        };

        this.pinnedSupportPositionProperty = new AppendableProperty<>(0d, "Pinned support position [m]") {
            @Override
            public PropertyErrors validateNewValue(Double newValue) {
                return positionValueErrorsFunc.apply(newValue);
            }
        };

        this.rollerSupportPositionProperty = new AppendableProperty<>(10d, "Roller support position [m]") {
            @Override
            public PropertyErrors validateNewValue(Double newValue) {
                return positionValueErrorsFunc.apply(newValue);
            }
        };
    }

    private boolean allPositionableObjectsInRange(double range) {
        final Set<Double> positionsForRangeCheck = new HashSet<>();
        positionsForRangeCheck.add(pinnedSupportPositionProperty.value());
        positionsForRangeCheck.add(rollerSupportPositionProperty.value());
        verticalPointLoadsProperty.value().forEach(pointLoad -> positionsForRangeCheck.add(pointLoad.position().doubleValue()));
        bendingMomentsProperty.value().forEach(bendingMoment -> positionsForRangeCheck.add(bendingMoment.position().doubleValue()));
        uniformlyDistributedLoadsProperty.value().forEach(uniformlyDistributedLoad -> {
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
