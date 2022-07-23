package com.example.fruitfall;

public class Pix {
    public static int xStartSpaces = 22;
    public static int yStartSpaces = 84;
    public static int wMainSpace = 60;
    public static int hMainSpace = 60;
    public static int wGap = 4;
    public static int hGap = 4;
    public static int wSpace = wMainSpace + wGap;
    public static int hSpace = hMainSpace + hGap;
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

    public static int resourceSide = 64;
}
