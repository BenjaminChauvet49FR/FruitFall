package com.example.fruitfall;


import com.example.fruitfall.level.LevelData;
import com.example.fruitfall.spaces.EmptySpace;
import com.example.fruitfall.spaces.Fruit;
import com.example.fruitfall.spaces.OmegaSphere;
import com.example.fruitfall.spaces.SpaceFiller;
import com.example.fruitfall.spaces.VoidSpace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameHandler {

    public static final int EMPTY_FRUIT = -2;
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
    private IntChecker spawnFruitsChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, EMPTY_FRUIT);

    private IntChecker horizAlignmentChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);
    private IntChecker vertAlignmentChecker = new IntChecker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH, 0);

    private final int RESOURCES_NUMBER_FRUITS = 8;
    private int[] randomIndex = new int[RESOURCES_NUMBER_FRUITS];
    private int score;
    private int thisMoveComboCoefficient;
    private int thisMoveFruitsDestroyedByFall;
    private int collectedFruits;
    private String title;

    public GameTimingHandler gth;  // TODO le passer en privé et le rendre accessible par getters...

    public GameHandler() {
        this.arrayField = new SpaceFiller[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        gth = new GameTimingHandler(this);
    }

    // ----------------------------
    // Misc lil methods

    public int getFruit(int x, int y) {
        return this.arrayField[y][x].getFruit();
    }

    // Precondition : x,y supposed to be a fruit
    private boolean gotAlignment(int x, int y, int x2, int y2, int x3, int y3) {
        return (this.getFruit(x, y) == this.getFruit(x2, y2) && this.getFruit(x, y) == this.getFruit(x3, y3));
    }

    public boolean hasFruit(int x, int y) {
        return (this.arrayField[y][x] instanceof Fruit);
    }

    private boolean validSwap(int x, int y) {
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

    public void initializeGrid(LevelData ld) {
        // Clear everything !
        alignedFruitsChecker.clear();
        toBeDestroyedFruitsChecker.clear();
        fallingElementsChecker.clear();
        scoreDestructionFallChecker.clear();
        spawnFruitsChecker.clear();
        horizAlignmentChecker.clear();
        vertAlignmentChecker.clear();
        this.score = 0;
        this.collectedFruits = 0;
        this.thisMoveComboCoefficient = 1;
        this.thisMoveFruitsDestroyedByFall = 0;

        int x, y;
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
        // Random fruits
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
        boolean shouldGainSpawn;
        for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                if (ld.getData(x, y) == GameEnums.SPACE_DATA.FRUIT) {
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

    public void triggerSwap(int x1, int y1, int x2, int y2) {
        this.gth.stopSwap();
        SpaceFiller tmp = this.arrayField[y1][x1];
        this.arrayField[y1][x1] = this.arrayField[y2][x2];
        this.arrayField[y2][x2] = tmp;
        if (this.validSwap(x1, y1) || this.validSwap(x2, y2)) {
            this.performStableCheck();
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
        this.alignedFruitsChecker.clear();
        this.scoreDestructionFallChecker.clear(); // Don't reinitialize the jackpot count, please ;)

        // Check all horizontal and vertical fruits, and set them for destruction
        this.horizAlignmentChecker.clear();
        this.vertAlignmentChecker.clear();
        int xAft, yAft, currentFruit; // x After, y After last fruit
        int xx, yy;
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
                            this.createSpecialFruit(xx, y, new Fruit(this.getFruit(x, y), GameEnums.FRUITS_POWER.FIRE));
                        }
                        if (numberAlignTransversal >= 5) {
                            xInter = xx;
                        }
                    }
                }
                if (xInter == -1 && numberAlign == 4) {
                    this.createSpecialFruit(x+1, y, new Fruit(this.getFruit(x, y), GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING));
                    // TODO : change position !
                }
            }
            if (numberAlign >= 5) {
                this.createSpecialFruit(x+(numberAlign-1)/2, y, new OmegaSphere());
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
                    this.createSpecialFruit(x, y+1, new Fruit(this.getFruit(x, y), GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING));
                    // TODO : change position !
                }
            }
            if (numberAlign == 5) {
                this.createSpecialFruit(x, y+(numberAlign-1)/2, new OmegaSphere());
                // TODO Space already taken ? Too bad !
            }
        }

        // Score management !
        int scoreAmount;
        for (SpaceCoors coors : this.toBeDestroyedFruitsChecker.getList()) {
            this.thisMoveFruitsDestroyedByFall++;
            scoreAmount = (this.thisMoveFruitsDestroyedByFall+2)/3;
            this.scoreDestructionFallChecker.add(coors.x, coors.y, scoreAmount);
            this.score += scoreAmount;
            this.collectedFruits++;
        }

        if (this.toBeDestroyedFruitsChecker.getList().isEmpty()) {
            // Nothing new destroyed : move on.
            this.thisMoveComboCoefficient = 1;
            this.thisMoveFruitsDestroyedByFall = 0;
            this.gth.endAllFalls();
        } else {
            // Fall time !
            this.fullyCheckFallingFruitsInStableCheck();
            this.gth.startDestruction();
        }
    }

    private void createSpecialFruit(int x, int y, SpaceFiller spaceFiller) {
        this.arrayField[y][x] = spaceFiller;
        this.toBeDestroyedFruitsChecker.remove(x, y);
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
        }
        this.spawnFruitsChecker.clear();

        // Change future array to array
        int xNew, yNew;
        for (SpaceCoors coorsNew : newFallingFruitsCoors) {
            xNew = coorsNew.x;
            yNew = coorsNew.y;
            this.arrayField[yNew][xNew] = this.arrayFutureField[yNew][xNew];
        }

        // Now, check again falling fruits.
        this.fullyCheckFallingFruits();

        boolean newFall = !this.fallingElementsChecker.getList().isEmpty();

        if (newFall) {
            this.gth.startFall();
        } else {
            this.thisMoveComboCoefficient++;
            this.performStableCheck();
        }
    }

    private void fullyCheckFallingFruits() {
        int xCheck, yCheck;
        for (yCheck = 0 ; yCheck < Constants.FIELD_YLENGTH ; yCheck++) {
            for (xCheck = 0 ; xCheck < Constants.FIELD_XLENGTH ; xCheck++) {
                if (this.arrayField[yCheck][xCheck] instanceof EmptySpace) {
                    this.handleNewFallingFruitsAndPotentiallySpawn(xCheck, yCheck);
                }
            }
        }
    }

    private void fullyCheckFallingFruitsInStableCheck() {
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
        return this.randomIndex[indexFieldFruit];
    }

    public int getRandomFruitFromCoors(int x, int y) {
        return this.randomIndex[this.arrayField[y][x].getFruit()];
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

    public int scoreSpace(int x, int y) {
        return (this.scoreDestructionFallChecker.get(x, y));
    }

    public List<SpaceCoors> getContributingSpacesScore() {
        return this.scoreDestructionFallChecker.getList();
    }

    public boolean hasOmegaSphere(int x, int y) {
        return (this.arrayField[y][x] instanceof OmegaSphere);
    }

    // ------------
    // Getter for input
    public boolean isClickable(int x, int y) {
        return this.arrayField[y][x].canBeSwapped();
    }

    // ------------
    // Getters for animations
    public List<SpaceCoors> getTrulyDestroyedFruitsCoors() {
        return toBeDestroyedFruitsChecker.getList();
    }
}
