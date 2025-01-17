package com.reconstruct.view.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class SaveCancelButtonPanel {
    private final HBox node;
    private final Button saveButton;
    private final Button cancelButton;

    public SaveCancelButtonPanel(EventHandler<ActionEvent> onSaveActionEvent, EventHandler<ActionEvent> onCancelActionEvent) {
        double prefButtonWidth = 75d;

        saveButton = new Button("Save");
        saveButton.setPrefWidth(prefButtonWidth);
        saveButton.setOnAction(onSaveActionEvent);

        cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(prefButtonWidth);
        cancelButton.setOnAction(onCancelActionEvent);

        HBox hBox = new HBox(15d, saveButton, cancelButton);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        this.node = hBox;
    }

    public Node node() {
        return node;
    }

    public void requestFocus() {
        saveButton.requestFocus();
    }

    public void saveButtonDisabled(boolean disabled) {
        saveButton.setDisable(disabled);
    }

    public void cancelButtonDisabled(boolean disabled) {
        cancelButton.setDisable(disabled);
    }
}
