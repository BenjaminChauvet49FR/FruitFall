package com.example.fruitfall;

public class WaitingNutData {
    private int count;
    private int delay;

    public WaitingNutData(int count, int delay) {
        this.count = count;
        this.delay = delay;
    }

    public void decrease() {
        delay--;
    }

    public int getCount() {
        return this.count;
    }

    public int getDelay() {
        return this.delay;
    }

    public WaitingNutData copy() {
        return new WaitingNutData(this.count, this.delay);
    }
}
