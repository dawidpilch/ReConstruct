package com.reconstruct.model.beam;

import com.reconstruct.model.beam.equation.SummationOfHorizontalForces;
import com.reconstruct.model.beam.equation.SummationOfMoments;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.Section;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;

import java.util.List;
import java.util.Map;

public class SimplySupportedBeam implements Beam {
    public static SimplySupportedBeam withRollerOnStart(Length length, Section section) {
        return new SimplySupportedBeam(length, section, Position.of(length.doubleValue()), Position.of(0));
    }

    public static SimplySupportedBeam withRollerOnEnd(Length length, Section section) {
        return new SimplySupportedBeam(length, section, Position.of(0), Position.of(length.doubleValue()));
    }

    private final Length length;
    private final Section section;
    private final Position pinnedPosition;
    private final Position rollerPosition;

    private SimplySupportedBeam(Length length, Section section, Position pinnedPosition, Position rollerPosition) {
        this.length = length;
        this.section = section;
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
        if (positionOfSupportWithUnknownReaction.isToTheLeftOf(pointOfRotation))
            magnitude *= -1;
        return VerticalPointLoad.of(positionOfSupportWithUnknownReaction, Magnitude.of(magnitude));
    }
}