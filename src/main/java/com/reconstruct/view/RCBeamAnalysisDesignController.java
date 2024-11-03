package com.reconstruct.view;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;

public class RCBeamAnalysisDesignController {
    @FXML public AreaChart<Double, Double> areaChart;

    @FXML public void initialize() {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        series.setName("SheerForceDemo");
        series.getData().add(new XYChart.Data<>(1d,1d));
        series.getData().add(new XYChart.Data<>(1d,7d));
        series.getData().add(new XYChart.Data<>(6d,6d));
        areaChart.getData().add(series);
        areaChart.autosize();
    }
}
