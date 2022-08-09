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
        levelLists.add(new LevelData("f4 F0099 iah", "4 fruits"));
        levelLists.add(new LevelData("f6 L12233 L22637 L36273 L46677 l5 l9 l13 l17 it", "Blocs verrouillés"));
        levelLists.add(new LevelData("f5 iah", "5 fruits"));
        levelLists.add(new LevelData("f5 X0495 X4059 t03064 t09604 t63664", "Quadrants"));
        levelLists.add(new LevelData("f5 X5059 t09602 t2990", "Téléportation"));
        levelLists.add(new LevelData("f512 iah", "5 fruits dont 2 forcés"));
        levelLists.add(new LevelData("f8 iah", "8 fruits"));
        levelLists.add(new LevelData("f6 X0019 X8099 X2072 X2779", "6 fruits, arène restreinte"));
        levelLists.add(new LevelData("f5 X!3366", "5 fruits, trou au milieu"));
        levelLists.add(new LevelData("f5 X3366", "5 fruits, trou spawnant au milieu"));
    }
}