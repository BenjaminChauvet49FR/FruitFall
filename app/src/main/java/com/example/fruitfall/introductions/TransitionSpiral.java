package com.example.fruitfall.introductions;

import static com.example.fruitfall.Constants.FIELD_NUMBER_SPACES;
import static com.example.fruitfall.Constants.FIELD_XLENGTH;
import static com.example.fruitfall.Constants.FIELD_YLENGTH;

import com.example.fruitfall.Constants;
import com.example.fruitfall.SpaceCoors;
import com.example.fruitfall.spatialTransformation.SpatialTransformation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TransitionSpiral extends Transition {

    @Override
    protected boolean initialize() {
        float step = (float)1.0/FIELD_NUMBER_SPACES;
        float value = step;
        int x = -1, y = 0;
        int[] xDelta = {1, 0, -1, 0};
        int[] yDelta = {0, 1, 0, -1};
        int dirIndex = 0;
        int stepsLeftStart = FIELD_XLENGTH;// TODO forces the field to be square
        int stepsLeft = stepsLeftStart;
        SpaceCoors transCoors;
        SpatialTransformation trans = SpatialTransformation.randomTransformation(0, 0, FIELD_XLENGTH-1, FIELD_YLENGTH-1);
        for (int i = 0 ; i < FIELD_NUMBER_SPACES ; i++) {
            x += xDelta[dirIndex];
            y += yDelta[dirIndex];
            value+=step;
            transCoors = trans.transform(x, y);
            this.spaces[transCoors.y][transCoors.x] = value;
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
