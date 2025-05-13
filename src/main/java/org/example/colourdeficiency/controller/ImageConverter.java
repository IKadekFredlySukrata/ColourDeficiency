package org.example.colourdeficiency.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.colourdeficiency.Main;
import org.example.colourdeficiency.models.NewColorBlindFormula;
import org.example.colourdeficiency.models.Variable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConverter {
    public ImageView OriimageView;
    public Slider severitySlider;
    public ImageView yourView;
    private static double YSeverity;
    private static String YType;
    private static double severity;
    private static String type;
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
    public static double getYourSeverity(){
        return YSeverity;
    }
    public static String getYourType(){
        return YType;
    }
    public static double getSeverity(){
        return severity;
    }
    public static String getType(){
        return type;
    }
    @FXML
    private void initialize() {
        severity = severitySlider.getValue();
        severitySlider.valueProperty().addListener((obs, oldVal, newVal) -> severity = newVal.doubleValue());
        openButton.setOnAction(e -> openImage());
        protanButton.setOnAction(e -> {applyFilter("protan", severity, 0); type = "protan";});
        deutanButton.setOnAction(e -> {applyFilter("deutan", severity, 0); type = "deutan";});
        tritanButton.setOnAction(e -> {applyFilter("tritan", severity, 0); type = "tritan";});
        achromaButton.setOnAction(e -> {applyAchromatopsia(severity, 0); type = "Achromatopsia";});
    }

    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                originalImage = ImageIO.read(file);
                imageView.setImage(new Image(file.toURI().toString()));
                OriimageView.setImage(new Image(file.toURI().toString()));
                yourView.setImage(new Image(file.toURI().toString()));
                if ("protan".equals(YType) || "deutan".equals(YType) || "tritan".equals(YType)) {
                    applyFilter(YType, YSeverity, 1);
                } else if ("Achromatopsia".equals(YType)){
                    applyAchromatopsia(YSeverity, 1);
                }
            } catch (IOException ignored) {}
        }
    }

    private void applyFilter(String type, double Severity, int num) {
        if (originalImage == null) return;
        BufferedImage processedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int rgb = originalImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int[] newRgb = NewColorBlindFormula.brettel(new int[]{r, g, b}, type, Severity);
                int newColor = (newRgb[0] << 16) | (newRgb[1] << 8) | newRgb[2];
                processedImage.setRGB(x, y, newColor);
            }
        }
        switch (num) {
            case 0:
                displayImage(processedImage);
                break;
            case 1:
                displayYour(processedImage);
                break;
        }
    }


    private void applyAchromatopsia(double Severity, int num) {
        if (originalImage == null) return;

        BufferedImage processedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int rgb = originalImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int[] newRgb = NewColorBlindFormula.monochrome_with_severity(new int[]{r, g, b}, Severity);
                int newColor = (newRgb[0] << 16) | (newRgb[1] << 8) | newRgb[2];
                processedImage.setRGB(x, y, newColor);
            }
        }
        switch (num) {
            case 0:
                displayImage(processedImage);
                break;
            case 1:
                displayYour(processedImage);
                break;
        }
    }

    private void displayImage(BufferedImage image) {
        try {
            File tempFile = File.createTempFile("processed_image", ".png");
            ImageIO.write(image, "png", tempFile);
            imageView.setImage(new Image(tempFile.toURI().toString()));
        } catch (IOException ignored) {}
    }
    private void displayYour(BufferedImage image) {
        try {
            File tempFile = File.createTempFile("processed_image", ".png");
            ImageIO.write(image, "png", tempFile);
            yourView.setImage(new Image(tempFile.toURI().toString()));
        } catch (IOException ignored) {}
    }

    public static void setSeverity(double severity) {
        YSeverity = severity;
    }
    public static void setType(String type){
        YType = type;
    }

    public void Custom() throws AWTException {
        new ScreenFilterApp().start(new Stage(), 0);
    }

    public void Your() throws AWTException {
        new ScreenFilterApp().start(new Stage(), 1);
    }

    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }
}
