package com.example.fruitfall.spatialTransformation;

import com.example.fruitfall.SpaceCoors;

public class SpatialTransformationRotationCW extends SpatialTransformation {

    public SpatialTransformationRotationCW(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    @Override
    public SpaceCoors transform(int xCoors, int yCoors) {
        return new SpaceCoors(x1+(y2-yCoors), y1+(xCoors-x1));
    }
}
