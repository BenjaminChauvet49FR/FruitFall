package com.example.fruitfall;

import java.util.ArrayList;
import java.util.List;

public class Checker  {

    private int xLength;
    private int yLength;
    private List<SpaceCoors> list;
    private boolean[][] array;

    public Checker(int _xLength, int _yLength) {
        this.xLength = _xLength;
        this.yLength = _yLength;
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

    public void clear() {
        for (SpaceCoors coors : this.list) {
            this.array[coors.y][coors.x] = false;
        }
        this.list.clear();
    }

    public List<SpaceCoors> getList() {
        return this.list;
    }

    public boolean get(int x, int y) {
        return this.array[y][x];
    }
}
