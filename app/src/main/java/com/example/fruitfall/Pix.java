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
    public static int xStartActiveLight = xStartSpaces;
    public static int yStartActiveLight = 22;
    public static float xScore = xStartSpaces + 50;
    public static float hScore = yStartSpaces * (float)0.75;
    public static float yScore = 10+hScore;
    public static float hScoreSpace = hScore/(float)1.5;
    public static int wActiveLight = 22;
    public static int hActiveLight = 22;

    public static float selectionFrame = 4f;
}
