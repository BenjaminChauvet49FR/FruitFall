package com.example.fruitfall;

public class GameTimingHandler {
    public static int NUMBER_FRAMES = 20;


    private boolean[][] arrayTimingFruitInPlace;
    private int xSwap1, xSwap2, ySwap1, ySwap2;
    private GameEnums.GAME_STATE gameState;
    private int frameSwap;
    private GameHandler gh;

    public GameTimingHandler(GameHandler gh) {
        this.arrayTimingFruitInPlace = new boolean[GameHandler.FIELD_YLENGTH][GameHandler.FIELD_XLENGTH];
        for (int y = 0 ; y < GameHandler.FIELD_YLENGTH ; y++) {
            for (int x = 0; x < GameHandler.FIELD_XLENGTH; x++) {
                this.arrayTimingFruitInPlace[y][x] = true;
            }
        }

        this.gameState = GameEnums.GAME_STATE.NORMAL;
        this.frameSwap = 0;
        this.gh = gh;
    }

    public boolean hasStillFruit(int x, int y) {
        return this.arrayTimingFruitInPlace[y][x];
    }

    // Transitions
    public void startSwap(int x1, int y1, int x2, int y2) {
        this.gameState = GameEnums.GAME_STATE.SWAP;
        this.frameSwap = 0;
        this.xSwap1 = x1;
        this.xSwap2 = x2;
        this.ySwap1 = y1;
        this.ySwap2 = y2;
        this.arrayTimingFruitInPlace[y1][x1] = false;
        this.arrayTimingFruitInPlace[y2][x2] = false;
    }

    public void startBackSwap(int x1, int y1, int x2, int y2) {
        this.gameState = GameEnums.GAME_STATE.SWAP_RETURN;
        this.frameSwap = 0;
        this.xSwap1 = x1;
        this.xSwap2 = x2;
        this.ySwap1 = y1;
        this.ySwap2 = y2;
        this.arrayTimingFruitInPlace[y1][x1] = false;
        this.arrayTimingFruitInPlace[y2][x2] = false;
    }

    public void startFall() {
        // TODO let the sky fall...
        this.gameState = GameEnums.GAME_STATE.NORMAL;
    }

    public void step() {
        if (this.gameState == GameEnums.GAME_STATE.SWAP) {
            this.frameSwap++;
            if (this.frameSwap == NUMBER_FRAMES) {
                this.arrayTimingFruitInPlace[this.ySwap1][this.xSwap1] = true;
                this.arrayTimingFruitInPlace[this.ySwap2][this.xSwap2] = true;
                this.gh.triggerSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.SWAP_RETURN) {
            this.frameSwap++;
            if (this.frameSwap == NUMBER_FRAMES) {
                this.arrayTimingFruitInPlace[this.ySwap1][this.xSwap1] = true;
                this.arrayTimingFruitInPlace[this.ySwap2][this.xSwap2] = true;
                this.gh.triggerBackSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
                this.gameState = GameEnums.GAME_STATE.NORMAL;
            }
            return;
        }
    }

    // Drawing
    public float ratioToCompletionSwap() {
        return (float)this.frameSwap / NUMBER_FRAMES;
    }
    public int getXSwap1() { return this.xSwap1; }
    public int getXSwap2() { return this.xSwap2; }
    public int getYSwap1() { return this.ySwap1; }
    public int getYSwap2() { return this.ySwap2; }


    public boolean isActive() {
        return this.gameState == GameEnums.GAME_STATE.NORMAL;
    }
    public boolean isInSwap() {
        return (this.gameState == GameEnums.GAME_STATE.SWAP) || (this.gameState == GameEnums.GAME_STATE.SWAP_RETURN);
    }



}
