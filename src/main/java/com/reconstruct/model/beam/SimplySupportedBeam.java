package com.reconstruct.model.beam;

import com.reconstruct.model.beam.equation.SummationOfHorizontalForces;
import com.reconstruct.model.beam.equation.SummationOfMoments;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

import java.util.List;
import java.util.Map;

public class SimplySupportedBeam implements Beam {

    /**
     *
     * @param span {@link Span} of the beam
     * @return {@link SimplySupportedBeam} with roller support on start
     */
    public static SimplySupportedBeam withRollerSupportOnStart(Span span) {
        return new SimplySupportedBeam(span, Position.of(span.length().doubleValue()), Position.of(0));
    }

    /**
     *
     * @param span {@link Span} of the beam
     * @return {@link SimplySupportedBeam} with roller support on end
     */
    public static SimplySupportedBeam withRollerSupportOnEnd(Span span) {
        return new SimplySupportedBeam(span, Position.of(0), Position.of(span.length().doubleValue()));
    }

    /**
     *
     * @param span {@link Span} of the beam
     * @param pinnedPosition {@link Position} of pinned support
     * @param rollerPosition {@link Position} of roller support
     * @throws IllegalArgumentException if {@link Position} of either support is out of range of the {@link Span},
     * or if {@link Position}s of both supports are equal
     * @return {@link SimplySupportedBeam} with custom support positions
     */
    public static SimplySupportedBeam withCustomSupportPositions(Span span, Position pinnedPosition, Position rollerPosition) {
        if (!pinnedPosition.inRangeOf(span) || !rollerPosition.inRangeOf(span))
            throw new IllegalArgumentException("Support's position must be defined in range of the span's length.");
        if (pinnedPosition.equals(rollerPosition))
            throw new IllegalArgumentException("Supports must be defined in different positions.");
        return new SimplySupportedBeam(span, pinnedPosition, rollerPosition);
    }

    private final Span span;
    private final Position pinnedPosition;
    private final Position rollerPosition;

    private SimplySupportedBeam(Span span, Position pinnedPosition, Position rollerPosition) {
        this.span = span;
        this.pinnedPosition = pinnedPosition;
        this.rollerPosition = rollerPosition;
    }

    @Override
    public Map<Position, List<VerticalPointLoad>> supportVerticalReactions(Loading loading) {
        SummationOfMoments summationOfMoments = new SummationOfMoments(loading.verticalPointLoads(), loading.bendingMoments());
        VerticalPointLoad vPinned = verticalReaction(summationOfMoments, rollerPosition, pinnedPosition);
        VerticalPointLoad vRoller = verticalReaction(summationOfMoments, pinnedPosition, rollerPosition);

        return Map.of(
            rollerPosition, List.of(vRoller),
            pinnedPosition, List.of(vPinned)
        );
    }

    @Override
    public Map<Position, List<HorizontalPointLoad>> supportHorizontalReactions(Loading loading) {
        SummationOfHorizontalForces summationOfHorizontalForces = new SummationOfHorizontalForces(loading.horizontalPointLoads());
        HorizontalPointLoad hPinned = horizontalReaction(summationOfHorizontalForces, pinnedPosition);
        return Map.of(pinnedPosition, List.of(hPinned));
    }

    @Override
    public Map<Position, List<BendingMoment>> supportBendingMomentReactions(Loading loading) {
        return Map.of();
    }

    private HorizontalPointLoad horizontalReaction(SummationOfHorizontalForces summationOfHorizontalForces, Position positionOfSupportWithUnknownReaction) {
        double magnitude = summationOfHorizontalForces.value().doubleValue() * -1;
        return HorizontalPointLoad.of(positionOfSupportWithUnknownReaction, Magnitude.of(magnitude));
    }

    private VerticalPointLoad verticalReaction(SummationOfMoments summationOfMoments, Position pointOfRotation, Position positionOfSupportWithUnknownReaction) {
        double magnitude = summationOfMoments.valueAtPosition(pointOfRotation) / Math.abs(pointOfRotation.doubleValue() - positionOfSupportWithUnknownReaction.doubleValue());
        if (positionOfSupportWithUnknownReaction.isToTheRightOf(pointOfRotation))
            magnitude *= -1;
        return VerticalPointLoad.of(positionOfSupportWithUnknownReaction, Magnitude.of(magnitude));
    }
}