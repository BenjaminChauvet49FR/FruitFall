package com.example.fruitfall;


import java.util.Random;

public class GameHandler {
    public static int FIELD_YLENGTH = 10;
    public static int FIELD_XLENGTH = 10;
    private int fruitsNumber;
    private int[][] arrayFruit;

    public GameHandler(int fruitsNumber) {
        this.fruitsNumber = fruitsNumber;
        this.arrayFruit = new int[FIELD_YLENGTH][FIELD_YLENGTH];
        int x, y;
        Random rand = new Random();
        for (y = 0 ; y < FIELD_YLENGTH ; y++) {
            for (x = 0; x < FIELD_XLENGTH; x++) {
                this.arrayFruit[y][x] = rand.nextInt(this.fruitsNumber);
            }
        }
    }

    public int getFruit(int x, int y) {
        return this.arrayFruit[y][x];
    }

    public void triggerSwap(int x1, int y1, int x2, int y2) {
        int tmp = this.arrayFruit[y1][x1];
        this.arrayFruit[y1][x1] = this.arrayFruit[y2][x2];
        this.arrayFruit[y2][x2] = tmp;
    }

}
