package com.example.fruitfall.introductions;

import com.example.fruitfall.level.LevelManager;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionSwirl extends Transition {

    // IMPORTANT : requires a square-sized field with even and even dimensions
    
    @Override
    protected boolean initialize() {
        float ratioXOnY,value;
        int x, y;
        SpatialTransformation trans = this.getSpatialTransformation(0, 0, LevelManager.currentLevelWidth()-1, LevelManager.currentLevelHeight()-1);

        final float antiHalfPi = (float) (2.0/Math.PI);
        final float xIndexCenter = LevelManager.currentLevelWidth()/(float)2.0;
        final float yIndexCenter = LevelManager.currentLevelHeight()/(float)2.0;
        final int yHalf = Math.floorDiv(LevelManager.currentLevelHeight(), 2);
        for (y = 0; y < yHalf; y ++) {
            for (x = 0; x < LevelManager.currentLevelWidth(); x++) {
                // Credits for atan : https://koor.fr/Java/API/java/lang/Math/atan__double.wp
                ratioXOnY = (float) ((float) (x+0.5-xIndexCenter)/(yIndexCenter-(y+0.5)));
                value = (float) ((Math.atan(ratioXOnY) * (antiHalfPi) + 1) /4.0); // 0 to 0.5
                SpatialTransformation.affectWithTransformation(this.spaces, trans, x, y, value);
            }
        }
        for (y = yHalf; y < LevelManager.currentLevelHeight(); y ++) {
            for (x = 0; x < LevelManager.currentLevelWidth(); x++) {
                ratioXOnY = (float) ((float) (x+0.5-xIndexCenter)/((y+0.5)-yIndexCenter));
                value = (float) ((Math.atan(ratioXOnY) * (antiHalfPi) + 1) /4.0 + 0.5); // 0.5 + 1
                SpatialTransformation.affectWithTransformation(this.spaces, trans, LevelManager.currentLevelWidth()-1-x, y, value);
            }
        }

        return true;
    }

}
