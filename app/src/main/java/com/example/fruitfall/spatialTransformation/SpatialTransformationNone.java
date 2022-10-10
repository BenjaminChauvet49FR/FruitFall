package com.example.fruitfall.spatialTransformation;

import com.example.fruitfall.structures.SpaceCoors;

public class SpatialTransformationNone extends SpatialTransformation {

    public SpatialTransformationNone(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    @Override
    public SpaceCoors transform(int xCoors, int yCoors) {
        return new SpaceCoors(xCoors, yCoors);
    }
}
