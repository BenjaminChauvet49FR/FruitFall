package com.example.fruitfall;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.fruitfall.animations.SpaceAnimation;
import com.example.fruitfall.animations.SpaceAnimationFire;
import com.example.fruitfall.animations.SpaceAnimationFruitShrinking;
import com.example.fruitfall.animations.SpaceAnimationLightning;
import com.example.fruitfall.animations.SpaceAnimationLockDuration;
import com.example.fruitfall.animations.SpaceAnimationOmegaSphere;

import java.util.List;

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
        this.gameState = GameEnums.GAME_STATE.INTRODUCTION;
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
    public void startGame() {
        this.gameState = GameEnums.GAME_STATE.NORMAL;
    }

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
                } else { // TODO Oops ! When a fruit is destroyed on this space, it overrides the previous animation. How to correct this ?
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

    public void startDestructionLocks(List<SpaceCoors> newlyDestroyed) {
        for (SpaceCoors coors : newlyDestroyed) {
            this.animations[coors.y][coors.x] = new SpaceAnimationLockDuration();
        }
        frameCount = 0;
        this.gameState = GameEnums.GAME_STATE.DESTRUCTING_LOCKS;
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
        if (this.gameState == GameEnums.GAME_STATE.NORMAL) {
            this.frameTotalCount++;
            return;
        }
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
        if (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_LOCKS) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_DESTRUCTION_FORDELAYEDLOCK) {
                this.gh.triggerUnstableCheck();
            }
        }
        if (this.gameState == GameEnums.GAME_STATE.INTRODUCTION) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_INTRODUCTION_TOTAL) {
                this.gh.triggerWelcoming();
            }
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
    public boolean isInIntro() { return (this.gameState == GameEnums.GAME_STATE.INTRODUCTION); }


    // Other
    public float ratioToCompletionSwap() {
        return (float)this.frameCount / Constants.NUMBER_FRAMES_SWAP;
    }
    public float ratioToCompletionFall() {
        return (float)this.frameCount / Constants.NUMBER_FRAMES_FALL;
    }

    // How it works : an affine-by-pieces method worth 0 before, climbing from 0 to 1, then worth 1
    // Works with rfi above !
    public float ratioProgressiveIntroSpaces(float desiredRatio) {
        // RFAI is 0.3 : time goes to 0 to 1 (always), full time goes to 0.3 to 1,
        // We desire 0.8 : window of opportunity is between 0.5 and 0.8
        float firstFrame = desiredRatio*(Constants.NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY-Constants.NUMBER_FRAMES_INTRODUCTION_GHOST);
        int numberFrames = Constants.NUMBER_FRAMES_INTRODUCTION_GHOST;
        float currentFrameRelativeTo1st = this.frameCount - firstFrame;
        if (currentFrameRelativeTo1st < 0) {
            return 0;
        }
        if (currentFrameRelativeTo1st > numberFrames) {
            return 1;
        }
        return currentFrameRelativeTo1st/numberFrames;
    }

    public boolean shouldDrawSpaceContentProgessiveIntro(float desiredRatio) {
        return this.frameCount > desiredRatio*(Constants.NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY-Constants.NUMBER_FRAMES_INTRODUCTION_GHOST) + Constants.NUMBER_FRAMES_INTRODUCTION_GHOST + Constants.NUMBER_FRAMES_INTRODUCTION_FLEX_FRUIT;
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
