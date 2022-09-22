package com.example.fruitfall.level;

import android.widget.Space;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.SpaceCoors;
import com.example.fruitfall.exceptions.IncorrectStringException;
import com.example.fruitfall.introductions.Transition;
import com.example.fruitfall.introductions.TransitionManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;
import com.example.fruitfall.spatialTransformation.SpatialTransformationHorizMirror;
import com.example.fruitfall.spatialTransformation.SpatialTransformationNone;
import com.example.fruitfall.spatialTransformation.SpatialTransformationUTurn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelData {
    private int numberOfFruitKinds;
    private GameEnums.SPACE_DATA[][] spaceData;
    private int[][] additionalSpaceData;
    private List<Integer> forcedIndexes;
    private List<Integer> locksDuration;
    // IMPORTANT : coordinate in position (i) in inFallTeleporters must be at a base, otherwise it can be very confusing. And coordinate in position (i) in outFallTeleporters must be at a summit.
    private List<SpaceCoors> inFallTeleporters;
    private List<SpaceCoors> outFallTeleporters;
    private GameEnums.SPACE_DATA[] topRowSpawn;
    private String charsForTransition;

    private String name;
    private int category;
    private String infos;
    private String fieldContents;

    private int[] amountsMission;
    private int numberOfMissions;
    private GameEnums.ORDER_KIND[] kindsOfMissions;
    private int[][] basketsSpaceData;
    private GameEnums.GOAL_KIND goalKind;

    private static int widthClipBoard = 0;
    private static int heightClipBoard = 0;
    private static GameEnums.SPACE_DATA[][] clipBoard = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private static int[][] additionalClipBoard = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private static int[][] basketsClipBoard = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];


    public int getFruitColours() {
        return this.numberOfFruitKinds;
    }
    public List<Integer> getForcedIndexes() {
        return this.forcedIndexes;
    }

    public GameEnums.SPACE_DATA getData(int x, int y) {
        return this.spaceData[y][x];
    }

    public List<SpaceCoors> getTeleportersSource() {
        return this.inFallTeleporters;
    }

    public List<SpaceCoors> getTeleportersDestination() {
        return this.outFallTeleporters;
    }

    public String getTitle() { return this.name; }
    public int getCategory() { return this.category; }
    public int getMissionsNumber() { return this.numberOfMissions; }
    public GameEnums.ORDER_KIND getKind(int i) { return this.kindsOfMissions[i]; }
    public int getAmount(int i) { return this.amountsMission[i]; }


    public GameEnums.SPACE_DATA getTopRowSpawn(int x) {
        return topRowSpawn[x];
    }

    // Note : the string is parsed into tokens ('split(" ")'). Read below to understand how each token is then interpreted.
    public LevelData(int category, String fieldContents, String infos, String name) {
        this.name = name;
        this.fieldContents = fieldContents;
        this.infos = infos;
        this.category = category;
    }

    public void deploy() {

        // Setup
        this.numberOfMissions = 0;
        this.kindsOfMissions = new GameEnums.ORDER_KIND[Constants.MAX_MISSIONS];
        this.amountsMission = new int[Constants.MAX_MISSIONS];
        for (int i = 0 ; i < Constants.MAX_MISSIONS ; i++) {
            this.kindsOfMissions[i] = GameEnums.ORDER_KIND.NONE;
        }

        int x, y;
        this.forcedIndexes = new ArrayList<>();
        this.topRowSpawn = new GameEnums.SPACE_DATA[Constants.FIELD_XLENGTH];
        for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
            this.topRowSpawn[x] = GameEnums.SPACE_DATA.VOID_SPAWN;
        }

        this.spaceData = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        this.additionalSpaceData = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        this.basketsSpaceData = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        this.goalKind = GameEnums.GOAL_KIND.ORDERS;
        for (y = 0; y < Constants.FIELD_YLENGTH; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                this.spaceData[y][x] = GameEnums.SPACE_DATA.FRUIT;
                this.basketsSpaceData[y][x] = 0;
            }
        }

        this.inFallTeleporters = new ArrayList<>();
        this.outFallTeleporters = new ArrayList<>();
        this.locksDuration = new ArrayList<>();

        // Field part
        String[] obtainedStrings = this.fieldContents.split(" ");
        for (String token : obtainedStrings) {
            if (token.length() == 0) {
                continue;
            }
            switch(token.charAt(0)) {

                // Field data
                case 'b' : interpretStringBlockSpawn1stRow(token.substring(1));
                    break;
                case 't' : interpretStringTeleporters(token.substring(1));
                    break;

                // Tool data
                case 'c' : interpretStringCopy(token.substring(1));
                    break;
                case 'v' :
                    interpretStringPaste(token.substring(1));
                break;

                // Space data
                case 'B' : interpretStringBaskets(token.substring(1));
                    break;

                default : interpretStringClassic(token);
                    break;
            } 
        }

        // Infos part
        obtainedStrings = this.infos.split(" ");
        for (String token : obtainedStrings) {
            if (token.length() == 0) {
                continue;
            }
            switch(token.charAt(0)) {
                // Level data
                case 'f' : interpretStringFruits(token.substring(1));
                    break;
                case 'l' : interpretStringLockLength(token.substring(1));
                    break;
                case 'i' : this.charsForTransition = (token.substring(1));
                    break;
                case 'm' : interpretStringMissions(token.substring(1));
                    break;
            }
        }
        // Default infos part
        if (this.numberOfMissions == 0) {
            this.numberOfMissions = 1;
            this.kindsOfMissions[0] = GameEnums.ORDER_KIND.LIGHTNING_LIGHTNING;
            this.amountsMission[1] = 5;
        }
    }

    // Offensive programming ! Don't put something out of bounds !
    private void interpretStringPaste(String tokenBody) {
        int xS, yS;
        SpatialTransformation transformation;
        switch (tokenBody.charAt(0)) {
            case 'h' :
                xS = charToInt(tokenBody, 1);
                yS = charToInt(tokenBody, 2);
                transformation = new SpatialTransformationHorizMirror(xS, yS, xS + widthClipBoard - 1, yS + heightClipBoard - 1);
            break;
            case 'x' :
                xS = charToInt(tokenBody, 1);
                yS = charToInt(tokenBody, 2);
                transformation = new SpatialTransformationUTurn(xS, yS, xS + widthClipBoard - 1, yS + heightClipBoard - 1);
            break;
            default :
                xS = charToInt(tokenBody, 0);
                yS = charToInt(tokenBody, 1);
                transformation = new SpatialTransformationNone(xS, yS, xS + widthClipBoard - 1, yS + heightClipBoard - 1);
            break; // TODO : not all possible transformations have been added, notably, vertical one is missing
        }
        SpaceCoors transCoors;
        int x, y;
        for (y = yS; y < yS + heightClipBoard; y++) {
            for (x = xS; x < xS + widthClipBoard; x++) {
                transCoors = transformation.transform(x, y);
                this.spaceData[transCoors.y][transCoors.x] = clipBoard[y-yS][x-xS];
                this.additionalSpaceData[transCoors.y][transCoors.x] = additionalClipBoard[y-yS][x-xS];
                this.basketsSpaceData[transCoors.y][transCoors.x] = basketsClipBoard[y-yS][x-xS];
            }
        }
    }

    private void interpretStringCopy(String tokenBody) {
        int x1 = charToInt(tokenBody, 0);
        int y1 = charToInt(tokenBody, 1);
        int x2 = charToInt(tokenBody, 2);
        int y2 = charToInt(tokenBody, 3);
        int x, y;
        for (y = y1; y <= y2; y++) {
            for (x = x1; x <= x2; x++) {
                clipBoard[y-y1][x-x1] = this.spaceData[y][x];
                additionalClipBoard[y-y1][x-x1] = this.additionalSpaceData[y][x];
                basketsClipBoard[y-y1][x-x1] = this.basketsSpaceData[y][x];
            }
        }
        heightClipBoard = y2-y1+1;
        widthClipBoard = x2-x1+1;
    }

    private void interpretStringMissions(String tokenBody) {
        GameEnums.ORDER_KIND kind = GameEnums.ORDER_KIND.NONE;
        int amount = 5;

        switch(tokenBody.charAt(0)) {
            case 'a' : kind = GameEnums.ORDER_KIND.FRUITS_ANY; break;
            case '0' : kind = GameEnums.ORDER_KIND.FRUIT_0; break;
            case '1' : kind = GameEnums.ORDER_KIND.FRUIT_1; break;
            case '2' : kind = GameEnums.ORDER_KIND.FRUIT_2; break;
            case '3' : kind = GameEnums.ORDER_KIND.FRUIT_3; break;
            case '4' : kind = GameEnums.ORDER_KIND.FRUIT_4; break;
            case '5' : kind = GameEnums.ORDER_KIND.FRUIT_5; break;
            case '6' : kind = GameEnums.ORDER_KIND.FRUIT_6; break;
            case '7' : kind = GameEnums.ORDER_KIND.FRUIT_7; break;
            case 'l' : kind = GameEnums.ORDER_KIND.LIGHTNING; break;
            case 'f' : kind = GameEnums.ORDER_KIND.FIRE; break;
            case 'o' : kind = GameEnums.ORDER_KIND.OMEGA; break;
            case 'L' : kind = GameEnums.ORDER_KIND.LIGHTNING_WILD; break;
            case 'F' : kind = GameEnums.ORDER_KIND.FIRE_WILD; break;
            case 'O' : kind = GameEnums.ORDER_KIND.OMEGA_WILD; break;
            case 'A' : kind = GameEnums.ORDER_KIND.LIGHTNING_LIGHTNING; break;
            case 'B' : kind = GameEnums.ORDER_KIND.FIRE_FIRE; break;
            case 'C' : kind = GameEnums.ORDER_KIND.OMEGA_OMEGA; break;
            case 'X' : kind = GameEnums.ORDER_KIND.LIGHTNING_FIRE; break;
            case 'Y' : kind = GameEnums.ORDER_KIND.LIGHTNING_OMEGA; break;
            case 'Z' : kind = GameEnums.ORDER_KIND.OMEGA_FIRE; break;
        }
        if (tokenBody.length() > 1) {
            amount = (Integer.parseInt(tokenBody.substring(1)));
        }
        if (kind != GameEnums.ORDER_KIND.NONE) {
            this.kindsOfMissions[numberOfMissions] = kind;
            this.amountsMission[numberOfMissions] = amount;
            this.numberOfMissions++;
        }
    }

    private void interpretStringFruits(String tokenBody) {
        this.numberOfFruitKinds = charToInt(tokenBody, 0);
        for ( int i = 1 ; i < tokenBody.length() ; i++) {
            this.forcedIndexes.add(charToInt(tokenBody, i));
        }
    }

    private void interpretStringBlockSpawn1stRow(String tokenBody) {
        for ( int i = 0 ; i < tokenBody.length() ; i++) {
            this.topRowSpawn[charToInt(tokenBody, i)] = GameEnums.SPACE_DATA.VOID;
        }
    }


    private void interpretStringBaskets(String tokenBody) {
        this.goalKind = GameEnums.GOAL_KIND.BASKETS;
        this.fillRectangleAreaInArray(this.basketsSpaceData, tokenBody);
    }

    private void fillRectangleAreaInArray(int[][] array, String tokenBody) {
        int value = charToInt(tokenBody, 0);
        int position1stSizeChar = 1;
        int x, y;
        int x1 = charToInt(tokenBody, position1stSizeChar);
        int y1 = charToInt(tokenBody, position1stSizeChar+1);
        int x2 = charToInt(tokenBody, position1stSizeChar+2);
        int y2 = charToInt(tokenBody, position1stSizeChar+3);
        for (y = y1 ; y <= y2 ; y++) {
            for (x = x1; x <= x2; x++) {
                array[y][x] = value;
            }
        }
    }

    private void interpretStringLockLength(String tokenBody) {
        this.locksDuration.add(Integer.parseInt(tokenBody));
    }

    private void interpretStringTeleporters(String tokenBody) {
        int xIn = charToInt(tokenBody, 0);
        int yIn = charToInt(tokenBody, 1);
        int xOut = charToInt(tokenBody, 2);
        int yOut = charToInt(tokenBody, 3);
        int length = 1;
        if (tokenBody.length() > 4) {
            length = charToInt(tokenBody, 4);
        }
        for (int i = 0 ; i < length ; i++) {
            this.inFallTeleporters.add(new SpaceCoors(xIn + i, yIn));
            this.outFallTeleporters.add(new SpaceCoors(xOut + i, yOut));
        }
    }

    private void interpretStringClassic(String tokenBody) {
        char cType = tokenBody.charAt(0);
        int additionalData = 0;
        int position1stSizeChar = 1;
        GameEnums.SPACE_DATA data = null;
        if (cType == 'F') {
            data = GameEnums.SPACE_DATA.FRUIT;
        }
        if (cType == 'E') {
            data = GameEnums.SPACE_DATA.EMPTY;
        }
        if (cType == 'X') {
            char cParam = tokenBody.charAt(1);
            if (cParam == '!') {
                position1stSizeChar = 2;
                data = GameEnums.SPACE_DATA.VOID;
            } else {
                data = GameEnums.SPACE_DATA.VOID_SPAWN;
            }
        }
        if (cType == 'L') {
            position1stSizeChar = 2;
            data = GameEnums.SPACE_DATA.DELAYED_LOCK;
            additionalData = charToInt(tokenBody, 1)-1;
            if (additionalData < 0 || additionalData > 3) {
                throw new IncorrectStringException(tokenBody);
            }
        }
        if (cType == 'S') {
            position1stSizeChar = 2;
            data = GameEnums.SPACE_DATA.BREAKABLE_BLOCK;
            additionalData = charToInt(tokenBody, 1);
            if (additionalData <= 0) {
                throw new IncorrectStringException(tokenBody);
            }
        }
        if (data == null) {
            throw new IncorrectStringException(tokenBody);
        } else {
            int x, y;
            int x1 = charToInt(tokenBody, position1stSizeChar);
            int y1 = charToInt(tokenBody, position1stSizeChar+1);
            int x2 = charToInt(tokenBody, position1stSizeChar+2);
            int y2 = charToInt(tokenBody, position1stSizeChar+3);
            for (y = y1 ; y <= y2 ; y++) {
                for (x = x1; x <= x2; x++) {
                    this.spaceData[y][x] = data;
                    this.additionalSpaceData[y][x] = additionalData;
                }
            }
        }
    }

    // Note : this is the correct place for this somewhat utilitary function
    private static int charToInt(String s, int pos) {
        char c = s.charAt(pos);
        if (c == 'A') {
            return 10;
        }
        int value = c-'0';
        if (value > 9 || value < 0) {
            throw new IncorrectStringException(s);
        }
        return value;
    }

    public int getLockDuration(int x, int y) {
        return (this.locksDuration.get(this.additionalSpaceData[y][x]));
    }

    public int getBreakableBlockLevel(int x, int y) {
        return this.additionalSpaceData[y][x];
    }

    public int getBaskets(int x, int y) {
        return this.basketsSpaceData[y][x];
    }

    public GameEnums.GOAL_KIND getGoalKind() {
        return this.goalKind;
    }

    public Transition getTransition() {
        if (this.charsForTransition == null || this.charsForTransition.isEmpty()) {
            return TransitionManager.getTransitionFromChar('X');
        }
        int i = new Random().nextInt(charsForTransition.length());
        return TransitionManager.getTransitionFromChar(charsForTransition.charAt(i));
    }

}
