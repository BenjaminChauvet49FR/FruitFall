package com.example.fruitfall.level;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.structures.SpaceCoors;
import com.example.fruitfall.checkers.Checker;
import com.example.fruitfall.checkers.CheckerOneDimension;
import com.example.fruitfall.exceptions.IncorrectStringException;
import com.example.fruitfall.exceptions.OutOfFieldException;
import com.example.fruitfall.introductions.Transition;
import com.example.fruitfall.introductions.TransitionManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;
import com.example.fruitfall.spatialTransformation.SpatialTransformationHorizMirror;
import com.example.fruitfall.spatialTransformation.SpatialTransformationNone;
import com.example.fruitfall.spatialTransformation.SpatialTransformationUTurn;
import com.example.fruitfall.spatialTransformation.SpatialTransformationVertMirror;

import java.util.ArrayList;
import java.util.List;

public class LevelData {

    private int numberOfFruitKinds;
    private List<Integer> locksDuration;
    // IMPORTANT : coordinate in position (i) in inFallTeleporters must be at a base, otherwise it can be very confusing. And coordinate in position (i) in outFallTeleporters must be at a summit.
    private List<SpaceCoors> inFallTeleporters;
    private List<SpaceCoors> outFallTeleporters;
    private final Checker checkerPotentialFruitsInStickyBombs = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final CheckerOneDimension checkerReductibleColour = new CheckerOneDimension(Constants.RESOURCES_NUMBER_FRUITS);
    private GameEnums.SPACE_DATA[] topRowSpawn;
    private String charsForTransition;
    private int fruitForcingIndex;

    private final String name;
    private final int category;
    private final String infos;
    private final String fieldContents;

    private int[] amountsMission;
    private int numberOfMissions;
    private GameEnums.ORDER_KIND[] kindsOfMissions;
    private int numberDownFruitsMission;
    private int numberDownFruitsPresence;
    private int delayDownFruits;
    // See below for baskets
    private GameEnums.GOAL_KIND goalKind;

    // Level infos
    private int widthField = 0;
    private int heightField = 0;

    private static int widthClipBoard = 0;
    private static int heightClipBoard = 0;

    private int[][] basketsSpaceData;
    private int[][] hostagesSpaceData;
    private GameEnums.SPACE_DATA[][] spaceData;
    private final int[][] additionalSpaceData = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private final int[][] additionalSpaceData2 = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH]; // A bit too many modifications around to make a checker instead of an array worthwhile...
    private static final GameEnums.SPACE_DATA[][] clipBoard = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private static final int[][] additionalClipBoard = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private static final int[][] additionalClipBoard2 = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private static final int[][] basketsClipBoard = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private static final int[][] hostagesClipBoard = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];

    private int goalTimeActionLimit = 1000;
    private int goalMovesActionLimit = 1000;
    private int goalMovesRelaxLimit = 1000;

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
    public int getNumberDownFruitsMission() { return this.numberDownFruitsMission; }
    public int getNumberDownFruitsPresence() { return this.numberDownFruitsPresence; }
    public int getDelayDownFruits() { return this.delayDownFruits; }

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

    private String layersRequired = "1-9 value required for layer.";

    public void deploy() {

        // Setup
        this.numberOfMissions = 0;
        this.kindsOfMissions = new GameEnums.ORDER_KIND[Constants.MAX_MISSIONS];
        this.amountsMission = new int[Constants.MAX_MISSIONS];

        this.checkerPotentialFruitsInStickyBombs.clear();
        this.checkerReductibleColour.clear();

        for (int i = 0 ; i < Constants.MAX_MISSIONS ; i++) {
            this.kindsOfMissions[i] = GameEnums.ORDER_KIND.NONE;
        }

        int x, y;
        this.fruitForcingIndex = Constants.DEFAULT_VALUE_ADDITIONAL_DATA;
        this.topRowSpawn = new GameEnums.SPACE_DATA[Constants.FIELD_XLENGTH];
        for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
            this.topRowSpawn[x] = GameEnums.SPACE_DATA.VOID_SPAWN;
        }

        this.spaceData = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        this.basketsSpaceData = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        this.hostagesSpaceData = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        this.goalKind = GameEnums.GOAL_KIND.ORDERS;
        for (y = 0; y < Constants.FIELD_YLENGTH; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                this.spaceData[y][x] = GameEnums.SPACE_DATA.FRUIT;
                this.basketsSpaceData[y][x] = 0;
                this.hostagesSpaceData[y][x] = 0;
                this.additionalSpaceData[y][x] = Constants.DEFAULT_VALUE_ADDITIONAL_DATA;
                this.additionalSpaceData2[y][x] = Constants.DEFAULT_VALUE_ADDITIONAL_DATA;
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
                
                case 'f' : interpretStringFruitForcing(token.substring(1)); break;

                // Space data
                case 'B' : interpretStringBaskets(token.substring(1));
                    break;
                case 'H' : interpretStringHostage(token.substring(1));
                    break;
                case 'N' : interpretStringNutCoorsFirst(token.substring(1));
                    break;
                case 'O' : interpretStringBasketsNutsAndFruits(token.substring(1));
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
                case 'f' : interpretStringFruitsInfos(token.substring(1));
                    break;
                case 'l' : interpretStringLockLength(token.substring(1));
                    break;
                case 'i' : this.charsForTransition = (token.substring(1));
                    break;
                case 'm' : interpretStringMissions(token.substring(1));
                    break;
                case 'd' : interpretStringDownFruits(token.substring(1));
                    break;
                case 'L' : interpretStringLimits(token.substring(1));
                    break;
            }
        }
        // Default infos part
        if (this.numberOfMissions == 0) {
            this.numberOfMissions = 1;
            this.kindsOfMissions[0] = GameEnums.ORDER_KIND.LIGHTNING_LIGHTNING;
            this.amountsMission[1] = 5;
        }


        // Now, the resizing part.
        // First "active" space needs to be 0, 0

        int xMin, yMin, xMax, yMax;
        xMin = 0;
        y = 0;
        boolean empty = true;
        while (empty) {
            if (this.isNoSpace(xMin, y)) {
                y++;
                if (y == Constants.FIELD_YLENGTH) {
                    xMin++;
                    y = 0;
                }
            } else {
                empty = false;
            }
        }
        xMax = Constants.FIELD_XLENGTH-1;
        y = 0;
        empty = true;
        while (empty) {
            if (this.isNoSpace(xMax, y)) {
                y++;
                if (y == Constants.FIELD_YLENGTH) {
                    xMax--;
                    y = 0;
                }
            } else {
                empty = false;
            }
        }
        x = 0;
        yMin = 0;
        empty = true;
        while (empty) {
            if (this.isNoSpace(x, yMin)) {
                x++;
                if (x == Constants.FIELD_XLENGTH) {
                    yMin++;
                    x = 0;
                }
            } else {
                empty = false;
            }
        }
        x = 0;
        yMax = Constants.FIELD_YLENGTH-1;
        empty = true;
        while (empty) {
            if (this.isNoSpace(x, yMax)) {
                x++;
                if (x == Constants.FIELD_XLENGTH) {
                    yMax--;
                    x = 0;
                }
            } else {
                empty = false;
            }
        }
        // Then, the offset
        this.widthField = xMax - xMin+1;
        this.heightField = yMax - yMin+1;
        if ((this.widthField != Constants.FIELD_XLENGTH) || (this.heightField != Constants.FIELD_YLENGTH)) {
            for (y = 0 ; y < Constants.FIELD_YLENGTH-yMin ; y++) {
                for (x = 0; x < Constants.FIELD_XLENGTH-xMin; x++) {
                    this.spaceData[y][x] = this.spaceData[y + yMin][x + xMin];
                    this.basketsSpaceData[y][x] = this.basketsSpaceData[y + yMin][x + xMin];
                    this.additionalSpaceData[y][x] = this.additionalSpaceData[y + yMin][x + xMin];
                    this.additionalSpaceData2[y][x] = this.additionalSpaceData2[y + yMin][x + xMin];
                }
            }
        }

        // Data collections (widthField and heightField above)

        // Reductible colours;
        int colour;
        for (SpaceCoors coors : this.checkerPotentialFruitsInStickyBombs.getList()) {
            x = coors.x-xMin; // Note : the x and y have been recorded before the xMin and yMin shift.
            y = coors.y-yMin;
            if (this.spaceData[y][x] == GameEnums.SPACE_DATA.STICKY_BOMB) {
                colour = this.getStickyBombContent(x, y);
                if (colour != Constants.NOT_A_FRUIT) {
                    this.checkerReductibleColour.add(colour);
                }
            }
        }
    }

    private void interpretStringLimits(String tokenBody) {
        String[] tokens = tokenBody.split("#");
        this.goalMovesActionLimit = Integer.parseInt(tokens[0]);
        this.goalTimeActionLimit = Integer.parseInt(tokens[1]);
        this.goalMovesRelaxLimit = Integer.parseInt(tokens[2]);
    }

    private boolean isNoSpace(int x, int y) {
        return this.spaceData[y][x] == GameEnums.SPACE_DATA.VOID || this.spaceData[y][x] == GameEnums.SPACE_DATA.VOID_SPAWN;
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
            case 'v' :
                xS = charToInt(tokenBody, 1);
                yS = charToInt(tokenBody, 2);
                transformation = new SpatialTransformationVertMirror(xS, yS, xS + widthClipBoard - 1, yS + heightClipBoard - 1);
            break;
            default :
                xS = charToInt(tokenBody, 0);
                yS = charToInt(tokenBody, 1);
                transformation = new SpatialTransformationNone(xS, yS, xS + widthClipBoard - 1, yS + heightClipBoard - 1);
            break; // TODO : not all possible transformations have been added, notably, vertical one is missing
        }
        SpaceCoors transCoors;
        int x = 0, y = 0;
        try {
            for (y = yS; y < yS + heightClipBoard; y++) {
                for (x = xS; x < xS + widthClipBoard; x++) {
                    transCoors = transformation.transform(x, y);
                    this.spaceData[transCoors.y][transCoors.x] = clipBoard[y-yS][x-xS];
                    this.additionalSpaceData[transCoors.y][transCoors.x] = additionalClipBoard[y-yS][x-xS];
                    this.additionalSpaceData2[transCoors.y][transCoors.x] = additionalClipBoard2[y-yS][x-xS];
                    this.basketsSpaceData[transCoors.y][transCoors.x] = basketsClipBoard[y-yS][x-xS];
                    this.hostagesSpaceData[transCoors.y][transCoors.x] = hostagesClipBoard[y-yS][x-xS];
                }
            }
        } catch (Exception e) {
            throw new OutOfFieldException(tokenBody, x, y);
        } // TODO didn't add them everywhere !
    }

    private void interpretStringCopy(String tokenBody) {
        int x1 = charToInt(tokenBody, 0);
        int y1 = charToInt(tokenBody, 1);
        int x2 = charToInt(tokenBody, tokenBody.length() == 4 ? 2 : 0);
        int y2 = charToInt(tokenBody, tokenBody.length() == 4 ? 3 : 1);
        int x = 0, y = 0;
        try {
            for (y = y1; y <= y2; y++) {
                for (x = x1; x <= x2; x++) {
                    clipBoard[y-y1][x-x1] = this.spaceData[y][x];
                    additionalClipBoard[y-y1][x-x1] = this.additionalSpaceData[y][x];
                    additionalClipBoard2[y-y1][x-x1] = this.additionalSpaceData2[y][x];
                    basketsClipBoard[y-y1][x-x1] = this.basketsSpaceData[y][x];
                    hostagesClipBoard[y-y1][x-x1] = this.hostagesSpaceData[y][x];
                }
            }
            heightClipBoard = y2-y1+1;
            widthClipBoard = x2-x1+1;
        } catch (Exception e) {
            throw new OutOfFieldException(tokenBody, x, y);
        }
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

    private void interpretStringFruitForcing(String tokenBody) {
        char c = tokenBody.charAt(0);
        if (c == 'a') {
            this.fruitForcingIndex = Constants.DEFAULT_VALUE_ADDITIONAL_DATA;
        } else {
            this.fruitForcingIndex = Integer.parseInt(""+c);
        }
    }

    private void interpretStringDownFruits(String tokenBody) {
        this.goalKind = GameEnums.GOAL_KIND.DOWNFRUITS;
        String[] tokens = tokenBody.split("#");
        this.numberDownFruitsMission = Integer.parseInt(tokens[0]);
        this.numberDownFruitsPresence = Integer.parseInt(tokens[1]);
        this.delayDownFruits = Integer.parseInt(tokens[2]);
    }

    private void interpretStringFruitsInfos(String tokenBody) {
        this.numberOfFruitKinds = charToInt(tokenBody, 0);
    }

    // Warning : this function totally exploits (but it isn't the only one) the fact that coordinates are labelled on one space !
    private void interpretStringNutCoorsFirst(String tokenBody) {
        this.goalKind = GameEnums.GOAL_KIND.NUTS;
        this.putData(charToInt(tokenBody, 0), charToInt(tokenBody, 1), GameEnums.SPACE_DATA.NUT, Integer.parseInt(tokenBody.substring(2)), 0);
    }

    private void interpretStringBlockSpawn1stRow(String tokenBody) {
        for ( int i = 0 ; i < tokenBody.length() ; i++) {
            this.topRowSpawn[charToInt(tokenBody, i)] = GameEnums.SPACE_DATA.VOID;
        }
    }


    private void interpretStringBaskets(String tokenBody) {
        this.goalKind = GameEnums.GOAL_KIND.BASKETS;
        this.fillRectangleAreaOneDigitArray(this.basketsSpaceData, tokenBody);
    }

    private void interpretStringBasketsNutsAndFruits(String tokenBody) {
        this.fillRectangleAreaOneDigitArray(this.basketsSpaceData, tokenBody); // Note : number of turns the fruit will be suspended (TODO : now, baskets officially has a multiple meaning: for baskets, but also nuts and downFruits)
    }


    private void interpretStringHostage(String tokenBody) {
        this.fillRectangleAreaOneDigitArray(this.hostagesSpaceData, tokenBody);
    }


    private void fillRectangleAreaOneDigitArray(int[][] array, String tokenBody) {
        int value, position1stSizeChar;
        if (tokenBody.length() % 2 == 1) {
            value = charToInt(tokenBody, 0);
            position1stSizeChar = 1;
        } else {
            value = 1;
            position1stSizeChar = 0;
        }
        if (charactersWithAndAfterIndex(tokenBody, position1stSizeChar) == 4) {
            int x, y;
            int x1 = charToInt(tokenBody, position1stSizeChar);
            int y1 = charToInt(tokenBody, position1stSizeChar + 1);
            int x2 = charToInt(tokenBody, position1stSizeChar + 2);
            int y2 = charToInt(tokenBody, position1stSizeChar + 3);
            for (y = y1; y <= y2; y++) {
                for (x = x1; x <= x2; x++) {
                    array[y][x] = value;
                }
            }
        } else if (charactersWithAndAfterIndex(tokenBody, position1stSizeChar) == 2)  {
            array[charToInt(tokenBody, position1stSizeChar + 1)]
                    [charToInt(tokenBody, position1stSizeChar)] = value;
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
        int additionalData2 = 0;
        int position1stSizeChar = 1;
        GameEnums.SPACE_DATA data = null;
        boolean alternateMode = false;
        if (cType == 'F') {
            data = GameEnums.SPACE_DATA.FRUIT;
            if (tokenBody.charAt(1) == '!') {
                position1stSizeChar++;
                alternateMode = true;
            }
            additionalData2 = this.fruitForcingIndex;
        }
        if (cType == 'E') {
            data = GameEnums.SPACE_DATA.EMPTY;
        }
        if (cType == 'D') {
            data = GameEnums.SPACE_DATA.DOWN_FRUIT;
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
                throw new IncorrectStringException(tokenBody, "lock index between 1-4 required");
            }
        }
        if (cType == 'S') {
            position1stSizeChar = 2;
            data = GameEnums.SPACE_DATA.BREAKABLE_BLOCK;
            additionalData = charToInt(tokenBody, 1);
            if (additionalData <= 0) {
                throw new IncorrectStringException(tokenBody, layersRequired);
            }
        }
        if (cType == 'T') {
            position1stSizeChar = 2;
            data = GameEnums.SPACE_DATA.STOP_BLAST;
            additionalData = charToInt(tokenBody, 1);
            if (additionalData <= 0) {
                throw new IncorrectStringException(tokenBody, layersRequired);
            }
        }
        // Remember : cType = tokenBody.char(0)
        if (cType == 'K') { // See exception message
            position1stSizeChar = 3;
            data = GameEnums.SPACE_DATA.STICKY_BOMB; // TODO un jour, on remplacera les "sticky bomb" par des "hostage fruits", qui bloquent simplement les fruits. Mais là je fais les sticky vides (qui donneront des cases vides) et les couleurs (qui exploseront) en bombes !
            additionalData = charToInt(tokenBody, 1);
            additionalData2 = charToInt(tokenBody, 2);
            if ((tokenBody.length() != 5 && tokenBody.length() != 7) || additionalData <= 0 || additionalData2 >= 8) {
                throw new IncorrectStringException(tokenBody, "Required : first the number of layers, then fruit, then 2 or 4 coordinates");
            }
        }
        if (cType == 'k') { // The number of layers only
            if (tokenBody.charAt(1) == 'T') {
                position1stSizeChar = 3;
                data = GameEnums.SPACE_DATA.STICKY_STOP_BLAST;
                additionalData = charToInt(tokenBody, 2);
            } else {
                position1stSizeChar = 2;
                data = GameEnums.SPACE_DATA.STICKY_BOMB;
                additionalData = charToInt(tokenBody, 1);
            }
            if (additionalData <= 0) {
                throw new IncorrectStringException(tokenBody, "1-9 value required for layer.");
            }
            additionalData2 = Constants.NOT_A_FRUIT;
        }
        if (data == null) {
            throw new IncorrectStringException(tokenBody, "Unrecognized item");
        } else if (charactersWithAndAfterIndex(tokenBody, position1stSizeChar) == 4) {
            int x, y;
            int x1 = charToInt(tokenBody, position1stSizeChar);
            int y1 = charToInt(tokenBody, position1stSizeChar + 1);
            int x2 = charToInt(tokenBody, position1stSizeChar + 2);
            int y2 = charToInt(tokenBody, position1stSizeChar + 3);
            for (y = y1; y <= y2; y++) {
                for (x = x1; x <= x2; x++) {
                    this.putData(x, y, data, additionalData, additionalData2);
                }
            }

            if (alternateMode) {
                if (data == GameEnums.SPACE_DATA.FRUIT) {
                    for (y = 0; y < Constants.FIELD_YLENGTH; y++) {
                        for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                            if (x < x1 || x > x2 || y < y1 || y > y2) {
                                this.spaceData[y][x] = GameEnums.SPACE_DATA.VOID_SPAWN;
                            }
                        }
                    }
                }
            }

        } else if (charactersWithAndAfterIndex(tokenBody, position1stSizeChar) == 2)  {
            int x = charToInt(tokenBody, position1stSizeChar);
            int y = charToInt(tokenBody, position1stSizeChar+1);
            this.putData(x, y, data, additionalData, additionalData2);
        }
    }

    // Note : this is the correct place for these somewhat utilitary functions
    private static int charToInt(String s, int pos) {
        char c = s.charAt(pos);
        if (c == 'A') {
            return 10;
        }
        int value = c-'0';
        if (value > 9 || value < 0) {
            throw new IncorrectStringException(s, "Not a digit");
        }
        return value;
    }

    private static int charactersWithAndAfterIndex(String s, int indexPosition) {
        return s.length()-indexPosition;
        // It's a character count, yup
    }

    private void putData(int x, int y, GameEnums.SPACE_DATA data, int additional1, int additional2) {
        this.spaceData[y][x] = data;
        this.additionalSpaceData[y][x] = additional1;
        this.additionalSpaceData2[y][x] = additional2;
        if (data == GameEnums.SPACE_DATA.STICKY_BOMB && additional2 != Constants.NOT_A_FRUIT) {
            this.checkerPotentialFruitsInStickyBombs.add(x, y);
        }
    }

    // Getters

    // Getters for level data
    public int getLockDuration(int x, int y) {
        return (this.locksDuration.get(this.additionalSpaceData[y][x]));
    }

    public int getBreakableItemLevel(int x, int y) {
        return this.additionalSpaceData[y][x];
    }

    public int getStickyBombContent(int x, int y) {
        return this.additionalSpaceData2[y][x];
    }

    public int getFruitValue(int x, int y) {
        return this.additionalSpaceData2[y][x];
    }

    public int getBaskets(int x, int y) {
        return this.basketsSpaceData[y][x];
    }
    public int getNutDropsStrength(int x, int y) {
        return this.basketsSpaceData[y][x]; // Number of turns you should wait...
    }
    public boolean isDropSpace(int x, int y) {
        return (this.basketsSpaceData[y][x] == 1); // Only for downFruit missions...
    }
    public int getHostage(int x, int y) {return this.hostagesSpaceData[y][x];}

    public int getNutValue(int x, int y) {return this.additionalSpaceData[y][x];}

    public int getGoalTimeActionLimit() {return this.goalTimeActionLimit;}
    public int getGoalMovesActionLimit() {return this.goalMovesActionLimit;}
    public int getGoalMovesRelaxLimit() {return this.goalMovesRelaxLimit;}

    // Getters for both level data and level sorting
    public GameEnums.GOAL_KIND getGoalKind() {
        return this.goalKind;
    }
    public int getFruitColours() {
        return this.numberOfFruitKinds;
    }
    public int getReductionFruitColours() { return this.checkerReductibleColour.getList().size(); }

    // Getters for others
    public Transition getTransition() {
        return TransitionManager.getTransitionFromString(this.charsForTransition);
    }

    public int getWidthField() { return widthField; }

    public int getHeightField() { return heightField; }

}
