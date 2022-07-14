package com.example.fruitfall.level;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.SpaceCoors;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private int fruitNumber;
    private GameEnums.SPACE_DATA[][] spaceData;
    private List<Integer> forcedIndexes;
    // IMPORTANT : coordinate in position (i) in inFallTeleporters must be at a base, otherwise it can be very confusing. And coordinate in position (i) in outFallTeleporters must be at a summit.
    private List<SpaceCoors> inFallTeleporters;
    private List<SpaceCoors> outFallTeleporters;
    private String name;
    private GameEnums.SPACE_DATA[] topRowSpawn;

    public LevelData(GameEnums.SPACE_DATA[][] _spaceData, int _fruitNumber, String name) {
        this(_spaceData, _fruitNumber, name, null);
    }

    public LevelData(GameEnums.SPACE_DATA[][] _spaceData, int _fruitNumber, String name, List<Integer> _forcedIndexes) {
        int x, y;
        this.spaceData = new GameEnums.SPACE_DATA[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];
        // Note : useful copy since the same array may be reused several times with modified spaces for _spaceData, who knows
        for (y = 0 ; y < Constants.FIELD_YLENGTH; y++) {
            for (x = 0; x < Constants.FIELD_XLENGTH; x++) {
                this.spaceData[y][x] = _spaceData[y][x];
            }
        }

        // Top row
        this.topRowSpawn = new GameEnums.SPACE_DATA[Constants.FIELD_XLENGTH];
        for (x = 0 ; x < Constants.FIELD_XLENGTH ; x++) {
            this.topRowSpawn[x] = GameEnums.SPACE_DATA.VOID_SPAWN;
        }


        this.fruitNumber = _fruitNumber;
        this.forcedIndexes = _forcedIndexes;
        this.inFallTeleporters = new ArrayList<>();
        this.outFallTeleporters = new ArrayList<>();
        this.name = name;
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

    public void addTeleporters(int xSource, int ySource, int xDest, int yDest) {
        this.addTeleporters(xSource, ySource, xDest, yDest, 0);
    }

    public void addTeleporters(int xSource, int ySource, int xDest, int yDest, int length) {
        for (int i = 0 ; i < length ; i++) {
            inFallTeleporters.add(new SpaceCoors(xSource + i, ySource));
            outFallTeleporters.add(new SpaceCoors(xDest + i, yDest));
        }
    }

    public List<SpaceCoors> getTeleportersSource() {
        return this.inFallTeleporters;
    }

    public List<SpaceCoors> getTeleportersDestination() {
        return this.outFallTeleporters;
    }

    public String getTitle() { return this.name; }

    public GameEnums.SPACE_DATA getTopRowSpawn(int x) {
        return topRowSpawn[x];
    }

    public void preventSpawn(List<Integer> xNoS) {
        for (int i : xNoS) {
            this.topRowSpawn[i] = GameEnums.SPACE_DATA.VOID;
        }
    }


}
