package com.example.fruitfall.spaces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;

public abstract class SpaceFiller {
    public abstract boolean canBeSwapped();
    public abstract boolean canFall();
    public int getIdFruit() {
        return Constants.NOT_A_FRUIT;
    }
    public GameEnums.FRUITS_POWER getPower() { return GameEnums.FRUITS_POWER.NONE; }
    public abstract void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint);

    public boolean isASpace() { return true; }
    public boolean mayDisappear() { return true; }
}
