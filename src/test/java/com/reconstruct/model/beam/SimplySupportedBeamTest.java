package com.reconstruct.model.beam;

import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.Rectangular;
import com.reconstruct.model.beam.section.Section;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimplySupportedBeamTest {
    private static final double TOLERANCE = 0.001;

    @Test
    public void test() {
        Length length = Length.of(10);
        Section section = new Rectangular(PositiveDouble.of(5), PositiveDouble.of(5));

        Loading loading = new Loading(
                List.of(VerticalPointLoad.directedDownwards(Position.of(6), Magnitude.of(5))),
                List.of(),
                List.of()
        );

        Position rollerPosition = Position.of(1.5);
        Position pinnedPosition = Position.of(6.5);

        SimplySupportedBeam beam = SimplySupportedBeam.custom(length, section, pinnedPosition, rollerPosition);
        var verticalReactions = beam.supportVerticalReactions(loading);
        var horizontalReactions = beam.supportHorizontalReactions(loading);
        var bendingMomentReactions = beam.supportBendingMomentReactions(loading);

        // vertical
        var verticalRoller = verticalReactions.getOrDefault(rollerPosition, List.of());
        var b0 = verticalRoller.stream().anyMatch(load -> Math.abs(load.magnitude().doubleValue() - 0.5) <= TOLERANCE);
        Assertions.assertTrue(verticalRoller.size() == 1 && b0);

        var verticalPinned = verticalReactions.getOrDefault(pinnedPosition, List.of());
        var b1 = verticalPinned.stream().anyMatch(load -> Math.abs(load.magnitude().doubleValue() - 4.5) <= TOLERANCE);
        Assertions.assertTrue(verticalPinned.size() == 1 && b1 );


        // horizontal
        var horizontalRoller = horizontalReactions.getOrDefault(rollerPosition, List.of());
        Assertions.assertTrue(horizontalRoller.size() == 0);

        var horizontalPinned = horizontalReactions.getOrDefault(pinnedPosition, List.of());
        var b2 = horizontalPinned.stream().anyMatch(load -> Math.abs(load.magnitude().doubleValue() - 0) <= TOLERANCE);
        Assertions.assertTrue(horizontalPinned.size() == 1 && b2 );


        // bending moment
        var momentRoller = bendingMomentReactions.getOrDefault(rollerPosition, List.of());
        Assertions.assertTrue(momentRoller.size() == 0);

        var momentPinned = bendingMomentReactions.getOrDefault(pinnedPosition, List.of());
        Assertions.assertTrue(momentPinned.size() == 0);
    }
}