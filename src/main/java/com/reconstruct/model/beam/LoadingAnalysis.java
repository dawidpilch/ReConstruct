package com.reconstruct.model.beam;

import com.reconstruct.model.beam.loading.point.VerticalPointLoad;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LoadingAnalysis {
    private final Collection<VerticalPointLoad> verticalSupportReactions;
    private final BendingMomentDiagram bendingMomentDiagram;
    private final SheerForceDiagram sheerForceDiagram;


    public LoadingAnalysis(Collection<VerticalPointLoad> verticalSupportReactions, BendingMomentDiagram bendingMomentDiagram, SheerForceDiagram sheerForceDiagram) {
        this.verticalSupportReactions = verticalSupportReactions;
        this.bendingMomentDiagram = bendingMomentDiagram;
        this.sheerForceDiagram = sheerForceDiagram;
    }

    public static LoadingAnalysis empty() {
        return new LoadingAnalysis(List.of(), new BendingMomentDiagram(Map.of()), new SheerForceDiagram(Map.of()));
    }

    public BendingMomentDiagram bendingMomentDiagram() {
        return bendingMomentDiagram;
    }

    public SheerForceDiagram sheerForceDiagram() {
        return sheerForceDiagram;
    }

    public Collection<VerticalPointLoad> verticalSupportReactions() {
        return verticalSupportReactions;
    }
}
