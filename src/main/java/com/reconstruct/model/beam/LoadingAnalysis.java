package com.reconstruct.model.beam;

public class LoadingAnalysis {
    private final BendingMomentDiagram bendingMomentDiagram;
    private final SheerForceDiagram sheerForceDiagram;

    public LoadingAnalysis(BendingMomentDiagram bendingMomentDiagram, SheerForceDiagram sheerForceDiagram) {
        this.bendingMomentDiagram = bendingMomentDiagram;
        this.sheerForceDiagram = sheerForceDiagram;
    }

    public BendingMomentDiagram bendingMomentDiagram() {
        return bendingMomentDiagram;
    }

    public SheerForceDiagram sheerForceDiagram() {
        return sheerForceDiagram;
    }
}
