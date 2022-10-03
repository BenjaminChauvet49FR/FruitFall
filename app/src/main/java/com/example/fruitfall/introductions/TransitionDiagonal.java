package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;
import com.example.fruitfall.level.LevelData;
import com.example.fruitfall.level.LevelManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionDiagonal extends Transition {

    // IMPORTANT : requires a square-sized field

    @Override
    protected boolean initialize() {
        int numberFieldSpaces = LevelManager.currentLevelHeight() * LevelManager.currentLevelWidth();
        float step = (float) 1.0 /numberFieldSpaces;
        float value = 0;
        SpatialTransformation transformation = this.getSpatialTransformation(0, 0, LevelManager.currentLevelWidth()-1, LevelManager.currentLevelHeight()-1);
        int x, y, i;
        for (i = 0 ; i < LevelManager.currentLevelWidth() ; i++) { // Must be square
            SpatialTransformation.affectWithTransformation(this.spaces, transformation, i, i, value);
            value += step;
        }
        for (y = LevelManager.currentLevelHeight()-2 ; y >= 0 ; y--) {
            if (y % 2 == 0) {
                for (x = LevelManager.currentLevelWidth()-1 ; x > y ; x--) {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, x, y, value);
                    value += step;
                }
            } else {
                for (x = y+1 ; x < LevelManager.currentLevelWidth() ; x++) {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, x, y, value);
                    value += step;
                }
            }
        }
        int parityLastLine = (LevelManager.currentLevelHeight()-1) % 2;
        for (y = 1 ; y <= LevelManager.currentLevelHeight()-1 ; y++) {
            if (y % 2 == parityLastLine) {
                for (x = 0 ; x < y ; x++) {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, x, y, value);
                    value += step;
                }
            } else {
                for (x = y-1 ; x >=0 ; x--) {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, x, y, value);
                    value += step;
                }
            }
        }
        return true;
    }

    @Override
    public float relativeTransitionLength() {return (float)2.0;}
}
