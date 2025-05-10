package org.example.colourdeficiency.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.colourdeficiency.models.NewColorBlindFormula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConverter {
    @FXML
    private Button openButton;
    @FXML
    private Button protanButton;
    @FXML
    private Button deutanButton;
    @FXML
    private Button tritanButton;
    @FXML
    private Button achromaButton;
    @FXML
    private ImageView imageView;

    private BufferedImage originalImage;

    @FXML
    private void initialize() {
        openButton.setOnAction(e -> openImage());
        protanButton.setOnAction(e -> applyFilter("protan"));
        deutanButton.setOnAction(e -> applyFilter("deutan"));
        tritanButton.setOnAction(e -> applyFilter("tritan"));
        achromaButton.setOnAction(e -> applyAchromatopsia());
    }

    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                originalImage = ImageIO.read(file);
                imageView.setImage(new Image(file.toURI().toString()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void applyFilter(String type) {
        if (originalImage == null) return;
        BufferedImage processedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int rgb = originalImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int[] newRgb = NewColorBlindFormula.brettel(new int[]{r, g, b}, type, 1.0);
                int newColor = (newRgb[0] << 16) | (newRgb[1] << 8) | newRgb[2];
                processedImage.setRGB(x, y, newColor);
            }
        }
        displayImage(processedImage);
    }

    private void applyAchromatopsia() {
        if (originalImage == null) return;
        BufferedImage processedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int rgb = originalImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int[] newRgb = NewColorBlindFormula.monochrome_with_severity(new int[]{r, g, b}, 1.0);
                int newColor = (newRgb[0] << 16) | (newRgb[1] << 8) | newRgb[2];
                processedImage.setRGB(x, y, newColor);
            }
        }
        displayImage(processedImage);
    }

    private void displayImage(BufferedImage image) {
        try {
            File tempFile = File.createTempFile("processed_image", ".png");
            ImageIO.write(image, "png", tempFile);
            imageView.setImage(new Image(tempFile.toURI().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
