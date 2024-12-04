package com.reconstruct.view.viewmodel;

import java.util.*;
import java.util.function.Consumer;

public class ValueErrors implements Iterable<String> {
    private final Collection<String> errors;

    public ValueErrors(Collection<String> errors) {
        this.errors = List.copyOf(errors);
    }

    public static ValueErrors empty() {
        return new ValueErrors(Collections.emptyList());
    }

    @Override
    public Iterator<String> iterator() {
        return errors.iterator();
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        errors.forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return errors.spliterator();
    }

    public int size() {
        return errors.size();
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }
}
