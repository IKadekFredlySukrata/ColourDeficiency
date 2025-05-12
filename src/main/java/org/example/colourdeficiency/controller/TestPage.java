package org.example.colourdeficiency.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.example.colourdeficiency.models.HueToColorFX;

import java.util.List;

public class TestPage {

    // Testing Page
    @FXML
    private ImageView imageView1;
    @FXML
    private ImageView imageView2;
    @FXML
    private ImageView imageView3;
    @FXML
    private ImageView imageView4;
    @FXML
    private ImageView imageView5;
    @FXML
    private ImageView imageView6;
    @FXML
    private ImageView imageView7;
    @FXML
    private ImageView imageView8;
    @FXML
    private ImageView imageView9;
    @FXML
    private ImageView imageView10;
    @FXML
    private ImageView imageView11;
    @FXML
    private ImageView imageView12;
    @FXML
    private ImageView imageView13;
    @FXML
    private ImageView imageView14;
    @FXML
    private ImageView imageView15;
    @FXML
    private ImageView imageView16;

    @FXML
    private void initialize() {
        System.out.println("TestPage initialized");

        List<ImageView> imageViews = List.of(imageView1, imageView2, imageView3, imageView4, imageView5,imageView6,
                                             imageView7, imageView8, imageView9, imageView10, imageView11, imageView12,
                                             imageView13, imageView14, imageView15, imageView16);

        for (int i = 1; i < 5; i++) {
            List<Color> RGBValue = HueToColorFX.getColorRGB(i);
            List<Color> RandomizeRGBValue = HueToColorFX.getRandomizedTray(RGBValue);


            for (int j = 0; j < RandomizeRGBValue.size(); j++) {
                Color firstColor = RandomizeRGBValue.get(j);
                Image image = HueToColorFX.createColorImage(
                        (int) (firstColor.getRed() * 255),
                        (int) (firstColor.getGreen() * 255),
                        (int) (firstColor.getBlue() * 255)
                );
                imageViews.get(j).setImage(image);
            }
        }

    }
}
