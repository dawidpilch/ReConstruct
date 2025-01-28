package com.reconstruct.model.beam;

public record ConcreteGrade(
        String name,
        double compressionCalculationMPaValueOfReinforcedConcrete
) {
    @Override
    public String toString() {
        return name();
    }
}
