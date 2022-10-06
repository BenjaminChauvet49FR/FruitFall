package com.example.fruitfall;

import com.example.fruitfall.level.LevelData;
import com.example.fruitfall.level.LevelManager;

public class Pix {
    public static int xBaseStartSpaces = 22;
    public static int yBaseStartSpaces = 84;
    public static int wGap = 4;
    public static int hGap = 4;
    public static int wMainSpace = 60;
    public static int hMainSpace = 60;

    public static int wSpace = wMainSpace + wGap;
    public static int hSpace = hMainSpace + hGap;
    public static int ghostSquareMargin = wSpace/2;
    public static int yAfterSpaces = yBaseStartSpaces + Constants.FIELD_YLENGTH * hSpace - hGap;

    public static int xStartActiveLight = xBaseStartSpaces;
    public static int yStartActiveLight = 22;
    public static float xScore = xBaseStartSpaces + 50;
    public static float hText = yBaseStartSpaces * (float)0.3;

    public static float yScore = 10+hText;
    public static float xTime = xScore + 300;
    public static float yTime = yScore;

    public static float xTitle = xBaseStartSpaces;
    public static float yTitle = yAfterSpaces + hText*(float)1.2;

    public static float hScoreSpace = hText;
    public static float hTextTeleporters = hText;
    public static int pauseFieldInfoSide = (int)hTextTeleporters*3/2;

    public static float hLockDuration = hText * (float)1.2;
    public static int wActiveLight = 22;
    public static int hActiveLight = 22;

    public static float basketSpaceMargin = wMainSpace * (float)0.5;
    public static int basketSpaceBGMargin = 0;// wMainSpace * 2 / 5;
    public static int basketSpaceSide = wMainSpace;//wMainSpace / 2;
    public static float pipSide = 10;
    public static float pipPadding = 5;

    public static float selectionFrame = 4f;
    public static float helpFrame = 4f;
    public static float backgroundFrame = 1f;

    public static int resourceSide = 128;
    public static int resourceLittleSide = Pix.resourceSide/3;

    public static float thicknessOuterBeam = 12f;
    public static float thicknessMidBeam = 8f;
    public static float thicknessInnerBeam = 4f;

    public static int squareSide = 128;
    public static int xGapBetweenGoals = 64;
    public static float yCommandsText = yScore + hText*(float)1.2;
    public static float xCommands = xScore;
    public static float yCommandsKind = yScore + hText*(float)0.2;
    public static float xCommandsKind(int i) {return xCommands + i*(resourceLittleSide + xGapBetweenGoals);}
    public static float xCommandsAmount(int i) {return xCommands + i*(resourceLittleSide + xGapBetweenGoals) + resourceLittleSide + 2;}
    public static float yNutText = yCommandsText;
    public static float xNuts = xScore;
    public static float yNutWaitingPicture = yCommandsKind;
    public static float xNutWaitingTextStart = xNuts + 100;
    public static float xNutWaitingPicture(int i) {return xCommands + i*(resourceLittleSide + 2) + xNutWaitingTextStart;}
    private static final float xNutWaitingTextIn = resourceLittleSide/2;
    public static float xNutWaitingText(int i) {return xNutWaitingPicture(i) + xNutWaitingTextIn;}

    public static int getXLeftFirstSpace() {
        return Pix.xBaseStartSpaces + (Constants.FIELD_XLENGTH - LevelManager.currentLevelWidth())*Pix.wSpace/2;
    }

    public static int getXLeftStartField() {
        return getXLeftFirstSpace()-wGap/2;
    }

    public static int getYUpFirstSpace() {
        return Pix.yBaseStartSpaces + (Constants.FIELD_YLENGTH - LevelManager.currentLevelHeight())*Pix.hSpace/2;
    }

    public static int getYUpStartField() {
        return getYUpFirstSpace()-hGap/2;
    }

    // Draw pix
    public static int xLeftMainSpace(float x) {
        return getXLeftFirstSpace() + (int)(x * Pix.wSpace); // Note : must return an int ?
    }
    public static int yUpMainSpace(float y) {
        return getYUpFirstSpace() + (int)(y * Pix.hSpace);
    }
    public static int xRightMainSpace(float x) {
        return xLeftMainSpace(x) + Pix.wMainSpace;
    }
    public static int yDownMainSpace(float y) {
        return yUpMainSpace(y) + Pix.hMainSpace;
    }
    public static int xLeftMainSpace(int x) {
        return getXLeftFirstSpace() + (x * Pix.wSpace);
    }
    public static int yUpMainSpace(int y) {
        return getYUpFirstSpace() + (y * Pix.hSpace);
    }
    public static int xRightMainSpace(int x) {
        return xLeftMainSpace(x) + Pix.wMainSpace;
    }
    public static int yDownMainSpace(int y) {
        return yUpMainSpace(y) + Pix.hMainSpace;
    }
    public static int xCenter(int x) {
        return xLeftMainSpace(x) + Pix.wMainSpace/2;
    }
    public static int yCenter(int y) { return yUpMainSpace(y) + Pix.hMainSpace/2; }
}
