package com.example.fruitfall.introductions;

public class TransitionGrid extends Transition {
    @Override
    // TODO only did it for a 10x10 grid !
    protected boolean initialize() {
        float value = (float)0.0;
        // TODO faire une version miroitée
        for (int i = 0 ; i <= 4 ; i++) {
            for (int j = 0; j <= 4; j++) {
                this.spaces[2 * i][2 * j] = value; // Up to down
                this.spaces[2 * j+1][2 * i] = value+(float)0.2;
                this.spaces[9-(2 * i)][9-(2 * j)] = value+(float)0.4;
                this.spaces[8-(2 * j)][9-(2 * i)] = value+(float)0.6;
            }
            value += 0.08;
        }
        return false;
    }
    @Override
    public float relativeTransitionLength() {return (float)1.5;}
}