package com.example.fruitfall.introductions;

public class TransitionManager {
    public static Transition getTransitionFromChar(char c) {
        switch (c) {
            case 'a' : return new TransitionUpward12121();
            case 'r' : return new TransitionRandom();
            case 'h' : return new TransitionLineHorizUD();
            case 't' : return new TransitionRandomFromLeft();
            default : return new TransitionRandom();
        }
    }


}
