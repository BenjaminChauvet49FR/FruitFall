package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;

import java.util.Random;

public class TransitionRandom extends Transition {
    @Override
    protected boolean initialize() {
        int x, y;
        Random rand = new Random();
        for (y = 0; y < Constants.FIELD_YLENGTH; y ++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                this.spaces[y][x] = rand.nextFloat();
            }
        }
        return false;
    }
}
