package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;

public class VoidSpace extends SpaceFiller {
    public boolean canBeSwapped() {
        return false;
    }

    public boolean canFall() {
        return false;
    }

    @Override
    public void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {

    }

    @Override
    public boolean isASpace() { return false; }

    @Override
    public boolean mayDisappear() { return false; }

    @Override
    public SpaceFiller copy() { return new VoidSpace(); }

}
