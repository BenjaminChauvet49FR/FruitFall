package com.example.fruitfall;

public class GameEnums {
    public enum GAME_STATE {
        INTRODUCTION,
        NORMAL,
        SWAP,
        SWAP_RETURN,
        FALLING,
        DESTRUCTING_STASIS,
        DESTRUCTING_LOCKS,
        RAY_ANIMATIONS,
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
        VOID,
        VOID_SPAWN,
        DELAYED_LOCK_LENGTH1,
        DELAYED_LOCK_LENGTH2,
        DELAYED_LOCK_LENGTH3,
        DELAYED_LOCK_LENGTH4,
    }

    public enum FRUITS_POWER {
        HORIZONTAL_LIGHTNING,
        VERTICAL_LIGHTNING,
        FIRE,
        OMEGA_SPHERE,
        VIRTUAL_FIRE_FIRE,
        VIRTUAL_FIRE_LIGHTNING,
        VIRTUAL_LIGHTNING_LIGHTNING,
        VIRTUAL_OMEGA_FIRE,
        VIRTUAL_OMEGA_VERT_LIGHTNING,
        VIRTUAL_OMEGA_HORIZ_LIGHTNING,
        VIRTUAL_OMEGA_OMEGA,
        NONE
    }

    public enum WHICH_SWAP {
        FRUIT_FRUIT,
        FRUIT_OMEGA,
        OMEGA_OMEGA,
        OMEGA_ELECTRIC,
        OMEGA_FIRE,
        FIRE_FIRE,
        FIRE_ELECTRIC,
        ELECTRIC_ELECTRIC,
        INVALID,
        NONE
    }

}
