package com.example.fruitfall.animations;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameTimingHandler;

public class SpaceAnimationFruitShrinking extends SpaceAnimation {

    private int imageFruit;

    public SpaceAnimationFruitShrinking(int _imageFruit) {
        super(Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
        this.imageFruit = _imageFruit;
    }

    public int getImageFruit() {
        return this.imageFruit;
    }
}
