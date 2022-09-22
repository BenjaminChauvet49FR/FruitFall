package com.example.fruitfall.spatialTransformation;

import com.example.fruitfall.SpaceCoors;

public class SpatialTransformationHorizMirror extends SpatialTransformation {

    public SpatialTransformationHorizMirror(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    @Override
    public SpaceCoors transform(int xCoors, int yCoors) {
        return new SpaceCoors(this.xCenterTimes2-xCoors, yCoors);
    }
}
