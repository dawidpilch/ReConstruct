package com.reconstruct.view.component;

import com.reconstruct.view.viewmodel.AppendableProperty;
import com.reconstruct.view.viewmodel.PropertyErrors;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ErrorDoubleTextField {
    private final VBox node = new VBox(5);
    private final AppendableProperty<Double> appendableProperty;
    private final TextField textField;
    private final Label errorMessage;

    public ErrorDoubleTextField(AppendableProperty<Double> appendableProperty) {
        this(appendableProperty, new TextFlow(new Text(appendableProperty.name())));
    }

    public ErrorDoubleTextField(AppendableProperty<Double> appendableProperty, TextFlow textFlow) {
        this.appendableProperty = appendableProperty;
        this.textField = new TextField(formattedDouble(this.appendableProperty.value()));
        this.errorMessage = new Label();

        errorMessage.setWrapText(true);
        errorMessage.setStyle("-fx-text-fill: red;");
        node.getChildren().add(textFlow);
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

            PropertyErrors errors = this.appendableProperty.tryAppend(newValueDouble);
            if (errors.isEmpty()) {
                textField.setText(newValue);
                hideErrorMessage();
                return;
            }

            showErrorMessage(errors.iterator().next());
        });
        this.appendableProperty.tryAppend(this.appendableProperty.value());
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

    private static String formattedDouble(double d) {
        return new FormattedStringDouble(d).toString();
    }
}
