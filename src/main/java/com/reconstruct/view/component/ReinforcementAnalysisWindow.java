package com.reconstruct.view.component;

import com.reconstruct.model.beam.BeamReinforcementAnalysis;
import com.reconstruct.model.beam.BendingMomentDiagram;
import com.reconstruct.model.beam.ConcreteGrade;
import com.reconstruct.model.beam.ReinforcementMaterialGrade;
import com.reconstruct.model.beam.section.RectangularSection;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.model.standard.PN02;
import com.reconstruct.model.value.Magnitude;
import com.reconstruct.view.viewmodel.AppendableProperty;
import com.reconstruct.view.viewmodel.PositiveDoubleAppendableProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReinforcementAnalysisWindow {
    private final RectangularSection rectangularSection;

    public ReinforcementAnalysisWindow(RectangularSection rectangularSection) {
        this.rectangularSection = rectangularSection;
    }

    public void show() {
        BorderPane content = new BorderPane();
        VBox propertiesVBox = new VBox(15);
        content.setCenter(propertiesVBox);

        AppendableProperty<Double> minCorrosionCoverThicknessProperty = new PositiveDoubleAppendableProperty(25d);
        ErrorDoubleTextField minCorrosionCoverThicknessTF = new ErrorDoubleTextField(
                minCorrosionCoverThicknessProperty,
                new SimpleTextFlowBuilder().addRegularText("Minimal corrosion cover thickness c").addSubscriptText("min").addRegularText(" (mm)").build()
        );
        Button selectMinCorrosionCoverThickness = new Button(". . .");
        selectMinCorrosionCoverThickness.setOnAction(event -> {
            BorderPane localContent = new BorderPane();

            ListView<Map.Entry<String, Double>> listView = new ListView<>();
            listView.setCellFactory(new Callback<>() {
                @Override
                public javafx.scene.control.ListCell<Map.Entry<String, Double>> call(javafx.scene.control.ListView<Map.Entry<String, Double>> param) {
                    return new javafx.scene.control.ListCell<>() {
                        @Override
                        protected void updateItem(Map.Entry<String, Double> item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                            } else {
                                // Format the text as "SomeText - 45"
                                setText(item.getKey() + " - " + item.getValue().intValue() + " (mm)");
                            }
                        }
                    };
                }
            });
            listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            Map<String, Runnable> standardComboBoxActionMap = new LinkedHashMap<>();
            standardComboBoxActionMap.put("PN-02 - Prestressed steel", () -> listView.setItems(FXCollections.observableList(PN02.MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.entrySet().stream().toList())));
            standardComboBoxActionMap.put("PN-02 - Standard steel", () -> listView.setItems(FXCollections.observableList(PN02.MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.entrySet().stream().toList())));

            ComboBox<String> standardComboBox = new ComboBox<>();
            standardComboBox.setPrefWidth(Double.MAX_VALUE);
            standardComboBox.getItems().addAll(standardComboBoxActionMap.keySet());
            standardComboBox.setOnAction(e -> {
                standardComboBoxActionMap.getOrDefault(
                        standardComboBox.getSelectionModel().getSelectedItem(),
                        () -> { /* do nothing */ }
                ).run();
            });

            Stage localStage = new FixedSizeStage(new Scene(localContent), "Minimal corrosion cover thickness", 580, 460, Modality.APPLICATION_MODAL);
            localContent.setTop(standardComboBox);
            localContent.setCenter(listView);
            SaveCancelButtonPanel saveCancelButtonPanel = new SaveCancelButtonPanel(
                    onSave -> {
                        minCorrosionCoverThicknessTF.setText(listView.getSelectionModel().getSelectedItem().getValue().toString());
                        localStage.hide();
                    },
                    onCancel -> localStage.hide()
            );
            localContent.setBottom(saveCancelButtonPanel.node());
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> saveCancelButtonPanel.saveButtonDisabled(newValue == null));
            saveCancelButtonPanel.saveButtonDisabled(true);

            localContent.setPadding(new Insets(15));
            var insets = new Insets(0, 0, 15, 0);
            BorderPane.setMargin(localContent.getTop(), insets);
            BorderPane.setMargin(localContent.getCenter(), insets);

            Platform.runLater(() -> {
                standardComboBox.getSelectionModel().select(standardComboBoxActionMap.keySet().stream().findFirst().get());
                saveCancelButtonPanel.requestFocus();
            });
            new RCWindow(localStage).showAndWait();
        });
        HBox minCorrosionCoverThicknessHBox = new HBox(5, minCorrosionCoverThicknessTF.node(), selectMinCorrosionCoverThickness);
        propertiesVBox.getChildren().add(minCorrosionCoverThicknessHBox);
        HBox.setHgrow(minCorrosionCoverThicknessTF.node(), Priority.ALWAYS);
        minCorrosionCoverThicknessHBox.setAlignment(Pos.BOTTOM_CENTER);

        AppendableProperty<Double> corrosionCoverToleranceProperty = new PositiveDoubleAppendableProperty(5d);
        ErrorDoubleTextField corrosionCoverToleranceTF = new ErrorDoubleTextField(
                corrosionCoverToleranceProperty,
                new SimpleTextFlowBuilder().addRegularText("Corrosion cover tolerance Δc (mm)").build()
        );
        propertiesVBox.getChildren().add(corrosionCoverToleranceTF.node());

        AppendableProperty<Double> diameterOfReinforcementBarProperty = new PositiveDoubleAppendableProperty(25d);
        ErrorDoubleTextField diameterOfReinforcementBarTF = new ErrorDoubleTextField(
                diameterOfReinforcementBarProperty,
                new SimpleTextFlowBuilder().addRegularText("Diameter of reinforcement bar Φ (mm)").build()
        );
        propertiesVBox.getChildren().add(diameterOfReinforcementBarTF.node());

        AppendableProperty<Double> diameterOfReinforcementStirrupProperty = new PositiveDoubleAppendableProperty(8d);
        ErrorDoubleTextField diameterOfReinforcementStirrupTF = new ErrorDoubleTextField(
                diameterOfReinforcementStirrupProperty,
                new SimpleTextFlowBuilder().addRegularText("Diameter of reinforcement stirrup Φ").addSubscriptText("s").addRegularText(" (mm)").build()
        );
        propertiesVBox.getChildren().add(diameterOfReinforcementStirrupTF.node());

        propertiesVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));

        List<ConcreteGrade> concreteGrades = PN02.CONCRETE_GRADE;
        ComboBox<ConcreteGrade> concreteGradeComboBox = new ComboBox<>();
        concreteGradeComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ConcreteGrade> call(ListView<ConcreteGrade> concreteGradeListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ConcreteGrade concreteGrade, boolean empty) {
                        super.updateItem(concreteGrade, empty);
                        if (concreteGrade == null || empty) {
                            setText("");
                        } else {
                            setText(concreteGrade.name());
                        }
                    }
                };
            }
        });
        BorderPane concreteGradeBP = new BorderPane();
        concreteGradeBP.setLeft(new Label("Concrete grade:"));
        BorderPane.setAlignment(concreteGradeBP.getLeft(), Pos.CENTER_LEFT);
        BorderPane.setMargin(concreteGradeBP.getLeft(), new Insets(0, 5, 0, 0));
        concreteGrades.forEach(concreteGrade -> concreteGradeComboBox.getItems().add(concreteGrade));
        concreteGradeComboBox.setPrefWidth(Double.MAX_VALUE);
        concreteGradeBP.setCenter(concreteGradeComboBox);
        BorderPane.setAlignment(concreteGradeBP.getCenter(), Pos.CENTER_LEFT);
        propertiesVBox.getChildren().add(concreteGradeBP);

        List<ReinforcementMaterialGrade> steelGrades = PN02.STEEL_GRADES;
        ComboBox<ReinforcementMaterialGrade> reinforcementSteelGradeComboBox = new ComboBox<>();
        reinforcementSteelGradeComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ReinforcementMaterialGrade> call(ListView<ReinforcementMaterialGrade> reinforcementMaterialGradeListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ReinforcementMaterialGrade reinforcementMaterialGrade, boolean empty) {
                        super.updateItem(reinforcementMaterialGrade, empty);
                        if (reinforcementMaterialGrade == null || empty) {
                            setText("");
                        } else {
                            setText(reinforcementMaterialGrade.name());
                        }
                    }
                };
            }
        });
        BorderPane steelGradeBP = new BorderPane();
        steelGradeBP.setLeft(new Label("Reinforcement steel grade:"));
        BorderPane.setAlignment(steelGradeBP.getLeft(), Pos.CENTER_LEFT);
        BorderPane.setMargin(steelGradeBP.getLeft(), new Insets(0, 5, 0, 0));
        steelGrades.forEach(reinforcementMaterialGrade -> reinforcementSteelGradeComboBox.getItems().add(reinforcementMaterialGrade));
        reinforcementSteelGradeComboBox.setPrefWidth(Double.MAX_VALUE);
        steelGradeBP.setCenter(reinforcementSteelGradeComboBox);
        BorderPane.setAlignment(steelGradeBP.getCenter(), Pos.CENTER_LEFT);
        propertiesVBox.getChildren().add(steelGradeBP);

        double prefButtonWidth = 75d;
        var nextButton = new Button("Next");
        nextButton.setPrefWidth(prefButtonWidth);
        var cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(prefButtonWidth);
        HBox buttons = new HBox(15, nextButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        content.setBottom(new VBox(15, new Separator(Orientation.HORIZONTAL), buttons));
        content.setPadding(new Insets(15));

        Stage stage = new FixedSizeStage(new Scene(content), "Reinforcement", 580, 520, Modality.APPLICATION_MODAL);
        cancelButton.setOnAction(event -> stage.close());
        double corrosionCoverThickness = minCorrosionCoverThicknessProperty.value() + corrosionCoverToleranceProperty.value();
        nextButton.setOnAction(event -> {
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();
            BeamReinforcementAnalysis beamReinforcementAnalysis = new BeamReinforcementAnalysis(
                    corrosionCoverThickness,
                    diameterOfReinforcementBarProperty.value(),
                    diameterOfReinforcementStirrupProperty.value(),
                    concreteGradeComboBox.getValue(),
                    reinforcementSteelGradeComboBox.getValue()
            );

            stage.close();

            Map<BeamReinforcementAnalysis.Reinforcement, Collection<BeamReinforcementAnalysis. BeamReinforcement>> reinforcement;
            try {
                double width = rectangularSection.width().doubleValue();
                double depth = rectangularSection.depth().doubleValue();
                reinforcement = beamReinforcementAnalysis.reinforcement(
                        rectangularSection,
                        new BendingMomentDiagram(Map.of(Position.of(5), Magnitude.of(200)))
//                    loadingAnalysis().bendingMomentDiagram()
                );

                BeamReinforcementAnalysis.BeamReinforcement additionalReinforcement = reinforcement.getOrDefault(BeamReinforcementAnalysis.Reinforcement.COMPRESSIVE, List.of()).stream().min((o1, o2) -> {
                    if (o1.numberOfBars() > o2.numberOfBars())
                        return 1;
                    else if (o1.numberOfBars() < o2.numberOfBars()) {
                        return -1;
                    }
                    return 0;
                }).orElse(BeamReinforcementAnalysis.BeamReinforcement.empty());

                BeamReinforcementAnalysis.BeamReinforcement mainReinforcement = reinforcement.getOrDefault(BeamReinforcementAnalysis.Reinforcement.TENSILE, List.of()).stream().min((o1, o2) -> {
                    if (o1.numberOfBars() > o2.numberOfBars())
                        return 1;
                    else if (o1.numberOfBars() < o2.numberOfBars()) {
                        return -1;
                    }
                    return 0;
                }).orElse(BeamReinforcementAnalysis.BeamReinforcement.empty());

                double beamMaxSize = Screen.getPrimary().getBounds().getWidth() / 4.3;
                StackPane beamReinforcementVisualization = new StackPane();
                beamReinforcementVisualization.setMaxSize(beamMaxSize, beamMaxSize);
                double scaleFactor = Math.min(beamMaxSize / depth, beamMaxSize / width);

                Color foregroundColor = Color.DIMGRAY;
                Rectangle beam = new Rectangle();
                beam.setFill(Color.LIGHTGRAY);
                beam.setStroke(foregroundColor);
                beam.setHeight(scaleFactor * depth);
                beam.setWidth(scaleFactor * width);
                beamReinforcementVisualization.getChildren().add(beam);
                StackPane.setAlignment(beam, Pos.CENTER);

                Rectangle stirrup = new Rectangle();
                double stirrupWidth = scaleFactor * diameterOfReinforcementStirrupProperty.value();
                stirrup.setHeight(scaleFactor * (depth - beamReinforcementAnalysis.verticalCorrosionCoverThickness()));
                stirrup.setWidth(scaleFactor * (width - (corrosionCoverThickness * 2) - stirrupWidth));
                stirrup.setFill(Color.LIGHTGRAY);
                stirrup.setStroke(foregroundColor);
                stirrup.setStrokeWidth(stirrupWidth);
                double maxDiameter = Math.max(mainReinforcement.diameterOfReinforcementBar(), additionalReinforcement.diameterOfReinforcementBar());
                stirrup.setArcHeight(maxDiameter);
                stirrup.setArcWidth(maxDiameter);
                beamReinforcementVisualization.getChildren().add(stirrup);
                StackPane.setAlignment(stirrup, Pos.CENTER);

                Function<BeamReinforcementAnalysis.BeamReinforcement, Circle> barSupplier = (beamReinforcement) -> {
                    double barDiameter = scaleFactor * beamReinforcement.diameterOfReinforcementBar();
                    Circle bar = new Circle(barDiameter / 2);
                    bar.setFill(foregroundColor);
                    bar.setStroke(foregroundColor);
                    return bar;
                };

                BiConsumer<BeamReinforcementAnalysis.BeamReinforcement, Boolean> processBarReinforcement = (rc, bottom) -> {
                    if (rc.numberOfBars() <= 0) {
                        return;
                    }

                    String labelText;
                    Pos leftPos;
                    Pos rightPos;
                    Pos centerPos;
                    if (bottom) {
                        leftPos = Pos.BOTTOM_LEFT;
                        centerPos = Pos.BOTTOM_CENTER;
                        rightPos = Pos.BOTTOM_RIGHT;
                        labelText = "bottom reinforcement";
                    } else {
                        centerPos = Pos.TOP_CENTER;
                        leftPos = Pos.TOP_LEFT;
                        rightPos = Pos.TOP_RIGHT;
                        labelText = "top reinforcement";
                    }

                    int yMultiplier = bottom ? -1 : 1;

                    double innerWidth = width - (diameterOfReinforcementStirrupProperty.value() * 2) - (corrosionCoverThickness * 2);
                    double innerHeight = depth - (beamReinforcementAnalysis.verticalCorrosionCoverThickness() * 2);
                    double innerWidthCalc = innerWidth;
                    int barsPerStandardRow = 0;
                    for (int i = 0; i < rc.numberOfBars(); i++) {
                        if (innerWidthCalc >= rc.diameterOfReinforcementBar() * 2) {
                            innerWidthCalc -= rc.diameterOfReinforcementBar() * 2;
                            barsPerStandardRow++;
                        } else if (innerWidthCalc + corrosionCoverToleranceProperty.value() >= rc.diameterOfReinforcementBar()) {
                            innerWidthCalc -= rc.diameterOfReinforcementBar();
                            barsPerStandardRow++;
                        } else {
                            break;
                        }
                    }
                    double xTranslate = 0;
                    double yTranslate = 0;
                    double scaledBarDiameter = scaleFactor * rc.diameterOfReinforcementBar();

                    if (barsPerStandardRow <= 0) {
                        throw new RuntimeException("barsPerStandardRow <= 0");
                    }

                    // process bars per standard row
                    final int standardRows = rc.numberOfBars() / barsPerStandardRow;
                    final int leftoverRow = rc.numberOfBars() % barsPerStandardRow;

                    if (((standardRows + standardRows - 1) + (leftoverRow + leftoverRow - 1) * rc.numberOfBars()) * rc.diameterOfReinforcementBar() > innerWidth * innerHeight ) {
                        throw new RuntimeException("Beam area is too small compared to the required reinforcement. Please, change the beam geometry values or try to adjust the reinforcement parameters.");
                    }

                    StackPane barsPane = new StackPane();
                    barsPane.setPrefSize(stirrup.getWidth(), stirrup.getHeight());
                    barsPane.setMaxSize(stirrup.getWidth(), stirrup.getHeight());
                    barsPane.setMinSize(stirrup.getWidth(), stirrup.getHeight());
                    barsPane.setPadding(new Insets(stirrupWidth/2));
                    beamReinforcementVisualization.getChildren().add(barsPane);
                    StackPane.setAlignment(barsPane, Pos.CENTER);

                    VBox labelVBox = new VBox(new Label(labelText), new Label(rc.numberOfBars() + "Φ" + formattedDouble(rc.diameterOfReinforcementBar())));
                    labelVBox.setMaxHeight(Font.getDefault().getSize() * labelVBox.getChildren().size());
                    beamReinforcementVisualization.getChildren().add(labelVBox);
                    StackPane.setAlignment(labelVBox, leftPos);
                    labelVBox.setTranslateX((beamMaxSize - beam.getWidth()) + beam.getWidth() + 20);
                    labelVBox.setTranslateY((beam.getHeight() * 0.1) * yMultiplier);

                    for (int i = 0; i < standardRows ; i++) {
                        if (barsPerStandardRow == 1) {
                            var bar = barSupplier.apply(rc);
                            barsPane.getChildren().add(bar);
                            StackPane.setAlignment(bar, centerPos);
                            bar.setTranslateY(yTranslate);
                        } else {
                            double sparedSpaceInStandardRow = innerWidth - (barsPerStandardRow * rc.diameterOfReinforcementBar());
                            double standardRowXStep = scaledBarDiameter + ((sparedSpaceInStandardRow * scaleFactor) / (barsPerStandardRow - 1));
                            for (int j = 0; j < barsPerStandardRow; j++) {
                                Circle bar = barSupplier.apply(rc);
                                barsPane.getChildren().add(bar);
                                StackPane.setAlignment(bar, leftPos);
                                bar.setTranslateX(xTranslate);
                                bar.setTranslateY(yTranslate);
                                xTranslate += standardRowXStep;
                            } xTranslate = 0;
                        } yTranslate += (scaledBarDiameter + (scaledBarDiameter/2)) * yMultiplier;
                    }

                    // process leftover bars
                    if (leftoverRow == 1) {
                        var bar = barSupplier.apply(rc);
                        barsPane.getChildren().add(bar);
                        StackPane.setAlignment(bar, centerPos);
                        bar.setTranslateY(yTranslate);
                    } else {
                        double sparedSpaceInLeftoverRow = innerWidth - (leftoverRow * rc.diameterOfReinforcementBar());
                        double leftoverRowXStep = scaledBarDiameter + ((sparedSpaceInLeftoverRow * scaleFactor) / (leftoverRow - 1));
                        for (int j = 0; j < leftoverRow; j++) {
                            Circle bar = barSupplier.apply(rc);
                            barsPane.getChildren().add(bar);
                            StackPane.setAlignment(bar, leftPos);
                            bar.setTranslateX(xTranslate);
                            bar.setTranslateY(yTranslate);
                            xTranslate += leftoverRowXStep;
                        }
                    }

//                    barsPane.setBackground(new Background(new BackgroundFill(
//                            Color.YELLOW,
//                            new CornerRadii(0),
//                            new Insets(0)
//                    )));
                };
                processBarReinforcement.accept(mainReinforcement, true);
                processBarReinforcement.accept(additionalReinforcement, false);

                VBox labelVBox = new VBox(new Label("stirrup"), new Label("Φ" + formattedDouble(diameterOfReinforcementStirrupProperty.value())));
                labelVBox.setMaxHeight(Font.getDefault().getSize() * labelVBox.getChildren().size());
                beamReinforcementVisualization.getChildren().add(labelVBox);
                StackPane.setAlignment(labelVBox, Pos.CENTER_LEFT);
                labelVBox.setTranslateX((beamMaxSize - beam.getWidth()) + beam.getWidth() + 20);

                // depth dimensional line
                var depthDimensionalLine = new Rectangle(1d, beam.getHeight(), foregroundColor);
                double depthXTranslate = ((beam.getWidth() / 2) + 50) * -1;
                beamReinforcementVisualization.getChildren().add(depthDimensionalLine);
                StackPane.setAlignment(depthDimensionalLine, Pos.CENTER);
                depthDimensionalLine.setTranslateX(depthXTranslate);
                var depthDimensionalLabel = new Label(formattedDouble(rectangularSection.depth().doubleValue()));
                depthDimensionalLabel.setMaxHeight(depthDimensionalLabel.getFont().getSize());
                depthDimensionalLabel.setRotate(-90);
                beamReinforcementVisualization.getChildren().add(depthDimensionalLabel);
                StackPane.setAlignment(depthDimensionalLabel, Pos.CENTER);
                depthDimensionalLabel.setTranslateX(depthXTranslate - (depthDimensionalLabel.getMaxHeight()));

                // width dimensional line
                var widthDimensionalLine = new Rectangle(beam.getWidth(), 1d, foregroundColor);
                double widthYTranslate = (beam.getHeight() / 2) + 50;
                beamReinforcementVisualization.getChildren().add(widthDimensionalLine);
                StackPane.setAlignment(widthDimensionalLine, Pos.CENTER);
                widthDimensionalLine.setTranslateY(widthYTranslate);
                var widthDimensionalLabel = new Label(formattedDouble(rectangularSection.width().doubleValue()));
                widthDimensionalLabel.setMaxHeight(widthDimensionalLabel.getFont().getSize());
                beamReinforcementVisualization.getChildren().add(widthDimensionalLabel);
                StackPane.setAlignment(widthDimensionalLabel, Pos.CENTER);
                widthDimensionalLabel.setTranslateY(widthYTranslate - widthDimensionalLabel.getMaxHeight());

                BorderPane root = new BorderPane(beamReinforcementVisualization);
                Stage beamReinforcementStage = new Stage();
                beamReinforcementStage.setScene(new Scene(root));
                beamReinforcementStage.setTitle(formattedDouble(width) + "x" + formattedDouble(depth) + " Concrete Beam Reinforcement");
                beamReinforcementStage.initModality(Modality.NONE);
                beamReinforcementStage.initStyle(StageStyle.DECORATED);
                beamReinforcementStage.setMinWidth(screenWidth / 1.6);
                beamReinforcementStage.setMinHeight(screenHeight / 1.6);
                new RCWindow(beamReinforcementStage).show();

//                Platform.runLater(() -> beamReinforcementStage.sizeToScene());
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.toString()).showAndWait();
            }
        });

        Platform.runLater(() -> {
            concreteGradeComboBox.getSelectionModel().select(concreteGradeComboBox.getItems().getFirst());
            reinforcementSteelGradeComboBox.getSelectionModel().select(reinforcementSteelGradeComboBox.getItems().getFirst());
            nextButton.requestFocus();
        });
        new RCWindow(stage).showAndWait();
    }

    private static String formattedDouble(double d) {
        String formatted = String.format("%.2f", d);
        return formatted.endsWith(".00") ? formatted.replaceFirst(".00", "") : formatted;
    }
}
