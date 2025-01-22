package com.reconstruct.view.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AppendableProperty<T> {
    private T value;
    private final String name;
    private final List<OnTryAppendValueListener<T>> onTryAppendedValueListeners = new ArrayList<>();

    public AppendableProperty(T defaultValue) {
        this(defaultValue, "");
    }

    public AppendableProperty(T defaultValue, String name) {
        this.value = Objects.requireNonNull(defaultValue);
        this.name = Objects.requireNonNull(name);
    }

    public void addOnTryAppendValueListener(OnTryAppendValueListener<T> listener) {
        onTryAppendedValueListeners.add(listener);
    }

    public void removeOnTryAppendValueListener(OnTryAppendValueListener<T> listener) {
        onTryAppendedValueListeners.remove(listener);
    }

    protected abstract PropertyErrors validateNewValue(T newValue);

    public final PropertyErrors tryAppend(T newValue) {
        PropertyErrors errors = validateNewValue(newValue);
        T oldValue = this.value;
        if (errors.isEmpty()) {
            this.value = newValue;
        }
        for (var listener : onTryAppendedValueListeners) {
            listener.onTryAppendValue(oldValue, newValue, errors);
        }
        return errors;
    }

    public final String name() {
        return name;
    }

    public final T value() {
        return value;
    }

    @FunctionalInterface
    public interface OnTryAppendValueListener<T> {
        void onTryAppendValue(T oldValue, T newValue, PropertyErrors propertyErrors);
    }
}
