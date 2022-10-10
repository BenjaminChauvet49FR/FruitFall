package com.example.fruitfall;

public class OptionHandler {

    public static int[] indexFruitsByPriority = {0, 1, 2, 3, 4, 5, 6, 7} ;
    public static int cursorRandomness = 4;
    // In position N = the N 1st fruits are non-random. Position N is between the fruits of index N-1 and N (so Nth and N+1th)
    public static GameEnums.GAME_MODE mode = GameEnums.GAME_MODE.ACTION;
}
