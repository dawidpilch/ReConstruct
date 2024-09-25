package com.reconstruct.view;

import com.reconstruct.ReConstructApp;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewCalculationController {
    @FXML public TreeView<String> calculationsTreeView;
    @FXML public WebView exampleWebView;

    private final Map<TreeItem<String>, String> registry = new HashMap<>();
    private final static Map<TreeItem<String>, String> beamElements = Map.of(
            new TreeItem<>("RC simply supported beam analysis & design"), "examples/example1.html",
            new TreeItem<>("Empty"), "examples/empty.html"
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
        exampleWebView.getEngine().setJavaScriptEnabled(false);
        initializeCalculationsTreeView();
    }

    private void initializeCalculationsTreeView() {
        TreeItem<String> root = new TreeItem<>();

        TreeItem<String> beams = new TreeItem<>("Beams");
        beams.getChildren().addAll(beamElements.keySet());
        registry.putAll(beamElements);

        calculationsTreeView.setRoot(root);
        calculationsTreeView.setShowRoot(false);
        root.getChildren().add(beams);

        calculationsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isLeaf()) {
                return;
            }

            String content = exampleNotAvailable;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(ReConstructApp.class.getResourceAsStream(registry.get(newValue)))
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
}