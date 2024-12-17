package com.reconstruct.view.controller;

import com.reconstruct.model.beam.BendingMomentDiagram;
import com.reconstruct.model.beam.LoadingAnalysis;
import com.reconstruct.model.beam.SheerForceDiagram;
import com.reconstruct.model.beam.SimplySupportedBeam;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.point.PointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.Rectangular;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import com.reconstruct.view.component.ErrorDoubleTextField;
import com.reconstruct.view.viewmodel.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
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

    private Node loadingWorkspaceNode;
    private Node geometryWorkspaceNode;

    private final SimplySupportedBeamViewModel beamViewModel = new SimplySupportedBeamViewModel();
    private final Map<Node, AppendableValue<Double>> singlePositionObjects = new HashMap<>();

    @FXML
    public void initialize() {
        scrollPaneWorkSpace.setManaged(false);
        scrollPaneWorkSpace.setVisible(false);

        areaChart.widthProperty().addListener((observable) -> {
            for (var entry : singlePositionObjects.entrySet()) {
                updatePolygonPoints(entry.getKey(), entry.getValue().value());
            }
        });

        singlePositionObjects.putAll(Map.of(
                pinnedPane, beamViewModel.pinnedSupportPositionValue,
                rollerPane, beamViewModel.rollerSupportPositionValue
        ));

        beamViewModel.beamLengthValue.addOnTryAppendValueListener((oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            xAxis.setTickUnit(1d);
            xAxis.setUpperBound(beamViewModel.beamLengthValue.value());
            for (var entry : singlePositionObjects.entrySet()) {
                updatePolygonPoints(entry.getKey(), entry.getValue().value());
            }
        });
        
        beamViewModel.pinnedSupportPositionValue.addOnTryAppendValueListener((oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }
            updatePolygonPoints(pinnedPane, newValue);
        });
        beamViewModel.rollerSupportPositionValue.addOnTryAppendValueListener((oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }
            updatePolygonPoints(rollerPane, newValue);
        });

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
        generateButton.setOnAction(event -> previewResults());

        initLoadingWorkspace();
        initGeometryWorkspace();
        Platform.runLater(() -> this.generateButton.requestFocus());
    }

    private void initGeometryWorkspace() {
        var beamLengthValue = beamViewModel.beamLengthValue;
        var pinnedSupportPositionValue = beamViewModel.pinnedSupportPositionValue;
        var rollerSupportPositionValue = beamViewModel.rollerSupportPositionValue;

        var beamLengthTF = new ErrorDoubleTextField(beamLengthValue);
        var pinnedSuppTF = new ErrorDoubleTextField(pinnedSupportPositionValue);
        var rollerSuppTF = new ErrorDoubleTextField(rollerSupportPositionValue);

        // values to restore if action canceled
        var lengthMemento = beamLengthValue.value();
        var pinnedPositionMemento = pinnedSupportPositionValue.value();
        var rollerPositionMemento = rollerSupportPositionValue.value();

        double prefButtonWidth = 75d;
        var localSave = new Button("Save");
        var localCancel = new Button("Cancel");
        localSave.setPrefWidth(prefButtonWidth);
        localCancel.setPrefWidth(prefButtonWidth);


        AppendableValue.OnTryAppendValueListener<Double> listener = (oldValue, newValue, valueErrors) -> {
            localSave.setDisable(!valueErrors.isEmpty());
        };

        beamLengthValue.addOnTryAppendValueListener(listener);
        pinnedSupportPositionValue.addOnTryAppendValueListener(listener);
        rollerSupportPositionValue.addOnTryAppendValueListener(listener);

        localSave.setOnAction(actionEvent -> {
            hideWorkSpace();
            previewResults();
        });

        localCancel.setOnAction(actionEvent -> {
            beamLengthValue.tryAppend(lengthMemento);
            pinnedSupportPositionValue.tryAppend(pinnedPositionMemento);
            rollerSupportPositionValue.tryAppend(rollerPositionMemento);
            hideWorkSpace();
        });

        var propertiesVBox = new VBox(15, beamLengthTF.node(), pinnedSuppTF.node(), rollerSuppTF.node());
        propertiesVBox.setMaxWidth(300);

        var geometryWorkspaceNode = new VBox(
                15,
                new HBox(15, localSave, localCancel),
                new Separator(Orientation.HORIZONTAL),
                propertiesVBox
        );

        geometryWorkspaceNode.setFillWidth(true);
        geometryWorkspaceNode.setPrefWidth(Region.USE_COMPUTED_SIZE);
        this.geometryWorkspaceNode = geometryWorkspaceNode;
    }

    private void initLoadingWorkspace() {
        double prefButtonWidth = 75d;
        var localSave = new Button("Save");
        var localCancel = new Button("Cancel");
        localSave.setPrefWidth(prefButtonWidth);
        localCancel.setPrefWidth(prefButtonWidth);

        var verticalPointLoadsMemento = beamViewModel.verticalPointLoadValue.value();

        Tab pointLoadsTab = new Tab("Point Loads");
        TableView<VerticalPointLoad> pointLoadsTableView = new TableView<>();
        pointLoadsTableView.setEditable(false);
        pointLoadsTableView.getSelectionModel().setCellSelectionEnabled(false);
        pointLoadsTableView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        pointLoadsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        AppendableValue.OnTryAppendValueListener<Collection<VerticalPointLoad>> fillTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            pointLoadsTableView.getItems().clear();
            newValue.forEach(verticalPointLoad -> pointLoadsTableView.getItems().add(verticalPointLoad));
        };

        beamViewModel.verticalPointLoadValue.addOnTryAppendValueListener(fillTableWithDataListener);

        TableColumn<VerticalPointLoad, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleStringProperty("ID"));

        TableColumn<VerticalPointLoad, Number> positionColumn = new TableColumn<>("Position (m)");
        positionColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().position().doubleValue()));

        TableColumn<VerticalPointLoad, Number> magnitudeColumn = new TableColumn<>("Magnitude (kN)");
        magnitudeColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().magnitude().doubleValue()));

        pointLoadsTableView.getColumns().add(idColumn);
        pointLoadsTableView.getColumns().add(positionColumn);
        pointLoadsTableView.getColumns().add(magnitudeColumn);

        var addButton = new Button("Add");
        addButton.setOnAction(event -> {
            var modified = new ArrayList<>(beamViewModel.verticalPointLoadValue.value());
            modified.add(VerticalPointLoad.of(Position.of(3), Magnitude.of(4)));
            ValueErrors errors = beamViewModel.verticalPointLoadValue.tryAppend(modified);
            if (!errors.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, errors.iterator().next(), ButtonType.OK);
            }
        });

        var removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            ObservableList<VerticalPointLoad> selectedItems = pointLoadsTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.size() != 1) {
                System.out.println("More than one item selected!");
            }
            PointLoad pointLoad = selectedItems.getFirst();

            var modified = new ArrayList<>(beamViewModel.verticalPointLoadValue.value());
            modified.remove(pointLoad);
            beamViewModel.verticalPointLoadValue.tryAppend(modified);
        });

        ScrollPane pointLoadsTVScrollPane = new ScrollPane(pointLoadsTableView);
        pointLoadsTVScrollPane.setFitToHeight(true);
        pointLoadsTVScrollPane.setFitToWidth(true);
        pointLoadsTVScrollPane.setPrefWidth(Region.USE_COMPUTED_SIZE);

        var pointLoadsContent = new VBox(
                15,
                new HBox(15, addButton, removeButton),
                new Separator(Orientation.HORIZONTAL),
                pointLoadsTVScrollPane
        );

        pointLoadsContent.setPadding(new Insets(15));

        pointLoadsTab.setContent(pointLoadsContent);

        TabPane loadingTabPane = new TabPane(pointLoadsTab);
        loadingTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        var loadingWorkspace = new VBox(
                15,
                new HBox(15, localSave, localCancel),
                new Separator(Orientation.HORIZONTAL),
                loadingTabPane
        );

        localSave.setOnAction(actionEvent -> {
            hideWorkSpace();
            previewResults();
        });

        localCancel.setOnAction(actionEvent -> {
            beamViewModel.verticalPointLoadValue.tryAppend(verticalPointLoadsMemento);
            hideWorkSpace();
        });

        loadingWorkspace.setFillWidth(true);
        loadingWorkspace.setPrefWidth(Region.USE_COMPUTED_SIZE);
        this.loadingWorkspaceNode = loadingWorkspace;
    }

    private void previewResults() {
        Loading loading = new Loading(
                beamViewModel.verticalPointLoadValue.value(),
                beamViewModel.horizontalPointLoadValue.value(),
                beamViewModel.bendingMomentValue.value()
        );

        if (loading.verticalPointLoads().isEmpty() && loading.horizontalPointLoads().isEmpty() && loading.bendingMoments().isEmpty()) {
            yAxis.setAutoRanging(false);
            yAxis.setTickUnit(1.000);
            yAxis.setUpperBound(10);
            yAxis.setLowerBound(-10);
            return;
        }

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

    public void onGeometryButtonAction(ActionEvent ignored) {
        showWorkSpace(geometryWorkspaceNode);
    }

    public void onLoadingButtonAction(ActionEvent ignored) {
        showWorkSpace(loadingWorkspaceNode);
    }

    private void showWorkSpace(Node workSpaceNode) {
        scrollPaneWorkSpace.setVisible(true);
        scrollPaneWorkSpace.setManaged(true);
        scrollPaneWorkSpace.setContent(workSpaceNode);
        generateButton.setDisable(true);
        cancelButton.setDisable(true);
        mainPane.getRight().setDisable(true);
        areaChart.getData().forEach(doubleDoubleSeries -> doubleDoubleSeries.getNode().setVisible(false));
    }

    private void hideWorkSpace() {
        scrollPaneWorkSpace.setVisible(false);
        scrollPaneWorkSpace.setManaged(false);
        scrollPaneWorkSpace.setContent(null);
        generateButton.setDisable(false);
        cancelButton.setDisable(false);
        mainPane.getRight().setDisable(false);
        areaChart.getData().forEach(doubleDoubleSeries -> doubleDoubleSeries.getNode().setVisible(true));
    }
}
