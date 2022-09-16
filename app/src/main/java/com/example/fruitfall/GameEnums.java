package com.example.fruitfall;

import androidx.annotation.NonNull;

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

    // Space as it is at start
    public enum SPACE_DATA {
        FRUIT,
        EMPTY,
        VOID,
        VOID_SPAWN,
        DELAYED_LOCK,
        BREAKABLE_BLOCK
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

    public enum ORDER_KIND {
        FRUITS_ANY,
        FRUIT_1,
        FRUIT_2,
        FRUIT_3,
        FRUIT_4,
        FRUIT_5,
        FRUIT_6,
        FRUIT_7,
        FRUIT_8,
        OMEGA,
        LIGHTNING,
        FIRE,
        OMEGA_FIRE,
        LIGHTNING_FIRE,
        LIGHTNING_OMEGA,
        OMEGA_OMEGA,
        LIGHTNING_LIGHTNING,
        FIRE_FIRE,
        FIRE_WILD,
        LIGHTNING_WILD,
        OMEGA_WILD,
        NONE
    }

    public enum GOAL_KIND {
        BASKETS,
        ORDERS
    }

    @NonNull
    public static String toString(ORDER_KIND m) {
        switch (m) {
            case OMEGA : return "O";
            case LIGHTNING : return "L";
            case FIRE : return "F";
            case LIGHTNING_FIRE: return "LF";
            case LIGHTNING_LIGHTNING: return "LL";
            case LIGHTNING_OMEGA: return "LO";
            case OMEGA_FIRE: return "FO";
            case FIRE_FIRE: return "FF";
            case OMEGA_OMEGA: return "OO";
            case LIGHTNING_WILD: return "L+";
            case OMEGA_WILD: return "O+";
            case FIRE_WILD: return "F+";
            default : return "";
        }
    }
}
