package com.example.fruitfall;

public class Constants {
    public static final int NUMBER_FRAMES_SCORE = 20;
    public static final int NUMBER_FRAMES_SWAP = 16;
    public static final int NUMBER_FRAMES_FALL = 8;
    public static final int NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY = 60; // 10;
    public static final float RATIO_FRAMES_GHOST_INTRODUCTION = (float)0.2;
    public static final int NUMBER_FRAMES_INTRODUCTION_GHOST = Math.max(1, Math.round(NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY *RATIO_FRAMES_GHOST_INTRODUCTION));
    public static final int NUMBER_FRAMES_INTRODUCTION_FLEX_FRUIT = Math.max(1, Math.round(NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY *RATIO_FRAMES_GHOST_INTRODUCTION));
    public static final int NUMBER_FRAMES_INTRODUCTION_TOTAL = NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY + NUMBER_FRAMES_INTRODUCTION_FLEX_FRUIT;
    public static final int NUMBER_FRAMES_DESTRUCTION = 8;
    public static final int NUMBER_FRAMES_DESTRUCTION_FORDELAYEDLOCK = 100;
    public static final int NUMBER_FRAMES_DESTRUCTION_FORANIM = 100;
    public static final int NUMBER_FRAMES_ANIMATION_LIGHT = 100;

    public static final int FIELD_YLENGTH = 10;
    public static final int FIELD_XLENGTH = 10;

    public static final int MAX_ANGLE_IN_DEGREES = 480;
    public static final int NOT_A_FRUIT = -1;
    public static final int NOT_A_SPACE_COOR = -1;

    public static final int DUMMY_BOOST_FRUIT_COUNT = FIELD_YLENGTH * FIELD_XLENGTH;
}
