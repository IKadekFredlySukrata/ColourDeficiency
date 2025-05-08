package org.example.colourdeficiency.models;

import java.util.*;

public class Formulas {
    static double LM = 2.02344;
    static double LS = 2.52581;
    static double ML = 0.49421;
    static double MS = 1.24827;
    static double SL = -0.39591;
    static double SM = 0.80111;
    static double Lb;
    static double Mb;
    static double Sb;
    static int R_blind;
    static int G_blind;
    static int B_blind;

    public static void LBlind(double M, double S){
        Lb =  (LM * M) - (LS * S);
    }
    public static void MBlind(double L, double S){
        Mb = (ML * L) + (MS * S);
    }
    public static void SBlind(double L, double M){
        Sb = (SL * L) + (SM * M);
    }
    public static int[] simulate_Blind(int R, int G, int B, double severity, String blindType){
        double Rl = Math.pow(R / 255.0, 2.2);
        double Gl = Math.pow(G / 255.0, 2.2);
        double Bl = Math.pow(B / 255.0, 2.2);

        double L = 0.31399 * Rl + 0.63951 * Gl + 0.04650 * Bl;
        double M = 0.15537 * Rl + 0.75789 * Gl + 0.08670 * Bl;
        double S = 0.01775 * Rl + 0.10944 * Gl + 0.87257 * Bl;

        double blind = 0;
        do {
            if (Objects.equals(blindType, "L")){
                LBlind(M, S);
                blind = (1 - severity) * L + severity * Lb;
            } else if (Objects.equals(blindType, "M")) {
                MBlind(L, S);
                blind = (1 - severity) * M + severity * Mb;
            } else if (Objects.equals(blindType, "S")) {
                SBlind(L, M);
                blind = (1 - severity) * M + severity * Sb;
            }
        } while (blind == 0);


        double Rl_blind = 5.47221 * blind - 4.64196 * M + 0.16964 * S;
        double Gl_blind = -1.12524 * blind + 2.29317 * M - 0.16790 * S;
        double Bl_blind = 0.02980 * blind - 0.19318 * M + 1.16365 * S;

        double Rm_blind = 5.47221 * L - 4.64196 * blind + 0.16964 * S;
        double Gm_blind = -1.12524 * L + 2.29317 * blind - 0.16790 * S;
        double Bm_blind = 0.02980 * L - 0.19318 * blind + 1.16365 * S;

        double Rs_blind = 5.47221 * L - 4.64196 * M + 0.16964 * blind;
        double Gs_blind = -1.12524 * L + 2.29317 * M - 0.16790 * blind;
        double Bs_blind = 0.02980 * L - 0.19318 * M + 1.16365 * blind;

        switch (blindType) {
            case "L" -> {
                R_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Rl_blind)), 1 / 2.2) * 255);
                G_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Gl_blind)), 1 / 2.2) * 255);
                B_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Bl_blind)), 1 / 2.2) * 255);
            }
            case "M" -> {
                R_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Rm_blind)), 1 / 2.2) * 255);
                G_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Gm_blind)), 1 / 2.2) * 255);
                B_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Bm_blind)), 1 / 2.2) * 255);
            }
            case "S" -> {
                R_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Rs_blind)), 1 / 2.2) * 255);
                G_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Gs_blind)), 1 / 2.2) * 255);
                B_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Bs_blind)), 1 / 2.2) * 255);
            }
        }

        R_blind = Math.min(Math.max(R_blind, 0), 255);
        G_blind = Math.min(Math.max(G_blind, 0), 255);
        B_blind = Math.min(Math.max(B_blind, 0), 255);

        return new int[]{R_blind, G_blind, B_blind};
    }
    public static double colorDistance(int[] rgb1, int[] rgb2) {
        int dr = rgb1[0] - rgb2[0];
        int dg = rgb1[1] - rgb2[1];
        int db = rgb1[2] - rgb2[2];
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static List<int[]> findDistinctSimilarColors(int r, int g, int b, double severity, int count, String blindType) {
        int[] targetBlind = simulate_Blind(r, g, b, severity, blindType);
        List<int[]> candidates = new ArrayList<>();
        Random random = new Random();

        while (candidates.size() < count) {
            int R = random.nextInt(256);
            int G = random.nextInt(256);
            int B = random.nextInt(256);

            int[] blindVersion = simulate_Blind(r, g, b, severity, blindType);

            double blindDistance = colorDistance(blindVersion, targetBlind);
            double originalDistance = colorDistance(new int[]{R, G, B}, new int[]{r, g, b});

            if (blindDistance < 50 && originalDistance > 150) {
                candidates.add(new int[]{R, G, B});
            }
        }

        return candidates;
    }
    public static void main(String[] args) {
        int[] original = {0, 0, 255};
        double severity = 1;
        int count = 10;
        String blind = "M";
        List<int[]> colorRGB = Collections.singletonList(simulate_Blind(original[0], original[1], original[2], severity, blind));
        for (int[] rgb : colorRGB) {
            System.out.printf("Blind RGB: (%d, %d, %d)%n", rgb[0], rgb[1], rgb[2]);
        }
        List<int[]> results = findDistinctSimilarColors(original[0], original[1], original[2], severity, count, blind);
        for (int[] rgb : results) {
            System.out.printf("Found RGB: (%d, %d, %d)%n", rgb[0], rgb[1], rgb[2]);
        }
    }
}
