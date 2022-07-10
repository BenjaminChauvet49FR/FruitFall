package com.example.fruitfall;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameHandler {
    public static int FIELD_YLENGTH = 10;
    public static int FIELD_XLENGTH = 10;
    private int fruitsNumber;
    private int[][] arrayFruit;
    private boolean[][] arrayGetAlignedFruit;
    private List<SpaceCoors> listGetAlignedFruit = new ArrayList<>();
    public GameTimingHandler gth = new GameTimingHandler(this); // TODO le passer en privé et le rendre accessible par getters...

    public GameHandler(int fruitsNumber) {
        this.arrayFruit = new int[FIELD_YLENGTH][FIELD_XLENGTH];
        this.arrayGetAlignedFruit = new boolean[FIELD_YLENGTH][FIELD_XLENGTH];
        this.shuffleGrid(fruitsNumber);
    }

    public int getFruit(int x, int y) {
        return this.arrayFruit[y][x];
    }

    private boolean gotAlignment(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (this.arrayFruit[y1][x1] == this.arrayFruit[y2][x2]) && (this.arrayFruit[y1][x1] == this.arrayFruit[y3][x3]);
    }

    private boolean gotAlignment(int x, int y) {
        if (x >= 1) {
            if (x >= 2) {
                if (gotAlignment(x, y, x-1, y, x-2, y)) {
                    return true;
                }
            }
            if (x <= FIELD_XLENGTH-2) {
                if (gotAlignment(x, y, x-1, y, x+1, y)) {
                    return true;
                }
                if (x <= FIELD_XLENGTH-3) {
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
            if (y <= FIELD_YLENGTH-2) {
                if (gotAlignment(x, y-1, x, y, x, y+1)){
                    return true;
                }
                if (y <= FIELD_YLENGTH-3) {
                    if (gotAlignment(x, y, x, y+1, x, y+2)){
                        return true;
                    }
                }
            }
        } else {
            if (gotAlignment(x, 0, x, 1, x, 2)){
                return true;
            }
        }
        return false;
    }

    // ----------------------------
    // Input

    public void inputSwap(int x1, int y1, int x2, int y2) {
        this.gth.startSwap(x1, y1, x2, y2);
    }

    // ----------------------------
    // End of transitions

    public void triggerSwap(int x1, int y1, int x2, int y2) {
        int tmp = this.arrayFruit[y1][x1];
        this.arrayFruit[y1][x1] = this.arrayFruit[y2][x2];
        this.arrayFruit[y2][x2] = tmp;
        // Si cela provoque un alignement : OK.
        if (this.gotAlignment(x1, y1) || this.gotAlignment(x2, y2)) {
            // TODO : destroy fruits correctly
            this.gth.startFall();
        } else {
            this.gth.startBackSwap(x1, y1, x2, y2);
        }
    }

    public void triggerBackSwap(int x1, int y1, int x2, int y2) {
        int tmp = this.arrayFruit[y1][x1];
        this.arrayFruit[y1][x1] = this.arrayFruit[y2][x2];
        this.arrayFruit[y2][x2] = tmp;
    }

    // ----------------------------
    // Initialization
    // Here, the grid is shuffled
    public void shuffleGrid(int fruitsNumber) {
        this.fruitsNumber = fruitsNumber;
        this.renewGetAlignedFruits();
        int x, y;
        int currentFruit;
        Random rand = new Random();
        for (y = 0 ; y < FIELD_YLENGTH ; y++) {
            for (x = 0; x < FIELD_XLENGTH; x++) {
                currentFruit = rand.nextInt(this.fruitsNumber);
                this.arrayFruit[y][x] = currentFruit;
                if (x >= 2) {
                    testAndAlertAboutAlignedFruits(x, y, x-1, y, x-2, y);
                }
                if (y >= 2) {
                    testAndAlertAboutAlignedFruits(x, y, x, y-1, x, y-2);
                }
            }
        }

        List<SpaceCoors> formerListGetAlignedFruit = new ArrayList<>();
        while(!this.listGetAlignedFruit.isEmpty()) {

            // Note : unfortunately, we need to copy coors one by one
            //formerListGetAlignedFruit = this.listGetAlignedFruit;
            formerListGetAlignedFruit.clear();
            for(SpaceCoors coors : this.listGetAlignedFruit) {
                formerListGetAlignedFruit.add(new SpaceCoors(coors.x, coors.y));
            }

            this.renewGetAlignedFruits();

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
                if (x >= 1) {
                    if (x >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x-1, y, x-2, y);
                    }
                    if (x <= FIELD_XLENGTH-2) {
                        testAndAlertAboutAlignedFruits(x-1, y, x, y, x+1, y);
                        if (x <= FIELD_XLENGTH-3) {
                            testAndAlertAboutAlignedFruits(x, y, x+1, y, x+2, y);
                        }
                    }
                } else {
                    testAndAlertAboutAlignedFruits(0, y, 1, y, 2, y);
                }
                if (y >= 1) {
                    if (y >= 2) {
                        testAndAlertAboutAlignedFruits(x, y, x, y-1, x, y-2);
                    }
                    if (y <= FIELD_YLENGTH-2) {
                        testAndAlertAboutAlignedFruits(x, y-1, x, y, x, y+1);
                        if (y <= FIELD_YLENGTH-3) {
                            testAndAlertAboutAlignedFruits(x, y, x, y+1, x, y+2);
                        }
                    }
                } else {
                    testAndAlertAboutAlignedFruits(x, 0, x, 1, x, 2);
                }


            }
        }
    }

    private void testAndAlertAboutAlignedFruits(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (gotAlignment(x1, y1, x2, y2, x3, y3)) {
            this.alertAboutAlignedFruits(x1, y1);
            this.alertAboutAlignedFruits(x2, y2);
            this.alertAboutAlignedFruits(x3, y3);
        }
    }

    // Warn that a space contains an "aligned fruit"
    private void alertAboutAlignedFruits(int x, int y) {
        if (!this.arrayGetAlignedFruit[y][x]) {
            this.arrayGetAlignedFruit[y][x] = true;
            this.listGetAlignedFruit.add(new SpaceCoors(x, y));
        }
    }

    private void renewGetAlignedFruits() {
        for (SpaceCoors coors : this.listGetAlignedFruit) {
            this.arrayGetAlignedFruit[coors.y][coors.x] = false;
        }
        this.listGetAlignedFruit.clear();

    }
}
