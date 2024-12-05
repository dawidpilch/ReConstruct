package com.reconstruct.view.viewmodel;

@FunctionalInterface
public interface ValueAppendedListener<T> {
    void onValueAppended(T oldValue, T newValue, ValueErrors valueErrors);
}
