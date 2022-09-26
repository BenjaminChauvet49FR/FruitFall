package com.example.fruitfall;

import static com.example.fruitfall.Constants.ANY_FRUIT;
import static com.example.fruitfall.Constants.NOT_A_FRUIT;

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
        BEAM_ANIMATIONS,
        PAUSED
    }

    // Space as it is at start
    public enum SPACE_DATA {
        FRUIT,
        EMPTY,
        VOID,
        VOID_SPAWN,
        DELAYED_LOCK,
        BREAKABLE_BLOCK,
        STICKY_BOMB,
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

    // Credits : https://stackoverflow.com/questions/2457076/can-i-add-a-function-to-enums-in-java
    public enum ORDER_SUPER_KIND {
        ANY, SIMPLE, SPECIAL, WILD_MIX, MIX
    }

    public enum ORDER_KIND {
        FRUITS_ANY(ORDER_SUPER_KIND.ANY,"a",ANY_FRUIT),
        FRUIT_0(ORDER_SUPER_KIND.SIMPLE,"0",0),
        FRUIT_1(ORDER_SUPER_KIND.SIMPLE,"1",1),
        FRUIT_2(ORDER_SUPER_KIND.SIMPLE,"2",2),
        FRUIT_3(ORDER_SUPER_KIND.SIMPLE,"3",3),
        FRUIT_4(ORDER_SUPER_KIND.SIMPLE,"4",4),
        FRUIT_5(ORDER_SUPER_KIND.SIMPLE,"5",5),
        FRUIT_6(ORDER_SUPER_KIND.SIMPLE,"6",6),
        FRUIT_7(ORDER_SUPER_KIND.SIMPLE,"7",7),
        OMEGA(ORDER_SUPER_KIND.SPECIAL,"O"),
        LIGHTNING(ORDER_SUPER_KIND.SPECIAL,"L"),
        FIRE(ORDER_SUPER_KIND.SPECIAL,"F"),
        OMEGA_FIRE(ORDER_SUPER_KIND.MIX,"FO"),
        LIGHTNING_FIRE(ORDER_SUPER_KIND.MIX,"LF"),
        LIGHTNING_OMEGA(ORDER_SUPER_KIND.MIX,"LO"),
        OMEGA_OMEGA(ORDER_SUPER_KIND.MIX,"OO"),
        LIGHTNING_LIGHTNING(ORDER_SUPER_KIND.MIX,"LL"),
        FIRE_FIRE(ORDER_SUPER_KIND.MIX,"FF"),
        FIRE_WILD(ORDER_SUPER_KIND.WILD_MIX,"F+"),
        LIGHTNING_WILD(ORDER_SUPER_KIND.WILD_MIX,"L+"),
        OMEGA_WILD(ORDER_SUPER_KIND.WILD_MIX,"O+"),
        NONE(ORDER_SUPER_KIND.ANY,"");

        private final int fruitId;
        private final String label;
        private final ORDER_SUPER_KIND superKind;
        private ORDER_KIND(ORDER_SUPER_KIND superKind, final String label, final int fruitId) {this.superKind = superKind; this.label = label; this.fruitId = fruitId; }
        private ORDER_KIND(ORDER_SUPER_KIND superKind, final String label) {this(superKind, label, NOT_A_FRUIT); }

        public int getFruitId() {return fruitId;}
        public ORDER_SUPER_KIND getSuperKind() {return this.superKind;}
    }

    public enum GOAL_KIND {
        BASKETS,
        NUTS,
        ORDERS
    }
}
