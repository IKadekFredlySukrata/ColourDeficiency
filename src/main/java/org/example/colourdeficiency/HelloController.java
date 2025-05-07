package org.example.colourdeficiency;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;

public class HelloController {

    @FXML
    private ImageView logoImage;

    @FXML
    private Label welcomeText; // Optional, not used in current FXML

    @FXML
    public void initialize() {
        System.out.println("initialize() called");

        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/resources/images/testimage.jpeg")).toExternalForm());
            logoImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Failed to load logo image: " + e.getMessage());
        }
    }

    @FXML
    private void onSimulateClick() {
        System.out.println("Simulate Color Blindness clicked");
        // TODO: Navigate to simulation screen
    }

    @FXML
    private void onAssistClick() {
        System.out.println("Assist Tools clicked");
        // TODO: Navigate to assist tools screen
    }

    @FXML
    private void onSettingsClick() {
        System.out.println("Settings clicked");
        // TODO: Open settings screen
    }
}
