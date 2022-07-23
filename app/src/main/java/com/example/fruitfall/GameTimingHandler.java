package com.example.fruitfall;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.fruitfall.animations.SpaceAnimation;
import com.example.fruitfall.animations.SpaceAnimationFire;
import com.example.fruitfall.animations.SpaceAnimationFruitShrinking;
import com.example.fruitfall.animations.SpaceAnimationLightning;
import com.example.fruitfall.animations.SpaceAnimationOmegaSphere;

public class GameTimingHandler {


    private int xSwap1, xSwap2, ySwap1, ySwap2;
    private GameEnums.GAME_STATE gameState;
    private int frameCount;
    private int frameScore;
    private GameHandler gh;
    private long frameTotalCount;

    private SpaceAnimation[][] animations = new SpaceAnimation[Constants.FIELD_YLENGTH][Constants.FIELD_XLENGTH];

    public GameTimingHandler(GameHandler gh) {
        this.gh = gh;
    }

    // Init
    public void init() {
        this.gameState = GameEnums.GAME_STATE.NORMAL;
        this.frameCount = 0;
        this.frameTotalCount = 0;
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

    public void startDestruction(boolean alignedFruits) {
        frameCount = 0;
        frameScore = 0;
        int x, y, fruitId;
        for(SpaceCoors coors : this.gh.getTrulyDestroyedFruitsCoors()) {
            x = coors.x;
            y = coors.y;
            fruitId = this.gh.getFruit(x, y);
            if (fruitId != Constants.NOT_A_FRUIT) {
                if (alignedFruits) {
                    this.animations[y][x] = new SpaceAnimationFruitShrinking(fruitId, false);
                } else {
                    this.animations[y][x] = new SpaceAnimationFruitShrinking(fruitId, true);
                }
            } else if (this.gh.hasOmegaSphere(x, y)) {
                this.animations[y][x] = new SpaceAnimationOmegaSphere();
            }
        }
        GameEnums.FRUITS_POWER power;
        for (SpaceCoors coors : this.gh.getListToBeActivatedSpecialFruits()) {
            x = coors.x;
            y = coors.y;
            power = this.gh.getFruitPowerFromCoors(x, y);
            if (power == GameEnums.FRUITS_POWER.FIRE) {
                this.animations[y][x] = new SpaceAnimationFire();
            }
            if (power == GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING) {
                this.animations[y][x] = new SpaceAnimationLightning(true);
            }
            if (power == GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING) {
                this.animations[y][x] = new SpaceAnimationLightning(false);
            }
            if (gh.hasOmegaSphere(x, y)) {
                // TODO chercher toutes les sphères détruites de cette couleur ?
            }
        }
        // TODO démarrer les destructions pour les fruits spéciaux
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
    @RequiresApi(api = Build.VERSION_CODES.N)
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
            this.frameTotalCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_SWAP) {
                this.gh.triggerBackSwap(this.xSwap1, this.ySwap1, this.xSwap2, this.ySwap2);
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_STASIS) {
            this.frameCount++;
            this.frameScore++;
            if (this.frameCount == Constants.NUMBER_FRAMES_DESTRUCTION) {
                this.gh.triggerDestruction();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.FALLING) {
            this.frameCount++;
            this.frameScore++;
            if (this.frameCount == Constants.NUMBER_FRAMES_FALL) {
                this.gh.triggerUnstableCheck();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.NORMAL) {
            this.frameTotalCount++;
            return;
        }
    }

    // For drawing

    // States
    public boolean isActive() {
        return this.gameState == GameEnums.GAME_STATE.NORMAL;
    }
    public boolean isActivePenalty() {
        return this.gameState == GameEnums.GAME_STATE.SWAP_RETURN;
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

    public boolean hasStillSpace(int x, int y) {
        return (
                ((x != this.xSwap1) || (y != this.ySwap1)) &&
                        ((x != this.xSwap2) || (y != this.ySwap2)) &&
                        (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_STASIS || this.gh.isNotFallingFruit(x, y)) &&
                        this.gh.isNotDestroyedBeforeFall(x, y)
        );
    }
    public boolean shouldDrawScore() {
        return (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_STASIS || this.gameState == GameEnums.GAME_STATE.FALLING) &&
                (frameScore < Constants.NUMBER_FRAMES_SCORE);
    }


    public SpaceAnimation getAnimation(int x, int y) {
        return this.animations[y][x];
    }
    public long getTimeToDisplay() {return this.frameTotalCount / 60;} // TODO handle time for real ! (with SecureDate ?)
}
