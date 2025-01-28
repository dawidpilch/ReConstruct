package com.reconstruct.model.beam;

public record ReinforcementMaterialGrade(
        String name,
        double yieldStrengthCalculationMPaValue,
        double xiFactorOfTheDeformationLimitValue,
        double omegaFactorOfTheDeformationLimitValue,
        double zetaFactorOfTheDeformationLimitValue,
        double muFactorOfTheDeformationLimitValue
) {
    @Override
    public String toString() {
        return name();
    }
}