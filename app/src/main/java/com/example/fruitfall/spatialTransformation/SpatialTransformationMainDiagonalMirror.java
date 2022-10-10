package com.example.fruitfall.spatialTransformation;

import com.example.fruitfall.structures.SpaceCoors;

public class SpatialTransformationMainDiagonalMirror extends SpatialTransformation {

    public SpatialTransformationMainDiagonalMirror(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    @Override
    public SpaceCoors transform(int xCoors, int yCoors) {
        return new SpaceCoors(xCenterTimes2-(x1+(y2-yCoors)), y1+(xCoors-x1));
    }
}
