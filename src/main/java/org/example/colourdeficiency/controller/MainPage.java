package org.example.colourdeficiency.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.colourdeficiency.Main;
import org.example.colourdeficiency.models.HueToColorFX;
import org.example.colourdeficiency.models.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainPage {

    // Initialized
    @FXML
    private ImageView logoImage;

    // Testing Page
    @FXML
    private ImageView imageView00;
    @FXML
    private ImageView imageView01;
    @FXML
    private ImageView imageView02;
    @FXML
    private ImageView imageView03;
    @FXML
    private ImageView imageView10;
    @FXML
    private ImageView imageView11;
    @FXML
    private ImageView imageView12;
    @FXML
    private ImageView imageView13;
    @FXML
    private ImageView imageView20;
    @FXML
    private ImageView imageView21;
    @FXML
    private ImageView imageView22;
    @FXML
    private ImageView imageView23;
    @FXML
    private ImageView imageView30;
    @FXML
    private ImageView imageView31;
    @FXML
    private ImageView imageView32;
    @FXML
    private ImageView imageView33;

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
        try {
            List<ImageView> imageViews = Arrays.asList(
                    imageView00, imageView01, imageView02, imageView03,
                    imageView10, imageView11, imageView12, imageView13,
                    imageView20, imageView21, imageView22, imageView23,
                    imageView30, imageView31, imageView32, imageView33);

            int index = 0;
            for (int i = 1; i < 5; i++) {
                List<Color> RGBValue = HueToColorFX.getColorRGB(i);
                List<Color> RandomizedRGBValue = HueToColorFX.getRandomizedTray(RGBValue);

                for (Color color : RandomizedRGBValue) {
                    if (index >= imageViews.size()) break;
                    Image img = createColorImage(color);
                    imageViews.get(index).setImage(img);
                    index++;
                }
            }

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("test-page-2.fxml"));
            Parent root = loader.load();
            Scene scene = model.getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + e.getMessage());
        }
    }


    private Image createColorImage(Color color) {
        WritableImage image = new WritableImage(200, 150);
        PixelWriter pw = image.getPixelWriter();

        for (int y = 0; y < 150; y++) {
            for (int x = 0; x < 200; x++) {
                pw.setColor(x, y, color);
            }
        }

        return image;
    }



    @FXML
    private void onAssistClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Stream.fxml"));
            Parent root = loader.load();
            Scene scene = model.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }

    @FXML
    private void onSettingsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("choose-image.fxml"));
            Parent root = loader.load();
            Scene scene = model.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }

}
