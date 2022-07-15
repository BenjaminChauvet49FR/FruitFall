package com.example.fruitfall.animations;

import com.example.fruitfall.Constants;

public class SpaceAnimationLightning extends SpaceAnimation {

    boolean isHorizontal;
    int maxCoor1, maxCoor2; // TODO lorsqu'on aura des fruits bloqués

    public SpaceAnimationLightning(boolean isHorizontal) {
        super(Constants.NUMBER_FRAMES_ANIMATION_LIGHT);
        this.isHorizontal = isHorizontal;
    }

    public boolean getHorizontal() {
        return this.isHorizontal;
    }

    public int getMaxCoor() {
        return Constants.FIELD_YLENGTH;
        // TODO C'est du remplissage
    }

    public int getMinCoor() {
        return 0;
    }

}
