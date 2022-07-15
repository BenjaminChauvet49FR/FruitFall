package com.example.fruitfall.level;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.GameHandler;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelManager {
    public static List<LevelData> levelLists;
    public static int levelNumber;

    private static int xLD =  Constants.FIELD_XLENGTH; // LD = length default
    private static int yLD =  Constants.FIELD_XLENGTH;

    public static void init() {
        levelNumber = 0;
        levelLists = new ArrayList<>();
        GameEnums.SPACE_DATA[][] customArray = new GameEnums.SPACE_DATA[yLD][xLD];

        levelLists.add(new LevelData(refreshArray(customArray,xLD-1, yLD-1), 4, "4 fruits"));

        refreshArray(customArray, xLD, yLD);
        addSpaces(customArray, GameEnums.SPACE_DATA.VOID, 5, 0, 5, yLD-1);
        LevelData myLevel000 = new LevelData(customArray, 5, "Terrain splité");
        myLevel000.addTeleporters(0, yLD-1, 6, 0, 2);
        myLevel000.addTeleporters(3, yLD-1, 8, 0, 2);

        refreshArray(customArray,xLD, yLD);
        addSpaces(customArray, GameEnums.SPACE_DATA.VOID, 3, 3, 6, 6);
        LevelData myLevel002 = new LevelData(customArray, 5, "Terrain qui est vide au milieu");
        addSpaces(customArray, GameEnums.SPACE_DATA.VOID_SPAWN, 3, 6, 6, 6);
        LevelData myLevel001 = new LevelData(customArray, 5, "Terrain qui spawne au milieu");
        refreshArray(customArray, xLD-1, yLD);
        LevelData myLevel003 = new LevelData(customArray, 5, "Terrain avec 3 colonnes vides");
        myLevel003.preventSpawn(Arrays.asList(0, 4, 8));
        levelLists.add(myLevel000);
        levelLists.add(myLevel001);
        levelLists.add(myLevel002);
        levelLists.add(myLevel003);
        levelLists.add(new LevelData(refreshArray(customArray,xLD, yLD), 5, "5 fruits"));
        levelLists.add(new LevelData(refreshArray(customArray,xLD, yLD), 6, "6 fruits"));
        levelLists.add(new LevelData(refreshArray(customArray,xLD, yLD), 8, "8 fruits"));
        levelLists.add(new LevelData(refreshArray(customArray,xLD, yLD), 5, "5 fruits dont 2 imposés", integerListFromString("12")));
    }

    private static GameEnums.SPACE_DATA[][] refreshArray(GameEnums.SPACE_DATA[][] theArray, int xLength, int yLength) {
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
