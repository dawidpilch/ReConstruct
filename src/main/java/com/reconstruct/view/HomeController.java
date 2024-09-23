package com.reconstruct.view;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;

public class HomeController {
    @FXML
    public SplitPane homeSplitPane;

    @FXML
    public void initialize() {
        homeSplitPane.setDividerPositions(0.15);
        homeSplitPane.widthProperty().addListener((obs, oldVal, newVal) -> homeSplitPane.setDividerPositions(0.15));
    }
}
