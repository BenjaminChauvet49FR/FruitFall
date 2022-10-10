package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;
import com.example.fruitfall.level.LevelManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionLineCrossing extends Transition {
    @Override
    protected boolean initialize() {
        int x, y;
        // LevelManager.currentLevelWidth() and LevelManager.currentLevelHeight() are not good because of levels with different width and height and rotations... should be corrected elsewhere ! (TODO)
        SpatialTransformation transformation = this.getSpatialTransformation(0, 0, Constants.FIELD_XLENGTH-1, Constants.FIELD_YLENGTH-1);
        final int totalSteps = (Constants.FIELD_YLENGTH + Constants.FIELD_XLENGTH) * 2;
        for (y = 0; y < Constants.FIELD_YLENGTH; y ++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                if (y % 2 == 0) {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, x, y, (float) (y + 1 + x * 3) / totalSteps);
                } else {
                    SpatialTransformation.affectWithTransformation(this.spaces, transformation, Constants.FIELD_XLENGTH-1-x, y, (float) (y + 1 + x * 3) / totalSteps);
                }
            }
        }
        return false;
    }
}
