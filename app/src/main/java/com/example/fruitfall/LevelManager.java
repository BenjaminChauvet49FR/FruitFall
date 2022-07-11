package com.example.fruitfall;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static List<LevelData> levelLists;
    public static int levelNumber;

    public static void init() {
        levelNumber = 0;
        levelLists = new ArrayList<>();
        levelLists.add(new LevelData(generateClassicArray(GameHandler.FIELD_XLENGTH-1, GameHandler.FIELD_YLENGTH-1), 4));
        levelLists.add(new LevelData(generateClassicArray(GameHandler.FIELD_XLENGTH, GameHandler.FIELD_YLENGTH), 5));
        levelLists.add(new LevelData(generateClassicArray(GameHandler.FIELD_XLENGTH, GameHandler.FIELD_YLENGTH), 6));
        levelLists.add(new LevelData(generateClassicArray(GameHandler.FIELD_XLENGTH, GameHandler.FIELD_YLENGTH), 8));
        GameEnums.SPACE_DATA[][] splitArrayVert = generateClassicArray(GameHandler.FIELD_XLENGTH, GameHandler.FIELD_YLENGTH);
        addSpaces(splitArrayVert, GameEnums.SPACE_DATA.VOID, 5, 0, 5, 9);
        levelLists.add(new LevelData(splitArrayVert, 5));
    }

    private static GameEnums.SPACE_DATA[][] generateClassicArray(int xLength, int yLength) {
        GameEnums.SPACE_DATA[][] theArray = new GameEnums.SPACE_DATA[GameHandler.FIELD_YLENGTH][GameHandler.FIELD_XLENGTH];
        int x, y;
        for (y = 0 ; y < yLength ; y++) {
            for (x = 0 ; x < xLength ; x++) {
                theArray[y][x] = GameEnums.SPACE_DATA.FRUIT;
            }
            for (; x < GameHandler.FIELD_XLENGTH ; x++) {
                theArray[y][x] = GameEnums.SPACE_DATA.VOID;
            }
        }
        for (; y < GameHandler.FIELD_YLENGTH ; y++) {
            for (x = 0 ; x < GameHandler.FIELD_XLENGTH ; x++) {
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

}
