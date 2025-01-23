package com.reconstruct.model.beam;

import com.reconstruct.model.beam.section.RectangularSection;

import java.util.Map;

public class ReinforcementAnalysis {
    private final double corrosionCoverThickness;
    private final double diameterOfReinforcementBar;
    private final ConcreteGrade concreteGrade;
    private final ReinforcementMaterialGrade reinforcementMaterialGrade;

    public ReinforcementAnalysis(double corrosionCoverThickness,
                                 double diameterOfReinforcementBar,
                                 ConcreteGrade concreteGrade,
                                 ReinforcementMaterialGrade reinforcementMaterialGrade) {
        this.corrosionCoverThickness = corrosionCoverThickness;
        this.diameterOfReinforcementBar = diameterOfReinforcementBar;
        this.concreteGrade = concreteGrade;
        this.reinforcementMaterialGrade = reinforcementMaterialGrade;
    }

    public void calculate(RectangularSection rectangularSection, BendingMomentDiagram bendingMomentDiagram) {
        double a1 = corrosionCoverThickness + (diameterOfReinforcementBar/2);
        double d = rectangularSection.depth().doubleValue() - a1;
        double bendingMomentMax = bendingMomentDiagram.stream().map(positionMagnitudeEntry -> positionMagnitudeEntry.getValue().positive().doubleValue()).max(Double::compareTo).orElseThrow();

        double mu = (bendingMomentMax / 1000) / ((rectangularSection.depth().doubleValue() / 1000) * ((d / 1000) * (d / 1000)) * concreteGrade.compressionCalculationMPaValueOfReinforcedConcrete());

        boolean doublyReinforcedRequired = mu > reinforcementMaterialGrade.muFactorOfTheDeformationLimitValue();


    }
}
