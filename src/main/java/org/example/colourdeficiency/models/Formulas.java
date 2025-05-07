package org.example.colourdeficiency.models;

import java.util.Objects;

public class Formulas {
    double LM = 2.02344;
    double LS = 2.52581;
    double ML = 0.49421;
    double MS = 1.24827;
    double SL = -0.39591;
    double SM = 0.80111;
    double Lb, Mb, Sb;
    int R_blind;
    int G_blind;
    int B_blind;

    public void LBlind(double M, double S){
        Lb =  (LM * M) - (LS * S);
    }
    public void MBlind(double L, double S){
        Mb = (ML * L) + (MS * S);
    }
    public void SBlind(double L, double M){
        Sb = (SL * L) + (SM * M);
    }
    public int[] simulate_Blind(int R, int G, int B, double severity, String blindType){
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
                blind = (1 - severity) * M + severity * Mb;
            }
        } while (blind == 0);


        double Rl_blind = 5.47221 * blind - 4.64196 * M + 0.16964 * S;
        double Gl_blind = -1.12524 * blind + 2.29317 * M - 0.16790 * S;
        double Bl_blind = 0.02980 * blind - 0.19318 * M + 1.16365 * S;

        R_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Rl_blind)), 1 / 2.2) * 255);
        G_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Gl_blind)), 1 / 2.2) * 255);
        B_blind = (int) Math.round(Math.pow(Math.max(0, Math.min(1, Bl_blind)), 1 / 2.2) * 255);

        R_blind = Math.min(Math.max(R_blind, 0), 255);
        G_blind = Math.min(Math.max(G_blind, 0), 255);
        B_blind = Math.min(Math.max(B_blind, 0), 255);

        return new int[]{R_blind, G_blind, B_blind};
    }
}
