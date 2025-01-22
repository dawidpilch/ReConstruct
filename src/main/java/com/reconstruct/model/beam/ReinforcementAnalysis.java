package com.reconstruct.model.beam;

import com.reconstruct.model.beam.section.RectangularSection;

public class ReinforcementAnalysis {
    private final double corrosionCoverThickness;
    private final double diameterOfReinforcementBar;
    private final double compressionCalculationMPaValueOfReinforcedConcrete;
    private final double yieldStrengthCalculationMPaValueOfReinforcementSteel;

    public ReinforcementAnalysis(double corrosionCoverThickness,
                                 double diameterOfReinforcementBar,
                                 double compressionCalculationMPaValueOfReinforcedConcrete,
                                 double yieldStrengthCalculationMPaValueOfReinforcementSteel) {
        this.corrosionCoverThickness = corrosionCoverThickness;
        this.diameterOfReinforcementBar = diameterOfReinforcementBar;
        this.compressionCalculationMPaValueOfReinforcedConcrete = compressionCalculationMPaValueOfReinforcedConcrete;
        this.yieldStrengthCalculationMPaValueOfReinforcementSteel = yieldStrengthCalculationMPaValueOfReinforcementSteel;
    }

    public void calculate(RectangularSection rectangularSection) {
        double a1 = corrosionCoverThickness + (diameterOfReinforcementBar/2);
        double d = rectangularSection.depth().doubleValue() - a1;

        
    }
}
