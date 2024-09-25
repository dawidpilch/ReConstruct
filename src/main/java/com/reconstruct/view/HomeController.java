package com.reconstruct.view;

import com.reconstruct.ReConstructApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML public TabPane tabPane;
    @FXML public ScrollPane scrollPane;

    @FXML public void initialize() {
        tabPane.getTabs().add(new Tab("Hello Tab!"));
    }

    @FXML public void onFileNew() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(ReConstructApp.class.getResource("fxml/new-calculation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.minHeightProperty().setValue(480);
        stage.minWidthProperty().setValue(720);
        stage.setTitle("Select Calculation");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
