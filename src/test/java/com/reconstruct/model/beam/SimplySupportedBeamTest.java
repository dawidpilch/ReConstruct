package com.reconstruct.model.beam;

import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.Rectangular;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimplySupportedBeamTest {
    private static final double TOLERANCE = 0.001;

    private static final Span COMMON_SPAN = new Span(
            Length.of(10),
            new Rectangular(PositiveDouble.of(5), PositiveDouble.of(5))
    );

    @ParameterizedTest
    @MethodSource("verticalPointLoadsOnlyTestSource")
    public void verticalPointLoadsOnlyTest(Loading loading,
                                           Position pinnedPosition,
                                           Position rollerPosition,
                                           Magnitude expectedVerticalPinned,
                                           Magnitude expectedVerticalRoller) {
        var simplySupportedBeam = SimplySupportedBeam.withCustomSupportPositions(COMMON_SPAN, pinnedPosition, rollerPosition);
        var verticalReactions = simplySupportedBeam.supportVerticalReactions(loading);
        var horizontalReactions = simplySupportedBeam.supportHorizontalReactions(loading);
        var bendingMomentReactions = simplySupportedBeam.supportBendingMomentReactions(loading);


        // vertical
        var verticalPinned = verticalReactions.getOrDefault(pinnedPosition, List.of());
        var b0 = verticalPinned.stream().anyMatch(load -> {
            logComparison(load.magnitude(), expectedVerticalPinned);
            return Precision.equals(load.magnitude().doubleValue(), expectedVerticalPinned.doubleValue(), TOLERANCE);
        });
        Assertions.assertTrue(verticalPinned.size() == 1 && b0 );

        var verticalRoller = verticalReactions.getOrDefault(rollerPosition, List.of());
        var b1 = verticalRoller.stream().anyMatch(load -> {
            logComparison(load.magnitude(), expectedVerticalRoller);
            return Precision.equals(load.magnitude().doubleValue(), expectedVerticalRoller.doubleValue(), TOLERANCE);
        });
        Assertions.assertTrue(verticalRoller.size() == 1 && b1);


        // horizontal
        var horizontalPinned = horizontalReactions.getOrDefault(pinnedPosition, List.of());
        var b2 = horizontalPinned.stream().anyMatch(load -> {
            logComparison(load.magnitude(), Magnitude.zero());
            return Precision.equals(load.magnitude().doubleValue(), 0, TOLERANCE);
        });
        Assertions.assertTrue(horizontalPinned.size() == 1 && b2 );

        var horizontalRoller = horizontalReactions.getOrDefault(rollerPosition, List.of());
        Assertions.assertTrue(horizontalRoller.size() == 0);


        // bending moment
        var momentPinned = bendingMomentReactions.getOrDefault(pinnedPosition, List.of());
        Assertions.assertTrue(momentPinned.size() == 0);

        var momentRoller = bendingMomentReactions.getOrDefault(rollerPosition, List.of());
        Assertions.assertTrue(momentRoller.size() == 0);
    }

    private Stream<Arguments> verticalPointLoadsOnlyTestSource() {
        return Stream.of(
                Arguments.of(
                        new Loading(
                                List.of(
                                        VerticalPointLoad.directedDownwards(Position.of(6), Magnitude.of(5))
                                ),
                                List.of(),
                                List.of()
                        ),
                        Position.of(6.5),           // pinned
                        Position.of(1.5),           // roller
                        Magnitude.of(4.5),    // pinned expected
                        Magnitude.of(0.5)     // roller expected
                ),
                Arguments.of(
                        new Loading(
                                List.of(
                                        VerticalPointLoad.directedUpwards(Position.of(5), Magnitude.of(8)),
                                        VerticalPointLoad.directedDownwards(Position.of(9), Magnitude.of(5))
                                ),
                                List.of(),
                                List.of()
                        ),
                        Position.of(10),            // pinned
                        Position.of(7),             // roller
                        Magnitude.of(8.667),  // pinned expected
                        Magnitude.of(-11.667) // roller expected
                )
        );
    }

    private static void logComparison(Magnitude real, Magnitude expected) {
        System.out.println("Real: " + real.doubleValue());
        System.out.println("Expected: " + expected.doubleValue());
    }
}