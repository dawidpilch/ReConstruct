package com.reconstruct.view.viewmodel;

import java.util.*;
import java.util.function.Consumer;

public class PropertyErrors implements Iterable<String> {
    private final Collection<String> errors;

    public PropertyErrors(Collection<String> errors) {
        this.errors = List.copyOf(errors);
    }

    public static PropertyErrors of(String... errors) {
        return new PropertyErrors(Arrays.asList(errors));
    }

    public static PropertyErrors empty() {
        return new PropertyErrors(Collections.emptyList());
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
