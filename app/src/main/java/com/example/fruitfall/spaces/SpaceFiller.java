package com.example.fruitfall.spaces;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;

public abstract class SpaceFiller {
    public abstract boolean canBeSwapped();
    public abstract boolean canFall();
    public int getFruit() {
        return Constants.NOT_A_FRUIT;
    }
    public GameEnums.FRUITS_POWER getPower() { return GameEnums.FRUITS_POWER.NONE; }
}
