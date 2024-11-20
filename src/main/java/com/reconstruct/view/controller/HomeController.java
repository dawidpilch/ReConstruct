package com.reconstruct.view.controller;

import com.reconstruct.ReConstructApp;
import com.reconstruct.view.component.RCWindow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
                new Scene(new FXMLLoader(ReConstructApp.class.getResource("fxml/new-calculation-view.fxml")).load()),
                "Select Calculation"
        ).show();
    }
}
