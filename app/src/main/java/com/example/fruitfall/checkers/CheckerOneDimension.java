package com.example.fruitfall.checkers;

import java.util.ArrayList;
import java.util.List;

public class CheckerOneDimension {

    private boolean needClean;
    private List<Integer> list;
    private final boolean[] array;

    public CheckerOneDimension(int length) {
        this.needClean = false;
        this.list = new ArrayList<>();
        this.array = new boolean[length];
        for (int i = 0; i < length; i++) {
            this.array[i] = false;
        }
    }

    public boolean add(int i) {
        if (!this.array[i]) {
            this.array[i] = true;
            this.list.add(i);
            return true;
        }
        return false;
    }

    public boolean remove(int i) {
        if (this.array[i]) {
            this.array[i] = false;
            this.needClean = true;
            return true;
        }
        return false;
    }

    public void clear() {
        for (int i : this.list) {
            this.array[i] = false;
        }
        this.list.clear();
        this.needClean = false;
    }

    public List<Integer> getList() {
        if (this.needClean) {
            List<Integer> newList = new ArrayList<>();
            for (int i : this.list) {
                if (this.array[i]) {
                    newList.add(i);
                }
            }
            this.needClean = false;
            this.list = newList;
        }
        return this.list;
    }

    public boolean get(int x) {
        return this.array[x];
    }

}
