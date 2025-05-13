package org.example.colourdeficiency.controller;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
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

public class ScreenFilterApp {

    private static final Logger LOGGER = Logger.getLogger(ScreenFilterApp.class.getName());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ImageView imageView;
    private Robot robot;
    private static String type = "protan";
    private static double severity = 1.0;
    private boolean clickThroughEnabled = true;
    Rectangle2D bounds = getUnionScreenBounds();
    public ScreenFilterApp() throws AWTException {
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
                makeWindowClickThrough("ScreenFilterApp");
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
                stage.hide();
                System.out.println("Overlay closed and executor shut down.");
            }
        });

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
                    filtered = NewColorBlindFormula.brettel(new int[]{r, g, b}, type, severity);
                }

                int newArgb = (0xFF << 24) | (filtered[0] << 16) | (filtered[1] << 8) | filtered[2];
                output.setRGB(x, y, newArgb);
            }
        }
        return output;
    }

    private void makeWindowClickThrough(String windowTitle) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);

                final int[] windowFound = {0};
                User32.INSTANCE.EnumWindows((hwnd, data) -> {
                    char[] windowText = new char[512];
                    User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
                    String wText = Native.toString(windowText);
                    System.out.println("Window: " + wText);

                    if (wText != null && wText.contains(windowTitle)) {
                        makeClickThrough(hwnd);
                        windowFound[0]++;
                        return false;
                    }
                    return true;
                }, null);

                if (windowFound[0] == 0) {
                    LOGGER.warning("Window not found via enumeration.");
                }

            } catch (Exception e) {
                logException("Failed to set click-through", e);
            }
        }).start();
    }


    private void makeClickThrough(WinDef.HWND hwnd) {
        int exStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        if (clickThroughEnabled) {
            exStyle |= WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
            LOGGER.info("Click-through ENABLED for: " + hwnd);
        } else {
            exStyle &= ~WinUser.WS_EX_TRANSPARENT;
            LOGGER.info("Click-through DISABLED for: " + hwnd);
        }

        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, exStyle);

        User32.INSTANCE.SetWindowPos(hwnd, new WinDef.HWND(Pointer.createConstant(-1)), 0, 0, 0, 0,
                WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE | WinUser.SWP_NOZORDER);
    }


    private void logException(String message, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        LOGGER.severe(message + ":\n" + sw.toString());
    }

}
