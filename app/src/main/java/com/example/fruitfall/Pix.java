package com.example.fruitfall;

public class Pix {
    public static int xStartSpaces = 22;
    public static int yStartSpaces = 84;
    public static int wGap = 4;
    public static int hGap = 4;
    public static int xStartField = xStartSpaces-wGap/2;
    public static int yStartField = yStartSpaces-hGap/2;
    public static int wMainSpace = 60;
    public static int hMainSpace = 60;

    public static int wSpace = wMainSpace + wGap;
    public static int hSpace = hMainSpace + hGap;
    public static int ghostSquareMargin = wSpace/2;
    public static int xAfterSpaces = xStartSpaces + Constants.FIELD_XLENGTH * wSpace - wGap; // (xAfterSpaces, yAfterSpaces NOT within X)
    public static int yAfterSpaces = yStartSpaces + Constants.FIELD_YLENGTH * hSpace - hGap;

    public static int xStartActiveLight = xStartSpaces;
    public static int yStartActiveLight = 22;
    public static float xScore = xStartSpaces + 50;
    public static float hText = yStartSpaces * (float)0.4;
    public static float yScore = 10+hText;
    public static float xTime = xScore + 300;
    public static float yTime = yScore;
    public static float xCommand1 = xScore;
    public static float yCommand1 = yScore + hText*(float)1.2;

    public static float xTitle = xStartSpaces;
    public static float yTitle = yAfterSpaces + hText*(float)1.2;

    public static float hScoreSpace = hText;
    public static float hLockDuration = hText * (float)1.2;
    public static int wActiveLight = 22;
    public static int hActiveLight = 22;

    public static float selectionFrame = 4f;
    public static float backgroundFrame = 1f;

    public static int resourceSide = 64;

    public static float thicknessOuterBeam = 12f;
    public static float thicknessMidBeam = 8f;
    public static float thicknessInnerBeam = 4f;

    // Draw pix
    public static int pixXLeftMainSpace(float x) {
        return Pix.xStartSpaces + (int)(x * Pix.wSpace); // Note : must return an int ?
    }
    public static int pixYUpMainSpace(float y) {
        return Pix.yStartSpaces + (int)(y * Pix.hSpace);
    }
    public static int pixXRightMainSpace(float x) {
        return pixXLeftMainSpace(x) + Pix.wMainSpace;
    }
    public static int pixYDownMainSpace(float y) {
        return pixYUpMainSpace(y) + Pix.hMainSpace;
    }
    public static int pixXLeftMainSpace(int x) {
        return Pix.xStartSpaces + (x * Pix.wSpace);
    }
    public static int pixYUpMainSpace(int y) {
        return Pix.yStartSpaces + (y * Pix.hSpace);
    }
    public static int pixXRightMainSpace(int x) {
        return pixXLeftMainSpace(x) + Pix.wMainSpace;
    }
    public static int pixYDownMainSpace(int y) {
        return pixYUpMainSpace(y) + Pix.hMainSpace;
    }
    public static int xCenter(int x) {
        return pixXLeftMainSpace(x) + Pix.hMainSpace/2;
    }
    public static int yCenter(int y) {
        return pixYUpMainSpace(y) + Pix.hMainSpace/2;
    } // TODO retirer "pix" des noms des méthodes
}
