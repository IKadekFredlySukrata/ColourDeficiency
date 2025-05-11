package org.example.colourdeficiency.controller;

import java.util.concurrent.CompletableFuture;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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

public class ScreenFilterApp extends Application {
    private ImageView imageView;
    private Stage primaryStage;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void start(Stage stage) throws AWTException {
        this.primaryStage = stage;
        imageView = new ImageView();
        Pane root = new Pane(imageView);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);
        stage.setTitle("ScreenFilterApp");
        scene.setFill(Color.TRANSPARENT);


        Rectangle2D allBounds = getScreenUnionBounds(Screen.getScreens());
        stage.setX(allBounds.getMinX());
        stage.setY(allBounds.getMinY());
        stage.setWidth(allBounds.getWidth());
        stage.setHeight(allBounds.getHeight());

        Platform.runLater(() -> {
            makeWindowTransparent("ScreenFilterApp");
            PauseTransition delay = new PauseTransition(Duration.millis(500));
            delay.setOnFinished(e -> primaryStage.setOpacity(1.0));
            delay.play();
        });

        scene.setFill(Color.TRANSPARENT);
        root.setMouseTransparent(true);


        stage.show();

        Platform.runLater(() -> makeWindowTransparent("ScreenFilterApp"));

        // Start real-time update loop
        startFilterLoop();
    }
    private Rectangle2D getScreenUnionBounds(List<Screen> screens) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Screen s : screens) {
            Rectangle2D b = s.getBounds();
            minX = Math.min(minX, b.getMinX());
            minY = Math.min(minY, b.getMinY());
            maxX = Math.max(maxX, b.getMaxX());
            maxY = Math.max(maxY, b.getMaxY());
        }

        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }
    private void startFilterLoop() {
        executorService.scheduleAtFixedRate(() -> {
            CompletableFuture.supplyAsync(() -> {
                try {
                    Robot robot = createRobot();
                    if (robot == null) return null;

                    List<Screen> screens = Screen.getScreens();
                    Rectangle2D unionBounds = getScreenUnionBounds(screens);
                    BufferedImage combinedImage = new BufferedImage(
                            (int) unionBounds.getWidth(),
                            (int) unionBounds.getHeight(),
                            BufferedImage.TYPE_INT_ARGB
                    );

                    Graphics2D g2d = combinedImage.createGraphics();

                    for (Screen screen : screens) {
                        Rectangle2D bounds = screen.getBounds();
                        Rectangle screenRect = new Rectangle(
                                (int) bounds.getMinX(),
                                (int) bounds.getMinY(),
                                (int) bounds.getWidth(),
                                (int) bounds.getHeight()
                        );
                        BufferedImage screenCapture = robot.createScreenCapture(screenRect);
                        g2d.drawImage(screenCapture,
                                (int) (bounds.getMinX() - unionBounds.getMinX()),
                                (int) (bounds.getMinY() - unionBounds.getMinY()), null);
                    }

                    g2d.dispose();

                    Image fxImage = bufferedImageToFXImage(combinedImage);
                    return applyColorBlindFilter(fxImage, "protan", 1.0);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).thenAccept(filtered -> {
                if (filtered != null) {
                    Platform.runLater(() -> {
                        imageView.setImage(filtered);
                        primaryStage.setOpacity(1.0); // Show overlay again
                    });
                }
            });
        }, 0, 50, TimeUnit.MILLISECONDS); // ~20 FPS
    }



    private Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void makeWindowTransparent(String windowTitle) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        if (hwnd != null) {
            int style = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
            style |= 0x80000 | 0x20; // WS_EX_LAYERED | WS_EX_TRANSPARENT | WS_EX_NOACTIVATE
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, style);

            // HWND_TOPMOST = (HWND) -1
            WinDef.HWND HWND_TOPMOST = new WinDef.HWND(Pointer.createConstant(-1));

            User32.INSTANCE.SetWindowPos(
                    hwnd, HWND_TOPMOST,
                    0, 0, 0, 0,
                    WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE
            );
        } else {
            System.out.println("HWND not found for title: " + windowTitle);
        }

        Platform.runLater(() -> primaryStage.setTitle(""));
    }


    private Image bufferedImageToFXImage(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", os);
            return new Image(new ByteArrayInputStream(os.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private WritableImage applyColorBlindFilter(Image image, String type, double severity) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                int[] rgb = {
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255)
                };
                int[] filtered = NewColorBlindFormula.brettel(rgb, type, severity);
                Color newColor = Color.rgb(filtered[0], filtered[1], filtered[2], 1.0);
                writer.setColor(x, y, newColor);
            }
        }
        return writableImage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
