package com.reconstruct;

import com.reconstruct.view.component.RCWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReConstructApp extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReConstructApp.class.getResource("fxml/new-calculation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.minHeightProperty().setValue(400);
        stage.minWidthProperty().setValue(400);
        stage.setTitle("ReConstruct");
        stage.setScene(scene);
        new RCWindow(stage).show();
    }

    public static void main(String[] args) {
        launch();
    }
}