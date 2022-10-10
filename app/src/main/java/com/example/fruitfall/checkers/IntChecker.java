package com.example.fruitfall.checkers;

import com.example.fruitfall.structures.SpaceCoors;

import java.util.ArrayList;
import java.util.List;

public class IntChecker {
    private int xLength;
    private int yLength;
    private List<SpaceCoors> list;
    private int[][] array;
    private int defaultValue;

    public IntChecker(int _xLength, int _yLength, int _defaultValue) {
        this.xLength = _xLength;
        this.yLength = _yLength;
        this.defaultValue = _defaultValue;
        this.list = new ArrayList<>();
        this.array = new int[this.yLength][this.xLength];
        for (int y = 0 ; y < this.yLength ; y++) {
            for (int x = 0; x < this.xLength; x++) {
                this.array[y][x] = _defaultValue;
            }
        }

    }

    /*
    Precondition : "default value" is never passed. Otherwise it could be added again, and again, and again...
     */
    public boolean add(int x, int y, int value) {
        if (this.array[y][x] == this.defaultValue) {
            this.array[y][x] = value;
            this.list.add(new SpaceCoors(x, y));
            return true;
        }
        return false;
    }

    public void clear() {
        for (SpaceCoors coors : this.list) {
            this.array[coors.y][coors.x] = this.defaultValue;
        }
        this.list.clear();
    }

    public List<SpaceCoors> getList() {
        return this.list;
    }

    public int get(int x, int y) {
        return this.array[y][x];
    }
}
