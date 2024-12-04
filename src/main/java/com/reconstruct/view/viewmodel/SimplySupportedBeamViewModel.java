package com.reconstruct.view.viewmodel;

import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import java.util.*;
import java.util.function.Function;

public class SimplySupportedBeamViewModel {
    public final AppendableValue<Double> beamLengthValue;
    public final AppendableValue<Double> pinnedSupportPositionValue;
    public final AppendableValue<Double> rollerSupportPositionValue;
    public final AppendableValue<Collection<VerticalPointLoad>> verticalPointLoadAppendableValues;
    public final AppendableValue<Collection<HorizontalPointLoad>> horizontalPointLoadAppendableValues;
    public final AppendableValue<Collection<BendingMoment>> bendingMomentAppendableValues;

    private final Set<AppendableValue<Double>> singlePositionsForRangeCheck;

    public SimplySupportedBeamViewModel() {
        Set<AppendableValue<Double>> singlePositionsForRangeCheck = new HashSet<>();
        this.beamLengthValue = new AppendableValue<>(10d, "Beam Length") {
            @Override
            public ValueErrors validateNewValue(Double newValue) {
                List<String> errors = new ArrayList<>();
                if (newValue <= 0) {
                    errors.add("The length must be greater than 0");
                } else if (!allPositionableObjectsInRange()) {
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

        this.verticalPointLoadAppendableValues = new AppendableValue<>(Collections.emptyList(),"Vertical point loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<VerticalPointLoad> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(verticalPointLoad -> verticalPointLoad.position().doubleValue()).toList());
            }
        };

        this.horizontalPointLoadAppendableValues = new AppendableValue<>(Collections.emptyList(),"Horizontal point loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<HorizontalPointLoad> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(horizontalPointLoad -> horizontalPointLoad.position().doubleValue()).toList());
            }
        };

        this.bendingMomentAppendableValues = new AppendableValue<>(Collections.emptyList(),"Horizontal point loads") {
            @Override
            protected ValueErrors validateNewValue(Collection<BendingMoment> newValue) {
                return positionValueErrorsFuncForCollection.apply(newValue.stream().map(moment -> moment.position().doubleValue()).toList());
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

        singlePositionsForRangeCheck.add(pinnedSupportPositionValue);
        singlePositionsForRangeCheck.add(rollerSupportPositionValue);
        this.singlePositionsForRangeCheck = singlePositionsForRangeCheck;
    }

    private boolean allPositionableObjectsInRange() {
        for (var property : singlePositionsForRangeCheck) {
            if (property.value() > beamLengthValue.value()) {
                return false;
            }
        } return true;
    }
}
