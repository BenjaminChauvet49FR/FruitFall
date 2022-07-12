package com.example.fruitfall.animations;

public abstract class SpaceAnimation {

    protected int frameLimit;
    protected int frameCount;

    protected SpaceAnimation(int _frameLimit) {
        frameCount = 0;
        frameLimit = _frameLimit;
    }

    public void progress() {
        frameCount++;
    }

    public boolean shouldBeDrawn() {
        return (frameCount < frameLimit);
    }

    public float ratio() {
        return (float) frameCount/frameLimit;
    }

}
