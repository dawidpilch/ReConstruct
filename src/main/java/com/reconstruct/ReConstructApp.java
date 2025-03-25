package com.reconstruct;

import com.reconstruct.view.component.RCWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ReConstructApp extends Application {
    public final static Image APP_ICON = new Image((Objects.requireNonNull(ReConstructApp.class.getResourceAsStream("icon.png"))));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReConstructApp.class.getResource("fxml/beam-analysis-design-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        new RCWindow(scene, "ReConstruct", Modality.NONE).show();
    }
}