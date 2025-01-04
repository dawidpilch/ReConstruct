package com.reconstruct.view.component;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Standardized popup application modal window for ReConstruct app
 */
public class RCWindow {
    private final Scene scene;
    private final String title;
    private final Modality modality;

    public RCWindow(Scene scene, String title) {
        this(scene, title, Modality.APPLICATION_MODAL);
    }

    public RCWindow(Scene scene, String title, Modality modality) {
        this.scene = scene;
        this.title = title;
        this.modality = modality;
    }

    public void show() {
        show(Screen.getPrimary().getBounds().getHeight() / 1.2, Screen.getPrimary().getBounds().getWidth() / 1.2);
    }


    public void show(double height, double width) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setHeight(height);
        stage.setWidth(width);
        stage.minHeightProperty().setValue(580);
        stage.minWidthProperty().setValue(720);
        stage.setTitle(title);
        stage.initModality(modality);
        stage.initStyle(StageStyle.DECORATED);
        stage.showAndWait();
    }
}
