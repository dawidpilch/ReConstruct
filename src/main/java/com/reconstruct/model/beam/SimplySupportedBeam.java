package com.reconstruct.model.beam;

import com.reconstruct.model.beam.equation.SummationOfHorizontalForces;
import com.reconstruct.model.beam.equation.SummationOfMoments;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.distributed.UniformlyDistributedLoad;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.range.EvenlyDistributedDoubleRange;

import java.util.*;

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
        SummationOfMoments summationOfMoments = new SummationOfMoments(loading);
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

    public LoadingAnalysis loadingAnalysis(Loading loading) {
        var supportVerticalReactions = supportVerticalReactions(loading).values().stream().flatMap(Collection::stream).toList();
        List<VerticalPointLoad> verticalPointLoads = new ArrayList<>(loading.verticalPointLoads());
        verticalPointLoads.addAll(supportVerticalReactions);
        if (verticalPointLoads.size() < 2)
            throw new RuntimeException("At least two Vertical Loads expected (Support reactions missing)");

        Map<Position, Magnitude> bendingMomentDiagram = new LinkedHashMap<>();
        Map<Position, Magnitude> sheerForceDiagram = new LinkedHashMap<>();

        Set<Position> characteristicPointsSet = new TreeSet<>(Comparator.comparingDouble(Position::doubleValue));
        verticalPointLoads.forEach(verticalPointLoad -> characteristicPointsSet.add(verticalPointLoad.position()));
        loading.bendingMoments().forEach(bendingMoment -> characteristicPointsSet.add(bendingMoment.position()));
        loading.uniformlyDistributedLoads().forEach(uniformlyDistributedLoad -> {
            characteristicPointsSet.add(uniformlyDistributedLoad.startPosition());
            characteristicPointsSet.add(uniformlyDistributedLoad.endPosition());
            characteristicPointsSet.add(uniformlyDistributedLoad.resultantPosition());
        });

        Position[] characteristicPointsArray = characteristicPointsSet.toArray(Position[]::new);
        int size = characteristicPointsArray.length;
        for (int i = 1; i < size; i++) {
            Position currentEndPosition = characteristicPointsArray[i];
            Position previousEndPosition = characteristicPointsArray[i - 1];

            List<VerticalPointLoad> verticalLoadsInSegment = new ArrayList<>(verticalPointLoads.stream()
                    .filter(verticalPointLoad -> verticalPointLoad.position().isToTheLeftOf(currentEndPosition))
                    .sorted(Comparator.comparingDouble(value -> value.position().doubleValue()))
                    .toList()
            );

            List<BendingMoment> bendingMomentsInSegment = new ArrayList<>(loading.bendingMoments().stream()
                    .filter(bendingMoment -> bendingMoment.position().isToTheLeftOf(currentEndPosition))
                    .sorted(Comparator.comparingDouble(value -> value.position().doubleValue()))
                    .toList()
            );

            List<UniformlyDistributedLoad> uniformlyDistributedLoadsInSegment = new ArrayList<>(loading.uniformlyDistributedLoads().stream()
                    .filter(uniformlyDistributedLoad -> uniformlyDistributedLoad.startPosition().isToTheLeftOf(currentEndPosition))
                    .sorted(Comparator.comparingDouble(value -> value.startPosition().doubleValue()))
                    .toList()
            );

            double segmentOffset = 0.00001;
            List<Double> positionsPerSpan = new EvenlyDistributedDoubleRange(
                    previousEndPosition.doubleValue() + segmentOffset,
                    currentEndPosition.doubleValue() - segmentOffset,
                    10
            ).values();


            for (double doublePosition : positionsPerSpan) {
                Position currentPosition = Position.of(doublePosition);
                if (bendingMomentDiagram.containsKey(currentPosition)) {
                    continue;
                }

                double bendingMomentSum = 0;
                double sheerForceSum = 0;
                for (VerticalPointLoad verticalPointLoad : verticalLoadsInSegment) {
                    bendingMomentSum += verticalPointLoad.magnitude().doubleValue() * (doublePosition - verticalPointLoad.position().doubleValue());
                    sheerForceSum += verticalPointLoad.magnitude().doubleValue();
                }

                for (var bendingMoment : bendingMomentsInSegment) {
                    // negate bm value
                    bendingMomentSum -= bendingMoment.magnitude().doubleValue();
                }

                for (var temp : uniformlyDistributedLoadsInSegment) {
                    UniformlyDistributedLoad udl;
                    if (temp.endPosition().isToTheRightOf(Position.of(doublePosition))) {
                        udl = UniformlyDistributedLoad.of(temp.startPosition(), Position.of(doublePosition), temp.magnitude());
                    } else {
                        udl = temp;
                    }
                    var resultant = udl.resultantForce();
                    sheerForceSum += resultant.magnitude().doubleValue();
                    bendingMomentSum += resultant.magnitude().doubleValue() * (udl.endPosition().doubleValue() - resultant.position().doubleValue() + (doublePosition - udl.endPosition().doubleValue()));
                }

                bendingMomentDiagram.put(Position.of(doublePosition), Magnitude.of(bendingMomentSum));
                sheerForceDiagram.put(Position.of(doublePosition), Magnitude.of(sheerForceSum));
            }
        }

        return new LoadingAnalysis(
                supportVerticalReactions,
                new BendingMomentDiagram(bendingMomentDiagram),
                new SheerForceDiagram(sheerForceDiagram)
        );
    }

    public Span span() {
        return span;
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