package com.example.fruitfall;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.fruitfall.animations.SpaceAnimation;
import com.example.fruitfall.animations.SpaceAnimationFire;
import com.example.fruitfall.animations.SpaceAnimationFruitShrinking;
import com.example.fruitfall.animations.SpaceAnimationLightning;
import com.example.fruitfall.animations.SpaceAnimationDelayedLock;
import com.example.fruitfall.animations.SpaceAnimationOmegaFire;
import com.example.fruitfall.animations.SpaceAnimationOmegaFruit;
import com.example.fruitfall.animations.SpaceAnimationOmegaLightning;
import com.example.fruitfall.animations.SpaceAnimationOmegaSphere;
import com.example.fruitfall.animations.SwapAnimationFireElectric;
import com.example.fruitfall.animations.SwapAnimationFireFire;
import com.example.fruitfall.checkers.Checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTimingHandler {


    private int xSwap1, xSwap2, ySwap1, ySwap2;
    private GameEnums.GAME_STATE gameState;
    private int frameCount;
    private int frameScore;
    private GameHandler gh;
    private long frameTotalCount;
    private List<SpaceAnimation> spaceAnimationList;
    private List<SpaceAnimation> spaceAnimationFruitDestroyedList;
    private Checker handledSpecialAnimationsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private Checker handledDestroyAnimationsChecker = new Checker(Constants.FIELD_XLENGTH, Constants.FIELD_YLENGTH);
    private int constantNumberFramesFall;
    private boolean debugShouldSwitchFallSpeed;
    private float relativeTransitionLength;
    private boolean pause;

    public GameTimingHandler(GameHandler gh) {
        this.gh = gh;
        this.constantNumberFramesFall = Constants.NUMBER_FRAMES_FALL;
        this.debugShouldSwitchFallSpeed = false;
    }

    // Init
    public void init() {
        this.gameState = GameEnums.GAME_STATE.INTRODUCTION;
        this.frameCount = 0;
        this.frameTotalCount = 0;
        this.clearSwap();
        this.handledSpecialAnimationsChecker.clear();
        this.handledDestroyAnimationsChecker.clear();
        this.spaceAnimationList = new ArrayList<>();
        this.spaceAnimationFruitDestroyedList = new ArrayList<>();
        this.pause = false;
    }

    public void setRelativeTransitionLength(float rtl) {
        this.relativeTransitionLength = rtl;
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

    // Called after unstable check where fruits still need to fall AND after there are no more fruits to destroy in a stable check
    public void startFall() {
        frameCount = 0;
        this.handledDestroyAnimationsChecker.clear();
        this.handledSpecialAnimationsChecker.clear();
        this.gameState = GameEnums.GAME_STATE.FALLING;
    }

    public void startDestruction(boolean alignedFruits) {
        // Redirection needed if there are fruits to be destroyed
        SpaceCoors source;
        boolean beamPhase = false;
        for (int colour = 0 ; colour < Constants.RESOURCES_NUMBER_FRUITS ; colour++) {
            source = this.gh.getCoorsSourceOmegaSphere(colour);
            if (source != null) {
                beamPhase = true;
                this.spaceAnimationList.add(new SpaceAnimationOmegaFruit(source.x, source.y, this.gh.getCoorsTargetOmegaSphere(colour), Color.rgb(255, 255, 255), Color.rgb(255, 255, 192), Color.rgb(255, 192, 128)));
            }
            // TODO revoir les couleurs des beams
        }
        if (beamPhase) {
            frameCount = 0;
            this.gameState = GameEnums.GAME_STATE.RAY_ANIMATIONS;
            return;
        } else {
            frameCount = 0;
            frameScore = 0;
            int x, y, fruitId;
            for(SpaceCoors coors : this.gh.getEmptiedSpacesCoors()) {
                x = coors.x;
                y = coors.y;
                fruitId = this.gh.getIdFruit(x, y);
                if (fruitId != Constants.NOT_A_FRUIT) {
                    this.tryToAddToAnimationsDestroy(x, y, new SpaceAnimationFruitShrinking(x, y, fruitId, !alignedFruits));
                } else if (this.gh.hasOmegaSphere(x, y)) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationOmegaSphere(x, y));
                }
            }
            GameEnums.WHICH_SWAP swap = this.gh.getLastSwap();
            x = this.gh.getXCenterAnimation();
            y = this.gh.getYCenterAnimation();
            if (swap == GameEnums.WHICH_SWAP.FIRE_ELECTRIC) {
                this.tryToAddToAnimations(x, y, new SwapAnimationFireElectric(x, y));
            }
            if (swap == GameEnums.WHICH_SWAP.FIRE_FIRE) {
                this.tryToAddToAnimations(x, y, new SwapAnimationFireFire(x, y));
            }
            if (swap == GameEnums.WHICH_SWAP.ELECTRIC_ELECTRIC) {
                this.tryToAddToAnimations(x, y,
                        Arrays.asList(new SpaceAnimationLightning(x, y, true), new SpaceAnimationLightning(x, y, false))
                );
            }
            GameEnums.FRUITS_POWER power;
            for (SpaceCoors coors : this.gh.getListToBeActivatedSpecialFruits()) {
                x = coors.x;
                y = coors.y;
                power = this.gh.getFruitPowerFromCoors(x, y);
                if (power == GameEnums.FRUITS_POWER.FIRE) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationFire(x, y));
                }
                if (power == GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationLightning(x, y, true));
                }
                if (power == GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationLightning(x, y, false));
                }
                if (power == GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_HORIZ_LIGHTNING) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationOmegaLightning(x, y, true));
                }
                if (power == GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_VERT_LIGHTNING) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationOmegaLightning(x, y, false));
                }
                if (power == GameEnums.FRUITS_POWER.VIRTUAL_OMEGA_FIRE) {
                    this.tryToAddToAnimations(x, y, new SpaceAnimationOmegaFire(x, y));
                }
            }
            this.gameState = GameEnums.GAME_STATE.DESTRUCTING_STASIS;
        }
    }

    private void tryToAddToAnimationsDestroy(int x, int y, SpaceAnimation animation) {
        if (this.handledDestroyAnimationsChecker.add(x, y)) {
            this.spaceAnimationList.add(animation);
        }
    }
    
    private void tryToAddToAnimations(int x, int y, SpaceAnimation animation) {
        if (this.handledSpecialAnimationsChecker.add(x, y)) {
            this.spaceAnimationList.add(animation);
        }
    }

    private void tryToAddToAnimations(int x, int y, List<SpaceAnimation> animations) {
        if (this.handledSpecialAnimationsChecker.add(x, y)) {
            for (SpaceAnimation animation : animations) {
                this.spaceAnimationList.add(animation);
            }
        }
    }

    public void startDestructionLocks(List<SpaceCoors> newlyDestroyed) {
        for (SpaceCoors coors : newlyDestroyed) {
            this.spaceAnimationList.add(new SpaceAnimationDelayedLock(coors.x, coors.y));
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
        if (this.pause) {
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.NORMAL) {
            this.frameTotalCount++;
            if (this.debugShouldSwitchFallSpeed) {
                this.debugShouldSwitchFallSpeed = false;
                this.constantNumberFramesFall = Constants.NUMBER_FRAMES_FALL + Constants.NUMBER_FRAMES_FALL_ALTER - this.constantNumberFramesFall;
            }
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
                this.gh.triggerAfterDestructionStasis();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.RAY_ANIMATIONS) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_RAY_ANIMATION) {
                this.gh.triggerAfterOmegaStasis();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.FALLING) {
            this.frameCount++;
            this.frameScore++;
            if (this.frameCount == this.constantNumberFramesFall) {
                this.gh.triggerUnstableCheck();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_LOCKS) {
            this.frameCount++;
            if (this.frameCount == Constants.NUMBER_FRAMES_DESTRUCTION_FORDELAYEDLOCK) {
                this.gh.triggerUnstableCheck();
            }
            return;
        }
        if (this.gameState == GameEnums.GAME_STATE.INTRODUCTION) {
            this.frameCount++;
            if (this.frameCount >= Constants.NUMBER_FRAMES_INTRODUCTION_TOTAL * this.relativeTransitionLength) {
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
        return (float)this.frameCount / this.constantNumberFramesFall;
    }

    // How it works : an affine-by-pieces method worth 0 before, climbing from 0 to 1, then worth 1
    // Works with rfi above !
    public float ratioProgressiveIntroSpaces(float desiredRatio) {
        // RFAI is 0.3 : time goes to 0 to 1 (always), full time goes to 0.3 to 1,
        // We desire 0.8 : window of opportunity is between 0.5 and 0.8
        float firstFrame = desiredRatio*(Constants.NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY-Constants.NUMBER_FRAMES_INTRODUCTION_GHOST)*(float)this.relativeTransitionLength;
        float numberFrames = Constants.NUMBER_FRAMES_INTRODUCTION_GHOST*this.relativeTransitionLength;
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
        return this.frameCount > (desiredRatio*(Constants.NUMBER_FRAMES_INTRODUCTION_SPACES_ONLY-Constants.NUMBER_FRAMES_INTRODUCTION_GHOST) + Constants.NUMBER_FRAMES_INTRODUCTION_GHOST + Constants.NUMBER_FRAMES_INTRODUCTION_FLEX_FRUIT)*(float)this.relativeTransitionLength;
    }

    public int getXSwap1() { return this.xSwap1; }
    public int getXSwap2() { return this.xSwap2; }
    public int getYSwap1() { return this.ySwap1; }
    public int getYSwap2() { return this.ySwap2; }

    public boolean hasStillSpace(int x, int y) {
        return (
                ((x != this.xSwap1) || (y != this.ySwap1)) &&
                        ((x != this.xSwap2) || (y != this.ySwap2)) &&
                        (this.gameState != GameEnums.GAME_STATE.FALLING || (this.gh.isNotFallingFruit(x, y) && this.gh.isNotInDiagonalSqueeze(x, y))) &&
                        this.gh.isNotDestroyedBeforeFall(x, y)
        );
    }
    public boolean shouldDrawScore() {
        return (this.gameState == GameEnums.GAME_STATE.DESTRUCTING_STASIS || this.gameState == GameEnums.GAME_STATE.FALLING) &&
                (frameScore < Constants.NUMBER_FRAMES_SCORE);
    }

    public long getTimeToDisplay() {return this.frameTotalCount / 60;} // TODO handle time for real ! (with SecureDate ?)

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<SpaceAnimation> getAnimations2List() {
        int l = this.spaceAnimationList.size();
        if (l > Constants.MAX_ANIMATIONS) {
            this.spaceAnimationList.removeIf(spaceAnimation -> !spaceAnimation.shouldBeDrawn());
        }
        return this.spaceAnimationList;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<SpaceAnimation> getAnimations1List() {
        int l = this.spaceAnimationFruitDestroyedList.size();
        if (l > Constants.MAX_ANIMATIONS) {
            this.spaceAnimationFruitDestroyedList.removeIf(spaceAnimation -> !spaceAnimation.shouldBeDrawn());
        }
        return this.spaceAnimationFruitDestroyedList;

    }

    public void switchFallSpeed() {
        this.debugShouldSwitchFallSpeed = true;
    }

    public void switchPause() {
        this.pause = !this.pause;
    }

    public boolean getPause() {
        return this.pause;
    }
}
