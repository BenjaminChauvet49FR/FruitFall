package com.example.fruitfall.spaces;

import com.example.fruitfall.Constants;

public abstract class SpaceFiller {
    public abstract boolean canBeSwapped();
    public abstract boolean canFall();
    public int getFruit() {
        return Constants.NOT_A_FRUIT;
    }
}
