package com.reconstruct.model.beam.loading.point;

import com.reconstruct.model.value.Degree;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

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
        Assertions.assertEquals(verticalComponent.magnitude().doubleValue(), expectedVerticalMagnitude.doubleValue(), epsilon);
        Assertions.assertEquals(horizontalComponent.magnitude().doubleValue(), expectedHorizontalMagnitude.doubleValue(), epsilon);
    }

    public Stream<Arguments> testSource() {
        return Stream.of(
                Arguments.of(   // down-right
                        InclinedPointLoad.directedDownwards(Position.of(5), Magnitude.of(5), Degree.of(45)),
                        Magnitude.of(-3.535),
                        Magnitude.of(3.535)
                ),
                Arguments.of(   // down-left
                        InclinedPointLoad.directedDownwards(Position.of(5), Magnitude.of(5), Degree.of(135)),
                        Magnitude.of(-3.535),
                        Magnitude.of(-3.535)
                ),
                Arguments.of(   // up-left
                        InclinedPointLoad.directedUpwards(Position.of(5), Magnitude.of(5), Degree.of(135)),
                        Magnitude.of(3.535),
                        Magnitude.of(-3.535)
                ),
                Arguments.of(   // up-right
                        InclinedPointLoad.directedUpwards(Position.of(5), Magnitude.of(5), Degree.of(45)),
                        Magnitude.of(3.535),
                        Magnitude.of(3.535)
                )
        );
    }
}