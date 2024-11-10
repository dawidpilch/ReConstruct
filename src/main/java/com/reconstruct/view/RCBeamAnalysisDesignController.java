package com.reconstruct.view;

import com.reconstruct.model.beam.BendingMomentDiagram;
import com.reconstruct.model.beam.LoadingAnalysis;
import com.reconstruct.model.beam.SheerForceDiagram;
import com.reconstruct.model.beam.SimplySupportedBeam;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.Rectangular;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.apache.commons.math3.util.MathUtils;

import java.util.List;
import java.util.Map;

public class RCBeamAnalysisDesignController {
    @FXML public AreaChart<Double, Double> areaChart;
    @FXML public NumberAxis xAxis;
    @FXML public NumberAxis yAxis;

    @FXML public void initialize() {
        double length = 14;
        SimplySupportedBeam simplySupportedBeam = SimplySupportedBeam.withCustomSupportPositions(
                new Span(
                        Length.of(length),
                        new Rectangular(
                                PositiveDouble.of(10),
                                PositiveDouble.of(10)
                        )
                ), Position.of(3), Position.of(14)
        );

        Loading loading = new Loading(
                List.of(
                        VerticalPointLoad.directedDownwards(Position.of(3), Magnitude.of(-8)),
                        VerticalPointLoad.directedUpwards(Position.of(7), Magnitude.of(5.64))
                ),
                List.of(),
                List.of()
        );

        LoadingAnalysis loadingAnalysis = simplySupportedBeam.loadingAnalysis(loading);
        BendingMomentDiagram bendingMomentDiagram = loadingAnalysis.bendingMomentDiagram();
        SheerForceDiagram sheerForceDiagram = loadingAnalysis.sheerForceDiagram();

        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(length / 20);
        xAxis.setUpperBound(length);
        xAxis.setLowerBound(0d);

        var maxBendingMoment = bendingMomentDiagram.stream().map(Map.Entry::getValue).map(Magnitude::doubleValue).map(Math::abs).max(Double::compareTo).orElse(0d);
        var maxSheerForce = sheerForceDiagram.stream().map(Map.Entry::getValue).map(Magnitude::doubleValue).map(Math::abs).max(Double::compareTo).orElse(0d);
        double maxMagnitude = Double.max(maxBendingMoment, maxSheerForce);

        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(maxMagnitude / 10);
        yAxis.setUpperBound(maxMagnitude);
        yAxis.setLowerBound(-maxMagnitude);

        XYChart.Series<Double, Double> bendingMomentSeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> sheerForceSeries = new XYChart.Series<>();
        bendingMomentSeries.setName("Bending Moment");
        sheerForceSeries.setName("Sheer Force");
        for (Map.Entry<Position, Magnitude> entry : bendingMomentDiagram) {
            bendingMomentSeries.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }
        for (Map.Entry<Position, Magnitude> entry : sheerForceDiagram) {
            sheerForceSeries.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }

        areaChart.getData().add(bendingMomentSeries);
        areaChart.getData().add(sheerForceSeries);
    }
}
