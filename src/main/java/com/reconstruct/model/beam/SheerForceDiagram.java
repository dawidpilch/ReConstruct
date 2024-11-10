package com.reconstruct.model.beam;

import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Magnitude;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class SheerForceDiagram implements Iterable<Map.Entry<Position, Magnitude>> {
    private final Map<Position, Magnitude> positionMagnitudeMap;

    public SheerForceDiagram(Map<Position, Magnitude> positionMagnitudeMap) {
        this.positionMagnitudeMap = Map.copyOf(positionMagnitudeMap);
    }

    public Stream<Map.Entry<Position, Magnitude>> stream() {
        return positionMagnitudeMap.entrySet().stream();
    }

    @Override
    public Iterator<Map.Entry<Position, Magnitude>> iterator() {
        return positionMagnitudeMap.entrySet().iterator();
    }
}
