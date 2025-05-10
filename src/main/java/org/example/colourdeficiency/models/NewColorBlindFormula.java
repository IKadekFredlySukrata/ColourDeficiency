package org.example.colourdeficiency.models;

import java.io.IOException;
import java.util.Arrays;

public class NewColorBlindFormula {
    private static final double[] sRGB_to_linearRGB_Lookup = new double[256];

    static {
        for (int i = 0; i < 256; i++) {
            sRGB_to_linearRGB_Lookup[i] = linearRGB_from_sRGB(i);
        }
    }

    public static double linearRGB_from_sRGB(int v) {
        double fv = v / 255.0;
        return (fv < 0.04045) ? (fv / 12.92) : Math.pow((fv + 0.055) / 1.055, 2.4);
    }

    public static int sRGB_from_linearRGB(double v) {
        if (v <= 0.0) return 0;
        if (v >= 1.0) return 255;
        return (int) Math.round(v < 0.0031308 ? (v * 12.92 * 255) : (255 * (Math.pow(v, 1.0 / 2.4) * 1.055 - 0.055)));
    }

    public static int[] brettel(int[] srgb, String type, double severity) {
        double[] rgb = {
                sRGB_to_linearRGB_Lookup[srgb[0]],
                sRGB_to_linearRGB_Lookup[srgb[1]],
                sRGB_to_linearRGB_Lookup[srgb[2]]
        };

        double[] params = getBrettelParams(type, rgb);
        double[] rgbCvd = new double[3];

        rgbCvd[0] = params[0] * rgb[0] + params[1] * rgb[1] + params[2] * rgb[2];
        rgbCvd[1] = params[3] * rgb[0] + params[4] * rgb[1] + params[5] * rgb[2];
        rgbCvd[2] = params[6] * rgb[0] + params[7] * rgb[1] + params[8] * rgb[2];

        for (int i = 0; i < 3; i++) {
            rgbCvd[i] = rgbCvd[i] * severity + rgb[i] * (1.0 - severity);
        }

        return new int[] {
                sRGB_from_linearRGB(rgbCvd[0]),
                sRGB_from_linearRGB(rgbCvd[1]),
                sRGB_from_linearRGB(rgbCvd[2])
        };
    }

    private static double[] getBrettelParams(String type, double[] rgb) {
        return switch (type) {
            case "protan" -> rgb[0] * 0.00048 + rgb[1] * 0.00416 + rgb[2] * -0.00464 >= 0 ?
                    new double[]{0.14510, 1.20165, -0.34675, 0.10447, 0.85316, 0.04237, 0.00429, -0.00603, 1.00174} :
                    new double[]{0.14115, 1.16782, -0.30897, 0.10495, 0.85730, 0.03776, 0.00431, -0.00586, 1.00155};
            case "deutan" -> rgb[0] * -0.00293 + rgb[1] * -0.00645 + rgb[2] * 0.00938 >= 0 ?
                    new double[]{0.36198, 0.86755, -0.22953, 0.26099, 0.64512, 0.09389, -0.01975, 0.02686, 0.99289} :
                    new double[]{0.37009, 0.88540, -0.25549, 0.25767, 0.63782, 0.10451, -0.01950, 0.02741, 0.99209};
            case "tritan" -> rgb[0] * 0.03960 + rgb[1] * -0.02831 + rgb[2] * -0.01129 >= 0 ?
                    new double[]{1.01354, 0.14268, -0.15622, -0.01181, 0.87561, 0.13619, 0.07707, 0.81208, 0.11085} :
                    new double[]{0.93337, 0.19999, -0.13336, 0.05809, 0.82565, 0.11626, -0.37923, 1.13825, 0.24098};
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    public static int[] monochrome_with_severity(int[] srgb, double severity) {
        int z = (int) Math.round(srgb[0] * 0.299 + srgb[1] * 0.587 + srgb[2] * 0.114);
        return new int[] {
                (int) Math.round(z * severity + (1.0 - severity) * srgb[0]),
                (int) Math.round(z * severity + (1.0 - severity) * srgb[1]),
                (int) Math.round(z * severity + (1.0 - severity) * srgb[2])
        };
    }

    public static void main(String[] args) throws IOException {
        int[] color = {255, 0, 0};
        System.out.println("Protanopia: " + Arrays.toString(brettel(color, "protan", 1.0)));
        System.out.println("Deuteranopia: " + Arrays.toString(brettel(color, "deutan", 1.0)));
        System.out.println("Tritanopia: " + Arrays.toString(brettel(color, "tritan", 1.0)));
        System.out.println("Achromatopsia: " + Arrays.toString(monochrome_with_severity(color, 1.0)));
    }
}
