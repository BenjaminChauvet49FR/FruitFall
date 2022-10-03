package com.example.fruitfall.spatialTransformation;

import com.example.fruitfall.SpaceCoors;

public class SpatialTransformationVertMirror extends SpatialTransformation {

    public SpatialTransformationVertMirror(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    @Override
    public SpaceCoors transform(int xCoors, int yCoors) {
        return new SpaceCoors(xCoors, this.yCenterTimes2-yCoors);
    }
}
