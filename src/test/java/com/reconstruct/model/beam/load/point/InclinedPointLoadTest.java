package com.reconstruct.model.beam.load.point;

import com.reconstruct.model.value.Degree;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.Position;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InclinedPointLoadTest {

    @ParameterizedTest
    @MethodSource("testSource")
    public void test(InclinedPointLoad inclinedPointLoad, Magnitude expectedVerticalMagnitude, Magnitude expectedHorizontalMagnitude) {
        VerticalPointLoad verticalComponent = inclinedPointLoad.verticalComponent();
        HorizontalPointLoad horizontalComponent = inclinedPointLoad.horizontalComponent();

        double epsilon = 0.001d;
        Assertions.assertEquals(verticalComponent.magnitude().value(), expectedVerticalMagnitude.value(), epsilon);
        Assertions.assertEquals(horizontalComponent.magnitude().value(), expectedHorizontalMagnitude.value(), epsilon);
    }

    public Stream<Arguments> testSource() {
        return Stream.of(
                Arguments.of(   // down-right
                        InclinedPointLoad.directedDownwards(new Position(5), new Magnitude(5), new Degree(45)),
                        new Magnitude(-3.535),
                        new Magnitude(3.535)
                ),
                Arguments.of(   // down-left
                        InclinedPointLoad.directedDownwards(new Position(5), new Magnitude(5), new Degree(135)),
                        new Magnitude(-3.535),
                        new Magnitude(-3.535)
                ),
                Arguments.of(   // up-left
                        InclinedPointLoad.directedUpwards(new Position(5), new Magnitude(5), new Degree(135)),
                        new Magnitude(3.535),
                        new Magnitude(-3.535)
                ),
                Arguments.of(   // up-right
                        InclinedPointLoad.directedUpwards(new Position(5), new Magnitude(5), new Degree(45)),
                        new Magnitude(3.535),
                        new Magnitude(3.535)
                )
        );
    }
}