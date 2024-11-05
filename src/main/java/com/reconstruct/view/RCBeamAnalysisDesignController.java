package com.reconstruct.view;

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
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Map;

public class RCBeamAnalysisDesignController {
    @FXML public AreaChart<Double, Double> areaChart;

    @FXML public void initialize() {
        SimplySupportedBeam simplySupportedBeam = SimplySupportedBeam.withRollerSupportOnEnd(
                new Span(
                        Length.of(10),
                        new Rectangular(
                                PositiveDouble.of(10),
                                PositiveDouble.of(10)
                        )
                )
        );

        Loading loading = new Loading(
                List.of(
                        VerticalPointLoad.directedDownwards(Position.of(2d), Magnitude.of(-4)),
                        VerticalPointLoad.directedUpwards(Position.of(5d), Magnitude.of(8))
                ),
                List.of(),
                List.of()
        );

        Map<Position, Magnitude> positionMagnitudeMap = simplySupportedBeam.bendingMomentDiagram(loading);
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        series.setName("SheerForceDemo");
        for (Map.Entry<Position, Magnitude> entry : positionMagnitudeMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }

//        series.getData().add(new XYChart.Data<>(1d,1d));
//        series.getData().add(new XYChart.Data<>(1d,7d));
//        series.getData().add(new XYChart.Data<>(6d,6d));
        areaChart.getData().add(series);
        areaChart.autosize();
    }
}
