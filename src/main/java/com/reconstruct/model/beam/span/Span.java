package com.reconstruct.model.beam.span;

import com.reconstruct.model.beam.section.Section;
import com.reconstruct.model.value.Length;

import java.util.Objects;

public record Span(Length length, Section section) {
    public Span {
        Objects.requireNonNull(length);
        Objects.requireNonNull(section);
    }
}