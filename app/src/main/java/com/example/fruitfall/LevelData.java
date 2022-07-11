package com.example.fruitfall;

public class LevelData {
    private int fruitNumber;
    private GameEnums.SPACE_DATA[][] spaceData;

    public LevelData(GameEnums.SPACE_DATA[][] _spaceData, int _fruitNumber) {
        this.spaceData = _spaceData;
        this.fruitNumber = _fruitNumber;
    }

    public int getFruitColours() {
        return this.fruitNumber;
    }

    public GameEnums.SPACE_DATA getData(int x, int y) {
        return this.spaceData[y][x];
    }

}
