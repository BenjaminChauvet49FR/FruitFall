package com.example.fruitfall;

public class GameEnums {
    enum GAME_STATE {
        NORMAL,
        SWAP,
        SWAP_RETURN,
        FALLING,
        PAUSED
    }

    enum FALL_DIRECTION {
        NONE,
        DOWN,
        DOWN_RIGHT,
        DOWN_LEFT
    }

    // Space as it is at start
    enum SPACE_DATA {
        FRUIT,
        EMPTY,
        VOID
    }

}
