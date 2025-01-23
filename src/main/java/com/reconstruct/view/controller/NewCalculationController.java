package com.reconstruct.view.controller;

import com.reconstruct.ReConstructApp;
import com.reconstruct.view.component.RCWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewCalculationController {
    @FXML public TreeView<String> calculationsTreeView;
    @FXML public WebView exampleWebView;
    @FXML public Button calculateButton;
    @FXML public Button cancelButton;

    private final Map<TreeItem<String>, ItemResources> registry = new HashMap<>();

    private final static Map<TreeItem<String>, ItemResources> beamElements = Map.ofEntries(
            Map.entry(
                    new TreeItem<>("RC simply supported beam analysis & design"),
                    new ItemResources("examples/rc-beam-analysis-design.html", "fxml/beam-analysis-design-view.fxml")
            ),
            Map.entry(
                    new TreeItem<>("Empty example"),
                    new ItemResources("examples/ex.html", "ex.fxml")
            )
    );

    private final static String exampleNotAvailable = """
            <!DOCTYPE html>
            <html lang="en">
                <body>
                    <h1>Example not available</h1>
                </body>
            </html>
            """;

    @FXML public void initialize() {
        calculateButton.setDisable(true);
        populateCalculationsTreeViewItems();

        exampleWebView.getEngine().setJavaScriptEnabled(false);
        calculationsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isLeaf()) {
                calculateButton.setDisable(true);
                return;
            }

            calculateButton.setDisable(false);
            String content = exampleNotAvailable;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(ReConstructApp.class.getResourceAsStream(registry.get(newValue).htmlExample()))
                    )
            )) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                } content = stringBuilder.toString();
            } catch (Exception ignored) {
                System.out.println("Unable to load HTML WebView for: " + newValue);
            }
            exampleWebView.getEngine().loadContent(content);
        });
    }

    public void populateCalculationsTreeViewItems() {
        TreeItem<String> root = new TreeItem<>();

        TreeItem<String> beams = new TreeItem<>("Beams");
        beams.getChildren().addAll(beamElements.keySet());
        registry.putAll(beamElements);

        calculationsTreeView.setRoot(root);
        calculationsTreeView.setShowRoot(false);
        root.getChildren().add(beams);
    }

    public void onCalculateButtonAction(ActionEvent actionEvent) {
        TreeItem<String> selectedItem = calculationsTreeView.getSelectionModel().getSelectedItem();
        ItemResources resources = registry.get(selectedItem);
        String fxml = resources.fxmlLayout();
        String title = selectedItem.getValue();

        this.close();

        Platform.runLater(() -> {
            try {
                new RCWindow(
                        new Scene(new FXMLLoader(ReConstructApp.class.getResource(fxml)).load()),
                        title,
                        Modality.NONE
                ).show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public void onCancelButtonAction(ActionEvent actionEvent) {
        this.close();
    }

    private void close() {
        Stage stage = (Stage) this.calculateButton.getScene().getWindow();
        stage.close();
    }

    private record ItemResources(String htmlExample, String fxmlLayout) {
        ItemResources {
            Objects.requireNonNull(htmlExample);
            Objects.requireNonNull(fxmlLayout);
        }
    }
}