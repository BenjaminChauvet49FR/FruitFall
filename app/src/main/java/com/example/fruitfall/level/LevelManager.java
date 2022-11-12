package com.example.fruitfall.level;

import static com.example.fruitfall.level.LevelCategories.DEBUG;
import static com.example.fruitfall.level.LevelCategories.FUN;
import static com.example.fruitfall.level.LevelCategories.LEVELS_TO_BE;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static List<LevelData> levelLists;
    public static int levelNumber;

    public static int currentLevelWidth() {
        return levelLists.get(levelNumber).getWidthField();
    }

    public static int currentLevelHeight() {
        return levelLists.get(levelNumber).getHeightField();
    }

    public static void init() {
        // Note : I chose to let the names behind. After all, ctrl + F is our friend.
        // 'k' 'kT' and 'K' : remember, layer first, (nature of fruit), 2 or 4 coors afterwards
        // 'N' : one at a time, coors first
        // 'L' in infos : moves action, time action, moves chill, time chill
        levelNumber = 0;
        levelLists = new ArrayList<>();
        levelLists.add(new LevelData(DEBUG,"f0 F00 H100 c00 v01 v03 v05 v07 v09 K105356", "f5", "Debug - Otage et retrait d'une couleur"));
        levelLists.add(new LevelData(DEBUG,"k40099 f0 F5357 F3575 f1 F55 f0 F2124 F0212 f1 F22 f0 F1848 F37 f1 F38", "f5", "Debug - Phénomene 'TL bout spécial', Activer sphère Oméga dans un croisement de L-T alors qu'on active un fruit enflammé dans le coin...")); // Horiz alignment of 5 fruits + vert alignment of 3 fruits including one special : used not to be problematic.
        levelLists.add(new LevelData(DEBUG,"k40099 f0 F1213 H21213 F2131 H22131 F10 F01 kT368 T388 F4558 K3238 K3218 f1 F45 F56 F47 F48", "f6", "Debug - otages"));
        levelLists.add(new LevelData(DEBUG,"", "f5 ml3 mL2 mA1", "Debug - missions Eclair"));
        levelLists.add(new LevelData(DEBUG,"", "f5 mf3 mF2 mB1", "Debug - missions Feu"));
        levelLists.add(new LevelData(DEBUG,"", "f5 mo3 mO3 mC1", "Debug - missions Oméga"));
        levelLists.add(new LevelData(DEBUG,"S118 S229 S338 S449 S558 S669 S778 S889 S998 k116 k227 k336 k447 k556 k667 k776 k887 k996 K1114 K2225 K3334 K4445 K5154 K6265 K7374 K8485 K9194 H112 H223 H332 H443 H552 H663 H772 H883 H992 T119 T228 T339 T448 T559 T668 T779 T888 T999", "f8", "Collection d'images - != niveaux 1-9"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X!0009 B30099 B101 S301 c01 v05 v09 X!9099 f4 B291 F91 c91 v95 v99 T25084 k326 c26 v47 v68 v89 v13 v34 v55 v76 v21 v42 v63 v84 v18 v39 v50 v71", "f5  L65#75#55", "Paniers dans des alcoves et stopblasts au dessus"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B31188 K9311 c11 v14 v18 v41 v55 v48 v81 v84 v88", "f6 L45#65#40", "Tu vas pas tous les détruire quand même, si ?"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"N036 K3004 N056 K3006 c0306 vv13 c0316 v23 v43 v63 v83 O0929 O7999", "f5 L60#60#50", "Noix en damier"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F!0088 K4111 K4113 K4131 K4133 c1133 v51 v15 v55", "f5 id m2200 L30#40#25", "Idée de base du retrait d'une couleur"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F!1188 k93366 f2 F4455 B23366 kT333 kT336 kT366 kT363", "f5", "Déloge-moi si tu peux !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B20999 X9099 H109 H118 H127 H138 H149 T11939 T128 H136 H145 T137 T14648 K200008 c0049 vh50", "f6 ", "Pyramide mystère, bombes latérales"));
        levelLists.add(new LevelData(DEBUG,"B40049 T20794 kT203 k204 kT214 k213 K2136 K2146 T10849 c0049 vh50", "f5", "Swirls away"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X99 H18889 H16677 H15859 O55989  N903 N943 N982 N626 N646 N736 N716 c5099 vh00", "f6 ig L60#55#55", "Noix latérales ou centrales"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B20099 S33366 S44455", "f5 ia L50#45#40","Casse-moi ces blocs !"));
        levelLists.add(new LevelData(DEBUG,"X4059 t09604 D6091 O6999", "f4 ih</ d16#8#4 L30#20#20", "La chute !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X9099 X1979 X2868 X3757 X4646 N4530", "f5 ih</ L40#40#35", "Noix à l'air !"));
        levelLists.add(new LevelData(DEBUG,"D30 D60 O0999", "f5 d16#4#2 L30#30#25", "La chute !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"", "f5 iah ml25 mf15 mo5 id L75#105#100", "Feu, éclair, omega"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"","f5 iah mX mL mF id", "FeuEclair, feuWild, eclairWild"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"O0999 k30999 D3060 k606 k614 c0416 v24 v44 v64 v84", "f5 is d8#2#10 L40#45#40", "Make'em fall !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"K1118 k117 k119 c1719 v27 v37 v47 c1749 v57 k208 k298 X!34 X!64 D30 D60 O0999", "f6 ia. d5#2#12 L50#50#40", "Six couleurs. Ou cinq."));
        levelLists.add(new LevelData(LEVELS_TO_BE,"K4100 K4111 c0011 v22 v66 v88 K4209 K4218 c0819 v26 v44 v62 v80 K4144 K4155", "f6 id ma500 m1100 L60#65#50", "Les fruits qui collent, et un dilemme !")); // TODO faire une option pour rendre "transparent" la copie de fruits
        levelLists.add(new LevelData(LEVELS_TO_BE,"X!0019 X!8099 B50409 E05 E07 E09 B49499 E96 E98 kT223 kT226 c2326 v34 v45 v53 v64 v75 c0509 v94", "f4", "Lâche-moi les baskets !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"E0799 k20406 k21517 k22648 c0448 vh54 K3116 K4237 K4167 K4286", "f6 ih m150 m250 mF4 L55#55#40", "'Wildfire' pour libérer de l'espace"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"L12233 L22637 L36273 L46677", "f6 l5 l9 l13 l17 it mA L65#120#50", "Blocs verrouillés, éclair éclair"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F0099 S509 S518 c0819 v26 v44 v62 v80 c0549 v00 v55", "f5 mL9 mF5 id@F L55#75#45", "Diagonales sauvages"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X1019 D00 T101 F102 F109 c0002 v03 v06 S54444 O29 O49 c0049 vh50 t09301 t99601 b09", "f5 d6#0#0 L40#50#30", "Gare à l'atterrissage"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B21989 k20595 E0696 k30797 E0898 k40999 K4290 K4299 K4200 K4209", "f5  L45#60#35", "Lignes tombantes"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"S31112 S33132 D21 kT222 c1132 v61 v17 v67 O29 O79", "f5 d4#0#0 L35#30#25", "Fais-les tomber des bacs !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"L10999 N062 N074 N086 c0608 v16 v26 v36 v46 c0648 v56 O10999", "f5 l5 L55#55#45", "Plein de noix, haha !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F!6099 E0559 B20559 B16099 X!0454 f0 F13 H13 f1 F22 H22 f2 F31 H31 f3 F40 H40 t13151 t22251 t31351 t40451", "f5 ia. L45#45#30", "C'est barré"));
        levelLists.add(new LevelData(DEBUG,"f2 F04 F15 c0415 v06 v08 c0419 v24 v44 v64 v84", "f5 mF15 mL30 mO10", "Going wild + mon beau damier"));
        levelLists.add(new LevelData(DEBUG,"X0549 X0202 X2222 F2527 F3747 c0049 vx50", "f5 mL9 mF5 id@F L55#75#45", "Forme WTF"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F!0077", "f4 ihsdfw mO4 ma250", "Sérieux, des sphères Oméga ici ?"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"O0999 S20999 H10898 S10797 k206 k215 k224 k233 k242 L10003 L204 L305 c0046 vh50 D50", "f6 ih. d6#1#8 l8 l16 l24", "Good luck to make'em fall !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F!0088 K4111 K4113 K4131 K4133 c1133 v51 v15 v55 B50088", "f5 is", "Muhaha, cinq couleurs c'est hard !"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F0099 S509 S518 c0819 v26 v44 v62 v80 c0549 v00 v55", "ma550 m0130 m1130 f5 id@F", "Diagonales sauvages 2"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"N2625 N3825 N4725 N4825 N5725 N5825 N6825 N7625 O20999", "f4 iw", "Bam bam noix"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"F!0187 F1070 F1878 B21177 k211 k222 k233 k244 c1144 v44 vh41 vh14", "f6 if", "Prêt à dégager de la place ?"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B21089 k30009 k39099 k311 k313 k322 k324 k215 k226 k217 k228 c1128 vh71", "f5 if", "Prêt à dégager de la place ?"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"S21738 B21188 S27688 k635 k646 k655 k666 c0599 vx00", "f6 ig", "Prêt à dégager de la place ?"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"K4111 K4113 K4131 K4133 c1133 v61 v16 v66  N2010 N4015 N5015 N7010 O40999", "f5 is", "Noix et attaques"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X0495 X4059 t03064 t09604 t63664 N0010 N1110 N2210 N3310 O56999", "f4", "Quadrants noix"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"H10549 k25599 K1158 K1166 K1178 K1186 B30949", "f5", "Aile gauche ou aile droite ?")); // TODO annuler le "hostage"
        levelLists.add(new LevelData(LEVELS_TO_BE,"k10709 k21316 c0319 v23 v43 v63 v83", "f5 ih ma2000", "Marais 2000"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X4059 t09604 N007 N107 N207 N307 O76999", "f5 ih", "Malheur si tu les laisses tomber"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"", "f4 ma9999", "On s'en fout"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"E0999 S10898 S21787 S32676 N455 N555 N403 N503 H14050 O20999", "f5 ih", "Casse ces noix"));
        levelLists.add(new LevelData(FUN,"B30099 B43366", "f5", "Paniers garnis"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "H107 H116 H127 H136 H147 H25559 X6899 X8697 F68 F86 c0599 vx00", "f5 id m1150 ml20", "Libérez les otages !"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B20099 H13366 H27799 H20729 H31122 H37182", "f5 id", "Libérez les otages !"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "F0099 b123456789", "f5 ml10 mf5 mo3 id", "Seule la colonne de gauche est non bloquée"));
        levelLists.add(new LevelData(FUN,"","f5 ma999 m1250 is","Lâche-toi !"));
        levelLists.add(new LevelData(LEVELS_TO_BE, "B21188 S51182 S41384 S31586 S21788 F1324 c1324 v31 v71 v17 v35 v53 v75 v57","f5 ih", "Casse mania"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"S11229 S24459 S17289","f5 ih mX4 mo4","Omega, éclairEclair + setup à détruire"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B30099 S21016 S20868 S28389 S23191 S23366 S44455" ,"f5 iah","Sous les blocs"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"B10099 S11016 S10868 S18389 S13191 S13366 L14455","f6 l5 iah","Sous les blocs"));
        levelLists.add(new LevelData(LEVELS_TO_BE,"X0495 X4059 t03064 t09604 t63664","f5 mA5 it L65#120#50", "Quadrants"));
        levelLists.add(new LevelData(FUN,""," f4 mo40 isd", "Rush Oméga"));
        levelLists.add(new LevelData(FUN,"B21188","f8 iah", "Juste 8 fruits"));
        levelLists.add(new LevelData(FUN,"","f6 mX mL mF ias", "FeuEclair, feuWild, eclairWild"));

        levelLists.add(new LevelData(DEBUG,"K1122 K1133 c2233 v66 K1227 H34759 K1236 c2637 v44 v62 K1144 K1155", "f6 ih ma500", "Les fruits qui collent (fragile)"));
        levelLists.add(new LevelData(DEBUG, "S92121 S91213 B21113 c1123 v35 vh37 vx52 ", "f5","Blocs et bugs"));
        levelLists.add(new LevelData(DEBUG, "S14049", "f5", "Barre fragile ultime"));
        levelLists.add(new LevelData(DEBUG, "S12277", "f5","Fragilité"));
        levelLists.add(new LevelData(DEBUG, "B22377", "f5","Paniers express"));
        levelLists.add(new LevelData(DEBUG,"B20099 B33366", "f5","Paniers garnis"));
        levelLists.add(new LevelData(DEBUG,"X6069 E0659 E7099 L10656 L20858 t0970 t5990 b8", "f5 l8 l14 it ml L65#120#50", "Combo verrou et téléportation"));
        levelLists.add(new LevelData(DEBUG,"X4059 L16090 E6199 L22030","f5 l3 l6 iat ml mf", "Verrous en haut au départ"));
        levelLists.add(new LevelData(DEBUG,"X!3366","f5 it", "Trou au milieu"));
        levelLists.add(new LevelData(DEBUG,"X3366","f5", "Trou spawnant au milieu"));
        levelLists.add(new LevelData(FUN, "B13366", "f8", "Paniers express"));
        levelLists.add(new LevelData(FUN,"X0019 X8099 X2072 X2779", "f6", "Arène restreinte"));
    }
}