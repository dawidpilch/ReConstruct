package com.reconstruct.view.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AppendableValue<T> {
    private T value;
    private final String name;
    private final List<ValueChangedListener<T>> onValueChangeListeners = new ArrayList<>();

    public AppendableValue(T defaultValue) {
        this(defaultValue, "");
    }

    public AppendableValue(T defaultValue, String name) {
        this.value = Objects.requireNonNull(defaultValue);
        this.name = Objects.requireNonNull(name);
    }

    public void addOnValueChangedListener(ValueChangedListener<T> changeListener) {
        onValueChangeListeners.add(changeListener);
    }

    protected abstract ValueErrors validateNewValue(T newValue);

    public final ValueErrors tryAppend(T newValue) {
        ValueErrors errors = validateNewValue(newValue);
        if (errors.isEmpty()) {
            T oldValue = this.value;
            this.value = newValue;
            for (var listener : onValueChangeListeners) {
                listener.onValueChanged(oldValue, newValue, errors);
            }
        } return errors;
    }

    public final String name() {
        return name;
    }

    public final T value() {
        return value;
    }
}
