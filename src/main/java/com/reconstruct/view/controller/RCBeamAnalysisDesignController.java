package com.reconstruct.view.controller;

import com.reconstruct.model.beam.BendingMomentDiagram;
import com.reconstruct.model.beam.LoadingAnalysis;
import com.reconstruct.model.beam.SheerForceDiagram;
import com.reconstruct.model.beam.SimplySupportedBeam;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.distributed.UniformlyDistributedLoad;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.PointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.Rectangular;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import com.reconstruct.view.component.BeamView;
import com.reconstruct.view.component.ErrorDoubleTextField;
import com.reconstruct.view.viewmodel.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

public class RCBeamAnalysisDesignController {
    @FXML public BorderPane borderPane;
    @FXML public StackPane centerPane;
    @FXML public MenuBar menuBar;

    private BeamView beamView;
    private final SimplySupportedBeamViewModel beamViewModel = new SimplySupportedBeamViewModel();

    @FXML
    public void initialize() {
        beamView = new BeamView(1000, 500, beamViewModel);
        Node beamViewNode = beamView.node();
        centerPane.getChildren().add(beamViewNode);
        StackPane.setAlignment(beamViewNode, Pos.CENTER);
        beamView.refreshGeometry();
    }

    private LoadingAnalysis loadingAnalysis() {
        Loading loading = new Loading(
                beamViewModel.verticalPointLoadsValue.value(),
                beamViewModel.horizontalPointLoadsValue.value(),
                beamViewModel.bendingMomentsValue.value(),
                beamViewModel.uniformlyDistributedLoadsValue.value()
        );

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

        return simplySupportedBeam.loadingAnalysis(loading);
    }

    public void onGeometryConfiguration(ActionEvent ignored) {
        var beamLengthValue = beamViewModel.beamLengthValue;
        var pinnedSupportPositionValue = beamViewModel.pinnedSupportPositionValue;
        var rollerSupportPositionValue = beamViewModel.rollerSupportPositionValue;

        var beamLengthTF = new ErrorDoubleTextField(beamLengthValue);
        var pinnedSuppTF = new ErrorDoubleTextField(pinnedSupportPositionValue);
        var rollerSuppTF = new ErrorDoubleTextField(rollerSupportPositionValue);

        double prefButtonWidth = 75d;
        var okButton = new Button("Ok");
        okButton.setPrefWidth(prefButtonWidth);

        AppendableValue.OnTryAppendValueListener<Double> listener = (oldValue, newValue, valueErrors) -> {
            okButton.setDisable(!valueErrors.isEmpty());
        };

        beamLengthValue.addOnTryAppendValueListener(listener);
        pinnedSupportPositionValue.addOnTryAppendValueListener(listener);
        rollerSupportPositionValue.addOnTryAppendValueListener(listener);

        var propertiesVBox = new VBox(15, beamLengthTF.node(), pinnedSuppTF.node(), rollerSuppTF.node());
        propertiesVBox.setMaxWidth(300);

        HBox buttons = new HBox(15, okButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane content = new BorderPane();
        content.setCenter(propertiesVBox);
        content.setBottom(new VBox(15, new Separator(Orientation.HORIZONTAL), buttons));
        content.setPadding(new Insets(15));

        BorderPane.setAlignment(propertiesVBox, Pos.TOP_LEFT);
        BorderPane.setAlignment(buttons, Pos.CENTER_RIGHT);
        Stage stage = simpleStage(new Scene(content), "Geometry configuration", 380, 300);

        okButton.setOnAction(actionEvent -> {
            beamLengthValue.removeOnTryAppendValueListener(listener);
            pinnedSupportPositionValue.removeOnTryAppendValueListener(listener);
            rollerSupportPositionValue.removeOnTryAppendValueListener(listener);
            beamView.refreshGeometry();
            beamView.displayLoading();
            stage.close();
        });

        beamView.hideDiagram();
        stage.setOnCloseRequest(event -> okButton.getOnAction().handle(new ActionEvent()));

        Platform.runLater(okButton::requestFocus);
        stage.showAndWait();
    }

    private static Stage simpleStage(Scene content, String title, double height, double width) {
        Stage stage = new Stage();
        stage.setScene(content);
        stage.setHeight(height);
        stage.setWidth(width);
        stage.setMinHeight(height);
        stage.setMinWidth(width);
        stage.setMaxHeight(height);
        stage.setMaxWidth(width);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        return stage;
    }

    public void onLoadingConfiguration(ActionEvent ignored) {
        double prefButtonWidth = 75d;
        var okButton = new Button("OK");
        okButton.setPrefWidth(prefButtonWidth);

        // --- Point Loads ---- //
        Tab pointLoadsTab = new Tab("Point Loads");
        TableView<VerticalPointLoad> pointLoadsTableView = new TableView<>();
        pointLoadsTableView.setEditable(false);
        pointLoadsTableView.getSelectionModel().setCellSelectionEnabled(false);
        pointLoadsTableView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        pointLoadsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        AppendableValue.OnTryAppendValueListener<Collection<VerticalPointLoad>> fillPointLoadsTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            pointLoadsTableView.getItems().clear();
            newValue.forEach(verticalPointLoad -> pointLoadsTableView.getItems().add(verticalPointLoad));
        };

        beamViewModel.verticalPointLoadsValue.value().forEach(verticalPointLoad -> pointLoadsTableView.getItems().add(verticalPointLoad));
        beamViewModel.verticalPointLoadsValue.addOnTryAppendValueListener(fillPointLoadsTableWithDataListener);
        TableColumn<VerticalPointLoad, Number> positionColumn = new TableColumn<>("Position (m)");
        positionColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().position().doubleValue()));
        TableColumn<VerticalPointLoad, Number> magnitudeColumn = new TableColumn<>("Magnitude (kN)");
        magnitudeColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().magnitude().doubleValue()));
        pointLoadsTableView.getColumns().add(positionColumn);
        pointLoadsTableView.getColumns().add(magnitudeColumn);

        var addPointLoadButton = new Button("Add");
        addPointLoadButton.setOnAction(event -> {
            BorderPane sceneRoot = new BorderPane();
            VBox center = new VBox(15);
            sceneRoot.setCenter(center);
            BorderPane.setMargin(center, new Insets(15));

            Stage stage = simpleStage(new Scene(sceneRoot) , "Add Point Load",380, 520);

            AppendableValue<Double> positionValue = positionAppendableValue();
            AppendableValue<Double> magnitudeValue = magnitudeAppendableValue();

            ErrorDoubleTextField positionTF = new ErrorDoubleTextField(positionValue);
            ErrorDoubleTextField magnitudeTF = new ErrorDoubleTextField(magnitudeValue);

            Button addButton = new Button("Add");
            addButton.setOnAction(addBtnEvent -> {
                var modified = new ArrayList<>(beamViewModel.verticalPointLoadsValue.value());
                modified.add(VerticalPointLoad.of(Position.of(positionValue.value()), Magnitude.of(magnitudeValue.value())));
                ValueErrors errors = beamViewModel.verticalPointLoadsValue.tryAppend(modified);
                if (!errors.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, errors.iterator().next(), ButtonType.OK).showAndWait();
                    return;
                }

                stage.close();
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(cancelBtnAction -> stage.close());

            center.getChildren().addAll(
                    positionTF.node(),
                    magnitudeTF.node()
            );

            VBox bottom = new VBox(
                    15,
                    new Separator(Orientation.HORIZONTAL),
                    new HBox(15, addButton, cancelButton)
            );
            sceneRoot.setBottom(bottom);
            BorderPane.setMargin(bottom, new Insets(15));
            BorderPane.setAlignment(bottom, Pos.CENTER_RIGHT);

            stage.showAndWait();
        });

        var removePointLoadButton = new Button("Remove");
        removePointLoadButton.setOnAction(event -> {
            ObservableList<VerticalPointLoad> selectedItems = pointLoadsTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }

            PointLoad pointLoad = selectedItems.getFirst();
            var modified = new ArrayList<>(beamViewModel.verticalPointLoadsValue.value());
            modified.remove(pointLoad);
            beamViewModel.verticalPointLoadsValue.tryAppend(modified);
        });

        ScrollPane tableViewScrollPane = new ScrollPane(pointLoadsTableView);
        tableViewScrollPane.setFitToHeight(true);
        tableViewScrollPane.setFitToWidth(true);
        tableViewScrollPane.setPrefWidth(Region.USE_COMPUTED_SIZE);

        var pointLoadsContent = new VBox(
                15,
                new HBox(15, addPointLoadButton, removePointLoadButton),
                new Separator(Orientation.HORIZONTAL),
                tableViewScrollPane
        );

        pointLoadsContent.setPadding(new Insets(15));
        pointLoadsTab.setContent(pointLoadsContent);


        // --- Bending Moments --- //
        Tab bendingMomentsTab = new Tab("Bending Moments");
        TableView<BendingMoment> bendingMomentsTableView = new TableView<>();
        bendingMomentsTableView.setEditable(false);
        bendingMomentsTableView.getSelectionModel().setCellSelectionEnabled(false);
        bendingMomentsTableView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        bendingMomentsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        AppendableValue.OnTryAppendValueListener<Collection<BendingMoment>> fillBendingMomentsTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            bendingMomentsTableView.getItems().clear();
            newValue.forEach(bendingMoment -> bendingMomentsTableView.getItems().add(bendingMoment));
        };

        beamViewModel.bendingMomentsValue.value().forEach(bendingMoment -> bendingMomentsTableView.getItems().add(bendingMoment));
        beamViewModel.bendingMomentsValue.addOnTryAppendValueListener(fillBendingMomentsTableWithDataListener);
        TableColumn<BendingMoment, Number> positionColumnBM = new TableColumn<>("Position (m)");
        positionColumnBM.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().position().doubleValue()));
        TableColumn<BendingMoment, Number> magnitudeColumnBM = new TableColumn<>("Magnitude (kN/m)");
        magnitudeColumnBM.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().magnitude().doubleValue()));
        bendingMomentsTableView.getColumns().add(positionColumnBM);
        bendingMomentsTableView.getColumns().add(magnitudeColumnBM);

        var addBendingMomentButton = new Button("Add");
        addBendingMomentButton.setOnAction(event -> {
            BorderPane sceneRoot = new BorderPane();
            VBox center = new VBox(15);
            sceneRoot.setCenter(center);
            BorderPane.setMargin(center, new Insets(15));

            Stage stage = simpleStage(new Scene(sceneRoot) , "Add Bending Moment",380, 520);

            AppendableValue<Double> positionValue = positionAppendableValue();
            AppendableValue<Double> magnitudeValue = magnitudeAppendableValue();

            ErrorDoubleTextField positionTF = new ErrorDoubleTextField(positionValue);
            ErrorDoubleTextField magnitudeTF = new ErrorDoubleTextField(magnitudeValue);

            Button addButton = new Button("Add");
            addButton.setOnAction(addBtnEvent -> {
                var modified = new ArrayList<>(beamViewModel.bendingMomentsValue.value());
                modified.add(BendingMoment.of(Position.of(positionValue.value()), Magnitude.of(magnitudeValue.value())));
                ValueErrors errors = beamViewModel.bendingMomentsValue.tryAppend(modified);
                if (!errors.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, errors.iterator().next(), ButtonType.OK).showAndWait();
                    return;
                }

                stage.close();
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(cancelBtnAction -> stage.close());

            center.getChildren().addAll(
                    positionTF.node(),
                    magnitudeTF.node()
            );

            VBox bottom = new VBox(
                    15,
                    new Separator(Orientation.HORIZONTAL),
                    new HBox(15, addButton, cancelButton)
            );
            sceneRoot.setBottom(bottom);
            BorderPane.setMargin(bottom, new Insets(15));
            BorderPane.setAlignment(bottom, Pos.CENTER_RIGHT);

            stage.showAndWait();
        });

        var removeBendingMomentButton = new Button("Remove");
        removeBendingMomentButton.setOnAction(event -> {
            ObservableList<BendingMoment> selectedItems = bendingMomentsTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            BendingMoment bendingMoment = selectedItems.getFirst();

            var modified = new ArrayList<>(beamViewModel.bendingMomentsValue.value());
            modified.remove(bendingMoment);
            beamViewModel.bendingMomentsValue.tryAppend(modified);
        });

        ScrollPane bendingMomentsScrollPane = new ScrollPane(bendingMomentsTableView);
        bendingMomentsScrollPane.setFitToHeight(true);
        bendingMomentsScrollPane.setFitToWidth(true);
        bendingMomentsScrollPane.setPrefWidth(Region.USE_COMPUTED_SIZE);

        var bendingMomentsContent = new VBox(
                15,
                new HBox(15, addBendingMomentButton, removeBendingMomentButton),
                new Separator(Orientation.HORIZONTAL),
                bendingMomentsScrollPane
        );

        bendingMomentsContent.setPadding(new Insets(15));
        bendingMomentsTab.setContent(bendingMomentsContent);


        // --- UDL --- //
        Tab udlTab = new Tab("UDL");
        TableView<UniformlyDistributedLoad> udlTableView = new TableView<>();
        udlTableView.setEditable(false);
        udlTableView.getSelectionModel().setCellSelectionEnabled(false);
        udlTableView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        udlTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        AppendableValue.OnTryAppendValueListener<Collection<UniformlyDistributedLoad>> fillUDLTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            udlTableView.getItems().clear();
            newValue.forEach(bendingMoment -> udlTableView.getItems().add(bendingMoment));
        };

        beamViewModel.uniformlyDistributedLoadsValue.value().forEach(bendingMoment -> udlTableView.getItems().add(bendingMoment));
        beamViewModel.uniformlyDistributedLoadsValue.addOnTryAppendValueListener(fillUDLTableWithDataListener);
        TableColumn<UniformlyDistributedLoad, Number> startPositionColumnUDL = new TableColumn<>("Start position (m)");
        positionColumnBM.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().position().doubleValue()));
        TableColumn<UniformlyDistributedLoad, Number> endPositionColumnUDL = new TableColumn<>("End position (m)");
        positionColumnBM.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().position().doubleValue()));
        TableColumn<UniformlyDistributedLoad, Number> magnitudeColumnUDL = new TableColumn<>("Magnitude (kN/m)");
        magnitudeColumnBM.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().magnitude().doubleValue()));
        udlTableView.getColumns().add(startPositionColumnUDL);
        udlTableView.getColumns().add(endPositionColumnUDL);
        udlTableView.getColumns().add(magnitudeColumnUDL);

        var addUDLButton = new Button("Add");
        addUDLButton.setOnAction(event -> {
            BorderPane sceneRoot = new BorderPane();
            VBox center = new VBox(15);
            sceneRoot.setCenter(center);
            BorderPane.setMargin(center, new Insets(15));

            Stage stage = simpleStage(new Scene(sceneRoot) , "Add Bending Moment",380, 520);

            AppendableValue<Double> startPositionValue = positionAppendableValue();
            AppendableValue<Double> endPositionValue = positionAppendableValue(beamViewModel.beamLengthValue.value());
            AppendableValue<Double> magnitudeValue = magnitudeAppendableValue();

            ErrorDoubleTextField startPositionTF = new ErrorDoubleTextField(startPositionValue);
            ErrorDoubleTextField endPositionTF = new ErrorDoubleTextField(endPositionValue);
            ErrorDoubleTextField magnitudeTF = new ErrorDoubleTextField(magnitudeValue);

            Button addButton = new Button("Add");
            addButton.setOnAction(addBtnEvent -> {
                var modified = new ArrayList<>(beamViewModel.uniformlyDistributedLoadsValue.value());
                modified.add(UniformlyDistributedLoad.of(
                        Position.of(startPositionValue.value()),
                        Position.of(endPositionValue.value()),
                        Magnitude.of(magnitudeValue.value())
                ));
                ValueErrors errors = beamViewModel.uniformlyDistributedLoadsValue.tryAppend(modified);
                if (!errors.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, errors.iterator().next(), ButtonType.OK).showAndWait();
                    return;
                }

                stage.close();
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(cancelBtnAction -> stage.close());

            center.getChildren().addAll(
                    startPositionTF.node(),
                    endPositionTF.node(),
                    magnitudeTF.node()
            );

            VBox bottom = new VBox(
                    15,
                    new Separator(Orientation.HORIZONTAL),
                    new HBox(15, addButton, cancelButton)
            );
            sceneRoot.setBottom(bottom);
            BorderPane.setMargin(bottom, new Insets(15));
            BorderPane.setAlignment(bottom, Pos.CENTER_RIGHT);

            stage.showAndWait();
        });

        var removeUDLButton = new Button("Remove");
        removeUDLButton.setOnAction(event -> {
            ObservableList<UniformlyDistributedLoad> selectedItems = udlTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            UniformlyDistributedLoad udl = selectedItems.getFirst();

            var modified = new ArrayList<>(beamViewModel.uniformlyDistributedLoadsValue.value());
            modified.remove(udl);
            beamViewModel.uniformlyDistributedLoadsValue.tryAppend(modified);
        });

        ScrollPane udlScrollPane = new ScrollPane(udlTableView);
        udlScrollPane.setFitToHeight(true);
        udlScrollPane.setFitToWidth(true);
        udlScrollPane.setPrefWidth(Region.USE_COMPUTED_SIZE);

        var udlContent = new VBox(
                15,
                new HBox(15, addUDLButton, removeUDLButton),
                new Separator(Orientation.HORIZONTAL),
                udlScrollPane
        );

        udlContent.setPadding(new Insets(15));
        udlTab.setContent(udlContent);


        // --- TABS --- //
        TabPane loadingTabPane = new TabPane(pointLoadsTab, bendingMomentsTab, udlTab);
        loadingTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        var content = new VBox(
                15,
                loadingTabPane,
                new Separator(Orientation.HORIZONTAL),
                buttonBox
        );

        content.setFillWidth(true);
        content.setPrefWidth(Region.USE_COMPUTED_SIZE);
        content.setPadding(new Insets(15));

        Stage stage = simpleStage(new Scene(content), "Loading Configuration", 580, 720);

        okButton.setOnAction(actionEvent -> {
            beamViewModel.verticalPointLoadsValue.removeOnTryAppendValueListener(fillPointLoadsTableWithDataListener);
            beamViewModel.bendingMomentsValue.removeOnTryAppendValueListener(fillBendingMomentsTableWithDataListener);
            beamViewModel.uniformlyDistributedLoadsValue.removeOnTryAppendValueListener(fillUDLTableWithDataListener);
            beamView.displayLoading();
            stage.close();
        });

        beamView.hideDiagram();
        stage.setOnCloseRequest(event -> okButton.getOnAction().handle(new ActionEvent()));

        Platform.runLater(okButton::requestFocus);
        stage.showAndWait();
    }

    private AppendableValue<Double> magnitudeAppendableValue() {
        return new AppendableValue<>(0d, "Magnitude (kN)") {
            @Override
            protected ValueErrors validateNewValue(Double newValue) {
                return ValueErrors.empty();
            }
        };
    }

    private AppendableValue<Double> positionAppendableValue(double defaultValue) {
        return new AppendableValue<>(defaultValue, "Position (m)") {
            @Override
            protected ValueErrors validateNewValue(Double newValue) {
                List<String> errors = new ArrayList<>(2);
                if ((newValue < 0) || (newValue > beamViewModel.beamLengthValue.value())) {
                    errors.add("Position must be in range of the beam");
                }
                return new ValueErrors(errors);
            }
        };
    }

    private AppendableValue<Double> positionAppendableValue() {
        return positionAppendableValue(0d);
    }

    public void onShowInternalForcesRequest(ActionEvent ignored) {
        BorderPane pane = new BorderPane();
        pane.setMinWidth(200);
        pane.setMinWidth(200);
        pane.setMinWidth(200);

        VBox vBox = new VBox(5);
        pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("whiteSmoke"), new CornerRadii(0), new Insets(0))));
        pane.setBorder(new Border(new BorderStroke(Paint.valueOf("lightGray"), BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 1))));
        pane.setPadding(new Insets(5));

        Label header = new Label("Internal Forces");
        header.setStyle("-fx-font-size: 18; -fx-text-fill: SteelBlue;");
        Separator separator = new Separator(Orientation.HORIZONTAL);
        HBox headerHBox = new HBox(5, header, separator);
        headerHBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(separator, Priority.ALWAYS);

        pane.setTop(vBox);
        vBox.getChildren().addAll(headerHBox);
        StackPane.setAlignment(vBox, Pos.TOP_CENTER);

        borderPane.setRight(pane);
        StackPane.setAlignment(pane, Pos.CENTER_RIGHT);

        LoadingAnalysis loadingAnalysis = loadingAnalysis();
        BendingMomentDiagram bendingMomentDiagram = loadingAnalysis.bendingMomentDiagram();
        SheerForceDiagram sheerForceDiagram = loadingAnalysis.sheerForceDiagram();

        XYChart.Series<Number, Number> bendingMomentSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> sheerForceSeries = new XYChart.Series<>();
        bendingMomentSeries.setName("Bending Moment");
        sheerForceSeries.setName("Sheer Force");
        for (Map.Entry<Position, Magnitude> entry : bendingMomentDiagram) {
            bendingMomentSeries.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }
        for (Map.Entry<Position, Magnitude> entry : sheerForceDiagram) {
            sheerForceSeries.getData().add(new XYChart.Data<>(entry.getKey().doubleValue(), entry.getValue().doubleValue()));
        }

        Map<String, Runnable> internalForcesToActionMap = new LinkedHashMap<>();
        internalForcesToActionMap.put("Sheer forces", () -> beamView.displayDiagram(sheerForceSeries));
        internalForcesToActionMap.put("Bending moments", () -> beamView.displayDiagram(bendingMomentSeries));

        ComboBox<String> internalForcesComboBox = new ComboBox<>();
        internalForcesComboBox.getItems().addAll(internalForcesToActionMap.keySet());
        internalForcesComboBox.setOnAction(event -> {
            internalForcesToActionMap.getOrDefault(
                    internalForcesComboBox.getSelectionModel().getSelectedItem(),
                    () -> beamView.hideDiagram()
            ).run();
        });

        CheckBox loadingVisibleCheckbox = new CheckBox("Loading visible");
        loadingVisibleCheckbox.setSelected(true);
        loadingVisibleCheckbox.setOnAction(event -> {
            if (loadingVisibleCheckbox.isSelected()) {
                beamView.displayLoading();
            } else {
                beamView.hideLoading();
            }
        });

        Button endPreviewButton = new Button("End preview");
        endPreviewButton.setOnAction(event -> {
            menuBar.setDisable(false);
            borderPane.setRight(null);
            beamView.hideDiagram();
            beamView.displayLoading();
        });

        pane.setBottom(new VBox(5, new Separator(Orientation.HORIZONTAL), endPreviewButton));
        BorderPane.setAlignment(endPreviewButton, Pos.CENTER);
        endPreviewButton.setMinWidth(200);

        VBox content = new VBox(10, loadingVisibleCheckbox, internalForcesComboBox);
        VBox.setMargin(content, new Insets(0, 0 , 0 , 15));

        vBox.getChildren().addAll(content);
        menuBar.setDisable(true);

        Platform.runLater(() -> {
            internalForcesComboBox.getSelectionModel().select(internalForcesToActionMap.keySet().stream().findFirst().get());
            endPreviewButton.requestFocus();
        });
    }
}
