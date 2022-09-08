package com.example.fruitfall;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.fruitfall.level.LevelData;
import com.example.fruitfall.spaces.BreakableBlock;
import com.example.fruitfall.spaces.EmptySpace;
import com.example.fruitfall.spaces.Fruit;
import com.example.fruitfall.spaces.DelayedLock;
import com.example.fruitfall.spaces.OmegaSphere;
import com.example.fruitfall.spaces.SpaceFiller;
import com.example.fruitfall.spaces.VoidSpace;

import java.util.ArrayList;
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
    private final IntChecker checkerSpawnFruits = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, Constants.NOT_A_FRUIT);
    private final IntChecker checkerHorizAlignment = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);
    private final IntChecker checkerVertAlignment = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);
    public static int[] gameIndexToImageIndex = new int[Constants.RESOURCES_NUMBER_FRUITS];
    private List<SpaceCoors> listToBeActivatedSpecialFruits = new ArrayList<>();
    private final List<SpaceCoors> listToBeActivatedOmegaSpheres = new ArrayList<>();
    private final Checker checkerDropUpperRightHere = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final Checker checkerDropUpperLeftHere = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private final Checker checkerBlockDiagonalSqueeze = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH); // During a "empty spaces handling", any space that is full OR immediately below a sucking space must not be able to vaccuum an element diagonally, neither the spaces below it until the next wall.
    private final List<SpaceCoors> listDelayedLocks = new ArrayList<>();
    // Note : convention : before a stable check, only one source / destination per colour.
    private final SpaceCoors[]  omegaSourceCoorsByColour = new SpaceCoors[Constants.RESOURCES_NUMBER_FRUITS];
    private final List<SpaceCoors>[] omegaTargetsCoorsByColour = new List[Constants.RESOURCES_NUMBER_FRUITS];

    private String title;
    private int numberOfFruitKinds;
    private int score;
    private int thisMoveComboCoefficient;
    private int thisMoveFruitsDestroyedByFall;
    private int collectedFruits;
    private int phaseCount;
    private int countRemainingLocks;

    private int xSwapCenter, ySwapCenter;
    private int xSwapSide, ySwapSide;
    private int idFruitOmegaDestruction = Constants.NOT_A_FRUIT;
    private int xSourceOmegaDestruction = Constants.NOT_A_SPACE_COOR;
    private int ySourceOmegaDestruction = Constants.NOT_A_SPACE_COOR;
    private GameEnums.WHICH_SWAP lastSwap = GameEnums.WHICH_SWAP.NONE;

    private int[] amountsMission = new int[Constants.MAX_MISSIONS];
    private int numberOfMissions;
    private GameEnums.ORDER_KIND[] kindsOfMissions = new GameEnums.ORDER_KIND[Constants.MAX_MISSIONS];

    private final int[][] arrayBaskets = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private int basketsCount;

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
        return (this.getIdFruit(x, y) == this.getIdFruit(x2, y2) && this.getIdFruit(x, y) == this.getIdFruit(x3, y3));
    }

    public boolean hasFruit(int x, int y) {
        return (this.arrayField[y][x] instanceof Fruit);
    }

    public boolean isASpace(int x, int y) {
        return (this.arrayField[y][x].isASpace());
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

    public boolean shouldSpawnFruit(int x, int y) {
        return this.arrayShouldFruitsBeSpawned[y][x];
    }

    private void spawnFruit(int x, int y) {
        this.checkerSpawnFruits.add(x, y, new Random().nextInt(this.numberOfFruitKinds));
    }

    /*
    Returns the coordinates of the fruit that is supposed to fall into this space (usually the one right above, but there may be teleporters)
    Or return null if the space is either non existent or empty
    SOMETHING IS SUPPOSED TO FALL (and emptiness can fall)
     */
    private SpaceCoors getCoorsFallableJustAbove(int x, int y) {
        SpaceCoors supposedSource = this.arrayTeleporterCorrespondingEntrance[y][x];
        if (supposedSource != null) {
            if (!this.arrayField[supposedSource.y][supposedSource.x].canFall()) {
                return null;
            }
            return supposedSource;
        }
        if (y == 0) {
            return null;
        }
        if (!this.arrayField[y-1][x].canFall()) {
            return null;
        }
        return new SpaceCoors(x, y-1);
    }

    /*
    SOMETHING IS SUPPOSED TO FALL
     */
    private SpaceCoors getCoorsFallableJustBelow(int x, int y) {
        SpaceCoors supposedDest = this.arrayTeleporterCorrespondingExit[y][x];
        if (supposedDest != null) {
            if (!this.arrayField[supposedDest.y][supposedDest.x].canFall()) {
                return null;
            }
            return supposedDest;
        }
        if (y == Constants.FIELD_YLENGTH-1) {
            return null;
        }
        if (!this.arrayField[y+1][x].canFall()) {
            return null;
        }
        return new SpaceCoors(x, y+1);
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
        checkerDropUpperLeftHere.clear();
        checkerBlockDiagonalSqueeze.clear();
        checkerDropUpperRightHere.clear();

        this.score = 0;
        this.collectedFruits = 0;
        this.thisMoveComboCoefficient = 1;
        this.thisMoveFruitsDestroyedByFall = 0;
        this.phaseCount = 0;
        this.countRemainingLocks = 0;

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
        for (i = 0 ; i < this.numberOfFruitKinds ; i++) {
            this.omegaTargetsCoorsByColour[i] = new ArrayList<>();
            this.omegaSourceCoorsByColour[i] = null;
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
        GameEnums.SPACE_DATA data;
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                shouldGainSpawn = shouldHaveSpawn(ld, x, y);
                data = ld.getData(x, y);
                if (data == GameEnums.SPACE_DATA.FRUIT) {
                    // Generate fruit
                    this.arrayField[y][x] = new Fruit(rand.nextInt(this.numberOfFruitKinds));
                    if (x >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x-1, y, x-2, y);
                    }
                    if (y >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x, y-1, x, y-2);
                    }
                } else if (data == GameEnums.SPACE_DATA.DELAYED_LOCK) {
                    this.arrayField[y][x] = new DelayedLock(ld.getLockDuration(x, y));
                    this.countRemainingLocks++;
                    this.listDelayedLocks.add(new SpaceCoors(x, y));
                } else if (data == GameEnums.SPACE_DATA.BREAKABLE_BLOCK) {
                    this.arrayField[y][x] = new BreakableBlock(ld.getBreakableBlockLevel(x, y));
                    // Note : the list of coors may be actually useless, unlike locks which are discounted each turn
                } else if (data == GameEnums.SPACE_DATA.EMPTY) {
                    this.arrayField[y][x] = new EmptySpace();
                } else {
                    // Not a space able to handle fruits ; still need to initialize arrays.
                    this.arrayField[y][x] = new VoidSpace();
                    this.arrayShouldFruitsBeSpawned[y][x] = false;
                    shouldGainSpawn = false;
                }
                this.arrayShouldFruitsBeSpawned[y][x] = shouldGainSpawn;
            }
        }

        this.goalKind = ld.getGoalKind();
        this.basketsCount = 0;
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
            }
            // So... what's next with new fruits ?
            for (SpaceCoors coors : formerListGetAlignedFruit) {
                testAndAlertAboutAlignedFruitsAroundSpace(coors.x, coors.y);
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

    // ----------------------------
    // Input

    /*
        Important : here are how thing works :
        // Input method (GH) -> start method; data of game not affected
        // start method (GTH) -> change state, initialize frames that will be increased by step
        // step method (GTH) -> increases one variable (unless normal state). Once counter has reached threshold, change state :  trigger method
        // trigger method (GH) -> stop method ; data of game affected ; start method or end methods
        // end methods (GTH) -> stop methods ; return to normal state
        // stop method (GTH) -> clearing things
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void triggerAfterOmegaStasis() {
        this.triggerAfterDestructionStasis();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void triggerAfterDestructionStasis() {
        // Oh, and also restart score arrays :
        this.checkerScoreDestructionSpecial.clear();
        this.checkerScoreDestructionFall.clear();

        // Omega check arrays (since it's for animations) :
        for (int i = 0 ; i < this.numberOfFruitKinds ; i++ ) {
            this.omegaSourceCoorsByColour[i] = null;
            this.omegaTargetsCoorsByColour[i].clear();
        }

        // Quite simplistic :
        // If there are no special fruits / special objects (omega) waiting for being destroyed, move on with falls.
        // Otherwise, destroy, detect new special fruits, restart a stasis
        boolean goForDestructionAgain = this.activateSpecialFruits();
        if (goForDestructionAgain) {
            this.performDestruction(false);
        } else {
            this.gth.startFall();
        }
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
                    this.arrayField[yy][xx] = new Fruit(this.arrayField[yy][xx].getIdFruit(), usePower1 ? power1 : power2);
                    this.listToBeActivatedSpecialFruits.add(new SpaceCoors(xx, yy));
                    this.checkerToBeEmptiedSpaces.add(xx, yy);
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
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.NONE || this.lastSwap == GameEnums.WHICH_SWAP.FRUIT_FRUIT) {
            this.alignmentDestructionCheck();
            isAlignment = true;
        }

        if (this.checkerToBeEmptiedSpaces.getList().isEmpty()) {
            // Nothing new destroyed : move on.
            this.thisMoveComboCoefficient = 1;
            this.thisMoveFruitsDestroyedByFall = 0;
            this.triggerNextPhaseAfterStableCheck();
        } else {
            this.performDestruction(isAlignment);
        }
    }

    // Destruction time !
    private void performDestruction(boolean isAlignment) {
        this.fullyCheckFallingElementsInStableCheck();
        this.gth.startDestruction(isAlignment);
    }

    private void alignmentDestructionCheck() {
        // Check all horizontal and vertical fruits, and set them for destruction
        this.checkerHorizAlignment.clear();
        this.checkerVertAlignment.clear();
        // Alignment check
        int x, y, currentFruit, xAft, yAft, xx, yy; // x After, y After last fruit
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0 ; x < Constants.FIELD_XLENGTH ; x++) {
                if (this.hasFruit(x, y)) {
                    currentFruit = this.getIdFruit(x, y);
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

        // Yeehaw, handling special fruits !
        int numberAlign, numberAlignTransversal;
        int xInter, yInter;
        for (SpaceCoors coors : this.checkerHorizAlignment.getList()) {
            x = coors.x;
            y = coors.y;
            numberAlign = this.checkerHorizAlignment.get(x, y);
            if (numberAlign == 3 || numberAlign == 4) {
                xInter = -1;
                for (xx = x ; xx < x + numberAlign ; xx++) {
                    if (this.checkerVertAlignment.get(xx, y) > 0) {
                        yy = y;
                        numberAlignTransversal = 1;
                        while (this.checkerVertAlignment.get(xx,yy) == 1) {
                            yy--;
                            numberAlignTransversal++;
                        } // numberAlignTransversal = number of fruits identical going upwards starting with yy included
                        yy = y+1;
                        while (yy < Constants.FIELD_YLENGTH && this.checkerVertAlignment.get(xx, yy) == 1) {
                            yy++;
                            numberAlignTransversal++;
                        }
                        if (numberAlignTransversal == 3 || numberAlignTransversal == 4) {
                            // TODO Changer ce comportement (pas de fruit créé si à l'intersection)... ou faire avec ?
                            // TODO Il faudra voir ce qu'on veut en cas de test 3x3 fruits identiques
                            // TODO sorte de bug quand on a 3 fruits dans un sens et 4 dans un autre, puisqu'on a 2 fruits spéciaux créés, on ne veut pas ça...
                            if (this.arrayField[y][xx].getPower() == GameEnums.FRUITS_POWER.NONE) {
                                this.createSpecialFruit(xx, y, new Fruit(this.getIdFruit(x, y), GameEnums.FRUITS_POWER.FIRE), 3);
                            }
                        }
                        if (numberAlignTransversal >= 5) {
                            xInter = xx;
                        }
                    }
                }
                if (xInter == -1 && numberAlign == 4) {
                    int xDefault = x+1;
                    if (this.lastSwap != GameEnums.WHICH_SWAP.NONE) {
                        xDefault = this.xSwapCenter;
                    }
                    this.tryToCreateSpecialFruitInHorizontalAlignment(x, y, numberAlign, xDefault, new Fruit(this.getIdFruit(x, y), GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING), 2);
                }
            }
            if (numberAlign >= 5) {
                this.createSpecialFruit(x+(numberAlign-1)/2, y, new OmegaSphere(), 5);
            }
        }
        for (SpaceCoors coors : this.checkerVertAlignment.getList()) {
            x = coors.x;
            y = coors.y;
            numberAlign = this.checkerVertAlignment.get(x, y);
            if (numberAlign == 4) {
                // Only check if there is nothing done horizontally - other checks already done.
                yInter = -1;
                for (xx = x ; xx < x + numberAlign ; xx++) {
                    if (this.checkerVertAlignment.get(xx, y) > 0) {
                        yInter = 0;
                        break;
                    }
                }
                if (yInter == 0) {
                    int yDefault = y+1;
                    if (this.lastSwap != GameEnums.WHICH_SWAP.NONE) {
                        yDefault = this.ySwapCenter;
                    }
                    this.tryToCreateSpecialFruitInVerticalAlignment(x, y, numberAlign, yDefault, new Fruit(this.getIdFruit(x, y), GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING), 2);
                }
            }
            if (numberAlign >= 5) {
                this.tryToCreateSpecialFruitInVerticalAlignment(x, y, numberAlign, y+(numberAlign-1)/2, new OmegaSphere(), 5);
            }
        }

        // Special fruit activation for regularly destroyed fruits
        this.listToBeActivatedSpecialFruits.clear(); // Note : could be cleared also elsewhere... ?
        this.listToBeActivatedOmegaSpheres.clear(); // Note : could be cleared also elsewhere... ?
        for (SpaceCoors coors : this.checkerToBeEmptiedSpaces.getList()) {
            x = coors.x;
            y = coors.y;
            if (this.arrayField[y][x].getPower() != GameEnums.FRUITS_POWER.NONE) {
                this.listToBeActivatedSpecialFruits.add(new SpaceCoors(x, y));
            }
        }
    }

    private void destroyByAlignment(int x, int y) {
        if (this.checkerToBeEmptiedSpaces.add(x, y)) {
            this.catchBasket(x, y);
            this.tryToDecreaseBreakableBlockAround(x, y);
            this.thisMoveFruitsDestroyedByFall++;
            int scoreAmount = (this.thisMoveFruitsDestroyedByFall+2)/3;
            this.checkerScoreDestructionFall.add(x, y, scoreAmount);
            this.score += scoreAmount;
            this.collectedFruits++;
        }
    }

    private static final int NO_SPECIAL_FOUND = -1;
    private static final int SATURATED = -2;
    /*
    This is where special fruits are created and score is marked.
    It has to take the former special fruits into amount ! (see below for more details)
    */
    private void tryToCreateSpecialFruitInHorizontalAlignment(int x, int y, int numberAlign, int xDefault, SpaceFiller spaceFiller, int scoreAmount) {
        int xCreation = this.lookingForXSpecialFruitCreation(x, y, numberAlign);
        if (xCreation == NO_SPECIAL_FOUND) {
            xCreation = xDefault; // TODO voir le swap x y, aussi
        }
        if (xCreation != SATURATED) {
            this.createSpecialFruit(xCreation, y, spaceFiller, scoreAmount);
        }
    }

    private void tryToCreateSpecialFruitInVerticalAlignment(int x, int y, int numberAlign, int yDefault, SpaceFiller spaceFiller, int scoreAmount) {
        int yCreation = this.lookingForYSpecialFruitCreation(x, y, numberAlign);
        if (yCreation == NO_SPECIAL_FOUND) {
            yCreation = yDefault;
        }
        if (yCreation != SATURATED) {
            this.createSpecialFruit(x, yCreation, spaceFiller, scoreAmount);
        }
    }

    private void createSpecialFruit(int x, int y, SpaceFiller spaceFiller, int correspondingScoreAmount) {
        this.arrayField[y][x] = spaceFiller;
        this.checkerToBeEmptiedSpaces.remove(x, y);
        this.checkerScoreDestructionFall.add(x, y, correspondingScoreAmount); // TODO score pour "création fruit spécial ?"
        this.score += correspondingScoreAmount;
    }

    /*
    Looks for the spot in which a special fruit should be created in an horizontal alignment,
    of length align and starting at (x, y)
    if no special fruit is found, return NO_SPECIAL_FOUND
    if at least one special fruit is found, return the first free spot starting from left
    if the spaces contain only special fruit, return SATURATED
     */
    private int lookingForXSpecialFruitCreation(int x, int y, int align) {
        boolean foundSpecial = false;
        int xx;
        for (xx = x ; xx < x + align ; xx++) {
            if (this.arrayField[y][xx].getPower() != GameEnums.FRUITS_POWER.NONE) {
                foundSpecial = true;
                break;
            }
        }
        if (!foundSpecial) {
            return NO_SPECIAL_FOUND;
        }
        for (xx = x ; xx < x + align ; xx++) {
            if (this.arrayField[y][xx].getPower() == GameEnums.FRUITS_POWER.NONE) {
                return xx;
            }
        }
        return SATURATED;
    }
    private int lookingForYSpecialFruitCreation(int x, int y, int align) {
        boolean foundSpecial = false;
        int yy;
        for (yy = y ; yy < y + align ; yy++) {
            if (this.arrayField[yy][x].getPower() != GameEnums.FRUITS_POWER.NONE) {
                foundSpecial = true;
                break;
            }
        }
        if (!foundSpecial) {
            return NO_SPECIAL_FOUND;
        }
        for (yy = y ; yy < y + align ; yy++) {
            if (this.arrayField[yy][x].getPower() == GameEnums.FRUITS_POWER.NONE) {
                return yy;
            }
        }
        return SATURATED;
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
            case 2 :
                this.gth.endAllFalls();
                this.phaseCount = 0;
            break;
        }
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
                            this.destroyBySpecialFruit(x - 1, y, newListToBeActivated);
                            if (leftMargin2) {
                                this.destroyBySpecialFruit(x - 2, y, newListToBeActivated);
                            }
                            if (downMargin1) {
                                this.destroyBySpecialFruit(x - 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.destroyBySpecialFruit(x - 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (rightMargin1) {
                            this.destroyBySpecialFruit(x + 1, y, newListToBeActivated);
                            if (rightMargin2) {
                                this.destroyBySpecialFruit(x + 2, y, newListToBeActivated);
                            }
                            if (downMargin1) {
                                this.destroyBySpecialFruit(x + 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.destroyBySpecialFruit(x + 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (upMargin1) {
                            this.destroyBySpecialFruit(x, y - 1, newListToBeActivated);
                            if (upMargin2) {
                                this.destroyBySpecialFruit(x, y - 2, newListToBeActivated);
                            }
                        }
                        if (downMargin1) {
                            this.destroyBySpecialFruit(x, y + 1, newListToBeActivated);
                            if (downMargin2) {
                                this.destroyBySpecialFruit(x, y + 2, newListToBeActivated);
                            }
                        }
                        break;
                    case HORIZONTAL_LIGHTNING:
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                        for (xx = 0; xx < Constants.FIELD_XLENGTH; xx++) {
                            this.destroyBySpecialFruit(xx, y, newListToBeActivated);
                        }
                        break;
                    case VERTICAL_LIGHTNING:
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                        for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                            this.destroyBySpecialFruit(x, yy, newListToBeActivated);
                        }
                        break;
                    case OMEGA_SPHERE:
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 1);
                        if (mostPresentFruitId < this.numberOfFruitKinds) {
                            int colour = orderedFruitIndexes.get(mostPresentFruitId);
                            for (yy = 0 ; yy < Constants.FIELD_YLENGTH ; yy++) {
                                for (xx = 0 ; xx < Constants.FIELD_XLENGTH ; xx++) {
                                    if (this.getIdFruit(xx, yy) == colour && !this.checkerToBeEmptiedSpaces.get(xx, yy)) {
                                        this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
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
                            this.destroyBySpecialFruit(xx, y, newListToBeActivated);
                        }
                        for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                            this.destroyBySpecialFruit(x, yy, newListToBeActivated);
                        }
                        break;
                    case VIRTUAL_FIRE_LIGHTNING:
                        for (int i = 0 ; i < 8 ; i++) {
                            xx = x + coefDirectional8X[i];
                            yy = y + coefDirectional8Y[i];
                            while (areAcceptableCoordinates(xx, yy)) { // Note : later on, it may be blocked
                                this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                                xx += coefDirectional8X[i];
                                yy += coefDirectional8Y[i];
                            }
                        }
                        break;
                    case VIRTUAL_FIRE_FIRE: // TODO à  noter : échange fruit spécial et fruit normal, mais un fruit spécial prend la place du premier ... (5 vs 4)
                        int dist, step, dir;
                        for (dir = 0 ; dir <= 3 ; dir++) {
                            for (dist = 1 ; dist <= 3 ; dist++) {
                                xx = this.xSwapCenter + dist*coefDirectionalX[dir];
                                yy = this.ySwapCenter + dist*coefDirectionalY[dir];
                                for (step = 0 ; step < dist ; step++) {
                                    if (areAcceptableCoordinates(xx, yy)) {
                                        this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                                    }
                                    xx += coefDirectionalClockwiseTurningX[dir];
                                    yy += coefDirectionalClockwiseTurningY[dir];
                                }
                            }
                        }
                        break;
                    case VIRTUAL_OMEGA_HORIZ_LIGHTNING:
                        xx = x-1;
                        yy = y;
                        while (xx >= Math.max(x-2, 0)) {
                            this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                            xx--;
                        }
                        xx = x+1;
                        while (xx <= Math.min(x+2, Constants.FIELD_XLENGTH-1)) {
                            this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                            xx++;
                        }
                        break;
                    case VIRTUAL_OMEGA_VERT_LIGHTNING:
                        xx = x;
                        yy = y-1;
                        while (yy >= Math.max(y-2, 0)) {
                            this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                            yy--;
                        }
                        yy = y+1;
                        while (yy <= Math.min(y+2, Constants.FIELD_XLENGTH-1)) {
                            this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                            yy++;
                        }
                        break;
                    case VIRTUAL_OMEGA_FIRE:
                        leftMargin1 = (x > 0);
                        upMargin1 = (y > 0);
                        rightMargin1 = (x < Constants.FIELD_XLENGTH - 1);
                        downMargin1 = (y < Constants.FIELD_YLENGTH - 1);
                        if (leftMargin1) {
                            this.destroyBySpecialFruit(x - 1, y, newListToBeActivated);
                            if (downMargin1) {
                                this.destroyBySpecialFruit(x - 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.destroyBySpecialFruit(x - 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (rightMargin1) {
                            this.destroyBySpecialFruit(x + 1, y, newListToBeActivated);
                            if (downMargin1) {
                                this.destroyBySpecialFruit(x + 1, y + 1, newListToBeActivated);
                            }
                            if (upMargin1) {
                                this.destroyBySpecialFruit(x + 1, y - 1, newListToBeActivated);
                            }
                        }
                        if (upMargin1) {
                            this.destroyBySpecialFruit(x, y - 1, newListToBeActivated);
                        }
                        if (downMargin1) {
                            this.destroyBySpecialFruit(x, y + 1, newListToBeActivated);
                        }
                        break;
                    case VIRTUAL_OMEGA_OMEGA:
                        for (yy = 0 ; yy < Constants.FIELD_YLENGTH ; yy++) {
                            for (xx = 0; xx < Constants.FIELD_XLENGTH; xx++) {
                                this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
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

    private void destroyBySpecialFruit(int x, int y, List<SpaceCoors> newList) {
        if (this.hasFruit(x, y) || this.hasOmegaSphere(x, y)) {
            if (this.checkerToBeEmptiedSpaces.add(x, y)) {
                if ((this.arrayField[y][x].getPower() != GameEnums.FRUITS_POWER.NONE) || (this.hasOmegaSphere(x, y))) {
                    newList.add(new SpaceCoors(x, y));
                }
                this.checkerScoreDestructionSpecial.add(x, y, 1);
                this.score += 1;
                this.collectedFruits++;
            }
        }

        // Collect a basket even if no fruit is found there
        this.catchBasket(x, y);
        this.tryToDecreaseBreakableBlock(x, y);
    }

    /*
    Tells from one space which fruits are supposed to fall and add them to "checkerFallingElements"
    Tells also where new fruits should be spawned !
     */
    private void handleNewFallingFruitsAndPotentiallySpawn(int xFallInto, int yFallInto) {
        SpaceCoors coorsToFall, coorsHead;
        coorsHead = new SpaceCoors(xFallInto, yFallInto); // Note : the head is itself if there is no fruit above it
        boolean keepClimbing = true;
        int xDiagonalClimb, yDiagonalClimb;
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
            this.spawnFruit(coorsHead.x, coorsHead.y);
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

        // Get rid of former fruits for good
        for (SpaceCoors coorsDestroy : this.checkerToBeEmptiedSpaces.getList()) {
            this.arrayField[coorsDestroy.y][coorsDestroy.x] = new EmptySpace();
        }
        this.checkerToBeEmptiedSpaces.clear();

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

        // Fruits in the spawning part
        int xSpawn, ySpawn;
        for (SpaceCoors coorsSpawn : this.checkerSpawnFruits.getList()) {
            xSpawn = coorsSpawn.x;
            ySpawn = coorsSpawn.y;
            this.arrayFutureField[ySpawn][xSpawn] = new Fruit(this.checkerSpawnFruits.get(xSpawn, ySpawn));
            fallingFruitsNewCoors.add(new SpaceCoors(xSpawn, ySpawn)); // Note : apparently it used to work fine without it when using int arrays...
            newFall = true; // Sometimes (typically in a omega+omega combo), all fruits on field are destroyed at once, meaning a renewal should be asked here... (renewal fall)
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
            this.gth.startFall();
        } else {
            this.thisMoveComboCoefficient++;
            this.performStableCheck(false);
        }
    }

    private void fullyCheckFallingElements() {
        int xCheck, yCheck;
        List<SpaceCoors> emptySpaces = new ArrayList<>();
        for (yCheck = 0 ; yCheck < Constants.FIELD_YLENGTH ; yCheck++) {
            for (xCheck = 0 ; xCheck < Constants.FIELD_XLENGTH ; xCheck++) {
                if (this.arrayField[yCheck][xCheck] instanceof EmptySpace) {
                    emptySpaces.add(new SpaceCoors(xCheck, yCheck));
                } else if (this.shouldBlockDiagonalSqueeze(xCheck, yCheck)) {
                    this.blockDiagonalSqueezeHereAndDownward(xCheck, yCheck);
                }
            }
        }
        handleAboveEmptySpaces(emptySpaces);
    }
    // 551551
    // Si on détruit 3 blocs d'un coup, les blocs se mettent à glisser. Il faut mieux déterminer quels blocs peuvent attirer les blocs en case suivante !


    private void fullyCheckFallingElementsInStableCheck() {
        int xCheck, yCheck;
        List<SpaceCoors> emptySpaces = new ArrayList<>();
        precheckEmptySpaces();
        for (yCheck = 0 ; yCheck < Constants.FIELD_YLENGTH ; yCheck++) {
            for (xCheck = 0 ; xCheck < Constants.FIELD_XLENGTH ; xCheck++) {
                if (this.checkerToBeEmptiedSpaces.get(xCheck, yCheck)) {
                    emptySpaces.add(new SpaceCoors(xCheck, yCheck));
                } else if (!(this.arrayField[yCheck][xCheck] instanceof EmptySpace) && this.shouldBlockDiagonalSqueeze(xCheck, yCheck)) {
                    this.blockDiagonalSqueezeHereAndDownward(xCheck, yCheck);
                }
            }
        }
        handleAboveEmptySpaces(emptySpaces);
    }

    private boolean shouldBlockDiagonalSqueeze(int x, int y) {
        return this.shouldSpawnFruit(x, y);
    }
    
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
                && this.arrayField[ySource][xSource].canFall() && !this.checkerFallingElements.get(xSource, ySource) && !this.checkerFallingElements.get(xFall, yFall-1)
                && (!checkerDropUpperHere.get(xFall, yFall-1))
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
        
        while (this.arrayField[yy][xx].canFall() && this.checkerBlockDiagonalSqueeze.add(xx, yy)) {
            coors = this.getCoorsFallableJustBelow(xx, yy);
            if (coors == null) {
                return;
            }
            xx = coors.x;
            yy = coors.y;
        }
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

    // ------------
    // Missions 
    // Warning : order matters A LOT !
    // Note : "Control mission" is the name for whenever we want to advance missions

    public void controlMissionsWithSwap(GameEnums.WHICH_SWAP swap) {
        switch (swap) {
            case FIRE_ELECTRIC:
                if (!this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_FIRE, 1)) {
                    if (this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                    }
                    if (this.advanceMission(GameEnums.ORDER_KIND.FIRE_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.FIRE, 1);
                    }
                }
                break;
            case OMEGA_ELECTRIC:
                if (!this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_OMEGA, 1)) {
                    if (this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.LIGHTNING, 1);
                    }
                    if (this.advanceMission(GameEnums.ORDER_KIND.OMEGA_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 1);
                    }
                }
                break;
            case OMEGA_FIRE:
                if (!this.advanceMission(GameEnums.ORDER_KIND.OMEGA_FIRE, 1)) {
                    if (this.advanceMission(GameEnums.ORDER_KIND.FIRE_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.FIRE, 1);
                    }
                    if (this.advanceMission(GameEnums.ORDER_KIND.OMEGA_WILD, 1)) {
                        this.advanceMission(GameEnums.ORDER_KIND.OMEGA, 1);
                    }
                }
                break;
            case FIRE_FIRE:
                if ((
                        this.advanceMission(GameEnums.ORDER_KIND.FIRE_FIRE, 1) ||
                                this.advanceMission(GameEnums.ORDER_KIND.FIRE_WILD, 2)
                )) {
                    this.advanceMission(GameEnums.ORDER_KIND.FIRE, 2);
                    // TODO Ajouter un checker "taken in swap for mission" ou qqch du genre pour quand on contrôlera les fruits ?
                    // Il faudra veiller à cleaner ce swap juste après la première salve de destructions
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

    public void tryToDecreaseBreakableBlock(int x, int y) {
        if (this.arrayField[y][x] instanceof BreakableBlock) {
            BreakableBlock bb = (BreakableBlock) this.arrayField[y][x];
            bb.downgrade();
            if (bb.getCount() == 0) {
                this.checkerToBeEmptiedSpaces.add(x, y);
            }
        }
    }

    public void tryToDecreaseBreakableBlockAround(int x, int y) {
        if (x > 0) {
            this.tryToDecreaseBreakableBlock(x-1, y);
        }
        if (y > 0) {
            this.tryToDecreaseBreakableBlock(x, y-1);
        }
        if (x < Constants.FIELD_XLENGTH-1) {
            this.tryToDecreaseBreakableBlock(x+1, y);
        }
        if (y < Constants.FIELD_YLENGTH-1) {
            this.tryToDecreaseBreakableBlock(x, y+1);
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

    public int spawn(int x, int y) {
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

    public String getMissionSummary() {
        
        String answer = "";
        switch (this.goalKind) {
            case BASKETS :
                answer = "Paniers : " + this.basketsCount;
            break;
            case ORDERS :
                for (int i = 0; i < this.numberOfMissions; i++) {
                    answer += GameEnums.toString(this.kindsOfMissions[i]) + " " + this.amountsMission[i] + " ";
                }
            break;
        }
        return answer;
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

    public GameEnums.GOAL_KIND getGoalKind() {
        return this.goalKind;
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
        // TODO change "checkerDropUpperRightHere" into "checkerDropUpperLeftHere" ;
        //  I didn't change it yet because of the logic of the checkers in handleDiagonallySqueezingFruits and the fact the falls were still bugged.
    }
}


// TODO philosophie paniers et blocs cassables : ce qui suit est acceptable ?
// Un breakable peut être dégradé plusieurs fois par des alignements.
// Dans un même check stable, un même breakable peut être détruit plusieurs fois
// Block breakable et à côté d'un fruit spécial nouvellement créé : on dégrade le breakable.
// Gestion breakables / paniers : quand la dernière couche d'un rocher est détruite, on ne collecte pas les paniers avant le stable check suivant.
// C'est ce qui se produit quand on fait un échange près d'un rocher à 1 qui déclenche le fruit spécial et celui-ci touche la case à panier visée. Pas de panier collecté.

// TODO : collecte panier dans le cas swap oméga + X et dans les cas feu + éclair (et les 2 autres swap.... ?)

// TODO Diagonal squeeze et chute :
// On doit pouvoir diminuer le check des cases vides à chaque check instable en construisant une liste des "cases potentiellement vides" ; idem pour les cases potentiellement squeezables. Mais attention, une case potentiellement squezzable n'a rien à voir avec une case qui squeeze lors d'une vérification, stable ou pas !

// 551551 Fait :
// Ajout du squeeze diagonal
// Ajout de boutons de changement
// Ajout d'un check qui n'était pas refait au-delà de la première destruction car jugé inutile
// Déplacement endroit où on score (ce n'est plus dans le check de l'alignement mais bien dans la destruction du fruit)
// Ajout du nombre de fruits dans le titre
// Ajout d'un menu de niveau !
// Changement icone (provisoire)

// 551551 Bug : les fruits effacés juste au dessus des sphères Oméga