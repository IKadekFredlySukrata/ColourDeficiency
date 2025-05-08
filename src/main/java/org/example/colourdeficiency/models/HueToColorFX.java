package org.example.colourdeficiency.models;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class HueToColorFX {
    // Get a single color from hue value
    public static Color getColorFromHue(float hueDegrees) {
        // Degrees (0–360)
        double saturation = 1.0;
        double brightness = 1.0;

        return Color.hsb(hueDegrees, saturation, brightness);
    }

    // Generate a list of 15 colors between start and end hue
    public static List<Color> generateHueTray(float hueStartDeg, float hueEndDeg) {
        List<Color> tray = new ArrayList<>();
        int numColors = 15;

        float step = (hueEndDeg - hueStartDeg) / (numColors - 1);

        for (int i = 0; i < numColors; i++) {
            float currentHue = hueStartDeg + (i * step);
            tray.add(getColorFromHue(currentHue));
        }

        return tray;
    }
}
