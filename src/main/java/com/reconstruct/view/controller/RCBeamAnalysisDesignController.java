package com.reconstruct.view.controller;

import com.reconstruct.model.beam.*;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.distributed.UniformlyDistributedLoad;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.PointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.section.RectangularSection;
import com.reconstruct.model.beam.span.Span;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.value.Length;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.model.value.PositiveDouble;
import com.reconstruct.view.component.*;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class RCBeamAnalysisDesignController {
    @FXML public BorderPane borderPane;
    @FXML public StackPane centerPane;
    @FXML public MenuBar menuBar;

    private BeamView beamView;
    private final SimplySupportedBeamViewModel beamViewModel = new SimplySupportedBeamViewModel();
    private final RectangularSectionViewModel rectangularSectionViewModel = new RectangularSectionViewModel();

    @FXML
    public void initialize() {
        beamView = new BeamView(1000, 500, beamViewModel);
        Node beamViewNode = beamView.node();
        centerPane.getChildren().add(beamViewNode);
        StackPane.setAlignment(beamViewNode, Pos.CENTER);
        beamView.refreshGeometry();
    }

    private Supplier<LoadingAnalysis> loadingAnalysisSupplier = () -> {
        Loading loading = new Loading(
                beamViewModel.verticalPointLoadsProperty.value(),
                beamViewModel.horizontalPointLoadsProperty.value(),
                beamViewModel.bendingMomentsProperty.value(),
                beamViewModel.uniformlyDistributedLoadsProperty.value()
        );

        double length = beamViewModel.beamLengthProperty.value();
        Position pinnedPosition = Position.of(beamViewModel.pinnedSupportPositionProperty.value());
        Position rollerPosition = Position.of(beamViewModel.rollerSupportPositionProperty.value());

        SimplySupportedBeam simplySupportedBeam = SimplySupportedBeam.withCustomSupportPositions(
                new Span(
                        Length.of(length),
                        new RectangularSection(
                                PositiveDouble.of(10),
                                PositiveDouble.of(10)
                        )
                ), pinnedPosition, rollerPosition
        );

        return simplySupportedBeam.loadingAnalysis(loading);
    };

    public void onGeometryConfiguration(ActionEvent ignored) {
        var beamLengthProperty = beamViewModel.beamLengthProperty;
        var pinnedSupportPositionProperty = beamViewModel.pinnedSupportPositionProperty;
        var rollerSupportPositionProperty = beamViewModel.rollerSupportPositionProperty;
        var sectionDepthProperty = rectangularSectionViewModel.depthProperty;
        var sectionWidthProperty = rectangularSectionViewModel.widthProperty;

        var beamLengthPropertyMemento = beamLengthProperty.value();
        var pinnedSupportPositionPropertyMemento = pinnedSupportPositionProperty.value();
        var rollerSupportPositionPropertyMemento = rollerSupportPositionProperty.value();
        var sectionDepthPropertyMemento = sectionDepthProperty.value();
        var sectionWidthPropertyMemento = sectionWidthProperty.value();

        var beamLengthTF = new ErrorDoubleTextField(beamLengthProperty);
        var pinnedSuppTF = new ErrorDoubleTextField(pinnedSupportPositionProperty);
        var rollerSuppTF = new ErrorDoubleTextField(rollerSupportPositionProperty);
        var sectionDepthTF = new ErrorDoubleTextField(sectionDepthProperty);
        var sectionWidthTF = new ErrorDoubleTextField(sectionWidthProperty);

        double prefButtonWidth = 75d;
        var saveButton = new Button("Save");
        saveButton.setPrefWidth(prefButtonWidth);

        var cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(prefButtonWidth);

        AppendableProperty.OnTryAppendValueListener<Double> listener = (oldValue, newValue, valueErrors) -> {
            saveButton.setDisable(!valueErrors.isEmpty());
        };

        beamLengthProperty.addOnTryAppendValueListener(listener);
        pinnedSupportPositionProperty.addOnTryAppendValueListener(listener);
        rollerSupportPositionProperty.addOnTryAppendValueListener(listener);
        sectionDepthProperty.addOnTryAppendValueListener(listener);
        sectionWidthProperty.addOnTryAppendValueListener(listener);

        var propertiesVBox = new VBox(15, beamLengthTF.node(), pinnedSuppTF.node(), rollerSuppTF.node());
        propertiesVBox.setMaxWidth(300);

        var sectionVBox = new VBox(15, sectionDepthTF.node(), sectionWidthTF.node());
        var sectionHBox = new HBox(45);
        StackPane sectionStackPane = new StackPane();
        sectionStackPane.setMaxSize(120, 200);
        sectionStackPane.setMinSize(120, 200);
        sectionStackPane.setPrefSize(120, 200);
        Rectangle sectionRectangle = new Rectangle(120, 200);
        sectionRectangle.setFill(Color.LIGHTGRAY);
        sectionRectangle.setStroke(Color.BLACK);
        Label widthLabel = new Label("width");
        Label depthLabel = new Label("depth");

        sectionStackPane.getChildren().addAll(sectionRectangle, widthLabel, depthLabel);
        StackPane.setAlignment(sectionRectangle, Pos.CENTER);
        StackPane.setAlignment(widthLabel, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(depthLabel, Pos.CENTER_LEFT);

        depthLabel.setTranslateX(-15 - depthLabel.getFont().getSize());
        depthLabel.setRotate(-90);
        widthLabel.setTranslateY(15 + widthLabel.getFont().getSize() / 2);

        sectionHBox.getChildren().addAll(sectionVBox, sectionStackPane);

        HBox buttons = new HBox(15, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane content = new BorderPane();
        content.setCenter(new HBox(15, propertiesVBox, new Separator(Orientation.VERTICAL), sectionHBox));
        content.setBottom(new VBox(15, new Separator(Orientation.HORIZONTAL), buttons));
        content.setPadding(new Insets(15));

        BorderPane.setAlignment(propertiesVBox, Pos.TOP_LEFT);
        BorderPane.setAlignment(buttons, Pos.CENTER_RIGHT);
        Stage stage = new FixedSizeStage(new Scene(content), "Geometry configuration", 380, 580, Modality.APPLICATION_MODAL);

        Runnable commonOnCloseAction = () -> {
            beamLengthProperty.removeOnTryAppendValueListener(listener);
            pinnedSupportPositionProperty.removeOnTryAppendValueListener(listener);
            rollerSupportPositionProperty.removeOnTryAppendValueListener(listener);
            sectionDepthProperty.removeOnTryAppendValueListener(listener);
            sectionWidthProperty.removeOnTryAppendValueListener(listener);
            beamView.refreshGeometry();
            beamView.displayLoading();
            stage.close();
        };

        Runnable restoreValuesAction = () -> {
            beamLengthProperty.tryAppend(beamLengthPropertyMemento);
            rollerSupportPositionProperty.tryAppend(rollerSupportPositionPropertyMemento);
            pinnedSupportPositionProperty.tryAppend(pinnedSupportPositionPropertyMemento);
            sectionDepthProperty.tryAppend(sectionDepthPropertyMemento);
            sectionWidthProperty.tryAppend(sectionWidthPropertyMemento);
        };

        saveButton.setOnAction(event -> commonOnCloseAction.run());
        cancelButton.setOnAction(event -> {
            restoreValuesAction.run();
            commonOnCloseAction.run();
        });

        beamView.hideDiagram();
        stage.setOnCloseRequest(event -> cancelButton.getOnAction().handle(new ActionEvent()));

        Platform.runLater(saveButton::requestFocus);
        new RCWindow(stage).showAndWait();
    }

    public void onLoadingConfiguration(ActionEvent ignored) {

        // --- Point Loads ---- //
        Tab pointLoadsTab = new Tab("Point Loads");
        TableView<VerticalPointLoad> pointLoadsTableView = new TableView<>();
        pointLoadsTableView.setEditable(false);
        pointLoadsTableView.getSelectionModel().setCellSelectionEnabled(false);
        pointLoadsTableView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        pointLoadsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        AppendableProperty.OnTryAppendValueListener<Collection<VerticalPointLoad>> fillPointLoadsTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            pointLoadsTableView.getItems().clear();
            newValue.forEach(verticalPointLoad -> pointLoadsTableView.getItems().add(verticalPointLoad));
        };

        beamViewModel.verticalPointLoadsProperty.value().forEach(verticalPointLoad -> pointLoadsTableView.getItems().add(verticalPointLoad));
        beamViewModel.verticalPointLoadsProperty.addOnTryAppendValueListener(fillPointLoadsTableWithDataListener);
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

            Stage stage = new FixedSizeStage(new Scene(sceneRoot) , "Add Point Load",380, 520, Modality.APPLICATION_MODAL);

            AppendableProperty<Double> positionValue = positionAppendableValue();
            AppendableProperty<Double> magnitudeValue = magnitudeAppendableValue("kN");

            ErrorDoubleTextField positionTF = new ErrorDoubleTextField(positionValue);
            ErrorDoubleTextField magnitudeTF = new ErrorDoubleTextField(magnitudeValue);

            Button addButton = new Button("Add");
            addButton.setOnAction(addBtnEvent -> {
                var modified = new ArrayList<>(beamViewModel.verticalPointLoadsProperty.value());
                modified.add(VerticalPointLoad.of(Position.of(positionValue.value()), Magnitude.of(magnitudeValue.value())));
                PropertyErrors errors = beamViewModel.verticalPointLoadsProperty.tryAppend(modified);
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

            new RCWindow(stage).showAndWait();
        });

        var removePointLoadButton = new Button("Remove");
        removePointLoadButton.setOnAction(event -> {
            ObservableList<VerticalPointLoad> selectedItems = pointLoadsTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }

            PointLoad pointLoad = selectedItems.getFirst();
            var modified = new ArrayList<>(beamViewModel.verticalPointLoadsProperty.value());
            modified.remove(pointLoad);
            beamViewModel.verticalPointLoadsProperty.tryAppend(modified);
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
        AppendableProperty.OnTryAppendValueListener<Collection<BendingMoment>> fillBendingMomentsTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            bendingMomentsTableView.getItems().clear();
            newValue.forEach(bendingMoment -> bendingMomentsTableView.getItems().add(bendingMoment));
        };

        beamViewModel.bendingMomentsProperty.value().forEach(bendingMoment -> bendingMomentsTableView.getItems().add(bendingMoment));
        beamViewModel.bendingMomentsProperty.addOnTryAppendValueListener(fillBendingMomentsTableWithDataListener);
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

            Stage stage = new FixedSizeStage(new Scene(sceneRoot) , "Add Bending Moment",380, 520, Modality.APPLICATION_MODAL);

            AppendableProperty<Double> positionValue = positionAppendableValue();
            AppendableProperty<Double> magnitudeValue = magnitudeAppendableValue("kN/m");

            ErrorDoubleTextField positionTF = new ErrorDoubleTextField(positionValue);
            ErrorDoubleTextField magnitudeTF = new ErrorDoubleTextField(magnitudeValue);

            Button addButton = new Button("Add");
            addButton.setOnAction(addBtnEvent -> {
                var modified = new ArrayList<>(beamViewModel.bendingMomentsProperty.value());
                modified.add(BendingMoment.of(Position.of(positionValue.value()), Magnitude.of(magnitudeValue.value())));
                PropertyErrors errors = beamViewModel.bendingMomentsProperty.tryAppend(modified);
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

            new RCWindow(stage).showAndWait();
        });

        var removeBendingMomentButton = new Button("Remove");
        removeBendingMomentButton.setOnAction(event -> {
            ObservableList<BendingMoment> selectedItems = bendingMomentsTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            BendingMoment bendingMoment = selectedItems.getFirst();

            var modified = new ArrayList<>(beamViewModel.bendingMomentsProperty.value());
            modified.remove(bendingMoment);
            beamViewModel.bendingMomentsProperty.tryAppend(modified);
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
        AppendableProperty.OnTryAppendValueListener<Collection<UniformlyDistributedLoad>> fillUDLTableWithDataListener = (oldValue, newValue, errors) -> {
            if (!errors.isEmpty()) {
                return;
            }

            udlTableView.getItems().clear();
            newValue.forEach(udl -> udlTableView.getItems().add(udl));
        };

        beamViewModel.uniformlyDistributedLoadsProperty.value().forEach(udl -> udlTableView.getItems().add(udl));
        beamViewModel.uniformlyDistributedLoadsProperty.addOnTryAppendValueListener(fillUDLTableWithDataListener);
        TableColumn<UniformlyDistributedLoad, Number> startPositionColumnUDL = new TableColumn<>("Start position (m)");
        startPositionColumnUDL.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().startPosition().doubleValue()));
        TableColumn<UniformlyDistributedLoad, Number> endPositionColumnUDL = new TableColumn<>("End position (m)");
        endPositionColumnUDL.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().endPosition().doubleValue()));
        TableColumn<UniformlyDistributedLoad, Number> magnitudeColumnUDL = new TableColumn<>("Magnitude (kN/m)");
        magnitudeColumnUDL.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().magnitude().doubleValue()));
        udlTableView.getColumns().add(startPositionColumnUDL);
        udlTableView.getColumns().add(endPositionColumnUDL);
        udlTableView.getColumns().add(magnitudeColumnUDL);

        var addUDLButton = new Button("Add");
        addUDLButton.setOnAction(event -> {
            BorderPane sceneRoot = new BorderPane();
            VBox center = new VBox(15);
            sceneRoot.setCenter(center);
            BorderPane.setMargin(center, new Insets(15));

            Stage stage = new FixedSizeStage(new Scene(sceneRoot) , "Add Bending Moment",380, 520, Modality.APPLICATION_MODAL);

            AppendableProperty<Double> startPositionValue = positionAppendableValue();
            AppendableProperty<Double> endPositionValue = positionAppendableValue(beamViewModel.beamLengthProperty.value());
            AppendableProperty<Double> magnitudeValue = magnitudeAppendableValue("kN/m");

            ErrorDoubleTextField startPositionTF = new ErrorDoubleTextField(startPositionValue);
            ErrorDoubleTextField endPositionTF = new ErrorDoubleTextField(endPositionValue);
            ErrorDoubleTextField magnitudeTF = new ErrorDoubleTextField(magnitudeValue);

            Button addButton = new Button("Add");
            addButton.setOnAction(addBtnEvent -> {
                var modified = new ArrayList<>(beamViewModel.uniformlyDistributedLoadsProperty.value());
                modified.add(UniformlyDistributedLoad.of(
                        Position.of(startPositionValue.value()),
                        Position.of(endPositionValue.value()),
                        Magnitude.of(magnitudeValue.value())
                ));
                PropertyErrors errors = beamViewModel.uniformlyDistributedLoadsProperty.tryAppend(modified);
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

            new RCWindow(stage).showAndWait();
        });

        var removeUDLButton = new Button("Remove");
        removeUDLButton.setOnAction(event -> {
            ObservableList<UniformlyDistributedLoad> selectedItems = udlTableView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            UniformlyDistributedLoad udl = selectedItems.getFirst();

            var modified = new ArrayList<>(beamViewModel.uniformlyDistributedLoadsProperty.value());
            modified.remove(udl);
            beamViewModel.uniformlyDistributedLoadsProperty.tryAppend(modified);
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

        double prefButtonWidth = 75d;
        var saveButton = new Button("Save");
        saveButton.setPrefWidth(prefButtonWidth);

        var cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(prefButtonWidth);

        HBox buttonBox = new HBox(15, saveButton, cancelButton);
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
        Stage stage = new FixedSizeStage(new Scene(content), "Loading Configuration", 580, 720, Modality.APPLICATION_MODAL);


        var verticalPointLoadsValueMemento = beamViewModel.verticalPointLoadsProperty.value();
        var bendingMomentsValueMemento = beamViewModel.bendingMomentsProperty.value();
        var udlValueMemento = beamViewModel.uniformlyDistributedLoadsProperty.value();

        Runnable commonOnCloseAction = () -> {
            beamViewModel.verticalPointLoadsProperty.removeOnTryAppendValueListener(fillPointLoadsTableWithDataListener);
            beamViewModel.bendingMomentsProperty.removeOnTryAppendValueListener(fillBendingMomentsTableWithDataListener);
            beamViewModel.uniformlyDistributedLoadsProperty.removeOnTryAppendValueListener(fillUDLTableWithDataListener);
            beamView.displayLoading();
            stage.close();
        };

        Runnable restoreValuesAction = () -> {
            beamViewModel.verticalPointLoadsProperty.tryAppend(verticalPointLoadsValueMemento);
            beamViewModel.bendingMomentsProperty.tryAppend(bendingMomentsValueMemento);
            beamViewModel.uniformlyDistributedLoadsProperty.tryAppend(udlValueMemento);
        };

        saveButton.setOnAction(event -> commonOnCloseAction.run());
        cancelButton.setOnAction(event -> {
            restoreValuesAction.run();
            commonOnCloseAction.run();
        });

        beamView.hideDiagram();
        stage.setOnCloseRequest(event -> cancelButton.getOnAction().handle(new ActionEvent()));

        Platform.runLater(saveButton::requestFocus);
        new RCWindow(stage).showAndWait();
    }

    private AppendableProperty<Double> magnitudeAppendableValue(String magnitudeUnit) {
        return new AppendableProperty<>(0d, String.format("Magnitude (%s)", magnitudeUnit)) {
            @Override
            protected PropertyErrors validateNewValue(Double newValue) {
                return PropertyErrors.empty();
            }
        };
    }

    private AppendableProperty<Double> positionAppendableValue(double defaultValue) {
        return new AppendableProperty<>(defaultValue, "Position (m)") {
            @Override
            protected PropertyErrors validateNewValue(Double newValue) {
                List<String> errors = new ArrayList<>(2);
                if ((newValue < 0) || (newValue > beamViewModel.beamLengthProperty.value())) {
                    errors.add("Position must be in range of the beam");
                }
                return new PropertyErrors(errors);
            }
        };
    }

    private AppendableProperty<Double> positionAppendableValue() {
        return positionAppendableValue(0d);
    }

    public void onResultsInternalForces(ActionEvent ignored) {
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

        LoadingAnalysis loadingAnalysis = loadingAnalysisSupplier.get();
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

        CheckBox supportReactionsVisibleCheckbox = new CheckBox("Support reactions visible");
        CheckBox loadingVisibleCheckbox = new CheckBox("Loading visible");
        VBox content = new VBox(10, loadingVisibleCheckbox, supportReactionsVisibleCheckbox, internalForcesComboBox);

        loadingVisibleCheckbox.setSelected(true);
        loadingVisibleCheckbox.setOnAction(event -> {
            if (loadingVisibleCheckbox.isSelected()) {
                content.getChildren().add(1, supportReactionsVisibleCheckbox);
                beamView.displayLoading(loadingAnalysis);
            } else {
                content.getChildren().remove(supportReactionsVisibleCheckbox);
                beamView.hideLoading();
            }
        });

        supportReactionsVisibleCheckbox.setSelected(true);
        supportReactionsVisibleCheckbox.setOnAction(event -> {
            if (!loadingVisibleCheckbox.isSelected()) {
                return;
            }
            if (supportReactionsVisibleCheckbox.isSelected()) {
                beamView.displayLoading(loadingAnalysis);
            } else {
                beamView.displayLoading();
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

        VBox.setMargin(content, new Insets(0, 0 , 0 , 15));

        vBox.getChildren().addAll(content);
        menuBar.setDisable(true);

        beamView.displayLoading(loadingAnalysis);
        Platform.runLater(() -> {
            internalForcesComboBox.getSelectionModel().select(internalForcesToActionMap.keySet().stream().findFirst().get());
            endPreviewButton.requestFocus();
        });
    }

    public void onResultsReinforcement(ActionEvent ignored) {
        new ReinforcementAnalysisWindow(new RectangularSection(
                PositiveDouble.of(rectangularSectionViewModel.widthProperty.value()),
                PositiveDouble.of(rectangularSectionViewModel.depthProperty.value())
        ), Length.of(beamViewModel.beamLengthProperty.value())).show(loadingAnalysisSupplier);
    }
}
