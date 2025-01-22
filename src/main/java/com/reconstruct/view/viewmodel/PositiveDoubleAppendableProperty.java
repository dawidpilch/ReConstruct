package com.reconstruct.view.viewmodel;

public final class PositiveDoubleAppendableProperty extends AppendableProperty<Double> {
    public PositiveDoubleAppendableProperty(Double defaultValue) {
        super(defaultValue);
    }

    public PositiveDoubleAppendableProperty(Double defaultValue, String name) {
        super(defaultValue, name);
    }

    @Override
    protected PropertyErrors validateNewValue(Double newValue) {
        return newValue > 0 ? PropertyErrors.empty() : PropertyErrors.of("Value must be greater than zero.");
    }
}
