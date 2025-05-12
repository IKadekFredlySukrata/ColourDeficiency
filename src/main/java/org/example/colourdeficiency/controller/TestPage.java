package org.example.colourdeficiency.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.example.colourdeficiency.models.HueToColorFX;

import java.util.List;

public class TestPage {

    @FXML
    private ImageView imageView1, imageView2, imageView3, imageView4,
            imageView5, imageView6, imageView7, imageView8,
            imageView9, imageView10, imageView11, imageView12,
            imageView13, imageView14, imageView15, imageView16;

    @FXML
    private Button nextButton;

    private final SimpleBooleanProperty continueFlag = new SimpleBooleanProperty(false);
    private ImageView firstSelected = null; // Keep the selected image reference globally

    @FXML
    private void initialize() {
        System.out.println("TestPage initialized");

        List<ImageView> imageViews = List.of(
                imageView1, imageView2, imageView3, imageView4, imageView5, imageView6,
                imageView7, imageView8, imageView9, imageView10, imageView11, imageView12,
                imageView13, imageView14, imageView15, imageView16
        );

        new Thread(() -> {
            for (int i = 1; i < 5; i++) {
                List<Color> RGBValue = HueToColorFX.getColorRGB(i);
                List<Color> RandomizeRGBValue = HueToColorFX.getRandomizedTray(RGBValue);

                Platform.runLater(() -> {
                    for (int j = 0; j < RandomizeRGBValue.size(); j++) {
                        Color firstColor = RandomizeRGBValue.get(j);
                        Image image = HueToColorFX.createColorImage(
                                (int) (firstColor.getRed() * 255),
                                (int) (firstColor.getGreen() * 255),
                                (int) (firstColor.getBlue() * 255)
                        );
                        imageViews.get(j).setImage(image);

                        // Setup swap on click for each imageView
                        setupImageSwap(imageViews.get(j));
                    }
                });

                if (i == 4) {
                    // Optional: do something when finished
                } else {
                    waitForButtonPress();
                }
            }
            System.out.println("Loop finished.");
        }).start();
    }

    @FXML
    private void onNextClicked() {
        System.out.println("Next button pressed!");
        continueFlag.set(true);
    }

    private void waitForButtonPress() {
        continueFlag.set(false);
        while (!continueFlag.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void setupImageSwap(ImageView iv) {
        iv.setOnMouseClicked(event -> {
            if (iv.getImage() == null) return;

            if (firstSelected == null) {
                firstSelected = iv;
                iv.setStyle("-fx-border-color: blue; -fx-border-width: 3;");
            } else if (firstSelected == iv) {
                // Cancel if same image clicked twice
                iv.setStyle("");
                firstSelected = null;
            } else {
                // Swap images
                Image temp = iv.getImage();
                iv.setImage(firstSelected.getImage());
                firstSelected.setImage(temp);

                // Reset
                firstSelected.setStyle("");
                iv.setStyle("");
                firstSelected = null;
            }
        });
    }
}