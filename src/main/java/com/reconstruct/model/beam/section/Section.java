package com.reconstruct.model.beam.section;

import com.reconstruct.model.value.PositiveDouble;

public sealed interface Section permits Rectangular {
    PositiveDouble area();
}