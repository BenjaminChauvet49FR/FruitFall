package com.example.fruitfall;


import com.example.fruitfall.level.LevelData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameHandler {

    public static final int VOID = -1;
    public static final int EMPTY_FRUIT = -2;
    private static final int DUMMY_IMPOSSIBLE_SCORE = 0;
    private int fruitsNumber;
    private int[][] arrayFruit;
    private Checker alignedFruitsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH); // Can ONLY contain coordinates of spaces with fruits
    private Checker toBeDestroyedFruitsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private Checker fallingFruitsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private Checker movedSinceLastStableChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private Checker movedSinceLastUnstableChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private boolean[][] arrayShouldFruitsBeSpawned = new boolean[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private SpaceCoors[][] arrayTeleporterCorrespondingExit = new SpaceCoors[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH]; // IMPORTANT : "out" array contains coors of corresponding "in" and vice-versa.
    private SpaceCoors[][] arrayTeleporterCorrespondingEntrance = new SpaceCoors[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private int[][] arrayFutureFruits = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
    private IntChecker scoreDestructionFallChecker = new IntChecker(Constants.FIELD_XLENGTH,Constants.FIELD_YLENGTH, DUMMY_IMPOSSIBLE_SCORE);
    private IntChecker spawnFruitsChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, EMPTY_FRUIT);
    private final int RESOURCES_NUMBER_FRUITS = 8;
    private int[] randomIndex = new int[RESOURCES_NUMBER_FRUITS];
    private int score;
    private int comboCoefficient;
    private int destroyedFruitsThisMove;
    private int collectedFruits;
    private String title;

    public GameTimingHandler gth;  // TODO le passer en privé et le rendre accessible par getters...

    public GameHandler() {
        this.arrayFruit = new int[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        gth = new GameTimingHandler(this);
    }

    // ----------------------------
    // Misc lil methods

    public int getFruit(int x, int y) {
        return this.arrayFruit[y][x];
    }

    private boolean gotAlignment(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (this.arrayFruit[y1][x1] == this.arrayFruit[y2][x2]) && (this.arrayFruit[y1][x1] == this.arrayFruit[y3][x3]);
    }

    public boolean hasFruit(int x, int y) {
        return (this.arrayFruit[y][x] != VOID && this.arrayFruit[y][x] != EMPTY_FRUIT);
    }

    public boolean hasSelectionnableFruit(int x, int y) {
        return this.hasFruit(x, y);
        // TODO we can have "fruits under glass" or more realistic fruits under lock.
    }

    private boolean gotAlignment(int x, int y) {
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
                    if (gotAlignment(x, y, x, y+1, x, y+2)){
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
    SOMETHING IS SUPPOSED TO FALL
     */
    private SpaceCoors getCoorsFruitRightAbove(int x, int y) {
        SpaceCoors supposedSource = this.arrayTeleporterCorrespondingEntrance[y][x];
        if (supposedSource != null) {
            if (!this.hasFruit(supposedSource.x, supposedSource.y)) {
                return null;
            }
            return supposedSource;
        }
        if (y == 0) {
            return null;
        }
        if (!this.hasFruit(x, y-1)) {
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
            return supposedDest;
        }
        if (y == Constants.FIELD_YLENGTH-1) {
            return null;
        }
        if (this.arrayFruit[y+1][x] == VOID) {
            return null;
        }
        return new SpaceCoors(x, y+1);
    }

    // ----------------------------
    // Initialization

    public void initializeGrid(LevelData ld) {
        // Clear everything !
        alignedFruitsChecker.clear();
        toBeDestroyedFruitsChecker.clear();
        fallingFruitsChecker.clear();
        movedSinceLastStableChecker.clear();
        movedSinceLastUnstableChecker.clear();
        scoreDestructionFallChecker.clear();
        spawnFruitsChecker.clear();
        this.score = 0;
        this.collectedFruits = 0;
        this.comboCoefficient = 1;

        int x, y;
        int currentFruit;
        Random rand = new Random();

        // Read data for everything
        this.fruitsNumber = ld.getFruitColours();
        this.title = ld.getTitle();

        // Read data for fruits
        int[] arrayNTI = new int[RESOURCES_NUMBER_FRUITS]; // NTI = not taken indexes
        int i;
        for (i = 0 ; i < RESOURCES_NUMBER_FRUITS ; i++) {
            arrayNTI[i] = i;
        }
        int numberChosen = 0;
        // Forced part
        if (ld.getForcedIndexes() != null) {
            for (Integer indexSelected : ld.getForcedIndexes()) {
                this.randomIndex[numberChosen] = indexSelected;
                arrayNTI[indexSelected] = -1;
                numberChosen++;
            }
        }
        // Random part
        int numberToMeet;
        int indexSelected;
        int numberMet;
        while (numberChosen < this.fruitsNumber) {
            numberToMeet = rand.nextInt(RESOURCES_NUMBER_FRUITS-numberChosen);
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
            this.randomIndex[numberChosen] = arrayNTI[indexSelected];
            arrayNTI[indexSelected] = -1;
            numberChosen++;
        }


        // Read data for array
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                if (ld.getData(x, y) == GameEnums.SPACE_DATA.FRUIT) {
                    // Generate fruit
                    currentFruit = rand.nextInt(this.fruitsNumber);
                    this.arrayFruit[y][x] = currentFruit;
                    if (x >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x-1, y, x-2, y);
                    }
                    if (y >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x, y-1, x, y-2);
                    }
                    // Spawn
                    this.arrayShouldFruitsBeSpawned[y][x] = (((y == 0) || (this.arrayFruit[y-1][x] == VOID)) && (this.arrayTeleporterCorrespondingEntrance[y][x] == null));
                } else {
                    // Not a space ; still need to initialize arrays.
                    this.arrayFruit[y][x] = VOID;
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
                this.arrayFruit[y][x] = rand.nextInt(this.fruitsNumber);
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

    public void triggerSwap(int x1, int y1, int x2, int y2) {
        this.gth.stopSwap();
        int tmp = this.arrayFruit[y1][x1];
        this.arrayFruit[y1][x1] = this.arrayFruit[y2][x2];
        this.arrayFruit[y2][x2] = tmp;
        // Si cela provoque un alignement : OK.
        if (this.gotAlignment(x1, y1) || this.gotAlignment(x2, y2)) {
            // TODO : destroy fruits correctly
            this.movedSinceLastStableChecker.add(x1, y1);
            this.movedSinceLastStableChecker.add(x2, y2);
            this.performStableCheck();

        } else {
            this.gth.startBackSwap(x1, y1, x2, y2);
        }
    }

    public void triggerBackSwap(int x1, int y1, int x2, int y2) {
        this.gth.stopSwap();
        int tmp = this.arrayFruit[y1][x1];
        this.arrayFruit[y1][x1] = this.arrayFruit[y2][x2];
        this.arrayFruit[y2][x2] = tmp;
        this.gth.endSwap();
    }

    public void triggerDestruction() {
        this.gth.startFall();
    }

    /*
    Check method !
    When fruits should be checked for destruction or creation.
    IMPORTANT : arrayFruit is NOT updated here for the purpose of drawing !
    */
    private void performStableCheck() {
        int x, y;
        this.toBeDestroyedFruitsChecker.clear();
        this.alignedFruitsChecker.clear();
        this.scoreDestructionFallChecker.clear(); // Don't reinitialize the jackpot count, please ;)

        // Check for destruction
        this.destroyedFruitsThisMove = 0;
        // TODO shuffle the order of destruction, or reorder it by reading order.
        for (SpaceCoors coors : this.movedSinceLastStableChecker.getList()) {
            x = coors.x;
            y = coors.y;
            this.testAndAlertAboutDestroyedAndCreatedFruits(x, y);
        }
        this.movedSinceLastStableChecker.clear();

        if (this.toBeDestroyedFruitsChecker.getList().isEmpty()) {
            // Nothing new destroyed : move on.
            this.comboCoefficient = 1;
            this.scoreDestructionFallChecker.clear();
            this.gth.endAllFalls();
        } else {
            // Decide which fruits should fall and be spawned
            for (SpaceCoors coors : this.toBeDestroyedFruitsChecker.getList()) {
                this.handleNewFallingFruitsAndPotentiallySpawn(coors.x, coors.y);
            }
            this.gth.startDestruction();
        }
    }

    /*
    Tells from one space which fruits are supposed to fall and add them to "fallingFruitsChecker"
    Tells also where new fruits should be spawned !
     */
    private void handleNewFallingFruitsAndPotentiallySpawn(int xFallInto, int yFallInto) {
        SpaceCoors coorsToFall, coorsHead;
        coorsToFall = this.getCoorsFruitRightAbove(xFallInto, yFallInto);
        coorsHead = new SpaceCoors(xFallInto, yFallInto); // Note : the head is itself if there is no fruit above it

        // Climb up to declare all falling spaces from this one
        while (coorsToFall != null) {
            this.fallingFruitsChecker.add(coorsToFall.x, coorsToFall.y);
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
    Put fruits caused by alignment into destruction line.
    Create new ones, too.
    */
    // TODO inexact : there may be very long alignments.
    private void testAndAlertAboutDestroyedAndCreatedFruits(int x, int y) {
        // Add to the "this.listDestroyedFruits".
        int formerAlignedLength = this.alignedFruitsChecker.getList().size();
        this.testAndAlertAboutAlignedFruitsAroundSpace(x, y);
        SpaceCoors coorsND; // Newly destroyed
        int xND, yND;
        int scoreAmount;
        for (int i = formerAlignedLength ; i < this.alignedFruitsChecker.getList().size() ; i++) {
            // TODO : If too funky, create new fruit.
            coorsND = this.alignedFruitsChecker.getList().get(i);
            xND = coorsND.x;
            yND = coorsND.y;
            if (this.toBeDestroyedFruitsChecker.add(xND, yND)) {
                this.destroyedFruitsThisMove++;
                // Add score
                scoreAmount = ((this.destroyedFruitsThisMove + 2) / 3) * (this.comboCoefficient);
                this.scoreDestructionFallChecker.add(xND, yND, scoreAmount);
                this.score += scoreAmount;
                this.collectedFruits++;
            }
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
            this.arrayFruit[coorsDestroy.y][coorsDestroy.x] = EMPTY_FRUIT;
        }
        this.toBeDestroyedFruitsChecker.clear();

        // Shift all falling fruits "below" in futur array
        int xFall, yFall;
        for (SpaceCoors coorsFall : this.fallingFruitsChecker.getList()) {
            xFall = coorsFall.x;
            yFall = coorsFall.y;
            belowCoors = this.getCoorsFruitRightBelow(xFall, yFall);
            this.arrayFutureFruits[belowCoors.y][belowCoors.x] = this.arrayFruit[yFall][xFall];
            this.arrayFruit[yFall][xFall] = EMPTY_FRUIT;
            newFallingFruitsCoors.add(new SpaceCoors(belowCoors.x, belowCoors.y));
        }
        this.fallingFruitsChecker.clear();

        // Fruits in the spawning line
        int xSpawn, ySpawn;
        for (SpaceCoors coorsSpawn : this.spawnFruitsChecker.getList()) {
            xSpawn = coorsSpawn.x;
            ySpawn = coorsSpawn.y;
            this.arrayFruit[ySpawn][xSpawn] = this.spawnFruitsChecker.get(xSpawn, ySpawn);
            this.movedSinceLastStableChecker.add(xSpawn, ySpawn);
            this.movedSinceLastUnstableChecker.add(xSpawn, ySpawn);
        }
        this.spawnFruitsChecker.clear();

        // Change future array to array
        int xNew, yNew;
        for (SpaceCoors coorsNew : newFallingFruitsCoors) {
            xNew = coorsNew.x;
            yNew = coorsNew.y;
            this.arrayFruit[yNew][xNew] = this.arrayFutureFruits[yNew][xNew];
            this.movedSinceLastStableChecker.add(xNew, yNew);
            this.movedSinceLastUnstableChecker.add(xNew, yNew);
        }

        // All spaces in this.movedSinceLastUnstableChecker should contain fruits whose state is unchecked since last passage in this point
        // Now, check again falling fruits.
        for(SpaceCoors coorsNewFruit : this.movedSinceLastUnstableChecker.getList()) {
            belowCoors = this.getCoorsFruitRightBelow(coorsNewFruit.x, coorsNewFruit.y);
            if (belowCoors != null && this.arrayFruit[belowCoors.y][belowCoors.x] == EMPTY_FRUIT) {
                this.handleNewFallingFruitsAndPotentiallySpawn(belowCoors.x, belowCoors.y);
            }
        }
        this.movedSinceLastUnstableChecker.clear();

        boolean newFall = !this.fallingFruitsChecker.getList().isEmpty();

        if (newFall) {
            this.gth.startFall();
        } else {
            this.comboCoefficient++;
            this.performStableCheck();
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

    // ------------
    // Getters for drawing

    public boolean isNotFallingFruit(int x, int y) {
        return (!this.fallingFruitsChecker.get(x, y));
    }

    public boolean isNotDestroyedBeforeFall(int x, int y) {
        return (!this.toBeDestroyedFruitsChecker.get(x, y));
    }
    public SpaceCoors getDestination(int x, int y) {
        return this.arrayTeleporterCorrespondingExit[y][x];
    }

    // List of (x, y) spaces with fruits that are falling from (x, y) to the next space (usually x, y+1)
    public List<SpaceCoors> getFallingFruitsCoors() {
        return this.fallingFruitsChecker.getList();
    }

    public int spawn(int x, int y) {
        return (this.spawnFruitsChecker.get(x, y));
    }

    // List of (x, y) spaces with fruits that are spawning into
    public List<SpaceCoors> getSpawningFruitsCoors() {
        return this.spawnFruitsChecker.getList();
    }

    public int getRandomFruit(int indexFieldFruit) {
        return this.randomIndex[indexFieldFruit];
    }

    public int getRandomFruitFromCoors(int x, int y) {
        return this.randomIndex[this.arrayFruit[y][x]];
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

    public int scoreSpace(int x, int y) {
        return (this.scoreDestructionFallChecker.get(x, y));
    }

    public List<SpaceCoors> getContributingSpacesScore() {
        return this.scoreDestructionFallChecker.getList();
    }

    // ------------
    // Getters for animations
    public List<SpaceCoors> getdestroyedFruitsCoors() {
        return this.toBeDestroyedFruitsChecker.getList();
    }
}
