package com.example.fruitfall.introductions;

import static com.example.fruitfall.Constants.FIELD_NUMBER_SPACES;

import com.example.fruitfall.Constants;

public class TransitionDiagonal extends Transition {

    @Override
    protected boolean initialize() {
        float step = new Float(1.0) /FIELD_NUMBER_SPACES;
        float value = 0;

        int x, y, i;
        for (i = 0 ; i < Constants.FIELD_XLENGTH ; i++) { // Must be square
            this.spaces[i][i] = value;
            value += step;
        }
        for (y = Constants.FIELD_YLENGTH-2 ; y >= 0 ; y--) {
            if (y % 2 == 0) {
                for (x = Constants.FIELD_XLENGTH-1 ; x > y ; x--) {
                    this.spaces[y][x] = value;
                    value += step;
                }
            } else {
                for (x = y+1 ; x < Constants.FIELD_XLENGTH ; x++) {
                    this.spaces[y][x] = value;
                    value += step;
                }
            }
        }
        int parityLastLine = (Constants.FIELD_YLENGTH-1) % 2;
        for (y = 1 ; y <= Constants.FIELD_YLENGTH-1 ; y++) {
            if (y % 2 == parityLastLine) {
                for (x = 0 ; x < y ; x++) {
                    this.spaces[y][x] = value;
                    value += step;
                }
            } else {
                for (x = y-1 ; x >=0 ; x--) {
                    this.spaces[y][x] = value;
                    value += step;
                }
            }
        }
        return true;
    }

    @Override
    public float relativeTransitionLength() {return (float)2.0;}
}
