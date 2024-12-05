package com.reconstruct.view.viewmodel;

@FunctionalInterface
public interface ValueNotAppendedListener<T> {
    void onValueNotAppended(T oldValue, T newValue, ValueErrors valueErrors);
}
