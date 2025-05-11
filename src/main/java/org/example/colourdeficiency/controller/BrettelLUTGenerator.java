package org.example.colourdeficiency.controller;

import java.io.FileWriter;
import java.io.IOException;
import org.example.colourdeficiency.models.NewColorBlindFormula;

public class BrettelLUTGenerator {
    public static void generate(String filename, String type, double severity) throws IOException {
        int size = 17;
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("TITLE \"" + type + " " + severity + "\"\n");
            writer.write("LUT_3D_SIZE " + size + "\n");

            for (int r = 0; r < size; r++) {
                for (int g = 0; g < size; g++) {
                    for (int b = 0; b < size; b++) {
                        int[] input = {
                                (int) Math.round(r * 255.0 / (size - 1)),
                                (int) Math.round(g * 255.0 / (size - 1)),
                                (int) Math.round(b * 255.0 / (size - 1))
                        };
                        int[] result = NewColorBlindFormula.brettel(input, type, severity);
                        writer.write(
                                String.format("%.6f %.6f %.6f\n",
                                        result[0] / 255.0,
                                        result[1] / 255.0,
                                        result[2] / 255.0
                                )
                        );
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        generate("tritanopia_1.0.cube", "tritan", 1.0);
    }
}

