package org.example.colourdeficiency.controller;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.colourdeficiency.models.Formula;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ScreenFilterApp {

    private static final Logger LOGGER = Logger.getLogger(ScreenFilterApp.class.getName());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ImageView imageView;
    private Robot robot;
    private static String type = "protan";
    private static double severity = 1.0;
    private boolean clickThroughEnabled = true;
    Rectangle2D bounds = getUnionScreenBounds();
    public ScreenFilterApp() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            logException("Failed to initialize Robot", e);
        }
    }

    public void start(Stage stage, int Capture) {
        switch (Capture){
            case 0:
                severity = ImageConverter.getSeverity();
                type = ImageConverter.getType();
                break;
            case 1:
                severity = ImageConverter.getYourSeverity();
                type = ImageConverter.getYourType();
                break;
        }
        this.imageView = new ImageView();
        Pane root = new Pane(imageView);
        root.setMouseTransparent(true);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setTitle("ScreenFilterApp");

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F12) {
                clickThroughEnabled = !clickThroughEnabled;
                LOGGER.info("Click-through toggled: " + clickThroughEnabled);
            }
        });

        stage.setOnCloseRequest(event -> {
            try {
                executor.shutdownNow();
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    LOGGER.warning("Executor did not terminate in the specified time.");
                }
            } catch (InterruptedException ex) {
                logException("Executor termination interrupted", ex);
            } finally {
                imageView.setImage(null);
                stage.close();
            }
        });

        stage.show();
        startCaptureLoop();
    }

    private Rectangle2D getUnionScreenBounds() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D bounds = screen.getBounds();
            minX = Math.min(minX, bounds.getMinX());
            minY = Math.min(minY, bounds.getMinY());
            maxX = Math.max(maxX, bounds.getMaxX());
            maxY = Math.max(maxY, bounds.getMaxY());
        }
        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }

    private void startCaptureLoop() {
        executor.scheduleAtFixedRate(() -> {
            if (!executor.isShutdown()) {
                CompletableFuture
                        .supplyAsync(() -> captureAndFilterScreen(bounds))
                        .thenAccept(filteredImage -> {
                            if (filteredImage != null) {
                                Platform.runLater(() -> imageView.setImage(filteredImage));
                            }
                        });
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    private WritableImage captureAndFilterScreen(Rectangle2D bounds) {
        try {
            BufferedImage capture = robot.createScreenCapture(
                    new Rectangle((int) bounds.getMinX(), (int) bounds.getMinY(),
                            (int) bounds.getWidth(), (int) bounds.getHeight()));
            BufferedImage filtered = applyColorBlindFilterBuffered(capture, type, severity);
            return convertBufferedToWritable(filtered);
        } catch (Exception e) {
            logException("Screen capture failed", e);
            return null;
        }
    }

    private WritableImage convertBufferedToWritable(BufferedImage image) {
        WritableImage fxImage = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter writer = fxImage.getPixelWriter();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                Color fxColor = Color.rgb(
                        (argb >> 16) & 0xFF,
                        (argb >> 8) & 0xFF,
                        argb & 0xFF,
                        ((argb >> 24) & 0xFF) / 255.0
                );
                writer.setColor(x, y, fxColor);
            }
        }
        return fxImage;
    }

    private BufferedImage applyColorBlindFilterBuffered(BufferedImage image, String type, double severity) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int[] filtered;

                if ("achromatopsia".equalsIgnoreCase(type)) {
                    int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    filtered = new int[]{gray, gray, gray};
                } else {
                    filtered = Formula.brettel(new int[]{r, g, b}, type, severity);
                }

                int newArgb = (0xFF << 24) | (filtered[0] << 16) | (filtered[1] << 8) | filtered[2];
                output.setRGB(x, y, newArgb);
            }
        }
        return output;
    }

    private void logException(String message, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        LOGGER.severe(message + ":\n" + sw);
    }

}
