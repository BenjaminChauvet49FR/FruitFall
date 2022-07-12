package com.example.fruitfall.level;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.GameHandler;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static List<LevelData> levelLists;
    public static int levelNumber;

    public static void init() {
        levelNumber = 0;
        levelLists = new ArrayList<>();
        levelLists.add(new LevelData(generateClassicArray(Constants.FIELD_XLENGTH-1, Constants.FIELD_YLENGTH-1), 4));
        levelLists.add(new LevelData(generateClassicArray(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH), 5));
        levelLists.add(new LevelData(generateClassicArray(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH), 6));
        levelLists.add(new LevelData(generateClassicArray(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH), 8));
        levelLists.add(new LevelData(generateClassicArray(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH), 5, integerListFromString("12")) );
        GameEnums.SPACE_DATA[][] splitArrayVert = generateClassicArray(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
        addSpaces(splitArrayVert, GameEnums.SPACE_DATA.VOID, 5, 0, 5, 9);
        levelLists.add(new LevelData(splitArrayVert, 5));
    }

    private static GameEnums.SPACE_DATA[][] generateClassicArray(int xLength, int yLength) {
        GameEnums.SPACE_DATA[][] theArray = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        int x, y;
        for (y = 0 ; y < yLength ; y++) {
            for (x = 0 ; x < xLength ; x++) {
                theArray[y][x] = GameEnums.SPACE_DATA.FRUIT;
            }
            for (; x < Constants.FIELD_XLENGTH ; x++) {
                theArray[y][x] = GameEnums.SPACE_DATA.VOID;
            }
        }
        for (; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0 ; x < Constants.FIELD_XLENGTH ; x++) {
                theArray[y][x] = GameEnums.SPACE_DATA.VOID;
            }
        }
        return theArray;
    }

    private static void addSpaces(GameEnums.SPACE_DATA[][] theArray, GameEnums.SPACE_DATA element, int x1, int y1, int x2, int y2) {
        int x, y;
        for (y = y1 ; y <= y2 ; y++) {
            for (x = x1; x <= x2; x++) {
                theArray[y][x] = element;
            }
        }

    }

    private static List<Integer> integerListFromString(String s) {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0 ; i < s.length() ; i++) {
            ints.add(s.charAt(i) - '0');
        }
        return ints;
    }

}
