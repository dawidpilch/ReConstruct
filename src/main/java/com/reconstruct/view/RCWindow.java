package com.reconstruct.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

/**
 * Standardized popup application modal window for ReConstruct app
 */
public class RCWindow {
    private final URL url;
    private final String title;
    private final Modality modality;

    public RCWindow(URL fxml, String title) {
        this(fxml, title, Modality.APPLICATION_MODAL);
    }

    public RCWindow(URL fxml, String title, Modality modality) {
        this.url = fxml;
        this.title = title;
        this.modality = modality;
    }

    public void show() throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(new FXMLLoader(url).load()));
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.2);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.2);
        stage.minHeightProperty().setValue(480);
        stage.minWidthProperty().setValue(720);
        stage.setTitle(title);
        stage.initModality(modality);
        stage.initStyle(StageStyle.DECORATED);
        stage.showAndWait();
    }
}
