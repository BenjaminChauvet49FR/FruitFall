package com.example.fruitfall.level;

import com.example.fruitfall.GameEnums;

import java.util.List;

public class LevelData {
    private int fruitNumber;
    private GameEnums.SPACE_DATA[][] spaceData;
    private List<Integer> forcedIndexes;

    public LevelData(GameEnums.SPACE_DATA[][] _spaceData, int _fruitNumber) {
        this(_spaceData, _fruitNumber, null);
    }

    public LevelData(GameEnums.SPACE_DATA[][] _spaceData, int _fruitNumber, List<Integer> _forcedIndexes) {
        this.spaceData = _spaceData;
        this.fruitNumber = _fruitNumber;
        this.forcedIndexes = _forcedIndexes;
    }

    public int getFruitColours() {
        return this.fruitNumber;
    }
    public List<Integer> getForcedIndexes() {
        return this.forcedIndexes;
    }


    public GameEnums.SPACE_DATA getData(int x, int y) {
        return this.spaceData[y][x];
    }



}
