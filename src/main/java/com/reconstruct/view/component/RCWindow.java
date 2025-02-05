package com.reconstruct.view.component;

import com.reconstruct.ReConstructApp;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.function.Supplier;

public class RCWindow {
    private final Stage stage;

    public RCWindow(Scene scene, String title) {
        this(scene, title, Modality.NONE);
    }

    public RCWindow(Scene scene, String title, Modality modality) {
        this(((Supplier<Stage>) () -> {
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setHeight(defaultHeight());
            stage.setWidth(defaultWidth());
            stage.minHeightProperty().setValue(580);
            stage.minWidthProperty().setValue(720);
            stage.setTitle(title);
            stage.initModality(modality);
            stage.initStyle(StageStyle.DECORATED);
            return stage;
        }).get());
    }

    public RCWindow(Stage stage) {
        this.stage = stage;
        this.stage.getIcons().add(ReConstructApp.APP_ICON);
    }

    private static double defaultWidth() {
        return Screen.getPrimary().getBounds().getWidth() / 1.2;
    }

    private static double defaultHeight() {
        return Screen.getPrimary().getBounds().getHeight() / 1.2;
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    public void showAndWait(double initWidth, double initHeight) {
        stage.setHeight(initHeight);
        stage.setWidth(initWidth);
        stage.showAndWait();
    }

    public void show() {
        stage.show();
    }

    public void show(double initWidth, double initHeight) {
        stage.setHeight(initHeight);
        stage.setWidth(initWidth);
        stage.show();
    }
}
