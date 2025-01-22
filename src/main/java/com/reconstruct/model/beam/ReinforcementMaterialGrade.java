package com.reconstruct.model.beam;

public record ReinforcementMaterialGrade(
        String name,
        double yieldStrengthCalculationMPaValue,
        double omegaFactorOfTheDeformationLimitValue,
        double zetaFactorOfTheDeformationLimitValue,
        double muFactorOfTheDeformationLimitValue
) { }