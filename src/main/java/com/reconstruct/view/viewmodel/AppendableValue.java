package com.reconstruct.view.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AppendableValue<T> {
    private T value;
    private final String name;
    private final List<ValueAppendedListener<T>> onValueAppendedListeners = new ArrayList<>();
    private final List<ValueNotAppendedListener<T>> onValueNotAppendedListeners = new ArrayList<>();

    public AppendableValue(T defaultValue) {
        this(defaultValue, "");
    }

    public AppendableValue(T defaultValue, String name) {
        this.value = Objects.requireNonNull(defaultValue);
        this.name = Objects.requireNonNull(name);
    }

    public void addOnValueAppendedListener(ValueAppendedListener<T> listener) {
        onValueAppendedListeners.add(listener);
    }

    public void removeOnValueChangedListener(ValueAppendedListener<T> listener) {
        onValueAppendedListeners.remove(listener);
    }

    public void addOnValueNotAppendedListener(ValueNotAppendedListener<T> listener) {
        onValueNotAppendedListeners.add(listener);
    }

    public void removeOnValueNotChangedListener(ValueNotAppendedListener<T> listener) {
        onValueNotAppendedListeners.remove(listener);
    }

    protected abstract ValueErrors validateNewValue(T newValue);

    public final ValueErrors tryAppend(T newValue) {
        ValueErrors errors = validateNewValue(newValue);
        T oldValue = this.value;
        if (errors.isEmpty()) {
            this.value = newValue;
            for (var listener : onValueAppendedListeners) {
                listener.onValueAppended(oldValue, newValue, errors);
            }
        } else {
            for (var listener : onValueNotAppendedListeners) {
                listener.onValueNotAppended(oldValue, newValue, errors);
            }
        }
        return errors;
    }

    public final String name() {
        return name;
    }

    public final T value() {
        return value;
    }
}
