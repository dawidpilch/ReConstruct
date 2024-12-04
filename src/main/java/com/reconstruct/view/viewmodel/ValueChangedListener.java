package com.reconstruct.view.viewmodel;

@FunctionalInterface
public interface ValueChangedListener<T> {
    void onValueChanged(T oldValue, T newValue, ValueErrors valueErrors);
}
