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
        System.out.println("initialize() called");

        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/resources/images/menu-logo.png")).toExternalForm());
            logoImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Failed to load logo image: " + e.getMessage());
        }
    }


    @FXML
    private void onSimulateClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("test-page-2.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }


    @FXML
    private void onAssistClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Stream.fxml"));
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