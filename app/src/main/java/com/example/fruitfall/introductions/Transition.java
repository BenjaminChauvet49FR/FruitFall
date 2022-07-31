package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;

import java.util.List;

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

    // NOTE : utilitary method
    // randSource values are supposed to be in strictly ascending order.
    // randTarget must have the same length as randSource.
    protected float affineTransformation(float randNumber, List<Float> randSource, List<Float> randTarget) {
        int last = randSource.size()-1;
        if (randNumber <= randSource.get(0)) {
            return randTarget.get(0);
        }
        if (randNumber > randSource.get(last)) {
            return randTarget.get(last);
        }
        for (int i = 0 ; i < last ; i++) { // TODO : okay for return within for loop ? (and several returns in general ?)
            if (randNumber > randSource.get(i)) {
                return randTarget.get(i) + (randTarget.get(i+1)-randTarget.get(i))*(randNumber- randSource.get(i))/(randSource.get(i+1) - randSource.get(i));
            }
        }
        return randTarget.get(last); // TODO : make exception ?
    }
}
