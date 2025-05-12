package org.example.colourdeficiency.models;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HueToColorFX {
    public static WritableImage createColorImage(int r, int g, int b){
        WritableImage image = new WritableImage(200, 150);

        PixelWriter pw = image.getPixelWriter();

        Color color = Color.rgb(r, g, b);

        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 150; j++) {
                pw.setColor(i, j, color);
            }
        }



        return image;
    }

    public static List<Color> getRandomizedTray(List<Color> originalTray){
        List<Color> randomizedTray = new ArrayList<>();
        randomizedTray.add(originalTray.getFirst());

        List<Color> middle = new ArrayList<>(originalTray.subList(1, originalTray.size() - 1));
        Collections.shuffle(middle);

        randomizedTray.addAll(middle);
        randomizedTray.add(originalTray.getLast());

        return randomizedTray;
    }

    public static List<Color> getColorRGB(int phase) {
        List<Color> tray = new ArrayList<>();

        if (phase == 1) {
            int R = 255;
            int B = 0;

            for (int i = 0; i <= 255; i += 17) {
                tray.add(Color.rgb(R, i, B));
            }

        } else if (phase == 2) {
            int G = 255;
            int B = 0;

            for (int i = 255; i >= 0; i -= 17) {
                tray.add(Color.rgb(i, G, B));
            }
        } else if (phase == 3) {
            int G = 255;
            int R = 0;

            for (int i = 0; i <= 255; i += 17) {
                tray.add(Color.rgb(R, G, i));
            }
        } else if (phase == 4) {
            int R = 0;
            int B = 255;

            for (int i = 255; i >= 0; i -= 17) {
                tray.add(Color.rgb(R, i, B));
            }
        }
        return tray;
    }
}