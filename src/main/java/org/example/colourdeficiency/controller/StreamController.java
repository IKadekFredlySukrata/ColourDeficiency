package org.example.colourdeficiency.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class StreamController implements Initializable {

    @FXML
    private ImageView desktopView;

    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startStreaming();
    }

    private void startStreaming() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Robot robot = new Robot();
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage screenCapture = robot.createScreenCapture(screenRect);

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(screenCapture, "png", os);
                    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                    Image fxImage = new Image(is);

                    Platform.runLater(() -> desktopView.setImage(fxImage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 100);
    }

    public void stopStreaming() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
