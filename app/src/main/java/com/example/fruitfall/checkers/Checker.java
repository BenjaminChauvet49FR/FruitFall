package com.example.fruitfall.checkers;

import com.example.fruitfall.SpaceCoors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Checker  {

    private int xLength;
    private int yLength;
    private boolean needClean;
    private List<SpaceCoors> list;
    private boolean[][] array;

    public Checker(int _xLength, int _yLength) {
        this.xLength = _xLength;
        this.yLength = _yLength;
        this.needClean = false;
        this.list = new ArrayList<>();
        this.array = new boolean[this.yLength][this.xLength];
        for (int y = 0 ; y < this.yLength ; y++) {
            for (int x = 0; x < this.xLength; x++) {
                this.array[y][x] = false;
            }
        }
    }

    public boolean add(int x, int y) {
        if (!this.array[y][x]) {
            this.array[y][x] = true;
            this.list.add(new SpaceCoors(x, y));
            return true;
        }
        return false;
    }

    public boolean remove(int x, int y) {
        if (this.array[y][x]) {
            this.array[y][x] = false;
            this.needClean = true;
            return true;
        }
        return false;
    }

    public void clear() {
        for (SpaceCoors coors : this.list) {
            this.array[coors.y][coors.x] = false;
        }
        this.list.clear();
        this.needClean = false;
    }

    public List<SpaceCoors> getList() {
        if (this.needClean) {
            List<SpaceCoors> newList = new ArrayList<>();
            for (SpaceCoors coors : this.list) {
                if (this.array[coors.y][coors.x]) {
                    newList.add(new SpaceCoors(coors.x, coors.y));
                }
            }
            this.needClean = false;
            this.list = newList;
        }
        return this.list;
    }

    public boolean get(int x, int y) {
        return this.array[y][x];
    }
}
