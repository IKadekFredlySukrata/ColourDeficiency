package org.example.colourdeficiency;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.example.colourdeficiency.models.Variable;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/resources/theme/dark.css")).toExternalForm());
        Image icon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/resources/images/logo.jpeg")));
        stage.getIcons().add(icon);
        stage.setTitle("ColorBlind Simulator");
        stage.setScene(scene);
        stage.show();
        Variable.UpdateScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}