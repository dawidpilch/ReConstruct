package com.reconstruct.view;

import com.reconstruct.model.beam.LoadingAnalysis;
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

import java.util.List;
import java.util.Map;

public class RCBeamAnalysisDesignController {
    @FXML public AreaChart<Double, Double> areaChart;

    @FXML public void initialize() {
        SimplySupportedBeam simplySupportedBeam = SimplySupportedBeam.withCustomSupportPositions(
                new Span(
                        Length.of(10),
                        new Rectangular(
                                PositiveDouble.of(10),
                                PositiveDouble.of(10)
                        )
                ), Position.of(2), Position.of(7)
        );

        Loading loading = new Loading(
                List.of(
                        VerticalPointLoad.directedDownwards(Position.of(1), Magnitude.of(-6)),
                        VerticalPointLoad.directedUpwards(Position.of(5), Magnitude.of(5))
                ),
                List.of(),
                List.of()
        );

        LoadingAnalysis loadingAnalysis = simplySupportedBeam.loadingAnalysis(loading);
        XYChart.Series<Double, Double> bendingMomentSeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> sheerForceSeries = new XYChart.Series<>();
        bendingMomentSeries.setName("Bending Moment");
        sheerForceSeries.setName("Sheer Force");
        for (Map.Entry<Position, Magnitude> entry : loadingAnalysis.bendingMomentDiagram()) {
            bendingMomentSeries.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }
        for (Map.Entry<Position, Magnitude> entry : loadingAnalysis.sheerForceDiagram()) {
            sheerForceSeries.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }

        areaChart.getData().add(bendingMomentSeries);
        areaChart.getData().add(sheerForceSeries);
        areaChart.autosize();
    }
}
