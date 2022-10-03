package com.example.fruitfall.introductions;

import static com.example.fruitfall.Constants.FIELD_XLENGTH;
import static com.example.fruitfall.Constants.FIELD_YLENGTH;

import com.example.fruitfall.Constants;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionSwirl extends Transition {

    @Override
    protected boolean initialize() {
        float ratioXOnY,value;
        int x, y;
        SpatialTransformation trans = this.getSpatialTransformation(0, 0, FIELD_XLENGTH-1, FIELD_YLENGTH-1);

        // TODO for now, only for grids with max height and width (and ideally even width and height, due to potential rotations)
        final float antiHalfPi = (float) (2.0/Math.PI);
        final float xIndexCenter = Constants.FIELD_XLENGTH/(float)2.0;
        final float yIndexCenter = Constants.FIELD_YLENGTH/(float)2.0;
        final int yHalf = Math.floorDiv(Constants.FIELD_YLENGTH, 2);
        for (y = 0; y < yHalf; y ++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                // Credits for atan : https://koor.fr/Java/API/java/lang/Math/atan__double.wp
                ratioXOnY = (float) ((float) (x+0.5-xIndexCenter)/(yIndexCenter-(y+0.5)));
                value = (float) ((Math.atan(ratioXOnY) * (antiHalfPi) + 1) /4.0); // 0 to 0.5
                SpatialTransformation.affectWithTransformation(this.spaces, trans, x, y, value);
            }
        }
        // TODO a mid line for odd-height stuff is missing
        for (y = yHalf; y < Constants.FIELD_YLENGTH; y ++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                ratioXOnY = (float) ((float) (x+0.5-xIndexCenter)/((y+0.5)-yIndexCenter));
                value = (float) ((Math.atan(ratioXOnY) * (antiHalfPi) + 1) /4.0 + 0.5); // 0.5 + 1
                SpatialTransformation.affectWithTransformation(this.spaces, trans, Constants.FIELD_XLENGTH-1-x, y, value);
            }
        }

        return true;
    }

}
