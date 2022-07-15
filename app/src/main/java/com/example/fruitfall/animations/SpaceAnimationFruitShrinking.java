package com.example.fruitfall.animations;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameTimingHandler;

public class SpaceAnimationFruitShrinking extends SpaceAnimation {

    private int imageFruit;
    private boolean alsoRotating;

    public SpaceAnimationFruitShrinking(int _imageFruit, boolean _alsoRotating) {
        super(Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
        this.imageFruit = _imageFruit;
        this.alsoRotating = _alsoRotating;
    }

    public int getImageFruit() {
        return this.imageFruit;
    }
    public boolean isAlsoRotating() { return this.alsoRotating; }
}
