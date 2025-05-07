package org.example.colourdeficiency;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.colourdeficiency.models.model;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
        stage.setTitle("ColorBlind Simulator");
        stage.setScene(scene);
        stage.show();
        model.UpdateScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}