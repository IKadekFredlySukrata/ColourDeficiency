package org.example.colourdeficiency.controller;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import org.example.colourdeficiency.models.NewColorBlindFormula;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.*;

import java.util.logging.Logger;

public class ScreenFilterApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(ScreenFilterApp.class.getName());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ImageView imageView;
    private Stage stage;
    private Robot robot;

    public ScreenFilterApp() throws AWTException {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            logException("Failed to initialize Robot", e);
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
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

        Rectangle2D bounds = getUnionScreenBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.show();

        Platform.runLater(() -> makeWindowClickThrough("ScreenFilterApp"));

        startCaptureLoop(bounds);
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

    private void startCaptureLoop(Rectangle2D screenBounds) {
        executor.scheduleAtFixedRate(() -> {
            CompletableFuture
                    .supplyAsync(() -> captureAndFilterScreen(screenBounds))
                    .thenAccept(filteredImage -> {
                        if (filteredImage != null) {
                            Platform.runLater(() -> imageView.setImage(filteredImage));
                        }
                    });
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    private WritableImage captureAndFilterScreen(Rectangle2D bounds) {
        try {
            BufferedImage capture = robot.createScreenCapture(
                    new Rectangle((int) bounds.getMinX(), (int) bounds.getMinY(),
                            (int) bounds.getWidth(), (int) bounds.getHeight()));

            BufferedImage filtered = applyColorBlindFilterBuffered(capture, "protan", 1.0);
            return convertBufferedToWritable(filtered);
        } catch (Exception e) {
            logException("Screen capture failed", e);
            return null;
        }
    }

    private Image bufferedImageToFXImage(BufferedImage image) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            return new Image(new ByteArrayInputStream(os.toByteArray()));
        } catch (Exception e) {
            logException("Image conversion failed", e);
            return null;
        }
    }
    private WritableImage convertBufferedToWritable(BufferedImage image) {
        WritableImage fxImage = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter writer = fxImage.getPixelWriter();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
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

                int[] filtered = NewColorBlindFormula.brettel(new int[]{r, g, b}, type, severity);
                int newArgb = (0xFF << 24) | (filtered[0] << 16) | (filtered[1] << 8) | filtered[2];
                output.setRGB(x, y, newArgb);
            }
        }
        return output;
    }


    private void makeWindowClickThrough(String title) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, title);
                if (hwnd == null) {
                    LOGGER.warning("Window not found: " + title);
                    return;
                }

                int exStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
                exStyle |= WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
                User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, exStyle);

                WinDef.HWND HWND_TOPMOST = new WinDef.HWND(Pointer.createConstant(-1));
                User32.INSTANCE.SetWindowPos(hwnd, HWND_TOPMOST, 0, 0, 0, 0,
                        WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE);
            } catch (Exception e) {
                logException("Failed to set click-through", e);
            }
        }).start();
    }

    private void logException(String message, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        LOGGER.severe(message + ":\n" + sw.toString());
    }

    @Override
    public void stop() throws Exception {
        executor.shutdownNow();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}