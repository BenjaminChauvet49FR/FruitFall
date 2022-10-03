package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;

import java.util.Arrays;
import java.util.Random;

public class TransitionRandomFromLeft extends Transition {

    // IMPORTANT : from left to right, and with forced dimensions
    @Override
    protected boolean initialize() {
        int y;
        Random rand = new Random();
        for (y = 0; y < Constants.FIELD_YLENGTH; y ++) {
            this.spaces[y][0] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0f, 0.1f, 0.6f));
            this.spaces[y][1] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0f, 0.2f, 0.7f));
            this.spaces[y][2] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0f, 0.3f, 0.8f));
            this.spaces[y][3] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0f, 0.4f, 0.9f));
            this.spaces[y][4] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0f, 0.5f, 1f));
            this.spaces[y][5] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0.1f, 0.6f, 1f));
            this.spaces[y][6] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0.2f, 0.7f, 1f));
            this.spaces[y][7] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0.3f, 0.8f, 1f));
            this.spaces[y][8] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0.4f, 0.9f, 1f));
            this.spaces[y][9] = affineTransformation(rand.nextFloat(), Arrays.asList(0f, 0.5f, 1f), Arrays.asList(0.5f, 0.9f, 1f));
        }
        return true;
    }
}
