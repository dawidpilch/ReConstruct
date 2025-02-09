package com.reconstruct.view.component;

import com.reconstruct.model.beam.LoadingAnalysis;
import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.value.Position;
import com.reconstruct.view.viewmodel.AppendableProperty;
import com.reconstruct.view.viewmodel.SimplySupportedBeamViewModel;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import org.apache.commons.math3.util.Precision;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BeamView {
    private final SimplySupportedBeamViewModel beamViewModel;
    private final Map<AppendableProperty<Double>, Node> supportPositionsToNodeMap = new HashMap<>();

    private final StackPane contentPane = new StackPane();
    private final StackPane chartPane = new StackPane();
    private final StackPane loadingPane = new StackPane();
    private final StackPane characteristicPointsPane = new StackPane();
    private final AreaChart<Number, Number> areaChart;
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;

    private final double componentWidth;
    private final double componentHeight;
    private final static double NODE_WIDTH = 40.0;

    public BeamView(double width, double height, SimplySupportedBeamViewModel beamViewModel) {
        this.componentWidth = width;
        this.componentHeight = height;
        this.beamViewModel = beamViewModel;

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        double yWidth = 19;
        yAxis.setPrefWidth(yWidth);
        yAxis.setMinWidth(yWidth);
        yAxis.setMaxWidth(yWidth);
        this.areaChart = new AreaChart<>(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        Node vZeroLine = areaChart.lookup(".chart-vertical-zero-line");
        if (vZeroLine != null) {
            vZeroLine.setStyle("""
                    -fx-stroke: f4f4f4;
                    -fx-stroke-dash-array: 4 4;
                    -fx-stroke-width: 1;
            """);
        }

        Node hZeroLine = areaChart.lookup(".chart-horizontal-zero-line");
        if (hZeroLine != null) {
            hZeroLine.setStyle("""
                    -fx-stroke: black;
                    -fx-stroke-width: 3;
            """);
        }

        contentPane.setPrefSize(componentWidth + NODE_WIDTH, componentHeight);
        contentPane.setMaxSize(componentWidth + NODE_WIDTH, componentHeight);
        contentPane.setMinSize(componentWidth + NODE_WIDTH, componentHeight);

        contentPane.getChildren().add(areaChart);
        StackPane.setAlignment(areaChart, Pos.CENTER);
        areaChart.setHorizontalGridLinesVisible(false);
        areaChart.setVerticalGridLinesVisible(false);
        areaChart.setTranslateY(10);
        areaChart.setTranslateX(-10);

        displayDiagram(new XYChart.Series<>());

        areaChart.toFront();
        displayDiagram(new XYChart.Series<>(new ReadOnlyListWrapper<>()));

        double chartPadding = 100;
        double chartHeight = componentHeight - chartPadding;
        chartPane.setPrefSize(componentWidth, chartHeight);
        chartPane.setMaxSize(componentWidth, chartHeight);
        chartPane.setMinSize(componentWidth, chartHeight);
        contentPane.getChildren().add(chartPane);
        StackPane.setAlignment(chartPane, Pos.CENTER);

        loadingPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
        loadingPane.setMaxSize(contentPane.getMaxWidth(), contentPane.getMaxHeight());
        loadingPane.setMinSize(contentPane.getMinWidth(), contentPane.getMinHeight());
        contentPane.getChildren().add(loadingPane);
        StackPane.setAlignment(loadingPane, Pos.CENTER);

        characteristicPointsPane.setPrefSize(componentWidth, chartPadding);
        characteristicPointsPane.setMaxSize(componentWidth, chartPadding);
        characteristicPointsPane.setMinSize(componentWidth, chartPadding);
        contentPane.getChildren().add(characteristicPointsPane);
        StackPane.setAlignment(characteristicPointsPane, Pos.BOTTOM_CENTER);
        refreshCharacteristicPoints();

        Node pinnedSupportNode = pinnedSupport();
        contentPane.getChildren().add(pinnedSupportNode);
        StackPane.setAlignment(pinnedSupportNode, Pos.CENTER_LEFT);

        Node rollerSupportNode = rollerSupport();
        contentPane.getChildren().add(rollerSupportNode);
        StackPane.setAlignment(rollerSupportNode, Pos.CENTER_LEFT);

        supportPositionsToNodeMap.put(beamViewModel.pinnedSupportPositionProperty, pinnedSupportNode);
        supportPositionsToNodeMap.put(beamViewModel.rollerSupportPositionProperty, rollerSupportNode);

        chartPane.toFront();
        loadingPane.toFront();
    }

    private void translateXNodesAbsPosition(Node node, double absolutePositionOnBeam) {
        node.setTranslateX((absolutePositionOnBeam / beamViewModel.beamLengthProperty.value()) * componentWidth);
    }

    private Node pinnedSupport() {
        double height = 38;
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                -19.0, 19.0,
                19.0, 19.0,
                0.0, -19.0
        );

        triangle.setFill(Color.WHITESMOKE);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeWidth(2);
        triangle.setTranslateY(height / 2);
        return triangle;
    }

    private Node rollerSupport() {
        Circle circle = new Circle(38d / 2);
        circle.setFill(Color.WHITESMOKE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        circle.setTranslateY(38d / 2);
        return circle;
    }

    public void displayLoading() {
        displayLoading(LoadingAnalysis.empty());
    }

    public void displayLoading(LoadingAnalysis loadingAnalysis) {
        loadingPane.setVisible(true);
        loadingPane.getChildren().clear();

        for (VerticalPointLoad verticalPointLoad : loadingAnalysis.verticalSupportReactions()) {
            Node pointArrow = pointArrow(verticalPointLoad.magnitude().doubleValue(), verticalPointLoad.position().doubleValue(), verticalPointLoad.isDirectedUpwards(), Color.STEELBLUE);
            loadingPane.getChildren().add(pointArrow);
            pointArrow.toFront();
        }

        for (BendingMoment bendingMoment : beamViewModel.bendingMomentsProperty.value()) {
            SVGPath svgPath = new SVGPath();
            double svgHeight = 107;
            double wrapperHeight = 30 + svgHeight;
            Color colorFill = Color.rgb(230, 181, 17);

            svgPath.setContent("M3 0A1 1 0 00-3 0 1 1 0 003 0ZM19 0Q19-27 6-44 3-43 1-42 0-50 1-56 6-53 13-47 10-46 8-45 21-28 21 0 21 29 0 50V48Q19 27 19 0");
            svgPath.setFill(colorFill);

            StackPane wrapper = new StackPane(svgPath);
            wrapper.setMaxSize(NODE_WIDTH, wrapperHeight);
            wrapper.setPrefSize(NODE_WIDTH, wrapperHeight);
            wrapper.setMinSize(NODE_WIDTH, wrapperHeight);
            loadingPane.getChildren().add(wrapper);
            StackPane.setAlignment(wrapper, Pos.CENTER_LEFT);

            Label label = numericLabel(Math.abs(bendingMoment.magnitude().doubleValue()));
            label.setStyle("-fx-font-size: 14;");
            label.setTextFill(colorFill);
            wrapper.getChildren().add(label);
            StackPane.setAlignment(label, Pos.TOP_CENTER);
            StackPane.setAlignment(svgPath, Pos.CENTER_RIGHT);

            double fix = 1;
            if (bendingMoment.isClockwise()) {
                svgPath.getTransforms().add(new Scale(-1, 1));
                fix = 2;
            }

            wrapper.setTranslateY(-6d);
            translateXNodesAbsPosition(wrapper, bendingMoment.position().doubleValue());

            wrapper.setTranslateX(wrapper.getTranslateX() + fix);
            wrapper.toFront();
        }

        for (var udl : beamViewModel.uniformlyDistributedLoadsProperty.value()) {
            Paint strokePaint = Color.rgb(74, 215, 104);
            Paint fillPaint = Color.rgb(74, 215, 104, 0.3);

            Node startArrow = pointArrow(udl.magnitude().doubleValue(), udl.startPosition().doubleValue(), udl.isDirectedUpwards(), strokePaint);
            Node endArrow = pointArrow(udl.magnitude().doubleValue(), udl.endPosition().doubleValue(), udl.isDirectedUpwards(), strokePaint);

            double rectangleHeight = 162;
            Rectangle rectangle = new Rectangle(endArrow.getTranslateX() - startArrow.getTranslateX(), rectangleHeight);
            rectangle.setFill(fillPaint);
            rectangle.setStroke(strokePaint);
            rectangle.setStrokeWidth(2);

            StackPane wrapper = new StackPane(rectangle, startArrow, endArrow);

            double width = (endArrow.getTranslateX() - startArrow.getTranslateX()) + NODE_WIDTH * 2;
            wrapper.setMaxSize(width, rectangleHeight);
            wrapper.setPrefSize(width, rectangleHeight);
            wrapper.setMinSize(width, rectangleHeight);

            StackPane.setAlignment(wrapper, Pos.CENTER_LEFT);
            StackPane.setAlignment(rectangle, Pos.CENTER_LEFT);

            rectangle.setTranslateX((startArrow.getTranslateX() + NODE_WIDTH/2) - 1);
            rectangle.setTranslateY(udl.isDirectedUpwards() ? rectangleHeight/2 : (rectangleHeight/2) * -1);


            loadingPane.getChildren().add(wrapper);
            wrapper.toFront();
        }

        for (VerticalPointLoad verticalPointLoad : beamViewModel.verticalPointLoadsProperty.value()) {
            Node pointArrow = pointArrow(verticalPointLoad.magnitude().doubleValue(), verticalPointLoad.position().doubleValue(), verticalPointLoad.isDirectedUpwards(), Paint.valueOf("red"));
            loadingPane.getChildren().add(pointArrow);
            pointArrow.toFront();
        }

        refreshCharacteristicPoints();
    }

    private Node pointArrow(double magnitude, double absolutePositionOnBeam, boolean directedUpwards, Paint paint) {
        SVGPath svgPath = new SVGPath();
        double svgHeight = 162;
        double wrapperHeight = 30 + svgHeight;
        svgPath.setContent("M 0 -162 H 1 V -10 H 6 Q 3 -3 0 0 Q -3 -3 -6 -10 H -1 V -162 Z");
        svgPath.setFill(paint);

        StackPane wrapper = new StackPane(svgPath);
        wrapper.setMaxSize(NODE_WIDTH, wrapperHeight);
        wrapper.setPrefSize(NODE_WIDTH, wrapperHeight);
        wrapper.setMinSize(NODE_WIDTH, wrapperHeight);

        StackPane.setAlignment(wrapper, Pos.CENTER_LEFT);

        Label label = numericLabel(Math.abs(magnitude));
        label.setStyle("-fx-font-size: 14;");
        label.setTextFill(paint);
        wrapper.getChildren().add(label);
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        StackPane.setAlignment(svgPath, Pos.BOTTOM_CENTER);

        if (directedUpwards) {
            double fix = 2d;
            wrapper.setRotate(180);
            wrapper.setTranslateY((wrapperHeight / 2) - fix);
            label.setRotate(180);
        } else {
            wrapper.setTranslateY(-(wrapperHeight / 2));
        }

        translateXNodesAbsPosition(wrapper, absolutePositionOnBeam);
        return wrapper;
    }

    public void hideLoading() {
        loadingPane.getChildren().clear();
    }

    public void displayDiagram(XYChart.Series<Number, Number> series) {
        chartPane.getChildren().clear();
        areaChart.getData().clear();

        areaChart.getData().add(series);
        double max = series.getData().stream().map(XYChart.Data::getYValue).map(Number::doubleValue).map(Math::abs).max(Double::compareTo).orElse(0d);

        if (max == 0) {
            max = 1;
        }

        List<XYChart.Data<Number, Number>> dataList = series.getData().sorted(Comparator.comparingDouble(o -> o.getXValue().doubleValue())).stream().toList();
        for (int i = 0; i < dataList.size(); i++) {
            boolean shouldLabelCurrent = false;
            if (i == 0 || i == dataList.size() - 1) {
                shouldLabelCurrent = true;
            } else {
                var previousY = dataList.get(i - 1).getYValue().doubleValue();
                var currentY = dataList.get(i).getYValue().doubleValue();
                var nextY = dataList.get(i + 1).getYValue().doubleValue();

                if ((previousY > currentY && nextY > currentY) || (previousY < currentY && nextY < currentY)) {
                    shouldLabelCurrent = true;
                } else if (Precision.equals(dataList.get(i).getXValue().doubleValue(), dataList.get(i + 1).getXValue().doubleValue(), 0.0001)) {
                    shouldLabelCurrent = true;
                } else if (Precision.equals(dataList.get(i).getXValue().doubleValue(), dataList.get(i - 1).getXValue().doubleValue(), 0.0001)) {
                    shouldLabelCurrent = true;
                }
            }

            if (!shouldLabelCurrent) {
                continue;
            }

            Label label = numericLabel(Math.abs(dataList.get(i).getYValue().doubleValue()));
            label.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
            translateXNodesAbsPosition(label, dataList.get(i).getXValue().doubleValue());
            label.setTranslateX(label.getTranslateX() - 150);

            double y = Precision.equals(dataList.get(i).getYValue().doubleValue(), 0, 0.000001) ? max * 0.15 : dataList.get(i).getYValue().doubleValue();
            double v = ((y - (-max)) / (max - (-max))) * (((componentHeight-150) / 2) - (-((componentHeight-150) / 2))) + (-((componentHeight-150) / 2));
            label.setTranslateY(-v);
            chartPane.getChildren().add(label);
            StackPane.setAlignment(label, Pos.CENTER_LEFT);
            label.toFront();
        }

        double offset = 11;
        areaChart.setPrefSize(componentWidth + NODE_WIDTH + offset, componentHeight - 100);
        areaChart.setMinSize(componentWidth + NODE_WIDTH  + offset, componentHeight - 100);
        areaChart.setMaxSize(componentWidth + NODE_WIDTH  + offset, componentHeight - 100);
        areaChart.setLegendVisible(false);
        areaChart.getXAxis().setOpacity(0);
        areaChart.getYAxis().setOpacity(0);

        adjustXAxisForLength();
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max * 1.15);
        yAxis.setLowerBound(-max * 1.15);
    }

    public void hideDiagram() {
        chartPane.getChildren().clear();
        areaChart.getData().clear();
    }

    public void refreshGeometry() {
        adjustXAxisForLength();
        for (var entry : supportPositionsToNodeMap.entrySet()) {
            translateXNodesAbsPosition(entry.getValue(), entry.getKey().value());
        }

        refreshCharacteristicPoints();
    }

    public void refreshCharacteristicPoints() {
        Set<Position> characteristicPoints = new TreeSet<>(Comparator.comparingDouble(Position::doubleValue));
        beamViewModel.verticalPointLoadsProperty.value().forEach(verticalPointLoad -> characteristicPoints.add(verticalPointLoad.position()));
        beamViewModel.bendingMomentsProperty.value().forEach(bendingMoment -> characteristicPoints.add(bendingMoment.position()));
        beamViewModel.uniformlyDistributedLoadsProperty.value().forEach(uniformlyDistributedLoad -> {
            characteristicPoints.add(uniformlyDistributedLoad.startPosition());
            characteristicPoints.add(uniformlyDistributedLoad.endPosition());
        });

        characteristicPoints.add(Position.of(beamViewModel.pinnedSupportPositionProperty.value()));
        characteristicPoints.add(Position.of(beamViewModel.rollerSupportPositionProperty.value()));

        Rectangle beamSketch = new Rectangle(999, 3);
        beamSketch.setFill(Color.DIMGRAY);
        beamSketch.setStrokeWidth(0);
        characteristicPointsPane.getChildren().clear();

        characteristicPointsPane.getChildren().add(beamSketch);
        StackPane.setAlignment(beamSketch, Pos.CENTER);

        BiConsumer<String, Double> addLabel = (text, absPosition) -> {
            Label l = new Label(text);
            l.setTextAlignment(TextAlignment.CENTER);
            l.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(0), new Insets(-5))));
            l.setStyle("-fx-text-fill: #696969;");
            translateXNodesAbsPosition(l, absPosition);
            characteristicPointsPane.getChildren().add(l);
            StackPane.setAlignment(l, Pos.CENTER_LEFT);
        };

        Consumer<Double> addCharacteristicPoint = (absPosition) -> {
            Rectangle r = new Rectangle(2, 15);
            r.setFill(Color.DIMGRAY);
            r.setStrokeWidth(0);
            translateXNodesAbsPosition(r, absPosition);
            characteristicPointsPane.getChildren().add(r);
            StackPane.setAlignment(r, Pos.CENTER_LEFT);
        };

        if (characteristicPoints.isEmpty()) {
            addLabel.accept(new FormattedStringDouble(beamViewModel.beamLengthProperty.value()).toString(), beamViewModel.beamLengthProperty.value() / 2d);
            return;
        }

        Position[] characteristicPointsArray = characteristicPoints.toArray(Position[]::new);
        String meterString = " [m]";
        for (int i = 0; i < characteristicPoints.size(); i++) {
            boolean isLast = i == characteristicPoints.size() - 1;
            Position current = characteristicPointsArray[i];

            if (i == 0 && !current.equals(Position.of(0))) {
                addCharacteristicPoint.accept(0d);
                addLabel.accept(new FormattedStringDouble(current.doubleValue()) + meterString, current.doubleValue() / 2);
            }

            if (isLast && !current.equals(Position.of(beamViewModel.beamLengthProperty.value()))) {
                addCharacteristicPoint.accept(beamViewModel.beamLengthProperty.value());
                addLabel.accept(new FormattedStringDouble(beamViewModel.beamLengthProperty.value() - current.doubleValue()) + meterString, ((beamViewModel.beamLengthProperty.value() - current.doubleValue()) / 2) + current.doubleValue());
            }

            if (i > 0) {
                Position previous = characteristicPointsArray[i - 1];
                addLabel.accept(new FormattedStringDouble(current.doubleValue() - previous.doubleValue()) + meterString, ((current.doubleValue() - previous.doubleValue()) / 2) + previous.doubleValue());
            }

            addCharacteristicPoint.accept(current.doubleValue());
        }
    }

    public Node node() {
        return contentPane;
    }

    private void adjustXAxisForLength() {
        xAxis.setAutoRanging(false);
        xAxis.setUpperBound(beamViewModel.beamLengthProperty.value());
        xAxis.setLowerBound(0d);
    }

    private Label numericLabel(double value) {
        Label label = new Label(String.format("%.2f", value));
        label.setMinWidth(300);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        return label;
    }
}
