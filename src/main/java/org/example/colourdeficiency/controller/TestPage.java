package org.example.colourdeficiency.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.colourdeficiency.models.HueToColorFX;

import java.util.ArrayList;
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
    List<Integer> RGBSeverity = new ArrayList<>();

    private List<ImageView> imageViews;

    @FXML
    private void initialize() {
        System.out.println("TestPage initialized");

        imageViews = List.of(
                imageView1, imageView2, imageView3, imageView4, imageView5, imageView6,
                imageView7, imageView8, imageView9, imageView10, imageView11, imageView12,
                imageView13, imageView14, imageView15, imageView16
        );

        // Add visual indicators to anchors
        imageViews.get(0).setStyle("-fx-border-color: gray; -fx-border-width: 2;");
        imageViews.get(imageViews.size() - 1).setStyle("-fx-border-color: gray; -fx-border-width: 2;");

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
                        setupImageSwap(imageViews.get(j));
                    }
                });

                if (i == 4) {
                    RGBSeverity.add(getUserSeverity(RGBValue, getUserResult()));
                    for (Integer integer : RGBSeverity) {
                        System.out.println(integer);
                    }
                    String result = determineDeficiency(RGBSeverity);
                    System.out.println(result);
                } else {
                    waitForButtonPress();
                    RGBSeverity.add(getUserSeverity(RGBValue, getUserResult()));
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

    // Anchor checker
    private boolean isAnchor(ImageView iv) {
        return iv == imageViews.get(0) || iv == imageViews.get(imageViews.size() - 1);
    }

    private void setupImageSwap(ImageView iv) {
        iv.setOnMouseClicked(event -> {
            if (iv.getImage() == null || isAnchor(iv)) return;

            if (firstSelected == null) {
                firstSelected = iv;
                iv.setStyle("-fx-border-color: blue; -fx-border-width: 3;");
            } else if (firstSelected == iv) {
                iv.setStyle("");
                firstSelected = null;
            } else {
                if (isAnchor(firstSelected)) {
                    firstSelected.setStyle("");
                    firstSelected = null;
                    return;
                }

                Image temp = iv.getImage();
                iv.setImage(firstSelected.getImage());
                firstSelected.setImage(temp);

                firstSelected.setStyle("");
                iv.setStyle("");
                firstSelected = null;
            }
        });
    }

    private List<Color> getUserResult() {
        List<Color> userResult = new ArrayList<>();
        for (ImageView iv : imageViews) {
            if (iv.getImage() != null) {
                Color color = iv.getImage().getPixelReader().getColor(0, 0);
                int r = (int) (color.getRed() * 255);
                int g = (int) (color.getGreen() * 255);
                int b = (int) (color.getBlue() * 255);
                userResult.add(Color.rgb(r, g, b));
            }
        }
        return userResult;
    }

    private int getUserSeverity(List<Color> RGBValue, List<Color> userResult) {
        int severity = 0;

        if (RGBValue.size() == userResult.size()) {
            for (int i = 0; i < RGBValue.size(); i++) {
                Color expected = RGBValue.get(i);
                Color actual = userResult.get(i);
                if (!expected.equals(actual)) {
                    severity++;
                }
            }
        } else {
            System.out.println("Some value is missing");
        }
        return severity;
    }

    public String determineDeficiency(List<Integer> RGBSeverity) {
        String result = "Normal Vision";

        int protanSeverity = RGBSeverity.get(0);
        int deutanSeverity = RGBSeverity.get(1);
        int tritanSeverity = RGBSeverity.get(2);

        if (protanSeverity > 3 && protanSeverity > deutanSeverity && protanSeverity > tritanSeverity) {
            result = "Protanomaly / Protanopia suspected";
        } else if (deutanSeverity > 3 && deutanSeverity > protanSeverity && deutanSeverity > tritanSeverity) {
            result = "Deuteranomaly / Deuteranopia suspected";
        } else if (tritanSeverity > 3 && tritanSeverity > protanSeverity && tritanSeverity > deutanSeverity) {
            result = "Tritanomaly / Tritanopia suspected";
        } else if (protanSeverity > 3 && deutanSeverity > 3) {
            result = "Possible Red-Green Color Blindness (Mixed Protan & Deutan)";
        } else if (protanSeverity <= 3 && deutanSeverity <= 3 && tritanSeverity <= 3) {
            result = "Likely Normal Vision";
        } else {
            result = "Indeterminate, retest recommended";
        }

        return result;
    }

}