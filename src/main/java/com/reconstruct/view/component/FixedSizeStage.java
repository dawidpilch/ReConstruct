package com.reconstruct.view.component;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FixedSizeStage extends Stage {
    public FixedSizeStage(Scene content, String title, double height, double width, Modality modality) {
        this.setScene(content);
        this.setHeight(height);
        this.setWidth(width);
        this.setMinHeight(height);
        this.setMinWidth(width);
        this.setMaxHeight(height);
        this.setMaxWidth(width);
        this.setTitle(title);
        this.initModality(modality);
        this.initStyle(StageStyle.DECORATED);
        this.setResizable(false);
    }
}
