package org.example.colourdeficiency.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

public class OutputController {

    @FXML
    private Label resultLabel;

    public void initialize() {

    }
    public void setResult(String result) {
        resultLabel.setText(result);
    }

    public void Back(ActionEvent actionEvent) {
        // navigation logic
    }

    public void LUT(ActionEvent actionEvent) {
        // LUT logic
    }

    public void Simulate(ActionEvent actionEvent) {
        // simulation logic
    }
}
