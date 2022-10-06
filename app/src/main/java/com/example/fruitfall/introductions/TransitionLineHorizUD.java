package com.example.fruitfall.introductions;

import com.example.fruitfall.level.LevelManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionLineHorizUD extends Transition {
    @Override
    protected boolean initialize() {
        int x, y;
        SpatialTransformation transformation = this.getSpatialTransformation(0, 0, LevelManager.currentLevelWidth()-1, LevelManager.currentLevelHeight()-1);
        final int totalSteps = (LevelManager.currentLevelHeight() + LevelManager.currentLevelWidth()) * 2;
        for (y = 0; y < LevelManager.currentLevelHeight(); y ++) {
            for (x = 0; x < LevelManager.currentLevelWidth(); x++) {
                if (y % 2 == 0) {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, x, y, (float) (y + 1 + x * 3) / totalSteps);
                } else {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, LevelManager.currentLevelWidth()-1-x, y, (float) (y + 1 + x * 3) / totalSteps);
                }
            }
        }
        return false;
    }
}
