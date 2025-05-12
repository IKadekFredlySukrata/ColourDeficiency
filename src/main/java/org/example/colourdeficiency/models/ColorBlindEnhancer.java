package org.example.colourdeficiency.models;

public class ColorBlindEnhancer {

    public static int[] enhanceForProtan(int[] rgb, double level) {
        return enhance(rgb, "protan", level);
    }

    public static int[] enhanceForDeutan(int[] rgb, double level) {
        return enhance(rgb, "deutan", level);
    }

    public static int[] enhanceForTritan(int[] rgb, double level) {
        return enhance(rgb, "tritan", level);
    }

    public static int[] enhanceForMonochrome(int[] rgb, double level) {
        int gray = (int) (rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114);
        return new int[] {
                clamp((int)(gray + (rgb[0] - gray) * level)),
                clamp((int)(gray + (rgb[1] - gray) * level)),
                clamp((int)(gray + (rgb[2] - gray) * level))
        };
    }

    private static int[] enhance(int[] rgb, String type, double level) {
        float[] hsl = rgbToHsl(rgb);

        // General saturation and brightness boost
        hsl[1] = clamp(hsl[1] * (1.0f + (float) level * 0.5f)); // Saturation
        hsl[2] = clamp(hsl[2] + (float) level * 0.1f);          // Brightness

        // Type-specific hue shifting
        switch (type) {
            case "protan" -> hsl[0] = (hsl[0] + 15f * (float) level) % 360f;
            case "deutan" -> hsl[0] = (hsl[0] + 10f * (float) level) % 360f;
            case "tritan" -> hsl[0] = (hsl[0] + 25f * (float) level) % 360f;
        }

        return hslToRgb(hsl);
    }

    // --- Utility Methods ---

    private static int clamp(int value) {
        return Math.min(255, Math.max(0, value));
    }

    private static float clamp(float value) {
        return Math.min(1.0f, Math.max(0.0f, value));
    }

    public static float[] rgbToHsl(int[] rgb) {
        float r = rgb[0] / 255f, g = rgb[1] / 255f, b = rgb[2] / 255f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l = (max + min) / 2f;

        if (max == min) {
            h = s = 0f;
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);
            if (max == r) {
                h = ((g - b) / d + (g < b ? 6f : 0f)) * 60f;
            } else if (max == g) {
                h = ((b - r) / d + 2f) * 60f;
            } else {
                h = ((r - g) / d + 4f) * 60f;
            }
        }
        return new float[] { h, s, l };
    }

    public static int[] hslToRgb(float[] hsl) {
        float h = hsl[0], s = hsl[1], l = hsl[2];

        float r, g, b;
        if (s == 0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1 + s) : (l + s - l * s);
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 120f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 120f);
        }

        return new int[] {
                clamp(Math.round(r * 255)),
                clamp(Math.round(g * 255)),
                clamp(Math.round(b * 255))
        };
    }

    private static float hueToRgb(float p, float q, float t) {
        t = (t % 360f + 360f) % 360f;
        if (t < 60f) return p + (q - p) * t / 60f;
        if (t < 180f) return q;
        if (t < 240f) return p + (q - p) * (240f - t) / 60f;
        return p;
    }
}
