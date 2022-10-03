package com.example.fruitfall.introductions;

import com.example.fruitfall.Constants;
import com.example.fruitfall.level.LevelManager;

import java.util.Random;

public class TransitionFlowerBlooming extends Transition {
    @Override
    protected boolean initialize() {
        Random rand = new Random();
        int x1 = rand.nextInt(LevelManager.currentLevelWidth());
        int y1 = rand.nextInt(LevelManager.currentLevelHeight());

        final int numberSteps = LevelManager.currentLevelHeight() + LevelManager.currentLevelWidth();
        float value = (float)0.0;
        final float gap = (float)1.0/numberSteps;
        int dist;
        this.spaces[y1][x1] = value;
        for (dist = 0 ; dist < numberSteps ; dist++) {
            setAroundAPoint(dist, x1, y1, value);
            value += gap;
        }
        return true;
    }

    private boolean areAcceptableCoordinates(int x, int y) {
        return (x >= 0 && y >= 0 && x < Constants.FIELD_XLENGTH && y < Constants.FIELD_YLENGTH);
    }

    private void setAroundAPoint(int dist, int xSource, int ySource, float value) {
        int x, y, step;
        for (int dir = 0 ; dir <= 3 ; dir++) {
            x = xSource + dist*Constants.coefDirectionalX[dir];
            y = ySource + dist*Constants.coefDirectionalY[dir];
            for (step = 0 ; step < dist ; step++) {
                if (areAcceptableCoordinates(x, y) && this.spaces[y][x] < 0.01) {
                    this.spaces[y][x] = value;
                }
                x += Constants.coefDirectionalClockwiseTurningX[dir];
                y += Constants.coefDirectionalClockwiseTurningY[dir];
            }
        }
    }

    @Override
    public float relativeTransitionLength() {return (float)2.0;}
}
