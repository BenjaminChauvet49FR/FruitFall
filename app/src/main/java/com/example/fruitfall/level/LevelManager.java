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

    public static void init() {
        levelNumber = 0;
        levelLists = new ArrayList<>();
        levelLists.add(new LevelData("f5 B22377", "Paniers express (5c)"));
        levelLists.add(new LevelData("f8 B13366", "Paniers express (8c)"));
        levelLists.add(new LevelData("f5 B20099 S33366 S44455", "Casse-moi ces blocs ! (5c)"));
        levelLists.add(new LevelData("f5 B20099 B33366", "Paniers garnis (5c)"));
        levelLists.add(new LevelData("f6 B30099 B43366", "Paniers garnis (6c)"));
        levelLists.add(new LevelData("f5 X6069 E0659 E7099 L10656 L20858 l8 l14 t0970 t5990 b8 it ml", "Combo verrou et téléportation"));
        levelLists.add(new LevelData("f5 X4059 L16090 E6199 L22030 l3 l6 it ml mf", "Verrous en haut au départ"));
        levelLists.add(new LevelData("f6 L12233 L22637 L36273 L46677 l5 l9 l13 l17 it mA", "Blocs verrouillés, éclair éclair"));
        levelLists.add(new LevelData("f512 iah ml25 mf15 mo5", "5 fruits dont 2 forcés - feu, éclair, omega"));
        levelLists.add(new LevelData("f4 F0099 iah mo40", "4 fruits, oméga"));
        levelLists.add(new LevelData("f5 iah mX mL mF", "5 fruits - feuEclair, feuWild, eclairWild"));
        levelLists.add(new LevelData("f6 iah mX mL mF", "6 fruits - feuEclair, feuWild, eclairWild"));
        levelLists.add(new LevelData("f6 iah mA mO","6 fruits, omega, éclairEclair"));
        levelLists.add(new LevelData("f5 X0495 X4059 t03064 t09604 t63664 mA", "Quadrants"));
        levelLists.add(new LevelData("f8 iah ml", "8 fruits"));
        levelLists.add(new LevelData("f5 X!3366", "5 fruits, trou au milieu"));
        levelLists.add(new LevelData("f5 X3366", "5 fruits, trou spawnant au milieu"));
        levelLists.add(new LevelData("f6 X0019 X8099 X2072 X2779", "6 fruits, arène restreinte"));
        levelLists.add(new LevelData("f6 B30099 S20009 S29099 S32229 S37279 S54359 ih", "Paniers garnis (6c)"));
    }
}