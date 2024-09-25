package com.reconstruct.view;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class NewCalculationController {
    @FXML public TreeView<String> calculationsTreeView;
    @FXML public AnchorPane samplePane;

    @FXML public void initialize() {
        initializeCalculationsTreeView();
    }

    private void initializeCalculationsTreeView() {
        TreeItem<String> root = new TreeItem<>();

        TreeItem<String> beams = new TreeItem<>("Beams");
        TreeItem<String> RCSSBeam = new TreeItem<>("RC simply supported beam analysis & design");
        RCSSBeam.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            samplePane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        beams.getChildren().add(RCSSBeam);
        
        calculationsTreeView.setRoot(root);
        calculationsTreeView.setShowRoot(false);
//        calculationsTreeView.selectionModelProperty().
        root.getChildren().add(beams);
    }

}
