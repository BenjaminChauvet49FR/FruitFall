package com.example.fruitfall;

public class GameEnums {
    public enum GAME_STATE {
        NORMAL,
        SWAP,
        SWAP_RETURN,
        FALLING,
        DESTRUCTING_STASIS,
        PAUSED
    }

    public enum FALL_DIRECTION {
        NONE,
        DOWN,
        DOWN_RIGHT,
        DOWN_LEFT
    }

    // Space as it is at start
    public enum SPACE_DATA {
        FRUIT,
        EMPTY,
        VOID
    }

}