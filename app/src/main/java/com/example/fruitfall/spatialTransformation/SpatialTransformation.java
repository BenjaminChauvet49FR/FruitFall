package com.example.fruitfall.spatialTransformation;

import com.example.fruitfall.SpaceCoors;

import java.util.Random;

public abstract class SpatialTransformation {

    protected int x1;
    protected int y1;
    protected int x2;
    protected int y2;

    protected int xCenterTimes2;
    protected int yCenterTimes2;

    abstract public SpaceCoors transform(int xCoors, int yCoors);

    // Spatial transformation of a portion of an array. All four indexes are within the transformation and must therefore be within the array.
    // Additionally, if the special transformation is a rotation (around center) or a diagonal mirror (around a line passing through the center) and the area is not square,
    // beware that the destination area will not be the same as the source area.
    public SpatialTransformation(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.xCenterTimes2 = x1+x2;
        this.yCenterTimes2 = y1+y2;
    }

    public static SpatialTransformation randomTransformation(int x1, int y1, int x2, int y2) {
        Random rand = new Random();
        int chance = rand.nextInt(4);
        switch (chance) {
            case 1 : return new SpatialTransformationHorizMirror(x1, y1, x2, y2);
            case 2 : return new SpatialTransformationUTurn(x1, y1, x2, y2);
            case 3 : return new SpatialTransformationMainDiagonalMirror(x1, y1, x2, y2);
            case 4 : return new SpatialTransformationRotationCW(x1, y1, x2, y2);
            default : return new SpatialTransformationNone(x1, y1, x2, y2);
        }
    }

}
