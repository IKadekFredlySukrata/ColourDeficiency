package org.example.colourdeficiency.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.colourdeficiency.Main;
import org.example.colourdeficiency.models.Variable;

import java.io.IOException;
import java.util.Objects;

public class MainPage {
    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/resources/images/menu-logo.png")).toExternalForm());
            logoImage.setImage(image);
        } catch (Exception ignore) {}
    }

    @FXML
    private void onSimulateClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("test-page.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }


    @FXML
    private void onAssistClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("choose-image.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }

    @FXML
    private void onSettingsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("setting.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }

}