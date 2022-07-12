package com.example.fruitfall;

import com.example.fruitfall.animations.SpaceAnimation;
import com.example.fruitfall.animations.SpaceAnimationFruitShrinking;

public class GameTimingHandler {


    private int xSwap1, xSwap2, ySwap1, ySwap2;
    private GameEnums.GAME_STATE gameState;
    private int frameCount;
    private GameHandler gh;

    private SpaceAnimation[][] animations = new SpaceAnimation[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];

    public GameTimingHandler(GameHandler gh) {
        this.gh = gh;
    }

    // Init
    public void init() {
        this.gameState = GameEnums.GAME_STATE.NORMAL;
        this.frameCount = 0;
        this.clearSwap();
    }

    // Misc
    private void clearSwap() {
        this.xSwap1 = -1;
        this.xSwap2 = -1;
    }

    // Transitions : start
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

    public void startFall() {
        frameCount = 0;
        this.gameState = GameEnums.GAME_STATE.FALLING;
    }

    public void startDestruction() {
        frameCount = 0;
        int x, y;
        for(SpaceCoors coors : this.gh.getdestroyedFruitsCoors()) {
            x = coors.x;
            y = coors.y;
            this.animations[y][x] = new SpaceAnimationFruitShrinking(this.gh.getFruit(x, y));
        }
        this.gameState = GameEnums.GAME_STATE.DESTRUCTING_STASIS;
    }

    // Transitions : stop
    public void stopSwap() {
        this.clearSwap();
    }

    // Transitions : end
    public void endAllFalls() {
        this.gameState = GameEnums.GAME_STATE.NORMAL;
    }
    public void endSwap() { this.gameState = GameEnums.GAME_STATE.NORMAL; }

    // Transitions : step
    public void step() {
        if (this.gameState == GameEnums.GAME_STATE.SWAP) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_SWAP) {
                this.gh.triggerSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.SWAP_RETURN) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_SWAP) {
                this.gh.triggerBackSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_STASIS) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_DESTRUCTION) {
                this.gh.triggerDestruction();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.FALLING) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_FALL) {
                this.gh.triggerUnstableCheck();
            }
            return;
        }
    }

    // For drawing

    // States
    public boolean isActive() {
        return this.gameState == GameEnums.GAME_STATE.NORMAL;
    }
    public boolean isInSwap() {
        return (this.gameState == GameEnums.GAME_STATE.SWAP) || (this.gameState == GameEnums.GAME_STATE.SWAP_RETURN);
    }
    public boolean isInFall() {
        return (this.gameState == GameEnums.GAME_STATE.FALLING);
    }

    // Other
    public float ratioToCompletionSwap() {
        return (float)this.frameCount / Constants.NUMBER_FRAMES_SWAP;
    }
    public float ratioToCompletionFall() {
        return (float)this.frameCount / Constants.NUMBER_FRAMES_FALL;
    }
    public int getXSwap1() { return this.xSwap1; }
    public int getXSwap2() { return this.xSwap2; }
    public int getYSwap1() { return this.ySwap1; }
    public int getYSwap2() { return this.ySwap2; }

    public boolean hasStillFruit(int x, int y) {
        return (
                ((x != this.xSwap1) || (y != this.ySwap1)) &&
                        ((x != this.xSwap2) || (y != this.ySwap2)) &&
                        (this.gh.getFruit(x, y) != GameHandler.VOID) &&
                        (this.gh.getFruit(x, y) != GameHandler.EMPTY_FRUIT) &&
                        (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_STASIS || this.gh.isNotFallingFruit(x, y)) &&
                        this.gh.isNotDestroyedBeforeFall(x, y)
        );
    }

    public SpaceAnimation getAnimation(int x, int y) {
        return this.animations[y][x];
    }
}
