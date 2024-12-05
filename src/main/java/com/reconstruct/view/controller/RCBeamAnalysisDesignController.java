package com.reconstruct.view.controller;

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
import com.reconstruct.view.component.ErrorDoubleTextField;
import com.reconstruct.view.viewmodel.AppendableValue;
import com.reconstruct.view.viewmodel.SimplySupportedBeamViewModel;
import com.reconstruct.view.viewmodel.ValueNotAppendedListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.util.*;

public class RCBeamAnalysisDesignController {
    @FXML public BorderPane mainPane;
    @FXML public AreaChart<Double, Double> areaChart;
    @FXML public NumberAxis xAxis;
    @FXML public NumberAxis yAxis;
    @FXML public StackPane elementsStackPane;
    @FXML public StackPane rollerPane;
    @FXML public StackPane pinnedPane;
    @FXML public ScrollPane scrollPaneWorkSpace;

    @FXML public Button generateButton;
    @FXML public Button cancelButton;

    private final SimplySupportedBeamViewModel beamViewModel = new SimplySupportedBeamViewModel();
    private final Map<Node, AppendableValue<Double>> singlePositionObjects = new HashMap<>();

    @FXML
    public void initialize() {
        scrollPaneWorkSpace.setManaged(false);
        scrollPaneWorkSpace.setVisible(false);

        areaChart.widthProperty().addListener((observable) -> updatePolygonPoints(rollerPane, beamViewModel.rollerSupportPositionValue.value()));
        areaChart.widthProperty().addListener((observable) -> updatePolygonPoints(pinnedPane, beamViewModel.pinnedSupportPositionValue.value()));

        singlePositionObjects.putAll(Map.of(
                pinnedPane, beamViewModel.pinnedSupportPositionValue,
                rollerPane, beamViewModel.rollerSupportPositionValue
        ));

        beamViewModel.beamLengthValue.addOnValueAppendedListener((oldValue, newValue, errors) -> {
            xAxis.setTickUnit(1d);
            xAxis.setUpperBound(beamViewModel.beamLengthValue.value());
            for (var entry : singlePositionObjects.entrySet()) {
                updatePolygonPoints(entry.getKey(), entry.getValue().value());
            }
        });
        
        beamViewModel.pinnedSupportPositionValue.addOnValueAppendedListener((oldValue, newValue, errors) -> updatePolygonPoints(pinnedPane, beamViewModel.pinnedSupportPositionValue.value()));
        beamViewModel.rollerSupportPositionValue.addOnValueAppendedListener((oldValue, newValue, errors) -> updatePolygonPoints(rollerPane, beamViewModel.rollerSupportPositionValue.value()));

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0d);
        xAxis.setTickUnit(1d);
        xAxis.setUpperBound(beamViewModel.beamLengthValue.value());
        yAxis.setVisible(false);
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(1.000);
        yAxis.setUpperBound(10);
        yAxis.setLowerBound(-10);
        areaChart.setLegendVisible(false);
        this.generateButton.setOnAction(event -> previewResults());
        Platform.runLater(() -> this.generateButton.requestFocus());
    }

    private void previewResults() {
        areaChart.getData().clear();
        double length = beamViewModel.beamLengthValue.value();
        Position pinnedPosition = Position.of(beamViewModel.pinnedSupportPositionValue.value());
        Position rollerPosition = Position.of(beamViewModel.rollerSupportPositionValue.value());

        SimplySupportedBeam simplySupportedBeam = SimplySupportedBeam.withCustomSupportPositions(
                new Span(
                        Length.of(length),
                        new Rectangular(
                                PositiveDouble.of(10),
                                PositiveDouble.of(10)
                        )
                ), pinnedPosition, rollerPosition
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

        var maxBendingMoment = bendingMomentDiagram.stream().map(Map.Entry::getValue).map(Magnitude::doubleValue).map(Math::abs).max(Double::compareTo).orElse(0d);
        var maxSheerForce = sheerForceDiagram.stream().map(Map.Entry::getValue).map(Magnitude::doubleValue).map(Math::abs).max(Double::compareTo).orElse(0d);
        double maxMagnitude = Double.max(maxBendingMoment, maxSheerForce);

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

        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(length / 20);
        xAxis.setUpperBound(length);
        xAxis.setLowerBound(0d);
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(maxMagnitude / 10);
        yAxis.setUpperBound(maxMagnitude);
        yAxis.setLowerBound(-maxMagnitude);
        areaChart.getData().add(bendingMomentSeries);
        areaChart.getData().add(sheerForceSeries);
    }

    private void updatePolygonPoints(Node node, double doubleValue) {
        // A *magic* number, due to some translations of other components in the layout
        double maxOffset = 125;
        double chartWidth = areaChart.getWidth();
        double chartMinX = xAxis.getLowerBound();
        double chartMaxX = xAxis.getUpperBound();
        double chartRangeX = chartMaxX - chartMinX;
        double xPixel = (doubleValue - chartMinX) / chartRangeX * chartWidth;
        node.setTranslateX(xPixel - ((maxOffset * doubleValue) / chartRangeX));
    }

    public void onGeometryButtonAction(ActionEvent ignore) {
        AppendableValue<Double> beamLengthValue = beamViewModel.beamLengthValue;
        AppendableValue<Double> pinnedSupportPositionValue = beamViewModel.pinnedSupportPositionValue;
        AppendableValue<Double> rollerSupportPositionValue = beamViewModel.rollerSupportPositionValue;

        ErrorDoubleTextField beamLengthTF = new ErrorDoubleTextField(beamLengthValue);
        ErrorDoubleTextField pinnedSuppTF = new ErrorDoubleTextField(pinnedSupportPositionValue);
        ErrorDoubleTextField rollerSuppTF = new ErrorDoubleTextField(rollerSupportPositionValue);

        // values to restore if action canceled
        Double previousLength = beamLengthValue.value();
        Double previousPinnedPosition = pinnedSupportPositionValue.value();
        Double previousRollerPosition = rollerSupportPositionValue.value();

        double prefButtonWidth = 75d;
        Button localSave = new Button("Save");
        Button localCancel = new Button("Cancel");
        localSave.setPrefWidth(prefButtonWidth);
        localCancel.setPrefWidth(prefButtonWidth);


        ValueNotAppendedListener<Double> listener = (oldValue, newValue, valueErrors) -> {
            if (valueErrors.isEmpty()) {
                localSave.setDisable(false);
            }
            localSave.setDisable(true);
        };

        beamLengthValue.addOnValueNotAppendedListener(listener);
        pinnedSupportPositionValue.addOnValueNotAppendedListener(listener);
        rollerSupportPositionValue.addOnValueNotAppendedListener(listener);

        localSave.setOnAction(actionEvent -> {
            hideWorkSpace();
            previewResults();
            beamLengthValue.removeOnValueNotChangedListener(listener);
            pinnedSupportPositionValue.removeOnValueNotChangedListener(listener);
            rollerSupportPositionValue.removeOnValueNotChangedListener(listener);
        });

        localCancel.setOnAction(actionEvent -> {
            hideWorkSpace();
            beamLengthValue.tryAppend(previousLength);
            pinnedSupportPositionValue.tryAppend(previousPinnedPosition);
            rollerSupportPositionValue.tryAppend(previousRollerPosition);
            areaChart.getData().forEach(doubleDoubleSeries -> doubleDoubleSeries.getNode().setVisible(true));
            beamLengthValue.removeOnValueNotChangedListener(listener);
            pinnedSupportPositionValue.removeOnValueNotChangedListener(listener);
            rollerSupportPositionValue.removeOnValueNotChangedListener(listener);
        });

        VBox propertiesVBox = new VBox(15, beamLengthTF.node(), pinnedSuppTF.node(), rollerSuppTF.node());
        propertiesVBox.setMaxWidth(300);

        VBox content = new VBox(
                15,
                new HBox(15, localSave, localCancel),
                new Separator(Orientation.HORIZONTAL),
                propertiesVBox
        );

        content.setFillWidth(true);
        content.setPrefWidth(Region.USE_COMPUTED_SIZE);
        areaChart.getData().forEach(doubleDoubleSeries -> doubleDoubleSeries.getNode().setVisible(false));
        showWorkSpace(content);
    }

    public void onLoadingButtonAction(ActionEvent actionEvent) {

    }

    private void showWorkSpace(Node workSpaceNode) {
//        VBox vBox = new VBox(15, workSpaceNode, new Separator(Orientation.HORIZONTAL), workSpaceNode);
//        vBox.setFillWidth(true);
//        vBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        scrollPaneWorkSpace.setVisible(true);
        scrollPaneWorkSpace.setManaged(true);
        scrollPaneWorkSpace.setContent(workSpaceNode);
        generateButton.setDisable(true);
        cancelButton.setDisable(true);
        mainPane.getRight().setDisable(true);
    }

    private void hideWorkSpace() {
        scrollPaneWorkSpace.setVisible(false);
        scrollPaneWorkSpace.setManaged(false);
        scrollPaneWorkSpace.setContent(null);
        mainPane.getRight().setDisable(false);
        generateButton.setDisable(false);
        cancelButton.setDisable(true);
    }
}
