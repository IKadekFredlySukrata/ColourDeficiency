package org.example.colourdeficiency.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import org.example.colourdeficiency.Main;
import org.example.colourdeficiency.models.Variable;

import java.io.IOException;
import java.util.Optional;

public class OutputController {
    @FXML
    private Label resultLabel;
    private static double PSeverity;
    private static double DSeverity;
    private static double TSeverity;
    private static String Type;
    private double Severity;
    public void initialize() {
    }
    public void setResult(String result) {
        conclusion();
    }
    public static void setPSeverity(double Pseverity){
        PSeverity = Pseverity;
    }
    public static void setDSeverity(double Dseverity){
        DSeverity = Dseverity;
    }
    public static void setTSeverity(double Tseverity){
        TSeverity = Tseverity;
    }
    public static void setType(String type){
        Type = type;
    }
    public void conclusion(){
        switch (Type) {
            case "Achromatopsia" -> Severity = (PSeverity + DSeverity + TSeverity) / 3;
            case "protan" -> Severity = PSeverity;
            case "deutan" -> Severity = DSeverity;
            case "tritan" -> Severity = TSeverity;
            case null, default -> Type = "normal";
        }
    }
    public void Back(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }

    public void LUT(ActionEvent actionEvent) throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("File Name Input");
        dialog.setHeaderText("Enter File Name");
        dialog.setContentText("Please enter the file name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            try {
                BrettelLUTGenerator.generate(fileName, Type, Severity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void Simulate(ActionEvent actionEvent) {
        ImageConverter.setSeverity(Severity);
        ImageConverter.setType(Type);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("choose-image.fxml"));
            Parent root = loader.load();
            Scene scene = Variable.getScene();
            scene.setRoot(root);
        } catch (IOException ignored) {}
    }
}