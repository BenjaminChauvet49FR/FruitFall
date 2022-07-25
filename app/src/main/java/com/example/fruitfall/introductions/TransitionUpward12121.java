package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;

public class TransitionUpward12121 extends Transition {


    @Override
    protected boolean initialize() {
        int y;
        float ratio = 0;
        final float progress = (float) 1/(Constants.FIELD_YLENGTH+2);
        for (y = Constants.FIELD_YLENGTH-1; y >= 0; y--) {
            this.spaces[y][1] = ratio;
            this.spaces[y][3] = ratio;
            this.spaces[y][6] = ratio;
            this.spaces[y][8] = ratio;
            ratio += progress;
            this.spaces[y][0] = ratio;
            this.spaces[y][2] = ratio;
            this.spaces[y][4] = ratio;
            this.spaces[y][5] = ratio;
            this.spaces[y][7] = ratio;
            this.spaces[y][9] = ratio;
        }
        return true;
    }
}
