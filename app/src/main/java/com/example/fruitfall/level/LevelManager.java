package com.example.fruitfall.level;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static List<LevelData> levelLists;
    public static int levelNumber;

    public static void init() {
        levelNumber = 0;
        levelLists = new ArrayList<>();
        levelLists.add(new LevelData("f5 F0099 b123456789", "Seule la colonne de gauche est non bloquée"));
        levelLists.add(new LevelData("f5 S51122 S55162 S43344 S47384 S31526 S35566 S27788 S23748", "Casse mania")); // 551551 Faire le copier-coller . // 551551 Faire un menu par couleurs // 551551 réduire les objets à des chaînes de caracs.
        levelLists.add(new LevelData("f5 S14049", "Barre fragile ultime"));
        levelLists.add(new LevelData("f5 S12277", "Fragilité"));
        levelLists.add(new LevelData("f5 S92121 S91212", "Locks et bugs"));
        levelLists.add(new LevelData("f5 B22377", "Paniers express"));
        levelLists.add(new LevelData("f8 B13366", "Paniers express"));
        levelLists.add(new LevelData("f5 B20099 S33366 S44455", "Casse-moi ces blocs !"));
        levelLists.add(new LevelData("f5 B20099 B33366", "Paniers garnis"));
        levelLists.add(new LevelData("f6 B30099 B43366", "Paniers garnis"));
        levelLists.add(new LevelData("f5 X6069 E0659 E7099 L10656 L20858 l8 l14 t0970 t5990 b8 it ml", "Combo verrou et téléportation"));
        levelLists.add(new LevelData("f5 X4059 L16090 E6199 L22030 l3 l6 it ml mf", "Verrous en haut au départ"));
        levelLists.add(new LevelData("f6 L12233 L22637 L36273 L46677 l5 l9 l13 l17 it mA", "Blocs verrouillés, éclair éclair"));
        levelLists.add(new LevelData("f512 iah ml25 mf15 mo5", "2 fruits forcés - feu, éclair, omega"));
        levelLists.add(new LevelData("f4 F0099 iah mo40", "Rush Oméga"));
        levelLists.add(new LevelData("f5 iah mX mL mF", "FeuEclair, feuWild, eclairWild"));
        levelLists.add(new LevelData("f6 iah mX mL mF", "FeuEclair, feuWild, eclairWild"));
        levelLists.add(new LevelData("f6 iah mA mO","Omega, éclairEclair"));
        levelLists.add(new LevelData("f5 X0495 X4059 t03064 t09604 t63664 mA", "Quadrants"));
        levelLists.add(new LevelData("f8 iah ml", "Juste 8 fruits"));
        levelLists.add(new LevelData("f5 X!3366", "Trou au milieu"));
        levelLists.add(new LevelData("f5 X3366", "Trou spawnant au milieu"));
        levelLists.add(new LevelData("f6 X0019 X8099 X2072 X2779", "Arène restreinte"));
        levelLists.add(new LevelData("f6 B30099 S20009 S29099 S32229 S37279 S54359 ih", "Paniers garnis (6c)"));
    }
}