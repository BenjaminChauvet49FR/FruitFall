package com.example.fruitfall.introductions;

import com.example.fruitfall.level.LevelManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionSpiral extends Transition {

    // IMPORTANT : requires a square-sized field

    @Override
    protected boolean initialize() {
        int numberFieldSpaces = LevelManager.currentLevelHeight() * LevelManager.currentLevelWidth();
        float step = (float)1.0/numberFieldSpaces;
        float value = step;
        int x = -1, y = 0;
        int[] xDelta = {1, 0, -1, 0};
        int[] yDelta = {0, 1, 0, -1};
        int dirIndex = 0;
        int stepsLeftStart = LevelManager.currentLevelHeight();// TODO forces the field to be square
        int stepsLeft = stepsLeftStart;
        SpatialTransformation trans = this.getSpatialTransformation(0, 0, LevelManager.currentLevelWidth()-1, LevelManager.currentLevelHeight()-1);
        for (int i = 0 ; i < numberFieldSpaces ; i++) {
            x += xDelta[dirIndex];
            y += yDelta[dirIndex];
            value+=step;
            SpatialTransformation.affectWithTransformation(this.spaces, trans, x, y, value);
            stepsLeft--;
            if (stepsLeft == 0) {
                dirIndex++;
                if (dirIndex == 1) {
                    stepsLeftStart--;
                } else if (dirIndex == 3) {
                    stepsLeftStart--;
                } else if (dirIndex == 4) {
                    dirIndex = 0;
                }
                stepsLeft = stepsLeftStart;
            }
        }
        return true;
    }
}
