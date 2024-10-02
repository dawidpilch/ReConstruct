package com.reconstruct.model.beam.section;

import com.reconstruct.model.value.PositiveNumber;

public sealed interface Section permits Rectangular {
    PositiveNumber area();
    String name();
}