package com.example.fruitfall.level;

import static com.example.fruitfall.level.LevelCategories.DEBUG;
import static com.example.fruitfall.level.LevelCategories.FUN;
import static com.example.fruitfall.level.LevelCategories.LEVELS_TO_BE;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static List<LevelData> levelLists;
    public static int levelNumber;

    public static void init() {
        levelNumber = 0;
        levelLists = new ArrayList<>();
        levelLists.add(new LevelData(FUN,"B30099 B43366", "f5", "Paniers garnis"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "H107 H116 H127 H136 H147 H25559 X6899 X8697 F68 F86 c0599 vx00", "f5 id m1150 ml20", "Libérez les otages !"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B20099 H13366 H27799 H20729 H31122 H37182", "f5 id", "Libérez les otages !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"L12233 L22637 L36273 L46677", "f6 l5 l9 l13 l17 it mA", "Blocs verrouillés, éclair éclair"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "F0099 b123456789", "f5 ml10 mf5 mo3 id", "Seule la colonne de gauche est non bloquée"));
        levelLists.add(new LevelData(FUN,"","f5 ma999 m1250 is","Lâche-toi !"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "F0099 S509 S518 c0819 v26 v44 v62 v80 c0549 v00 v55", "f5 mL16 mF9 id", "Diagonales sauvages")); // TODO faire une diagonale inversée...
        levelLists.add(new LevelData(LEVELS_TO_BE, "F0099 S509 S518 c0819 v26 v44 v62 v80 c0549 v00 v55", "ma550 m0130 m1130 f5 id", "Diagonales sauvages 2"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B21188 S51182 S41384 S31586 S21788 F1324 c1324 v31 v71 v17 v35 v53 v75 v57","f5 ih", "Casse mania"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"S11229 S24459 S17289","f5 ih mX4 mo4","Omega, éclairEclair + setup à détruire"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B30099 S21016 S20868 S28389 S23191 S23366 S44455" ,"f5 iah","Sous les blocs"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B10099 S11016 S10868 S18389 S13191 S13366","f6 l5 L14455 iah","Sous les blocs"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B20099 S33366 S44455", "f5 ia","Casse-moi ces blocs !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"", "f512 iah ml25 mf15 mo5 id", "2 fruits forcés - feu, éclair, omega"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"","f5 iah mX mL mF id", "FeuEclair, feuWild, eclairWild"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X0495 X4059 t03064 t09604 t63664","f5 mA5 it", "Quadrants"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B30099 S20009 S29099 S32229 S37279 S54359","f6 ih", "Paniers garnis, arène vide"));
        levelLists.add(new LevelData(FUN,""," f4 mo40 isd", "Rush Oméga"));
        levelLists.add(new LevelData(FUN,"B21188","f8 iah", "Juste 8 fruits"));
        levelLists.add(new LevelData(FUN,"","f6 mX mL mF ias", "FeuEclair, feuWild, eclairWild"));

        levelLists.add(new LevelData(DEBUG, "S92121 S91213 B21113 c1123 v35 vh37 vx52 ", "f5","Blocs et bugs"));
        levelLists.add(new LevelData(DEBUG, "S14049", "f5", "Barre fragile ultime"));
        levelLists.add(new LevelData(DEBUG, "S12277", "f5","Fragilité"));
        levelLists.add(new LevelData(DEBUG, "B22377", "f5","Paniers express"));
        levelLists.add(new LevelData(DEBUG,"B20099 B33366", "f5","Paniers garnis"));
        levelLists.add(new LevelData(DEBUG,"X6069 E0659 E7099 L10656 L20858 t0970 t5990 b8", "f5 l8 l14 it ml", "Combo verrou et téléportation"));
        levelLists.add(new LevelData(DEBUG,"X4059 L16090 E6199 L22030","f5 l3 l6 iat ml mf", "Verrous en haut au départ"));
        levelLists.add(new LevelData(DEBUG,"X!3366","f5 it", "Trou au milieu"));
        levelLists.add(new LevelData(DEBUG,"X3366","f5", "Trou spawnant au milieu"));
        levelLists.add(new LevelData(FUN, "B13366", "f8", "Paniers express"));
        levelLists.add(new LevelData(FUN,"X0019 X8099 X2072 X2779", "f6", "Arène restreinte"));
    }
}