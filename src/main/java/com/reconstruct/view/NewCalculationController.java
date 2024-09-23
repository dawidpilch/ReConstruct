package com.reconstruct.view;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class NewCalculationController {
    @FXML
    public TreeView<String> calculationsTreeView;

    @FXML public void initialize() {
        initializeCalculationsTreeView();
    }

    private void initializeCalculationsTreeView() {
        TreeItem<String> root = new TreeItem<>();

        TreeItem<String> beams = new TreeItem<>("Beams");
        TreeItem<String> RCSSBeam = new TreeItem<>("RC simply supported beam analysis & design");
        beams.getChildren().add(RCSSBeam);
        
        calculationsTreeView.setRoot(root);
        calculationsTreeView.setShowRoot(false);
        root.getChildren().add(beams);
    }
}
