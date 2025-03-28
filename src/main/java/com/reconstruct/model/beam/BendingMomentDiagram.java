package com.reconstruct.model.beam;

import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Magnitude;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class BendingMomentDiagram implements Iterable<Map.Entry<Position, Magnitude>> {
    private final Map<Position, Magnitude> positionMagnitudeMap;

    public BendingMomentDiagram(Map<Position, Magnitude> positionMagnitudeMap) {
        this.positionMagnitudeMap = Map.copyOf(positionMagnitudeMap);
    }

    public Stream<Map.Entry<Position, Magnitude>> stream() {
        return positionMagnitudeMap.entrySet().stream();
    }

    @Override
    public Iterator<Map.Entry<Position, Magnitude>> iterator() {
        return positionMagnitudeMap.entrySet().iterator();
    }

    public Magnitude maxMagnitude() {
        return Magnitude.of(positionMagnitudeMap.values().stream().map(Magnitude::positive).map(Magnitude::doubleValue).max(Double::compareTo).orElse(0d));
    }
}
