package com.example.fruitfall;


import android.os.Build;
import android.widget.Space;

import androidx.annotation.RequiresApi;

import com.example.fruitfall.level.LevelData;
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

    private int fruitsNumber;
    private SpaceFiller[][] arrayField;
    private Checker alignedFruitsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH); // Can ONLY contain coordinates of spaces with fruits
    private Checker toBeDestroyedFruitsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private Checker fallingElementsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private boolean[][] arrayShouldFruitsBeSpawned = new boolean[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private SpaceCoors[][] arrayTeleporterCorrespondingExit = new SpaceCoors[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH]; // IMPORTANT : "out" array contains coors of corresponding "in" and vice-versa.
    private SpaceCoors[][] arrayTeleporterCorrespondingEntrance = new SpaceCoors[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private SpaceFiller[][] arrayFutureField = new SpaceFiller[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private IntChecker scoreDestructionFallChecker = new IntChecker(Constants.FIELD_XLENGTH,Constants.FIELD_YLENGTH, 0);
    private IntChecker scoreDestructionSpecialChecker = new IntChecker(Constants.FIELD_XLENGTH,Constants.FIELD_YLENGTH, 0);
    private IntChecker spawnFruitsChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, Constants.NOT_A_FRUIT);

    private IntChecker horizAlignmentChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);
    private IntChecker vertAlignmentChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);

    public static int[] gameIndexToImageIndex = new int[Constants.RESOURCES_NUMBER_FRUITS];
    private int score;
    private int thisMoveComboCoefficient;
    private int thisMoveFruitsDestroyedByFall;
    private int collectedFruits;
    private String title;

    private int omegaDestructionIdFruit = Constants.NOT_A_FRUIT;
    private int omegaDestructionXSphere = Constants.NOT_A_SPACE_COOR;
    private int omegaDestructionYSphere = Constants.NOT_A_SPACE_COOR;
    private int xSwapCenter, ySwapCenter;
    private int xSwapSide, ySwapSide;
    private int phaseCount;
    private GameEnums.WHICH_SWAP lastSwap = GameEnums.WHICH_SWAP.NONE;

    public GameTimingHandler gth;  // TODO le passer en privé et le rendre accessible par getters...
    private List<SpaceCoors> listToBeActivatedSpecialFruits = new ArrayList<>();
    private List<SpaceCoors> listToBeActivatedOmegaSpheres = new ArrayList<>();
    
    private List<SpaceCoors> listDelayedLocks = new ArrayList<>();
    private int countRemainingLocks;
    private int[] coefDirectionalX = {-1, 0, 1, 0};
    private int[] coefDirectionalY = {0, -1, 0, 1};
    private int[] coefDirectional8X = {0, 1, 1, 1, 0, -1, -1, -1};
    private int[] coefDirectional8Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    private int[] coefDirectionalClockwiseTurningX = {1, 1, -1, -1};
    private int[] coefDirectionalClockwiseTurningY = {-1, 1, 1, -1};

    // Note : convention : before a stable check, only one source / destination per colour.
    private SpaceCoors[]  omegaSourceCoorsByColour = new SpaceCoors[Constants.RESOURCES_NUMBER_FRUITS];
    private List<SpaceCoors>[] omegaTargetsCoorsByColour = new List[Constants.RESOURCES_NUMBER_FRUITS];

    // Cheat part
    boolean toleranceMode = false;

    public GameHandler() {
        this.arrayField = new SpaceFiller[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        gth = new GameTimingHandler(this);
    }

    // ----------------------------
    // Misc lil methods

    public int getFruit(int x, int y) {
        return this.arrayField[y][x].getFruit();
    }

    public SpaceFiller getSpace(int x, int y) {
        return this.arrayField[y][x];
    }

    // Precondition : x,y supposed to be a fruit
    private boolean gotAlignment(int x, int y, int x2, int y2, int x3, int y3) {
        return (this.getFruit(x, y) == this.getFruit(x2, y2) && this.getFruit(x, y) == this.getFruit(x3, y3));
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
            this.omegaDestructionXSphere = x1;
            this.omegaDestructionYSphere = y1;
            return GameEnums.WHICH_SWAP.OMEGA_OMEGA;
        }
        if (omega1 && fire2) {
            this.omegaDestructionIdFruit = this.getFruit(x2, y2);
            this.omegaDestructionXSphere = x1;
            this.omegaDestructionYSphere = y1;
            return GameEnums.WHICH_SWAP.OMEGA_FIRE;
        }
        if (omega2 && fire1) {
            this.omegaDestructionIdFruit = this.getFruit(x1, y1);
            this.omegaDestructionXSphere = x2;
            this.omegaDestructionYSphere = y2;
            return GameEnums.WHICH_SWAP.OMEGA_FIRE;
        }
        if (omega2 && fire1) {
            this.omegaDestructionIdFruit = this.getFruit(x1, y1);
            this.omegaDestructionXSphere = x2;
            this.omegaDestructionYSphere = y2;
            return GameEnums.WHICH_SWAP.OMEGA_FIRE;
        }
        if (omega1 && light2) {
            this.omegaDestructionIdFruit = this.getFruit(x2, y2);
            this.omegaDestructionXSphere = x1;
            this.omegaDestructionYSphere = y1;
            return GameEnums.WHICH_SWAP.OMEGA_ELECTRIC;
        }
        if (omega2 && light1) {
            this.omegaDestructionIdFruit = this.getFruit(x1, y1);
            this.omegaDestructionXSphere = x2;
            this.omegaDestructionYSphere = y2;
            return GameEnums.WHICH_SWAP.OMEGA_ELECTRIC;
        }
        // Put them at the end, or else !
        if (omega1 && this.hasFruit(x2, y2)) {
            this.omegaDestructionIdFruit = this.getFruit(x2, y2);
            this.omegaDestructionXSphere = x1;
            this.omegaDestructionYSphere = y1;
            return GameEnums.WHICH_SWAP.FRUIT_OMEGA;
        }
        if (omega2 && this.hasFruit(x1, y1)) {
            this.omegaDestructionIdFruit = this.getFruit(x1, y1); // TODO rename this ;)
            this.omegaDestructionXSphere = x2;
            this.omegaDestructionYSphere = y2;
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
        this.spawnFruitsChecker.add(x, y, new Random().nextInt(this.fruitsNumber));
    }

    /*
    Returns the coordinates of the fruit that is supposed to fall into this space (usually the one right above, but there may be teleporters)
    Or return null if the space is either non existent or empty
    SOMETHING IS SUPPOSED TO FALL (and emptiness can fall)
     */
    private SpaceCoors getCoorsFruitRightAbove(int x, int y) {
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
    private SpaceCoors getCoorsFruitRightBelow(int x, int y) {
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

    public void initializeGrid(LevelData ld) { // TODO rebaptize
        // Clear everything !
        alignedFruitsChecker.clear();
        toBeDestroyedFruitsChecker.clear();
        fallingElementsChecker.clear();
        scoreDestructionFallChecker.clear();
        scoreDestructionSpecialChecker.clear();
        spawnFruitsChecker.clear();
        horizAlignmentChecker.clear();
        vertAlignmentChecker.clear();
        listDelayedLocks.clear();

        this.score = 0;
        this.collectedFruits = 0;
        this.thisMoveComboCoefficient = 1;
        this.thisMoveFruitsDestroyedByFall = 0;
        this.phaseCount = 0;
        this.countRemainingLocks = 0;
        
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
        this.fruitsNumber = ld.getFruitColours();
        for (i = 0 ; i < this.fruitsNumber ; i++) {
            this.omegaTargetsCoorsByColour[i] = new ArrayList<>();
            this.omegaSourceCoorsByColour[i] = null;
        }
        int numberChosen = 0;
        // Forced part
        if (ld.getForcedIndexes() != null) {
            for (Integer indexSelected : ld.getForcedIndexes()) {
                this.gameIndexToImageIndex[numberChosen] = indexSelected;
                arrayNTI[indexSelected] = -1;
                numberChosen++;
            }
        }
        // Random fruits
        int numberToMeet;
        int indexSelected;
        int numberMet;
        while (numberChosen < this.fruitsNumber) {
            numberToMeet = rand.nextInt(Constants.RESOURCES_NUMBER_FRUITS-numberChosen);
            numberToMeet++; // (1 to fruitsNumber-numberChosen) The "i-th" not taken index met is the one desired.
            numberMet = 0;
            indexSelected = 0;
            while (numberMet < numberToMeet) {
                if (arrayNTI[indexSelected] != -1) {
                    numberMet++;
                }
                indexSelected++;
            }
            indexSelected--;
            this.gameIndexToImageIndex[numberChosen] = arrayNTI[indexSelected];
            arrayNTI[indexSelected] = -1;
            numberChosen++;
        }


        // Read data for array
        boolean shouldGainSpawn;
        GameEnums.SPACE_DATA data;
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                data = ld.getData(x, y);
                if (data == GameEnums.SPACE_DATA.FRUIT) {
                    // Generate fruit
                    this.arrayField[y][x] = new Fruit(rand.nextInt(this.fruitsNumber));
                    if (x >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x-1, y, x-2, y);
                    }
                    if (y >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x, y-1, x, y-2);
                    }
                    // Spawn
                    shouldGainSpawn = (y == 0 && ld.getTopRowSpawn(x) == GameEnums.SPACE_DATA.VOID_SPAWN);
                    if (this.arrayTeleporterCorrespondingEntrance[y][x] != null) {
                        shouldGainSpawn = false;
                    } else if (y != 0) {
                        shouldGainSpawn = (ld.getData(x, y-1) == GameEnums.SPACE_DATA.VOID_SPAWN);
                    }
                    this.arrayShouldFruitsBeSpawned[y][x] = shouldGainSpawn;
                } else if (data == GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH1 || data == GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH2 ||
                        data == GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH3 || data == GameEnums.SPACE_DATA.DELAYED_LOCK_LENGTH4 ) {
                    this.arrayField[y][x] = new DelayedLock(ld.getLockDuration(data));
                    this.countRemainingLocks++;
                    this.listDelayedLocks.add(new SpaceCoors(x, y));
                } else {
                    // Not a space able to handle fruits ; still need to initialize arrays.
                    this.arrayField[y][x] = new VoidSpace();
                    this.arrayShouldFruitsBeSpawned[y][x] = false;
                }
            }
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

        // Make sure there are no aligned fruits at start !
        List<SpaceCoors> formerListGetAlignedFruit = new ArrayList<>();
        while(!this.alignedFruitsChecker.getList().isEmpty()) {

            // Note : unfortunately, we need to copy coors one by one
            //formerListGetAlignedFruit = this.listGetAlignedFruit;
            formerListGetAlignedFruit.clear();
            for(SpaceCoors coors : this.alignedFruitsChecker.getList()) {
                formerListGetAlignedFruit.add(new SpaceCoors(coors.x, coors.y));
            }

            this.alignedFruitsChecker.clear();

            // Renew fruits
            for (SpaceCoors coors : formerListGetAlignedFruit) {
                x = coors.x;
                y = coors.y;
                this.arrayField[y][x] = new Fruit(rand.nextInt(this.fruitsNumber));
            }
            // So... what's next with new fruits ?
            for (SpaceCoors coors : formerListGetAlignedFruit) {
                x = coors.x;
                y = coors.y;
                testAndAlertAboutAlignedFruitsAroundSpace(x, y);
            }
        }

        this.gth.init();
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

    public void triggerAfterOmegaStasis() {
        this.gth.startDestruction(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void triggerDestruction() {
        // Oh, and also restart score arrays :
        this.scoreDestructionSpecialChecker.clear();
        this.scoreDestructionFallChecker.clear();

        // Omega check arrays (since it's for animations) :
        for (int i = 0 ; i < this.fruitsNumber ; i++ ) {
            this.omegaSourceCoorsByColour[i] = null;
            this.omegaTargetsCoorsByColour[i].clear();
        }

        // Quite simplistic :
        // If there are no special fruits / special objects (omega) waiting for being destroyed, move on with falls.
        // Otherwise, destroy, detect new special fruits, restart a stasis
        List<SpaceCoors> newListToBeActivated = new ArrayList<>();
        int x, y, fruit, xx, yy;
        boolean leftMargin1, leftMargin2, upMargin1, upMargin2, rightMargin1, rightMargin2, downMargin1, downMargin2;
        boolean goForDestructionAgain = false;
        if (!this.listToBeActivatedSpecialFruits.isEmpty()) {

            // Count each fruit for Omega spheres
            // The most present one gets destroyed first, etc...
            // In case of a swap, the id of the stripped fruit is artificially boosted so it is ranked first !
            int[] countRemainingFruits = new int[this.fruitsNumber];
            if (this.omegaDestructionIdFruit != Constants.NOT_A_FRUIT) {
                countRemainingFruits[this.omegaDestructionIdFruit] = Constants.DUMMY_BOOST_FRUIT_COUNT;
                this.omegaDestructionIdFruit = Constants.NOT_A_FRUIT;
            }
            for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                    fruit = this.getFruit(x, y);
                    if (fruit != Constants.NOT_A_FRUIT && !this.toBeDestroyedFruitsChecker.get(x, y)) {
                        countRemainingFruits[fruit]++;
                    }
                }
            }

            List<Integer> orderedFruitIndexes = new ArrayList<>();
            for (int i = 0 ; i < this.fruitsNumber ; i++) {
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
                        for (xx = 0; xx < Constants.FIELD_XLENGTH; xx++) {
                            this.destroyBySpecialFruit(xx, y, newListToBeActivated);
                        }
                        break;
                    case VERTICAL_LIGHTNING:
                        for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                            this.destroyBySpecialFruit(x, yy, newListToBeActivated);
                        }
                        break;
                    case OMEGA_SPHERE:
                        if (mostPresentFruitId < this.fruitsNumber) {
                            int colour = orderedFruitIndexes.get(mostPresentFruitId);
                            for (yy = 0 ; yy < Constants.FIELD_YLENGTH ; yy++) {
                                for (xx = 0 ; xx < Constants.FIELD_XLENGTH ; xx++) {
                                    if (this.getFruit(xx, yy) == colour && !this.toBeDestroyedFruitsChecker.get(xx, yy)) {
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
                            xx = x + this.coefDirectional8X[i];
                            yy = y + this.coefDirectional8Y[i];
                            while (areAcceptableCoordinates(xx, yy)) { // Note : later on, it may be blocked
                                this.destroyBySpecialFruit(xx, yy, newListToBeActivated);
                                xx += this.coefDirectional8X[i];
                                yy += this.coefDirectional8Y[i];
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
        if (goForDestructionAgain) {
            this.gth.startDestruction(false);
        } else {
            this.fullyCheckFallingElementsInStableCheck(); // TODO j'ai du ajouter ça pour recalculer les nouveaux éléments qui spawnaient. Il va vraiment falloir revoir ça !
            this.gth.startFall();
        }
    }

    private void destroyBySpecialFruit(int x, int y, List<SpaceCoors> newList) {
        if (this.hasFruit(x, y) || this.hasOmegaSphere(x, y)) {
            if (this.toBeDestroyedFruitsChecker.add(x, y)) {
                if ((this.arrayField[y][x].getPower() != GameEnums.FRUITS_POWER.NONE) || (this.hasOmegaSphere(x, y))) {
                    newList.add(new SpaceCoors(x, y));
                }
                this.scoreDestructionSpecialChecker.add(x, y, 1);
                this.score += 1;
                this.collectedFruits++;
            }
        }
    }

    /*
    Check method !
    When fruits should be checked for destruction or creation.
    IMPORTANT : arrayFruit is NOT updated here for the purpose of drawing !
    */
    private void performStableCheck(boolean comesFromSwap) {
        int x, y;
        this.alignedFruitsChecker.clear();
        this.scoreDestructionFallChecker.clear(); // Don't reinitialize the jackpot count, please ;)

        // Check all horizontal and vertical fruits, and set them for destruction
        this.horizAlignmentChecker.clear();
        this.vertAlignmentChecker.clear();
        int xAft, yAft, currentFruit; // x After, y After last fruit
        int xx, yy;
        boolean isAlignment = false;
        boolean isHorizontalSwap = (ySwapCenter == ySwapSide);
        if (!comesFromSwap) {
            this.lastSwap = GameEnums.WHICH_SWAP.NONE;
        }
        if (this.lastSwap == GameEnums.WHICH_SWAP.ELECTRIC_ELECTRIC) {
            this.arrayField[ySwapCenter][xSwapCenter] = new Fruit(this.arrayField[ySwapCenter][xSwapCenter].getFruit(), GameEnums.FRUITS_POWER.VIRTUAL_LIGHTNING_LIGHTNING);
            this.toBeDestroyedFruitsChecker.add(this.xSwapSide, this.ySwapSide); // TODO Deal with the "to be destroyed" mob et les to be activated
            this.toBeDestroyedFruitsChecker.add(this.xSwapCenter, this.ySwapCenter);
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.xSwapCenter, this.ySwapCenter));
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.FIRE_ELECTRIC) {
            this.arrayField[ySwapCenter][xSwapCenter] = new Fruit(this.arrayField[ySwapCenter][xSwapCenter].getFruit(), GameEnums.FRUITS_POWER.VIRTUAL_FIRE_LIGHTNING);
            this.toBeDestroyedFruitsChecker.add(this.xSwapSide, this.ySwapSide);
            this.toBeDestroyedFruitsChecker.add(this.xSwapCenter, this.ySwapCenter);
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.xSwapCenter, this.ySwapCenter));
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.FIRE_FIRE) {
            this.arrayField[ySwapCenter][xSwapCenter] = new Fruit(this.arrayField[ySwapCenter][xSwapCenter].getFruit(), GameEnums.FRUITS_POWER.VIRTUAL_FIRE_FIRE);
            this.toBeDestroyedFruitsChecker.add(this.xSwapSide, this.ySwapSide);
            this.toBeDestroyedFruitsChecker.add(this.xSwapCenter, this.ySwapCenter);
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.xSwapCenter, this.ySwapCenter));
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_FIRE) {
            this.toBeDestroyedFruitsChecker.add(this.omegaDestructionXSphere, this.omegaDestructionYSphere); // TODO uniformize : put X and Y first
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.omegaDestructionXSphere, this.omegaDestructionYSphere));
            this.omegaSourceCoorsByColour[this.omegaDestructionIdFruit] = (new SpaceCoors(this.omegaDestructionXSphere, this.omegaDestructionYSphere)); // Note : ne pas oublier ça, sinon...
            for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                for (xx = 0; xx < Constants.FIELD_XLENGTH ; xx++) {
                    if (this.getFruit(xx, yy) == this.omegaDestructionIdFruit) {
                        this.arrayField[yy][xx] = new Fruit(this.arrayField[yy][xx].getFruit(), GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_FIRE);
                        this.listToBeActivatedSpecialFruits.add(new SpaceCoors(xx, yy));
                        this.toBeDestroyedFruitsChecker.add(xx, yy);
                        this.omegaTargetsCoorsByColour[this.omegaDestructionIdFruit].add(new SpaceCoors(xx, yy)); // TODO : faire un rayon différent pour les rayons des lightning et les fruit ?
                    }
                }
            }
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_ELECTRIC) {
            this.toBeDestroyedFruitsChecker.add(this.omegaDestructionXSphere, this.omegaDestructionYSphere); // TODO uniformize : put X and Y first
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.omegaDestructionXSphere, this.omegaDestructionYSphere));
            this.omegaSourceCoorsByColour[this.omegaDestructionIdFruit] = (new SpaceCoors(this.omegaDestructionXSphere, this.omegaDestructionYSphere));
            boolean shouldBeHoriz = true;
            for (yy = 0; yy < Constants.FIELD_YLENGTH; yy++) {
                for (xx = 0; xx < Constants.FIELD_XLENGTH ; xx++) {
                    if (this.getFruit(xx, yy) == this.omegaDestructionIdFruit) {
                        this.arrayField[yy][xx] = new Fruit(this.arrayField[yy][xx].getFruit(), shouldBeHoriz ? GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_HORIZ_LIGHTNING : GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_VERT_LIGHTNING);
                        this.listToBeActivatedSpecialFruits.add(new SpaceCoors(xx, yy));
                        this.toBeDestroyedFruitsChecker.add(xx, yy);
                        shouldBeHoriz = !shouldBeHoriz;
                        this.omegaTargetsCoorsByColour[this.omegaDestructionIdFruit].add(new SpaceCoors(xx, yy));
                    }
                }
            }
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_OMEGA) {
            this.toBeDestroyedFruitsChecker.add(this.omegaDestructionXSphere, this.omegaDestructionYSphere);
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.omegaDestructionXSphere, this.omegaDestructionYSphere));
            this.arrayField[this.omegaDestructionYSphere][this.omegaDestructionXSphere] = new Fruit(this.arrayField[this.omegaDestructionYSphere][this.omegaDestructionXSphere].getFruit(), GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_OMEGA);
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.FRUIT_OMEGA) {
            this.toBeDestroyedFruitsChecker.add(this.omegaDestructionXSphere, this.omegaDestructionYSphere);
            this.listToBeActivatedSpecialFruits.add(new SpaceCoors(this.omegaDestructionXSphere, this.omegaDestructionYSphere));
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.NONE || this.lastSwap == GameEnums.WHICH_SWAP.FRUIT_FRUIT) {
            // Alignment check
            isAlignment = true;
            for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                for (x = 0 ; x < Constants.FIELD_XLENGTH ; x++) {
                    if (this.hasFruit(x, y)) {
                        currentFruit = this.getFruit(x, y);
                        // Horizontal check
                        if (x == 0 || this.getFruit(x-1, y) != currentFruit) {
                            xAft = x+1;
                            while (xAft < Constants.FIELD_XLENGTH && this.getFruit(xAft, y) == currentFruit) {
                                xAft++;
                            }
                            // xx = last coordinate with the correct fruit PLUS ONE
                            if (xAft - x >= 3) {
                                this.horizAlignmentChecker.add(x, y, xAft-x);
                                this.toBeDestroyedFruitsChecker.add(x, y);
                                for (xx = x+1; xx < xAft ; xx++) {
                                    this.horizAlignmentChecker.add(xx, y, 1);
                                    this.toBeDestroyedFruitsChecker.add(xx, y);
                                }
                            }
                        }
                        // Vertical check
                        if (y == 0 || this.getFruit(x, y-1) != currentFruit) {
                            yAft = y+1;
                            while (yAft < Constants.FIELD_YLENGTH && this.getFruit(x, yAft) == currentFruit) {
                                yAft++;
                            }
                            if (yAft - y >= 3) {
                                this.vertAlignmentChecker.add(x, y, yAft - y);
                                this.toBeDestroyedFruitsChecker.add(x, y);
                                for (yy = y+1; yy < yAft ; yy++) {
                                    this.vertAlignmentChecker.add(x, yy, 1);
                                    this.toBeDestroyedFruitsChecker.add(x, yy);
                                }
                            }
                        }
                    }
                }
            }

            // Yeehaw, handling special fruits !
            int numberAlign, numberAlignTransversal;
            int xInter, yInter;
            for (SpaceCoors coors : this.horizAlignmentChecker.getList()) {
                x = coors.x;
                y = coors.y;
                numberAlign = this.horizAlignmentChecker.get(x, y);
                if (numberAlign == 3 || numberAlign == 4) {
                    xInter = -1;
                    for (xx = x ; xx < x + numberAlign ; xx++) {
                        if (this.vertAlignmentChecker.get(xx, y) > 0) {
                            yy = y;
                            numberAlignTransversal = 1;
                            while (this.vertAlignmentChecker.get(xx,yy) == 1) {
                                yy--;
                                numberAlignTransversal++;
                            } // numberAlignTransversal = number of fruits identical going upwards starting with yy included
                            yy = y+1;
                            while (yy < Constants.FIELD_YLENGTH && this.vertAlignmentChecker.get(xx, yy) == 1) {
                                yy++;
                                numberAlignTransversal++;
                            }
                            if (numberAlignTransversal == 3 || numberAlignTransversal == 4) {
                                // TODO Changer ce comportement (pas de fruit créé si à l'intersection)... ou faire avec ?
                                // TODO Il faudra voir ce qu'on veut en cas de test 3x3 fruits identiques
                                // TODO sorte de bug quand on a 3 fruits dans un sens et 4 dans un autre, puisqu'on a 2 fruits spéciaux créés, on ne veut pas ça...
                                if (this.arrayField[y][xx].getPower() == GameEnums.FRUITS_POWER.NONE) {
                                    this.createSpecialFruit(xx, y, new Fruit(this.getFruit(x, y), GameEnums.FRUITS_POWER.FIRE), 3);
                                }
                            }
                            if (numberAlignTransversal >= 5) {
                                xInter = xx;
                            }
                        }
                    }
                    if (xInter == -1 && numberAlign == 4) {
                        this.tryToCreateSpecialFruitInHorizontalAlignment(x, y, numberAlign, x+1, new Fruit(this.getFruit(x, y), GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING), 2);
                    }
                }
                if (numberAlign >= 5) {
                    this.createSpecialFruit(x+(numberAlign-1)/2, y, new OmegaSphere(), 5);
                }
            }
            for (SpaceCoors coors : this.vertAlignmentChecker.getList()) {
                x = coors.x;
                y = coors.y;
                numberAlign = this.vertAlignmentChecker.get(x, y);
                if (numberAlign == 4) {
                    // Only check if there is nothing done horizontally - other checks already done.
                    yInter = -1;
                    for (xx = x ; xx < x + numberAlign ; xx++) {
                        if (this.vertAlignmentChecker.get(xx, y) > 0) {
                            yInter = 0;
                            break;
                        }
                    }
                    if (yInter == 0) {
                        this.tryToCreateSpecialFruitInVerticalAlignment(x, y, numberAlign, y+1, new Fruit(this.getFruit(x, y), GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING), 2);
                    }
                }
                if (numberAlign >= 5) {
                    this.tryToCreateSpecialFruitInVerticalAlignment(x, y, numberAlign, y+(numberAlign-1)/2, new OmegaSphere(), 5);
                }
            }

            // Score management + special fruit activation for regularly destroyed fruits
            int scoreAmount;
            this.listToBeActivatedSpecialFruits.clear(); // Note : could be cleared also elsewhere... ?
            this.listToBeActivatedOmegaSpheres.clear(); // Note : could be cleared also elsewhere... ?
            for (SpaceCoors coors : this.toBeDestroyedFruitsChecker.getList()) {
                x = coors.x;
                y = coors.y;
                this.thisMoveFruitsDestroyedByFall++;
                scoreAmount = (this.thisMoveFruitsDestroyedByFall+2)/3;
                this.scoreDestructionFallChecker.add(x, y, scoreAmount);
                this.score += scoreAmount;
                this.collectedFruits++;
                if (this.arrayField[y][x].getPower() != GameEnums.FRUITS_POWER.NONE) {
                    this.listToBeActivatedSpecialFruits.add(new SpaceCoors(x, y));
                }
            }

        }



        if (this.toBeDestroyedFruitsChecker.getList().isEmpty()) {
            // Nothing new destroyed : move on.
            this.thisMoveComboCoefficient = 1;
            this.thisMoveFruitsDestroyedByFall = 0;
            this.triggerNextPhaseAfterStableCheck();
        } else if (this.lastSwap == GameEnums.WHICH_SWAP.FRUIT_OMEGA || this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_ELECTRIC || this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_FIRE || this.lastSwap == GameEnums.WHICH_SWAP.OMEGA_OMEGA ) {
            // Swap with omega : an extra stasis
            this.gth.startRayAnimations();
        } else {
            // Destruction time !
            this.fullyCheckFallingElementsInStableCheck();
            this.gth.startDestruction(isAlignment);
        }
    }

    private void performLockDiscount() {
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
                        this.toBeDestroyedFruitsChecker.add(x, y);
                        newlyDestroyed.add(new SpaceCoors(x, y));
                    }
                }
            }
            if (!newlyDestroyed.isEmpty()) {
                this.gth.startDestructionLocks(newlyDestroyed);
                return;
            }
        }
        this.triggerNextPhaseAfterStableCheck();
    }
    
    private void triggerNextPhaseAfterStableCheck() {
        this.phaseCount++; // Note : phaseCount should only be started here
        switch(this.phaseCount) {
            case 1 : this.performLockDiscount(); break;
            case 2 :
                this.gth.endAllFalls();
                this.phaseCount = 0;
            break;
        }
    }

    private void createSpecialFruit(int x, int y, SpaceFiller spaceFiller, int correspondingScoreAmount) {
        this.arrayField[y][x] = spaceFiller;
        this.toBeDestroyedFruitsChecker.remove(x, y);
        this.scoreDestructionFallChecker.add(x, y, correspondingScoreAmount); // TODO score pour "création fruit spécial ?"
        this.score += correspondingScoreAmount;
    }
    
    private static int NO_SPECIAL_FOUND = -1;
    private static int SATURATED = -2;
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
            yCreation = yDefault; // TODO voir le swap x y, aussi
        }
        if (yCreation != SATURATED) {
            this.createSpecialFruit(x, yCreation, spaceFiller, scoreAmount);
        }
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
    Tells from one space which fruits are supposed to fall and add them to "fallingElementsChecker"
    Tells also where new fruits should be spawned !
     */
    private void handleNewFallingFruitsAndPotentiallySpawn(int xFallInto, int yFallInto) {
        SpaceCoors coorsToFall, coorsHead;
        coorsToFall = this.getCoorsFruitRightAbove(xFallInto, yFallInto);
        coorsHead = new SpaceCoors(xFallInto, yFallInto); // Note : the head is itself if there is no fruit above it

        // Climb up to declare all falling spaces from this one
        while (coorsToFall != null && this.fallingElementsChecker.add(coorsToFall.x, coorsToFall.y)) {
            coorsHead = new SpaceCoors(coorsToFall.x, coorsToFall.y);
            coorsToFall = this.getCoorsFruitRightAbove(coorsToFall.x, coorsToFall.y);
        }
        // TODO glissement diagonal. Ca va être drôle.

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
        List<SpaceCoors> newFallingFruitsCoors = new ArrayList<>();

        // Get rid of former fruits for good
        for (SpaceCoors coorsDestroy : this.toBeDestroyedFruitsChecker.getList()) {
            this.arrayField[coorsDestroy.y][coorsDestroy.x] = new EmptySpace();
        }
        this.toBeDestroyedFruitsChecker.clear();

        // Shift all falling fruits "below" in futur array
        int xFall, yFall;
        boolean newFall = false;
        for (SpaceCoors coorsFall : this.fallingElementsChecker.getList()) {
            xFall = coorsFall.x;
            yFall = coorsFall.y;
            belowCoors = this.getCoorsFruitRightBelow(xFall, yFall);
            this.arrayFutureField[belowCoors.y][belowCoors.x] = this.arrayField[yFall][xFall];
            this.arrayField[yFall][xFall] = new EmptySpace();
            newFallingFruitsCoors.add(new SpaceCoors(belowCoors.x, belowCoors.y));
        }
        this.fallingElementsChecker.clear();

        // Fruits in the spawning part
        int xSpawn, ySpawn;
        for (SpaceCoors coorsSpawn : this.spawnFruitsChecker.getList()) {
            xSpawn = coorsSpawn.x;
            ySpawn = coorsSpawn.y;
            this.arrayFutureField[ySpawn][xSpawn] = new Fruit(this.spawnFruitsChecker.get(xSpawn, ySpawn));
            newFallingFruitsCoors.add(new SpaceCoors(xSpawn, ySpawn)); // Note : apparently it used to work fine without it when using int arrays...
            newFall = true; // Sometimes (typically in a omega+omega combo), all fruits on field are destroyed at once, meaning a renewal should be asked here... (renewal fall)
        }
        this.spawnFruitsChecker.clear();

        // Change future array to array
        int xNew, yNew;
        for (SpaceCoors coorsNew : newFallingFruitsCoors) {
            xNew = coorsNew.x;
            yNew = coorsNew.y;
            this.arrayField[yNew][xNew] = this.arrayFutureField[yNew][xNew];
        }

        // Now, check again falling elements.
        this.fullyCheckFallingElements();

        if (!newFall) {
            // (renewal fall) ... instead of here
            for (SpaceCoors check : this.fallingElementsChecker.getList()) { // If not for this check it would be launched again and again
                if (!(this.arrayField[check.y][check.x] instanceof EmptySpace)) {
                    newFall = true ; break;
                }
            }
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
        for (yCheck = 0 ; yCheck < Constants.FIELD_YLENGTH ; yCheck++) {
            for (xCheck = 0 ; xCheck < Constants.FIELD_XLENGTH ; xCheck++) {
                if (this.arrayField[yCheck][xCheck] instanceof EmptySpace) {
                    this.handleNewFallingFruitsAndPotentiallySpawn(xCheck, yCheck);
                }
            }
        }
    }

    private void fullyCheckFallingElementsInStableCheck() {
        int xCheck, yCheck;
        for (yCheck = 0 ; yCheck < Constants.FIELD_YLENGTH ; yCheck++) {
            for (xCheck = 0 ; xCheck < Constants.FIELD_XLENGTH ; xCheck++) {
                if (this.toBeDestroyedFruitsChecker.get(xCheck, yCheck)) {
                    this.handleNewFallingFruitsAndPotentiallySpawn(xCheck, yCheck);
                }
            }
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
            this.alignedFruitsChecker.add(x1, y1);
            this.alignedFruitsChecker.add(x2, y2);
            this.alignedFruitsChecker.add(x3, y3);
        }
    }

    private boolean areAcceptableCoordinates(int x, int y) {
        return (x >= 0 && y >= 0 && x < Constants.FIELD_XLENGTH && y < Constants.FIELD_YLENGTH);
    }

    // ------------
    // Getters for drawing

    public boolean isNotFallingFruit(int x, int y) {
        return (!this.fallingElementsChecker.get(x, y));
    }

    public boolean isNotDestroyedBeforeFall(int x, int y) {
        return (!this.toBeDestroyedFruitsChecker.get(x, y));
    }
    public SpaceCoors getDestination(int x, int y) {
        return this.arrayTeleporterCorrespondingExit[y][x];
    }

    // List of (x, y) spaces with fruits that are falling from (x, y) to the next space (usually x, y+1)
    public List<SpaceCoors> getFallingEltsCoors() {
        return this.fallingElementsChecker.getList();
    }

    public int spawn(int x, int y) {
        return (this.spawnFruitsChecker.get(x, y));
    }

    // List of (x, y) spaces with fruits that are spawning into
    public List<SpaceCoors> getSpawningFruitsCoors() {
        return this.spawnFruitsChecker.getList();
    }

    public int getRandomFruit(int indexFieldFruit) {
        return this.gameIndexToImageIndex[indexFieldFruit];
    }

    public GameEnums.FRUITS_POWER getFruitPowerFromCoors(int x, int y) {
        return this.arrayField[y][x].getPower();
    }

    public int getScore() {
        return this.score;
    }

    public int getFruits() {
        return this.collectedFruits;
    }

    public String getTitle() {
        return this.title;
    }

    public int scoreFallSpace(int x, int y) {
        return (this.scoreDestructionFallChecker.get(x, y));
    }

    public List<SpaceCoors> getContributingSpacesScoreFall() {
        return this.scoreDestructionFallChecker.getList();
    }

    public int scoreDestructionSpecialSpace(int x, int y) {
        return (this.scoreDestructionSpecialChecker.get(x, y));
    }

    public List<SpaceCoors> getContributingSpacesScoreDestructionSpecial() {
        return this.scoreDestructionSpecialChecker.getList();
    }

    public boolean hasOmegaSphere(int x, int y) {
        return (this.arrayField[y][x].getPower() == GameEnums.FRUITS_POWER.OMEGA_SPHERE);
    }

    // ------------
    // Getter for input
    public boolean isClickable(int x, int y) {
        return this.arrayField[y][x].canBeSwapped();
    }

    public void setTolerance() {
        this.toleranceMode = true;
    }

    // ------------
    // Getters for animations
    public List<SpaceCoors> getTrulyDestroyedFruitsCoors() {
        return toBeDestroyedFruitsChecker.getList();
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

}
