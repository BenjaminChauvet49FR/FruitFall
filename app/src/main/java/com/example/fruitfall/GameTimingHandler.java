package com.example.fruitfall;

public class GameTimingHandler {
    public static int NUMBER_FRAMES_SWAP = 20;
    public static int NUMBER_FRAMES_FALL = 15;

    private int xSwap1, xSwap2, ySwap1, ySwap2;
    private GameEnums.GAME_STATE gameState;
    private int frameCount;
    private GameHandler gh;

    public GameTimingHandler(GameHandler gh) {
        this.gh = gh;
    }

    public void init() {
        this.gameState = GameEnums.GAME_STATE.NORMAL;
        this.frameCount = 0;
        this.clearSwap();
    }

    public boolean hasStillFruit(int x, int y) {
        return (
                ((x != this.xSwap1) || (y != this.ySwap1)) &&
                ((x != this.xSwap2) || (y != this.ySwap2)) &&
                (this.gh.getFruit(x, y) != GameHandler.VOID) &&
                (this.gh.getFruit(x, y) != GameHandler.EMPTY_FRUIT) &&
                this.gh.isNotFallingFruit(x, y) &&
                this.gh.isNotDestroyedBeforeFall(x, y)
        );
    }

    private void clearSwap() {
        this.xSwap1 = -1;
        this.xSwap2 = -1;
    }

    // Transitions
    public void startSwap(int x1, int y1, int x2, int y2) {
        this.gameState = GameEnums.GAME_STATE.SWAP;
        this.frameCount = 0;
        this.xSwap1 = x1;
        this.xSwap2 = x2;
        this.ySwap1 = y1;
        this.ySwap2 = y2;
    }

    public void startBackSwap(int x1, int y1, int x2, int y2) {
        this.gameState = GameEnums.GAME_STATE.SWAP_RETURN;
        this.frameCount = 0;
        this.xSwap1 = x1;
        this.xSwap2 = x2;
        this.ySwap1 = y1;
        this.ySwap2 = y2;
    }

    public void stopSwap() {
        this.clearSwap();
    }

    public void startFall() {
        frameCount = 0;
        this.gameState = GameEnums.GAME_STATE.FALLING;
    }

    public void endAllFalls() {
        this.gameState = GameEnums.GAME_STATE.NORMAL;
    }

    public void step() {
        if (this.gameState == GameEnums.GAME_STATE.SWAP) {
            this.frameCount++;
            if (this.frameCount == NUMBER_FRAMES_SWAP) {
                this.gh.triggerSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.SWAP_RETURN) {
            this.frameCount++;
            if (this.frameCount == NUMBER_FRAMES_SWAP) {
                this.gh.triggerBackSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
                this.gameState = GameEnums.GAME_STATE.NORMAL;
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.FALLING) {
            this.frameCount++;
            if (this.frameCount == NUMBER_FRAMES_FALL) {
                this.gh.triggerUnstableCheck();
            }
            return;
        }
    }

    // Drawing
    public float ratioToCompletionSwap() {
        return (float)this.frameCount / NUMBER_FRAMES_SWAP;
    }
    public float ratioToCompletionFall() {
        return (float)this.frameCount / NUMBER_FRAMES_FALL;
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
