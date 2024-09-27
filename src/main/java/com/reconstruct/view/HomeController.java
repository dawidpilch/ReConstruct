package com.reconstruct.view;

import com.reconstruct.ReConstructApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class HomeController {
    @FXML public TabPane tabPane;
    @FXML public ScrollPane scrollPane;

    @FXML public void initialize() {
        tabPane.getTabs().add(new Tab("Hello Tab!"));
    }

    @FXML public void onFileNew() throws IOException {
        new RCWindow(
                ReConstructApp.class.getResource("fxml/new-calculation-view.fxml"),
                "Select Calculation"
        ).show();
    }
}
