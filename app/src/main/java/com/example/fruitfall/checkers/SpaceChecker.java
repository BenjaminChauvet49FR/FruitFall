package com.example.fruitfall.checkers;

import com.example.fruitfall.structures.SpaceCoors;

import java.util.ArrayList;
import java.util.List;

public class SpaceChecker<T> {
    private int xLength;
    private int yLength;
    private boolean needClean;
    private List<SpaceCoors> list;
    private List<List<T>> array;

    public SpaceChecker(int _xLength, int _yLength) {
        this.xLength = _xLength;
        this.yLength = _yLength;
        this.needClean = false;
        this.list = new ArrayList<>();
        this.array = new ArrayList<>();
        for (int y = 0 ; y < this.yLength ; y++) {
            this.array.add(new ArrayList<>());
            for (int x = 0; x < this.xLength; x++) {
                this.array.get(y).add(null);
            }
        }

    }

    public boolean add(int x, int y, T element) {
        if (this.array.get(y).get(x) == null) {
            this.array.get(y).set(x, element);
            this.list.add(new SpaceCoors(x, y));
            return true;
        }
        return false;
    }

    public boolean remove(int x, int y) {
        if (this.array.get(y).get(x) != null) {
            this.array.get(y).set(x, null);
            this.needClean = true;
            return true;
        }
        return false;
    }

    public void clear() {
        for (SpaceCoors coors : this.list) {
            this.array.get(coors.y).set(coors.x, null);
        }
        this.list.clear();
        this.needClean = false;
    }

    public List<SpaceCoors> getList() {
        if (this.needClean) {
            List<SpaceCoors> newList = new ArrayList<>();
            for (SpaceCoors coors : this.list) {
                if (this.array.get(coors.y).get(coors.x) != null) {
                    newList.add(new SpaceCoors(coors.x, coors.y));
                }
            }
            this.needClean = false;
            this.list = newList;
        }
        return this.list;
    }

    public T get(int x, int y) {
        return this.array.get(y).get(x);
    }

}
