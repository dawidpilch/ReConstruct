package com.reconstruct.model.value;

import java.util.Objects;

public record Length(PositiveDouble value) {
    public Length {
        Objects.requireNonNull(value);
    }
}
