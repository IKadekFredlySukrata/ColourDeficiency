package org.example.colourdeficiency.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.example.colourdeficiency.Main;
import org.example.colourdeficiency.models.Variable;

import java.io.IOException;
import java.util.Objects;

public class SettingsController {

    @FXML
    private ComboBox<String> resolutionComboBox;

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    public void initialize() {
        resolutionComboBox.getItems().addAll("800x600", "1024x768", "1280x720", "1920x1080");
        resolutionComboBox.setValue("Chose Resolution");

        themeComboBox.getItems().addAll("Light", "Dark", "Pastel", "Graystone", "Matcha", "Minimalist", "Nature", "Autumn", "Neon");
        themeComboBox.setValue("Chose theme");

        resolutionComboBox.setOnAction(e -> applyResolution());
        themeComboBox.setOnAction(e -> applyTheme());
    }

    private void applyResolution() {
        String selected = resolutionComboBox.getValue();
        String[] dims = selected.split("x");
        int width = Integer.parseInt(dims[0]);
        int height = Integer.parseInt(dims[1]);

        Stage stage = (Stage) Variable.getScene().getWindow();
        stage.setWidth(width);
        stage.setHeight(height);
    }

    private void applyTheme() {
        String selectedTheme = themeComboBox.getValue();
        Scene scene = Variable.getScene();

        scene.getStylesheets().clear();
        switch (selectedTheme) {
            case "Light":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/light.css")).toExternalForm());
                break;
            case "Dark":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/dark.css")).toExternalForm());
                break;
            case "Pastel":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/dark-pastel.css")).toExternalForm());
                break;
            case "Graystone":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/graystone.css")).toExternalForm());
                break;
            case "Matcha":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/matcha.css")).toExternalForm());
                break;
            case "Minimalist":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/minimalist.css")).toExternalForm());
                break;
            case "Nature":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/nature.css")).toExternalForm());
                break;
            case "Autumn":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/autumn.css")).toExternalForm());
                break;
            case "Neon":
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/theme/neon.css")).toExternalForm());
                break;
            case "System Default":
            default:
                break;
        }
    }

    @FXML
    private void handleBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }
}
