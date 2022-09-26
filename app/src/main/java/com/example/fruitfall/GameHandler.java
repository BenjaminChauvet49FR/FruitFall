package com.example.fruitfall;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.fruitfall.checkers.Checker;
import com.example.fruitfall.checkers.IntChecker;
import com.example.fruitfall.checkers.SpaceChecker;
import com.example.fruitfall.level.LevelData;
import com.example.fruitfall.spaces.BreakableBlock;
import com.example.fruitfall.spaces.EmptySpace;
import com.example.fruitfall.spaces.Fruit;
import com.example.fruitfall.spaces.DelayedLock;
import com.example.fruitfall.spaces.HostageLock;
import com.example.fruitfall.spaces.Nut;
import com.example.fruitfall.spaces.OmegaSphere;
import com.example.fruitfall.spaces.SpaceFiller;
import com.example.fruitfall.spaces.StickyBomb;
import com.example.fruitfall.spaces.VoidSpace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameHandler {
    private static final int[] coefDirectionalX = {-1, 0, 1, 0};
    private static final int[] coefDirectionalY = {0, -1, 0, 1};
    private static final int[] coefDirectional8X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final int[] coefDirectional8Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    private static final int[] coefDirectionalClockwiseTurningX = {1, 1, -1, -1};
    private static final int[] coefDirectionalClockwiseTurningY = {-1, 1, 1, -1};

    public GameTimingHandler gth;  // TODO le passer en privé et le rendre accessible par getters...

    private final SpaceFiller[][] arrayField;
    private final Checker checkerAlignedFruits = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH); // Can ONLY contain coordinates of spaces with fruits
    private final Checker checkerToBeEmptiedSpaces = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH); // Contains newly destroyed spaces "to be emptied" since the last unstable check.
    private final Checker checkerFallingElements = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final boolean[][] arrayShouldFruitsBeSpawned = new boolean[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private final SpaceCoors[][] arrayTeleporterCorrespondingExit = new SpaceCoors[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH]; // IMPORTANT : "out" array contains coors of corresponding "in" and vice-versa.
    private final SpaceCoors[][] arrayTeleporterCorrespondingEntrance = new SpaceCoors[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private final SpaceFiller[][] arrayFutureField = new SpaceFiller[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private final IntChecker checkerScoreDestructionFall = new IntChecker(Constants.FIELD_XLENGTH,Constants.FIELD_YLENGTH, 0);
    private final IntChecker checkerScoreDestructionSpecial = new IntChecker(Constants.FIELD_XLENGTH,Constants.FIELD_YLENGTH, 0);
    private final SpaceChecker<SpaceFiller> checkerSpawnFruits = new SpaceChecker<>(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final IntChecker checkerHorizAlignment = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);
    private final IntChecker checkerVertAlignment = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);
    public static int[] gameIndexToImageIndex = new int[Constants.RESOURCES_NUMBER_FRUITS];
    private List<SpaceCoors> listToBeActivatedSpecialFruits = new ArrayList<>();
    private final List<SpaceCoors> listFruitsToCreateCoors = new ArrayList<>();
    private final List<SpaceFiller> listFruitsToCreateKind = new ArrayList<>();
    private final List<WaitingNutData> listWaitingNutData = new ArrayList<>();
    private final List<Integer> listIdFruitsToRemove = new ArrayList<>();
    private final List<WaitingNutData> listWaitingNutDataThisTime = new ArrayList<>();
    private final Checker checkerDropUpperRightHere = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final Checker checkerDropUpperLeftHere = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final Checker checkerBlockDiagonalSqueeze = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH); // During a "empty spaces handling", any space that is full OR immediately below a sucking space must not be able to vaccuum an element diagonally, neither the spaces below it until the next wall.

    private final IntChecker checkerNutDrops = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0); // During a "empty spaces handling", any space that is full OR immediately below a sucking space must not be able to vaccuum an element diagonally, neither the spaces below it until the next wall.

    private final List<SpaceCoors> listDelayedLocks = new ArrayList<>();
    // Note : convention : before a stable check, only one source / destination per colour.
    private final SpaceCoors[]  omegaSourceCoorsByColour = new SpaceCoors[Constants.RESOURCES_NUMBER_FRUITS];
    private final List<SpaceCoors>[] omegaTargetsCoorsByColour = new List[Constants.RESOURCES_NUMBER_FRUITS];
    private final int[] numberRemainingBombsThisColour = new int[Constants.RESOURCES_NUMBER_FRUITS];

    private String title;
    private int numberOfFruitKinds; // TODO some instance of this array can be replaced by the new "numberOfSpawnableFruitKinds" but I didn't want to do so because it didn't matter yet
    private int score;
    private int numberElapsedMoves;
    private int thisMoveFruitsDestroyedByFall;
    private int phaseCount;
    private int countRemainingLocks;

    private int xSwapCenter, ySwapCenter;
    private int xSwapSide, ySwapSide;
    private int idFruitOmegaDestruction = Constants.NOT_A_FRUIT;
    private int xSourceOmegaDestruction = Constants.NOT_A_SPACE_COOR;
    private int ySourceOmegaDestruction = Constants.NOT_A_SPACE_COOR;
    private GameEnums.WHICH_SWAP lastSwap = GameEnums.WHICH_SWAP.NONE;
    private final int[] amountsMission = new int[Constants.MAX_MISSIONS];
    private int numberOfMissions;
    private final GameEnums.ORDER_KIND[] kindsOfMissions = new GameEnums.ORDER_KIND[Constants.MAX_MISSIONS];

    private final int[][] arrayBaskets = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private int basketsCount;
    private int nutsHealthCount;

    private int numberOfSpawnableFruitKinds = 0;
    private final int[] indexesOfGameSpawnableFruits = new int[Constants.RESOURCES_NUMBER_FRUITS]; // Only used to spawn new fruits ; initialized at [0, 1, 2.. n-1], updated when a colour is permanently removed

    private GameEnums.GOAL_KIND goalKind;

    // Cheat part
    private boolean toleranceMode = false;

    public GameHandler() {
        this.arrayField = new SpaceFiller[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        gth = new GameTimingHandler(this);
    }

    // ----------------------------
    // Misc lil methods

    public int getIdFruit(int x, int y) {
        return this.arrayField[y][x].getIdFruit();
    }

    public SpaceFiller getSpace(int x, int y) {
        return this.arrayField[y][x];
    }

    // Precondition : x,y supposed to be a fruit
    private boolean gotAlignment(int x, int y, int x2, int y2, int x3, int y3) {
        int idFruit = this.getIdFruit(x, y);
        return (idFruit != Constants.NOT_A_FRUIT && idFruit == this.getIdFruit(x2, y2) && idFruit == this.getIdFruit(x3, y3));
    }

    // Check if a space has a FREE fruit
    public boolean hasFruit(int x, int y) {
        return (this.arrayField[y][x] instanceof Fruit);
    }

    public boolean isASpace(int x, int y) {
        return (this.arrayField[y][x].isASpace());
    }

    public boolean shouldSpawnFruit(int x, int y) {
        return this.arrayShouldFruitsBeSpawned[y][x];
    }

    private void spawnRandomFruit(int x, int y) {
        this.checkerSpawnFruits.add(x, y,
                new Fruit(this.indexesOfGameSpawnableFruits[new Random().nextInt(this.numberOfSpawnableFruitKinds)])
        );
    }

    /* The "listWaitingNutData" must be already sorted with a ready-to-add nut in first place
     since we treat it as a FIFO */
    private void replaceFruitWithNut(int x, int y) {
        this.checkerSpawnFruits.remove(x, y);
        this.checkerSpawnFruits.add(x, y, new Nut(this.listWaitingNutData.get(0).getCount()));
        this.listWaitingNutData.remove(0);
    }

    // Note : used before fruits are removed, of course
    // "special fruits" should include Omega spheres ! (activateSpecialFruitBlast)
    private boolean isSpecialFruit(int x, int y) {
        return this.arrayField[y][x].getPower() != GameEnums.FRUITS_POWER.NONE;
    }

    // Should be considered empty for fall calculations
    private boolean isEmpty(int x, int y) {
        return this.arrayField[y][x] instanceof EmptySpace;
    }

    private boolean isHostage(int x, int y) {
        return this.arrayField[y][x] instanceof HostageLock;
    }

    private void testAndAlertAboutAlignedFruits(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (gotAlignment(x1, y1, x2, y2, x3, y3)) {
            this.checkerAlignedFruits.add(x1, y1);
            this.checkerAlignedFruits.add(x2, y2);
            this.checkerAlignedFruits.add(x3, y3);
        }
    }

    private boolean areAcceptableCoordinates(int x, int y) {
        return (x >= 0 && y >= 0 && x < Constants.FIELD_XLENGTH && y < Constants.FIELD_YLENGTH);
    }

    // ----------------------------
    // Initialization

    public void start(LevelData ld) {
        // Clear everything !
        checkerAlignedFruits.clear();
        checkerToBeEmptiedSpaces.clear();
        checkerFallingElements.clear();
        checkerScoreDestructionFall.clear();
        checkerScoreDestructionSpecial.clear();
        checkerSpawnFruits.clear();
        checkerHorizAlignment.clear();
        checkerVertAlignment.clear();
        listDelayedLocks.clear();
        listIdFruitsToRemove.clear();
        checkerDropUpperLeftHere.clear();
        checkerBlockDiagonalSqueeze.clear();
        checkerDropUpperRightHere.clear();
        checkerNutDrops.clear();
        listWaitingNutData.clear();
        listWaitingNutDataThisTime.clear();

        this.score = 0;
        this.thisMoveFruitsDestroyedByFall = 0;
        this.phaseCount = 0;
        this.countRemainingLocks = 0;
        this.numberElapsedMoves = 0;

        ld.deploy();

        this.numberOfMissions = ld.getMissionsNumber();
        for (int i = 0 ; i < Constants.MAX_MISSIONS ; i++) {
            this.kindsOfMissions[i] = ld.getKind(i);
            this.amountsMission[i] = ld.getAmount(i);
        }

        int x, y;
        Random rand = new Random();

        // Read data for everything
        this.title = ld.getTitle();

        // Read data for fruits
        int[] arrayNTI = new int[Constants.RESOURCES_NUMBER_FRUITS]; // NTI = not taken indexes
        int i;
        for (i = 0 ; i < Constants.RESOURCES_NUMBER_FRUITS ; i++) {
            arrayNTI[i] = i;
        }
        this.numberOfFruitKinds = ld.getFruitColours();
        this.numberOfSpawnableFruitKinds = this.numberOfFruitKinds;
        for (i = 0 ; i < this.numberOfFruitKinds ; i++) {
            this.omegaTargetsCoorsByColour[i] = new ArrayList<>();
            this.omegaSourceCoorsByColour[i] = null;
            this.numberRemainingBombsThisColour[i] = 0;
            this.indexesOfGameSpawnableFruits[i] = i;
        }
        int numberChosen = 0;
        // Forced part
        if (ld.getForcedIndexes() != null) {
            for (Integer indexSelected : ld.getForcedIndexes()) {
                gameIndexToImageIndex[numberChosen] = indexSelected;
                arrayNTI[indexSelected] = -1;
                numberChosen++;
            }
        }
        // Random fruits
        int numberToMeet;
        int indexSelected;
        int numberMet;
        while (numberChosen < this.numberOfFruitKinds) {
            numberToMeet = rand.nextInt(Constants.RESOURCES_NUMBER_FRUITS-numberChosen);
            numberToMeet++; // (1 to numberOfFruitKinds-numberChosen) The "i-th" not taken index met is the one desired.
            numberMet = 0;
            indexSelected = 0;
            while (numberMet < numberToMeet) {
                if (arrayNTI[indexSelected] != -1) {
                    numberMet++;
                }
                indexSelected++;
            }
            indexSelected--;
            gameIndexToImageIndex[numberChosen] = arrayNTI[indexSelected];
            arrayNTI[indexSelected] = -1;
            numberChosen++;
        }

        // Teleportings !
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                this.arrayTeleporterCorrespondingEntrance[y][x] = null;
                this.arrayTeleporterCorrespondingExit[y][x] = null;
            }
        }
        SpaceCoors coorsIn, coorsOut;
        for (i = 0 ; i < ld.getTeleportersSource().size() ; i++) {
            coorsIn = ld.getTeleportersSource().get(i);
            coorsOut = ld.getTeleportersDestination().get(i);
            this.arrayTeleporterCorrespondingEntrance[coorsOut.y][coorsOut.x] = new SpaceCoors(coorsIn.x, coorsIn.y);
            this.arrayTeleporterCorrespondingExit[coorsIn.y][coorsIn.x] = new SpaceCoors(coorsOut.x, coorsOut.y);
        }

        // Setup field !
        // conditions (to be completed) : teleports done.
        boolean shouldGainSpawn;
        GameEnums.SPACE_DATA mainData;
        int fruitKind;
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                shouldGainSpawn = shouldHaveSpawn(ld, x, y);
                mainData = ld.getData(x, y);
                if (mainData == GameEnums.SPACE_DATA.FRUIT) {
                    // Generate fruit
                    this.arrayField[y][x] = new Fruit(rand.nextInt(this.numberOfFruitKinds));
                } else if (mainData == GameEnums.SPACE_DATA.DELAYED_LOCK) {
                    this.arrayField[y][x] = new DelayedLock(ld.getLockDuration(x, y));
                    this.countRemainingLocks++;
                    this.listDelayedLocks.add(new SpaceCoors(x, y));
                } else if (mainData == GameEnums.SPACE_DATA.BREAKABLE_BLOCK) {
                    this.arrayField[y][x] = new BreakableBlock(ld.getBreakableBlockLevel(x, y));
                    // Note : the list of coors may be actually useless, unlike locks which are discounted each turn
                } else if (mainData == GameEnums.SPACE_DATA.EMPTY) {
                    this.arrayField[y][x] = new EmptySpace();
                } else if (mainData == GameEnums.SPACE_DATA.STICKY_BOMB) {
                    fruitKind = ld.getStickyBombContent(x, y);
                    this.arrayField[y][x] = new StickyBomb(ld.getStickyBombLevel(x, y), fruitKind);
                    if (fruitKind != Constants.NOT_A_FRUIT) {
                        this.numberRemainingBombsThisColour[fruitKind]++;
                    }
                } else {
                    // Not a space able to handle fruits ; still need to initialize arrays.
                    this.arrayField[y][x] = new VoidSpace();
                    this.arrayShouldFruitsBeSpawned[y][x] = false;
                    shouldGainSpawn = false;
                }
                // Should put test alignment here, after fruits are known
                if (this.arrayField[y][x].getIdFruit() != Constants.NOT_A_FRUIT) {
                    if (x >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x-1, y, x-2, y);
                    }
                    if (y >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x, y-1, x, y-2);
                    }
                }
                this.arrayShouldFruitsBeSpawned[y][x] = shouldGainSpawn;
            }
        }

        this.goalKind = ld.getGoalKind();
        this.basketsCount = 0;
        if (ld.getGoalKind() == GameEnums.GOAL_KIND.BASKETS) {
            for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                    if (this.arrayField[y][x].mayDisappear()) {
                        this.arrayBaskets[y][x] = ld.getBaskets(x, y);
                        this.basketsCount += this.arrayBaskets[y][x];
                    } else {
                        this.arrayBaskets[y][x] = 0;
                    }
                }
            }
        }
        if (ld.getGoalKind() == GameEnums.GOAL_KIND.NUTS) {
            for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                    if (this.arrayField[y][x].mayDisappear() && ld.getNutDropsStrength(x, y) > 0) {
                        this.checkerNutDrops.add(x, y, ld.getNutDropsStrength(x, y));
                    }
                }
            }
        }

        // Make sure there are no aligned fruits at start !
        List<SpaceCoors> formerListGetAlignedFruit = new ArrayList<>();
        while(!this.checkerAlignedFruits.getList().isEmpty()) {

            // Note : unfortunately, we need to copy coors one by one
            //formerListGetAlignedFruit = this.listGetAlignedFruit;
            formerListGetAlignedFruit.clear();
            for(SpaceCoors coors : this.checkerAlignedFruits.getList()) {
                formerListGetAlignedFruit.add(new SpaceCoors(coors.x, coors.y));
            }

            this.checkerAlignedFruits.clear();

            // Renew fruits
            for (SpaceCoors coors : formerListGetAlignedFruit) {
                this.arrayField[coors.y][coors.x] = new Fruit(rand.nextInt(this.numberOfFruitKinds));
                // Remember, hostages are managed at the end
            }
            // So... what's next with new fruits ?
            for (SpaceCoors coors : formerListGetAlignedFruit) {
                testAndAlertAboutAlignedFruitsAroundSpace(coors.x, coors.y);
            }
        }

        // Replacing elements, such as nuts (after spaces, but before hostage logs)
        this.nutsHealthCount = 0;
        SpaceCoors coors;
        // Warning : likely overrides other space data ! (not a big deal if simple fruits)
        for (i = 0 ; i < ld.getNutsValues().size() ; i++ ) {
            this.nutsHealthCount += ld.getNutsValues().get(i);
            coors = ld.getNutsCoors().get(i);
            this.replaceData(coors.x, coors.y, new Nut(ld.getNutsValues().get(i)));
        }

        // Now, the hostage locks
        int hostageLevel;
        SpaceFiller spaceF;
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                hostageLevel = ld.getHostage(x, y);
                spaceF = this.arrayField[y][x];
                if (spaceF.isASpace() && hostageLevel > 0) {
                    this.arrayField[y][x] = new HostageLock(spaceF, hostageLevel);
                }
            }
        }

        this.gth.init();
    }

    // In level initialization, tells whether fruits should be by default spawned up from this space or not
    private boolean shouldHaveSpawn (LevelData ld, int x, int y) {
        if (this.arrayTeleporterCorrespondingEntrance[y][x] != null) {
            return false;
        } else if (y != 0) {
            return (ld.getData(x, y-1) == GameEnums.SPACE_DATA.VOID_SPAWN);
        } else {
            return (ld.getTopRowSpawn(x) == GameEnums.SPACE_DATA.VOID_SPAWN);
        }
    }

    private void replaceData(int x, int y, SpaceFiller sf) {
        if (this.arrayField[y][x] instanceof StickyBomb ) {
            int fruitId = ((StickyBomb) this.arrayField[y][x]).getContainedFruitId();
            if (fruitId != Constants.NOT_A_FRUIT) {
                this.numberRemainingBombsThisColour[fruitId]--;
            }
        }
        this.arrayField[y][x] = sf;
    }

    // ----------------------------
    // Input

    /*
        Important : here are how thing works :
        // Input method (GH) -> start method; F of game not affected
        // start method (GTH) -> change state, initialize frames that will be increased by step
        // step method (GTH) -> increases one variable (unless normal state). Once counter has reached threshold, change state :  trigger method
        // trigger method (GH) -> stop method ; data of game affected ; start method or end methods
        // end methods (GTH) -> stop methods ; return to normal state
        // stop method (GTH) -> clearing things
        // TODO clairement pas à jour
    */

    public void inputSwap(int x1, int y1, int x2, int y2) {
        this.gth.startSwap(x1, y1, x2, y2);
    }

    public void setTolerance() {
        this.toleranceMode = true;
    }

    // ----------------------------
    // End of transitions

    public void triggerWelcoming() {
        this.gth.startGame();
    }

    public void triggerSwap(int x1, int y1, int x2, int y2) {
        this.gth.stopSwap();
        SpaceFiller tmp = this.arrayField[y1][x1];
        this.arrayField[y1][x1] = this.arrayField[y2][x2];
        this.arrayField[y2][x2] = tmp;
        this.lastSwap = this.getSwapNature(x1, y1, x2, y2);
        this.controlMissionsWithSwap(this.lastSwap);
        if (this.lastSwap != GameEnums.WHICH_SWAP.INVALID) {
            this.performStableCheck(true);
        } else {
            this.gth.startBackSwap(x1, y1, x2, y2);
        }
    }

    public void triggerBackSwap(int x1, int y1, int x2, int y2) {
        this.gth.stopSwap();
        SpaceFiller tmp = this.arrayField[y1][x1];
        this.arrayField[y1][x1] = this.arrayField[y2][x2];
        this.arrayField[y2][x2] = tmp;
        this.gth.endSwap();
    }

    private GameEnums.WHICH_SWAP getSwapNature(int x1, int y1, int x2, int y2) {
        this.xSwapSide = x2;
        this.ySwapSide = y2;
        this.xSwapCenter = x1;
        this.ySwapCenter = y1;
        GameEnums.FRUITS_POWER power1 = this.arrayField[y1][x1].getPower();
        GameEnums.FRUITS_POWER power2 = this.arrayField[y2][x2].getPower();
        boolean light1 = (power1 == GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING || power1 == GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING);
        boolean light2 = (power2 == GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING || power2 == GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING);
        boolean fire1 = (power1 == GameEnums.FRUITS_POWER.FIRE);
        boolean fire2 = (power2 == GameEnums.FRUITS_POWER.FIRE);
        boolean omega1 = (power1 == GameEnums.FRUITS_POWER.OMEGA_SPHERE);
        boolean omega2 = (power2 == GameEnums.FRUITS_POWER.OMEGA_SPHERE);

        this.numberElapsedMoves++;
        if (light1 && light2) {
            return GameEnums.WHICH_SWAP.ELECTRIC_ELECTRIC;
        }
        if ((light1 && fire2) || (light2 && fire1)) {
            return GameEnums.WHICH_SWAP.FIRE_ELECTRIC;
        }
        if (fire1 && fire2) {
            return GameEnums.WHICH_SWAP.FIRE_FIRE;
        }
        if (omega1 && omega2) {
            this.xSourceOmegaDestruction = x1;
            this.ySourceOmegaDestruction = y1;
            return GameEnums.WHICH_SWAP.OMEGA_OMEGA;
        }
        if (omega1 && fire2) {
            this.idFruitOmegaDestruction = this.getIdFruit(x2, y2);
            this.xSourceOmegaDestruction = x1;
            this.ySourceOmegaDestruction = y1;
            return GameEnums.WHICH_SWAP.OMEGA_FIRE;
        }
        if (omega2 && fire1) {
            this.idFruitOmegaDestruction = this.getIdFruit(x1, y1);
            this.xSourceOmegaDestruction = x2;
            this.ySourceOmegaDestruction = y2;
            return GameEnums.WHICH_SWAP.OMEGA_FIRE;
        }
        if (omega1 && light2) {
            this.idFruitOmegaDestruction = this.getIdFruit(x2, y2);
            this.xSourceOmegaDestruction = x1;
            this.ySourceOmegaDestruction = y1;
            return GameEnums.WHICH_SWAP.OMEGA_ELECTRIC;
        }
        if (omega2 && light1) {
            this.idFruitOmegaDestruction = this.getIdFruit(x1, y1);
            this.xSourceOmegaDestruction = x2;
            this.ySourceOmegaDestruction = y2;
            return GameEnums.WHICH_SWAP.OMEGA_ELECTRIC;
        }
        // Put them at the end, or else !
        if (omega1 && this.hasFruit(x2, y2)) {
            this.idFruitOmegaDestruction = this.getIdFruit(x2, y2);
            this.xSourceOmegaDestruction = x1;
            this.ySourceOmegaDestruction = y1;
            return GameEnums.WHICH_SWAP.FRUIT_OMEGA;
        }
        if (omega2 && this.hasFruit(x1, y1)) {
            this.idFruitOmegaDestruction = this.getIdFruit(x1, y1);
            this.xSourceOmegaDestruction = x2;
            this.ySourceOmegaDestruction = y2;
            return GameEnums.WHICH_SWAP.FRUIT_OMEGA;
        }
        if (validFruitAlignment(x1, y1) || validFruitAlignment(x2, y2)) {
            return GameEnums.WHICH_SWAP.FRUIT_FRUIT;
        }
        if (this.toleranceMode) {
            this.toleranceMode = false;
            return GameEnums.WHICH_SWAP.FRUIT_FRUIT;
        }
        this.numberElapsedMoves--;
        return GameEnums.WHICH_SWAP.INVALID;
    }

    private boolean validFruitAlignment(int x, int y) {
        if (x >= 1) {
            if (x >= 2) {
                if (gotAlignment(x, y, x-1, y, x-2, y)) {
                    return true;
                }
            }
            if (x <= Constants.FIELD_XLENGTH-2) {
                if (gotAlignment(x, y, x-1, y, x+1, y)) {
                    return true;
                }
                if (x <= Constants.FIELD_XLENGTH-3) {
                    if (gotAlignment(x, y, x+1, y, x+2, y)) {
                        return true;
                    }
                }
            }
        } else {
            if (gotAlignment(0, y, 1, y, 2, y)) {
                return true;
            }
        }
        if (y >= 1) {
            if (y >= 2) {
                if (gotAlignment(x, y, x, y-1, x, y-2)){
                    return true;
                }
            }
            if (y <= Constants.FIELD_YLENGTH-2) {
                if (gotAlignment(x, y-1, x, y, x, y+1)){
                    return true;
                }
                if (y <= Constants.FIELD_YLENGTH-3) {
                    if (gotAlignment(x, y, x, y+1, x, y+2)) {
                        return true;
                    }
                }
            }
        } else {
            return gotAlignment(x, 0, x, 1, x, 2);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void triggerAfterOmegaStasis() {
        this.triggerAfterDestructionStasis(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void triggerAfterDestructionStasis(boolean afterOmegaStasis) {
        // Oh, and also restart score arrays :
        if (!afterOmegaStasis) {
            this.checkerScoreDestructionSpecial.clear();
            this.checkerScoreDestructionFall.clear();
        } else {
            for (int i = 0 ; i < this.numberOfFruitKinds ; i++ ) {
                this.omegaSourceCoorsByColour[i] = null;
                this.omegaTargetsCoorsByColour[i].clear();
            }
        }

        // Omega check arrays (since it's for animations) :

        // Quite simplistic :
        // If there are no special fruits / special objects (omega) waiting for being destroyed, move on with falls.
        // Otherwise, destroy, detect new special fruits, restart a stasis
        boolean goForDestructionAgain = this.permanentlyRemoveEmptiedColours();
        goForDestructionAgain = this.activateSpecialFruits() | goForDestructionAgain;
        if (goForDestructionAgain) {
            this.performDestruction(false);
        } else if (afterOmegaStasis) {
            this.gth.startDestruction(false); // Note : written at a time "performDestruction" only contained a call to "gth.startDestruction"
            // Yesh there will be two destructions stases in a row
        } else {
            this.performCleanUpAndFall();
        }
    } // TODO : bug sphère oméga destruction sphère oméga ? bug lié à une couleur supprimée définitiement


    // The final thing to do before going to destruction check
    private void performCleanUpAndFall() {
        this.emptyTheSpaces();
        this.fullyCheckFallingElements();
        this.causeDutiesToDropAtRandom();
        this.gth.startFall();
    }

    private void causeDutiesToDropAtRandom() {
        // Here, all nuts waiting to fall should randomly replace one of the spaces.
        // List all spawn spaces, replace randomy a spawn space by a fruit ready to fall,
        // then update the list.
        int numberSpawnSpaces = this.checkerSpawnFruits.getList().size();
        int readyWaitingNuts = 0;
        while (readyWaitingNuts < numberSpawnSpaces && readyWaitingNuts < this.listWaitingNutData.size() && this.listWaitingNutData.get(readyWaitingNuts).getDelay() <= 0) {
            readyWaitingNuts++;
        }
        SpaceCoors coors;
        if (readyWaitingNuts > 2) {
            List<SpaceCoors> possibleSpaces = new ArrayList<>();
            for (int i = 0 ; i < this.checkerSpawnFruits.getList().size() ; i++) {
                coors = this.checkerSpawnFruits.getList().get(i);
                possibleSpaces.add(new SpaceCoors(coors.x, coors.y));
            }
            Collections.shuffle(possibleSpaces);
            for (int i = 0 ; i < readyWaitingNuts ; i++) {
                coors = possibleSpaces.get(i);
                replaceFruitWithNut(coors.x, coors.y);
            }
        }
        else if (readyWaitingNuts == 1) {
            coors = this.checkerSpawnFruits.getList().get(new Random().nextInt(this.checkerSpawnFruits.getList().size()));
            replaceFruitWithNut(coors.x, coors.y);
        }
    }

    // Should be called at the end of a stable check
    private void emptyTheSpaces() {
        // Note : actually, checkerToBeEmptiedSpaces mustn't be cleaned before, because of the animations.
        for (SpaceCoors coors : this.checkerToBeEmptiedSpaces.getList()) {
            // TODO Il faut récupérer les fruits ici avant qu'ils disparaissent définitivement
            //this.destroyedFruits[coors.y][coors.x] = this.arrayField[coors.y][coors.x].copy();
            this.arrayField[coors.y][coors.x] = new EmptySpace();
        }
        this.checkerToBeEmptiedSpaces.clear();
    }

    private void swapNonOmega(GameEnums.FRUITS_POWER power) {
        this.arrayField[ySwapCenter][xSwapCenter] = new Fruit(this.arrayField[ySwapCenter][xSwapCenter].getIdFruit(), power);
        this.removeSpecialFruit(this.xSwapSide, this.ySwapSide);
        this.removeSpecialFruit(this.xSwapCenter, this.ySwapCenter);
        this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.xSwapCenter, this.ySwapCenter));
    }

    private void swapOmega(GameEnums.FRUITS_POWER power1, GameEnums.FRUITS_POWER power2) {
        // Warning : regular omega swap is copied onto this !
        // We have no real choice to use omegaSourceCoorsByColour and omegaTargetsCoorsByColour in order to reflect which ones are really destroyed. It is recensed "by colour" of course.
        // Note : neither the omega sphere nor the powered standard fruit are to be activated
        this.removeSpecialFruit(this.xSourceOmegaDestruction, this.ySourceOmegaDestruction);
        this.omegaSourceCoorsByColour[this.idFruitOmegaDestruction] = (new SpaceCoors(this.xSourceOmegaDestruction, this.ySourceOmegaDestruction));
        int xx, yy;
        boolean usePower1 = true;
        for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
            for (xx = 0; xx < Constants.FIELD_XLENGTH ; xx++) {
                if (this.getIdFruit(xx, yy) == this.idFruitOmegaDestruction) {
                    if (this.arrayField[yy][xx] instanceof HostageLock) {
                        this.activateSpecialFruitBlast(xx, yy, new ArrayList<>());
                        this.activateSpecialFruitBlast(xx, yy, new ArrayList<>());
                        // So if the fruit is hidden behind a lock, instead we blast the space twice !
                    } else {
                        this.arrayField[yy][xx] = new Fruit(this.arrayField[yy][xx].getIdFruit(), usePower1 ? power1 : power2);
                        this.checkerToBeEmptiedSpaces.add(xx, yy);
                    }
                    this.listToBeActivatedSpecialFruits.add(new SpaceCoors(xx, yy));
                    usePower1 = !usePower1;
                    this.omegaTargetsCoorsByColour[this.idFruitOmegaDestruction].add(new SpaceCoors(xx, yy));
                }
            }
        }
    }

    private void removeSpecialFruit(int x, int y) {
        this.checkerToBeEmptiedSpaces.add(x, y);
    }

    /*
    Check method !
    When fruits should be checked for destruction or creation.
    IMPORTANT : arrayFruit is NOT updated here for the purpose of drawing !
    */
    private void performStableCheck(boolean comesFromSwap) {
        this.checkerAlignedFruits.clear();
        this.checkerScoreDestructionFall.clear(); // Don't reinitialize the jackpot count, please ;)

        boolean isAlignment = false;
        if (!comesFromSwap) {
            this.lastSwap = GameEnums.WHICH_SWAP.NONE;
        }
        if (this.lastSwap == GameEnums.WHICH_SWAP.ELECTRIC_ELECTRIC) {
            this.swapNonOmega(GameEnums.FRUITS_POWER.VIRTUAL_LIGHTNING_LIGHTNING);
         } else if (this.lastSwap == GameEnums.WHICH_SWAP.FIRE_ELECTRIC) {
            this.swapNonOmega(GameEnums.FRUITS_POWER.VIRTUAL_FIRE_LIGHTNING);
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.FIRE_FIRE) {
            this.swapNonOmega(GameEnums.FRUITS_POWER.VIRTUAL_FIRE_FIRE);
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_FIRE) {
            this.swapOmega(GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_FIRE, GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_FIRE);
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_ELECTRIC) {
            this.swapOmega(GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_HORIZ_LIGHTNING, GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_VERT_LIGHTNING);
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_OMEGA) {
            this.removeSpecialFruit(this.xSwapSide, this.ySwapSide);
            this.removeSpecialFruit(this.xSwapCenter, this.ySwapCenter);
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.xSourceOmegaDestruction, this.ySourceOmegaDestruction));
            this.arrayField[this.ySourceOmegaDestruction][this.xSourceOmegaDestruction] = new Fruit(this.arrayField[this.ySourceOmegaDestruction][this.xSourceOmegaDestruction].getIdFruit(), GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_OMEGA);
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.FRUIT_OMEGA) {
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.xSourceOmegaDestruction, this.ySourceOmegaDestruction));
            this.removeSpecialFruit(this.xSourceOmegaDestruction, this.ySourceOmegaDestruction);
        } else {
            this.fallBeforeAlignmentCheck();
            if (this.checkerToBeEmptiedSpaces.getList().isEmpty() || this.lastSwap == GameEnums.WHICH_SWAP.FRUIT_FRUIT) {
                this.alignmentDestructionCheck();
                isAlignment = true;
            }
        }

        if (this.checkerToBeEmptiedSpaces.getList().isEmpty()) {
            // Nothing new destroyed : move on.
            this.thisMoveFruitsDestroyedByFall = 0;
            this.triggerNextPhaseAfterStableCheck();
        } else {
            this.performDestruction(isAlignment);
        }
    }

    // Destruction time !
    private void performDestruction(boolean isAlignment) {
        this.gth.startDestruction(isAlignment);
    }

    // Must be called BEFORE alignment check in stable check.
    private void fallBeforeAlignmentCheck() {
        int x, y;
        for (SpaceCoors coors : this.checkerNutDrops.getList()) {
            x = coors.x;
            y = coors.y;
            if (this.arrayField[y][x] instanceof Nut) {
                this.listWaitingNutDataThisTime.add(new WaitingNutData(
                        ((Nut) this.arrayField[y][x]).getCount(),
                        this.checkerNutDrops.get(x,y))
                );
                this.checkerToBeEmptiedSpaces.add(x,y);
            }
        }
    }

    private void alignmentDestructionCheck() {
        // Check all horizontal and vertical fruits, and set them for destruction
        this.checkerHorizAlignment.clear();
        this.checkerVertAlignment.clear();
        // Alignment check
        int x, y, currentFruit, xAft, yAft, xx, yy; // x After, y After last fruit
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0 ; x < Constants.FIELD_XLENGTH ; x++) {
                currentFruit = this.getIdFruit(x, y);
                if (currentFruit != Constants.NOT_A_FRUIT) {
                    // Horizontal check
                    if (x == 0 || this.getIdFruit(x-1, y) != currentFruit) {
                        xAft = x+1;
                        while (xAft < Constants.FIELD_XLENGTH && this.getIdFruit(xAft, y) == currentFruit) {
                            xAft++;
                        }
                        // xx = last coordinate with the correct fruit PLUS ONE
                        if (xAft - x >= 3) {
                            this.checkerHorizAlignment.add(x, y, xAft-x);
                            this.destroyByAlignment(x, y);
                            for (xx = x+1; xx < xAft ; xx++) {
                                this.checkerHorizAlignment.add(xx, y, 1);
                                this.destroyByAlignment(xx, y);
                            }
                        }
                    }
                    // Vertical check
                    if (y == 0 || this.getIdFruit(x, y-1) != currentFruit) {
                        yAft = y+1;
                        while (yAft < Constants.FIELD_YLENGTH && this.getIdFruit(x, yAft) == currentFruit) {
                            yAft++;
                        }
                        if (yAft - y >= 3) {
                            this.checkerVertAlignment.add(x, y, yAft - y);
                            this.destroyByAlignment(x, y);
                            for (yy = y+1; yy < yAft ; yy++) {
                                this.checkerVertAlignment.add(x, yy, 1);
                                this.destroyByAlignment(x, yy);
                            }
                        }
                    }
                }
            }
        }

        // Creating special fruits here !
        this.createNewSpecialFruits();

        // Special fruit activation for regularly destroyed fruits
        this.listToBeActivatedSpecialFruits.clear(); // Note : could be cleared also elsewhere... ?
        for (SpaceCoors coors : this.checkerToBeEmptiedSpaces.getList()) {
            x = coors.x;
            y = coors.y;
            if (this.arrayField[y][x].getPower() != GameEnums.FRUITS_POWER.NONE) {
                this.listToBeActivatedSpecialFruits.add(new SpaceCoors(x, y));
            }
        }
    }

    // Supposed goal : take down 1 level of the lock ; replace it if it is empty
    private void attackHostage(int x, int y) {
        HostageLock h = (HostageLock) this.arrayField[y][x];
        h.downgrade();
        if (h.getCount() == 0) {
            if (this.isEmpty(x, y)) {
                this.checkerToBeEmptiedSpaces.add(x, y);
            } else {
                this.arrayField[y][x] = h.getHostage();
            }
        }
    }

    private void destroyByAlignment(int x, int y) {
        if (this.isHostage(x, y)) { // Instance of hostage lock
            this.attackHostage(x, y);
        } else if (this.checkerToBeEmptiedSpaces.add(x, y)) {
            if (!this.isSpecialFruit(x, y)) {
                // Philosophie : Considérer que si on a un fruit spécial ici on ne doit pas faire tout ce qui suit...
                this.catchBasket(x, y);
                this.tryToDecreaseDirectlyAdjacentStuffAround(x, y);
                int scoreAmount;// 3 times : 5 7 9 10 11 12 13 14 15 15 16 16 17 17 18
                if (this.thisMoveFruitsDestroyedByFall < 9) {
                    scoreAmount = 5+2*(this.thisMoveFruitsDestroyedByFall/3);
                } else if (this.thisMoveFruitsDestroyedByFall < 24) {
                    scoreAmount = 10+(this.thisMoveFruitsDestroyedByFall-9)/3;
                } else {
                    scoreAmount = 15+(this.thisMoveFruitsDestroyedByFall-24)/6;
                }
                this.thisMoveFruitsDestroyedByFall++;
                this.checkerScoreDestructionFall.add(x, y, scoreAmount);
                this.score += scoreAmount; // Score adding !
                this.advanceFruitMission(x, y);
            }
        }
    }
    
    private void createNewSpecialFruits() {
        for (SpaceCoors coors : this.checkerHorizAlignment.getList()) {
            this.createNewSpecialFruitHorizChecker(coors.x, coors.y);
        }
        for (SpaceCoors coors : this.checkerVertAlignment.getList()) {
            this.createNewSpecialFruitVertChecker(coors.x, coors.y);
        }
    }

    // Do not create anything if any special fruit is met.
    private void createNewSpecialFruitHorizChecker(int x, int y) {
        int xx, yy, yTransversalHead;
        boolean createdSpecial = false;
        int numberAlign, numberAlignTransversal;
        this.listFruitsToCreateCoors.clear(); // Note : these lists are only used in this method & submethods.
        this.listFruitsToCreateKind.clear();
        numberAlign = this.checkerHorizAlignment.get(x, y);
        if (numberAlign == 3 || numberAlign == 4) {
            for (xx = x ; xx < x + numberAlign ; xx++) {
                if (this.isSpecialFruit(xx, y)) {return;} // Any special fruit = we move on.
                if (this.checkerVertAlignment.get(xx, y) > 0) {
                    yy = y;
                    numberAlignTransversal = 1;
                    while (this.checkerVertAlignment.get(xx, yy) == 1) {
                        if (this.isSpecialFruit(xx, yy)) {return;}
                        yy--;
                        numberAlignTransversal++;
                    } // numberAlignTransversal = number of fruits identical going upwards starting with yy included
                    if (this.isSpecialFruit(xx, yy)) {return;}
                    yTransversalHead = yy;
                    yy = y+1;
                    while (yy < Constants.FIELD_YLENGTH && this.checkerVertAlignment.get(xx, yy) == 1) {
                        if (this.isSpecialFruit(xx, yy)) {return;}
                        yy++;
                        numberAlignTransversal++;
                    }
                    if (numberAlignTransversal == 3 || numberAlignTransversal == 4) {
                        // TODO Il faudra voir ce qu'on veut en cas de test 3x3 fruits identiques
                        this.reserveSpecialFruit(xx, y, new Fruit(this.getIdFruit(x, y), GameEnums.FRUITS_POWER.FIRE));
                        createdSpecial = true;
                    }
                    if (numberAlignTransversal >= 5) {
                        this.reserveSpecialFruit(xx, yTransversalHead + (numberAlign-1)/2, new OmegaSphere());
                        createdSpecial = true;
                    }
                } //End of "if checker vertical alignment"
            }
            if (numberAlign == 4 && !createdSpecial) {
                int xDefault = x+1;
                if (this.lastSwap != GameEnums.WHICH_SWAP.NONE) {
                    xDefault = this.xSwapCenter;
                }
                this.createSpecialFruit(xDefault, y, new Fruit(this.getIdFruit(x, y), GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING), 2);
            }
        }
        if (numberAlign >= 5) {
            for (xx = x ; xx < x + numberAlign ; xx++) {
                if (this.isSpecialFruit(xx, y)) {return;}
            }
            this.createSpecialFruit(x+(numberAlign-1)/2, y, new OmegaSphere(), 5);
        }
        this.createReservedSpecialFruits();
    }

    // TODO la liste "listFruitsToCreateKind" serait-elle inutile ? Pourrait-on réserver la liste aux créations de fruits de feu ?  A voir...
    private void reserveSpecialFruit(int x, int y, SpaceFiller sf) {
        this.listFruitsToCreateCoors.add(new SpaceCoors(x, y));
        this.listFruitsToCreateKind.add(sf);
    }

    private void createReservedSpecialFruits() {
        SpaceFiller filler;
        int scoreAmount;
        for (int i = 0 ; i < this.listFruitsToCreateCoors.size() ; i++) {
            filler = this.listFruitsToCreateKind.get(i);
            scoreAmount = (filler.getPower() == GameEnums.FRUITS_POWER.OMEGA_SPHERE ? 3 : (filler.getPower() == GameEnums.FRUITS_POWER.FIRE ? 3 : 2));
            this.createSpecialFruit(this.listFruitsToCreateCoors.get(i).x, this.listFruitsToCreateCoors.get(i).y, filler, scoreAmount);
        }
    }

    // Only check if there is nothing done horizontally - it should not cross anything horizontal because it would have already been checked.
    private void createNewSpecialFruitVertChecker(int x, int y) {
        // Note : no need to clean lists such as listFruitsToCreateCoors and listFruitsToCreateKind
        int numberAlign = this.checkerVertAlignment.get(x, y);
        if (numberAlign >= 4) {
            for (int yy = y; yy < y + numberAlign; yy++) {
                if (this.checkerHorizAlignment.get(x, yy) > 0 || this.isSpecialFruit(x, yy)) {
                    return;
                }
            }
            if (numberAlign == 4) {
                int yDefault = (this.lastSwap != GameEnums.WHICH_SWAP.NONE) ? this.ySwapCenter : y+1;
                this.createSpecialFruit(x, yDefault, new Fruit(this.getIdFruit(x, y), GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING), 2);
            } else {
                this.createSpecialFruit(x, y + (numberAlign - 1) / 2, new OmegaSphere(), 5);
            }
        }
    }

    private void createSpecialFruit(int x, int y, SpaceFiller spaceFiller, int correspondingScoreAmount) {
        this.arrayField[y][x] = spaceFiller;
        this.checkerToBeEmptiedSpaces.remove(x, y);
        this.checkerScoreDestructionFall.add(x, y, correspondingScoreAmount); // TODO score pour "création fruit spécial ?"
        this.score += correspondingScoreAmount; // Score adding !
    }

    /*
     * List "delayed locks" and removes their counter by one.
     * Should one fall to 0, it becomes destroyed.
     * Returns the list of coors of newly destroyed.
     */
    private List<SpaceCoors> lockDiscountAndDestroy() {
        int x, y;
        List<SpaceCoors> newlyDestroyed = new ArrayList<>();
        if (this.countRemainingLocks > 0) {
            for (SpaceCoors coors : this.listDelayedLocks) {
                x = coors.x;
                y = coors.y;
                if (this.arrayField[y][x] instanceof DelayedLock) {
                    DelayedLock delayedLock = (DelayedLock) (this.arrayField[y][x]);
                    delayedLock.decreaseCount();
                    if (delayedLock.getCount() == 0) {
                        this.countRemainingLocks--;
                        this.checkerToBeEmptiedSpaces.add(x, y);
                        newlyDestroyed.add(new SpaceCoors(x, y));
                    }
                }
            }
        }
        return newlyDestroyed;
    }

    private void triggerNextPhaseAfterStableCheck() {
        this.phaseCount++; // Note : phaseCount should only be started here
        switch(this.phaseCount) {
            case 1 :
                List<SpaceCoors> newlyDestroyed = this.lockDiscountAndDestroy();
                if (!newlyDestroyed.isEmpty()) {
                    this.gth.startDestructionLocks(newlyDestroyed);
                } else {
                    this.triggerNextPhaseAfterStableCheck();
                }
            break;
            case 2 : // Hand held back to player
                this.endBeforeNewPlayerMove();
                this.gth.endAllFalls();
                this.phaseCount = 0;
            break;
        }
    }

    private boolean permanentlyRemoveEmptiedColours() {
        int x, y, tempIndex;
        List<SpaceCoors> newListToBeActivated = new ArrayList<>();
        for (int idRemoveMe : this.listIdFruitsToRemove) {
            this.omegaSourceCoorsByColour[idRemoveMe] = (new SpaceCoors(0, 0));// TODO changer par le "milieu" lorsqu'on redimensionnera l'arène (ou par une meilleure animation)
            for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                    if (this.getIdFruit(x, y) == idRemoveMe && !this.checkerToBeEmptiedSpaces.get(x, y)) {
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        this.omegaTargetsCoorsByColour[idRemoveMe].add(new SpaceCoors(x, y));
                    }
                }
            }
            tempIndex = this.indexesOfGameSpawnableFruits[idRemoveMe];
            this.indexesOfGameSpawnableFruits[idRemoveMe] = this.indexesOfGameSpawnableFruits[this.numberOfSpawnableFruitKinds-1];
            this.indexesOfGameSpawnableFruits[this.numberOfSpawnableFruitKinds-1] = tempIndex;
            this.numberOfSpawnableFruitKinds--;
        }
        this.listToBeActivatedSpecialFruits.addAll(newListToBeActivated);
        boolean result = !this.listIdFruitsToRemove.isEmpty();
        this.listIdFruitsToRemove.clear();
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean activateSpecialFruits() {
        List<SpaceCoors> newListToBeActivated = new ArrayList<>();
        int x, y, fruit, xx, yy;
        boolean leftMargin1, leftMargin2, upMargin1, upMargin2, rightMargin1, rightMargin2, downMargin1, downMargin2;
        boolean goForDestructionAgain = false;
        if (!this.listToBeActivatedSpecialFruits.isEmpty()) {

            // Count each fruit for Omega spheres
            // The most present one gets destroyed first, etc...
            // In case of a swap, the id of the stripped fruit is artificially boosted so it is ranked first !
            int[] countRemainingFruits = new int[this.numberOfFruitKinds];
            if (this.idFruitOmegaDestruction != Constants.NOT_A_FRUIT) {
                // NOTE : Swapping with Omega should NOT directly result into an alignment-related destructing of a StickyBomb.
                countRemainingFruits[this.idFruitOmegaDestruction] = Constants.DUMMY_BOOST_FRUIT_COUNT;
                this.idFruitOmegaDestruction = Constants.NOT_A_FRUIT;
            }
            for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                    fruit = this.getIdFruit(x, y);
                    if (fruit != Constants.NOT_A_FRUIT && !this.checkerToBeEmptiedSpaces.get(x, y)) {
                        countRemainingFruits[fruit]++;
                    }
                }
            }

            List<Integer> orderedFruitIndexes = new ArrayList<>();
            for (int i = 0 ; i < this.numberOfFruitKinds ; i++) {
                orderedFruitIndexes.add(i);
            }
            orderedFruitIndexes.sort((a, b) -> countRemainingFruits[b]-countRemainingFruits[a]);
            int mostPresentFruitId = 0;

            // Now, activate them !
            for (SpaceCoors coors : this.listToBeActivatedSpecialFruits) {
                x = coors.x;
                y = coors.y;
                switch (this.arrayField[y][x].getPower()) {
                    case FIRE:
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        this.advanceMission(GameEnums.ORDER_KIND.FIRE, 1);
                        leftMargin1 = (x > 0);
                        leftMargin2 = (x > 1);
                        upMargin1 = (y > 0);
                        upMargin2 = (y > 1);
                        rightMargin1 = (x < Constants.FIELD_XLENGTH - 1);
                        rightMargin2 = (x < Constants.FIELD_XLENGTH - 2);
                        downMargin1 = (y < Constants.FIELD_YLENGTH - 1);
                        downMargin2 = (y < Constants.FIELD_YLENGTH - 2);
                        if (leftMargin1) {
                            this.activateSpecialFruitBlast(x - 1, y, newListToBeActivated);
                            if (leftMargin2) {
                                this.activateSpecialFruitBlast(x - 2, y, newListToBeActivated);
                            }
                            if (downMargin1) {
                                this.activateSpecialFruitBlast(x - 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.activateSpecialFruitBlast(x - 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (rightMargin1) {
                            this.activateSpecialFruitBlast(x + 1, y, newListToBeActivated);
                            if (rightMargin2) {
                                this.activateSpecialFruitBlast(x + 2, y, newListToBeActivated);
                            }
                            if (downMargin1) {
                                this.activateSpecialFruitBlast(x + 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.activateSpecialFruitBlast(x + 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (upMargin1) {
                            this.activateSpecialFruitBlast(x, y - 1, newListToBeActivated);
                            if (upMargin2) {
                                this.activateSpecialFruitBlast(x, y - 2, newListToBeActivated);
                            }
                        }
                        if (downMargin1) {
                            this.activateSpecialFruitBlast(x, y + 1, newListToBeActivated);
                            if (downMargin2) {
                                this.activateSpecialFruitBlast(x, y + 2, newListToBeActivated);
                            }
                        }
                        break;
                    case HORIZONTAL_LIGHTNING:
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                        for (xx = 0; xx < Constants.FIELD_XLENGTH; xx++) {
                            this.activateSpecialFruitBlast(xx, y, newListToBeActivated);
                        } // WARNING : on the day I'll make blocked lightnings, I'll have to blast the center space.
                        break;
                    case VERTICAL_LIGHTNING:
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                        for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                            this.activateSpecialFruitBlast(x, yy, newListToBeActivated);
                        }
                        break;
                    case OMEGA_SPHERE:
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 1);
                        if (mostPresentFruitId < this.numberOfFruitKinds) {
                            int colour = orderedFruitIndexes.get(mostPresentFruitId);
                            for (yy = 0 ; yy < Constants.FIELD_YLENGTH ; yy++) {
                                for (xx = 0 ; xx < Constants.FIELD_XLENGTH ; xx++) {
                                    if (this.getIdFruit(xx, yy) == colour && !this.checkerToBeEmptiedSpaces.get(xx, yy)) {
                                        this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                                        this.omegaTargetsCoorsByColour[colour].add(new SpaceCoors(xx, yy));
                                    }
                                }
                            }
                            if (this.omegaTargetsCoorsByColour[colour].size() > 0) {
                                this.omegaSourceCoorsByColour[colour] = new SpaceCoors(x, y);
                            }
                            mostPresentFruitId++;
                        }
                        break;
                    case VIRTUAL_LIGHTNING_LIGHTNING:
                        for (xx = 0; xx < Constants.FIELD_XLENGTH; xx++) {
                            this.activateSpecialFruitBlast(xx, y, newListToBeActivated);
                        }
                        for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                            this.activateSpecialFruitBlast(x, yy, newListToBeActivated);
                        }
                        break;
                    case VIRTUAL_FIRE_LIGHTNING:
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        for (int i = 0 ; i < 8 ; i++) {
                            xx = x + coefDirectional8X[i];
                            yy = y + coefDirectional8Y[i];
                            while (areAcceptableCoordinates(xx, yy)) { // Note : later on, it may be blocked
                                this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                                xx += coefDirectional8X[i];
                                yy += coefDirectional8Y[i];
                            }
                        }
                        break;
                    case VIRTUAL_FIRE_FIRE: // TODO à  noter : échange fruit spécial et fruit normal, mais un fruit spécial prend la place du premier ... (5 vs 4)
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        int dist, step, dir;
                        for (dir = 0 ; dir <= 3 ; dir++) {
                            for (dist = 1 ; dist <= 3 ; dist++) {
                                xx = this.xSwapCenter + dist*coefDirectionalX[dir];
                                yy = this.ySwapCenter + dist*coefDirectionalY[dir];
                                for (step = 0 ; step < dist ; step++) {
                                    if (areAcceptableCoordinates(xx, yy)) {
                                        this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                                    }
                                    xx += coefDirectionalClockwiseTurningX[dir];
                                    yy += coefDirectionalClockwiseTurningY[dir];
                                }
                            }
                        }
                        break;
                    case VIRTUAL_OMEGA_HORIZ_LIGHTNING:
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        xx = x-1;
                        yy = y;
                        while (xx >= Math.max(x-2, 0)) {
                            this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                            xx--;
                        }
                        xx = x+1;
                        while (xx <= Math.min(x+2, Constants.FIELD_XLENGTH-1)) {
                            this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                            xx++;
                        }
                        break;
                    case VIRTUAL_OMEGA_VERT_LIGHTNING:
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        xx = x;
                        yy = y-1;
                        while (yy >= Math.max(y-2, 0)) {
                            this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                            yy--;
                        }
                        yy = y+1;
                        while (yy <= Math.min(y+2, Constants.FIELD_XLENGTH-1)) {
                            this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                            yy++;
                        }
                        break;
                    case VIRTUAL_OMEGA_FIRE:
                        this.activateSpecialFruitBlast(x, y, newListToBeActivated);
                        leftMargin1 = (x > 0);
                        upMargin1 = (y > 0);
                        rightMargin1 = (x < Constants.FIELD_XLENGTH - 1);
                        downMargin1 = (y < Constants.FIELD_YLENGTH - 1);
                        if (leftMargin1) {
                            this.activateSpecialFruitBlast(x - 1, y, newListToBeActivated);
                            if (downMargin1) {
                                this.activateSpecialFruitBlast(x - 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.activateSpecialFruitBlast(x - 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (rightMargin1) {
                            this.activateSpecialFruitBlast(x + 1, y, newListToBeActivated);
                            if (downMargin1) {
                                this.activateSpecialFruitBlast(x + 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.activateSpecialFruitBlast(x + 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (upMargin1) {
                            this.activateSpecialFruitBlast(x, y - 1, newListToBeActivated);
                        }
                        if (downMargin1) {
                            this.activateSpecialFruitBlast(x, y + 1, newListToBeActivated);
                        }
                        break;
                    case VIRTUAL_OMEGA_OMEGA:
                        for (yy = 0 ; yy < Constants.FIELD_YLENGTH ; yy++) {
                            for (xx = 0; xx < Constants.FIELD_XLENGTH; xx++) {
                                this.activateSpecialFruitBlast(xx, yy, newListToBeActivated);
                            }
                        }
                        break;
                    case NONE:
                        break;
                }
                goForDestructionAgain = true;
            }
        }
        this.listToBeActivatedSpecialFruits = newListToBeActivated;
        return goForDestructionAgain;
    }

    private void activateSpecialFruitBlast(int x, int y, List<SpaceCoors> newList) {
        if (this.hasFruit(x, y) || this.hasOmegaSphere(x, y)) {
            if (this.checkerToBeEmptiedSpaces.add(x, y)) {
                this.advanceFruitMission(x, y);
                if (this.isSpecialFruit(x, y)) {
                    newList.add(new SpaceCoors(x, y));
                } else {
                    this.checkerScoreDestructionSpecial.add(x, y, 5);
                    this.score += 5; // Score adding !
                }
            }
        }

        if (this.isHostage(x, y)) {
            this.attackHostage(x, y);
        } else {
            // Collect a basket even if no fruit is found there
            this.catchBasket(x, y);
            this.tryToDecreaseDirectlyAdjacentStuff(x, y);
        }
    }

    /*
    Tells from one space which fruits are supposed to fall and add them to "checkerFallingElements"
    Tells also where new fruits should be spawned !
     */
    private void handleNewFallingFruitsAndPotentiallySpawn(int xFallInto, int yFallInto) {
        SpaceCoors coorsToFall, coorsHead;
        coorsHead = new SpaceCoors(xFallInto, yFallInto); // Note : the head is itself if there is no fruit above it
        boolean keepClimbing = true;
        // Climb up to declare all falling spaces from this one
        while (keepClimbing) {
            coorsToFall = this.getCoorsFallableJustAbove(coorsHead.x, coorsHead.y);
            while (coorsToFall != null && this.checkerFallingElements.add(coorsToFall.x, coorsToFall.y)) {
                coorsHead = new SpaceCoors(coorsToFall.x, coorsToFall.y);
                coorsToFall = this.getCoorsFallableJustAbove(coorsToFall.x, coorsToFall.y);
            }
            keepClimbing = false;
        }

        // Spawn
        if (this.shouldSpawnFruit(coorsHead.x, coorsHead.y)) {
            this.spawnRandomFruit(coorsHead.x, coorsHead.y);
        }
    }

    /*
    Check method !
    When a fall animation is ended.
    Chained with a stable check method or another fall animation.
    Also where arrayFruit is updated.
     */
    public void triggerUnstableCheck() {
        SpaceCoors belowCoors;
        List<SpaceCoors> fallingFruitsNewCoors = new ArrayList<>();

        // Shift all falling fruits "below" in futur array
        int xFall, yFall, xDest, yDest, xSource, ySource;
        boolean newFall;

        for (SpaceCoors coorsFall : this.checkerFallingElements.getList()) {
            xFall = coorsFall.x;
            yFall = coorsFall.y;
            belowCoors = this.getCoorsFallableJustBelow(xFall, yFall);
            this.arrayFutureField[belowCoors.y][belowCoors.x] = this.arrayField[yFall][xFall];
            this.arrayField[yFall][xFall] = new EmptySpace();
            fallingFruitsNewCoors.add(new SpaceCoors(belowCoors.x, belowCoors.y)); // Note : nomination "coors" refers as a list, but "new coors" refers to new coordinates ! Beware !
        }
        for (SpaceCoors coorsFall : this.checkerDropUpperLeftHere.getList()) {
            xDest = coorsFall.x;
            yDest = coorsFall.y;
            xSource = xDest-1;
            ySource = yDest-1;
            this.arrayFutureField[yDest][xDest] = this.arrayField[ySource][xSource];
            this.arrayField[ySource][xSource] = new EmptySpace();
            fallingFruitsNewCoors.add(new SpaceCoors(xDest, yDest));
        }
        for (SpaceCoors coorsFall : this.checkerDropUpperRightHere.getList()) {
            xDest = coorsFall.x;
            yDest = coorsFall.y;
            xSource = xDest+1;
            ySource = yDest-1;
            this.arrayFutureField[yDest][xDest] = this.arrayField[ySource][xSource];
            this.arrayField[ySource][xSource] = new EmptySpace();
            fallingFruitsNewCoors.add(new SpaceCoors(xDest, yDest));
        }

        this.checkerFallingElements.clear();
        this.checkerDropUpperLeftHere.clear();
        this.checkerDropUpperRightHere.clear();
        // TODO A few remaining unwanted diagonal squeezes

        // Fruits in the spawning part
        int xSpawn, ySpawn;
        for (SpaceCoors coorsSpawn : this.checkerSpawnFruits.getList()) {
            xSpawn = coorsSpawn.x;
            ySpawn = coorsSpawn.y;
            this.arrayFutureField[ySpawn][xSpawn] = this.checkerSpawnFruits.get(xSpawn, ySpawn);
            fallingFruitsNewCoors.add(new SpaceCoors(xSpawn, ySpawn)); // Note : apparently it used to work fine without it when using int arrays...
        }
        this.checkerSpawnFruits.clear();

        // Change future array to array
        int xNew, yNew;
        for (SpaceCoors coorsNew : fallingFruitsNewCoors) {
            xNew = coorsNew.x;
            yNew = coorsNew.y;
            this.arrayField[yNew][xNew] = this.arrayFutureField[yNew][xNew];
        }

        // Now, check again falling elements.
        this.fullyCheckFallingElements();
        newFall = !this.checkerDropUpperRightHere.getList().isEmpty() || !this.checkerDropUpperLeftHere.getList().isEmpty();


        if (!newFall) {
            // (renewal fall) ... instead of here
            for (SpaceCoors check : this.checkerFallingElements.getList()) { // If not for this check it would be launched again and again
                if (!(this.arrayField[check.y][check.x] instanceof EmptySpace)) {
                    newFall = true ; break;
                }
            }
        }
        if (!newFall) {
            // Note that spaces with spawning may have been updated. Typical example : delayed lock on a spawning space.
            newFall = !this.checkerSpawnFruits.getList().isEmpty();
        }

        if (newFall) {
            this.causeDutiesToDropAtRandom();
            this.gth.startFall();
        } else {
            this.performStableCheck(false);
        }
    }

    private void fullyCheckFallingElements() {
        int xCheck, yCheck;
        List<SpaceCoors> emptySpaces = new ArrayList<>();
        precheckEmptySpaces();
        for (yCheck = 0 ; yCheck < Constants.FIELD_YLENGTH ; yCheck++) {
            for (xCheck = 0 ; xCheck < Constants.FIELD_XLENGTH ; xCheck++) {
                if (this.isEmpty(xCheck, yCheck)) { // Instead of simple checkerToBeEmptiedSpaces so that stable check after unstable check takes it into account
                    emptySpaces.add(new SpaceCoors(xCheck, yCheck));
                } else if (this.arrayField[yCheck][xCheck].canFall()) {
                    this.blockDiagonalSqueezeHereAndDownward(xCheck, yCheck);
                }
                if (this.shouldSpawnFruit(xCheck, yCheck)) {
                    this.blockDiagonalSqueezeHereAndDownward(xCheck, yCheck);
                }// Note : should NOT be added in the else because any spawning space needs to have a downward blocking.
            }
        }
        handleAboveEmptySpaces(emptySpaces);
    }
    // TODO : last (?) bug to correct for diagonal squeezes : if you destroy 3 solid blocks from the left, fruits start falling from the right but ultimately fall from the left
    
    private void precheckEmptySpaces() {
        checkerBlockDiagonalSqueeze.clear();
    }

    private void handleAboveEmptySpaces(List<SpaceCoors> emptySpaces) {
        for (SpaceCoors coors : emptySpaces) {
            this.handleNewFallingFruitsAndPotentiallySpawn(coors.x, coors.y);
        }
        for (SpaceCoors coors : emptySpaces) {
            if (coors.x > 0) {
                this.handleDiagonallySqueezingFruits(coors.x, coors.y, coors.x-1, this.checkerDropUpperLeftHere);
            }
        }
        for (SpaceCoors coors : emptySpaces) {
            if (coors.x < Constants.FIELD_XLENGTH-1 && !this.checkerDropUpperLeftHere.get(coors.x, coors.y) && (coors.x == Constants.FIELD_XLENGTH-2 || !this.checkerDropUpperLeftHere.get(coors.x+2, coors.y))) {
                this.handleDiagonallySqueezingFruits(coors.x, coors.y, coors.x+1, this.checkerDropUpperRightHere);
            }
        }
    }

    private void handleDiagonallySqueezingFruits(int xFall, int yFall, int xSource, Checker checkerDropUpperHere) {
        int ySource = yFall-1;
       // if (ySource >= 0 && this.arrayField[ySource][xSource].canFall() && !this.checkerFallingElements.get(xSource, ySource)  && !this.checkerFallingElements.get(xFall, yFall-1)
        if (ySource >= 0 
                && this.arrayField[ySource][xSource].canFall() && !this.checkerFallingElements.get(xSource, ySource)
                && (!this.checkerFallingElements.get(xFall, yFall-1) || this.isEmpty(xFall, yFall-1)) // "emptyspace check" to allow elements to form diagonal walls
                && (!checkerDropUpperHere.get(xFall, yFall-1))
                && (!(this.isEmpty(xSource, ySource))) // To prevent infinite loops
                && !checkerBlockDiagonalSqueeze.get(xFall, yFall-1)
        ) {
            checkerDropUpperHere.add(xFall, yFall);
            this.blockDiagonalSqueezeHereAndDownward(xFall, yFall);
        }
    }

    // Blocks diagonal squeeze until a solid block is met
    private void blockDiagonalSqueezeHereAndDownward(int x, int y) {
        int yy = y;
        int xx = x;
        SpaceCoors coors;
        
        while (this.shouldBeFallable(xx, yy) && this.checkerBlockDiagonalSqueeze.add(xx, yy)) {
            coors = this.getCoorsFallableJustBelow(xx, yy);
            if (coors == null) {
                return;
            }
            xx = coors.x;
            yy = coors.y;
        } // If we had only a (this.arrayField[yy][xx].canFall()), a "block that is about to be destroyed" would not count.
    }

    // Equals true IF : x, y is either an item to fall OR a block that is gonna be destroyed.
    private boolean shouldBeFallable(int x, int y) {
        return this.arrayField[y][x].canFall() || this.isEmpty(x, y);
    }

    private void testAndAlertAboutAlignedFruitsAroundSpace(int xFruit, int yFruit) {
        if (xFruit >= 1) {
            if (xFruit >= 2) {
                testAndAlertAboutAlignedFruits(xFruit, yFruit, xFruit-1, yFruit, xFruit-2, yFruit);
            }
            if (xFruit <= Constants.FIELD_XLENGTH-2) {
                testAndAlertAboutAlignedFruits(xFruit-1, yFruit, xFruit, yFruit, xFruit+1, yFruit);
                if (xFruit <= Constants.FIELD_XLENGTH-3) {
                    testAndAlertAboutAlignedFruits(xFruit, yFruit, xFruit+1, yFruit, xFruit+2, yFruit);
                }
            }
        } else {
            testAndAlertAboutAlignedFruits(0, yFruit, 1, yFruit, 2, yFruit);
        }
        if (yFruit >= 1) {
            if (yFruit >= 2) {
                testAndAlertAboutAlignedFruits(xFruit, yFruit, xFruit, yFruit-1, xFruit, yFruit-2);
            }
            if (yFruit <= Constants.FIELD_YLENGTH-2) {
                testAndAlertAboutAlignedFruits(xFruit, yFruit-1, xFruit, yFruit, xFruit, yFruit+1);
                if (yFruit <= Constants.FIELD_YLENGTH-3) {
                    testAndAlertAboutAlignedFruits(xFruit, yFruit, xFruit, yFruit+1, xFruit, yFruit+2);
                }
            }
        } else {
            testAndAlertAboutAlignedFruits(xFruit, 0, xFruit, 1, xFruit, 2);
        }
    }

    /*
    Returns the coordinates of the fruit that is supposed to fall into this space (usually the one right above, but there may be teleporters)
    Or return null if the space is either non existent or empty
    SOMETHING IS SUPPOSED TO FALL (and emptiness can fall)
     */
    private SpaceCoors getCoorsFallableJustAbove(int x, int y) {
        SpaceCoors supposedSource = this.arrayTeleporterCorrespondingEntrance[y][x];
        if (supposedSource != null) {
            if (!this.shouldBeFallable(supposedSource.x,supposedSource.y)) {
                return null;
            }
            return supposedSource;
        }
        if (y == 0) {
            return null;
        }
        if (!this.shouldBeFallable(x, y-1)) {
            return null;
        }
        return new SpaceCoors(x, y-1);
    }

    /*
    Coors of the space immediately below, if it can fall...
     */
    private SpaceCoors getCoorsFallableJustBelow(int x, int y) {
        SpaceCoors supposedDest = this.arrayTeleporterCorrespondingExit[y][x];
        if (supposedDest != null) {
            if (!this.shouldBeFallable(supposedDest.x, supposedDest.y)) {
                return null;
            }
            return supposedDest;
        }
        if (y == Constants.FIELD_YLENGTH-1) {
            return null;
        }
        if (!this.shouldBeFallable(x, y+1)) {
            return null;
        }
        return new SpaceCoors(x, y+1);
    }

    // This is where everything is done before a new move should occur.
    // After the last stable check !
    private void endBeforeNewPlayerMove() {
        // Shuffle new WaitingNutData, add'em to the main list, then decrease all elements of the main list by 1 (some will come to 0)
        Collections.shuffle(this.listWaitingNutDataThisTime);
        // https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#shuffle-java.util.List-
        for (WaitingNutData wnd : this.listWaitingNutDataThisTime) {
            this.listWaitingNutData.add(wnd.copy());
        }
        this.listWaitingNutDataThisTime.clear();
        for (WaitingNutData wnd : this.listWaitingNutData) {
            wnd.decrease();
        }
    }

    // ------------
    // Goals and missions
    // Warning : order matters A LOT !
    // Note : "Control mission" is the name for whenever we want to advance missions

    public void controlMissionsWithSwap(GameEnums.WHICH_SWAP swap) {
        switch (swap) {
            case FIRE_ELECTRIC:
                if (!this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_FIRE, 1)) {
                    if (!this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                    }
                    if (!this.advanceMission(GameEnums.ORDER_KIND.FIRE_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.FIRE, 1);
                    }
                }
                break;
            case OMEGA_ELECTRIC:
                if (!this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_OMEGA, 1)) {
                    if (!this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                    }
                    if (!this.advanceMission(GameEnums.ORDER_KIND.OMEGA_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 1);
                    }
                }
                break;
            case OMEGA_FIRE:
                if (!this.advanceMission(GameEnums.ORDER_KIND.OMEGA_FIRE, 1)) {
                    if (!this.advanceMission(GameEnums.ORDER_KIND.FIRE_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.FIRE, 1);
                    }
                    if (!this.advanceMission(GameEnums.ORDER_KIND.OMEGA_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 1);
                    }
                }
                break;
            case FIRE_FIRE:
                if ((
                        !this.advanceMission(GameEnums.ORDER_KIND.FIRE_FIRE, 1) &&
                                !this.advanceMission(GameEnums.ORDER_KIND.FIRE_WILD, 2)
                )) {
                    this.advanceMission(GameEnums.ORDER_KIND.FIRE, 2);
                }
                break;

            case ELECTRIC_ELECTRIC:
                if ((
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_LIGHTNING, 1) ||
                                this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_WILD, 2)
                )) {
                    this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 2);
                }
                break;
            case OMEGA_OMEGA:
                if (!(
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA_OMEGA, 1) ||
                                this.advanceMission(GameEnums.ORDER_KIND.OMEGA_WILD, 2)
                )) {
                    this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 2);
                }
                break;

        }
    }

    public void tryToDecreaseDirectlyAdjacentStuff(int x, int y) {
        if (this.arrayField[y][x] instanceof BreakableBlock) {
            BreakableBlock bb = (BreakableBlock) this.arrayField[y][x];
            bb.downgrade();
            if (bb.getCount() == 0) {
                this.checkerToBeEmptiedSpaces.add(x, y);
            }
        } else if (this.arrayField[y][x] instanceof StickyBomb) {
            StickyBomb ss = (StickyBomb) this.arrayField[y][x];
            ss.downgrade();
            if (ss.getCount() == 0) {
                int fruitId = ss.getContainedFruitId();
                if (fruitId != Constants.NOT_A_FRUIT) {
                    this.numberRemainingBombsThisColour[fruitId]--;
                    if (this.numberRemainingBombsThisColour[fruitId] == 0) {
                        this.listIdFruitsToRemove.add(fruitId);
                    }
                }
                this.checkerToBeEmptiedSpaces.add(x, y);
            }
        } else if (this.arrayField[y][x] instanceof Nut) {
            if (!this.checkerToBeEmptiedSpaces.get(x, y)) { // Don't affect it if already in nut drop space.
                Nut nn = (Nut) this.arrayField[y][x];
                if (nn.getCount() > 0) {
                    nn.downgrade();
                    this.nutsHealthCount--;
                }
                if (nn.getCount() == 0) {
                    this.checkerToBeEmptiedSpaces.add(x, y);
                }
            }
        }
    }

    public void tryToDecreaseDirectlyAdjacentStuffAround(int x, int y) {
        if (x > 0) {
            this.tryToDecreaseDirectlyAdjacentStuff(x-1, y);
        }
        if (y > 0) {
            this.tryToDecreaseDirectlyAdjacentStuff(x, y-1);
        }
        if (x < Constants.FIELD_XLENGTH-1) {
            this.tryToDecreaseDirectlyAdjacentStuff(x+1, y);
        }
        if (y < Constants.FIELD_YLENGTH-1) {
            this.tryToDecreaseDirectlyAdjacentStuff(x, y+1);
        }
    }

    public boolean advanceMission(GameEnums.ORDER_KIND kind, int amount) {
        for (int i = 0 ; i < this.numberOfMissions; i++) {
            if (this.kindsOfMissions[i] == kind) {
                if (this.amountsMission[i] <= 0) {
                    return false;
                }
                this.amountsMission[i] -= amount;
                return true;
            }
        }
        return false;
    }

    private void advanceFruitMission(int x, int y) {
        this.advanceFruitMissionAux(this.arrayField[y][x].getIdFruit()); // TODO if a fruit is collected as a "special fruit", it should be collected as a "coloured fruit" but not as an "any fruit". Here, it's an any fruit.
    }

    private void advanceFruitMissionAux(int index) {
        for (int i = 0 ; i < this.numberOfMissions; i++) {
            if (this.kindsOfMissions[i].getFruitId() == index) {
                this.amountsMission[i]--;
                if (this.amountsMission[i] == -1) {
                    for (int j = 0 ; j < this.numberOfMissions ; j++) {
                        if (this.kindsOfMissions[j].getFruitId() == Constants.ANY_FRUIT && this.amountsMission[j] > 0) {
                            this.amountsMission[j]--;
                        }
                    }
                    this.amountsMission[i] = 0;
                }
                return; // Found fruit of the colour of a mission (that may be over) : leave here.
            }
        }
        // Didn't find, but we have this
        for (int j = 0 ; j < this.numberOfMissions ; j++) {
            if (this.kindsOfMissions[j].getFruitId() == Constants.ANY_FRUIT  && this.amountsMission[j] > 0) {
                this.amountsMission[j]--;
            }
        }
    }

    public void catchBasket(int x, int y) {
        if (this.arrayBaskets[y][x] > 0) {
            if (this.arrayField[y][x].canFall()) {
                this.arrayBaskets[y][x]--;
                this.basketsCount--;
            }
        }

    }
    
    // ------------
    // Getters for drawing

    public boolean isNotFallingFruit(int x, int y) {
        return (!this.checkerFallingElements.get(x, y));
    }

    public boolean isNotDestroyedBeforeFall(int x, int y) {
        return (!this.checkerToBeEmptiedSpaces.get(x, y));
    }

    public SpaceCoors getDestination(int x, int y) {
        return this.arrayTeleporterCorrespondingExit[y][x];
    }

    // List of (x, y) spaces with fruits that are falling from (x, y) to the next space (usually x, y+1)
    public List<SpaceCoors> getFallingEltsCoors() {
        return this.checkerFallingElements.getList();
    }

    public SpaceFiller spawn(int x, int y) {
        return (this.checkerSpawnFruits.get(x, y));
    }

    // List of (x, y) spaces with fruits that are spawning into
    public List<SpaceCoors> getSpawningFruitsCoors() {
        return this.checkerSpawnFruits.getList();
    }

    public int getSpriteIdFromFieldIndex(int indexFieldFruit) { 
        return gameIndexToImageIndex[indexFieldFruit];
    }

    public GameEnums.FRUITS_POWER getFruitPowerFromCoors(int x, int y) {
        return this.arrayField[y][x].getPower();
    }

    public int getScore() {
        return this.score;
    }

    public GameEnums.GOAL_KIND getGoalKind() {
        return this.goalKind;
    }

    public int getNumberofMissions() {
        return this.numberOfMissions;
    }

    public int[] getAmountsOrder() {
        return this.amountsMission;
    }
    public GameEnums.ORDER_KIND[] getKindsOfOrder() {
        return this.kindsOfMissions;
    }

    public int getBasketsCount() {
           return this.basketsCount;
    }

    public int getNutsHealthCount() {
        return this.nutsHealthCount;
    }

    public String getTitleAndInfos() {
        return this.title + " " + "(" + this.numberOfFruitKinds + "c)";
    }

    public int scoreFallSpace(int x, int y) {
        return (this.checkerScoreDestructionFall.get(x, y));
    }

    public List<SpaceCoors> getContributingSpacesScoreFall() {
        return this.checkerScoreDestructionFall.getList();
    }

    public int scoreDestructionSpecialSpace(int x, int y) {
        return (this.checkerScoreDestructionSpecial.get(x, y));
    }

    public List<SpaceCoors> getContributingSpacesScoreDestructionSpecial() {
        return this.checkerScoreDestructionSpecial.getList();
    }

    public boolean hasOmegaSphere(int x, int y) {
        return (this.arrayField[y][x].getPower() == GameEnums.FRUITS_POWER.OMEGA_SPHERE);
    }

    public int getBaskets(int x, int y) {
        return (this.arrayBaskets[y][x]);
    }

    public List<SpaceCoors> getCoorsElementsGettingFromUpperRight() {
        return this.checkerDropUpperRightHere.getList();
    }

    public List<SpaceCoors> getCoorsElementsGettingFromUpperLeft() {
        return this.checkerDropUpperLeftHere.getList();
    }

    public int getElapsedMoves() {return this.numberElapsedMoves;}

    public List<SpaceCoors> getCoorsForNutDrops() {
        return this.checkerNutDrops.getList();
    }

    public List<WaitingNutData> getListWaitingNutData() {
        return this.listWaitingNutData;
    }

    // ------------
    // Getter for input
    public boolean isClickable(int x, int y) {
        return this.arrayField[y][x].canBeSwapped();
    }

    // ------------
    // Getters for animations
    public List<SpaceCoors> getEmptiedSpacesCoors() {
        return checkerToBeEmptiedSpaces.getList();
    }

    public List<SpaceCoors> getListToBeActivatedSpecialFruits() {
        return listToBeActivatedSpecialFruits;
    }

    public int getXCenterAnimation() { return this.xSwapCenter; }
    public int getYCenterAnimation() { return this.ySwapCenter; }
    public GameEnums.WHICH_SWAP getLastSwap() { return this.lastSwap; }

    public List<SpaceCoors> getCoorsTargetOmegaSphere(int colour) {
        return this.omegaTargetsCoorsByColour[colour];
    }
    public SpaceCoors getCoorsSourceOmegaSphere(int colour) {
        return this.omegaSourceCoorsByColour[colour];
    }

    public boolean isNotInDiagonalSqueeze(int x, int y) {
        return (y == Constants.FIELD_YLENGTH-1 || (

                (x == 0 || !this.checkerDropUpperRightHere.get(x-1, y+1)) &&
                        (x == Constants.FIELD_XLENGTH-1 || !this.checkerDropUpperLeftHere.get(x+1, y+1))
        ));
        // TODO rather than make a check on destinations, make one on starts ? (whole diagonal squeeze implementations have already been thought, though...)
    }

    public boolean hasDisappearingNut(int x, int y) {
        return ((this.arrayField[y][x] instanceof Nut) && this.checkerNutDrops.get(x, y) > 0);
    }
}


// TODO philosophie paniers et blocs cassables : ce qui suit est acceptable ?
// Un breakable peut être dégradé plusieurs fois par des alignements.
// Dans un même check stable, un même breakable peut être détruit plusieurs fois
// Block breakable et à côté d'un fruit spécial nouvellement créé : on dégrade le breakable.
// Gestion breakables / paniers : quand la dernière couche d'un rocher est détruite, on ne collecte pas les paniers avant le stable check suivant.
// C'est ce qui se produit quand on fait un échange près d'un rocher à 1 qui déclenche le fruit spécial et celui-ci touche la case à panier visée. Pas de panier collecté.

// TODO : collecte panier dans le cas swap oméga + X et dans les cas feu + éclair (et les 2 autres swap.... ?)
// De plus, quand on active une sphère oméga, la case de destination de la sphère oméga ne collecte pas le panier...

// TODO Diagonal squeeze et chute :
// On doit pouvoir diminuer le check des cases vides à chaque check instable en construisant une liste des "cases potentiellement vides" ; idem pour les cases potentiellement squeezables. Mais attention, une case potentiellement squezzable n'a rien à voir avec une case qui squeeze lors d'une vérification, stable ou pas !

// TODO création de fruits spéciaux :
// Aucun fruit n'est créé si dans l'ensemble il y a au moins 1 fruit spécial activé
// Si une ligne de 3 ou 4 (H ou V) intersecte deux lignes de 5, fruit créé au milieu de la ligne 5
// Problème potentiel : intersection de deux lignes de 5
// Philosophie : on considère quand un fruit spécial est créé qu'il compte pour une mission de fruit, qu'il détruit les alentours... car il y a destruction. Mais doit-on considérer la même chose pour quand un fruit spécial est détruit ? (bon, actuellement tout compte. Générosité...)
// Par contre, il faut changer ce résultat de "collecter un fruit spécial et un fruit d'une mission any fruit"

// TODO deux questions qui restent sans réponse :
// Que faire quand on a aucun coup possible ? Swap libre, ou bien brasser les fruits ?
// Combo feu + feu ?

// TODO sur les noix :
// Lorsqu'elles tombent dans un "vortex 1" : elles seront de nouveau disponible au prochain coup (elles disparaîssent lors des échangens engendrées par le coup N, elles réapparaissent après le coup N+P, ici P=1)
// Bug que j'ai cru voir : une noix est capturée par un panier, puis dans le même coup une sphère oméga est détruite, et la noix réapparaît ???

// TODO sur les bombes collantes :
// Lorsque je gérerai les missions réussies ou ratées, il faudra prendre en compte que certaines missions ne pourront plus être réussies
// Philosophie du nom "bombe collante" pour l'insant ça désigne aussi bien un preneur d'otage d'une case vide qu'un objet un peu spécial dont une fois qu'on retire tout, tout disparaît.
// Philosophie des locks : si on retire une couleur, ça ne retire pas le fruit dans le lock ! Eh ouais, c'est dur...

// TODO philosophie animations :
// Pas de rotation pour les "nuts" pour l'instant, mais ça pourra changer !

// TODO physique :
// Dans chaque cas (collecte fruit normal, création/collecte fruit spécial, fruit issu d'un combo, etc...)
// traiter les évènements : panier, collecte fruit (lesquels ?), blast des trucs adjacents, libération des otages. Il faudrait une fonction pour gérer tout ça.