package com.reconstruct.view.component;

import com.reconstruct.view.viewmodel.AppendableValue;
import com.reconstruct.view.viewmodel.ValueErrors;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ErrorDoubleTextField {
    private final VBox node = new VBox();
    private final AppendableValue<Double> appendableValue;
    private final TextField textField;
    private final Label errorMessage;

    public ErrorDoubleTextField(AppendableValue<Double> appendableValue) {
        this.appendableValue = appendableValue;
        this.textField = new TextField(this.appendableValue.value().toString());
        this.errorMessage = new Label();

        errorMessage.setWrapText(true);
        errorMessage.setStyle("-fx-text-fill: red;");
        node.getChildren().add(new Label(this.appendableValue.name()));
        node.getChildren().add(textField);
        node.getChildren().add(errorMessage);
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            double newValueDouble;
            try {
                newValueDouble = Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                showErrorMessage("Inappropriate number format.");
                return;
            }

            ValueErrors errors = this.appendableValue.tryAppend(newValueDouble);
            if (errors.isEmpty()) {
                textField.setText(newValue);
                hideErrorMessage();
                return;
            }

            showErrorMessage(errors.iterator().next());
        });
        this.appendableValue.tryAppend(this.appendableValue.value());
    }

    private void showErrorMessage(String message) {
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
        errorMessage.setText(message);
        textField.setStyle("-fx-border-color: red;");
    }

    private void hideErrorMessage() {
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);
        textField.setStyle("");
    }

    public void disable() {
        textField.setDisable(true);
    }

    public void enable() {
        textField.setDisable(false);
    }

    public Node node() {
        return node;
    }

    public void setText(String text) {
        this.textField.textProperty().setValue(text);
    }
}
