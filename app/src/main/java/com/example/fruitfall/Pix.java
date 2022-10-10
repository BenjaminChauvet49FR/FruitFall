package com.example.fruitfall;

import android.graphics.Rect;

import com.example.fruitfall.level.LevelManager;

public class Pix {

    // Screen size
    // Credits : https://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions-as-pixels-in-android

    public static int screenWidth = 2000; // 720 and 1406 for my smartphone
    public static int screenHeight = 2000;
    public static int canvasWidth = 2000;
    public static int canvasHeight = 2000;

    public static float selectionFrame = 4f;
    public static float helpFrame = 4f;
    public static float backgroundFrame = 1f;
    public static int resourceSide = 128;
    public static int squareSide = 128;
    public static int resourceLittleSide = Pix.resourceSide/3;
    public static int iconLittleSide = resourceLittleSide; // Side behind the name = defined here. Otherwise, defined below !
    // I need to put the data here but they are only available (or easy to get) in some kotlin file.
    // Tool to sort lines in alphabetical order : https://www.textfixerfr.com/outils/ordre-alphabetique.php
    public static int sideIconBasket = 18;
    public static int paddingBasket = 2;

    public static void updateScreenDimensions(int pixScreenWidth, int pixScreenHeight, int pixCanvasWidth, int pixCanvasHeight) {
        // Options
        screenWidth = pixScreenWidth;
        screenHeight = pixScreenHeight;
        canvasWidth = pixCanvasWidth;
        canvasHeight = pixCanvasHeight;

        wOptionalFruit = screenWidth/15;
        hOptionalFruit = wOptionalFruit;
        gapOptionalFruit = wOptionalFruit/3;
        int paddingOptionalFruits;
        paddingOptionalFruits = wOptionalFruit/2;
        xOptionalFruitFrameStart = screenWidth/20;
        xOptionalFruitFrameEnd = screenWidth*19/20;
        xOptionalFruitStart = paddingOptionalFruits + xOptionalFruitFrameStart;
        yOptionalFruitFrameStart = 20;
        yOptionalFruitStart = paddingOptionalFruits + yOptionalFruitFrameStart;
        yOptionalFruitFrameEnd = yOptionalFruitFrameStart + hOptionalFruit + paddingOptionalFruits*2;
        yOptionalCursor = Pix.yOptionalFruitStart + Pix.hOptionalFruit;
        hOptionalFruitCursor = paddingOptionalFruits*5/2;
        wOptionalFruitCursor = 8;
        hTextOptionalFloat = hOptionalFruit;
        gapBetweenOptionsFrames = hOptionalFruit*3/4;

        paddingOptionalText = (int)hTextOptionalFloat/4;
        yWriteLineFirst = yOptionalFruitFrameEnd + hOptionalFruitCursor + gapBetweenOptionsFrames;
        yWriteLineOptionalMode = yWriteLineFirst;
        xWriteLineOptionalMode = xOptionalFruitFrameStart + paddingOptionalText;

        // Game
        wInnerSpace = screenWidth/12; // 60/720 ...
        hInnerSpace = wInnerSpace;
        strokeWidthOptionalFrame = (float) (Math.ceil(screenWidth/90.0));
        xBaseStartSpaces = screenWidth/32;
        yBaseStartSpaces = 3*screenHeight/50; // 84 (/1406)
        wActiveLight = xBaseStartSpaces;
        hActiveLight = wActiveLight;
        horizontalPaddingSpace = wInnerSpace/30;
        verticalPaddingSpace = horizontalPaddingSpace;
        wSpace = wInnerSpace + horizontalPaddingSpace*2;
        hSpace = hInnerSpace + verticalPaddingSpace*2;
        borderThickness = 4;
        roundBorderSpace = 4;
        marginGhostSpace = wSpace/2;
        yAfterSpaces = yBaseStartSpaces + Constants.FIELD_YLENGTH * hSpace - verticalPaddingSpace*2;
        xStartActiveLight = xBaseStartSpaces;
        yStartActiveLight = xBaseStartSpaces; // Like X
        xTextScore = xBaseStartSpaces + screenWidth/(float)16;
        hTextFloat = yBaseStartSpaces * (float)0.3;
        hTextInt = yBaseStartSpaces * 3/10; //(25.2/1406)
        paddingMainText = hTextInt/4;
        float yWritingLine1 = hTextInt * (float)1.4;
        yTextScore = yWritingLine1;
        int gapTimeMoves = hTextInt*4;
        xTimePicture = (int)xTextScore + screenWidth/2-iconLittleSide-2;
        xMovesPicture = xTimePicture + gapTimeMoves;
        yTimeMovesLine = (int) (yWritingLine1);

        hTextScoreSpace = hTextFloat;
        hTextTeleporters = hTextFloat;
        sidePauseFieldInfo = (int)hTextTeleporters*3/2;

        hTextLockDuration = hTextFloat * (float)1.2;
        basketSpaceMargin = wInnerSpace * (float)0.5;

        thicknessOuterBeam = wInnerSpace/(float)5;
        thicknessMidBeam = wInnerSpace*(float)2/15;
        thicknessInnerBeam = wInnerSpace/(float)15;

        xGapBetweenGoals = screenWidth/12;
        yTextCommands = yTextScore + hTextFloat*(float)1.4;
        xTextCommands = xTextScore;
        yPictureCommandsKind = (int) (yTextCommands - hTextFloat);
        yTextNut = yTextCommands;
        xTextNuts = xTextScore;
        yPictureNutWaiting = yPictureCommandsKind;
        xNutWaitingPicturesStart = (int)xTextNuts + screenWidth*7/50;
        xTextInNutWaiting = resourceLittleSide/(float)2;

        xTextTitle = xBaseStartSpaces;
        yTextTitle = yAfterSpaces + hTextFloat*(float)1.4;

        basketFrameWidth = (float)4.0;
        sidePip = sideIconBasket*2/3;
        paddingPip = paddingBasket;
    }

    private static float xTextInNutWaiting;
    private static int xBaseStartSpaces;
    private static int xNutWaitingPicturesStart;
    public static float basketFrameWidth;
    public static float basketSpaceMargin;
    public static float borderThickness;
    public static float hTextFloat;
    public static float hTextLockDuration;
    public static float hTextOptionalFloat;
    public static float hTextScoreSpace;
    public static float hTextTeleporters;
    public static float strokeWidthOptionalFrame;
    public static float thicknessInnerBeam;
    public static float thicknessMidBeam;
    public static float thicknessOuterBeam;
    public static float xTextCommands;
    public static float xTextNuts;
    public static float xTextScore;
    public static float xTextTitle;
    public static float yTextCommands;
    public static float yTextNut;
    public static float yTextScore;
    public static float yTextTitle;
    public static int gapBetweenOptionsFrames;
    public static int gapOptionalFruit;
    public static int hActiveLight;
    public static int hInnerSpace;
    public static int hOptionalFruit;
    public static int hOptionalFruitCursor;
    public static int horizontalPaddingSpace;
    public static int hSpace;
    public static int hTextInt;
    public static int marginGhostSpace;
    public static int paddingMainText;
    public static int paddingOptionalText;
    public static int paddingPip;
    public static int roundBorderSpace;
    public static int sidePauseFieldInfo;
    public static int sidePip;
    public static int verticalPaddingSpace;
    public static int wActiveLight;
    public static int wInnerSpace;
    public static int wOptionalFruit;
    public static int wOptionalFruitCursor;
    public static int wSpace;
    public static int xGapBetweenGoals;
    public static int xMovesPicture;
    public static int xOptionalFruitFrameEnd;
    public static int xOptionalFruitFrameStart;
    public static int xOptionalFruitStart;
    public static int xStartActiveLight;
    public static int xTimePicture;
    public static int xWriteLineOptionalMode;
    public static int yAfterSpaces;
    public static int yBaseStartSpaces;
    public static int yOptionalCursor;
    public static int yOptionalFruitFrameEnd;
    public static int yOptionalFruitFrameStart;
    public static int yOptionalFruitStart;
    public static int yPictureCommandsKind;
    public static int yPictureNutWaiting;
    public static int yStartActiveLight;
    public static int yTimeMovesLine;
    public static int yWriteLineFirst;
    public static int yWriteLineOptionalMode;
    public static Rect rectMode = new Rect(0,0,0,0);

    public static int xPictureCommandsKind(int i) {return (int) (xTextCommands + i*(resourceLittleSide + xGapBetweenGoals));}
    public static float xTextCommandsAmount(int i) {return xTextCommands + i*(resourceLittleSide + xGapBetweenGoals) + resourceLittleSide + 2;}


    public static int xNutWaitingPicture(int i) {return (int)xTextCommands + i*resourceLittleSide + 2 + xNutWaitingPicturesStart;}
    public static float xTextNutWaiting(int i) {return xNutWaitingPicture(i) + xTextInNutWaiting;}

    public static int getXLeftFirstSpace() {
        return Pix.xBaseStartSpaces + (Constants.FIELD_XLENGTH - LevelManager.currentLevelWidth())*Pix.wSpace/2;
    }
    public static int getXLeftFirstInnerSpace() {
        return getXLeftFirstSpace()+horizontalPaddingSpace;
    }

    public static int getXLeftStartField() {
        return getXLeftFirstSpace()-horizontalPaddingSpace;
    }

    public static int getYUpFirstSpace() {
        return Pix.yBaseStartSpaces + (Constants.FIELD_YLENGTH - LevelManager.currentLevelHeight())*Pix.hSpace/2;
    }
    public static int getYUpFirstInnerSpace() {
        return getYUpFirstSpace()+verticalPaddingSpace;
    }

    public static int getYUpStartField() {
        return getYUpFirstSpace()-verticalPaddingSpace;
    }

    // Draw pix
    // Must return ints
    public static int xLeftSpace(float x) { return getXLeftFirstSpace() + (int)(x * Pix.wSpace);}
    public static int yUpSpace(float y) {
        return getYUpFirstSpace() + (int)(y * Pix.hSpace);
    }
    public static int xRightSpace(float x) { return xLeftSpace(x) + Pix.wSpace; } // -1 is NOT necessary *
    public static int yDownSpace(float y) {
        return yUpSpace(y) + Pix.hSpace;
    }
    public static int xLeftSpace(int x) {return getXLeftFirstSpace() + (x * Pix.wSpace);}
    public static int yUpSpace(int y) {
        return getYUpFirstSpace() + (y * Pix.hSpace);
    }
    public static int xRightSpace(int x) {return xLeftSpace(x) + Pix.wSpace;}
    public static int yDownSpace(int y) {
        return yUpSpace(y) + Pix.hSpace;
    }
    public static int xLeftInnerSpace(float x) { return xLeftSpace(x) + horizontalPaddingSpace;}
    public static int yUpInnerSpace(float y) {
        return yUpSpace(y) + verticalPaddingSpace;
    }
    public static int xLeftInnerSpace(int x) { return xLeftSpace(x) + horizontalPaddingSpace;}
    public static int yUpInnerSpace(int y) {
        return yUpSpace(y) + verticalPaddingSpace;
    }
    public static int xCenter(int x) {
        return xLeftSpace(x) + Pix.wSpace/2;
    }
    public static int yCenter(int y) { return yUpSpace(y) + Pix.hSpace/2; }
    // *(-1NN) https://developer.android.com/reference/android/graphics/Rect "Note that the right and bottom coordinates are exclusive. This means a Rect being drawn untransformed onto a Canvas will draw into the column and row described by its left and top coordinates, but not those of its bottom and right."

    // Precondition : x/y source of writing in top-left
    // All tests start on same x pix ; 1st and last of the pixY's are given
    public static void adaptRectForFrame(Rect rect, int pixXTextStart, int pixYTextStart1, int maxLengthX, int pixYTextEndLast) {
        rect.set(pixXTextStart-Pix.paddingMainText, pixYTextStart1-Pix.hTextInt,
                pixXTextStart + maxLengthX*Pix.hTextInt/2+Pix.paddingMainText, pixYTextEndLast+Pix.paddingMainText);
    }

    public static void adaptRectForFrameOptional(Rect rect, int pixXTextStart, int pixYTextStart1, int maxLengthX, int pixYTextEndLast) {
        rect.set(pixXTextStart-Pix.paddingOptionalText, pixYTextStart1-(int)Pix.hTextOptionalFloat,
                pixXTextStart + (int)(maxLengthX*hTextOptionalFloat/2)+Pix.paddingOptionalText, pixYTextEndLast+Pix.paddingOptionalText);
    }

    // Methods when drawing the content of a space, supposedly inner but exceptionally full here...
    public static void adaptRectForFullSpace(Rect rect) {
        rect.left -= Pix.horizontalPaddingSpace;
        rect.top -= Pix.verticalPaddingSpace;
        rect.right += Pix.horizontalPaddingSpace;
        rect.bottom += Pix.verticalPaddingSpace;
    }

    // ... and going back to normal.
    public static void adaptRectForInnerSpace(Rect rect) {
        rect.left += Pix.horizontalPaddingSpace;
        rect.top += Pix.verticalPaddingSpace;
        rect.right -= Pix.horizontalPaddingSpace;
        rect.bottom -= Pix.verticalPaddingSpace;
    }
}
