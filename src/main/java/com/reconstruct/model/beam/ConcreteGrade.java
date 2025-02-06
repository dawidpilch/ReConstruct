package com.reconstruct.model.beam;

public record ConcreteGrade(
        String name,
        double compressionCalculationMPaValueOfReinforcedConcrete,
        double tensileCalculationMPaValueOfReinforcedConcrete,
        double averageTensileStrengthMPaValue
) {
    @Override
    public String toString() {
        return name();
    }
}
