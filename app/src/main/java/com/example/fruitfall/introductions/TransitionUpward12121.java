package com.example.fruitfall.introductions;

import static com.example.fruitfall.Constants.FIELD_XLENGTH;
import static com.example.fruitfall.Constants.FIELD_YLENGTH;

import com.example.fruitfall.Constants;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

public class TransitionUpward12121 extends Transition {

    // IMPORTANT : transition dedicated to boards with at least one dimension with 10 spaces

    @Override
    protected boolean initialize() {
        int y;
        float ratio = 0;
        final float progress = (float) 1/(Constants.FIELD_YLENGTH+2);
        SpatialTransformation trans = this.getSpatialTransformation(0, 0, FIELD_XLENGTH-1, FIELD_YLENGTH-1);

        for (y = Constants.FIELD_YLENGTH-1; y >= 0; y--) {
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 1, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 3, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 6, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 8, y, ratio);
            ratio += progress;
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 0, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 2, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 4, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 5, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 7, y, ratio);
            SpatialTransformation.affectWithTransformation(this.spaces, trans, 9, y, ratio);
        }
        return true;
    }
}
