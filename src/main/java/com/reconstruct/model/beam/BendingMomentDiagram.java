package com.reconstruct.model.beam;

import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Magnitude;

import java.util.Iterator;
import java.util.Map;

public class BendingMomentDiagram implements Iterable<Map.Entry<Position, Magnitude>> {
    private final Map<Position, Magnitude> positionMagnitudeMap;

    public BendingMomentDiagram(Map<Position, Magnitude> positionMagnitudeMap) {
        this.positionMagnitudeMap = Map.copyOf(positionMagnitudeMap);
    }

    @Override
    public Iterator<Map.Entry<Position, Magnitude>> iterator() {
        return positionMagnitudeMap.entrySet().iterator();
    }
}
