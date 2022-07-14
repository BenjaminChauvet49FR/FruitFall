package com.example.fruitfall.spaces;

import com.example.fruitfall.GameEnums;

public class Fruit extends SpaceFiller {
    private int value;
    private GameEnums.FRUITS_POWER power;

    public Fruit(int value) {
        this(value, GameEnums.FRUITS_POWER.NONE);
    }

    public Fruit(int value, GameEnums.FRUITS_POWER power) {
        this.value = value;
        this.power = power;
    }

    @Override
    public int getFruit() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean canBeSwapped() {
        return true;
    }

    @Override
    public boolean canFall() {
        return true;
    }
}
