package com.example.fruitfall.level;

import android.widget.Space;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.SpaceCoors;
import com.example.fruitfall.exceptions.IncorrectStringException;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private int fruitNumber;
    private GameEnums.SPACE_DATA[][] spaceData;
    private List<Integer> forcedIndexes;
    private List<Integer> locksDuration;
    // IMPORTANT : coordinate in position (i) in inFallTeleporters must be at a base, otherwise it can be very confusing. And coordinate in position (i) in outFallTeleporters must be at a summit.
    private List<SpaceCoors> inFallTeleporters;
    private List<SpaceCoors> outFallTeleporters;
    private String name;
    private GameEnums.SPACE_DATA[] topRowSpawn;

    public int getFruitColours() {
        return this.fruitNumber;
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

    public GameEnums.SPACE_DATA getTopRowSpawn(int x) {
        return topRowSpawn[x];
    }

    // Note : the string is parsed into tokens ('split(" ")'). Read below to understand how each token is then interpreted.
    public LevelData(String s, String name) {
        int x, y;
        this.name = name;
        this.forcedIndexes = new ArrayList<>();
        this.topRowSpawn = new GameEnums.SPACE_DATA[Constants.FIELD_XLENGTH];
        for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
            this.topRowSpawn[x] = GameEnums.SPACE_DATA.VOID_SPAWN;
        }

        this.spaceData = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        for (y = 0; y < Constants.FIELD_YLENGTH; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                this.spaceData[y][x] = GameEnums.SPACE_DATA.FRUIT;
            }
        }

        this.inFallTeleporters = new ArrayList<>();
        this.outFallTeleporters = new ArrayList<>();
        this.locksDuration = new ArrayList<>();

        String[] obtainedStrings = s.split(" ");
        for (String token : obtainedStrings) {
            switch(token.charAt(0)) {
                case 'b' : interpretStringBlockSpawn1stRow(token.substring(1));
                    break;
                case 'f' : interpretStringFruits(token.substring(1));
                    break;
                case 'l' : interpretStringLockLength(token.substring(1));
                    break;
                case 't' : interpretStringTeleporters(token.substring(1));
                    break;
                default : interpretStringClassic(token);
                    break;
            } 
        }
    }

    private void interpretStringFruits(String tokenBody) {
        this.fruitNumber = charToInt(tokenBody, 0);
        for ( int i = 1 ; i < tokenBody.length() ; i++) {
            this.forcedIndexes.add(charToInt(tokenBody, i));
        }
    }

    private void interpretStringBlockSpawn1stRow(String tokenBody) {
        for ( int i = 1 ; i < tokenBody.length() ; i++) {
            this.topRowSpawn[charToInt(tokenBody, i)] = GameEnums.SPACE_DATA.VOID;
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
        int position1stSizeChar = 1;
        GameEnums.SPACE_DATA data = null;
        if (cType == 'F') {
            data = GameEnums.SPACE_DATA.FRUIT;
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
            char cParam = tokenBody.charAt(1);
            if (cParam == '1') {
                data = GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH1;
            } else if (cParam == '2') {
                data = GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH2;
            } else if (cParam == '3') {
                data = GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH3;
            } else if (cParam == '4') {
                data = GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH4;
            } else {
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

    public int getLockDuration(GameEnums.SPACE_DATA data) {
        switch (data) {
            case DELAYED_LOCK_LENGTH1: return this.locksDuration.get(0);
            case DELAYED_LOCK_LENGTH2: return this.locksDuration.get(1);
            case DELAYED_LOCK_LENGTH3: return this.locksDuration.get(2);
            case DELAYED_LOCK_LENGTH4: return this.locksDuration.get(3);
            default : return 1;
        }
    }

}
