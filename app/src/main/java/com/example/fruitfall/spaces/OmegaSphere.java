package com.example.fruitfall.spaces;

public class OmegaSphere extends SpaceFiller {

    @Override
    public boolean canBeSwapped() {
        return true;
    }

    @Override
    public boolean canFall() {
        return true;
    }
}
