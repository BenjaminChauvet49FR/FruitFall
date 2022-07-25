package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;

import java.util.Random;

public class TransitionLineHorizUD extends Transition {
    @Override
    protected boolean initialize() {
        int x, y;
        final int totalSteps = (Constants.FIELD_YLENGTH + Constants.FIELD_XLENGTH) * 2;
        for (y = 0; y < Constants.FIELD_YLENGTH; y ++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                if (y % 2 == 0) {
                    this.spaces[y][x] = (float) (y + 1 + x * 3) / totalSteps;
                } else {
                    this.spaces[y][Constants.FIELD_XLENGTH-1-x] = (float) (y + 1 + x * 3) / totalSteps;
                }
            }
        }
        return false;
    }
}
