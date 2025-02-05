package com.reconstruct.model.beam;

import com.reconstruct.model.beam.section.RectangularSection;
import com.reconstruct.model.standard.EN1992Eurocode2;
import com.reconstruct.model.value.Magnitude;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class BeamReinforcementAnalysis {
    private final double corrosionCoverThickness;
    private final double diameterOfReinforcementBar;
    private final double diameterOfMainReinforcementStirrup;
    private final ConcreteGrade concreteGrade;
    private final ReinforcementMaterialGrade reinforcementMaterialGrade;

    public BeamReinforcementAnalysis(double corrosionCoverThickness,
                                     double diameterOfReinforcementBar,
                                     ConcreteGrade concreteGrade,
                                     ReinforcementMaterialGrade reinforcementMaterialGrade) {
        this(corrosionCoverThickness, diameterOfReinforcementBar, 0, concreteGrade, reinforcementMaterialGrade);
    }

    public BeamReinforcementAnalysis(double corrosionCoverThickness,
                                     double diameterOfReinforcementBar,
                                     double diameterOfMainReinforcementStirrup,
                                     ConcreteGrade concreteGrade,
                                     ReinforcementMaterialGrade reinforcementMaterialGrade) {
        this.corrosionCoverThickness = corrosionCoverThickness;
        this.diameterOfReinforcementBar = diameterOfReinforcementBar;
        this.diameterOfMainReinforcementStirrup = diameterOfMainReinforcementStirrup;
        this.concreteGrade = concreteGrade;
        this.reinforcementMaterialGrade = reinforcementMaterialGrade;
    }

    public double verticalCorrosionCoverThickness() {
        return corrosionCoverThickness + (diameterOfReinforcementBar / 2) + diameterOfMainReinforcementStirrup;
    }

    public Results reinforcement(RectangularSection rectangularSection, Magnitude bendingMomentMagnitude) {
        double a1_mm = verticalCorrosionCoverThickness();
        double d_m = (rectangularSection.depth().doubleValue() - a1_mm) / 1000;
        double bendingMomentMax = bendingMomentMagnitude.doubleValue();
        if (bendingMomentMax == 0) {
            return new Results(Map.of(), 0d);
        }

        double mu_mm = (bendingMomentMax / 1000) / ((rectangularSection.width().doubleValue() / 1000) * (d_m * d_m) * concreteGrade.compressionCalculationMPaValueOfReinforcedConcrete());
        double MRd_lim = 0;
        boolean doublyReinforcedRequired = mu_mm > reinforcementMaterialGrade.muFactorOfTheDeformationLimitValue();
        Map<ReinforcementType, Collection<BeamReinforcement>> results = new HashMap<>();
        if (!doublyReinforcedRequired) {
            double areaOfRequiredTensileReinforcement_cm2 = (reinforcementMaterialGrade.omegaFactorOfTheDeformationLimitValue() * d_m * (rectangularSection.width().doubleValue() / 1000) * (concreteGrade.compressionCalculationMPaValueOfReinforcedConcrete() / reinforcementMaterialGrade.yieldStrengthCalculationMPaValue())) * 10000;
            var numberOfBarsToProvidedAreaOfReinforcementSection = beamReinforcementMatches(areaOfRequiredTensileReinforcement_cm2, 3);
            results.put(ReinforcementType.BOTTOM, numberOfBarsToProvidedAreaOfReinforcementSection);
        } else {
            MRd_lim = reinforcementMaterialGrade.muFactorOfTheDeformationLimitValue() * (d_m * d_m) * (rectangularSection.width().doubleValue() / 1000) * concreteGrade.compressionCalculationMPaValueOfReinforcedConcrete();
            double areaOfRequiredTensileReinforcement_cm2 = 17d/21d * (reinforcementMaterialGrade.xiFactorOfTheDeformationLimitValue() * rectangularSection.width().doubleValue()/1000d * d_m * concreteGrade.compressionCalculationMPaValueOfReinforcedConcrete() / reinforcementMaterialGrade.yieldStrengthCalculationMPaValue());
            areaOfRequiredTensileReinforcement_cm2 = areaOfRequiredTensileReinforcement_cm2 * 10000;

            double deltaMSd = (bendingMomentMax / 1000) - MRd_lim;
            double areaOfRequiredCompressiveReinforcement_cm2 = (deltaMSd / (reinforcementMaterialGrade.yieldStrengthCalculationMPaValue() * (d_m - (a1_mm / 1000)))) * 10000;
            areaOfRequiredTensileReinforcement_cm2 += areaOfRequiredCompressiveReinforcement_cm2;

            results.put(ReinforcementType.BOTTOM, beamReinforcementMatches(areaOfRequiredTensileReinforcement_cm2, 3));
            results.put(ReinforcementType.TOP, beamReinforcementMatches(areaOfRequiredCompressiveReinforcement_cm2, 3));
        }

        return new Results(results, MRd_lim);
    }

    private List<BeamReinforcement> beamReinforcementMatches(double requiredAreaOfReinforcementSection, int matches) {
        Map<Double, Pair<Integer, Double>> barDiameterToPairOfBarsCountToSectionArea = new LinkedHashMap<>();
        for (var diameterOfReinforcementBar : EN1992Eurocode2.CROSS_SECTION_AREAS.entrySet()) {
            Optional<Double> optionalMin = Arrays.stream(diameterOfReinforcementBar.getValue())
                    .filter(aDouble -> aDouble > requiredAreaOfReinforcementSection)
                    .min(Double::compareTo);
            if (optionalMin.isPresent()) {
                double min = optionalMin.get();
                int barsCount = 0;

                for (int i = 0; i < diameterOfReinforcementBar.getValue().length; i++) {
                    if (diameterOfReinforcementBar.getValue()[i] == min) {
                        barsCount = i + 1;
                        break;
                    }
                }

                barDiameterToPairOfBarsCountToSectionArea.put(diameterOfReinforcementBar.getKey(), new Pair<>(barsCount, min));
            }
        }

        List<BeamReinforcement> results = new LinkedList<>();
        barDiameterToPairOfBarsCountToSectionArea.entrySet().stream()
                .sorted((o1, o2) -> {
                    if (o1.getValue().getValue() > o2.getValue().getValue()) {
                        return 1;
                    } else if (o1.getValue().getValue() < o2.getValue().getValue()) {
                        return -1;
                    }
                    return 0;
                }).limit(matches).forEachOrdered(match -> results.add(new BeamReinforcement(
                        match.getValue().getKey(),
                        match.getKey(),
                        match.getValue().getValue(),
                        requiredAreaOfReinforcementSection
                )));

        return results;
    }

    public record Results(Map<ReinforcementType, Collection<BeamReinforcement>> beamReinforcement, double maxSectionCapacityMPa) { }

    public static class BeamReinforcement {
        private final int numberOfBars;
        private final double diameterOfReinforcementBar;
        private final double providedAreaOfReinforcementSection;
        private final double requiredAreaOfReinforcementSection;

        public static BeamReinforcement empty() {
            return new BeamReinforcement(0, 0, 0, 0);
        }

        private BeamReinforcement(int numberOfBars, double diameterOfReinforcementBar, double providedAreaOfReinforcementSection,double requiredAreaOfReinforcementSection ) {
            this.numberOfBars = numberOfBars;
            this.diameterOfReinforcementBar = diameterOfReinforcementBar;
            this.providedAreaOfReinforcementSection = providedAreaOfReinforcementSection;
            this.requiredAreaOfReinforcementSection = requiredAreaOfReinforcementSection;
        }

        public int numberOfBars() {
            return numberOfBars;
        }

        public double diameterOfReinforcementBar() {
            return diameterOfReinforcementBar;
        }

        public double providedAreaOfReinforcementSection() {
            return providedAreaOfReinforcementSection;
        }

        public double requiredAreaOfReinforcementSection() {
            return requiredAreaOfReinforcementSection;
        }
    }

    public enum ReinforcementType {
        BOTTOM("As1") {
            @Override
            public String toString() {
                return "Bottom reinforcement";
            }
        },
        TOP("As2") {
            @Override
            public String toString() {
                return "Top reinforcement";
            }
        };

        public final String areaOfReinforcementSectionSymbol;

        ReinforcementType(String areaOfReinforcementSectionSymbol) {
            this.areaOfReinforcementSectionSymbol = areaOfReinforcementSectionSymbol;
        }
    }
}
