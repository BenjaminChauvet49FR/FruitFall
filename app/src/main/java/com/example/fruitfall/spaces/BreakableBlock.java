package com.example.fruitfall.spaces;

public class BreakableBlock extends SpaceFiller {
    @Override
    public boolean canBeSwapped() {
        return false;
    }

    @Override
    public boolean canFall() {
        return false;
    }
}
