package com.reconstruct.model.beam;

import com.reconstruct.model.beam.section.RectangularSection;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.standard.EN1992Eurocode2;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BeamReinforcementAnalysisTest {
    @Test
    public void reinforcementTest() {
        BeamReinforcementAnalysis beamReinforcementAnalysis = new BeamReinforcementAnalysis(
                30,
                25,
                8,
                EN1992Eurocode2.CONCRETE_GRADE.get(1),
                EN1992Eurocode2.STEEL_GRADES.get(3)
        );

        var reinforcement = beamReinforcementAnalysis.reinforcement(
                new RectangularSection(PositiveDouble.of(250), PositiveDouble.of(450)),
                new BendingMomentDiagram(Map.of(Position.of(5), Magnitude.of(200))).maxMagnitude()
        );
    }
}