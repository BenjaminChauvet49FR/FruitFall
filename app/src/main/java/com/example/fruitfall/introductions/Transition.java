package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;

public abstract class Transition {

    // array of values between 0 and 1;
    protected float[][] spaces;

    public Transition() {
        this.spaces = new float[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        boolean refitNeeded = this.initialize();
        if (refitNeeded) {
            int x, y;
            for (y = 0; y < Constants.FIELD_YLENGTH; y ++) {
                for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                    if (this.spaces[y][x] < 0) {
                        this.spaces[y][x] = 0;
                    } else if (this.spaces[y][x] > 1) {
                        this.spaces[y][x] = 1;
                    }
                }
            }
        }
    }

    // Array x, y should be filled with (theorically) 0..1 values here.
    // Should there be values outside of the range, they must be declared.
    // Otherwise, all values are assumed to be 0..1.
    protected abstract boolean initialize();

    public float getProgressThreshold(int x, int y) {
        return this.spaces[y][x];
    }

}
